<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>KT학습플랫폼 지니어스</title>

<%-- src/main/webapp/--%>
<c:set var="_contextPath" value="${pageContext.request.contextPath}" scope="application"/>
<%-- src/main/webapp/resources/webjars --%>
<c:set var="_webjars" value="${_contextPath}/webjars" scope="application"/>

<%-- icon --%>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/icons/favicon.ico" type="image/x-icon">
<link rel="icon" href="${pageContext.request.contextPath}/resources/icons/favicon.ico" type="image/x-icon">

<%-- 공통 css --%>
<link rel="stylesheet" href="<c:url value='/resources/css/common.min.css' />">
<link rel="stylesheet" href="<c:url value='/resources/css/header.min.css' />">

<%-- 공통 라이브러리 --%>
<script src="${_webjars}/jquery/3.7.1/jquery.min.js"></script>
<script src="${_webjars}/bootstrap/5.3.8/js/bootstrap.min.js"></script>

<%-- 공통 모듈 --%>
<%@ include file="/WEB-INF/views/common/util/loading.jsp" %>    <%-- loading bar --%>
<%@ include file="/WEB-INF/views/common/util/alert.jsp" %>      <%-- alert box --%>
<%@ include file="/WEB-INF/views/common/util/modal.jsp" %>      <%-- modal box --%>
<%@ include file="/WEB-INF/views/common/util/snackbar.jsp" %>   <%-- snackbar --%>

<%-- 공통 script--%>
<script type="text/javascript">
    window._contextPath = "${_contextPath}";
    window._accessToken = "${sessionScope.accessToken}";
</script>

<%-- 공통 js--%>
<script src="<c:url value='/resources/js/common/header.min.js' />"></script>
<script src="<c:url value='/resources/js/common/common.min.js' />"></script>
<script src="<c:url value='/resources/js/common/common-ajax.min.js' />"></script>
<script src="<c:url value='/resources/js/common/formatter.min.js' />"></script>