/**
 * 공통 헤더 유틸리티
 */

// mobile 기준 width
const MOBILE_WIDTH_BREAKPOINT = 768;

// 브라우저 현재 가로 폭 측정
function getBrowserWidth() {
    return window.innerWidth
        || document.documentElement.clientWidth
        || document.body.clientWidth;
}

// User-Agent 기반 모바일 기기 판별
function checkMobileDevice() {
    const userAgent = navigator.userAgent.toLowerCase();
    return userAgent.includes("mobile")
        || userAgent.includes("android")
        || userAgent.includes("iphone");
}

// 하이브리드 앱(WebView) 환경 판별 함수
function isAppEnvironment() {
    const userAgent = navigator.userAgent.toLowerCase();
    const isAndroidApp = window.Android !== undefined || (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.Android);
    const isIosApp = window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.callbackHandler;
    return isAndroidApp || isIosApp;
}

// 모바일 check
function checkIsMobile() {
    const currentWidth = getBrowserWidth();
    const isMobileDevice = checkMobileDevice();

    return currentWidth < MOBILE_WIDTH_BREAKPOINT || isMobileDevice;
}

/** 화면이 다 그려지기 전에 실행되는 함수 */
(function () {
    /* mobile/web 라다이렉트 처리
    * url 접속 시 화면 가로폭 기준으로 모바일 check & /mobile/m 처리*/
    function redirectTraffic() {
        const currentPath = window.location.pathname;
        console.log("currentPath ::: " + currentPath);
        // 로그인 페이지는 공통
        if (currentPath === "/") {
            return false;
        }

        const isMobile = checkIsMobile();
        const hasMobileUrl = currentPath.includes("/mobile/m/");
        console.log("isMobile ::: " + isMobile);

        // 모바일 환경인데 PC 주소로 들어온 경우 ➡> 모바일로 강제 이동
        if (isMobile && !hasMobileUrl) {
            window.location.href = window._contextPath + "/mobile/m" + currentPath.replace(window._contextPath, "");
            return true;
        }

        // PC 환경인데 모바일 주소로 들어와 있는 경우 ➡> PC로 원복
        if (!isMobile && hasMobileUrl) {
            window.location.href = currentPath.replace("/mobile/m", "");
            return true;
        }

        return false;
    }

    /* 모바일(/mobile/m) | 웹 리다이렉트 처리 */
    const isRedirected = redirectTraffic();

    if (!isRedirected) {
        $(document).ready(function () {
            $(document).ajaxError(function (event, xhr, options, exc) {
                if (xhr.status === 401) {
                    if (options.url.indexOf("/auth/login") !== -1) {
                        return;
                    }

                    alert("로그인 유효시간이 만료되었습니다.\n안전한 이용을 위해 로그인 페이지로 이동합니다.");
                    window.location.href = window._contextPath + "/logout";
                }
            });
        });
    }
})();

/** html 이 다 그려진 후(DOM 완료) 실행되는 함수 */
$(document).ready(function () {

});