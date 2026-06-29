package com.kt.ktedu.auth.jwt;

import com.kt.ktedu.auth.ldap.dto.LoginDTO;
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
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret-key}")
    private String secretKey;

    // ⚠️ 실무 보안 필수: application.properties 로 외부화 필요 (최소 32바이트 이상)
    private static final String ACCESS_SECRET_STRING = "KT_LMS_PORTAL_ACCESS_TOKEN_SECRET_KEY_2026";
    private static final String REFRESH_SECRET_STRING = "KT_LMS_PORTAL_REFRESH_TOKEN_SECRET_KEY_2026_LONG";

    private final SecretKey accessSecretKey = Keys.hmacShaKeyFor(ACCESS_SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    private final SecretKey refreshSecretKey = Keys.hmacShaKeyFor(REFRESH_SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    // Access Token: 3시간
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
                .signWith(accessSecretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Access Token Claims 파싱
     */
    public Claims parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(accessSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Access Token 유효성 검증
     */
    public boolean validateAccessToken(String token) {
        try {
            parseAccessToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 Access Token");
            throw e; // 만료는 caller 에서 처리 (refresh 로직 분기용)
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 Access Token: {}", e.getMessage());
            return false;
        }
    }

    // =========================================================
    // Refresh Token
    // =========================================================

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_EXPIRATION_MS))
                .signWith(refreshSecretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Refresh Token Claims 파싱
     */
    public Claims parseRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey)
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

    // =========================================================
    // 쿠키 관리
    // =========================================================

    /**
     * Access Token 쿠키 세팅
     */
    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(ACCESS_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(isCookieSecure());
        cookie.setPath("/");
        cookie.setMaxAge((int) (ACCESS_EXPIRATION_MS / 1000));
        response.addCookie(cookie);
    }

    /**
     * Refresh Token 쿠키 세팅
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(isCookieSecure());
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge((int) (REFRESH_EXPIRATION_MS / 1000));
        response.addCookie(cookie);
    }

    /**
     * Access + Refresh 쿠키 모두 삭제 (로그아웃)
     */
    public void clearAuthCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie(ACCESS_COOKIE_NAME, null);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(isCookieSecure());
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(isCookieSecure());
        refreshCookie.setPath("/auth/refresh");
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
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
