package com.ly.ttd.inf.rpc.core.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.ttd.inf.rpc.api.annotation.RpcService;
import com.ly.ttd.inf.rpc.api.annotation.Rpcwired;
import com.ly.ttd.inf.rpc.core.autoconfigure.NacosRpcProperties;
import com.ly.ttd.inf.rpc.core.discovery.LoadBalancerStrategy;
import com.ly.ttd.inf.rpc.core.discovery.NacosServiceDiscovery;
import com.ly.ttd.inf.rpc.core.proxy.RpcInvocationHandler;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * RPC 服务 Bean 后处理器。
 * <p>
 * <ul>
 *   <li>收集所有标注 {@link com.ly.ttd.inf.rpc.api.annotation.RpcService} 的 Bean，暴露给 RPC 服务端</li>
 *   <li>处理所有标注 {@link com.ly.ttd.inf.rpc.api.annotation.Rpcwired} 的字段，注入远程代理</li>
 * </ul>
 */
@Slf4j
public class NacosRpcServicePostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private final NacosServiceDiscovery discovery;
    private final NacosRpcProperties properties;
    private final LoadBalancerStrategy loadBalancer;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;

    /** 存储所有 RPC 发布端服务 Bean <接口名, Bean 实例> */
    private final java.util.Map<String, Object> exportedServices = new java.util.concurrent.ConcurrentHashMap<>();

    private ConfigurableListableBeanFactory beanFactory;

    public NacosRpcServicePostProcessor(NacosServiceDiscovery discovery, NacosRpcProperties properties,
                                        ObjectMapper objectMapper) {
        this.discovery = discovery;
        this.properties = properties;
        this.loadBalancer = LoadBalancerStrategy.random();
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(properties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(properties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(properties.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 收集 @RpcService 的实现 Bean
        Class<?> beanClass = bean.getClass();
        Class<?>[] interfaces = beanClass.getInterfaces();
        for (Class<?> iface : interfaces) {
            RpcService annotation = iface.getAnnotation(RpcService.class);
            if (annotation != null) {
                String serviceName = resolveServiceName(annotation, iface);
                exportedServices.put(iface.getName(), bean);
                log.info("Exported RPC service: {} -> {}", serviceName, iface.getName());
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 处理 @Rpcwired 字段，注入远程代理
        Class<?> beanClass = bean.getClass();
        ReflectionUtils.doWithFields(beanClass, field -> {
            Rpcwired wired = field.getAnnotation(Rpcwired.class);
            if (wired != null) {
                Object proxy = createRpcProxy(field, wired);
                field.setAccessible(true);
                field.set(bean, proxy);
                log.info("Injected RPC proxy for field: {}.{} -> {}",
                        beanClass.getSimpleName(), field.getName(), field.getType().getSimpleName());
            }
        });
        return bean;
    }

    /**
     * 为 @Rpcwired 字段创建远程代理。
     */
    private Object createRpcProxy(Field field, Rpcwired wired) {
        Class<?> interfaceClass = field.getType();

        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("@Rpcwired field must be an interface type: " + field.getName());
        }

        String serviceName = wired.serviceName();
        if (!StringUtils.hasText(serviceName)) {
            // 从接口上的 @NacosRpcService 获取
            RpcService annotation = interfaceClass.getAnnotation(RpcService.class);
            if (annotation != null && StringUtils.hasText(annotation.serviceName())) {
                serviceName = annotation.serviceName();
            } else {
                // 从应用属性读取默认服务名
                serviceName = properties.getAppName();
            }
        }

        String version = wired.version();
        if (!StringUtils.hasText(version)) {
            RpcService annotation = interfaceClass.getAnnotation(RpcService.class);
            if (annotation != null) {
                version = annotation.version();
            }
        }

        // 注册服务变更订阅（缓存刷新）
        String finalServiceName = serviceName;
        discovery.subscribe(serviceName, () ->
                log.debug("Service '{}' instances updated, will refresh on next call", finalServiceName));

        RpcInvocationHandler handler = new RpcInvocationHandler(
                serviceName, version, wired.timeout(),
                interfaceClass, discovery, loadBalancer, httpClient, objectMapper, properties);

        return Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                handler);
    }

    /**
     * 解析导出的服务名。
     */
    private String resolveServiceName(RpcService annotation, Class<?> iface) {
        String name = annotation.serviceName();
        if (StringUtils.hasText(name)) {
            return name;
        }
        return properties.getAppName();
    }

    /**
     * 获取已导出的所有服务 Bean。
     */
    public java.util.Map<String, Object> getExportedServices() {
        return exportedServices;
    }
}
