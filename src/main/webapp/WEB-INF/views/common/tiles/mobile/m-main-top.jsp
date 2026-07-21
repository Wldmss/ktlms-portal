<%-- genius 모바일 메인 top (mobileMainLayout) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <%-- 기존 defaultMobileCssLayout / defaultMobileJsLayout 대체 --%>
    <%@ include file="/WEB-INF/views/common/core/meta.jsp" %>

    <%-- genius 레거시 모바일 레이아웃 CSS --%>
    <%@ include file="/WEB-INF/views/common/tiles/mobile/m-css.jsp" %>

    <%@ include file="/WEB-INF/views/common/core/resources.jsp" %>

</head>
<body class="font-normal"><%--body 시작--%>
<div class="l-wrap new2023-wrap">
    <%-- 메인/전체메뉴용 풀 GNB (햄버거 전체메뉴 + 알림 + 사용자정보) --%>
    <%@ include file="/WEB-INF/views/common/tiles/mobile/KT_GNB_Mobile_Main.jsp" %>

    <div id="content" class="l-content new2023 mobile-main"><%--content 시작--%>
