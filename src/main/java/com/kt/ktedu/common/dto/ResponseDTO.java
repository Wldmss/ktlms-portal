package com.kt.ktedu.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseDTO {
    private boolean success;
    private String status;
    private String message;
}
