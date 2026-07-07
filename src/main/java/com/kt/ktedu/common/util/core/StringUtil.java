package com.kt.ktedu.common.util.core;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 문자열 공통 유틸.
 *
 * <p>genius/lms 레거시의 {@code StringManager}, {@code Utils}, {@code SharedMethods.NullCheck}
 * 계열 중 화면/서비스에서 반복되는 null 처리, 공백 확인, 문자열 자르기, padding, 마스킹 기능을 대체한다.</p>
 *
 * <pre>{@code
 * String userNm = StringUtil.nvl(param.get("userNm"));
 * boolean hasKeyword = StringUtil.isNotBlank(keyword);
 * String paddedNo = StringUtil.leftPad(seq, 5, "0");
 * String maskedName = StringUtil.maskName("홍길동"); // 홍*동
 * }</pre>
 *
 * <p>주의: SQL escape 목적의 {@code makeSQL}, {@code SqlChk}류 기능은 만들지 않는다.
 * SQL 값 처리는 MyBatis 바인딩 또는 명시적 whitelist 검증으로 처리한다.</p>
 */
public class StringUtil {

    private StringUtil() {
        // 인스턴스화 방지
    }

    /**
     * 문자열이 비어있거나 공백만 있는지 확인
     *
     * <p>{@code null}, {@code ""}, {@code "   "} 모두 true.</p>
     */
    public static boolean isEmpty(String str) {
        return StringUtils.isBlank(str);
    }

    /**
     * 값이 실제 문자열을 가지고 있는지 확인한다.
     *
     * <p>{@code null}, {@code ""}, {@code "   "} 모두 false.</p>
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotBlank(str);
    }

    /**
     * {@link #isEmpty(String)}와 동일한 의미의 alias.
     */
    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    /**
     * {@link #isNotEmpty(String)}와 동일한 의미의 alias.
     */
    public static boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    /**
     * Null이 들어오면 빈 값("")으로 변환, 값이 있으면 trim 처리
     *
     * <pre>{@code
     * StringUtil.nvl(null);      // ""
     * StringUtil.nvl("  a  ");   // "a"
     * }</pre>
     */
    public static String nvl(String str) {
        return StringUtils.defaultString(str).trim();
    }

    /**
     * Null이 들어오면 지정한 기본값(defaultStr)으로 대체
     *
     * <pre>{@code
     * StringUtil.nvl(null, "N");  // "N"
     * StringUtil.nvl("", "N");    // "N"
     * }</pre>
     */
    public static String nvl(String str, String defaultStr) {
        return StringUtils.defaultString(StringUtils.defaultIfBlank(str, defaultStr)).trim();
    }

    /**
     * Object 값을 문자열로 변환하고 trim 한다. Map/Request 파라미터 값 처리에 사용한다.
     */
    public static String nvl(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    /**
     * Object 값이 null/blank이면 기본값을 반환한다.
     */
    public static String nvl(Object value, String defaultStr) {
        return isBlank(nvl(value)) ? nvl(defaultStr) : nvl(value);
    }

    /**
     * {@link #nvl(String)}과 동일한 의미의 alias.
     */
    public static String defaultString(String str) {
        return nvl(str);
    }

    /**
     * {@link #nvl(String, String)}과 동일한 의미의 alias.
     */
    public static String defaultString(String str, String defaultStr) {
        return nvl(str, defaultStr);
    }

    /**
     * trim 후 null이면 빈 문자열로 반환한다.
     */
    public static String trimToEmpty(String str) {
        return StringUtils.trimToEmpty(str);
    }

    /**
     * trim 후 빈 문자열이면 null로 반환한다. DB optional 조건 분기에 사용하기 좋다.
     */
    public static String trimToNull(String str) {
        return StringUtils.trimToNull(str);
    }

    /**
     * null-safe 문자열 비교.
     */
    public static boolean equals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    /**
     * null-safe 대소문자 무시 문자열 비교.
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) return str2 == null;
        return str1.equalsIgnoreCase(str2);
    }

    /**
     * null-safe replace. replacement가 null이면 빈 문자열로 대체한다.
     */
    public static String replace(String text, String searchString, String replacement) {
        if (text == null || searchString == null || searchString.isEmpty()) return text;
        return text.replace(searchString, replacement == null ? "" : replacement);
    }

    /**
     * 왼쪽부터 지정 길이만큼 자른다. null은 빈 문자열로 처리한다.
     */
    public static String left(String str, int len) {
        return StringUtils.left(nvl(str), len);
    }

    /**
     * 오른쪽부터 지정 길이만큼 자른다. null은 빈 문자열로 처리한다.
     */
    public static String right(String str, int len) {
        return StringUtils.right(nvl(str), len);
    }

    /**
     * 시작 위치부터 문자열을 자른다. null은 빈 문자열로 처리한다.
     */
    public static String substring(String str, int start) {
        return StringUtils.substring(nvl(str), start);
    }

    /**
     * 시작/종료 위치 기준으로 문자열을 자른다. null은 빈 문자열로 처리한다.
     */
    public static String substring(String str, int start, int end) {
        return StringUtils.substring(nvl(str), start, end);
    }

    /**
     * 왼쪽을 지정 문자로 채운다.
     *
     * <pre>{@code
     * StringUtil.leftPad("7", 3, "0"); // "007"
     * }</pre>
     */
    public static String leftPad(String str, int size, String padStr) {
        return StringUtils.leftPad(nvl(str), size, padStr);
    }

    /**
     * 오른쪽을 지정 문자로 채운다.
     */
    public static String rightPad(String str, int size, String padStr) {
        return StringUtils.rightPad(nvl(str), size, padStr);
    }

    /**
     * 숫자만 남긴다. 전화번호/사번/날짜 문자열 정규화에 사용한다.
     */
    public static String onlyDigits(String str) {
        return nvl(str).replaceAll("[^0-9]", "");
    }

    /**
     * 콤마를 제거한다. 숫자 변환 전처리에 사용한다.
     */
    public static String removeComma(String str) {
        return nvl(str).replace(",", "");
    }

    /**
     * null을 제외한 컬렉션 값을 delimiter로 연결한다.
     */
    public static String join(Collection<?> values, String delimiter) {
        if (values == null || values.isEmpty()) return "";
        String safeDelimiter = delimiter == null ? "" : delimiter;

        return values.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(safeDelimiter));
    }

    /**
     * 이름 마스킹 (예: 홍길동 -> 홍*동 / 남궁민수 -> 남**수)
     */
    public static String maskName(String name) {
        if (isEmpty(name)) return "";
        int length = name.length();
        if (length <= 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "*".repeat(length - 2) + name.substring(length - 1);
    }

    /**
     * 휴대폰 번호 마스킹 (예: 01012345678 -> 010-****-5678)
     */
    public static String maskCellPhone(String phoneNo) {
        if (isEmpty(phoneNo)) return "";
        String cleanPhone = phoneNo.replaceAll("[^0-9]", ""); // 숫자만 추출

        if (cleanPhone.length() == 11) {
            return cleanPhone.substring(0, 3) + "-****-" + cleanPhone.substring(7);
        } else if (cleanPhone.length() == 10) {
            return cleanPhone.substring(0, 3) + "-***-" + cleanPhone.substring(6);
        }
        return phoneNo;
    }
}
