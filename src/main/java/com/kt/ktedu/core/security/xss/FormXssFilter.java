package com.kt.ktedu.core.security.xss;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;

/* lucy xss 대신 from 필터링 - 그 외는 HtmlCharacterEscapes 로 처리. 오래된 코드를 위해 구현 */
public class FormXssFilter implements Filter {

    // 와일드플라이 컨테이너의 안정적인 라이프사이클 관리를 위해 init 명시
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 필터 초기화 로직 (필요 시)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 일반 Form 데이터가 들어올 때 특수문자를 바꿔치기하는 래퍼 클래스로 감쌈
        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
    }

    // 서버 언배포/톰캣 종료 시 안전한 자원 해제를 위해 destroy 명시
    @Override
    public void destroy() {
        // 필터 소멸 로직 (필요 시)
    }

    // 파라미터 요청이 들어올 때 실시간 체크
    private static class XssRequestWrapper extends HttpServletRequestWrapper {
        public XssRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            return cleanXss(super.getParameter(name));
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) return null;
            for (int i = 0; i < values.length; i++) {
                values[i] = cleanXss(values[i]);
            }
            return values;
        }

        // 핵심 변환 로직 (Lucy 코드 순수 자바 코드로 구현)
        private String cleanXss(String value) {
            if (value == null) return null;
            return value.replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("'", "&#39;")
                    .replaceAll("\"", "&quot;")
                    .replaceAll("\\(", "&#40;")
                    .replaceAll("\\)", "&#41;");
        }
    }
}