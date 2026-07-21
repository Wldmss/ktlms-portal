<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<div class="l-gnb new2023"></div>
<script nonce="${cspNonce}">
    $(function () {
        $.ajax({
            url: "/a/layout/gnbListAjax",
            async: false,
            type: 'GET',
            timeout: 3000,
            dataType: "html",
            data: {
                mwFlag: 'W'
            },
            error: function (xhr, status, error) {
                if (xhr.status === 999) {
                    setTimeout(function () {
                        alert('세션이 만료되었습니다. \n로그인 화면으로 이동합니다.');
                        location.href = '/login';
                    }, 100);
                }
            },
            success: function (data) {
                $(".l-gnb").html(data);
            }
        });

    });
</script>