package com.ly.ttd.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.deserializer.ParseProcess;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.helpers.MessageFormatter;

import java.util.*;

public abstract class TextUtils {
    private final static TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };
    private final static TypeReference<List<Object>> LIST_TYPE_REFERENCE = new TypeReference<>() {
    };

    public static String fmtMessage(String messageTemplate, Object... parameters) {
        if (parameters == null || parameters.length == 0) {
            return messageTemplate;
        } else {
            return MessageFormatter.arrayFormat(messageTemplate, parameters).getMessage();
        }
    }

    public static void checkTrue(
            boolean b, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!b) {
            throw new IllegalArgumentException(fmtMessage(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static Map<String, Object> toMapString(String JSONString) {
        if (StringUtils.isBlank(JSONString)) {
            return new HashMap<>();
        }
        return JSON.parseObject(JSONString, MAP_TYPE_REFERENCE.getType());
    }

    public static List<Object> toListString(String json) {
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        return JSON.parseObject(json, LIST_TYPE_REFERENCE.getType());
    }


    public static String emojiFilter(String source) {
        if (StringUtils.isNotBlank(source)) {
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");
        } else {
            return source;
        }
    }


    public static String toJSONString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
    }

    public static <T> T parseObject(String json, TypeReference<T> type) {
        return JSON.parseObject(json, type);
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> T parseObject(String json, Class<T> clazz, ParseProcess processor) {
        return JSON.parseObject(json, clazz, processor);
    }


    public static <T> T checkNotNull(
            T reference, String errorMessageTemplate, Object... errorMessageArgs) {
        if (reference == null) {
            throw new NullPointerException(fmtMessage(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }


    public static String checkNotBlank(String reference, String errorMessageTemplate, Object... errorMessageArgs) {
        if (StringUtils.isBlank(reference)) {
            throw new NullPointerException(fmtMessage(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }


    public static void checkNotEmpty(Collection<?> collection, String errorMessageTemplate, Object... errorMessageArgs) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new NullPointerException(fmtMessage(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkNotEmpty(Map<?, ?> map, String errorMessageTemplate, Object... errorMessageArgs) {
        if (MapUtils.isEmpty(map)) {
            throw new NullPointerException(fmtMessage(errorMessageTemplate, errorMessageArgs));
        }
    }


    public static Date parseDate(String date) {
        return parseDate(date, true);
    }

    public static Date parseDate(String date, boolean lenient) {
        try {
            String[] parsePatterns = {
                    "yyyyMMddHHmmss",
                    "yyyyMMddHHmmssSSS",
                    "yyyyMMdd",
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd",
                    "yyyy/MM/dd HH:mm:ss.SSS",
                    "yyyy/MM/dd HH:mm:ss",
                    "yyyy/MM/dd",
                    "yyyy年MM月dd日",
                    "yyyy年MM月dd日HH时mm分ss秒SSS毫秒",
                    "yyyy年MM月dd日HH时mm分ss秒",
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ"};

            return lenient ? DateUtils.parseDate(date, parsePatterns) :
                    DateUtils.parseDateStrictly(date, parsePatterns);
        } catch (Exception e) {
            throw new IllegalArgumentException(fmtMessage("parse date error {}", date));
        }
    }
}
