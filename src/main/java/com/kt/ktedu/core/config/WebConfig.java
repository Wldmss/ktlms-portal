package com.kt.ktedu.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.ktedu.core.security.HtmlCharacterEscapes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * View Resolver (JSP)
     */
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setOrder(1);
        return resolver;
    }

    /**
     * Static Resource Mapping
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * Default Servlet (필수)
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * Message Converters (UTF-8 + JSON)
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // String UTF-8
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converters.add(stringConverter);

        // Jackson JSON (XSS escape 포함)
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
        converters.add(jsonConverter);
    }

    /**
     * ObjectMapper (XSS 대응)
     */
    @Bean
    public ObjectMapper customObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 기존 legacy XSS escape 유지
        mapper.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
        return mapper;
    }

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