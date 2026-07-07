package com.kt.ktedu.common.util.security;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

/**
 * XSS 방어용 HTML 처리 유틸.
 * <ul>
 *   <li>{@link #sanitizeEditorHtml} : 에디터 HTML(허용 태그만 남기고 위험 태그/속성 제거) — DB 저장 시 사용</li>
 *   <li>{@link #escapeText} : 일반 텍스트를 HTML 엔티티로 이스케이프 — 레거시 htmlSpecialChar/shieldXSS 대체</li>
 *   <li>{@link #stripTags} : 모든 태그 제거 후 순수 텍스트 반환 — 목록 미리보기/검색/알림 문구용</li>
 * </ul>
 * 주의: 화면 출력은 JSP {@code <c:out>}(escapeXml 기본) 로도 충분하다. 이 유틸은 저장 전 처리나
 * 서버에서 문자열을 직접 조립/가공할 때 사용한다. 전역 입력 필터(레거시 Lucy 방식)는 두지 않는다.
 */
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

    /**
     * 에디터 HTML sanitize. 허용된 태그/속성만 남기고 script/on* 등 위험 요소를 제거한다.
     * (에디터 본문을 DB 저장하기 전에 사용)
     * <pre>String safe = htmlSanitizer.sanitizeEditorHtml(dto.getContent());</pre>
     */
    public String sanitizeEditorHtml(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        return EDITOR_POLICY.sanitize(html);
    }

    /**
     * 일반 텍스트 HTML 이스케이프 ({@code < > & " '}). 태그를 전혀 허용하지 않는 값에 사용.
     * 레거시 {@code htmlSpecialChar}/{@code shieldXSS} 의 1:1 대체.
     * <p>화면 출력만이면 JSP {@code <c:out>} 이 더 낫다. 서버에서 문자열을 직접 조립하거나
     * innerHTML 로 들어갈 값을 저장/응답하기 전 방어용으로 사용한다.
     * <pre>escapeText("<b>a&b</b>") → "&lt;b&gt;a&amp;b&lt;/b&gt;"</pre>
     */
    public String escapeText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 모든 HTML 태그를 제거하고 순수 텍스트만 반환. 엔티티는 원래 문자로 복원한다.
     * (목록 미리보기, 검색 색인, 알림/메일 문구 등 태그가 없어야 하는 곳에 사용)
     * <pre>stripTags("<p>안녕&nbsp;<b>하세요</b></p>") → "안녕 하세요"</pre>
     */
    public String stripTags(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        // Safelist.none() 로 태그 제거 후, 남은 엔티티(&nbsp; 등)를 실제 문자로 되돌림
        String noTags = Jsoup.clean(html, org.jsoup.safety.Safelist.none());
        return Parser.unescapeEntities(noTags, false);
    }
}