# Redis 缓存

> 模块：`ttd-redis`
> 配置类：`RedisConfig`
> 客户端：`RedisTemplate<String, Object>`（Bean 名 `TtdRedisTemplate`）
> 封装接口：`XRedisClient`
> 连接池：Lettuce
> 布隆过滤器：Redisson

---

## 一、引入 POM 依赖

在业务项目 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.ly.ttd</groupId>
    <artifactId>ttd-redis</artifactId>
    <version>2026.0.0-SNAPSHOT</version>
</dependency>
```

> `ttd-redis` 已传递依赖 `ttd-nacos-config`、`spring-boot-starter-data-redis`、`commons-pool2`、`redisson-spring-boot-starter`。

---

## 二、配置

### 2.1 单点模式

```properties
# [必填] Redis 运行模式：single / sentinel / cluster
spring.redis.server.mode=single

# 连接信息
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=
spring.redis.database=0
```

### 2.2 哨兵模式

```properties
spring.redis.server.mode=sentinel
spring.redis.sentinel.master=mymaster
spring.redis.sentinel.nodes=127.0.0.1:26379,127.0.0.1:26380,127.0.0.1:26381
spring.redis.password=
spring.redis.database=0
```

### 2.3 集群模式

```properties
spring.redis.server.mode=cluster
spring.redis.cluster.nodes=127.0.0.1:7000,127.0.0.1:7001,127.0.0.1:7002
spring.redis.cluster.max-redirects=3
spring.redis.password=
```

### 2.4 Nacos 配置中心管理

通过 `ttd-nacos-config` 模块，将 Redis 配置放到 Nacos 中动态管理。

`application.properties` 中启用 Nacos 配置加载：

```properties
ttd.nacos.server-addr=127.0.0.1:8848
ttd.nacos[0].namespace=
ttd.nacos[0].group=DEFAULT_GROUP
ttd.nacos[0].dataId=myapp.properties
```

Nacos 配置文件中写入 Redis 相关配置即可（properties 格式）。

### 2.5 配置属性参考

| 配置项 | 必填 | 说明 |
|---|---|---|
| `spring.redis.server.mode` | 是 | 运行模式：`single` / `sentinel` / `cluster` |
| `spring.redis.host` | 是（single） | Redis 主机地址 |
| `spring.redis.port` | 否 | Redis 端口，默认 `6379` |
| `spring.redis.password` | 否 | Redis 密码，默认空 |
| `spring.redis.database` | 否 | 数据库编号，默认 `0` |
| `spring.redis.sentinel.master` | 是（sentinel） | 哨兵 master 名称 |
| `spring.redis.sentinel.nodes` | 是（sentinel） | 哨兵节点列表 |
| `spring.redis.cluster.nodes` | 是（cluster） | 集群节点列表 |
| `spring.redis.cluster.max-redirects` | 否 | 集群最大重定向次数 |

---

## 三、Bean 清单

| Bean 名称 | 类型 | 说明 |
|---|---|---|
| `nacosRedisConnectionFactory` | `LettuceConnectionFactory`（`@Primary`） | Redis 连接工厂 |
| `TtdRedisTemplate` | `RedisTemplate<String, Object>` | Redis 操作模板 |

---

## 四、XRedisClient 使用

`XRedisClient` 对 `RedisTemplate` 进行了封装，提供常用操作：

```java
import com.ly.ttd.redis.XRedisClient;

@Service
public class UserService {

    @Resource
    private XRedisClient redisClient;

    public void cacheUser(String userId, String userJson) {
        // 设置值，60秒过期
        redisClient.set("user:" + userId, userJson, 60L);

        // 获取值
        String cached = redisClient.get("user:" + userId);

        // 判断是否存在
        boolean exists = redisClient.exist("user:" + userId);

        // 自增
        Long count = redisClient.incr("visit:count");
    }
}
```

### XRedisClient API 一览

| 类别 | 方法 |
|---|---|
| **String** | `set` / `get` / `exist` / `setNx` / `pSetNx` / `strLen` / `append` |
| **计数** | `incr` / `decr` / `incrBy` / `incrDouble` / `decrDouble` |
| **Hash** | `hset` / `hget` |
| **List** | `lpush` / `rpush` / `lpop` / `rpop` / `lrange` / `llen` / `lrem` / `lset` |
| **过期** | `expire` |
| **布隆过滤器** | `btryInit` / `badd` / `bexists` |
