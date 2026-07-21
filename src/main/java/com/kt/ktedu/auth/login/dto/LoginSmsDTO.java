package com.kt.ktedu.auth.login.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Migrated from genius LoginSmsVo.
 */
@Getter
@Setter
public class LoginSmsDTO {
    private String userId;
    private String deviceToken;
    private String osType;
    private String appVersion;
}
