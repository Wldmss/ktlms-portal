package com.kt.ktedu.common.util.security;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

/*
 * html 태그 DB 저장 시 변환
 * String safeContent = htmlSanitizer.sanitizeEditorHtml(request.getContent());
 * */
@Component
public class HtmlSanitizer {

    private static final PolicyFactory EDITOR_POLICY =
            Sanitizers.BLOCKS
                    .and(Sanitizers.FORMATTING)
                    .and(Sanitizers.LINKS)
                    .and(Sanitizers.IMAGES)
                    .and(Sanitizers.TABLES)
                    .and(new HtmlPolicyBuilder()
                            .allowElements("figure", "figcaption")
                            .allowUrlProtocols("http", "https", "mailto")
                            .requireRelNofollowOnLinks()
                            .toFactory());

    public String sanitizeEditorHtml(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        return EDITOR_POLICY.sanitize(html);
    }
}