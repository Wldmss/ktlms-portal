package com.kt.ktedu.core.config;

import com.kt.ktedu.common.web.CommandMapArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/* direct 모두 전환하는 경우 WebConfig 삭제 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 컨트롤러 인자로 선언된 CommandMap 을 요청 파라미터로 자동 채워주는 리졸버 등록.
     * (레거시 CommandMap 컨트롤러 무수정 이관용 브릿지 — CommandMap 참고)
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CommandMapArgumentResolver());
    }

    /**
     * 기존 /WebContent 하위에 위치하는 direct 로 접속하는 jsp 정의. /webapp/WEB-INF/views/direct/ 하위로 이동
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/directSample").setViewName("direct/directSample");
        registry.addViewController("/shareLink").setViewName("direct/shareLink");
        registry.addViewController("/shareLink.do").setViewName("direct/shareLink");
        registry.addViewController("/pageLink").setViewName("direct/pageLink");
        registry.addViewController("/pageLink.do").setViewName("direct/pageLink");
        registry.addViewController("/nsso_auth").setViewName("direct/nsso_auth");
        registry.addViewController("/nsso_auth.do").setViewName("direct/nsso_auth");
        registry.addViewController("/nsso_return").setViewName("direct/nsso_return");
        registry.addViewController("/nsso_return.do").setViewName("direct/nsso_return");
        registry.addViewController("/sso_logon").setViewName("direct/sso_logon");
        registry.addViewController("/sso_logon.do").setViewName("direct/sso_logon");
    }
}
