package com.kt.ktedu.auth.login.controller;

import com.kt.ktedu.auth.jwt.JwtProvider;
import com.kt.ktedu.common.dto.ResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private JwtProvider jwtProvider;

    @Value("#{config['ktgenius.key']}")
    String ktGeniusKey;

    @Value("#{config['db.url']}")
    String dbUrl;

    /**
     * 로그인 페이지
     */
    @GetMapping("/")
    public String login(HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException {
        String accessToken = jwtProvider.resolveToken(request);
        log.info("login-{}", accessToken);

        if (accessToken != null) {
            try {
                if (jwtProvider.validateToken(accessToken)) {
                    return "redirect:/sample";
                }
            } catch (ExpiredJwtException e) {
                jwtProvider.alertExpiredToken(response, request);
                return null;
            } catch (JwtException | IllegalArgumentException e) {
                // token 이 없는 경우 로그인 페이지로 이동
            }
        }

        return "login/login";
    }

    @GetMapping({"/login", "/mobile/m/login"})
    public String loginRedirect() {
        return "redirect:/";
    }

    @PostMapping(value = "/doLogin", produces = "application/json; charset=UTF-8")
    public ResponseEntity<ResponseDTO> doLogin(HttpServletResponse response, HttpSession session, @RequestBody Map<String, String> loginData) {
        String accessToken = jwtProvider.issueTokenAndSetCookie("91352089", response);
        session.setAttribute("accessToken", accessToken);
        log.info("doLogin-{}", accessToken);
        ResponseDTO responseDTO = ResponseDTO.builder()
                .success(true)
                .status("BYPASS")
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 로그아웃
     */
    @GetMapping("/logout")
    public String logout(HttpServletResponse response, HttpServletRequest request, HttpSession session) {
        jwtProvider.deleteTokenCookie(response);
        session.invalidate();
        return "redirect:/";
    }

    /* 크롬 devtools pageNotFound 로그 숨김 처리 */
    @GetMapping({"/.well-known/appspecific/com.chrome.devtools.json"})
    @ResponseBody
    public void ignoreChromeDevToolsRequest() {
    }
}