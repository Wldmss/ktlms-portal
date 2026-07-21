package com.kt.ktedu.core.web;

import com.kt.ktedu.auth.jwt.dto.JwtDTO;
import com.kt.ktedu.core.security.auth.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 모든 JSP 에서 현재 로그인 사용자를 {@code ${loginUser}} 로 참조할 수 있도록 model 에 노출.
 * 레거시의 {@code ${sessionScope.sessionMemberInfo.xxx}} 참조를 {@code ${loginUser.xxx}} 로 대체한다.
 * 미인증 요청에서는 null 이 담긴다 (JSTL 에서 {@code ${not empty loginUser}} 로 분기).
 */
@ControllerAdvice
public class CurrentUserModelAdvice {

    /* login user info */
    @ModelAttribute("loginUser")
    public JwtDTO loginUser() {
        return SecurityUtil.getCurrentUserOrNull();
    }

    /* portal main url */
    @Value("${url.portal.main}")
    private String mainUrl;

    @ModelAttribute("mainUrl")
    public String mainUrl() {
        return mainUrl;
    }
}
