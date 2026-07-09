package com.ly.ttd.inf.rpc.core.lifecycle;

import com.ly.ttd.inf.rpc.api.annotation.RpcService;
import com.ly.ttd.inf.rpc.api.constant.RpcConstant;
import com.ly.ttd.inf.rpc.core.autoconfigure.NacosRpcProperties;
import com.ly.ttd.inf.rpc.core.proxy.NacosRpcServicePostProcessor;
import com.ly.ttd.inf.rpc.core.registry.NacosServiceRegistry;
import com.ly.ttd.inf.rpc.core.server.NacosRpcServerBootstrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Nacos RPC 生命周期管理。
 * <p>
 * 在 Spring 容器初始化完成后启动 RPC 服务器并注册到 Nacos，
 * 在应用关闭时注销服务并停止 RPC 服务器。
 */
@Slf4j
public class NacosRpcLifecycle implements SmartLifecycle {

    private final NacosRpcProperties properties;
    private final NacosServiceRegistry registry;
    private final NacosRpcServerBootstrap serverBootstrap;
    private final NacosRpcServicePostProcessor servicePostProcessor;

    private volatile boolean running = false;
    private int actualPort;
    public NacosRpcLifecycle(NacosRpcProperties properties,
                             NacosServiceRegistry registry,
                             NacosRpcServerBootstrap serverBootstrap,
                             NacosRpcServicePostProcessor servicePostProcessor) {
        this.properties = properties;
        this.registry = registry;
        this.serverBootstrap = serverBootstrap;
        this.servicePostProcessor = servicePostProcessor;
    }

    @Override
    public void start() {
        if (running) return;

        // 1. 启动 RPC 服务器，获取实际端口
        actualPort = serverBootstrap.start();

        // 2. 收集导出的服务元数据
        Map<String, Object> exportedServices = servicePostProcessor.getExportedServices();
        if (exportedServices.isEmpty()) {
            log.warn("No services to register with Nacos, skipping registration.");
            running = true;
            return;
        }

        // 3. 构建元数据
        Map<String, String> metadata = new HashMap<>();
        metadata.put(RpcConstant.META_APP_NAME, properties.getAppName());
        metadata.put(RpcConstant.META_APP_VERSION, properties.getAppVersion());
        metadata.put(RpcConstant.META_ENV, properties.getEnv());
        metadata.put(RpcConstant.META_RPC_PORT, String.valueOf(actualPort));
        metadata.put(RpcConstant.META_START_TIME,
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        metadata.put("version", properties.getAppVersion());
        if (properties.getMetadata() != null) {
            metadata.putAll(properties.getMetadata());
        }

        // 4. 注册到 Nacos
        for (String interfaceName : exportedServices.keySet()) {
            // 获取接口上的 @NacosRpcService 注解来解析服务名
            try {
                Class<?> iface = Class.forName(interfaceName);
                RpcService annotation =
                        iface.getAnnotation(RpcService.class);
                String serviceName = properties.getAppName();
                if (annotation != null && !annotation.serviceName().isEmpty()) {
                    serviceName = annotation.serviceName();
                }

                registry.registerInstance(
                        serviceName,
                        properties.getServerHost(),
                        actualPort,
                        metadata
                );
                log.info("Registered RPC service '{}' -> {}:{} (interface: {})",
                        serviceName, properties.getServerHost(), actualPort, interfaceName);
            } catch (ClassNotFoundException e) {
                log.error("Failed to register service for interface: " + interfaceName, e);
            }
        }

        running = true;
        log.info("NacosRpcLifecycle started successfully.");
    }

    @Override
    public void stop() {
        if (!running) return;

        // 1. 注销 Nacos 注册
        Map<String, Object> exportedServices = servicePostProcessor.getExportedServices();
        for (String interfaceName : exportedServices.keySet()) {
            try {
                Class<?> iface = Class.forName(interfaceName);
                RpcService annotation =
                        iface.getAnnotation(RpcService.class);
                String serviceName = properties.getAppName();
                if (annotation != null && !annotation.serviceName().isEmpty()) {
                    serviceName = annotation.serviceName();
                }
                registry.deregisterInstance(serviceName, properties.getServerHost(), actualPort);
            } catch (ClassNotFoundException e) {
                log.warn("Service class not found during deregistration: {}", interfaceName);
            }
        }

        // 2. 停止 RPC 服务器
        serverBootstrap.stop();

        running = false;
        log.info("NacosRpcLifecycle stopped successfully.");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        // 在常规 Spring 生命周期后执行
        return Integer.MAX_VALUE - 10;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }
}
