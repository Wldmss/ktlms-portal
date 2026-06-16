<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <%@ include file="/WEB-INF/views/common/layout/header.jsp" %>
</head>

<body class="font-normal"><%--body 시작--%>
<main class="main-wrapper">
    <jsp:include page="/WEB-INF/views/common/layout/gnb.jsp"/>
    <c:if test="${not empty subGNB}">
        <jsp:include page="${subGNB}"/>
    </c:if>
    <div class="content"><%--content 시작--%>
        <c:if test="${not empty bodyPage}">
            <jsp:include page="${bodyPage}"/>
        </c:if>
    </div>
</main>
<jsp:include page="/WEB-INF/views/common/layout/footer.jsp"/>
</body>
</html>