package com.kt.ktedu.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 요청마다 CSP nonce 를 발급하는 필터.
 *
 * <p>발급한 nonce 를 (1) request attribute {@code cspNonce} 로 노출하고(JSP 에서 {@code ${cspNonce}} 로 사용),
 * (2) <b>Report-Only</b> CSP 헤더의 {@code script-src} 에 {@code 'nonce-...'} 로 넣는다.</p>
 *
 * <p>이관 전략(점진 적용):</p>
 * <ul>
 *   <li>적용(enforced) CSP 는 {@code script-src 'self' 'unsafe-inline'} 유지 → 전환 전 인라인 스크립트도 안 깨짐.</li>
 *   <li>Report-Only 는 nonce 기반 → 인라인 {@code <script nonce="${cspNonce}">} 로 바꾼 것만 통과,
 *       아직 안 바꾼 것은 콘솔에 계속 보고됨(= 남은 작업 목록).</li>
 *   <li>모든 인라인 스크립트가 nonce 로 전환되면, 그때 enforced 를 {@code script-src 'self' 'nonce-...'} 로 flip.</li>
 * </ul>
 */
public class CspNonceFilter extends OncePerRequestFilter {

    public static final String NONCE_ATTRIBUTE = "cspNonce";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String nonce = generateNonce();
        request.setAttribute(NONCE_ATTRIBUTE, nonce);

        response.setHeader("Content-Security-Policy-Report-Only",
                "default-src 'self'; " +
                        "script-src 'self' 'nonce-" + nonce + "'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data: blob:; " +
                        "font-src 'self' data:; " +
                        "connect-src 'self'; " +
                        "object-src 'none'; " +
                        "base-uri 'self'; " +
                        "frame-ancestors 'self'; " +
                        "form-action 'self'");

        chain.doFilter(request, response);
    }

    private String generateNonce() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
