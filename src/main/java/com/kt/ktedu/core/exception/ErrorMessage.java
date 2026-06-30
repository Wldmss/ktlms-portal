package com.kt.ktedu.core.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    API_ERROR("API 처리 중 알 수 없는 오류가 발생했습니다."),
    INVALID_REQUEST("요청 값이 올바르지 않습니다."),
    NOT_FOUND("요청하신 데이터를 찾을 수 없습니다."),
    UNAUTHORIZED("인증 정보가 유효하지 않습니다."),
    FORBIDDEN("접근 권한이 없습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
