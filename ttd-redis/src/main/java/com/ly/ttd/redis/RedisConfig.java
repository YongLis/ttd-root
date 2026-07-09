package com.ly.ttd.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.ttd.nacos.util.EnvPropertyUtil;
import com.ly.ttd.redis.consts.RedisConsts;
import com.ly.ttd.redis.consts.RedisRunModeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Properties;

/**
 * Redis 配置 — 从 Spring Environment（含 Nacos PropertySource）读取配置。
 * <p>
 * Nacos 配置在 bootstrap 阶段同步到 Environment，因此 @Bean 方法执行时已可用。
 *
 * @author yong.li
 * @since 2026/3/5 15:14
 */
@Configuration
@Slf4j
public class RedisConfig {
    @Resource
    private ConfigurableEnvironment env;

    @Bean("nacosRedisConnectionFactory")
    @Primary
    public RedisConnectionFactory buildConnectionFactory() {
        log.info("load redis config from Environment");

        String serverMode = env.getProperty(RedisConsts.SERVER_MODE);
        if (StringUtils.isEmpty(serverMode)) {
            log.error("redis config serverMode is empty, please set spring.redis.server.mode");
            throw new IllegalStateException("spring.redis.server.mode is missing");
        }
        log.info("redis config serverMode is {}", serverMode);

        String host = env.getProperty(RedisConsts.REDIS_HOST);
        int port = Integer.parseInt(env.getProperty(RedisConsts.REDIS_PORT, "6379"));
        String password = env.getProperty("spring.redis.password", "");
        int database = Integer.parseInt(env.getProperty("spring.redis.database", "0"));

        LettuceConnectionFactory factory;
        if (RedisRunModeEnum.SINGLE.getMode().equals(serverMode)) {
            factory = new LettuceConnectionFactory(host, port);
        } else if (RedisRunModeEnum.SENTINEL.getMode().equals(serverMode)) {
            Properties properties = EnvPropertyUtil.collect(env, RedisConsts.REDIS_PREFIX);
            factory = new LettuceConnectionFactory(RedisSentinelConfiguration.of(
                    new PropertiesPropertySource("redis-sentinel", properties)));
        } else {
            Properties properties = EnvPropertyUtil.collect(env, RedisConsts.REDIS_PREFIX);
            factory = new LettuceConnectionFactory(RedisClusterConfiguration.of(
                    new PropertiesPropertySource("redis-cluster", properties)));
        }

        if (StringUtils.isNotEmpty(password)) {
            factory.setPassword(password);
        }
        factory.setDatabase(database);
        factory.afterPropertiesSet();
        log.info("redis connection factory init success, mode={}, host={}:{}/{}", serverMode, host, port, database);
        return factory;
    }
    @Bean("TtdRedisTemplate")
    @DependsOn("nacosRedisConnectionFactory")
    public RedisTemplate<String, Object> initRedisTemplate(@Qualifier("nacosRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        log.info("redis template init success");
        return redisTemplate;
    }
}
