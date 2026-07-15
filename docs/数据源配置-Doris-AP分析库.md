# Doris AP 分析库数据源

> 模块：`ttd-daf-starter` + `ttd-inf-daf`
> 自动配置类：`DorisApSlaveDataSourceAutoConfiguration`
> 数据源实现：`DruidDataSource`（连接池）
> 支持访问方式：MyBatis / JdbcTemplate

---

## 一、引入 POM 依赖

在业务项目 `pom.xml` 中添加：

```xml
<!-- 多数据源 Starter（含自动配置 + DataSourceFactoryBean + Druid + Nacos） -->
<dependency>
    <groupId>com.ly.ttd</groupId>
    <artifactId>ttd-daf-starter</artifactId>
    <version>2026.0.0-SNAPSHOT</version>
</dependency>
```

> `ttd-daf-starter` 已传递依赖 `ttd-inf-daf`（包含 `DataSourceFactoryBean`、`DruidDataSource`、`nacos-client`、`mysql-connector-j`），无需单独引入。
> Doris 使用 MySQL 协议（FE 端口 9030），同样依赖 `mysql-connector-j`。

如果需要 MyBatis 支持，还需：

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.4</version>
</dependency>
```

---

## 二、application.properties 开关配置

在项目的 `application.properties` 中添加以下开关：

```properties
# ===== Doris AP 数据源 =====

# [必填] 启用 Doris AP 数据源
ttd.load.doris.ap.enable=true

# [可选] 启用 MyBatis（SqlSessionFactory + MapperScanner）
ttd.load.doris.ap.mybatis.enable=true

# [可选] 启用 NamedParameterJdbcTemplate
ttd.load.doris.ap.jdbc.enable=true

# [可选] Nacos 连接参数（有默认值）
ttd.nacos.server-addr=127.0.0.1:8848
ttd.nacos.namespace=
```

| 配置项 | 默认值 | 说明 |
|---|---|---|
| `ttd.load.doris.ap.enable` | — | **必填**，设为 `true` 启用数据源 |
| `ttd.load.doris.ap.mybatis.enable` | — | 设为 `true` 启用 MyBatis |
| `ttd.load.doris.ap.jdbc.enable` | — | 设为 `true` 启用 JdbcTemplate |
| `ttd.nacos.server-addr` | `127.0.0.1:8848` | Nacos 服务地址 |
| `ttd.nacos.namespace` | 空 | Nacos 命名空间 |

---

## 三、Nacos 配置文件

在 Nacos 控制台创建配置：

| 参数 | 值 |
|---|---|
| **Data ID** | `db-doris-ap.properties` |
| **Group** | `DEFAULT_GROUP` |
| **配置格式** | Properties |

配置内容示例：

```properties
# ===== Doris AP Druid 数据源 =====

# [必填] 数据库连接（Doris FE MySQL 协议端口，默认 9030）
ttd.db.slave.url=jdbc:mysql://127.0.0.1:9030/doris_ap?useSSL=false&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
ttd.db.slave.username=root
ttd.db.slave.password=
ttd.db.slave.driver-class-name=com.mysql.cj.jdbc.Driver

# 连接池参数（Doris 分析场景建议较小池）
ttd.db.slave.initial-size=3
ttd.db.slave.min-idle=3
ttd.db.slave.max-active=10
ttd.db.slave.max-wait=60000
ttd.db.slave.time-between-eviction-runs-millis=60000
ttd.db.slave.min-evictable-idle-time-millis=300000

# 连接检测
ttd.db.slave.validation-query=SELECT 1
ttd.db.slave.test-while-idle=true
ttd.db.slave.test-on-borrow=false
ttd.db.slave.test-on-return=false

# Prepared Statement 池化（Doris 场景建议关闭）
ttd.db.slave.pool-prepared-statements=false

# Druid 过滤器（Doris 场景关闭 wall 防止误拦分析 SQL）
ttd.db.slave.filters=stat
```

> **注意**: Doris FE 默认监听 **9030** 端口（MySQL 协议），与 MySQL 默认 3306 不同，请根据实际部署修改 url 中的端口和库名。

### Nacos 配置属性参考

| 属性 | 必填 | 说明 |
|---|---|---|
| `ttd.db.slave.url` | 是 | JDBC 连接地址（Doris FE 端口 9030） |
| `ttd.db.slave.username` | 是 | Doris 用户名 |
| `ttd.db.slave.password` | 是 | Doris 密码（默认 root 无密码） |
| `ttd.db.slave.driver-class-name` | 否 | JDBC 驱动类名，默认由 Druid 自动检测 |
| `ttd.db.slave.initial-size` | 否 | 连接池初始连接数 |
| `ttd.db.slave.min-idle` | 否 | 最小空闲连接数 |
| `ttd.db.slave.max-active` | 否 | 最大活跃连接数 |
| `ttd.db.slave.max-wait` | 否 | 获取连接最大等待时间（ms） |
| `ttd.db.slave.time-between-eviction-runs-millis` | 否 | 空闲连接检测间隔（ms） |
| `ttd.db.slave.min-evictable-idle-time-millis` | 否 | 连接最小空闲时间（ms） |
| `ttd.db.slave.validation-query` | 否 | 连接验证 SQL |
| `ttd.db.slave.test-while-idle` | 否 | 空闲时检测连接有效性 |
| `ttd.db.slave.test-on-borrow` | 否 | 借用时检测连接有效性 |
| `ttd.db.slave.test-on-return` | 否 | 归还时检测连接有效性 |
| `ttd.db.slave.pool-prepared-statements` | 否 | 是否开启 PSCache |
| `ttd.db.slave.max-pool-prepared-statement-per-connection-size` | 否 | 每个连接最大 PSCache 数 |
| `ttd.db.slave.filters` | 否 | Druid 过滤器（Doris 建议只用 stat） |

---

## 四、Bean 清单

| Bean 名称 | 类型 | 激活条件 |
|---|---|---|
| `Doris.AP.DataSource` | `DruidDataSource` | `ttd.load.doris.ap.enable=true` |
| `Doris.AP.SqlSessionFactory` | `SqlSessionFactory` | 上述 + `ttd.load.doris.ap.mybatis.enable=true` |
| `Doris.AP.MapperScanner` | `MapperScannerConfigurer` | 依赖 `Doris.AP.SqlSessionFactory` 存在 |
| `Doris.AP.JdbcTemplate` | `NamedParameterJdbcTemplate` | 上述 + `ttd.load.doris.ap.jdbc.enable=true` |

---

## 五、MyBatis 使用

### 5.1 Mapper 接口包路径

```
com.ly.ttd.biz.**.doris.ap.mapper
```

### 5.2 Mapper XML 路径

```
classpath*:db/mybatis/doris/ap/mapper/*Mapper.xml
```

### 5.3 目录结构

```
src/main/resources/
└── db/
    └── mybatis/
        └── doris/
            └── ap/
                └── mapper/
                    └── OrderStatsMapper.xml
```

### 5.4 Mapper 接口示例

```java
package com.ly.ttd.biz.report.doris.ap.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderStatsMapper {

    List<Map<String, Object>> countByDate(String date);
}
```

对应的 XML（`db/mybatis/doris/ap/mapper/OrderStatsMapper.xml`）：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ly.ttd.biz.report.doris.ap.mapper.OrderStatsMapper">

    <select id="countByDate" resultType="java.util.Map">
        SELECT date, count(*) AS cnt
        FROM order_stats
        WHERE date = #{date}
        GROUP BY date
    </select>

</mapper>
```

---

## 六、JdbcTemplate 使用

```java
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class OrderStatsService {

    @Qualifier("Doris.AP.JdbcTemplate")
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> queryDailyStats(String date) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        return jdbcTemplate.queryForList(
            "SELECT date, count(*) AS cnt FROM order_stats WHERE date = :date GROUP BY date",
            params
        );
    }
}
```

---

## 七、配置加载链路

```
application.properties
  └── ttd.load.doris.ap.enable = true
      └── DataSourceFactoryBean(serverAddr, namespace, "db-doris-ap.properties", "DEFAULT_GROUP")
          └── Nacos 远程拉取 db-doris-ap.properties
              └── 提取 ttd.db.slave.* 前缀属性 → 去掉前缀
                  └── DruidDataSource(url, username, password, ...)
                      └── Bean: Doris.AP.DataSource
                          ├── SqlSessionFactory (classpath*:db/mybatis/doris/ap/mapper/*Mapper.xml)
                          └── NamedParameterJdbcTemplate
```
