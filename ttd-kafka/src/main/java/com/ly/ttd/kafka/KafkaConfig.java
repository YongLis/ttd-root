package com.ly.ttd.kafka;

import com.ly.ttd.nacos.util.EnvPropertyUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Properties;

/**
 * Kafka 配置 — 从 Spring Environment（含 Nacos PropertySource）读取配置。
 * <p>
 * Nacos 配置在 bootstrap 阶段同步到 Environment，因此 @Bean 方法执行时已可用。
 *
 * @author yong.li
 * @since 2026/3/11 16:06
 */
@Configuration
@Slf4j
public class KafkaConfig {
    @Resource
    private ConfigurableEnvironment env;

    private static final String PRODUCER_PREFIX = "spring.kafka.producer.";
    private static final String CONSUMER_PREFIX = "spring.kafka.consumer.";

    @Bean
    public AdminClient buildKafkaClient() {
        String bootstrapServers = env.getProperty("spring.kafka.bootstrap.servers");
        if (bootstrapServers == null || bootstrapServers.isEmpty()) {
            throw new IllegalStateException("spring.kafka.bootstrap.servers is missing");
        }

        Properties properties = EnvPropertyUtil.collect(env, PRODUCER_PREFIX);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        log.info("Kafka producer config loaded, bootstrap={}", bootstrapServers);
        return KafkaAdminClient.create(properties);
    }


    @Bean
    public KafkaProducer<String, String> buildKafkaProducer() {
        String bootstrapServers = env.getProperty("spring.kafka.bootstrap.servers");
        if (bootstrapServers == null || bootstrapServers.isEmpty()) {
            throw new IllegalStateException("spring.kafka.bootstrap.servers is missing");
        }
        Properties properties = EnvPropertyUtil.collect(env, PRODUCER_PREFIX);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        log.info("Kafka producer config loaded, bootstrap={}", bootstrapServers);
        return new KafkaProducer<>(properties);
    }

    @Bean
    public KafkaConsumer<String, String> buildKafkaConsumer() {
        String bootstrapServers = env.getProperty("spring.kafka.bootstrap.servers");
        if (bootstrapServers == null || bootstrapServers.isEmpty()) {
            throw new IllegalStateException("spring.kafka.bootstrap.servers is missing");
        }

        Properties properties = EnvPropertyUtil.collect(env, CONSUMER_PREFIX);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        log.info("Kafka consumer config loaded, bootstrap={}", bootstrapServers);
        return new KafkaConsumer<>(properties);
    }
}
