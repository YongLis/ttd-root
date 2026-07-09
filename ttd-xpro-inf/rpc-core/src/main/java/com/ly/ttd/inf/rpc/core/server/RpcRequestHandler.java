package com.ly.ttd.inf.rpc.core.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.ttd.inf.rpc.core.protocol.RpcRequest;
import com.ly.ttd.inf.rpc.core.protocol.RpcResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * RPC 请求分发处理器——Undertow 的 HTTP 请求处理器。
 * <p>
 * 接收 HTTP RPC 请求，反序列化，反射调用本地服务实现，返回结果。
 */
@Slf4j
public class RpcRequestHandler implements HttpHandler {
    private final Map<String, Object> exportedServices;
    private final ObjectMapper objectMapper;

    public RpcRequestHandler(Map<String, Object> exportedServices, ObjectMapper objectMapper) {
        this.exportedServices = exportedServices;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        long startTime = System.currentTimeMillis();

        // 异步非阻塞处理
        exchange.dispatch(() -> {
            try {
                // 仅处理 POST
                if (!exchange.getRequestMethod().toString().equals("POST")) {
                    sendError(exchange, 405, "Only POST is supported");
                    return;
                }

                exchange.getRequestReceiver().receiveFullBytes((exch, bytes) -> {
                    try {
                        // 反序列化请求
                        String body = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
                        RpcRequest request = objectMapper.readValue(body, RpcRequest.class);

                        log.debug("RPC server received: {}#{}", request.getClassName(), request.getMethodName());

                        // 查找本地服务 Bean
                        Object serviceBean = exportedServices.get(request.getClassName());
                        if (serviceBean == null) {
                            sendError(exchange, 404, "Service not found: " + request.getClassName());
                            return;
                        }

                        // 解析参数类型
                        Class<?>[] paramTypes = toClasses(request.getParameterTypes());

                        // 查找目标方法
                        Method method = serviceBean.getClass().getMethod(request.getMethodName(), paramTypes);

                        // 反序列化参数值
                        Object[] args = deserializeArgs(request.getParameters(), paramTypes);

                        // 反射调用
                        Object result = method.invoke(serviceBean, args);

                        long cost = System.currentTimeMillis() - startTime;

                        // 构建响应
                        RpcResponse response = RpcResponse.ok(result, method.getReturnType().getName());
                        response.setCostMillis(cost);

                        String json = objectMapper.writeValueAsString(response);
                        exchange.setStatusCode(200);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send(json);

                        log.debug("RPC server response: {}#{} cost={}ms", request.getClassName(), request.getMethodName(), cost);

                    } catch (Exception e) {
                        log.error("RPC invoke error", e);
                        sendError(exchange, 500, "Invocation error: " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                log.error("RPC handle error", e);
                sendError(exchange, 500, "Internal error: " + e.getMessage());
            }
        });
    }

    private void sendError(HttpServerExchange exchange, int status, String message) {
        try {
            RpcResponse response = RpcResponse.fail(message, new RuntimeException(message));
            String json = objectMapper.writeValueAsString(response);
            exchange.setStatusCode(status);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(json);
        } catch (Exception e) {
            exchange.setStatusCode(status);
            exchange.getResponseSender().send("{\"success\":false,\"errorMessage\":\"" + message + "\"}");
        }
    }

    private Class<?>[] toClasses(String[] typeNames) throws ClassNotFoundException {
        if (typeNames == null) return new Class[0];
        Class<?>[] types = new Class<?>[typeNames.length];
        for (int i = 0; i < typeNames.length; i++) {
            types[i] = Class.forName(typeNames[i]);
        }
        return types;
    }

    private Object[] deserializeArgs(Object[] parameters, Class<?>[] paramTypes) {
        if (parameters == null) return new Object[0];
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            args[i] = objectMapper.convertValue(parameters[i], paramTypes[i]);
        }
        return args;
    }
}
