package com.ly.ttd.inf.rpc.core.proxy;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ly.ttd.inf.rpc.core.autoconfigure.NacosRpcProperties;
import com.ly.ttd.inf.rpc.core.discovery.LoadBalancerStrategy;
import com.ly.ttd.inf.rpc.core.discovery.NacosServiceDiscovery;
import com.ly.ttd.inf.rpc.core.protocol.RpcRequest;
import com.ly.ttd.inf.rpc.core.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

/**
 * RPC 动态代理调用处理器。
 * <p>
 * 拦截接口方法调用，通过 Nacos 发现服务地址，发起 HTTP RPC 请求。
 */
@Slf4j
public class RpcInvocationHandler implements InvocationHandler {

    private final String serviceName;
    private final String version;
    private final long timeout;
    private final Class<?> interfaceClass;
    private final NacosServiceDiscovery discovery;
    private final LoadBalancerStrategy loadBalancer;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String rpcPath;

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    public RpcInvocationHandler(String serviceName, String version, long timeout,
                                Class<?> interfaceClass, NacosServiceDiscovery discovery,
                                LoadBalancerStrategy loadBalancer, OkHttpClient httpClient,
                                ObjectMapper objectMapper, NacosRpcProperties properties) {
        this.serviceName = serviceName;
        this.version = version;
        this.timeout = timeout;
        this.interfaceClass = interfaceClass;
        this.discovery = discovery;
        this.loadBalancer = loadBalancer;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.rpcPath = properties.getRpcPath();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Object 原生方法直接调用
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        // 构建 RPC 请求
        RpcRequest request = RpcRequest.builder()
                .serviceName(serviceName)
                .version(version)
                .className(interfaceClass.getName())
                .methodName(method.getName())
                .parameterTypes(toTypeNames(method.getParameterTypes()))
                .parameters(args)
                .build();

        // 从 Nacos 发现服务实例
        List<Instance> instances = discovery.getInstances(serviceName, version);
        if (instances.isEmpty()) {
            throw new RuntimeException("No available instances for service: " + serviceName
                    + (version != null && !version.isEmpty() ? " (version=" + version + ")" : ""));
        }

        // 负载均衡选择目标实例
        Instance target = loadBalancer.select(instances);
        if (target == null) {
            throw new RuntimeException("Load balancer returned null for service: " + serviceName);
        }

        // 序列化请求
        String requestBody = objectMapper.writeValueAsString(request);

        // 构建 HTTP 请求
        String url = "http://" + target.getIp() + ":" + target.getPort() + rpcPath;
        okhttp3.Request httpRequest = new okhttp3.Request.Builder()
                .url(url)
                .post(RequestBody.create(requestBody, JSON_MEDIA_TYPE))
                .addHeader("X-Request-Id", UUID.randomUUID().toString().replace("-", ""))
                .addHeader("X-Service-Name", serviceName)
                .build();

        log.debug("RPC call: {} -> {}#{}(args={})", serviceName, target.getIp() + ":" + target.getPort(),
                method.getName(), args);

        // 执行 HTTP 调用
        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("RPC call failed with status: " + response.code()
                        + ", body: " + (response.body() != null ? response.body().string() : "null"));
            }

            String responseBody = response.body() != null ? response.body().string() : null;
            if (responseBody == null) {
                return null;
            }

            RpcResponse rpcResponse = objectMapper.readValue(responseBody, RpcResponse.class);
            if (!rpcResponse.isSuccess()) {
                throw new RuntimeException("RPC call failed: " + rpcResponse.getErrorMessage());
            }

            // 处理 void 返回
            if (method.getReturnType() == void.class || method.getReturnType() == Void.class) {
                return null;
            }

            // 反序列化返回值
            if (rpcResponse.getResult() == null) {
                return null;
            }

            Type returnType = method.getGenericReturnType();
            JavaType javaType = objectMapper.getTypeFactory().constructType(returnType);
            return objectMapper.convertValue(rpcResponse.getResult(), javaType);
        }
    }

    private String[] toTypeNames(Class<?>[] types) {
        if (types == null) return new String[0];
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getName();
        }
        return names;
    }
}
