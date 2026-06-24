package com.kt.ktedu.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 인증은 Spring Security Filter Chain (JwtAuthenticationFilter) 에서 처리됩니다.
 * 이 Interceptor 는 인증 이후 공통 처리가 필요할 때 사용하세요.
 * (예: 요청 로깅, 권한 세분화, loginUser 세션 세팅 등)
 */
@Component("jwtInterceptor")
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 필요시 공통 처리 로직 추가
        return true;
    }
}
