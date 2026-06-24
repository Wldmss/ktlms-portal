<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%-- jquery --%>
<script src="${pageContext.request.contextPath}/webjars/jquery/3.7.1/jquery.min.js"></script>

<%-- flatpickr--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/vendor/flatpickr/4.6.13/flatpickr.min.css">

<%-- selectric --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/vendor/selectric/1.13.0/selectric.css">

<%-- 공통 모듈 --%>
<%@ include file="/WEB-INF/views/common/util/loading.jsp" %>    <%-- loading bar --%>
<%@ include file="/WEB-INF/views/common/util/alert.jsp" %>      <%-- alert box --%>
<%@ include file="/WEB-INF/views/common/util/modal.jsp" %>      <%-- modal box --%>
<%@ include file="/WEB-INF/views/common/util/snackbar.jsp" %>   <%-- snackbar --%>