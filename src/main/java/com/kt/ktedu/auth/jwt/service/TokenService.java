package com.kt.ktedu.auth.jwt.service;

import com.kt.ktedu.auth.jwt.JwtProvider;
import com.kt.ktedu.auth.jwt.dto.JwtDTO;
import com.kt.ktedu.auth.jwt.dto.RefreshTokenDTO;
import com.kt.ktedu.auth.jwt.mapper.RefreshTokenMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * JWT access/refresh 토큰 라이프사이클 전담 서비스.
 *
 * <p>{@link JwtProvider}(생성/검증/쿠키 등 저수준 crypto)와 {@link RefreshTokenMapper}(refresh token 영속화)를
 * 오케스트레이션한다. 로그인 성공 시 발급({@link #issue}), refresh rotation·재사용 탐지({@link #refresh}),
 * 로그아웃 폐기({@link #logout}) 등 토큰 관련 처리를 한곳으로 모아 컨트롤러/로그인 서비스가 공유한다.</p>
 *
 * <p>사용처: {@code LoginService}(로그인 성공 발급), {@code AuthController}(refresh/logout),
 * {@code EntraSSOController}·SSO 로그인 성공 발급.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenMapper refreshTokenMapper;

    /**
     * 로그인 성공 시 access + refresh 토큰 발급, refresh token 영속화, 쿠키 세팅.
     *
     * @return 발급된 access token (Bearer 방식으로도 쓸 수 있게 바디로 내려주기 위함)
     */
    public String issue(JwtDTO jwtDTO, HttpServletResponse response) {
        String accessToken = jwtProvider.createAccessToken(jwtDTO);
        String refreshToken = jwtProvider.createRefreshToken(jwtDTO.getUserId());
        String tokenId = jwtProvider.getTokenIdFromRefreshToken(refreshToken);
        String refreshTokenHash = jwtProvider.hashToken(refreshToken);

        try {
            refreshTokenMapper.insert(RefreshTokenDTO.builder()
                    .userId(jwtDTO.getUserId())
                    .tokenId(tokenId)
                    .token(refreshTokenHash)
                    .expiresAt(toLocalDateTime(jwtProvider.getExpirationFromRefreshToken(refreshToken)))
                    .build());
        } catch (Exception e) {
            log.warn("refresh token persistence skipped. userId={}, reason={}", jwtDTO.getUserId(), e.getMessage());
        }

        jwtProvider.setAccessTokenCookie(response, accessToken);
        jwtProvider.setRefreshTokenCookie(response, refreshToken);
        return accessToken;
    }

    /**
     * Refresh Token 으로 Access Token 재발급 + Refresh Token Rotation.
     * <p>매 refresh 마다 refresh token 도 새로 발급하고 기존 것은 폐기하여, 탈취된 토큰의 유효 기간을 최소화한다.
     * 이미 rotation 으로 폐기된 토큰이 재사용되면 탈취 가능성으로 판단하여 해당 유저의 모든 세션을 무효화한다.</p>
     */
    public RefreshResult refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);
        if (refreshToken == null) {
            return RefreshResult.fail("로그인이 만료되었습니다. 다시 로그인해 주세요.");
        }

        // Refresh Token 서명 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            return RefreshResult.fail("유효하지 않은 접근입니다. 다시 로그인해 주세요.");
        }

        String userId = jwtProvider.getUserIdFromRefreshToken(refreshToken);
        String tokenId = jwtProvider.getTokenIdFromRefreshToken(refreshToken);
        String refreshTokenHash = jwtProvider.hashToken(refreshToken);

        // DB 에 저장된 세션(userId + tokenId)과 비교 (rotation 재사용/탈취 감지)
        RefreshTokenDTO savedToken = refreshTokenMapper.findByUserIdAndTokenId(userId, tokenId);
        if (savedToken == null || !savedToken.getToken().equals(refreshTokenHash)) {
            // 이미 rotation 으로 폐기된 토큰이 재사용됨 -> 탈취 가능성으로 판단, 해당 유저의 모든 기기 세션 무효화
            log.warn("Refresh Token 재사용/불일치 감지 - 탈취 가능성 - userId: {}", userId);
            refreshTokenMapper.deleteByUserId(userId);
            jwtProvider.clearAuthCookies(response);
            return RefreshResult.fail("로그인 정보가 유효하지 않습니다. 다시 로그인해 주세요.");
        }

        // 새 Access Token 발급
        // TODO: DB 에서 최신 사용자 정보 조회 후 JwtDTO 구성하는 것을 권장
        JwtDTO jwtDTO = JwtDTO.builder()
                .userId(userId)
                .userNm(savedToken.getUserId()) // TODO: DB 조회로 교체
                .role("ROLE_USER")              // TODO: DB 조회로 교체
                .build();

        // 절대 만료 시각을 그대로 물려줌 (rotation 으로 세션 최대 수명이 연장되지 않도록 => 로그인 후 최대 24시간).
        // 유휴 시계는 createRefreshToken(userId, absExp) 내부에서 now + 3시간으로 다시 갱신된다.
        long absExp = jwtProvider.getAbsoluteExpFromRefreshToken(refreshToken);

        String newAccessToken = jwtProvider.createAccessToken(jwtDTO);
        String newRefreshToken = jwtProvider.createRefreshToken(userId, absExp);
        String newTokenId = jwtProvider.getTokenIdFromRefreshToken(newRefreshToken);
        String newRefreshTokenHash = jwtProvider.hashToken(newRefreshToken);

        // 사용한 refresh token(tokenId) 은 삭제하고 새 세션으로 교체 -> 재사용 불가
        refreshTokenMapper.deleteByUserIdAndTokenId(userId, tokenId);
        refreshTokenMapper.insert(RefreshTokenDTO.builder()
                .userId(userId)
                .tokenId(newTokenId)
                .token(newRefreshTokenHash)
                .expiresAt(toLocalDateTime(jwtProvider.getExpirationFromRefreshToken(newRefreshToken)))
                .build());

        jwtProvider.setAccessTokenCookie(response, newAccessToken);
        jwtProvider.setRefreshTokenCookie(response, newRefreshToken);

        log.info("Access Token/Refresh Token 재발급(rotation) - userId: {}", userId);
        return RefreshResult.ok(newAccessToken);
    }

    /**
     * 현재 기기의 Refresh Token(DB) 삭제 + 쿠키 초기화.
     *
     * @return 로그아웃한 사용자 ID. Refresh Token이 없거나 파싱할 수 없으면 {@code null}
     */
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        String logoutUserId = null;
        String refreshToken = jwtProvider.resolveRefreshToken(request);
        if (refreshToken != null) {
            try {
                String userId = jwtProvider.getUserIdFromRefreshToken(refreshToken);
                logoutUserId = userId;
                String tokenId = jwtProvider.getTokenIdFromRefreshToken(refreshToken);
                int deleted = refreshTokenMapper.deleteByUserIdAndTokenId(userId, tokenId);
                log.info("로그아웃 - userId: {}, deletedRefreshTokenRows: {}", userId, deleted);
            } catch (Exception e) {
                log.warn("로그아웃 중 Refresh Token 파싱 실패 (무시): {}", e.getMessage());
            }
        }
        clearCookies(response);
        return logoutUserId;
    }

    /**
     * 해당 유저의 모든 refresh token(DB) 폐기 — 중복 세션 전체 로그아웃(force login) 등에 사용.
     */
    public void revokeAll(String userId) {
        refreshTokenMapper.deleteByUserId(userId);
    }

    public boolean hasActiveSession(String userId) {
        return refreshTokenMapper.existsActiveByUserId(userId);
    }

    public String createLoginChallenge(String userId,
                                       String purpose,
                                       String resultUrl,
                                       boolean accountLocked,
                                       HttpServletRequest request) {
        return jwtProvider.createLoginChallenge(
                userId,
                purpose,
                resultUrl,
                accountLocked,
                requestFingerprint(request)
        );
    }

    /**
     * 로그인 챌린지의 서명, 용도, 요청 지문을 함께 검증한다.
     */
    public JwtProvider.LoginChallenge verifyLoginChallenge(String token,
                                                           String expectedPurpose,
                                                           HttpServletRequest request) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("로그인 인증 정보가 없습니다.");
        }
        JwtProvider.LoginChallenge challenge = jwtProvider.parseLoginChallenge(token);
        if (!Objects.equals(expectedPurpose, challenge.purpose())) {
            throw new IllegalArgumentException("로그인 인증 단계가 올바르지 않습니다.");
        }
        if (!Objects.equals(requestFingerprint(request), challenge.requestFingerprint())) {
            throw new IllegalArgumentException("로그인 인증 요청 정보가 일치하지 않습니다.");
        }
        return challenge;
    }

    /**
     * access/refresh 쿠키 제거.
     */
    public void clearCookies(HttpServletResponse response) {
        jwtProvider.clearAuthCookies(response);
    }

    /* java.util.Date → LocalDateTime (DB expiresAt 기록용) */
    private static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private String requestFingerprint(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String source = request.getRemoteAddr() + "|" + (userAgent == null ? "" : userAgent);
        return jwtProvider.hashToken(source);
    }

    /**
     * refresh 처리 결과 — 컨트롤러는 이를 HTTP 응답(ResponseDTO/status)으로 매핑한다.
     */
    public record RefreshResult(boolean success, String accessToken, String message) {
        public static RefreshResult ok(String accessToken) {
            return new RefreshResult(true, accessToken, null);
        }

        public static RefreshResult fail(String message) {
            return new RefreshResult(false, null, message);
        }
    }
}
