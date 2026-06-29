package com.kt.ktedu.auth.ldap.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/* ldap return */
public class LdapResultDTO {
    private Boolean isAuth = false;     // 인증 여부
    private Boolean isExpired = false;  // 비번 만료 여부
    private String message;             // 메시지

    // 데이터 확인용 TEST
    private String cpAuth;
    private Map<String, Object> requestParam;
    private LdapResponseDTO ldapResponseDTO;
    private String ldapResponseOrigin;
    private String ldapAesKey;

    // ldap pass
    public static LdapResultDTO getLdapPassResult() {
        return LdapResultDTO.builder()
                .isAuth(true)
                .isExpired(false)
                .message("로그인 PASS")
                .build();
    }

    // ldap fail
    public static LdapResultDTO getLdapFailResult(String message) {
        return LdapResultDTO.builder()
                .isAuth(false)
                .isExpired(false)
                .message(message)
                .build();
    }
}