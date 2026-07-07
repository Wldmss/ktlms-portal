package com.kt.ktedu.auth.jwt.dto;

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
    private String adminGrade; // 과도기용, nullable: 레거시 관리자 등급 코드(예: lms gadmin 'A', 'A1', 'ZZ')
}