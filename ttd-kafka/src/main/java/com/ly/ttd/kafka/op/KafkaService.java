package com.ly.ttd.kafka.op;

/**
 * Kafka操作服务
 * @author yong.li
 * @since 2026/3/11 16:13
 */
public interface KafkaService {

    /**
     * 创建主题Topic
     */
    void createTopic(KafkaTopicDef topicDef);


    /**
     * 发送消息
     */
    void sendMessage(String topic, String key, String message);


}
