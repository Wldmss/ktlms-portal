package com.kt.ktedu.common.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 요청 기반 디바이스 판별기. 레거시 CustomLiteDeviceResolver(Spring Mobile) 대체.
 * <p>
 * 판별 신호:
 * 1. 네이티브 앱(genius-app, RN Expo WebView) → {@link #isApp}. UA 마커({@value #APP_UA_MARKER}) 또는
 *    헤더 {@code X-Client-Type: app}. WebView 는 source.headers 가 첫 요청에만 붙으므로 UA 마커가 주 채널이다.
 * 2. 그 외 → User-Agent 파싱 (JWT 와 무관한 요청 단위 판별)
 */
@Component
public class DeviceResolver {

    public static final String APP_CLIENT_HEADER = "X-Client-Type";
    public static final String APP_CLIENT_VALUE = "app";
    // 앱 WebView 의 User-Agent 에 append 하기로 한 마커 (앱 팀과 합의 필요). 모든 요청에 적용되는 신뢰 채널.
    public static final String APP_UA_MARKER = "GeniusApp";

    // 스마트폰 UA 만 MOBILE 로 본다. 태블릿(ipad, android 태블릿 등)은 매칭되지 않아 WEB 으로 처리됨.
    private static final Pattern MOBILE =
            Pattern.compile("(?i)(iphone|ipod|android.*mobile|windows phone|iemobile|blackberry|bb10|opera mini)");

    /**
     * 네이티브 앱(genius-app) 안에서 온 요청인지 여부.
     * 모바일 브라우저와 구분해 앱 전용 UI/동작(헤더 숨김, 네이티브 브리지 등)에 사용.
     */
    public boolean isApp(HttpServletRequest request) {
        if (APP_CLIENT_VALUE.equalsIgnoreCase(request.getHeader(APP_CLIENT_HEADER))) {
            return true;
        }
        String ua = request.getHeader("User-Agent");
        return ua != null && ua.toLowerCase().contains(APP_UA_MARKER.toLowerCase());
    }

    public DeviceType resolve(HttpServletRequest request) {
        // 앱은 UA 와 무관하게 모바일로 취급
        if (isApp(request)) {
            return DeviceType.MOBILE;
        }

        String ua = request.getHeader("User-Agent");
        if (ua == null || ua.isBlank()) {
            return DeviceType.WEB;
        }
        if (MOBILE.matcher(ua).find()) {
            return DeviceType.MOBILE;
        }
        return DeviceType.WEB;
    }
}
