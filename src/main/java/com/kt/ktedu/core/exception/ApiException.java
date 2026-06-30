package com.kt.ktedu.core.exception;

import lombok.Getter;

/**
 * REST API(@RestController) 에서 의도적으로 던지는 비즈니스 예외
 * GlobalExceptionHandler 에서 공통으로 잡아 ResponseDTO.fail() 형태로 응답
 */
@Getter
public class ApiException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public ApiException(String message, ErrorMessage errorMessage) {
        super(message);
        this.errorMessage = errorMessage;
    }

    public ApiException(String message) {
        super(message);
        this.errorMessage = ErrorMessage.API_ERROR;
    }

    public ApiException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
