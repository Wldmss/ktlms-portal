package com.kt.ktedu.core.config;

import com.kt.ktedu.auth.ldap.CustomLdapAuthenticationProvider;
import com.kt.ktedu.auth.login.service.CustomUserDetailsService;
import com.kt.ktedu.auth.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    //    private final CustomUserDetailsService customUserDetailsService;
    private final CustomLdapAuthenticationProvider customLdapAuthenticationProvider;

    public SecurityConfig(CustomLdapAuthenticationProvider customLdapAuthenticationProvider,
                          JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.customLdapAuthenticationProvider = customLdapAuthenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 활성화
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 불필요
                        .requestMatchers(
                                "/",
                                "/login",
                                "/logout",
                                "/auth/**",
                                "/api/test/**",
                                "/api/health",
                                "/api/entra-sso/**",
                                "/api/key/**"
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
                                // 원래 요청 URL 을 redirect 파라미터로 저장
                                String redirectUrl = request.getRequestURI();
                                String query = request.getQueryString();
                                if (query != null) redirectUrl += "?" + query;
                                response.sendRedirect(request.getContextPath() + "/?redirect="
                                        + java.net.URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8));
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ktlms portal
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("https://ktedu.kt.com");             // 운영 지니어스
        configuration.addAllowedOrigin("https://dev.ktedu.kt.com");         // 개발 지니어스
        configuration.addAllowedOrigin("https://dev.ktedu.kt.com:1443");
        configuration.addAllowedOrigin("https://dev.ktedu.kt.com:2443");

        // ktlms admin
        configuration.addAllowedOrigin("http://localhost:8081");
        configuration.addAllowedOrigin("https://lms.ktedu.kt.com");         // 운영 lms
        configuration.addAllowedOrigin("https://dev.ktedu.kt.com:3443");    // 개발 lms
        configuration.addAllowedOrigin("https://dev.ktedu.kt.com:4443");

        // ktexam
        configuration.addAllowedOrigin("http://localhost:8082");            // ktexam
        configuration.addAllowedOrigin("http://localhost:3000");            // ktexam local front
        configuration.addAllowedOrigin("https://dev.exam.ktedu.kt.com");    // 개발 front
        configuration.addAllowedOrigin("https://exam.ktedu.kt.com");        // 운영 front

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보 허용 (쿠키, 인증 헤더)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 정책 적용
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

        /*DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);*/
    }

    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customLdapAuthenticationProvider);
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
