package com.kt.ktedu.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/* direct 모두 전환하는 경우 WebConfig 삭제 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 기존 /WebContent 하위에 위치하는 direct 로 접속하는 jsp 정의. /webapp/WEB-INF/views/direct/ 하위로 이동
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/directSample").setViewName("direct/directSample");
        registry.addViewController("/shareLink.do").setViewName("direct/shareLink");
        registry.addViewController("/pageLink.do").setViewName("direct/pageLink");
        registry.addViewController("/nsso_auth.do").setViewName("direct/nsso_auth");
        registry.addViewController("/nsso_return.do").setViewName("direct/nsso_return");
        registry.addViewController("/sso_logon.do").setViewName("direct/sso_logon");
    }
}