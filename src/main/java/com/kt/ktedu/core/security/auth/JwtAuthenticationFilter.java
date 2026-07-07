package com.kt.ktedu.core.security.auth;

import com.kt.ktedu.auth.jwt.JwtProvider;
import com.kt.ktedu.auth.jwt.dto.JwtDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 매 요청마다 Access Token 을 검증하고 SecurityContext 에 인증 정보를 세팅하는 필터
 * Spring Security 필터 체인에서 UsernamePasswordAuthenticationFilter 이전에 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    /**
     * /auth/** 는 access token 필터를 태우지 않는다.
     * 로그인/재발급/로그아웃은 refresh 쿠키·요청 바디로 자체 인증하며,
     * 특히 만료된 access token 때문에 /auth/refresh 가 막혀 재발급 자체가 불가능해지는 것을 방지한다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtProvider.resolveAccessToken(request);

        if (token != null) {
            try {
                // Access Token 검증 후 SecurityContext 세팅
                jwtProvider.validateAccessToken(token); // 만료 시 ExpiredJwtException throw
                JwtDTO jwtDTO = jwtProvider.getUserInfoFromAccessToken(token);

                CustomUserDetails userDetails = new CustomUserDetails(jwtDTO, null);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException e) {
                // Access Token 만료 → 401 반환 (클라이언트가 Refresh 요청 처리)
                SecurityContextHolder.clearContext();
                writeUnauthorized(response, "TOKEN_EXPIRED", "Access Token이 만료되었습니다.");
                return;

            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
                writeUnauthorized(response, "INVALID_TOKEN", "유효하지 않은 Access Token입니다.");
                return;

            } catch (Exception e) {
                log.error("JWT 처리 중 오류: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String result, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"result\":\"%s\",\"message\":\"%s\"}",
                result,
                message
        ));
    }
}
