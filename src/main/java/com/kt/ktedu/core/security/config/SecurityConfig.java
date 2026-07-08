package com.kt.ktedu.core.security.config;

import com.kt.ktedu.core.security.auth.CustomLdapAuthenticationProvider;
import com.kt.ktedu.core.security.auth.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CookieSecurityProperties cookieSecurityProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestHandler csrfTokenRequestHandler = new SpaCsrfTokenRequestHandler();

        CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfRepository.setCookieCustomizer(cookie -> cookie
                .path("/")
                .sameSite(cookieSecurityProperties.sameSite())
                .secure(cookieSecurityProperties.isSecure())
        );

        http
                .csrf(csrf -> csrf
                                .csrfTokenRepository(csrfRepository)
                                .csrfTokenRequestHandler(csrfTokenRequestHandler)
                        // 외부 서버/앱에서 CSRF 헤더를 못 붙이는 공개 API 예외 처리
                        // .ignoringRequestMatchers("/api/health")
                )
                .addFilterBefore(new FetchMetadataFilter(), CsrfFilter.class)
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 활성화
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(AbstractHttpConfigurer::disable) // 기본 LogoutFilter 비활성화 (로그아웃은 /auth/logout 에서 자체 처리)
                // 보안 헤더
                .headers(headers -> headers
                        // 운영에서 실제 적용되는 완화 정책
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                "script-src 'self' 'unsafe-inline'; " +
                                                "style-src 'self' 'unsafe-inline'; " +
                                                "img-src 'self' data: blob:; " +
                                                "font-src 'self' data:; " +
                                                "connect-src 'self'; " +
                                                "object-src 'none'; " +
                                                "base-uri 'self'; " +
                                                "frame-ancestors 'self'; " +
                                                "form-action 'self'"
                                )
                        )

                        // 나중에 목표로 삼을 엄격 정책은 별도 Report-Only 헤더로 관찰
                        .addHeaderWriter(new StaticHeadersWriter(
                                "Content-Security-Policy-Report-Only",
                                "default-src 'self'; " +
                                        "script-src 'self'; " +
                                        "style-src 'self'; " +
                                        "img-src 'self' data: blob:; " +
                                        "font-src 'self' data:; " +
                                        "connect-src 'self'; " +
                                        "object-src 'none'; " +
                                        "base-uri 'self'; " +
                                        "frame-ancestors 'self'; " +
                                        "form-action 'self'"
                        ))
                        .frameOptions(frame -> frame.sameOrigin())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                        )
                        .addHeaderWriter(new StaticHeadersWriter(
                                "Permissions-Policy",
                                "geolocation=(), camera=(), microphone=()"
                        ))
                )
                // URL 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.PUT, "/**").denyAll()
                        .requestMatchers(HttpMethod.DELETE, "/**").denyAll()
                        .requestMatchers(HttpMethod.PATCH, "/**").denyAll()
                        .requestMatchers(HttpMethod.TRACE, "/**").denyAll()
                        .requestMatchers("/robots.txt").permitAll()
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
                        .requestMatchers(
                                "/directSample",
                                "/shareLink", "/shareLink.do",
                                "/pageLink", "/pageLink.do",
                                "/nsso_auth", "/nsso_auth.do",
                                "/nsso_return", "/nsso_return.do",
                                "/sso_logon", "/sso_logon.do"
                        ).permitAll()
                        // 그 외 모든 요청 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 인증/인가 실패 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) -> {
                            // 미인증 접근 처리
                            if (isApiRequest(request)) {
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
                            if (isApiRequest(request)) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json;charset=UTF-8");

                                if (e instanceof CsrfException) {
                                    response.getWriter().write("{\"result\":\"CSRF_DENIED\",\"message\":\"요청 보안 토큰이 유효하지 않습니다.\"}");
                                    return;
                                }

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

        configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-XSRF-TOKEN",
                "X-Requested-With",
                "Accept"
        ));
        configuration.setExposedHeaders(List.of("Content-Disposition"));
        configuration.setAllowCredentials(true); // 인증 정보 허용 (쿠키, 인증 헤더)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 정책 적용
        return source;
    }

    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public AuthenticationManager authenticationManager(CustomLdapAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

        // 임시: 어떤 비밀번호든 통과 (DB 유저 데이터 없는 개발 초기 단계)
//        return new PasswordEncoder() {
//            @Override
//            public String encode(CharSequence raw) {
//                return raw.toString();
//            }
//
//            @Override
//            public boolean matches(CharSequence raw, String encoded) {
//                return true;
//            }
//        };
    }

    /* csrf filter */
    private static final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
        private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
        private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request,
                           HttpServletResponse response,
                           Supplier<CsrfToken> csrfToken
        ) {
            this.xor.handle(request, response, csrfToken);
            csrfToken.get();
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            String headerValue = request.getHeader(csrfToken.getHeaderName());

            return StringUtils.hasText(headerValue)
                    ? this.plain.resolveCsrfTokenValue(request, csrfToken)
                    : this.xor.resolveCsrfTokenValue(request, csrfToken);
        }
    }

    private static final class CsrfCookieFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain
        ) throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

            if (csrfToken != null) {
                csrfToken.getToken(); // XSRF-TOKEN 쿠키 생성을 강제로 트리거
            }

            filterChain.doFilter(request, response);
        }
    }

    private static final class FetchMetadataFilter extends OncePerRequestFilter {

        private static final List<String> SAFE_METHODS =
                List.of("GET", "HEAD", "OPTIONS", "TRACE");

        private static final List<String> EXCLUDED_PATH_PREFIXES =
                List.of(
                        "/api/entra-sso/",
                        "/nsso_auth",
                        "/nsso_auth.do",
                        "/nsso_return",
                        "/nsso_return.do",
                        "/sso_logon",
                        "/sso_logon.do"
                );

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain
        ) throws ServletException, IOException {
            String secFetchSite = request.getHeader("Sec-Fetch-Site");

            if ("cross-site".equals(secFetchSite)
                    && !SAFE_METHODS.contains(request.getMethod())
                    && !isExcludedPath(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            filterChain.doFilter(request, response);
        }

        private boolean isExcludedPath(HttpServletRequest request) {
            String uri = request.getRequestURI();
            String contextPath = request.getContextPath();
            String path = uri.substring(contextPath.length());

            return EXCLUDED_PATH_PREFIXES.stream().anyMatch(path::startsWith);
        }
    }

    /* api request check */
    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());
        String accept = request.getHeader("Accept");

        return path.startsWith("/api/")
                || path.startsWith("/auth/")
                || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
                || (accept != null && accept.contains("application/json"))
                || uri.contains("Ajax")
                || uri.contains("Json");
    }
}
