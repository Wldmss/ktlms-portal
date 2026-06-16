package com.kt.ktedu.auth.jwt;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtDTO {
    private String userId;
    private String userNm;
    private String orgCd;
    private String comp;
    private String role;
}