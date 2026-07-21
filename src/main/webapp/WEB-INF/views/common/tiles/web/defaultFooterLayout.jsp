<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="footer-wrap">
    <hr class="hr">
    <div class="ly-grid">
        <div class="footer-bottom-wrap">
            <div class="capyright-list">
                <p class="tit">(주)케이티 경기도 성남시 분당구 불정로 90 (정자동 206번지)<br>Copyright ⓒ2020 kt corp. All rights reserved.
                </p>
                <p class="tit">플랫폼 Help Desk 1588-3391 (1 > 1)<br>(운영시간 : 평일 09시 ~ 18시)</p>
            </div>
            <div class="footer-info-list">
                <button type="button" onclick="goOpenSource();">오픈소스라이선스</button>
                <button type="button" onclick="goPrivacyPolicy();">개인정보처리방침</button>
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