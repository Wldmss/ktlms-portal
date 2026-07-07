package com.kt.ktedu.core.security.auth;

import com.kt.ktedu.auth.jwt.dto.JwtDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 현재 로그인 사용자 정보
 * @Controller -> {@AuthenticationPrincipal CustomUserDetails} 우선 사용
 * @Service/@Component 계층처럼 인자 주입이 불가능한 곳에서만 이 유틸을 사용
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * 현재 로그인 사용자 정보 반환.
     * 인증 정보가 없으면 예외를 던진다 (인증이 보장된 URL 에서만 호출할 것).
     */
    public static JwtDTO getCurrentUser() {
        JwtDTO user = getCurrentUserOrNull();
        if (user == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        return user;
    }

    /**
     * 현재 로그인 사용자 정보 반환. 미인증이면 null.
     * 로그인/비로그인 모두 접근 가능한 곳에서 사용한다.
     */
    public static JwtDTO getCurrentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getJwtDTO();
        }
        return null;
    }

    /**
     * 현재 로그인 사용자 ID 반환 (미인증이면 null).
     */
    public static String getCurrentUserId() {
        JwtDTO user = getCurrentUserOrNull();
        return user != null ? user.getUserId() : null;
    }
}
