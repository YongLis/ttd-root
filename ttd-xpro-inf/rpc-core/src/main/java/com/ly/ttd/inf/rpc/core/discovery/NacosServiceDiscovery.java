package com.ly.ttd.inf.rpc.core.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.StringUtils;
import com.ly.ttd.inf.rpc.core.autoconfigure.NacosRpcProperties;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Nacos 服务发现实现。
 * <p>
 * 从注册中心获取目标服务的可用节点地址，支持版本过滤。
 */
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery {
    private final NamingService namingService;
    private final NacosRpcProperties properties;

    /** 订阅缓存 <serviceName, listener> */
    private final Map<String, List<Runnable>> subscriberMap = new ConcurrentHashMap<>();

    public NacosServiceDiscovery(NacosRpcProperties properties) throws NacosException {
        this.properties = properties;
        this.namingService = NamingFactory.createNamingService(properties.getServerAddr());
        log.info("NacosServiceDiscovery initialized, serverAddr={}", properties.getServerAddr());
    }

    @Override
    public List<Instance> getInstances(String serviceName) {
        return getInstances(serviceName, null);
    }

    @Override
    public List<Instance> getInstances(String serviceName, String version) {
        try {
            List<Instance> instances = namingService.selectInstances(serviceName, properties.getGroup(), true);
            if (StringUtils.isNotBlank(version)) {
                instances = instances.stream()
                        .filter(inst -> version.equals(inst.getMetadata().get("version")))
                        .collect(Collectors.toList());
            }
            log.debug("Discovered {} instances for service '{}' (version={})", instances.size(), serviceName, version);
            return instances;
        } catch (NacosException e) {
            log.error("Failed to discover instances for service: " + serviceName, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void subscribe(String serviceName, Runnable listener) {
        subscriberMap.computeIfAbsent(serviceName, k -> {
            try {
                namingService.subscribe(serviceName, properties.getGroup(), event -> {
                    if (event instanceof NamingEvent) {
                        log.info("Service '{}' instances changed, notifying subscribers...", serviceName);
                        List<Runnable> callbacks = subscriberMap.get(serviceName);
                        if (callbacks != null) {
                            callbacks.forEach(Runnable::run);
                        }
                    }
                });
                log.info("Subscribed to service '{}' changes", serviceName);
            } catch (NacosException e) {
                log.error("Failed to subscribe service: " + serviceName, e);
            }
            return new ArrayList<>();
        });
        subscriberMap.get(serviceName).add(listener);
    }

    @PreDestroy
    public void shutdown() {
        try {
            namingService.shutDown();
            log.info("Nacos naming service shut down.");
        } catch (NacosException e) {
            log.warn("Nacos naming service shutdown error", e);
        }
    }
}
