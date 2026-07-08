<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  앱-우선 딥링크 브리지 (LinkController#shareLink).
  1) 네이티브 앱 스킴으로 열기 시도 → 앱이 설치돼 있으면 앱이 뜬다.
  2) 1.5초 내 앱 전환이 안 되면(미설치) /pageLink(웹)로 폴백.
  returnPage 는 서버(LinkController)에서 내부 상대경로로 검증되고 따옴표/꺾쇠/CRLF/백슬래시가 제거된 값이라
  JS 문자열 리터럴에 그대로 넣어도 안전하다. appSchemePrefix/mobilePrefix 는 서버 상수.
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <%@ include file="/WEB-INF/views/common/core/meta.jsp" %>
</head>
<body>
<p style="margin:40px;text-align:center;color:#555;">이동 중입니다...</p>
<script>
    (function () {
        // 서버 검증·정제된 내부 경로 (예: "/educontents/view.do?id=1")
        var returnPage = "${returnPage}";
        var appSchemePrefix = "${appSchemePrefix}";
        var mobilePrefix = "${mobilePrefix}";

        // 앱 스킴: url 쿼리로 넘기므로 return_page 안의 '?' 는 '&' 로 치환
        var appUrl = appSchemePrefix + mobilePrefix + returnPage.replace(/\?/g, "&");
        window.location.href = appUrl;

        // 앱 미설치 폴백: 웹 pageLink 로 이동 (pageLink 가 디바이스별 prefix 처리)
        setTimeout(function () {
            window.location.href = "<c:url value='/pageLink'/>?return_page=" + encodeURIComponent(returnPage);
        }, 1500);
    })();
</script>
</body>
</html>
