package com.ly.ttd.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.smile.SmileFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class JacksonSerializer implements Serializer {

    private final ObjectMapper objectMapper;

    public JacksonSerializer() {
        this(false);
    }

    public JacksonSerializer(boolean pretty) {
        this(pretty, false);
    }

    public JacksonSerializer(boolean pretty, boolean includeNull) {
        this(false, pretty, includeNull, "GMT+8");
    }

    public JacksonSerializer(boolean jacksonSmile, boolean pretty, boolean includeNull, String timeZone) {
        this("yyyy-MM-dd HH:mm:ss", jacksonSmile, pretty, includeNull, timeZone);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public JacksonSerializer(String datePattern, boolean jacksonSmile, boolean pretty, boolean includeNull, String timeZone) {
        super();
        MapperBuilder builder;

        if (jacksonSmile) {
            // SmileFactory 不是 JsonFactory 子类，通过 ObjectMapper.rebuild() 配置
            builder = new ObjectMapper(new SmileFactory()).rebuild();
        } else {
            builder = JsonMapper.builder();
        }

        builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        builder.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // 序列化时忽略 null 字段
        if (!includeNull) {
            builder.changeDefaultPropertyInclusion(v -> JsonInclude.Include.NON_NULL);
        }

        builder.defaultTimeZone(TimeZone.getTimeZone(timeZone));
        builder.defaultDateFormat(new SimpleDateFormat(datePattern));

        // 美化输出
        if (pretty) {
            builder.enable(SerializationFeature.INDENT_OUTPUT);
        }

        this.objectMapper = (ObjectMapper) builder.build();
    }

    public String serializeAsString(Object bean) {
        try {
            return objectMapper.writeValueAsString(bean);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public byte[] serializeAsBytes(Object bean) {
        try {
            return objectMapper.writeValueAsBytes(bean);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public <T> T deserialize(Class<T> clazz, String serializeString) {
        try {
            return objectMapper.readValue(serializeString, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public <T> T deserialize(Class<T> clazz, byte[] serializeBytes) {
        try {
            return objectMapper.readValue(serializeBytes, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    @Override
    public <T> T deserialize(tools.jackson.core.type.TypeReference<T> typeReference, String serializeString) {
        try {
            return objectMapper.readValue(serializeString, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public JsonNode deserialize(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("Date", new Date());
        map.put("name", "Aw哦是@🎁");
        map.put("age", 18);

        System.out.println(Serializer.INSTANCE.serializeAsString(map));

    }
}
