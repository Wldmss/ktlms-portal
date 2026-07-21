<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="l-footer">
    <div class="footer">
        <div class="footer-bot">
            <div class="footer-info">
                <p>
                    <span>(주)케이티 경기도 성남시 분당구 불정로 90 (정자동 206번지) </span>
                    <span>Copyright ⓒ2020 kt corp. All rights reserved.</span>
                </p>
            </div>
        </div>
    </div>
</div>
<script nonce="${cspNonce}">
    function goOpenSource() {
        var url = "<c:url value="/resources/legacy/policy/openSource.html" />";
        window.open(url, "history", "toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1100,height=600,left=0,top=0");
    }

    function goPrivacyPolicy() {
        var url = "<c:url value="/resources/html/policy/privacyPolicy.html" />";
        window.open(url, "history", "toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1100,height=600,left=0,top=0");
    }
</script>