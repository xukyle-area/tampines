package com.ganten.market.common.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectUtils {

    public static Map<String, String> toStringMap(Object obj) {
        Map<String, String> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                if (value == null) {
                    continue;
                } else {
                    map.put(field.getName(), value.toString());
                }
            } catch (IllegalAccessException e) {
            }
        }
        return map;
    }
}
