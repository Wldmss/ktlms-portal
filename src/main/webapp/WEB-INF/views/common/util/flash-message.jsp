<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--
    RedirectUtil flash 메시지 표시 영역.
    - 신규 코드: RedirectUtil.success/error/info/warn 로 flashMessage 를 담는다.
    - 이관 코드: RedirectUtil.redirect(request, response, ...) 류는 response script 를 직접 쓰므로 이 JSP를 거치지 않는다.
    - error/warn 은 alert, success/info 는 snackbar 로 표시한다.
--%>
<c:if test="${not empty flashMessage}">
    <div id="global-flash-message"
         data-type="<c:out value='${flashMessageType}'/>"
         style="display:none;"><c:out value="${flashMessage}"/></div>

    <script nonce="${cspNonce}">
        (function () {
            function showFlashMessage() {
                const el = document.getElementById("global-flash-message");
                if (!el) return;

                const message = el.textContent.trim();
                const type = el.dataset.type || "info";
                if (!message) return;

                if ((type === "error" || type === "warn") && typeof openAlert === "function") {
                    openAlert(message);
                    return;
                }

                if (typeof showSnackbar === "function") {
                    showSnackbar(message);
                    return;
                }

                alert(message);
            }

            if (document.readyState === "loading") {
                document.addEventListener("DOMContentLoaded", showFlashMessage);
            } else {
                showFlashMessage();
            }
        })();
    </script>
</c:if>
