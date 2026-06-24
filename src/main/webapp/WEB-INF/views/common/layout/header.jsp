<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>KT학습플랫폼 지니어스</title>

<%-- jquery --%>
<script src="${pageContext.request.contextPath}/webjars/jquery/3.7.1/jquery.min.js"></script>

<%-- icon --%>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/icons/favicon.ico" type="image/x-icon">
<link rel="icon" href="${pageContext.request.contextPath}/resources/icons/favicon.ico" type="image/x-icon">

<%-- 공통 css --%>
<link rel="stylesheet" href="<c:url value='/resources/css/common/common-ui.min.css' />">
<link rel="stylesheet" href="<c:url value='/resources/css/common/common.min.css' />">
<link rel="stylesheet" href="<c:url value='/resources/css/common/header.min.css' />">

<%-- flatpickr--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/vendor/flatpickr/4.6.13/flatpickr.min.css">

<%-- selectric --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/vendor/selectric/1.13.0/selectric.css">

<%-- 전역 변수 --%>
<script type="text/javascript">
    window._contextPath = "${pageContext.request.contextPath}";
    window._accessToken = "${sessionScope.accessToken}";
</script>

<%-- 공통 모듈 --%>
<%@ include file="/WEB-INF/views/common/util/loading.jsp" %>    <%-- loading bar --%>
<%@ include file="/WEB-INF/views/common/util/alert.jsp" %>      <%-- alert box --%>
<%@ include file="/WEB-INF/views/common/util/modal.jsp" %>      <%-- modal box --%>
<%@ include file="/WEB-INF/views/common/util/snackbar.jsp" %>   <%-- snackbar --%>