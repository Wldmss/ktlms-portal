package com.kt.ktedu.auth.sso.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entra SSO
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/entra-sso")
public class EntraSSOController {
/*
    private final OidcClientService oidcClientService;
    private final LoginService loginService;
    private final JwtProvider jwtProvider;

    *//**
     * OIDC 로그인 페이지 리다이렉트
     *//*
    @GetMapping(value = "/login")
    public void oidcLogin(HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException {
        try {
            String oidcIdpUrl = oidcClientService.getOidcIdpUrl(httpServletRequest);

            // 호출 실패 처리
            if (oidcIdpUrl == null || oidcIdpUrl.isEmpty()) {
                loginService.handleLoginFail(response, null);
                return;
            }

            // oidc 로그인 페이지 호출
            response.sendRedirect(oidcIdpUrl);
        } catch (Exception e) {
            loginService.handleLoginFail(response, null);
        }
    }

    *//**
     * OIDC Login Callback
     *//*
    @GetMapping(value = "/callback")
    public void handleOidcCallback(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        try {
            // 토큰 획득
            OidcUserDto oidcUserDto = oidcClientService.getOidcToken(request, response);
            if (oidcUserDto == null) {
                loginService.handleLoginFail(response, "사용자 정보를 가져오는데 실패했습니다.\n관리자에게 문의해주세요.");
                return;
            }

            // 임직원 사번
            String userId = oidcUserDto.getUserAttributes().getEmployeeNumber();
            if (userId == null || loginService.getUserIdExists(userId) == null) {
                loginService.handleLoginFail(response, "등록되지 않은 사용자입니다.\n관리자에게 문의해주세요.");
                return;
            }

            // 로그인 토큰 생성
            JwtDTO jwtDTO = JwtDTO.builder()
                    .userId(userId)
                    .userNm("테스트유저")
                    .orgCd("1001")
                    .comp("KT")
                    .role("ROLE_ADMIN")
                    .build();
            jwtProvider.loginToken(response, jwtDTO);

            // front redirect
            response.sendRedirect(loginService.getExamDomain());
        } catch (Exception e) {
            loginService.handleLoginFail(response, null);
        }
    }*/
}