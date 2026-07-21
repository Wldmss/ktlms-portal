package com.kt.ktedu.auth.sso.entra.config;

import com.core.sso.oidc.service.OidcClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/* TODO nexus 연결 후 수정 */
//@ComponentScan(basePackages = {"com.core.sso.cmm"})
//@Configuration
public class EntraOidcConfig {
    @Bean
    public OidcClientService getOidcClientService() {
        return new OidcClientService();
    }
}