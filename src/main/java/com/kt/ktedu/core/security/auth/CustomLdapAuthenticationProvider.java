package com.kt.ktedu.core.security.auth;

import com.kt.ktedu.auth.ldap.dto.LdapResultDTO;
import com.kt.ktedu.auth.ldap.dto.LoginDTO;
import com.kt.ktedu.auth.ldap.service.LdapService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomLdapAuthenticationProvider implements AuthenticationProvider {

    private final LdapService ldapService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        LoginDTO loginDTO = LoginDTO.builder()
                .userId(username)
                .password(password)
                .build();

        // LDAP API 호출
        LdapResultDTO result = ldapService.authenticate(loginDTO);
        if (result == null || !Boolean.TRUE.equals(result.getIsAuth())) {
            throw new BadCredentialsException("Invalid LDAP credentials");
        }

        // 인증 성공 시 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(
                result,
                password,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}