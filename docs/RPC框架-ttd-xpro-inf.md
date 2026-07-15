# RPC 框架（ttd-xpro-inf）

> 模块：`ttd-xpro-inf`（聚合工程）
> 子模块：`rpc-api` / `rpc-core` / `rpc-spring-boot-starter`
> 注册中心：Nacos
> HTTP 引擎：Undertow（服务端） / OkHttp（客户端）
> 序列化：Jackson

---

## 一、引入 POM 依赖

### 1.1 服务提供方 + 消费方（业务 srv 模块）

```xml
<dependency>
    <groupId>com.ly.ttd</groupId>
    <artifactId>rpc-spring-boot-starter</artifactId>
    <version>2026.0.0-SNAPSHOT</version>
</dependency>
```

> `rpc-spring-boot-starter` 已传递依赖 `rpc-core`（包含注册中心、服务发现、代理、服务端启动等全部能力）。

### 1.2 仅 API 接口（业务 api 模块）

如果只需要定义 RPC 接口（注解 + 接口定义），不需要实现：

```xml
<dependency>
    <groupId>com.ly.ttd</groupId>
    <artifactId>rpc-api</artifactId>
    <version>2026.0.0-SNAPSHOT</version>
</dependency>
```

> `rpc-api` 只包含 `@RpcService`、`@Rpcwired` 注解和常量定义，无运行时依赖。

---

## 二、配置

### 2.1 application.properties 配置

```properties
# ===== Nacos RPC 框架 =====

# [可选] 是否启用，默认 true
nacos.rpc.enabled=true

# Nacos 注册中心
nacos.rpc.server-addr=127.0.0.1:8848
nacos.rpc.namespace=
nacos.rpc.group=DEFAULT_GROUP

# RPC 服务端
nacos.rpc.server-host=0.0.0.0
nacos.rpc.server-port=20880

# 应用信息
nacos.rpc.app-name=my-service
nacos.rpc.app-version=1.0.0
nacos.rpc.env=dev

# Undertow 服务器
nacos.rpc.io-threads=4
nacos.rpc.worker-threads=32
nacos.rpc.direct-buffers=true

# OkHttp 客户端超时
nacos.rpc.connect-timeout=5000
nacos.rpc.read-timeout=10000
nacos.rpc.write-timeout=10000
```

### 2.2 配置属性参考

| 配置项 | 默认值 | 说明 |
|---|---|---|
| `nacos.rpc.enabled` | `true` | 是否启用 RPC 框架 |
| `nacos.rpc.server-addr` | `127.0.0.1:8848` | Nacos 服务地址 |
| `nacos.rpc.namespace` | 空 | Nacos 命名空间 |
| `nacos.rpc.group` | `DEFAULT_GROUP` | Nacos 分组 |
| `nacos.rpc.server-host` | `0.0.0.0` | RPC 服务监听主机 |
| `nacos.rpc.server-port` | `20880` | RPC 服务监听端口 |
| `nacos.rpc.app-name` | `unknown` | 应用名称（自动注册到 Nacos） |
| `nacos.rpc.app-version` | `1.0.0` | 应用版本 |
| `nacos.rpc.env` | `dev` | 环境标识 |
| `nacos.rpc.io-threads` | CPU 核心数 | Undertow IO 线程数 |
| `nacos.rpc.worker-threads` | IO线程数 x 8 | Undertow 工作线程数 |
| `nacos.rpc.direct-buffers` | `true` | 是否使用直接缓冲区 |
| `nacos.rpc.connect-timeout` | `5000` | OkHttp 连接超时（ms） |
| `nacos.rpc.read-timeout` | `10000` | OkHttp 读取超时（ms） |
| `nacos.rpc.write-timeout` | `10000` | OkHttp 写入超时（ms） |
| `nacos.rpc.metadata.*` | — | 附加元数据（注册到 Nacos） |

---

## 三、定义 RPC 服务（提供方）

### 3.1 在 api 模块定义接口

```java
package com.ly.ttd.biz.pay.api;

import com.ly.ttd.inf.rpc.api.annotation.RpcService;

@RpcService(serviceName = "biz-pay-rcs-dem-srv")
public interface PaymentService {

    PaymentResult pay(PaymentRequest request);
}
```

### 3.2 在 srv 模块实现接口

```java
package com.ly.ttd.biz.pay.srv.service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentResult pay(PaymentRequest request) {
        // 业务逻辑
        return new PaymentResult("SUCCESS");
    }
}
```

> 框架自动扫描 `@RpcService` 注解的接口，启动 Undertow 服务并将服务注册到 Nacos。

---

## 四、调用 RPC 服务（消费方）

使用 `@Rpcwired` 注解注入远程服务代理：

```java
@Service
public class OrderService {

    @Rpcwired(serviceName = "biz-pay-rcs-dem-srv")
    private PaymentService paymentService;

    public void createOrder(Order order) {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(order.getId());
        PaymentResult result = paymentService.pay(request);
    }
}
```

### @Rpcwired 参数

| 参数 | 默认值 | 说明 |
|---|---|---|
| `serviceName` | 从接口 `@RpcService` 读取 | 目标服务名称 |
| `version` | 空 | 服务版本（灰度调用） |
| `timeout` | `3000` | 调用超时时间（ms） |

---

## 五、Bean 清单

| Bean 名称 | 类型 | 说明 |
|---|---|---|
| `nacosRpcObjectMapper` | `ObjectMapper` | Jackson 序列化（`@ConditionalOnMissingBean`） |
| `nacosServiceRegistry` | `NacosServiceRegistry` | Nacos 服务注册 |
| `nacosServiceDiscovery` | `NacosServiceDiscovery` | Nacos 服务发现 |
| `nacosRpcServicePostProcessor` | `NacosRpcServicePostProcessor` | 扫描 `@Rpcwired` 注入代理 |
| `nacosRpcServerBootstrap` | `NacosRpcServerBootstrap` | Undertow 服务端启动 |
| `nacosRpcLifecycle` | `NacosRpcLifecycle` | 生命周期管理（启动/停止注册与注销） |

---

## 六、模块结构

```
ttd-xpro-inf/
├── rpc-api/                    ← 注解与常量（@RpcService, @Rpcwired）
├── rpc-core/                   ← 核心实现（注册中心、服务发现、代理、Undertow 服务端）
└── rpc-spring-boot-starter/    ← Spring Boot 自动装配入口
```

| 模块 | 职责 |
|---|---|
| `rpc-api` | 定义 `@RpcService`、`@Rpcwired` 注解和 RPC 常量 |
| `rpc-core` | Nacos 注册/发现、Undertow 服务端、OkHttp 客户端代理、生命周期管理 |
| `rpc-spring-boot-starter` | `NacosRpcAutoConfiguration` 自动装配 |
