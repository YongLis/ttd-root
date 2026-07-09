package com.ly.ttd.inf.rpc.core.discovery;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 简单负载均衡策略
 * <p>
 * 从可用实例列表中按策略选出一个目标节点。
 */
public interface LoadBalancerStrategy {
    /**
     * 从实例列表中选择一个。
     *
     * @param instances 可用实例列表
     * @return 选中的实例
     */
    Instance select(List<Instance> instances);

    /**
     * 随机负载均衡策略（默认）。
     */
    static LoadBalancerStrategy random() {
        return instances -> {
            if (instances == null || instances.isEmpty()) {
                return null;
            }
            int index = ThreadLocalRandom.current().nextInt(instances.size());
            return instances.get(index);
        };
    }

    /**
     * 轮询负载均衡策略。
     */
    static LoadBalancerStrategy roundRobin() {
        return new RoundRobinStrategy();
    }
}
