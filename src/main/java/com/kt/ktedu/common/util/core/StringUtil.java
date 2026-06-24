package com.kt.ktedu.common.util.core;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

    private StringUtil() {
        // 인스턴스화 방지
    }

    /**
     * 문자열이 비어있거나 공백만 있는지 확인
     */
    public static boolean isEmpty(String str) {
        return StringUtils.isBlank(str);
    }

    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotBlank(str);
    }

    /**
     * Null이 들어오면 빈 값("")으로 변환, 값이 있으면 trim 처리
     */
    public static String nvl(String str) {
        return StringUtils.defaultString(str).trim();
    }

    /**
     * Null이 들어오면 지정한 기본값(defaultStr)으로 대체
     */
    public static String nvl(String str, String defaultStr) {
        return StringUtils.defaultIfBlank(str, defaultStr).trim();
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