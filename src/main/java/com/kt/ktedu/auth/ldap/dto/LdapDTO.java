/*
 * ktgenius-portal version 1.0
 *
 *  Copyright ⓒ [2026] kt corp. All rights reserved.
 *
 *  This is a proprietary software of kt corp, and you may not use this file except in
 *  compliance with license agreement with kt corp. Any redistribution or use of this
 *  software, with or without modification shall be strictly prohibited without prior written
 *  approval of kt corp, and the copyright notice above does not evidence any actual or
 *  intended publication of such software.
 */
package com.kt.ktedu.auth.ldap.dto;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Getter
@Component
public class LdapDTO {

    private final String connId;
    private final String connPwd;
    private final String url;
    private final String aesKey;
    private final String apiId;
    private final String apiPwd;

    public LdapDTO(
            @Value("${ldap.conn-id}") String connId,
            @Value("${ldap.conn-pwd}") String connPwd,
            @Value("${ldap.url}") String url,
            @Value("${ldap.aes-key}") String aesKey,
            @Value("${ldap.api-id}") String apiId,
            @Value("${ldap.api-pwd}") String apiPwd) {

        this.connId = connId;
        this.connPwd = connPwd;
        this.url = url;
        this.aesKey = aesKey;
        this.apiId = apiId;
        this.apiPwd = apiPwd;
    }

    // Base64로 변환된 key 생성
    public String getCPAuth() {
        String rawValue = apiId + ":" + apiPwd;
        return Base64.getEncoder().encodeToString(rawValue.getBytes(StandardCharsets.UTF_8));
    }
}