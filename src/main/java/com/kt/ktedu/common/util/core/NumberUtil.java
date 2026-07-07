package com.kt.ktedu.common.util.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtil {

    private NumberUtil() {
        // 인스턴스화 방지
    }

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

    public static int toInt(Object value) {
        return toInt(value, 0);
    }

    public static int toInt(Object value, int defaultValue) {
        BigDecimal number = toBigDecimal(value, null);
        return number == null ? defaultValue : number.intValue();
    }

    public static long toLong(Object value) {
        return toLong(value, 0L);
    }

    public static long toLong(Object value, long defaultValue) {
        BigDecimal number = toBigDecimal(value, null);
        return number == null ? defaultValue : number.longValue();
    }

    public static double toDouble(Object value) {
        return toDouble(value, 0D);
    }

    public static double toDouble(Object value, double defaultValue) {
        BigDecimal number = toBigDecimal(value, null);
        return number == null ? defaultValue : number.doubleValue();
    }

    public static BigDecimal toBigDecimal(Object value) {
        return toBigDecimal(value, BigDecimal.ZERO);
    }

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

    public static String comma(Object value) {
        return format(value, "#,###");
    }

    public static String format(Object value, String pattern) {
        BigDecimal number = toBigDecimal(value, null);
        if (number == null) return "0";

        return new DecimalFormat(pattern).format(number);
    }

    public static BigDecimal scale(Object value, int scale) {
        return scale(value, scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal scale(Object value, int scale, RoundingMode roundingMode) {
        return toBigDecimal(value).setScale(scale, roundingMode);
    }

    public static String percent(Object numerator, Object denominator, int scale) {
        BigDecimal bottom = toBigDecimal(denominator, BigDecimal.ZERO);
        if (BigDecimal.ZERO.compareTo(bottom) == 0) return "0";

        BigDecimal top = toBigDecimal(numerator, BigDecimal.ZERO);
        BigDecimal percent = top.multiply(BigDecimal.valueOf(100))
                .divide(bottom, scale, RoundingMode.HALF_UP);

        return percent.stripTrailingZeros().toPlainString();
    }

    private static String normalize(Object value) {
        return StringUtil.nvl(value).replace(",", "");
    }
}
