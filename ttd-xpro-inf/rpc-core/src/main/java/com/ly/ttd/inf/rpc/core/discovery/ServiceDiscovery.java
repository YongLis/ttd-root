package com.ly.ttd.inf.rpc.core.discovery;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 服务发现接口
 * <p>
 * 从 Nacos 注册中心查询指定服务的可用实例列表。
 */
public interface ServiceDiscovery {

    /**
     * 获取指定服务的所有健康实例。
     *
     * @param serviceName 服务名
     * @return 健康实例列表
     */
    List<Instance> getInstances(String serviceName);

    /**
     * 获取指定服务的健康实例，指定版本。
     *
     * @param serviceName 服务名
     * @param version     版本过滤
     * @return 健康实例列表
     */
    List<Instance> getInstances(String serviceName, String version);

    /**
     * 订阅服务变更事件。
     *
     * @param serviceName 服务名
     * @param listener    变更监听器
     */
    void subscribe(String serviceName, Runnable listener);
}
