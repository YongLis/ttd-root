package com.ly.ttd.kafka.op;

/**
 * Kafka操作服务
 * @author yong.li
 * @since 2026/3/11 16:13
 */
public interface KafkaAdminOpService {

    /**
     * 创建主题Topic
     */
    public void createTopic(KafkaTopicDef topicDef);


}
