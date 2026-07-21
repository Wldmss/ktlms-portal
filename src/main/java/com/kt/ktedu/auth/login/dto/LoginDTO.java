package com.kt.ktedu.auth.login.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {
    String userId;    // 사번
    String password;    // 비밀번호

    String keySeq;
    String encryptedPassword;

    Map<String, Object> claims; // jwt claims

    public void addClaims(String userId, String examId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        setUserId(userId);
        setClaims(claims);
    }
}