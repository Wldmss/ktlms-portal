<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/common/layout/default-top.jsp" %>

<style>
    .change-pw-page {
        min-height: 70vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 40px 20px;
        background: #f6f7fb;
    }

    .change-pw-box {
        width: min(460px, 100%);
        background: #fff;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        padding: 30px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
    }

    .change-pw-box h1 {
        margin: 0 0 18px;
        font-size: 22px;
    }

    .change-pw-box label {
        display: block;
        margin: 12px 0 6px;
        font-size: 13px;
        font-weight: 700;
    }

    .change-pw-box input {
        width: 100%;
        height: 42px;
        border: 1px solid #ddd;
        border-radius: 4px;
        padding: 0 12px;
    }

    .change-pw-box button {
        width: 100%;
        height: 44px;
        margin-top: 18px;
        border: 0;
        border-radius: 6px;
        background: #e15257;
        color: #fff;
        font-weight: 700;
    }

    .change-pw-msg {
        min-height: 18px;
        margin-top: 12px;
        color: #d33030;
        font-size: 13px;
    }
</style>

<div class="change-pw-page">
    <form class="change-pw-box" id="changePwForm" onsubmit="return false;">
        <h1>비밀번호 변경</h1>
        <input type="hidden" id="s_userid" name="s_userid" value="<c:out value='${loginUser.userId}' />">
        <input type="hidden" id="type" value="<c:out value='${output.type}' />">
        <label for="current_pwd">현재 비밀번호</label>
        <input id="current_pwd" name="current_pwd" type="password">
        <label for="conf_pwd">새 비밀번호</label>
        <input id="conf_pwd" name="conf_pwd" type="password">
        <button type="button" id="btnChangePw">변경</button>
        <div class="change-pw-msg" id="changePwMsg"></div>
    </form>
</div>

<script nonce="${cspNonce}">
    function changePwUrl(path) {
        return window._contextPath + path;
    }

    // enter 처리
    $("#changePwForm").on("keydown", "input", function (event) {
        if (event.key === "Enter" || event.keyCode === 13) {
            event.preventDefault();
            $("#btnChangePw").click();
        }
    });

    $("#btnChangePw").on("click", function () {
        $.ajax({
            url: changePwUrl("/login/confirmPwdAjax"),
            type: "POST",
            dataType: "json",
            data: {
                s_userid: $("#s_userid").val(),
                current_pwd: $("#current_pwd").val(),
                conf_pwd: $("#conf_pwd").val()
            },
            headers: {"X-Requested-With": "XMLHttpRequest"}
        }).done(function (res) {
            const messages = {
                "1": "변경되었습니다.",
                "2": "기존 비밀번호와 새 비밀번호가 같습니다.",
                "3": "비밀번호는 8자리 이상, 문자/숫자/특수문자를 포함해야 합니다.",
                "4": "현재 비밀번호가 올바르지 않습니다.",
                "0": "비밀번호 변경 대상 정보를 확인할 수 없습니다."
            };

            if (res.error === "1") {
                openAlert(messages[res.error] || "변경되었습니다.").then(() => {
                    const type = $("#type").val();
                    window.location.href = (type != null && true && type === "etc") ? "/education/list/courseList?type=MPACK" : window._mainUrl || "/";
                });
            } else {
                $("#changePwMsg").css("color", res.error === "1" ? "#0056b3" : "#d33030").text(messages[res.error] || "처리 중 오류가 발생했습니다.");
            }
        });
    });
</script>

<%@ include file="/WEB-INF/views/common/layout/default-bottom.jsp" %>
