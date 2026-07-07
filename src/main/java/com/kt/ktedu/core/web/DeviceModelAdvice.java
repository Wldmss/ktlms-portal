package com.kt.ktedu.core.web;

import com.kt.ktedu.common.web.DeviceResolver;
import com.kt.ktedu.common.web.DeviceType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 모든 JSP 에서 현재 접속 디바이스/앱 여부를 참조할 수 있게 노출.
 * 레거시 CustomLiteDeviceResolver + preUrl(/mobile/m) 분기를 대체한다.
 * <ul>
 *   <li>{@code ${deviceType}} : WEB / MOBILE — 예 {@code <c:if test="${deviceType.mobile}">}</li>
 *   <li>{@code ${isApp}} : 네이티브 앱(genius-app) 안 여부 — 예 {@code <c:if test="${!isApp}">웹/브라우저 전용</c:if>}</li>
 * </ul>
 */
@ControllerAdvice
@RequiredArgsConstructor
public class DeviceModelAdvice {

    private final DeviceResolver deviceResolver;

    @ModelAttribute("deviceType")
    public DeviceType deviceType(HttpServletRequest request) {
        return deviceResolver.resolve(request);
    }

    @ModelAttribute("isApp")
    public boolean isApp(HttpServletRequest request) {
        return deviceResolver.isApp(request);
    }
}
