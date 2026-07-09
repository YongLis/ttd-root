package com.ly.ttd.redis;

import java.util.List;
import java.util.Set;

/**
 * @author yong.li
 * @since 2026/3/5 15:18
 */
public interface XRedisClient {
    /**
     * 存在
     */
    boolean exist(String key);

    /**
     * @param key           键
     * @param value         值
     * @param timeoutSecond 过期时间，单位秒
     */
    void set(String key, String value, Long timeoutSecond);

    /**
     * 获取值
     *
     * @param key 键
     * @return
     */
    String get(String key);

    Boolean setNx(String key, String value, Long timeoutSecond);

    int strLen(String key);

    Long incr(String key);

    Long decr(String key);

    Long incrBy(String key, Long increment);

    Long decr(String key, int decrement);

    Boolean pSetNx(String key, String value, Long timeoutMs);


    Double incrDouble(String key, Double increment);

    /**
     * 将 key 所储存的值加上给定的浮点增量值（increment） 。
     */
    Double decrDouble(String key, Double decrement);

    /**
     * 追加字符串
     * 如果 key 已经存在并且是一个字符串， APPEND 命令将指定的 value 追加到该 key 原来值（value）的末尾。
     */
    void append(String key, String value);

    void hset(String key, String field, String value, Long timeoutSecond);

    String hget(String key, String field);

    /**
     * 移出并获取列表的第一个元素
     */
    Object lpop(String key);

    /**
     * 获取列表长度
     */
    int llen(String key);

    /**
     * 将一个插入到列表头部
     */
    void lpush(String key, Object value);

    void lpush(String key, List<String> values);

    /**
     * 设置过期时间
     */
    void expire(String key, Long timeoutSecond);

    /**
     * 获取列表指定范围内的元素
     */
    List<String> lrange(String key, Long start, Long end);

    /**
     * 移除列表元素
     * COUNT 的值可以是以下几种：
     * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
     * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
     * count = 0 : 移除表中所有与 VALUE 相等的值。
     */
    void lrem(String key, Long count, String value);

    /**
     * 通过索引设置列表元素的值
     */
    void lset(String key, Long index, String value);

    /**
     * 移除列表的最后一个元素，返回值为移除的元素。
     */
    Object rpop(String key);

    /**
     * 在列表中添加一个或多个值到列表尾部
     */
    void rpush(String key, Object value);

    void rpush(String key, List<Object> values);


    /**
     * 布隆过滤器初始化
     */
    boolean btryInit(String key, Long expectedInsertions, double falseProbability);


    /**
     * 布隆过滤器添加数据
     */
    boolean badd(String key, String element);

    Long badd(String key, Set<String> element);

    /**
     * 布隆过滤器判定是否存在
     */
    boolean bexists(String key, String element);

}
