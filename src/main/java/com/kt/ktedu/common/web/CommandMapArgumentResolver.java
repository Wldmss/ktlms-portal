package com.kt.ktedu.common.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

/**
 * 컨트롤러 인자로 선언된 {@link CommandMap} 을 요청 데이터로 자동 채워주는 리졸버.
 * {@code WebConfig.addArgumentResolvers} 에 등록되어 전역 동작한다.
 * <p>
 * 동작 순서 (한 요청당):
 * <ol>
 *   <li>컨트롤러 메서드 인자 타입이 {@link CommandMap} 이면 이 리졸버가 잡는다({@link #supportsParameter}).</li>
 *   <li>빈 {@code CommandMap} 을 만든 뒤,</li>
 *   <li><b>요청 파라미터</b>(query string / form)를 모두 담는다. 값이 하나면 String, 여러 개면 String[].</li>
 *   <li>Content-Type 이 {@code application/json} 이면 <b>요청 바디(JSON)</b>를 Jackson 으로 파싱해 추가로 담는다.
 *       (portal common-ajax.js 는 POST 를 JSON 으로 보내므로 이 경로로 채워진다.)</li>
 *   <li>완성된 {@code CommandMap} 을 컨트롤러 인자로 전달.</li>
 * </ol>
 *
 * <p><b>레거시와 달라진 점 — 세션을 주입하지 않는다.</b> 레거시 리졸버는 세션 사용자정보를 {@code s_} 접두어로
 * 함께 넣었지만, 그 결합은 session → JWT 이관으로 제거했다. 로그인 사용자 정보는 {@code SecurityUtil} 로 얻는다.</p>
 *
 * <p>또한 값에 XSS escape 를 걸지 않는다. 저장 시엔 {@code HtmlSanitizer}, 출력 시엔 {@code <c:out>} 으로
 * 처리하는 것이 portal 정책이므로, 여기서 escape 하면 이중 처리가 된다.</p>
 */
public class CommandMapArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger log = LoggerFactory.getLogger(CommandMapArgumentResolver.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return CommandMap.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        CommandMap commandMap = new CommandMap();

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return commandMap;
        }

        // 1) 요청 파라미터 (query string / form) — 값 1개면 String, 여러 개면 String[]
        request.getParameterMap().forEach((key, values) -> {
            if (values == null || values.length == 0) {
                return;
            }
            commandMap.put(key, values.length == 1 ? values[0] : values);
        });

        // 2) JSON 바디 (application/json) — Jackson 으로 파싱해 병합
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            try {
                Map<String, Object> body = OBJECT_MAPPER.readValue(request.getInputStream(), Map.class);
                commandMap.putAll(body);
            } catch (Exception e) {
                // 빈 바디 / 비 JSON / 이미 소비된 스트림 등은 무시 (파라미터만으로 처리)
                log.debug("CommandMap JSON body parse skipped: {}", e.getMessage());
            }
        }

        return commandMap;
    }
}
