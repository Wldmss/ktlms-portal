package com.kt.ktedu.auth.sso.entra.controller;

import com.core.sso.oidc.dto.OidcUserDto;
import com.kt.ktedu.auth.jwt.dto.JwtDTO;
import com.kt.ktedu.auth.jwt.service.TokenService;
import com.kt.ktedu.auth.login.service.LoginService;
import com.kt.ktedu.common.util.core.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Entra SSO (Microsoft Entra ID / OIDC)
 * TODO nexus 연결 후 수정
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/sso/entra-sso")
public class EntraSSOController {

    /**
     * 로그인 시작 시점의 redirect url 을 OIDC 왕복 이후 callback 까지 넘기기 위한 세션 키
     */
    private static final String SSO_REDIRECT_URL = "ssoRedirectUrl";

    //    private final OidcClientService oidcClientService;
    private final LoginService loginService;
    private final TokenService tokenService;

    @Value("${url.portal.main}")
    private String mainUrl;

    /**
     * OIDC 로그인 페이지 리다이렉트 (Microsoft 로그인 실행)
     */
    @GetMapping(value = "/login")
    public String oidcLogin(HttpServletRequest request) {
        // redirect url 검증만 먼저 수행 (세션 기록은 실제 IdP 로 넘어갈 때만 → 실패 시 세션 잔여값 없음)
        String redirectUrl = requestedSafeRedirect(request);
        try {
//            String oidcIdpUrl = oidcClientService.getOidcIdpUrl(request);
            String oidcIdpUrl = "";

            // IdP URL 생성 실패
            if (StringUtil.isBlankParam(oidcIdpUrl)) {
                return loginFail(null, redirectUrl);
            }

            // 실제로 Microsoft 로 넘어갈 때만 세션에 보관 (callback 은 별도 요청이라 파라미터가 유실됨)
            storeRedirectUrl(request, redirectUrl);
            return "redirect:" + oidcIdpUrl;
        } catch (Exception e) {
            log.error("Entra SSO IdP redirect failed.", e);
            return loginFail(null, redirectUrl);
        }
    }

    /**
     * OIDC Login Callback
     * <p>Microsoft 로그인 후 콜백. 토큰 획득 → 사용자 검증 → (성공 시) LDAP 로그인과 동일한 JWT 발급.</p>
     */
    @GetMapping(value = "/callback")
    public String handleOidcCallback(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            // 토큰 획득
//            OidcUserDto oidcUserDto = oidcClientService.getOidcToken(request, response);
            OidcUserDto oidcUserDto = null;
            if (oidcUserDto == null) {
                return loginFail("사용자 정보를 가져오는데 실패했습니다.\n관리자에게 문의해주세요.", consumeRedirectUrl(session));
            }

            // 임직원 사번
            String userId = oidcUserDto.getUserAttributes().getEmployeeNumber();
            if (StringUtil.isBlankParam(userId) || loginService.isUserIdExists(userId)) {
                return loginFail("등록되지 않은 사용자입니다.\n관리자에게 문의해주세요.", consumeRedirectUrl(session));
            }

            // 로그인 처리: 기존 LDAP 로그인과 동일하게 memberInfo 기반 JWT 발급 + refresh token 영속화
            JwtDTO jwtDTO = loginService.buildJwtDTO(userId);
            tokenService.issue(jwtDTO, response);

            // 로그인 성공 → 세션에 보관해둔 redirect url(없으면 메인)로 이동
            String redirectUrl = consumeRedirectUrl(session);
            return "redirect:" + (redirectUrl != null ? redirectUrl : mainUrl);
        } catch (Exception e) {
            log.error("Entra SSO callback failed.", e);
            return loginFail(null, consumeRedirectUrl(session));
        }
    }

    /**
     * 로그인 시작 요청의 {@code url}/{@code redirect} 파라미터를 오픈리다이렉트 검증.
     *
     * @return 검증을 통과한 안전한 redirect 경로, 없거나 부적합하면 {@code null}
     */
    private String requestedSafeRedirect(HttpServletRequest request) {
        String requested = StringUtil.firstNonBlank(request.getParameter("url"), request.getParameter("redirect"));
        return loginService.resolveSafeRedirect(requested);
    }

    /**
     * 검증된 redirect url 을 세션에 보관 (callback 에서 소비). 값이 없으면 이전 잔여 값을 제거.
     */
    private void storeRedirectUrl(HttpServletRequest request, String safe) {
        HttpSession session = request.getSession(true);
        if (safe != null) {
            session.setAttribute(SSO_REDIRECT_URL, safe);
        } else {
            session.removeAttribute(SSO_REDIRECT_URL);
        }
    }

    /**
     * 세션에 보관된 redirect url 을 꺼내 소비(제거)한다. 없으면 {@code null}.
     */
    private String consumeRedirectUrl(HttpSession session) {
        Object saved = session.getAttribute(SSO_REDIRECT_URL);
        session.removeAttribute(SSO_REDIRECT_URL);
        return saved == null ? null : String.valueOf(saved);
    }

    /**
     * SSO 로그인 실패 처리 — 포탈 로그인 페이지로 리다이렉트하며 메시지 + redirect url 전달.
     * (login.jsp 가 {@code message} 는 {@code ${ssoMessage}} 로, {@code url} 은 hidden 필드로 유지 →
     * 사용자가 이어서 일반 로그인/재시도 해도 원래 목적지가 보존됨)
     */
    private String loginFail(String message, String redirectUrl) {
        String query = "";
        if (StringUtil.isBlankParam(message)) {
            message = "Microsoft 로그인 오류가 발생했습니다.\n관리자에게 문의해주세요.";
        }
        query = appendParam(query, "message", message);

        if (!StringUtil.isBlankParam(redirectUrl)) {
            query = appendParam(query, "url", redirectUrl);
        }
        return "redirect:/login" + query;
    }

    private String appendParam(String query, String key, String value) {
        return query + (query.isEmpty() ? "?" : "&") + key + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
