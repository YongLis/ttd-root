package com.ly.ttd.inf.rpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ly.ttd.inf.rpc.core.autoconfigure.NacosRpcProperties;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Nacos 服务注册实现。
 * <p>
 * 将服务实例信息注册到注册中心，注册后 Nacos 负责心跳续约。
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {

    private final NamingService namingService;
    private final NacosRpcProperties properties;

    public NacosServiceRegistry(NacosRpcProperties properties) throws NacosException {
        this.properties = properties;
        this.namingService = NamingFactory.createNamingService(properties.getServerAddr());
        log.info("NacosServiceRegistry initialized, serverAddr={}", properties.getServerAddr());
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port, Map<String, String> metadata) {
        try {
            Instance instance = new Instance();
            instance.setIp(ip);
            instance.setPort(port);
            instance.setWeight(1.0);
            instance.setHealthy(true);
            instance.setEnabled(true);
            instance.setEphemeral(true); // 临时实例，心跳停止自动摘除
            instance.setMetadata(metadata);

            namingService.registerInstance(serviceName, properties.getGroup(), instance);
            log.info("Service registered to Nacos: {}@{}({}:{}) metadata={}",
                    serviceName, properties.getGroup(), ip, port, metadata);
        } catch (NacosException e) {
            log.error("Failed to register service to Nacos: " + serviceName, e);
            throw new RuntimeException("Nacos register failed: " + serviceName, e);
        }
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port) {
        try {
            namingService.deregisterInstance(serviceName, properties.getGroup(), ip, port);
            log.info("Service deregistered from Nacos: {}:{}({})", serviceName, ip, port);
        } catch (NacosException e) {
            log.error("Failed to deregister service from Nacos: " + serviceName, e);
        }
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
