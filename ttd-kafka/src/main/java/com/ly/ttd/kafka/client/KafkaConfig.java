package com.ly.ttd.kafka.client;

import com.ly.ttd.config.nacos.NacosConfig;
import com.ly.ttd.config.nacos.client.NacosConfigClient;
import com.ly.ttd.config.nacos.consts.DefaultNacosConfigEnum;
import com.ly.ttd.kafka.json.JsonDeserializer;
import jakarta.annotation.Resource;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author yong.li
 * @since 2026/3/11 16:06
 */

@Component
public class KafkaConfig {
    private Logger logger = LoggerFactory.getLogger(KafkaConfig.class);


    @Resource
    private NacosConfigClient nacosConfigClient;
    private NacosConfig nacosConfig;

    public KafkaConfig(NacosConfig nacosConfig){
        this.nacosConfig = nacosConfig;
        logger.info("KafkaConfig init, form source namespace={}, group={}, dataId={}", nacosConfig.getNameSpaceId(),
                nacosConfig.getAppName(),
                DefaultNacosConfigEnum.KAFKA.getDataId());
       }
    /**
     * 创建kafka操作客户端
     */
    @Bean
    public AdminClient buildAdminClient() throws Exception {
        String serverName = nacosConfig.getAppName();
        Properties properties = nacosConfigClient.getRemoteConfig(nacosConfig.getNameSpaceId(), serverName
                , DefaultNacosConfigEnum.KAFKA.getDataId());
        return AdminClient.create(properties);

    }

    /**
     * 创建kafka生产者
     */
    @Bean
    public KafkaProducer<String, String> buildKafkaProducer() throws Exception {
        String serverName = nacosConfig.getAppName();
        Properties properties = nacosConfigClient.getRemoteConfig(nacosConfig.getNameSpaceId(),serverName,
                DefaultNacosConfigEnum.KAFKA.getDataId());
        return new KafkaProducer<>(properties);
    }

    @Bean
    public KafkaConsumer<String, String> buildKafkaConsumer() throws Exception {
        String serverName = nacosConfig.getAppName();
        Properties properties = nacosConfigClient.getRemoteConfig(nacosConfig.getNameSpaceId(),
                serverName, DefaultNacosConfigEnum.KAFKA.getDataId());
        return new KafkaConsumer<>(properties);
    }


}
