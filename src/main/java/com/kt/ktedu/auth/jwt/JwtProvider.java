package com.kt.ktedu.auth.jwt;

import com.kt.ktedu.auth.jwt.dto.JwtDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.access-key}")
    private String accessKeyString;

    @Value("${jwt.refresh-key}")
    private String refreshKeyString;

    private SecretKey accessSecretKey;
    private SecretKey refreshSecretKey;

    /* value init */
    private SecretKey accessSecretKey() {
        if (accessSecretKey == null) {
            accessSecretKey = Keys.hmacShaKeyFor(accessKeyString.getBytes(StandardCharsets.UTF_8));
        }
        return accessSecretKey;
    }

    private SecretKey refreshSecretKey() {
        if (refreshSecretKey == null) {
            refreshSecretKey = Keys.hmacShaKeyFor(refreshKeyString.getBytes(StandardCharsets.UTF_8));
        }
        return refreshSecretKey;
    }

    // Access Token: 1시간
    public static final long ACCESS_EXPIRATION_MS = 1000L * 60 * 60 * 1;
    // Refresh Token: 7일
    public static final long REFRESH_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7;

    public static final String ACCESS_COOKIE_NAME = "access_token";
    public static final String REFRESH_COOKIE_NAME = "refresh_token";

    @Autowired
    private Environment environment;

    /**
     * local 프로파일이면 false, 그 외(dev/prod)는 true
     */
    private boolean isCookieSecure() {
        return !Arrays.asList(environment.getActiveProfiles()).contains("local");
    }

    // create login token TODO issueToken 처리
    public String loginToken(HttpServletResponse response, JwtDTO jwtDTO) {
        String token = this.createAccessToken(jwtDTO);
        return token;
    }

    // =========================================================
    // Access Token
    // =========================================================

    /**
     * Access Token 생성
     */
    public String createAccessToken(JwtDTO jwtDTO) {
        Date now = new Date();
        return Jwts.builder()
                .subject(jwtDTO.getUserId())
                .claim("userId", jwtDTO.getUserId())
                .claim("userNm", jwtDTO.getUserNm())
                .claim("orgCd", jwtDTO.getOrgCd())
                .claim("comp", jwtDTO.getComp())
                .claim("role", jwtDTO.getRole())
                .claim("type", "access")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_EXPIRATION_MS))
                .signWith(accessSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Access Token Claims 파싱
     */
    public Claims parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(accessSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Access Token 유효성 검증
     */
    public void validateAccessToken(String token) {
        try {
            Claims claims = parseAccessToken(token);

            if (!"access".equals(claims.get("type", String.class))) {
                throw new JwtException("Access Token type mismatch");
            }
        } catch (ExpiredJwtException e) {
            log.warn("만료된 Access Token");
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("유효하지 않은 Access Token: {}", e.getMessage());
            throw e;
        }
    }

    // =========================================================
    // Refresh Token
    // =========================================================

    /**
     * Refresh Token 생성
     * 토큰 고유 id(JWT 표준 클레임명: jti) 를 부여해 기기/세션 별로 구분 저장하고, rotation 재사용 감지에 사용한다.
     */
    public String createRefreshToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_EXPIRATION_MS))
                .signWith(refreshSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Refresh Token Claims 파싱
     */
    public Claims parseRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Refresh Token 유효성 검증 (만료 포함)
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseRefreshToken(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            log.warn("유효하지 않은 Refresh Token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Refresh Token 에서 userId 추출
     */
    public String getUserIdFromRefreshToken(String token) {
        return parseRefreshToken(token).getSubject();
    }

    /**
     * Refresh Token 에서 토큰 고유 id 추출 (JWT 표준 클레임명은 jti) - 기기/세션 구분 및 rotation 재사용 감지용
     */
    public String getTokenIdFromRefreshToken(String token) {
        return parseRefreshToken(token).getId();
    }

    // =========================================================
    // 공통 유틸
    // =========================================================

    /**
     * Access Token → JwtDTO 변환
     */
    public JwtDTO getUserInfoFromAccessToken(String token) {
        Claims claims = parseAccessToken(token);
        return JwtDTO.builder()
                .userId(claims.getSubject())
                .userNm(claims.get("userNm", String.class))
                .orgCd(claims.get("orgCd", String.class))
                .comp(claims.get("comp", String.class))
                .role(claims.get("role", String.class))
                .build();
    }

    /**
     * 요청에서 Access Token 추출 (Header 우선 → Cookie 순)
     * Header: Authorization: Bearer {token}
     * Cookie: access_token={token}
     */
    public String resolveAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return getCookieValue(request, ACCESS_COOKIE_NAME);
    }

    /**
     * 요청에서 Refresh Token 추출 (Cookie)
     */
    public String resolveRefreshToken(HttpServletRequest request) {
        return getCookieValue(request, REFRESH_COOKIE_NAME);
    }

    /**
     * refresh token hash 추출
     *
     */
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Token hash failed", e);
        }
    }

    // =========================================================
    // 쿠키 관리
    // =========================================================

    private String sameSite() {
        return "local".equals(String.join(",", environment.getActiveProfiles()))
                ? "Lax"
                : "None";
    }

    /**
     * Access Token 쿠키 세팅
     */
    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(isCookieSecure())
                .sameSite(sameSite())
                .path("/")
                .maxAge(ACCESS_EXPIRATION_MS / 1000)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Refresh Token 쿠키 세팅
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(isCookieSecure())
                .sameSite(sameSite())
                .path("/auth")
                .maxAge(REFRESH_EXPIRATION_MS / 1000)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Access + Refresh 쿠키 모두 삭제 (로그아웃)
     */
    public void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(isCookieSecure())
                .sameSite(sameSite())
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(isCookieSecure())
                .sameSite(sameSite())
                .path("/auth")
                .maxAge(0)
                .build();

        ResponseCookie csrfCookie = ResponseCookie.from("XSRF-TOKEN", "")
                .httpOnly(false)
                .secure(isCookieSecure())
                .sameSite(sameSite())
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        response.addHeader("Set-Cookie", csrfCookie.toString());
    }

    // =========================================================
    // private
    // =========================================================

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
