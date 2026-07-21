package com.kt.ktedu.auth.login.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Migrated from genius LoginSmsAuthVo.
 */
@Getter
@Setter
public class LoginSmsAuthDTO {
    private String userId;
    private String serial;
    private String deviceToken;
    private String osType;
    private String appVersion;
    private String isLock;
    private String devType;
}
