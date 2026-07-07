package com.kt.ktedu.common.util.core;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtil {

    private MapUtil() {
        // 인스턴스화 방지
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static Object get(Map<?, ?> map, Object key) {
        return isEmpty(map) ? null : map.get(key);
    }

    public static String getString(Map<?, ?> map, Object key) {
        return getString(map, key, "");
    }

    public static String getString(Map<?, ?> map, Object key, String defaultValue) {
        Object value = get(map, key);
        return StringUtil.nvl(value, defaultValue);
    }

    public static int getInt(Map<?, ?> map, Object key) {
        return getInt(map, key, 0);
    }

    public static int getInt(Map<?, ?> map, Object key, int defaultValue) {
        return NumberUtil.toInt(get(map, key), defaultValue);
    }

    public static long getLong(Map<?, ?> map, Object key) {
        return getLong(map, key, 0L);
    }

    public static long getLong(Map<?, ?> map, Object key, long defaultValue) {
        return NumberUtil.toLong(get(map, key), defaultValue);
    }

    public static BigDecimal getBigDecimal(Map<?, ?> map, Object key) {
        return getBigDecimal(map, key, BigDecimal.ZERO);
    }

    public static BigDecimal getBigDecimal(Map<?, ?> map, Object key, BigDecimal defaultValue) {
        return NumberUtil.toBigDecimal(get(map, key), defaultValue);
    }

    public static boolean getBoolean(Map<?, ?> map, Object key) {
        return getBoolean(map, key, false);
    }

    public static boolean getBoolean(Map<?, ?> map, Object key, boolean defaultValue) {
        Object value = get(map, key);
        if (value == null) return defaultValue;
        if (value instanceof Boolean bool) return bool;

        String str = StringUtil.nvl(value).toLowerCase();
        if (StringUtil.isEmpty(str)) return defaultValue;

        return "true".equals(str) || "y".equals(str) || "yes".equals(str) || "1".equals(str);
    }

    public static void putIfNotBlank(Map<String, Object> map, String key, Object value) {
        if (map == null || StringUtil.isBlank(key) || StringUtil.isBlank(StringUtil.nvl(value))) return;
        map.put(key, value);
    }

    public static Map<String, String> toStringMap(Map<?, ?> map) {
        Map<String, String> result = new LinkedHashMap<>();
        if (isEmpty(map)) return result;

        map.forEach((key, value) -> result.put(StringUtil.nvl(key), StringUtil.nvl(value)));
        return result;
    }

    public static Map<String, Object> trimStringValues(Map<String, Object> map) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (isEmpty(map)) return result;

        map.forEach((key, value) -> {
            Object normalized = value instanceof String str ? StringUtil.trimToEmpty(str) : value;
            result.put(key, normalized);
        });

        return result;
    }
}
