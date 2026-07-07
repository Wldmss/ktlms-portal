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

    /**
     * 현재 로그인 사용자명 반환 (미인증이면 null).
     */
    public static String getCurrentUserNm() {
        JwtDTO user = getCurrentUserOrNull();
        return user != null ? user.getUserNm() : null;
    }

    /**
     * 현재 사용자의 회사/기관 코드 반환 (미인증이면 null). 레거시 세션 comp 대체.
     */
    public static String getCurrentComp() {
        JwtDTO user = getCurrentUserOrNull();
        return user != null ? user.getComp() : null;
    }

    /**
     * 현재 사용자의 대표 조직코드 반환 (미인증이면 null).
     */
    public static String getCurrentOrgCd() {
        JwtDTO user = getCurrentUserOrNull();
        return user != null ? user.getOrgCd() : null;
    }

    /**
     * 현재 사용자의 role 반환 (예: ROLE_USER, ROLE_ADMIN. 미인증이면 null).
     */
    public static String getCurrentRole() {
        JwtDTO user = getCurrentUserOrNull();
        return user != null ? user.getRole() : null;
    }

    /**
     * 현재 사용자의 레거시 관리자 등급 코드 반환 (예: lms gadmin 'A','A1','ZZ'. 없으면/미인증이면 null).
     * 과도기용 — 신규 코드는 role/@PreAuthorize 사용 권장.
     */
    public static String getCurrentAdminGrade() {
        JwtDTO user = getCurrentUserOrNull();
        return user != null ? user.getAdminGrade() : null;
    }

    /**
     * 관리자 여부 (role 이 ROLE_ADMIN 인지). 레거시 isadmin/admr_yn 분기 대체.
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * role 보유 여부. "ADMIN" / "ROLE_ADMIN" 어느 형태로 넘겨도 동일하게 동작.
     * <pre>if (SecurityUtil.hasRole("ADMIN")) { ... }</pre>
     */
    public static boolean hasRole(String role) {
        String current = getCurrentRole();
        if (current == null || role == null) return false;

        String normalized = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        String currentNormalized = current.startsWith("ROLE_") ? current : "ROLE_" + current;
        return currentNormalized.equals(normalized);
    }
}
