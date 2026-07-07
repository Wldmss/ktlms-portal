package com.kt.ktedu.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * API 공통 에러 유형. 메시지 + HTTP status 매핑.
 * ApiException 과 함께 사용 — GlobalExceptionHandler 가 status/message 대로 응답한다.
 * <pre>throw new ApiException(ErrorMessage.NOT_FOUND);</pre>
 */
@Getter
public enum ErrorMessage {
    API_ERROR("알 수 없는 오류가 발생했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST("요청 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND("요청하신 데이터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("인증 정보가 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;

    ErrorMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
