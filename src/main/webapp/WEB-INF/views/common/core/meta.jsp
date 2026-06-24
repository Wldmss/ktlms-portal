<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>KT학습플랫폼 지니어스</title>

<%-- icon --%>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/icons/favicon.ico" type="image/x-icon">
<link rel="icon" href="${pageContext.request.contextPath}/resources/icons/favicon.ico" type="image/x-icon">

<%-- 공통 css --%>
<link rel="stylesheet" href="<c:url value='/resources/css/common/common-ui.min.css' />">
<link rel="stylesheet" href="<c:url value='/resources/css/common/common.min.css' />">
<link rel="stylesheet" href="<c:url value='/resources/css/common/header.min.css' />">

<%-- 전역 변수 --%>
<script type="text/javascript">
    window._contextPath = "${pageContext.request.contextPath}";
    window._accessToken = "${sessionScope.accessToken}";
</script>