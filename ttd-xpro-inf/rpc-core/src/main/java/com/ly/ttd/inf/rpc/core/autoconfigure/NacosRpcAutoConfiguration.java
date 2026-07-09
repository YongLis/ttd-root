package com.ly.ttd.inf.rpc.core.autoconfigure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ly.ttd.inf.rpc.core.discovery.NacosServiceDiscovery;
import com.ly.ttd.inf.rpc.core.lifecycle.NacosRpcLifecycle;
import com.ly.ttd.inf.rpc.core.proxy.NacosRpcServicePostProcessor;
import com.ly.ttd.inf.rpc.core.registry.NacosServiceRegistry;
import com.ly.ttd.inf.rpc.core.server.NacosRpcServerBootstrap;
import com.ly.ttd.inf.rpc.core.util.AppUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Nacos RPC 框架自动配置。
 * <p>
 * 自动装配 Nacos RPC 框架所需的所有 Bean。
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(NacosRpcProperties.class)
@ConditionalOnProperty(prefix = "nacos.rpc", name = "enabled", havingValue = "true", matchIfMissing = true)
public class NacosRpcAutoConfiguration {

    private final NacosRpcProperties properties;
    private final ApplicationContext applicationContext;

    public NacosRpcAutoConfiguration(NacosRpcProperties properties, ApplicationContext applicationContext) {
        this.properties = properties;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        // 从 app.properties 填充默认应用信息
        String appName = AppUtils.getAppName();
        if (appName != null && ("unknown".equals(properties.getAppName()) || properties.getAppName() == null)) {
            properties.setAppName(appName);
        }
        String appVersion = AppUtils.getAppVersion();
        if (appVersion != null && ("1.0.0".equals(properties.getAppVersion()) || properties.getAppVersion() == null)) {
            properties.setAppVersion(appVersion);
        }
        // 从环境变量读取环境信息
        String env = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        if (env != null) {
            properties.setEnv(env);
        }

        log.info("NacosRpcAutoConfiguration initialized: appName={}, appVersion={}, env={}",
                properties.getAppName(), properties.getAppVersion(), properties.getEnv());
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper nacosRpcObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.findAndRegisterModules();
        return mapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosServiceRegistry nacosServiceRegistry() throws Exception {
        return new NacosServiceRegistry(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosServiceDiscovery nacosServiceDiscovery() throws Exception {
        return new NacosServiceDiscovery(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosRpcServicePostProcessor nacosRpcServicePostProcessor(
            NacosServiceDiscovery discovery, ObjectMapper objectMapper) {
        return new NacosRpcServicePostProcessor(discovery, properties, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosRpcServerBootstrap nacosRpcServerBootstrap(
            NacosRpcServicePostProcessor servicePostProcessor, ObjectMapper objectMapper) {
        return new NacosRpcServerBootstrap(properties, servicePostProcessor, objectMapper);
    }
    @Bean
    @ConditionalOnMissingBean
    public NacosRpcLifecycle nacosRpcLifecycle(
            NacosServiceRegistry registry,
            NacosRpcServerBootstrap serverBootstrap,
            NacosRpcServicePostProcessor servicePostProcessor) {
        return new NacosRpcLifecycle(properties, registry, serverBootstrap, servicePostProcessor);
    }
}
