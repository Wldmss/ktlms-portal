package com.kt.ktedu.auth.login.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Migrated from genius LoginFirstVo.
 * Login completion is handled by Spring Security + JWT; this DTO preserves the legacy request shape.
 */
@Getter
@Setter
public class LoginFirstDTO {
    private String userId;
    private String pwd;
    private String keySeq;
}
