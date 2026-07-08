package com.kt.ktedu.core.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.SessionCookieConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSESSIONID 쿠키 정책을 JWT/CSRF 쿠키 정책과 동일한 SECURITY_COOKIE_SECURE 기준으로 맞춘다.
 *
 * <p>Servlet listener 는 Spring context 생성 전에 실행되므로 @Value 대신 system property/env 를 직접 읽는다.</p>
 */
public class SessionCookieConfigListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(SessionCookieConfigListener.class);
    private static final String PROPERTY_KEY = "security.cookie.secure";
    private static final String ENV_KEY = "SECURITY_COOKIE_SECURE";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        boolean secure = resolveSecure();
        SessionCookieConfig cookieConfig = event.getServletContext().getSessionCookieConfig();
        cookieConfig.setHttpOnly(true);
        cookieConfig.setSecure(secure);
        log.info("JSESSIONID cookie config initialized: httpOnly=true, secure={}", secure);
    }

    private boolean resolveSecure() {
        String value = System.getProperty(PROPERTY_KEY);
        if (value == null || value.isBlank()) {
            value = System.getenv(ENV_KEY);
        }
        if (value == null || value.isBlank()) {
            return true;
        }
        return Boolean.parseBoolean(value);
    }
}
