package com.ly.ttd.inf.rpc.core.registry;

import java.util.Map;

/**
 * 服务注册接口。
 * <p>
 * 将当前服务的 RPC 端点注册到 Nacos 注册中心，
 * 供其他微服务发现和调用。
 */
public interface ServiceRegistry {

    /**
     * 注册服务实例到 Nacos。
     *
     * @param serviceName 服务名
     * @param ip          IP 地址
     * @param port        RPC 端口
     * @param metadata    附加元数据（版本、环境、应用名等）
     */
    void registerInstance(String serviceName, String ip, int port, Map<String, String> metadata);

    /**
     * 从 Nacos 注销服务实例。
     *
     * @param serviceName 服务名
     * @param ip          IP 地址
     * @param port        RPC 端口
     */
    void deregisterInstance(String serviceName, String ip, int port);
}
