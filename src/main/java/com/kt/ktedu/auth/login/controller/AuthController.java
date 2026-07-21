package com.kt.ktedu.auth.login.controller;

import com.kt.ktedu.auth.jwt.service.TokenService;
import com.kt.ktedu.auth.login.service.LoginService;
import com.kt.ktedu.common.common.dto.ResponseDTO;
import com.kt.ktedu.common.web.DeviceResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 API 컨트롤러 (JSON/토큰 클라이언트용)
 * POST /auth/refresh - Access Token 재발급 (Refresh Token rotation 포함)
 * POST /auth/logout  - 로그아웃 (현재 기기의 Refresh Token 삭제)
 *
 * <p>토큰 발급/회전/폐기 로직은 {@link TokenService}에 위임한다(브라우저 로그인 흐름과 공유).</p>
 */
@Slf4j
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final LoginService loginService;
    private final DeviceResolver deviceResolver;

    /**
     * POST /auth/refresh
     * Access Token 재발급
     * Refresh Token 으로 새 Access Token 발급 + Refresh Token Rotation (탈취 재사용 탐지 포함).
     * Refresh Token 은 HttpOnly Cookie 로 자동 전송됨.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO> refresh(HttpServletRequest request, HttpServletResponse response) {
        TokenService.RefreshResult result = tokenService.refresh(request, response);
        if (!result.success()) {
            return ResponseEntity.status(401).body(ResponseDTO.fail(result.message()));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", result.accessToken());
        return ResponseEntity.ok(ResponseDTO.success("Access Token이 재발급되었습니다.", data));
    }

    /**
     * POST /auth/logout
     * 현재 기기의 Refresh Token(DB) 삭제 + 쿠키 초기화
     * GET 은 상태를 변경해도 CSRF 검증 대상에서 빠지는 문제가 있어 POST 로만 허용한다.
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(HttpServletRequest request, HttpServletResponse response) {
        String userId = tokenService.logout(request, response);
        if (userId != null && deviceResolver.isApp(request)) {
            loginService.updateExpireLoginKey(userId);
        }
        return ResponseEntity.ok(ResponseDTO.success("로그아웃 되었습니다.", null));
    }
}
