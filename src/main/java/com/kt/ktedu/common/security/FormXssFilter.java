package com.kt.ktedu.common.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;

/* lucy xss 대신 from 필터링 - 그 외는 HtmlCharacterEscapes 로 처리. 오래된 코드를 위해 구현 */
public class FormXssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 일반 Form 데이터가 들어올 때 특수문자를 바꿔치기하는 래퍼 클래스로 감쌈
        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
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
                    .replaceAll("\"", "&quot;");
        }
    }
}