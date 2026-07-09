package com.ly.ttd.inf.rpc.core.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.ttd.inf.rpc.core.autoconfigure.NacosRpcProperties;
import com.ly.ttd.inf.rpc.core.proxy.NacosRpcServicePostProcessor;
import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Nacos RPC 服务端启动器。
 * <p>
 * 在应用启动时启动嵌入式 Undertow HTTP 服务器，用于接收 RPC 请求。
 */
@Slf4j
public class NacosRpcServerBootstrap {
    private final NacosRpcProperties properties;
    private final NacosRpcServicePostProcessor servicePostProcessor;
    private final ObjectMapper objectMapper;
    private Undertow server;

    public NacosRpcServerBootstrap(NacosRpcProperties properties,
                                   NacosRpcServicePostProcessor servicePostProcessor,
                                   ObjectMapper objectMapper) {
        this.properties = properties;
        this.servicePostProcessor = servicePostProcessor;
        this.objectMapper = objectMapper;
    }

    /**
     * 启动 RPC 服务器。
     *
     * @return 实际监听的端口
     */
    public int start() {
        int port = properties.getServerPort();
        Map<String, Object> exportedServices = servicePostProcessor.getExportedServices();

        if (exportedServices.isEmpty()) {
            log.warn("No RPC services exported, RPC server will not start.");
            return port;
        }

        // 随机器端口：如果未指定端口或端口为0，使用随机端口
        if (port == 0) {
            port = findAvailablePort();
        }

        RpcRequestHandler handler = new RpcRequestHandler(exportedServices, objectMapper);

        server = Undertow.builder()
                .addHttpListener(port, properties.getServerHost())
                .setHandler(handler)
                .setIoThreads(properties.getIoThreads())
                .setWorkerThreads(properties.getWorkerThreads())
                .setDirectBuffers(properties.isDirectBuffers())
                .build();

        server.start();
        log.info("Nacos RPC server started on {}:{} (services: {})",
                properties.getServerHost(), port, exportedServices.keySet());

        return port;
    }

    /**
     * 停止 RPC 服务器。
     */
    public void stop() {
        if (server != null) {
            try {
                server.stop();
                log.info("Nacos RPC server stopped.");
            } catch (Exception e) {
                log.warn("Nacos RPC server stop error", e);
            }
        }
    }

    private int findAvailablePort() {
        // 简单实现：从默认端口开始尝试
        int port = properties.getServerPort() != 0 ? properties.getServerPort() : 19090;
        // 实际生产环境应使用 ServerSocket 探测可用端口
        return port;
    }
}
