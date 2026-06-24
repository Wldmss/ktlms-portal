<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/core/resources.jsp"/>
</head>
<body>
<%-- alert & redirect --%>
<script type="text/javascript">
    $(document).ready(function () {
        const message = "${serverMessage}";
        let targetUrl = "${serverTargetUrl}";

        const baseCtx = typeof window._contextPath !== 'undefined' ? window._contextPath : '';
        if (targetUrl.startsWith("/") && baseCtx.endsWith("/")) {
            targetUrl = targetUrl.substring(1);
        }

        if (typeof openAlert === 'function') {
            openAlert(message).then(function () {
                location.href = baseCtx + targetUrl;
            });
        } else {
            alert(message);
            location.href = baseCtx + targetUrl;
        }
    });
</script>

</body>
</html>