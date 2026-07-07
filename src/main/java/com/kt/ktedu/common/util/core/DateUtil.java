package com.kt.ktedu.common.util.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 날짜/시간 공통 유틸.
 *
 * <p>genius/lms 레거시의 {@code FormatDate}, {@code DateUtil}에서 반복되던 현재일자,
 * 날짜 포맷 변환, 날짜 가감, 기간 계산 기능을 Java Time API 기준으로 대체한다.</p>
 *
 * <pre>{@code
 * String today = DateUtil.getCurrentDate();              // 2026-07-07
 * String ymd = DateUtil.toYmd("2026-07-07");             // 20260707
 * String nextWeek = DateUtil.addDays("20260707", 7);     // 2026-07-14
 * boolean open = DateUtil.isBetween("20260707", "20260701", "20260731");
 * }</pre>
 *
 * <p>입력 문자열은 {@code yyyy-MM-dd}, {@code yyyyMMdd}, {@code yyyy/MM/dd},
 * {@code yyyy.MM.dd} 형식을 우선 지원한다. 실패 시 빈 문자열 또는 null을 반환한다.</p>
 */
public class DateUtil {

    public static final String PATTERN_YMD = "yyyyMMdd";
    public static final String PATTERN_YMD_DASH = "yyyy-MM-dd";
    public static final String PATTERN_YMDHMS = "yyyyMMddHHmmss";
    public static final String PATTERN_YMDHMS_DASH = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern(PATTERN_YMD);
    private static final DateTimeFormatter YMD_DASH = DateTimeFormatter.ofPattern(PATTERN_YMD_DASH);
    private static final DateTimeFormatter YMDHMS = DateTimeFormatter.ofPattern(PATTERN_YMDHMS);
    private static final DateTimeFormatter YMDHMS_DASH = DateTimeFormatter.ofPattern(PATTERN_YMDHMS_DASH);
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            YMD_DASH,
            YMD,
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd")
    );
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            YMDHMS_DASH,
            YMDHMS,
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
    );

    private DateUtil() {
        // 인스턴스화 방지
    }

    /**
     * 현재 날짜 구하기 (포맷: yyyy-MM-dd)
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(YMD_DASH);
    }

    /**
     * 현재 일시 구하기 (포맷: yyyy-MM-dd HH:mm:ss)
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(YMDHMS_DASH);
    }

    /**
     * 순수 현재날짜 (포맷: yyyyMMdd) -> 배치 파일명 등
     */
    public static String getCurrentDatePure() {
        return LocalDate.now().format(YMD);
    }

    /**
     * 현재 일시를 원하는 패턴으로 반환한다.
     *
     * <pre>{@code
     * DateUtil.now("yyyyMMddHHmmss");
     * }</pre>
     */
    public static String now(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 현재 날짜를 원하는 패턴으로 반환한다.
     */
    public static String today(String pattern) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 문자열을 LocalDate로 변환한다.
     *
     * <p>지원: yyyy-MM-dd, yyyyMMdd, yyyy/MM/dd, yyyy.MM.dd.</p>
     */
    public static LocalDate parseLocalDate(String value) {
        if (StringUtil.isEmpty(value)) return null;

        String date = StringUtil.nvl(value);
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException ignored) {
                // 다음 포맷으로 재시도
            }
        }

        String digits = StringUtil.onlyDigits(date);
        if (digits.length() == 8) {
            try {
                return LocalDate.parse(digits, YMD);
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }

        return null;
    }

    /**
     * 문자열을 LocalDateTime으로 변환한다.
     *
     * <p>날짜만 들어오면 00:00:00으로 보정한다.</p>
     */
    public static LocalDateTime parseLocalDateTime(String value) {
        if (StringUtil.isEmpty(value)) return null;

        String dateTime = StringUtil.nvl(value);
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateTime, formatter);
            } catch (DateTimeParseException ignored) {
                // 다음 포맷으로 재시도
            }
        }

        String digits = StringUtil.onlyDigits(dateTime);
        if (digits.length() == 14) {
            try {
                return LocalDateTime.parse(digits, YMDHMS);
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }

        LocalDate date = parseLocalDate(dateTime);
        return date == null ? null : date.atStartOfDay();
    }

    /**
     * LocalDate를 yyyy-MM-dd로 변환한다.
     */
    public static String format(LocalDate date) {
        return format(date, PATTERN_YMD_DASH);
    }

    /**
     * LocalDate를 지정 패턴으로 변환한다.
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * LocalDateTime을 yyyy-MM-dd HH:mm:ss로 변환한다.
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, PATTERN_YMDHMS_DASH);
    }

    /**
     * LocalDateTime을 지정 패턴으로 변환한다.
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 날짜 문자열을 yyyy-MM-dd로 변환한다.
     */
    public static String format(String value) {
        return format(value, PATTERN_YMD_DASH);
    }

    /**
     * 날짜/일시 문자열을 지정 패턴으로 변환한다.
     */
    public static String format(String value, String pattern) {
        LocalDateTime dateTime = parseLocalDateTime(value);
        if (dateTime == null) return "";

        boolean timePattern = pattern.contains("H") || pattern.contains("m") || pattern.contains("s");
        return timePattern ? format(dateTime, pattern) : format(dateTime.toLocalDate(), pattern);
    }

    /**
     * 날짜 문자열을 yyyyMMdd로 변환한다.
     */
    public static String toYmd(String value) {
        return format(value, PATTERN_YMD);
    }

    /**
     * 날짜 문자열을 yyyy-MM-dd로 변환한다.
     */
    public static String toYmdDash(String value) {
        return format(value, PATTERN_YMD_DASH);
    }

    /**
     * 일시 문자열을 yyyyMMddHHmmss로 변환한다.
     */
    public static String toYmdhms(String value) {
        return format(value, PATTERN_YMDHMS);
    }

    /**
     * 일시 문자열을 yyyy-MM-dd HH:mm:ss로 변환한다.
     */
    public static String toYmdhmsDash(String value) {
        return format(value, PATTERN_YMDHMS_DASH);
    }

    /**
     * 기준 날짜에 n일(또는 n개월) 더하거나 빼기
     *
     * @param targetDate 기준날짜 (yyyy-MM-dd)
     * @param days       더할 일수 (음수 입력 시 뺄셈으로 작동)
     */
    public static String addDays(String targetDate, long days) {
        LocalDate date = parseLocalDate(targetDate);
        return date == null ? "" : date.plusDays(days).format(YMD_DASH);
    }

    /**
     * 기준 날짜에 월을 더하거나 뺀다. 결과는 yyyy-MM-dd.
     */
    public static String addMonths(String targetDate, long months) {
        LocalDate date = parseLocalDate(targetDate);
        return date == null ? "" : date.plusMonths(months).format(YMD_DASH);
    }

    /**
     * 기준 날짜에 연도를 더하거나 뺀다. 결과는 yyyy-MM-dd.
     */
    public static String addYears(String targetDate, long years) {
        LocalDate date = parseLocalDate(targetDate);
        return date == null ? "" : date.plusYears(years).format(YMD_DASH);
    }

    /**
     * 두 날짜 사이의 일수 차이 계산
     *
     * @param startDate 시작일 (yyyy-MM-dd)
     * @param endDate   종료일 (yyyy-MM-dd)
     */
    public static long getDaysBetween(String startDate, String endDate) {
        LocalDate start = parseLocalDate(startDate);
        LocalDate end = parseLocalDate(endDate);
        if (start == null || end == null) return 0;
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * {@link #getDaysBetween(String, String)}과 동일한 의미의 alias.
     */
    public static long daysBetween(String startDate, String endDate) {
        return getDaysBetween(startDate, endDate);
    }

    /**
     * targetDate가 startDate와 endDate 사이인지 확인한다. 시작일/종료일 포함.
     */
    public static boolean isBetween(String targetDate, String startDate, String endDate) {
        LocalDate target = parseLocalDate(targetDate);
        LocalDate start = parseLocalDate(startDate);
        LocalDate end = parseLocalDate(endDate);
        if (target == null || start == null || end == null) return false;

        return !target.isBefore(start) && !target.isAfter(end);
    }

    /**
     * 날짜 문자열을 해당 일자의 시작 시각(yyyy-MM-dd 00:00:00)으로 변환한다.
     */
    public static String startOfDay(String value) {
        LocalDate date = parseLocalDate(value);
        return date == null ? "" : date.atStartOfDay().format(YMDHMS_DASH);
    }

    /**
     * 날짜 문자열을 해당 일자의 종료 시각(yyyy-MM-dd 23:59:59)으로 변환한다.
     */
    public static String endOfDay(String value) {
        LocalDate date = parseLocalDate(value);
        return date == null ? "" : LocalDateTime.of(date, LocalTime.MAX.withNano(0)).format(YMDHMS_DASH);
    }
}
