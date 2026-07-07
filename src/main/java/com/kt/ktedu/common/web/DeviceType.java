package com.kt.ktedu.common.web;

/**
 * 접속 디바이스 종류. 레거시 Spring Mobile 의 Device/DeviceType 을 대체한다.
 * (태블릿은 별도 사이트를 두지 않으므로 WEB 으로 취급한다.)
 * JSP 에서는 {@code ${deviceType.mobile}}, {@code ${deviceType == 'MOBILE'}} 형태로 사용.
 */
public enum DeviceType {
    WEB, MOBILE;

    public boolean isWeb()    { return this == WEB; }
    public boolean isMobile() { return this == MOBILE; }
}
