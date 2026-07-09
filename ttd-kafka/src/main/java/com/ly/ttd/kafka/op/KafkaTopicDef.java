package com.ly.ttd.kafka.op;

import lombok.Data;

/**
 * @author yong.li
 * @since 2026/3/11 16:14
 */
@Data
public class KafkaTopicDef {
    private int partitionNum = 1;
    private short replicaNum = 1;
    private String topicName;
}
