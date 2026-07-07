package com.kt.ktedu.common.util.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 숫자 변환/포맷 공통 유틸.
 * 모든 메서드는 Object 를 받아 null/빈문자열/콤마포함 문자열("1,234")을 안전하게 처리한다.
 */
public class NumberUtil {

    private NumberUtil() {
        // 인스턴스화 방지
    }

    /**
     * 숫자로 해석 가능한 값인지 확인 (콤마 포함 문자열 허용, null/빈값은 false)
     * <pre>isNumeric("1,234.5") → true / isNumeric("abc") → false</pre>
     */
    public static boolean isNumeric(Object value) {
        if (value == null) return false;
        String str = normalize(value);
        if (StringUtil.isEmpty(str)) return false;

        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * int 변환. 변환 불가/null 이면 0
     */
    public static int toInt(Object value) {
        return toInt(value, 0);
    }

    /**
     * int 변환. 변환 불가/null 이면 defaultValue (소수점은 버림)
     * <pre>toInt("1,234", 0) → 1234 / toInt(null, -1) → -1</pre>
     */
    public static int toInt(Object value, int defaultValue) {
        BigDecimal number = toBigDecimal(value, null);
        return number == null ? defaultValue : number.intValue();
    }

    /**
     * long 변환. 변환 불가/null 이면 0
     */
    public static long toLong(Object value) {
        return toLong(value, 0L);
    }

    /**
     * long 변환. 변환 불가/null 이면 defaultValue (소수점은 버림)
     */
    public static long toLong(Object value, long defaultValue) {
        BigDecimal number = toBigDecimal(value, null);
        return number == null ? defaultValue : number.longValue();
    }

    /**
     * double 변환. 변환 불가/null 이면 0
     */
    public static double toDouble(Object value) {
        return toDouble(value, 0D);
    }

    /**
     * double 변환. 변환 불가/null 이면 defaultValue
     */
    public static double toDouble(Object value, double defaultValue) {
        BigDecimal number = toBigDecimal(value, null);
        return number == null ? defaultValue : number.doubleValue();
    }

    /**
     * BigDecimal 변환. 변환 불가/null 이면 BigDecimal.ZERO
     */
    public static BigDecimal toBigDecimal(Object value) {
        return toBigDecimal(value, BigDecimal.ZERO);
    }

    /**
     * BigDecimal 변환. 변환 불가/null 이면 defaultValue.
     * Number 계열/숫자 문자열(콤마 포함) 모두 허용 — 다른 to* 메서드들의 공통 기반.
     */
    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
        if (value == null) return defaultValue;

        if (value instanceof BigDecimal decimal) {
            return decimal;
        }

        if (value instanceof Number number) {
            try {
                return BigDecimal.valueOf(number.doubleValue());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        String str = normalize(value);
        if (StringUtil.isEmpty(str)) return defaultValue;

        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 세 자리마다 콤마 포맷. 변환 불가 시 "0"
     * <pre>comma(1234567) → "1,234,567"</pre>
     */
    public static String comma(Object value) {
        return format(value, "#,###");
    }

    /**
     * DecimalFormat 패턴 포맷. 변환 불가 시 "0"
     * <pre>format(1234.5, "#,##0.00") → "1,234.50"</pre>
     */
    public static String format(Object value, String pattern) {
        BigDecimal number = toBigDecimal(value, null);
        if (number == null) return "0";

        return new DecimalFormat(pattern).format(number);
    }

    /**
     * 소수점 scale 자리로 반올림(HALF_UP)
     * <pre>scale(3.14159, 2) → 3.14</pre>
     */
    public static BigDecimal scale(Object value, int scale) {
        return scale(value, scale, RoundingMode.HALF_UP);
    }

    /**
     * 소수점 scale 자리로 지정한 방식(roundingMode)으로 처리
     */
    public static BigDecimal scale(Object value, int scale, RoundingMode roundingMode) {
        return toBigDecimal(value).setScale(scale, roundingMode);
    }

    /**
     * 백분율 계산 (분자/분모 × 100, HALF_UP). 분모가 0/변환불가면 "0", 끝자리 0 은 제거
     * <pre>percent(30, 200, 1) → "15" / percent(1, 3, 2) → "33.33"</pre>
     */
    public static String percent(Object numerator, Object denominator, int scale) {
        BigDecimal bottom = toBigDecimal(denominator, BigDecimal.ZERO);
        if (BigDecimal.ZERO.compareTo(bottom) == 0) return "0";

        BigDecimal top = toBigDecimal(numerator, BigDecimal.ZERO);
        BigDecimal percent = top.multiply(BigDecimal.valueOf(100))
                .divide(bottom, scale, RoundingMode.HALF_UP);

        return percent.stripTrailingZeros().toPlainString();
    }

    /**
     * 문자열 변환 + 콤마 제거 (내부 공통 전처리)
     */
    private static String normalize(Object value) {
        return StringUtil.nvl(value).replace(",", "");
    }
}
