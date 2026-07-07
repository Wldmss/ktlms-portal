package com.kt.ktedu.core.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieSecurityProperties {

    private final boolean secure;

    public CookieSecurityProperties(
            @Value("${security.cookie.secure:true}") boolean secure
    ) {
        this.secure = secure;
    }

    public boolean isSecure() {
        return secure;
    }

    public String sameSite() {
        return secure ? "None" : "Lax";
    }
}