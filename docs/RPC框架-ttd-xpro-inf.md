# RPC 框架（ttd-xpro-inf）

> 基于 Nacos 注册中心的轻量级 RPC 框架，支持服务自动注册与发现。
> HTTP 引擎：Undertow（服务端） / OkHttp（客户端）
> 序列化：Jackson

---

## 一、服务注册（提供方）

### 1.1 引入 POM 依赖

**srv 模块**（服务实现方）引入：

```xml
<dependency>
    <groupId>com.ly.ttd</groupId>
    <artifactId>rpc-spring-boot-starter</artifactId>
    <version>2026.0.0-SNAPSHOT</version>
</dependency>
```

> `rpc-spring-boot-starter` 已传递依赖 `rpc-core`（注册中心、Undertow 服务端、生命周期管理）。

**api 模块**（接口定义方）引入：

```xml
<dependency>
    <groupId>com.ly.ttd</groupId>
    <artifactId>rpc-api</artifactId>
    <version>2026.0.0-SNAPSHOT</version>
</dependency>
```

> `rpc-api` 只包含 `@RpcService` 注解和常量定义，无运行时依赖。

### 1.2 定义 RPC 接口

在 api 模块中定义接口，标注 `@RpcService`：

```java
package com.ly.ttd.biz.pay.api;

import com.ly.ttd.inf.rpc.api.annotation.RpcService;

@RpcService(serviceName = "biz-pay-rcs-dem-srv")
public interface PaymentService {

    PaymentResult pay(PaymentRequest request);
}
```

`@RpcService` 参数：

| 参数 | 默认值 | 说明 |
|---|---|---|
| `serviceName` | 应用名 | 注册到 Nacos 的服务名称 |
| `version` | 空 | 服务版本，用于灰度路由 |

### 1.3 实现 RPC 接口

在 srv 模块中实现接口（普通 Spring Bean 即可）：

```java
package com.ly.ttd.biz.pay.srv.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentResult pay(PaymentRequest request) {
        // 业务逻辑
        return new PaymentResult("SUCCESS");
    }
}
```

### 1.4 注册流程

框架自动完成以下流程，无需手动编码：

```
Spring 容器启动
  └── NacosRpcServicePostProcessor 扫描所有 Bean
      └── 发现实现了 @RpcService 接口的 Bean → 收集到 exportedServices
          └── NacosRpcLifecycle.start()（SmartLifecycle 阶段）
              ├── 1. NacosRpcServerBootstrap.start()
              │      └── 启动 Undertow HTTP 服务，监听 RPC 请求
              ├── 2. 构建元数据（app.name, app.version, env, rpc.port, start.time）
              └── 3. NacosServiceRegistry.registerInstance()
                     └── 注册到 Nacos（临时实例，心跳自动续约）
```

**关键类**：

| 类 | 职责 |
|---|---|
| `NacosRpcServicePostProcessor` | 扫描 `@RpcService` Bean，收集到 `exportedServices` |
| `NacosRpcServerBootstrap` | 启动 Undertow HTTP 服务端，接收 RPC 请求 |
| `NacosServiceRegistry` | 调用 Nacos Naming API 注册服务实例 |
| `NacosRpcLifecycle` | 生命周期管理：启动时注册，关闭时注销 |
| `RpcRequestHandler` | 处理 Undertow 收到的 HTTP 请求，反射调用目标方法 |

---

## 二、服务发现（消费方）

### 2.1 引入 POM 依赖

消费方 srv 模块引入（同时需要 api 模块依赖以获取接口定义）：

```xml
<!-- RPC 框架 -->
<dependency>
    <groupId>com.ly.ttd</groupId>
    <artifactId>rpc-spring-boot-starter</artifactId>
    <version>2026.0.0-SNAPSHOT</version>
</dependency>

<!-- 目标服务的 API 接口 -->
<dependency>
    <groupId>com.ly.ttd</groupId>
    <artifactId>biz-pay-rcs-dem-api</artifactId>
    <version>2026.0.0-SNAPSHOT</version>
</dependency>
```

### 2.2 注入远程服务代理

使用 `@Rpcwired` 注解注入远程服务代理，调用时自动发起 RPC 请求：

```java
package com.ly.ttd.biz.order.srv.service;

import com.ly.ttd.biz.pay.api.PaymentService;
import com.ly.ttd.inf.rpc.api.annotation.Rpcwired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Rpcwired(serviceName = "biz-pay-rcs-dem-srv")
    private PaymentService paymentService;

    public void createOrder(Order order) {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(order.getId());
        // 调用远程服务
        PaymentResult result = paymentService.pay(request);
    }
}
```

`@Rpcwired` 参数：

| 参数 | 默认值 | 说明 |
|---|---|---|
| `serviceName` | 从接口 `@RpcService` 读取 | 目标服务名称 |
| `version` | 空 | 服务版本，用于灰度调用（按 metadata.version 过滤实例） |
| `timeout` | `3000` | 单次调用超时时间（ms） |

### 2.3 调用流程

```
paymentService.pay(request)
  └── RpcInvocationHandler.invoke()（JDK 动态代理）
      ├── 1. NacosServiceDiscovery.getInstances(serviceName, version)
      │      └── 从 Nacos 查询健康实例，按 version 过滤
      ├── 2. LoadBalancerStrategy.select(instances)
      │      └── 随机负载均衡选择一个实例
      ├── 3. OkHttp POST → http://{ip}:{port}/nacos-rpc/invoke
      │      └── 请求体：RpcRequest（接口名、方法名、参数类型、参数值）
      └── 4. 解析 RpcResponse，返回结果或抛出异常
```

**关键类**：

| 类 | 职责 |
|---|---|
| `NacosRpcServicePostProcessor` | 扫描 `@Rpcwired` 字段，创建 JDK 动态代理注入 |
| `RpcInvocationHandler` | 代理拦截器，编排发现→负载均衡→HTTP调用→反序列化 |
| `NacosServiceDiscovery` | 从 Nacos 查询实例，支持版本过滤和服务变更订阅 |
| `LoadBalancerStrategy` | 随机负载均衡策略 |
| `RpcRequest` / `RpcResponse` | RPC 协议体（接口名、方法名、参数、结果） |

### 2.4 服务变更订阅

框架在注入代理时自动订阅 Nacos 服务变更事件，实例上下线时 Nacos 推送通知，下次调用自动获取最新实例列表，无需重启。

---

## 三、服务配置

### 3.1 application.properties

```properties
# ===== Nacos RPC 框架 =====

# 是否启用（默认 true）
nacos.rpc.enabled=true

# Nacos 注册中心
nacos.rpc.server-addr=127.0.0.1:8848
nacos.rpc.namespace=
nacos.rpc.group=DEFAULT_GROUP

# RPC 服务端（提供方）
nacos.rpc.server-host=0.0.0.0
nacos.rpc.server-port=19090

# 应用信息
nacos.rpc.app-name=my-service
nacos.rpc.app-version=1.0.0
nacos.rpc.env=dev

# Undertow 线程模型
nacos.rpc.io-threads=4
nacos.rpc.worker-threads=32
nacos.rpc.direct-buffers=true

# OkHttp 客户端超时（消费方）
nacos.rpc.connect-timeout=5000
nacos.rpc.read-timeout=10000
nacos.rpc.write-timeout=10000
```

### 3.2 配置属性参考

| 配置项 | 默认值 | 说明 |
|---|---|---|
| `nacos.rpc.enabled` | `true` | 是否启用 RPC 框架 |
| `nacos.rpc.server-addr` | `127.0.0.1:8848` | Nacos 服务地址 |
| `nacos.rpc.namespace` | 空 | Nacos 命名空间 |
| `nacos.rpc.group` | `DEFAULT_GROUP` | Nacos 分组 |
| `nacos.rpc.server-host` | `0.0.0.0` | RPC 服务监听主机 |
| `nacos.rpc.server-port` | `19090` | RPC 服务监听端口，`0` 表示随机端口 |
| `nacos.rpc.app-name` | `unknown` | 应用名称（注册到 Nacos 的元数据） |
| `nacos.rpc.app-version` | `1.0.0` | 应用版本（灰度路由依据） |
| `nacos.rpc.env` | `dev` | 环境标识 |
| `nacos.rpc.io-threads` | CPU 核心数 | Undertow IO 线程数 |
| `nacos.rpc.worker-threads` | IO线程数 x 8 | Undertow 工作线程数 |
| `nacos.rpc.direct-buffers` | `true` | 是否使用直接缓冲区 |
| `nacos.rpc.connect-timeout` | `5000` | OkHttp 连接超时（ms） |
| `nacos.rpc.read-timeout` | `10000` | OkHttp 读取超时（ms） |
| `nacos.rpc.write-timeout` | `10000` | OkHttp 写入超时（ms） |
| `nacos.rpc.metadata.*` | — | 附加元数据（注册到 Nacos 实例） |

### 3.3 自动装配

框架通过 `NacosRpcAutoConfiguration` 自动装配以下 Bean，`@ConditionalOnMissingBean` 允许覆盖：

| Bean 名称 | 类型 | 说明 |
|---|---|---|
| `nacosRpcObjectMapper` | `ObjectMapper` | Jackson 序列化 |
| `nacosServiceRegistry` | `NacosServiceRegistry` | Nacos 服务注册 |
| `nacosServiceDiscovery` | `NacosServiceDiscovery` | Nacos 服务发现 |
| `nacosRpcServicePostProcessor` | `NacosRpcServicePostProcessor` | 扫描 `@RpcService` / `@Rpcwired` |
| `nacosRpcServerBootstrap` | `NacosRpcServerBootstrap` | Undertow 服务端启动 |
| `nacosRpcLifecycle` | `NacosRpcLifecycle` | 生命周期管理（注册/注销） |

---

## 四、模块结构

```
ttd-xpro-inf/
├── rpc-api/                    ← 注解与常量（@RpcService, @Rpcwired, RpcConstant）
├── rpc-core/                   ← 核心实现
│   ├── autoconfigure/          ← 自动配置（NacosRpcAutoConfiguration, NacosRpcProperties）
│   ├── registry/               ← 服务注册（NacosServiceRegistry）
│   ├── discovery/              ← 服务发现（NacosServiceDiscovery, LoadBalancerStrategy）
│   ├── proxy/                  ← 代理（NacosRpcServicePostProcessor, RpcInvocationHandler）
│   ├── server/                 ← 服务端（NacosRpcServerBootstrap, RpcRequestHandler）
│   ├── lifecycle/              ← 生命周期（NacosRpcLifecycle）
│   ├── protocol/               ← 协议体（RpcRequest, RpcResponse）
│   └── util/                   ← 工具（AppUtils）
└── rpc-spring-boot-starter/    ← Spring Boot 自动装配入口
```
