package com.ly.ttd.kafka.op.impl;

import com.ly.ttd.kafka.op.KafkaService;
import com.ly.ttd.kafka.op.KafkaTopicDef;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author yong.li
 * @since 2026/3/11 16:16
 */
@Service
@Slf4j
public class KafkaServiceImpl implements KafkaService {
    @Resource
    private AdminClient adminClient;
    @Resource
    private KafkaProducer producer;


    @Override
    public void createTopic(KafkaTopicDef topicDef) {
        if (null != adminClient) {
            NewTopic topic = new NewTopic(topicDef.getTopicName(), topicDef.getPartitionNum(), topicDef.getReplicaNum());
            try {
                adminClient.createTopics(Collections.singleton(topic))
                        .all().get();

                ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
                ListTopicsResult result = adminClient.listTopics(listTopicsOptions);
                result.names().get().forEach(System.out::println);

            } catch (Exception e) {
                log.error("create topic {} failed", topicDef.getTopicName(), e);
            }
        }
    }
    @Override
    public void sendMessage(String topic, String key, String message) {
        try {
            ProducerRecord record = new ProducerRecord(topic, key, message);
            producer.send(record);
        }catch (Exception e){
            log.error("send kafka message error, topic={}, message={}", topic, message);
        }



    }

}
