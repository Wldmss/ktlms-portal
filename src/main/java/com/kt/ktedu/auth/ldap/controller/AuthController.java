package com.kt.ktedu.auth.ldap.controller;

import com.kt.ktedu.auth.jwt.dto.JwtDTO;
import com.kt.ktedu.auth.jwt.JwtProvider;
import com.kt.ktedu.auth.jwt.dto.RefreshTokenDTO;
import com.kt.ktedu.auth.jwt.RefreshTokenMapper;
import com.kt.ktedu.auth.ldap.dto.LdapResultDTO;
import com.kt.ktedu.auth.ldap.dto.LoginDTO;
import com.kt.ktedu.common.common.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 인증 API 컨트롤러
 * POST /auth/login   - 로그인 (Access + Refresh Token 발급)
 * POST /auth/refresh - Access Token 재발급
 * POST /auth/logout  - 로그아웃 (Refresh Token 삭제)
 */
@Slf4j
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenMapper refreshTokenMapper;

    // =========================================================
    // 로그인
    // =========================================================

    /**
     * POST /auth/login
     * 아이디/패스워드 검증 후 Access Token + Refresh Token 발급
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginDTO request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword())
            );
            log.info("인증 성공 - userId: {}", request.getUserId());

//            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            LdapResultDTO ldapResultDTO = (LdapResultDTO) authentication.getPrincipal();

            if (!ldapResultDTO.getIsAuth()) {
                log.warn("로그인 실패 - 잘못된 자격증명: {}", request.getUserId());
                return ResponseEntity.status(401).body(ResponseDTO.fail("아이디 또는 패스워드가 올바르지 않습니다."));
            }

            JwtDTO jwtDTO = JwtDTO.builder()
                    .userId(request.getUserId())
                    .userNm("테스트유저")
                    .orgCd("1001")
                    .comp("KT")
                    .role("ROLE_ADMIN")
                    .build();
            log.info("UserDetails 로드 완료 - role: {}", jwtDTO.getRole());

            Map<String, Object> data = new HashMap<>();
            data.put("userId", jwtDTO.getUserId());
            data.put("userNm", jwtDTO.getUserNm());
            data.put("role", jwtDTO.getRole());

            // 2차 인증 우회 대상 여부 확인
            if (isBypassUser(jwtDTO)) {
                log.info("bypass 대상 - 토큰 발급 시작");
                issueTokens(jwtDTO, response);
                log.info("토큰 발급 완료");
                log.info("로그인 성공 (bypass) - userId: {}", jwtDTO.getUserId());
                return ResponseEntity.ok(ResponseDTO.bypass("로그인 성공", data));
            }

            // 일반 대상: 2차 인증 요청
            log.info("2차 인증 필요 - userId: {}", jwtDTO.getUserId());
            return ResponseEntity.ok(ResponseDTO.need2fa("인증번호를 입력해주세요."));

        } catch (BadCredentialsException e) {
            log.warn("로그인 실패 - 잘못된 자격증명: {}", request.getUserId());
            return ResponseEntity.status(401).body(ResponseDTO.fail("아이디 또는 패스워드가 올바르지 않습니다."));
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 - {}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
            return ResponseEntity.status(500).body(ResponseDTO.fail("로그인 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * 2차 인증 우회 대상 여부
     * TODO: 실제 조건으로 교체 (관리자 role, 특정 IP, 내부망 등)
     */
    private boolean isBypassUser(JwtDTO jwtDTO) {
        return "ROLE_ADMIN".equals(jwtDTO.getRole());
    }

    /**
     * Access Token + Refresh Token 발급 및 쿠키 세팅 공통 처리
     */
    private void issueTokens(JwtDTO jwtDTO, HttpServletResponse response) {
        String accessToken = jwtProvider.createAccessToken(jwtDTO);
        String refreshToken = jwtProvider.createRefreshToken(jwtDTO.getUserId());
        String refreshTokenHash = jwtProvider.hashToken(refreshToken);

        refreshTokenMapper.upsert(RefreshTokenDTO.builder()
                .userId(jwtDTO.getUserId())
                .token(refreshTokenHash)
                .expiresAt(LocalDateTime.now().plusSeconds(JwtProvider.REFRESH_EXPIRATION_MS / 1000))
                .build());

        jwtProvider.setAccessTokenCookie(response, accessToken);
        jwtProvider.setRefreshTokenCookie(response, refreshToken);
    }

    // =========================================================
    // Access Token 재발급
    // =========================================================

    /**
     * POST /auth/refresh
     * Refresh Token 으로 새 Access Token 발급
     * Refresh Token 은 HttpOnly Cookie 로 자동 전송됨
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        if (refreshToken == null) {
            return ResponseEntity.status(401).body(ResponseDTO.fail("로그인이 만료되었습니다. 다시 로그인해 주세요."));
        }

        // Refresh Token 서명 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).body(ResponseDTO.fail("유효하지 않은 접근입니다. 다시 로그인해 주세요."));
        }

        // DB 에 저장된 토큰과 비교 (탈취 감지)
        String userId = jwtProvider.getUserIdFromRefreshToken(refreshToken);
        RefreshTokenDTO savedToken = refreshTokenMapper.findByUserId(userId);
        String refreshTokenHash = jwtProvider.hashToken(refreshToken);

        if (savedToken == null || !savedToken.getToken().equals(refreshTokenHash)) {
            log.warn("Refresh Token 불일치 - 탈취 가능성 - userId: {}", userId);
            refreshTokenMapper.deleteByUserId(userId); // 의심스러운 토큰 전부 삭제
            return ResponseEntity.status(401)
                    .body(ResponseDTO.fail("로그인 정보가 유효하지 않습니다. 다시 로그인해 주세요."));
        }

        // 새 Access Token 발급
        // TODO: DB 에서 최신 사용자 정보 조회 후 JwtDTO 구성하는 것을 권장
        JwtDTO jwtDTO = JwtDTO.builder()
                .userId(userId)
                .userNm(savedToken.getUserId()) // TODO: DB 조회로 교체
                .role("ROLE_USER")              // TODO: DB 조회로 교체
                .build();

        String newAccessToken = jwtProvider.createAccessToken(jwtDTO);
        jwtProvider.setAccessTokenCookie(response, newAccessToken);

        log.info("Access Token 재발급 - userId: {}", userId);
        return ResponseEntity.ok(ResponseDTO.success("Access Token이 재발급되었습니다.", null));
    }

    // =========================================================
    // 로그아웃
    // =========================================================

    /**
     * POST /auth/logout
     * Refresh Token DB 삭제 + 쿠키 초기화
     */
    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ResponseDTO> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        if (refreshToken != null) {
            try {
                String userId = jwtProvider.getUserIdFromRefreshToken(refreshToken);
                int deleted = refreshTokenMapper.deleteByUserId(userId);
                log.info("로그아웃 - userId: {}, deletedRefreshTokenRows: {}", userId, deleted);
                log.info("로그아웃 - userId: {}", userId);
            } catch (Exception e) {
                log.warn("로그아웃 중 Refresh Token 파싱 실패 (무시): {}", e.getMessage());
            }
        }

        jwtProvider.clearAuthCookies(response);
        return ResponseEntity.ok(ResponseDTO.success("로그아웃 되었습니다.", null));
    }
}
