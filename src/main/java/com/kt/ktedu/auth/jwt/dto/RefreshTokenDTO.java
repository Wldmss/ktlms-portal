package com.kt.ktedu.auth.jwt.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Refresh Token DB 저장용 DTO
 * 테이블: tb_refresh_token
 *
 * CREATE TABLE tb_refresh_token (
 *     user_id      VARCHAR(100)  NOT NULL,
 *     token        TEXT          NOT NULL,
 *     expires_at   TIMESTAMP     NOT NULL,
 *     created_at   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
 *     PRIMARY KEY (user_id)
 * );
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDTO {
    private String        userId;
    private String        token;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
