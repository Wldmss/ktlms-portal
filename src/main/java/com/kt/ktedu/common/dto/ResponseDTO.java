package com.kt.ktedu.common.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {

    public enum ResultType {
        SUCCESS,    // 성공
        FAIL,       // 실패
        BYPASS,     // 2차 인증 우회 (관리자, 특정 조건 등)
        NEED_2FA,   // 2차 인증 필요
        TOKEN_EXPIRED,
        UNAUTHORIZED,
        FORBIDDEN
    }

    private boolean success;
    private ResultType result;
    private String message;
    private Object data;

    public static ResponseDTO success(String message, Object data) {
        return ResponseDTO.builder()
                .success(true)
                .result(ResultType.SUCCESS)
                .message(message)
                .data(data)
                .build();
    }

    public static ResponseDTO bypass(String message, Object data) {
        return ResponseDTO.builder()
                .success(true)
                .result(ResultType.BYPASS)
                .message(message)
                .data(data)
                .build();
    }

    public static ResponseDTO need2fa(String message) {
        return ResponseDTO.builder()
                .success(true)
                .result(ResultType.NEED_2FA)
                .message(message)
                .build();
    }

    public static ResponseDTO fail(String message) {
        return ResponseDTO.builder()
                .success(false)
                .result(ResultType.FAIL)
                .message(message)
                .build();
    }
}
