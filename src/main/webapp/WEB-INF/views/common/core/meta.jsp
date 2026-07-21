<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- 모바일 여부 = 현재 URL 경로(/mobile/m) 기준. (width 판정은 클라이언트 header.js 가 /mobile/m 로 리다이렉트) --%>
<c:set var="isMobilePath" value="${fn:contains(pageContext.request.requestURI, '/mobile/m')}"/>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>KT학습플랫폼 지니어스</title>

<%-- icon --%>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/icons/favicon.ico" type="image/x-icon">
<link rel="icon" href="${pageContext.request.contextPath}/resources/icons/favicon.ico" type="image/x-icon">

<%-- 공통 css --%>
<link rel="stylesheet" href="<c:url value='/resources/css/common/base.min.css' />">
<link rel="stylesheet" href="<c:url value='/resources/css/common/common-ui.min.css' />">
<link rel="stylesheet" href="<c:url value='/resources/css/common/common.min.css' />">

<%-- device 분기: 모바일(/mobile/m)/웹 CSS --%>
<c:choose>
    <c:when test="${isMobilePath}">
        <link rel="stylesheet" href="<c:url value='/resources/css/mobile/mobile.min.css' />">
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" href="<c:url value='/resources/css/web/web.min.css' />">
    </c:otherwise>
</c:choose>

<%-- 전역 변수 --%>
<script nonce="${cspNonce}">
    window._contextPath = "${pageContext.request.contextPath}";
    window._isMobile = ${isMobilePath};
    window._mainUrl = "${mainUrl}";

    // contextPath + (모바일이면 /mobile/m) prefix 를 붙여 전체 URL 생성
    window.resolveUrl = function (path) {
        var ctx = window._contextPath || "";
        var mob = window._isMobile ? "/mobile/m" : "";
        if (!path) return ctx + mob;
        if (/^(https?:)?\/\//.test(path)) return path;      // 절대 URL
        if (path.charAt(0) !== "/") path = "/" + path;

        // contextPath 중복 방지: 이미 붙어있으면 그 뒤 경로만 사용
        if (ctx && (path === ctx || path.indexOf(ctx + "/") === 0)) {
            path = path.substring(ctx.length) || "/";
        }
        // 모바일 prefix 중복 방지
        if (mob && (path === "/mobile/m" || path.indexOf("/mobile/m/") === 0)) {
            mob = "";
        }
        return ctx + mob + path;
    };
    window.getPreURL = function () {
        return (window._contextPath || "") + (window._isMobile ? "/mobile/m" : "");
    };
</script>