package com.kt.ktedu.auth.security;

import com.kt.ktedu.auth.jwt.JwtDTO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security UserDetails 구현체
 * JwtDTO 를 래핑하여 인증 정보를 보유
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final JwtDTO  jwtDTO;
    private final String  password;

    public CustomUserDetails(JwtDTO jwtDTO, String password) {
        this.jwtDTO   = jwtDTO;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // role 값이 "ROLE_" prefix 없으면 붙여줌
        String role = jwtDTO.getRole();
        if (role != null && !role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        return List.of(new SimpleGrantedAuthority(role != null ? role : "ROLE_USER"));
    }

    @Override public String getPassword()                 { return password; }
    @Override public String getUsername()                 { return jwtDTO.getUserId(); }
    @Override public boolean isAccountNonExpired()        { return true; }
    @Override public boolean isAccountNonLocked()         { return true; }
    @Override public boolean isCredentialsNonExpired()    { return true; }
    @Override public boolean isEnabled()                  { return true; }
}
