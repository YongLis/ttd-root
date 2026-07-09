package com.ly.ttd.nacos.util;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Properties;

/**
 * Spring Environment 配置采集工具
 * <p>
 * 从 Environment 的所有 PropertySource（含 Nacos）中提取匹配前缀的配置项，
 * 用于构建 Redis、Kafka 等客户端的原生 Properties。
 *
 * @author yong.li
 * @since 2026/6/30
 */
public final class EnvPropertyUtil {

    private EnvPropertyUtil() {
    }

    /**
     * 从 Environment 中采集所有匹配前缀的属性，key 去除前缀后放入目标 Properties。
     *
     * @param env    Spring 环境
     * @param prefix 属性 key 前缀（如 "spring.redis."）
     * @return 去除前缀后的 Properties 副本
     */
    public static Properties collect(ConfigurableEnvironment env, String prefix) {
        Properties result = new Properties();
        for (PropertySource<?> ps : env.getPropertySources()) {
            if (ps instanceof EnumerablePropertySource) {
                for (String key : ((EnumerablePropertySource<?>) ps).getPropertyNames()) {
                    if (key.startsWith(prefix)) {
                        Object value = ps.getProperty(key);
                        if (value != null) {
                            result.setProperty(key.substring(prefix.length()), value.toString());
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取单个属性值，key 不存在时返回 null。
     */
    public static String getProperty(ConfigurableEnvironment env, String key) {
        return env.getProperty(key);
    }

    /**
     * 获取必需属性，key 不存在时抛出异常。
     */
    public static String getRequiredProperty(ConfigurableEnvironment env, String key) {
        String value = env.getProperty(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Required property '" + key + "' is missing");
        }
        return value;
    }
}
