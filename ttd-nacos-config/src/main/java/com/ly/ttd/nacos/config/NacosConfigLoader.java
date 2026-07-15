package com.ly.ttd.nacos.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.config.NacosConfigService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.StringReader;
import java.util.*;

/**
 * Nacos 配置自动加载处理器
 * <p>
 * 在容器启动早期（Environment 准备阶段）读取 {@code ttd.nacos} 配置，
 * 从 Nacos 远程拉取各 namespace/group/dataId 的配置并注入 Spring Environment，
 * 确保后续 {@code RedisConfig}、{@code KafkaConfig} 等 Bean 创建时能读取到完整配置。
 * <p>
 * 通过 {@code META-INF/spring/org.springframework.boot.env.EnvironmentPostProcessor.imports} SPI 自动发现。
 *
 * @author yong.li
 * @since 2026/6/30 16:05
 */
@Order
public class NacosConfigLoader implements EnvironmentPostProcessor, Ordered, ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    // 使用 DeferredLog 记录日志
    private static final DeferredLog log = new DeferredLog();

//    private static final Logger log = LoggerFactory.getLogger(NacosConfigLoader.class);
    private static final String PREFIX = "ttd.nacos";
    private static final String DEFAULT_SERVER_ADDR = "127.0.0.1:8848";
    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    private static final long TIMEOUT_MS = 5000;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        System.out.println("NacosConfigLoader starting — scanning for ttd.nacos config entries");
        log.info("NacosConfigLoader starting — scanning for ttd.nacos config entries");

        String serverAddr = environment.getProperty(PREFIX + ".server-addr", DEFAULT_SERVER_ADDR);

        List<ConfigEntry> entries = parseEntries(environment);
        if (entries.isEmpty()) {
            log.info("NacosConfigLoader — no ttd.nacos[N].dataId entries found, skip remote config loading. "
                    + "Add ttd.nacos[0].namespace/group/dataId to application.properties.");

            return;
        }

        System.out.println("NacosConfigLoader config read ready"+ JSON.toJSONString(entries));

        Map<String, NacosConfigService> configServiceCache = new LinkedHashMap<>();
        MutablePropertySources propertySources = environment.getPropertySources();
        int loadedCount = 0;

        for (ConfigEntry entry : entries) {
            try {
                NacosConfigService cs = configServiceCache.computeIfAbsent(
                        serverAddr + "#" + entry.namespace,
                        k -> createConfigService(serverAddr, entry.namespace));
                if (cs == null) {
                    continue;
                }

                String content = cs.getConfig(entry.dataId, entry.group, TIMEOUT_MS);

                if (content == null || content.trim().isEmpty()) {
                    log.info(String.format("Nacos config not found — namespace=%s, group=%s, dataId=%s",
                            entry.namespace, entry.group, entry.dataId));
                    System.out.println(String.format("Nacos config not found — namespace=%s, group=%s, dataId=%s",
                            entry.namespace, entry.group, entry.dataId));
                    continue;
                }

                Properties props = new Properties();
                props.load(new StringReader(content));
                System.out.println("load config from nacos, value="+JSON.toJSONString(props));
                String sourceName = "nacos:" + entry.namespace + "/" + entry.group + "/" + entry.dataId;
                if (propertySources.contains("systemEnvironment")) {
                    propertySources.addAfter("systemEnvironment", new PropertiesPropertySource(sourceName, props));
                } else if (propertySources.contains("systemProperties")) {
                    propertySources.addAfter("systemProperties", new PropertiesPropertySource(sourceName, props));
                } else {
                    propertySources.addFirst(new PropertiesPropertySource(sourceName, props));
                }

                loadedCount++;
            } catch (Exception e) {
//                log.error("Failed to load Nacos config — namespace={}, group={}, dataId={}",
//                        entry.namespace, entry.group, entry.dataId, e);
                log.error(e);
               e.printStackTrace();
            }
        }

        log.info("NacosConfigLoader completed");
    }

    private List<ConfigEntry> parseEntries(ConfigurableEnvironment environment) {
        List<ConfigEntry> entries = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String dataId = environment.getProperty(PREFIX + "[" + i + "].dataId");
            if (dataId == null || dataId.trim().isEmpty()) {
                break;
            }
            String namespace = environment.getProperty(PREFIX + "[" + i + "].namespace", "");
            String group = environment.getProperty(PREFIX + "[" + i + "].group", DEFAULT_GROUP);
            entries.add(new ConfigEntry(namespace, group, dataId.trim()));
            log.info(String.format("Nacos config entry[%d] — namespace=%s, group=%s, dataId=%s", i, namespace, group, dataId));
        }
        return entries;
    }

    private NacosConfigService createConfigService(String serverAddr, String namespace) {
        try {
            Properties nacosProps = new Properties();
            nacosProps.setProperty("serverAddr", serverAddr);
            if (namespace != null && !namespace.isEmpty()) {
                nacosProps.setProperty("namespace", namespace);
            }
            return new NacosConfigService(nacosProps);
        } catch (Exception e) {
//            log.error("Failed to create Nacos ConfigService — server={}", serverAddr, e);
            log.error(e);
            return null;
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        log.replayTo(NacosConfigLoader.class);
    }

    /**
     * 必须在 ConfigDataEnvironmentPostProcessor (HIGHEST_PRECEDENCE + 10) 之后执行，
     * 确保 application.properties 已加载到 Environment 中。
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }

    private static class ConfigEntry {
        final String namespace;
        final String group;
        final String dataId;

        ConfigEntry(String namespace, String group, String dataId) {
            this.namespace = namespace;
            this.group = group;
            this.dataId = dataId;
        }
    }
}
