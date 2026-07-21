<%-- genius web 공통 top (tiles pageLayout/main/miniLecture/myCourse 통합) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <%-- 기존 defaultCssLayout / defaultJsLayout 대체 --%>
    <%@ include file="/WEB-INF/views/common/core/meta.jsp" %>

    <%-- genius 레거시 레이아웃 CSS --%>
    <%@ include file="/WEB-INF/views/common/tiles/web/tiles-css.jsp" %>

    <%@ include file="/WEB-INF/views/common/core/resources.jsp" %>

</head>
<body class="font-normal"><%--body 시작--%>
<main class="main-wrapper">
    <%-- GNB: 모든 화면 공통 --%>
    <%@ include file="/WEB-INF/views/common/tiles/web/gnbLayout.jsp" %>

    <%-- subGNB(서브 비주얼): sub-page만. 페이지에서 <c:set var="subGNB" .../> 로 경로 전달 시에만 --%>
    <c:if test="${not empty subGNB}">
        <jsp:include page="${subGNB}"/>
    </c:if>

    <%-- 콘텐츠 래퍼 class: 기본 content, 페이지에서 <c:set var="contentClass" .../> 로 override --%>
    <div class="${empty contentClass ? 'content' : contentClass}"><%--content 시작--%>
