package com.ly.ttd.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.ttd.config.nacos.NacosConfig;
import com.ly.ttd.config.nacos.client.NacosConfigClient;
import com.ly.ttd.config.nacos.consts.DefaultNacosConfigEnum;
import com.ly.ttd.redis.consts.RedisConsts;
import com.ly.ttd.redis.consts.RedisRunModeEnum;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author yong.li
 * @since 2026/3/5 15:14
 */
@Component
public class RedisConfig {
    private Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    @Resource
    private NacosConfigClient configClient;
    @Resource
    private NacosConfig nacosConfig;

    @Bean("nacosRedisConnectionFactory")
    @Primary
    public RedisConnectionFactory buildConnectionFactory() throws Exception{
        // redis配置分为通用配置和应用配置
        // 通用配置在redis.properties中
        String serverName = nacosConfig.getAppName();
        logger.info("load redis config start,source from namespace={}, group={}, dataId={}", nacosConfig.getNameSpaceId(),serverName, DefaultNacosConfigEnum.REDIS.getDataId());

        Properties properties = configClient.getRemoteConfig(nacosConfig.getNameSpaceId(),serverName
                , DefaultNacosConfigEnum.REDIS.getDataId());

        Properties appRedisProperties = configClient.getRemoteConfig(nacosConfig.getNameSpaceId(),
                serverName, DefaultNacosConfigEnum.APP.getDataId());
        if(null == properties || properties.isEmpty()){
            properties = new Properties();
        }
        if(!appRedisProperties.isEmpty()){
            properties.putAll(appRedisProperties);
        }

        String serverMode = properties.getProperty(RedisConsts.SERVER_MODE);
        if(StringUtils.isEmpty(serverMode)){
            logger.warn("redis config serverMode is empty, please set spring.redis.server.mode");
            throw new Exception("serverMode is empty");
        }
        logger.info("redis config serverMode is {}", serverMode);
        if(RedisRunModeEnum.SINGLE.getMode().equals(serverMode)){
            String host = properties.getProperty(RedisConsts.REDIS_HOST);
            int port = Integer.parseInt(properties.getProperty(RedisConsts.REDIS_PORT));
            return new LettuceConnectionFactory(host, port);
        } else if (RedisRunModeEnum.SENTINEL.getMode().equals(serverMode)) {
            PropertiesPropertySource source = new PropertiesPropertySource("remote",properties );
            return new LettuceConnectionFactory(RedisSentinelConfiguration.of(source));
        }

        PropertiesPropertySource source = new PropertiesPropertySource("remote",properties );
        return new LettuceConnectionFactory(RedisClusterConfiguration.of(source));
    }

    @Bean("TtdRedisTemplate")
    public RedisTemplate<String, Object> initRedisTemplate(@Qualifier("nacosRedisConnectionFactory") RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // JSON序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
        // String 的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key 采用String的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // valuex序列化方式采用jackson
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的序列化也用jackson
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        logger.info("redis template init success");
        return redisTemplate;
    }


}
