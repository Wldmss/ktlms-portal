<%-- genius 모바일 공통 top (mobileLayout/Cont/Search/ExamCourse/ExamCourseLnb 통합) --%>
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
    <%-- 서브페이지용 GNB (뒤로가기/현재 페이지 타이틀) --%>
    <%@ include file="/WEB-INF/views/common/tiles/mobile/KT_GNB_Mobile.jsp" %>

    <%-- 콘텐츠 래퍼 class: 기본 'l-content new2023', 페이지에서 <c:set var="contentClass" .../> 로 override --%>
    <div id="content" class="${empty contentClass ? 'l-content new2023' : contentClass}"><%--content 시작--%>
