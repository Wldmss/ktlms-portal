package com.kt.ktedu.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/* .do 로 들어오는 경우 .do 제거 처리 */
public class UrlSuffixRedirectFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        if (uri.endsWith(".do")) {
            // .do를 떼어낸 깨끗한 주소 추출
            String cleanUri = uri.substring(0, uri.length() - 3);

            // 파라미터(?id=test 등) 복구
            String queryString = httpRequest.getQueryString();
            if (queryString != null) {
                cleanUri += "?" + queryString;
            }

            httpResponse.sendRedirect(cleanUri);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}