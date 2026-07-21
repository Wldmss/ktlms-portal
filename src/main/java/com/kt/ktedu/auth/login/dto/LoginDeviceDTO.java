package com.kt.ktedu.auth.login.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Migrated from genius LoginVo.
 */
@Getter
@Setter
public class LoginDeviceDTO {
    private String deviceToken;
    private String osType;
    private String appVersion;
    private String loginKey;
    private String devType;
}
