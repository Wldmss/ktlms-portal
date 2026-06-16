package com.kt.ktedu.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    // ⚠️ 실무 보안 필수: 최소 32바이트 이상의 비밀키 문자열
    private final String SECRET_STRING = "KT_LMS_PORTAL_PROJECT_JWT_SECRET_KEY_2026_06_04";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    private final long EXPIRATION_TIME = 1000L * 60 * 60 * 2; // 2시간 유효

    // 토큰 굽기 (생성)
    public String createToken(JwtDTO jwtDTO) {
        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(jwtDTO.getUserId())
                .claim("userId", jwtDTO.getUserId())
                .claim("userNm", jwtDTO.getUserNm())
                .claim("role", jwtDTO.getRole())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // 토큰 쪼개기 (Claims 해독)
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 JWT 토큰입니다.", e);
            throw e;
        }
    }

    // 토큰 안에서 바로 DTO 객체로 유저 정보 복원하기
    public JwtDTO getUserInfoFromToken(String token) {
        Claims claims = parseClaims(token);
        return JwtDTO.builder()
                .userId(claims.getSubject())
                .userNm(claims.get("userNm", String.class))
                .role(claims.get("role", String.class))
                .build();
    }

    // 토큰 만료 여부 단순 확인
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰 만료 alert
    public void alertExpiredToken(HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {
        request.setAttribute("serverMessage", "로그인 세션이 만료되었습니다.\\n다시 로그인해 주세요.");
        request.setAttribute("serverTargetUrl", "/");

        request.getRequestDispatcher("/WEB-INF/common/message-redirect.jsp").forward(request, response);
    }

    /**
     * 토큰 발급 및 쿠키 적재 일괄 처리
     *
     * @param userId   사번 또는 ID
     * @param response HTTP 응답 객체 (쿠키를 심기 위함)
     * @return 생성된 Access Token 문자열 (필요시 컨트롤러에서 로그나 후처리에 쓰도록 리턴)
     */
    public String issueTokenAndSetCookie(String userId, HttpServletResponse response) {
        String accessToken = createToken(new JwtDTO(userId, "김지은", "1001", "1001", "ROLE_USER"));
        Cookie jwtCookie = new Cookie("Authorization", "Bearer_" + accessToken);

        jwtCookie.setHttpOnly(true);   // 🛡️ 자바스크립트 변수 탈취(XSS) 방지
        jwtCookie.setSecure(false);    // 개발(local/http)은 false, 운영(https)은 true로 튜닝
        jwtCookie.setPath("/");        // 프로젝트 전역 경로에서 접근 허용
        jwtCookie.setMaxAge((int) (EXPIRATION_TIME / 1000)); // 쿠키 수명 = 토큰 수명 동기화

        response.addCookie(jwtCookie);
        return accessToken;
    }

    /**
     * 토큰 자동 추출 (Header 우선 -> 없으면 Cookie 검색)
     *
     * @param request HTTP 요청 객체
     * @return 순수 JWT 토큰 문자열 (없으면 null)
     */
    public String resolveToken(HttpServletRequest request) {
        // HTTP 헤더에서 "Authorization" 확인 (Ajax 통신)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // "Bearer " 제거 후 리턴
        }

        // 헤더에 없다면 브라우저 쿠키(Cookie) 저장소 확인(페이지 이동 대비)
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    String cookieValue = cookie.getValue();
                    if (cookieValue != null && cookieValue.startsWith("Bearer_")) {
                        return cookieValue.substring(7);
                    }
                }
            }
        }

        return null;
    }

    /**
     * 로그아웃 처리 및 JWT 쿠키 제거
     *
     * @param response HTTP 응답 객체 (쿠키를 지우기 위함)
     */
    public void deleteTokenCookie(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("Authorization", null);

        jwtCookie.setHttpOnly(true);   // 발급할 때와 동일하게 쉴드 유지
        jwtCookie.setSecure(false);    // 발급할 때와 동일하게 설정 (local=false / 실운영=true)
        jwtCookie.setPath("/");        // 발급할 때와 동일한 경로 지정 필수
        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);
    }
}