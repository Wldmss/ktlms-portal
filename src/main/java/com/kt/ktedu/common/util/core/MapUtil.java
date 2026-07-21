package com.kt.ktedu.common.util.core;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Map 값 안전 조회/가공 공통 유틸.
 * map 이 null 이거나 key 가 없어도 NPE 없이 기본값을 반환한다.
 * (MyBatis 조회 결과 Map, 파라미터 Map 처리용)
 */
public class MapUtil {

    private MapUtil() {
        // 인스턴스화 방지
    }

    /**
     * map 이 null 이거나 비어있으면 true
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * map 에 값이 하나라도 있으면 true
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 값 조회. map 이 null 이어도 안전 (없으면 null)
     */
    public static Object get(Map<?, ?> map, Object key) {
        return isEmpty(map) ? null : map.get(key);
    }

    /**
     * 문자열로 조회. 없거나 null 이면 ""
     */
    public static String getString(Map<?, ?> map, Object key) {
        return getString(map, key, "");
    }

    /**
     * 문자열로 조회. 없거나 null 이면 defaultValue
     */
    public static String getString(Map<?, ?> map, Object key, String defaultValue) {
        Object value = get(map, key);
        return StringUtil.nvl(value, defaultValue);
    }

    /**
     * int 로 조회. 없거나 변환 불가면 0 (콤마 문자열 "1,234" 허용)
     */
    public static int getInt(Map<?, ?> map, Object key) {
        return getInt(map, key, 0);
    }

    /**
     * int 로 조회. 없거나 변환 불가면 defaultValue
     */
    public static int getInt(Map<?, ?> map, Object key, int defaultValue) {
        return NumberUtil.toInt(get(map, key), defaultValue);
    }

    /**
     * long 으로 조회. 없거나 변환 불가면 0
     */
    public static long getLong(Map<?, ?> map, Object key) {
        return getLong(map, key, 0L);
    }

    /**
     * long 으로 조회. 없거나 변환 불가면 defaultValue
     */
    public static long getLong(Map<?, ?> map, Object key, long defaultValue) {
        return NumberUtil.toLong(get(map, key), defaultValue);
    }

    /**
     * BigDecimal 로 조회. 없거나 변환 불가면 BigDecimal.ZERO
     */
    public static BigDecimal getBigDecimal(Map<?, ?> map, Object key) {
        return getBigDecimal(map, key, BigDecimal.ZERO);
    }

    /**
     * BigDecimal 로 조회. 없거나 변환 불가면 defaultValue
     */
    public static BigDecimal getBigDecimal(Map<?, ?> map, Object key, BigDecimal defaultValue) {
        return NumberUtil.toBigDecimal(get(map, key), defaultValue);
    }

    /**
     * boolean 으로 조회. 없으면 false
     */
    public static boolean getBoolean(Map<?, ?> map, Object key) {
        return getBoolean(map, key, false);
    }

    /**
     * boolean 으로 조회. "true"/"y"/"yes"/"1"(대소문자 무관) 이면 true, 없거나 그 외 해석불가면 defaultValue
     * <pre>getBoolean(map, "useYn")  — DB 의 Y/N 플래그 읽기에 사용</pre>
     */
    public static boolean getBoolean(Map<?, ?> map, Object key, boolean defaultValue) {
        Object value = get(map, key);
        if (value == null) return defaultValue;
        if (value instanceof Boolean bool) return bool;

        String str = StringUtil.nvl(value).toLowerCase();
        if (StringUtil.isEmpty(str)) return defaultValue;

        return "true".equals(str) || "y".equals(str) || "yes".equals(str) || "1".equals(str);
    }

    /**
     * 여러 key 중 첫 번째 non-blank 값을 문자열로 반환한다. (정확한 key 매칭, 값은 trim)
     * 모두 없거나 blank({@link StringUtil#isBlankParam}) 면 {@code null}.
     *
     * <p>파라미터 별칭 우선순위 처리에 사용한다 — 예: {@code firstNonBlank(param, "userid", "userId", "s_userid")}.</p>
     */
    public static String firstNonBlank(Map<String, Object> map, String... keys) {
        if (isEmpty(map) || keys == null) return null;
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null && !StringUtil.isBlankParam(String.valueOf(value))) {
                return String.valueOf(value).trim();
            }
        }
        return null;
    }

    /**
     * 여러 key 를 <b>대소문자 무시</b>로 조회해 첫 번째로 존재하는 key 의 값을 문자열로 반환한다.
     * 매칭되는 key 가 없으면 {@code null}(매칭된 key 의 값이 null 이어도 {@code null}).
     *
     * <p>DB 컬럼 별칭의 대소문자 편차(userid/USERID)를 흡수할 때 사용한다.
     * 정확한 key 매칭이면 {@link #getString(Map, Object)} 를 쓰는 게 낫다.</p>
     */
    public static String valueAsString(Map<String, Object> map, String... keys) {
        if (isEmpty(map) || keys == null) return null;
        for (String key : keys) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)) {
                    Object value = entry.getValue();
                    return value == null ? null : String.valueOf(value);
                }
            }
        }
        return null;
    }

    /**
     * 값이 공백이 아닐 때만 put (검색조건 Map 조립 시 빈 파라미터 제외용)
     * <pre>putIfNotBlank(param, "keyword", keyword); — keyword 가 null/"" 이면 넣지 않음</pre>
     */
    public static void putIfNotBlank(Map<String, Object> map, String key, Object value) {
        if (map == null || StringUtil.isBlank(key) || StringUtil.isBlank(StringUtil.nvl(value))) return;
        map.put(key, value);
    }

    /**
     * 모든 key/value 를 문자열로 변환한 새 Map 반환 (순서 유지, null 값은 "")
     */
    public static Map<String, String> toStringMap(Map<?, ?> map) {
        Map<String, String> result = new LinkedHashMap<>();
        if (isEmpty(map)) return result;

        map.forEach((key, value) -> result.put(StringUtil.nvl(key), StringUtil.nvl(value)));
        return result;
    }

    /**
     * String 값만 trim 한 새 Map 반환 (그 외 타입 값은 그대로, 순서 유지)
     * — 폼 입력값 앞뒤 공백 일괄 제거용
     */
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
