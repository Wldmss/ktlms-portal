package com.kt.ktedu.auth.login.controller;

import com.kt.ktedu.auth.jwt.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final JwtProvider jwtProvider;

    /**
     * 로그인 페이지 (GET /)
     * 이미 유효한 Access Token 이 있으면 메인으로 리다이렉트
     */
    @GetMapping("/")
    public String loginPage(HttpServletRequest request) {
        String accessToken = jwtProvider.resolveAccessToken(request);

        if (accessToken != null) {
            try {
                jwtProvider.validateAccessToken(accessToken);
                return "redirect:/sample"; // 이미 로그인된 경우 메인으로
            } catch (ExpiredJwtException e) {
                // 만료 → 로그인 페이지 (클라이언트에서 refresh 처리)
            } catch (JwtException | IllegalArgumentException e) {
                // 유효하지 않은 토큰 → 로그인 페이지
            }
        }

        return "login/login";
    }

    @GetMapping({"/login", "/mobile/m/login"})
    public String loginRedirect() {
        return "redirect:/";
    }

    @GetMapping({"/logout", "/mobile/m/logout"})
    public String logout() {
        return "redirect:/auth/logout";
    }

    /* 크롬 devtools pageNotFound 로그 숨김 처리 */
    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    @ResponseBody
    public void ignoreChromeDevToolsRequest() {
    }
}
