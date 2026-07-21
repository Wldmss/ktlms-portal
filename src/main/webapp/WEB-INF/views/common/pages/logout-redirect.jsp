<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/layout/default-top.jsp" %>

<div class="login-container">
    <p>로그아웃 처리 중입니다...</p>
</div>

<script nonce="${cspNonce}">
    $(document).ready(function () {
        postAjax("/auth/logout").then(res => {
            location.href = window._contextPath + "/";
        });
    });
</script>

<%@ include file="/WEB-INF/views/common/layout/default-bottom.jsp" %>
