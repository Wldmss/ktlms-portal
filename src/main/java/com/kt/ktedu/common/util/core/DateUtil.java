package com.kt.ktedu.common.util.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    // 가장 표준으로 쓰는 날짜 포맷 사전 정의 (Thread-Safe)
    private static final DateTimeFormatter YMD_DASH = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter YMDHMS_DASH = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter YMD_PURE = DateTimeFormatter.ofPattern("yyyyMMdd");

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
        return LocalDate.now().format(YMD_PURE);
    }

    /**
     * 기준 날짜에 n일(또는 n개월) 더하거나 빼기
     *
     * @param targetDate 기준날짜 (yyyy-MM-dd)
     * @param days       더할 일수 (음수 입력 시 뺄셈으로 작동)
     */
    public static String addDays(String targetDate, long days) {
        if (StringUtil.isEmpty(targetDate)) return "";
        LocalDate date = LocalDate.parse(targetDate, YMD_DASH);
        return date.plusDays(days).format(YMD_DASH);
    }

    public static String addMonths(String targetDate, long months) {
        if (StringUtil.isEmpty(targetDate)) return "";
        LocalDate date = LocalDate.parse(targetDate, YMD_DASH);
        return date.plusMonths(months).format(YMD_DASH);
    }

    /**
     * 두 날짜 사이의 일수 차이 계산
     *
     * @param startDate 시작일 (yyyy-MM-dd)
     * @param endDate   종료일 (yyyy-MM-dd)
     */
    public static long getDaysBetween(String startDate, String endDate) {
        if (StringUtil.isEmpty(startDate) || StringUtil.isEmpty(endDate)) return 0;
        LocalDate start = LocalDate.parse(startDate, YMD_DASH);
        LocalDate end = LocalDate.parse(endDate, YMD_DASH);
        return ChronoUnit.DAYS.between(start, end);
    }
}