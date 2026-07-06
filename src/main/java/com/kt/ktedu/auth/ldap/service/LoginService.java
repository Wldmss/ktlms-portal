package com.kt.ktedu.auth.ldap.service;

import com.kt.ktedu.auth.ldap.dto.LdapResultDTO;
import com.kt.ktedu.auth.ldap.dto.LoginDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${url.domain}")
    private String appDomain;

    private final AuthenticationManager authenticationManager;
//    private final RsaKeyService rsaKeyService;

    // LDAP Login
    public LdapResultDTO ldapLogin(LoginDTO loginDTO) throws Exception {
        if (loginDTO.getEncryptedPassword() != null) {
//            String decryptedPassword = rsaKeyService.getDecryptValue(loginDTO.getKeySeq(), loginDTO.getEncryptedPassword());
            String decryptedPassword = loginDTO.getEncryptedPassword();
            loginDTO.setPassword(decryptedPassword);
        }

        if (loginDTO.getPassword() != null) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUserId(), loginDTO.getPassword())
            );

            return (LdapResultDTO) authentication.getPrincipal();
        } else {
            return LdapResultDTO.getLdapFailResult("비밀번호가 올바르지 않습니다.");
        }
    }

    // email 로 user_id 가져오기
    public String getUserIdFromEmail(String email) {
        return "Y";
//        return examUserService.getUserIdByEmail(email);
    }

    // 대량평가에 존재하는 계정인지 확인
    public String getUserIdExists(String userId) {
        return "Y";
//        return examUserService.getUserIdExists(userId);
    }

    // 인증 실패 리턴
    public void handleLoginFail(HttpServletResponse response, String message) throws IOException {
        String redirectUri = "/sso-login?code=E";

        if (message != null) {
            redirectUri += ("&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
        }

        response.sendRedirect(appDomain + redirectUri);
    }
}