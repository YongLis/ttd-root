package com.ly.ttd.kafka.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

/**
 * @author yong.li
 * @since 2026/3/13 11:13
 */
public class JsonDeserializer<T> implements Deserializer<T> {
    private Class<T> targetClass;
    @Override
    public T deserialize(String s, byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(bytes, targetClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
