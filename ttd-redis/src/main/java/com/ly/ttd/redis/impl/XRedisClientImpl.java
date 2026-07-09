package com.ly.ttd.redis.impl;

import com.ly.ttd.redis.XRedisClient;
import jakarta.annotation.Resource;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author yong.li
 * @since 2026/3/6 09:31
 */
@Service
public class XRedisClientImpl implements XRedisClient {

    @Resource
    @Qualifier("TtdRedisTemplate")
    private RedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;


    @Override
    public boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void set(String key, String value, Long timeoutSecond) {
        redisTemplate.opsForValue().set(key, value, timeoutSecond, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean setNx(String key, String value, Long timeoutSecond) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, timeoutSecond, TimeUnit.SECONDS);
        return result;
    }

    @Override
    public int strLen(String key) {
        Long length = redisTemplate.opsForValue().size(key);
        return length != null ? length.intValue() : 0;
    }

    @Override
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public Long decr(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    @Override
    public Long incrBy(String key, Long increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    @Override
    public Long decr(String key, int decrement) {
        return redisTemplate.opsForValue().decrement(key, decrement);
    }

    @Override
    public Boolean pSetNx(String key, String value, Long timeoutMs) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, timeoutMs, TimeUnit.MILLISECONDS);
        return result;
    }

    @Override
    public Double incrDouble(String key, Double increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    @Override
    public Double decrDouble(String key, Double decrement) {
        return redisTemplate.opsForValue().increment(key, -decrement);
    }

    @Override
    public void append(String key, String value) {
        redisTemplate.opsForValue().append(key, value);
    }

    @Override
    public void hset(String key, String field, String value, Long timeoutSecond) {
        redisTemplate.opsForHash().put(key, field, value);
        if (timeoutSecond != null && timeoutSecond > 0) {
            redisTemplate.expire(key, timeoutSecond, TimeUnit.SECONDS);
        }
    }

    @Override
    public String hget(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    @Override
    public Object lpop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public int llen(String key) {
        Long length = redisTemplate.opsForList().size(key);
        return length != null ? length.intValue() : 0;
    }

    @Override
    public void lpush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public void lpush(String key, List<String> values) {
        if (values != null && !values.isEmpty()) {
            redisTemplate.opsForList().leftPushAll(key, values);
        }
    }

    @Override
    public void expire(String key, Long timeoutSecond) {
        if (timeoutSecond != null && timeoutSecond > 0) {
            redisTemplate.expire(key, timeoutSecond, TimeUnit.SECONDS);
        }
    }

    @Override
    public List<String> lrange(String key, Long start, Long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public void lrem(String key, Long count, String value) {
        redisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public void lset(String key, Long index, String value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    @Override
    public Object rpop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public void rpush(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public void rpush(String key, List<Object> values) {
        if (values != null && !values.isEmpty()) {
            redisTemplate.opsForList().rightPushAll(key, values);
        }
    }

    @Override
    public boolean btryInit(String key, Long expectedInsertions, double falseProbability) {
        // 布隆过滤器初始化
        // 注意：需要Redis 4.0+版本支持布隆过滤器
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(key);
        return bloomFilter.tryInit(expectedInsertions, falseProbability);
    }

    @Override
    public boolean badd(String key, String element) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(key);
        return bloomFilter.add(element);
    }

    @Override
    public Long badd(String key, Set<String> elements) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(key);
        return bloomFilter.add(elements);
    }

    @Override
    public boolean bexists(String key, String element) {
        // 检查键是否存在
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(key);
        return bloomFilter.contains(element);
    }


}
