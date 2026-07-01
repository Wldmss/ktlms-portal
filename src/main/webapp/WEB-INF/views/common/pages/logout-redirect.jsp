<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/layout/default-top.jsp" %>

<div class="login-container">
    <p>로그아웃 처리 중입니다...</p>
</div>

<script>
    const contextPath = "${pageContext.request.contextPath}";

    <%-- GET 진입 시 상태변경(DB 세션 삭제, 쿠키 초기화)은 CSRF 보호된 POST 로만 수행한다 --%>
    $(document).ready(function () {
        $.ajax({
            url: contextPath + "/auth/logout",
            type: "POST"
        }).always(function () {
            location.href = contextPath + "/";
        });
    });
</script>

<%@ include file="/WEB-INF/views/common/layout/default-bottom.jsp" %>
