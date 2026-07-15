# Kafka 消息队列

> 模块：`ttd-kafka`
> 配置类：`KafkaConfig`
> 客户端：`KafkaProducer` / `KafkaConsumer` / `AdminClient`
> 序列化：String（默认） / JSON / Kryo

---

## 一、引入 POM 依赖

在业务项目 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.ly.ttd</groupId>
    <artifactId>ttd-kafka</artifactId>
    <version>2026.0.0-SNAPSHOT</version>
</dependency>
```

> `ttd-kafka` 已传递依赖 `ttd-nacos-config`、`spring-kafka`、`kryo`、`jackson-databind`。

---

## 二、配置

### 2.1 application.properties 配置

配置可写在 `application.properties` 中，也可通过 Nacos 配置中心管理：

```properties
# Kafka 集群地址（必填）
spring.kafka.bootstrap.servers=127.0.0.1:9092

# Producer 配置（前缀 spring.kafka.producer.）
spring.kafka.producer.acks=all
spring.kafka.producer.retries=3
spring.kafka.producer.batch.size=16384
spring.kafka.producer.linger.ms=1

# Consumer 配置（前缀 spring.kafka.consumer.）
spring.kafka.consumer.group.id=my-group
spring.kafka.consumer.auto.offset.reset=latest
spring.kafka.consumer.enable.auto.commit=true
```

### 2.2 Nacos 配置中心管理

通过 `ttd-nacos-config` 模块，将 Kafka 配置放到 Nacos 配置文件中，实现动态管理。

`application.properties` 中启用 Nacos 配置加载：

```properties
ttd.nacos.server-addr=127.0.0.1:8848
ttd.nacos[0].namespace=
ttd.nacos[0].group=DEFAULT_GROUP
ttd.nacos[0].dataId=myapp.properties
```

Nacos 配置文件中写入 Kafka 相关配置即可（properties 格式）。

---

## 三、Bean 清单

| Bean 名称 | 类型 | 说明 |
|---|---|---|
| — | `AdminClient` | Kafka 管理客户端，用于创建 Topic |
| — | `KafkaProducer<String, String>` | 消息生产者，默认 String 序列化 |
| — | `KafkaConsumer<String, String>` | 消息消费者，默认 String 反序列化 |

---

## 四、KafkaService 使用

`KafkaService` 提供 Topic 创建和消息发送的封装：

```java
import com.ly.ttd.kafka.op.KafkaService;
import com.ly.ttd.kafka.op.KafkaTopicDef;

@Service
public class OrderService {

    @Resource
    private KafkaService kafkaService;

    public void createOrder(Order order) {
        // 创建 Topic
        KafkaTopicDef topicDef = new KafkaTopicDef();
        topicDef.setTopicName("order-topic");
        topicDef.setPartitionNum(3);
        topicDef.setReplicaNum((short) 1);
        kafkaService.createTopic(topicDef);

        // 发送消息
        kafkaService.sendMessage("order-topic", order.getId(), JSON.toJSONString(order));
    }
}
```

---

## 五、自定义序列化

模块内置了 JSON 和 Kryo 两种序列化器，可按需替换默认的 String 序列化：

| 序列化器 | 类 | 说明 |
|---|---|---|
| JSON | `JsonSerializer` / `JsonDeserializer` | 基于 Jackson，支持复杂对象 |
| Kryo | `KryoSerializer` / `KryoDeserializer` | 高性能二进制序列化 |

配置方式（Nacos 或 application.properties）：

```properties
# 使用 JSON 序列化
spring.kafka.producer.key.serializer=com.ly.ttd.kafka.json.JsonSerializer
spring.kafka.producer.value.serializer=com.ly.ttd.kafka.json.JsonSerializer

# 使用 Kryo 序列化
spring.kafka.producer.key.serializer=com.ly.ttd.kafka.kryo.KryoSerializer
spring.kafka.producer.value.serializer=com.ly.ttd.kafka.kryo.KryoSerializer
```
