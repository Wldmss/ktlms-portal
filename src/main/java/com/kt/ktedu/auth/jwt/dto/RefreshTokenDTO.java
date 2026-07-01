package com.kt.ktedu.auth.jwt.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Refresh Token DB 저장용 DTO
 * 테이블: tb_refresh_token
 *
 * 기기/세션별로 여러 개의 refresh token 을 동시에 유지할 수 있도록
 * user_id 단일 PK 대신 user_id + tokenId(토큰 고유 id) 복합키를 사용한다.
 * tokenId 는 JWT 안에 표준 클레임(jti) 으로 실려 있는 값을 우리 코드/DB 쪽에서 부르는 이름이다.
 *
 * CREATE TABLE tb_refresh_token (
 *     user_id      VARCHAR(100)  NOT NULL,
 *     token_id     VARCHAR(36)   NOT NULL,
 *     token        TEXT          NOT NULL,
 *     expires_at   TIMESTAMP     NOT NULL,
 *     created_at   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
 *     PRIMARY KEY (user_id, token_id)
 * );
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDTO {
    private String userId;
    private String tokenId;
    private String token;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
