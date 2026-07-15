package com.ly.ttd.utils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ObjectUtils {

    private ObjectUtils() {
    }

    public static Object asView(Object o) {
        if (o == null) {
            return null;
        }

        if (o instanceof Object[] array) {
            return Arrays.stream(array).map(ObjectUtils::asView).toList();
        }

        if (o instanceof Collection<?> collection) {
            return collection.stream().map(ObjectUtils::asView).toList();
        }

        if (o instanceof Unmodifiable) {
            return o;
        }

        if (o instanceof Map<?, ?> map) {
            return Collections.unmodifiableMap(
                    map.entrySet().stream()
                            .collect(LinkedHashMap::new,
                                    (m, e) -> m.put(e.getKey(), asView(e.getValue())),
                                    LinkedHashMap::putAll)
            );
        }
        return o;
    }

    public static Object deepClone(Object o) {
        if (o == null) {
            return null;
        }

        if (o instanceof Object[] array) {
            return Arrays.stream(array).map(ObjectUtils::deepClone).collect(Collectors.toCollection(ArrayList::new));
        }

        if (o instanceof Collection<?> collection) {
            return collection.stream().map(ObjectUtils::deepClone).collect(Collectors.toCollection(ArrayList::new));
        }

        if (o instanceof HashMap<?, ?> map) {
            return map.entrySet().stream()
                    .collect(LinkedHashMap::new,
                            (m, e) -> m.put(e.getKey(), deepClone(e.getValue())),
                            LinkedHashMap::putAll);
        }
        return o;
    }

    public static Object copy(Object o, boolean w) {
        return w ? deepClone(o) : asView(o);
    }

}
