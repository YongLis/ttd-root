# Nacos 远程配置加载

> 模块：`ttd-nacos-config`
> 核心类：`NacosConfigLoader`（实现 `EnvironmentPostProcessor`）
> 工具类：`EnvPropertyUtil`
> SPI 机制：`META-INF/spring.factories` 自动发现

---

## 一、引入 POM 依赖

在业务项目 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.ly.ttd</groupId>
    <artifactId>ttd-nacos-config</artifactId>
    <version>2026.0.0-SNAPSHOT</version>
</dependency>
```

> `ttd-nacos-config` 是框架基础模块，`ttd-redis`、`ttd-kafka` 等模块已传递依赖，通常无需单独引入。

---

## 二、配置

### 2.1 application.properties 配置

```properties
# Nacos 服务地址（默认 127.0.0.1:8848）
ttd.nacos.server-addr=127.0.0.1:8848

# Nacos 配置项（支持多组，索引从 0 开始）
ttd.nacos[0].namespace=
ttd.nacos[0].group=DEFAULT_GROUP
ttd.nacos[0].dataId=myapp.properties

# 多组配置示例
# ttd.nacos[1].namespace=prod
# ttd.nacos[1].group=DEFAULT_GROUP
# ttd.nacos[1].dataId=shared.properties
```

| 配置项 | 默认值 | 说明 |
|---|---|---|
| `ttd.nacos.server-addr` | `127.0.0.1:8848` | Nacos 服务地址 |
| `ttd.nacos[N].namespace` | 空（公共命名空间） | Nacos 命名空间 |
| `ttd.nacos[N].group` | `DEFAULT_GROUP` | Nacos 配置分组 |
| `ttd.nacos[N].dataId` | — | **必填**，Nacos 配置文件 dataId |

### 2.2 配置加载时机

`NacosConfigLoader` 实现 `EnvironmentPostProcessor`，在 Spring Boot 启动早期执行：

1. 在 `ConfigDataEnvironmentPostProcessor`（加载 `application.properties`）**之后**执行（order = `HIGHEST_PRECEDENCE + 11`）
2. 从 Nacos 拉取配置注入 Spring Environment
3. 后续所有 `@Bean`、`@Value`、`@ConfigurationProperties` 均可读取到 Nacos 远程配置

### 2.3 配置优先级

Nacos 远程配置注入到 `systemEnvironment` 之后，优先级如下：

| 优先级 | 配置来源 |
|---|---|
| 高 | 命令行参数 / 系统属性 / 环境变量 |
| 中 | **Nacos 远程配置** |
| 低 | `application.properties` |

---

## 三、EnvPropertyUtil 工具类

用于从 Spring Environment 中批量采集指定前缀的配置项，常用于构建 Redis、Kafka 等客户端的原生 Properties：

```java
import com.ly.ttd.nacos.util.EnvPropertyUtil;

// 采集所有 spring.redis.* 前缀的配置（去掉前缀）
Properties redisProps = EnvPropertyUtil.collect(env, "spring.redis.");
// 结果: host=127.0.0.1, port=6379, ...

// 获取单个属性
String value = EnvPropertyUtil.getProperty(env, "spring.redis.host");

// 获取必需属性（不存在时抛异常）
String required = EnvPropertyUtil.getRequiredProperty(env, "spring.redis.host");
```

---

## 四、SPI 注册文件

模块通过以下文件注册 `EnvironmentPostProcessor`，无需手动配置：

- `META-INF/spring.factories`
- `META-INF/spring/org.springframework.boot.env.EnvironmentPostProcessor.imports`

> 注意：SPI 文件**必须以换行符 `\n` 结尾**，否则 Spring Boot 无法解析最后一行。
