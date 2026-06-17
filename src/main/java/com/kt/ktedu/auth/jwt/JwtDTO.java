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
    private String s_userid;

    public JwtDTO(String userId, String userNm, String orgCd, String comp, String role) {
        this.userId = userId;
        this.userNm = userNm;
        this.orgCd = orgCd;
        this.comp = comp;
        this.role = role;
        this.s_userid = userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.s_userid = userId;
    }
}