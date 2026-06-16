package com.kt.ktedu.common.interceptor;

import com.kt.ktedu.auth.jwt.JwtDTO;
import com.kt.ktedu.auth.jwt.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component("jwtInterceptor")
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authHeader = request.getHeader("Authorization");
        String redirectLoginUrl = request.getContextPath() + "/";

        try {
            // 토큰 검증
            if (authHeader != null && jwtProvider.validateToken(authHeader)) {
                // token 유효함
                String token = authHeader.substring(7);
                try {
                    JwtDTO loginUser = jwtProvider.getUserInfoFromToken(token);
                    request.setAttribute("loginUser", loginUser);

                    return true;
                } catch (Exception e) {
                    response.sendRedirect(redirectLoginUrl);
                    return false;
                }
            }
        } catch (ExpiredJwtException e) {
            // 토큰 만료
            request.getSession().invalidate();

            // 현재 요청이 Ajax(비동기) 요청인지, 일반 브라우저 주소창 이동인지 판별
            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

            if (isAjax) {
                // A. Ajax 요청이라면 HTTP 상태코드를 401(인증안됨)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("TOKEN_EXPIRED");
            } else {
                // B. 일반 페이지 이동 중에 만료 시 alert -> 로그인 페이지 이동
                jwtProvider.alertExpiredToken(response, request);
            }
            return false;
        }

        // 토큰이 아예 없는 등 기타 예외 처리 시 로그인 페이지로 리다이렉트
        response.sendRedirect(redirectLoginUrl);
        return false;
    }
}

