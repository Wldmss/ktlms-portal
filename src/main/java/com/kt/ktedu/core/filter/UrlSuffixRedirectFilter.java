package com.kt.ktedu.core.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/*
 * 레거시 URL 접미사(.do, .jsp)를 떼고 302 리다이렉트.
 */
public class UrlSuffixRedirectFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        // 접미사를 떼어낸 깨끗한 주소 추출
        String cleanUri = null;
        if (uri.endsWith(".do")) {
            cleanUri = uri.substring(0, uri.length() - ".do".length());
        } else if (uri.endsWith(".jsp")) {
            cleanUri = uri.substring(0, uri.length() - ".jsp".length());
        }

        if (cleanUri != null) {
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