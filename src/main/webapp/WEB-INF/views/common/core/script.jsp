<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- src/main/webapp/resources/webjars --%>
<c:set var="_webjars" value="${pageContext.request.contextPath}/webjars" scope="application"/>

<%-- 공통 라이브러리 --%>
<script src="${_webjars}/bootstrap/5.3.6/js/bootstrap.min.js"></script>

<%-- flatpickr --%>
<script src="${pageContext.request.contextPath}/resources/vendor/flatpickr/4.6.13/flatpickr.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/vendor/flatpickr/4.6.13/ko.min.js"></script>

<%-- selectric --%>
<script src="${pageContext.request.contextPath}/resources/vendor/selectric/1.13.0/jquery.selectric.min.js"></script>

<%-- plotly chart --%>
<script src="${pageContext.request.contextPath}/resources/vendor/plotly/3.6.0/plotly-3.6.0.min.js"></script>

<%-- 공통 js--%>
<script src="<c:url value='/resources/js/common/header.min.js' />"></script>
<script src="<c:url value='/resources/js/common/common.min.js' />"></script>
<script src="<c:url value='/resources/js/common/formatter.min.js' />"></script>
<script src="<c:url value='/resources/js/common/common-dialog.min.js' />"></script>
<script src="<c:url value='/resources/js/common/common-form.min.js' />"></script>
<script src="<c:url value='/resources/js/common/common-file.min.js' />"></script>
<script src="<c:url value='/resources/js/common/common-check.min.js' />"></script>
<script src="<c:url value='/resources/js/common/common-ajax.min.js' />"></script>
<script src="<c:url value='/resources/js/common/common-datepicker.js' />"></script>
