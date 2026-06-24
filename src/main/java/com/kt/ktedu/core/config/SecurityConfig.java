package com.kt.ktedu.core.config;

import com.kt.ktedu.auth.login.service.CustomUserDetailsService;
import com.kt.ktedu.core.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 불필요
                        .requestMatchers(
                                "/",
                                "/login",
                                "/logout",
                                "/auth/**"
                        ).permitAll()
                        // 정적 리소스
                        .requestMatchers("/resources/**", "/webjars/**", "/favicon.ico").permitAll()
                        // 직접 접근 뷰
                        .requestMatchers("/directSample", "/shareLink.do",
                                "/pageLink.do", "/nsso_auth.do", "/nsso_return.do",
                                "/sso_logon.do").permitAll()
                        // 그 외 모든 요청 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 인증/인가 실패 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) -> {
                            // 미인증 접근 처리
                            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
                            if (isAjax) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"result\":\"UNAUTHORIZED\",\"message\":\"로그인이 필요합니다.\"}");
                            } else {
                                response.sendRedirect(request.getContextPath() + "/");
                            }
                        })
                        .accessDeniedHandler((request, response, e) -> {
                            // 권한 없음 처리
                            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
                            if (isAjax) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"result\":\"FORBIDDEN\",\"message\":\"접근 권한이 없습니다.\"}");
                            } else {
                                response.sendRedirect(request.getContextPath() + "/");
                            }
                        })
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO: 개발 완료 후 아래 주석 해제 후 임시 코드 제거
        // return new BCryptPasswordEncoder();

        // 임시: 어떤 비밀번호든 통과 (DB 유저 데이터 없는 개발 초기 단계)
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence raw) {
                return raw.toString();
            }

            @Override
            public boolean matches(CharSequence raw, String encoded) {
                return true;
            }
        };
    }
}
