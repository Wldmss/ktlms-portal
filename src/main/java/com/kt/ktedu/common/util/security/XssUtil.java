package com.kt.ktedu.common.util.security;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class XssUtil {

    private XssUtil() {
        // 인스턴스화 방지
    }

    /**
     * 웹 에디터 본문용 정밀 XSS 필터링
     * 일반적인 본문 태그(p, b, img, a 등)는 살리고 악성 스크립트만 완전 삭제
     */
    public static String cleanEditorHtml(String content) {
        if (content == null) return null;
        return Jsoup.clean(content, Safelist.relaxed());
    }

    /**
     * 텍스트 내부에서 모든 HTML 태그를 100% 제거하고 순수 글자만 추출
     * 게시판 목록에서 본문 앞부분 미리보기(Text Preview)나 RSS 피드 생성 시 필수 유틸
     */
    public static String stripAllHtml(String content) {
        if (content == null) return "";

        // 아무것도 허용하지 않는 Safelist.none()을 먹이면 태그만 싹 지워짐
        String clean = Jsoup.clean(content, Safelist.none());

        // 엔티티 문자들(&amp;, &lt; 등)까지 최종적으로 깔끔한 순수 일반 문자로 디코딩
        return Jsoup.parse(clean).text();
    }
}