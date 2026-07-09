package com.ly.ttd.inf.rpc.core.discovery;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡实现。
 */
class RoundRobinStrategy implements LoadBalancerStrategy {

    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public Instance select(List<Instance> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        int idx = index.getAndIncrement() & Integer.MAX_VALUE; // 避免负数
        return instances.get(idx % instances.size());
    }
}
