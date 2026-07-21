package com.kt.ktedu.core.config;

import com.kt.ktedu.common.web.CommandMapArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

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
}
