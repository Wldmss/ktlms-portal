<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/layout/default-top.jsp" %>

<link rel="stylesheet" href="<c:url value='/resources/css/login/login.min.css' />">
<script src="<c:url value='/resources/js/login/login.min.js' />" defer></script>

<div class="login-container">
    <div class="login-header">
        <h2>KT LMS Portal</h2>
        <p>서비스 이용을 위해 사번과 비밀번호를 입력해주세요.</p>
    </div>

    <form id="loginForm" onsubmit="return false;">
        <div class="input-group">
            <label for="userId">사번 (ID)</label>
            <input type="text" id="userId" placeholder="사번을 입력하세요" required>
        </div>
        <div class="input-group">
            <label for="userPw">비밀번호</label>
            <input type="password" id="userPw" placeholder="비밀번호를 입력하세요" required>
        </div>

        <div id="secondAuthArea">
            <div class="input-group" style="margin-bottom: 0;">
                <label for="authCode" style="color: #0056b3;">🔑 인증번호 입력</label>
                <input type="text" id="authCode" placeholder="인증번호 6자리를 입력하세요">
            </div>
        </div>

        <button type="button" class="btn-submit" id="btnAction" onclick="handleLoginStep1()">인증 요청</button>
    </form>

    <div class="system-msg" id="msgBox"></div>
</div>

<script>
    // 미인증 접근으로 온 경우 alert
    const _redirectUrl = new URLSearchParams(location.search).get('redirect');
    if (_redirectUrl) {
        alert('로그인이 필요합니다.');
    }

    const contextPath = "${pageContext.request.contextPath}";
    let isSecondStep = false; // 현재 2차 인증 단계인지 여부 플래그

    // 입력 필드에서 Enter → 인증 요청/로그인 (사번·비밀번호·인증번호 칸 공통)
    $("#loginForm").on("keydown", "input", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            handleLoginStep1();
        }
    });

    // 💡 [Step 1]: ID, PW 유효성 검사 및 우회 판단 요청
    function handleLoginStep1() {
        if (isSecondStep) {
            handleLoginStep2(); // 만약 이미 2차인증 창이 열려있다면 2단계로 토스
            return;
        }

        const id = $("#userId").val();
        const pw = $("#userPw").val();

        if (!id || !pw) {
            $("#msgBox").text("사번과 비밀번호를 모두 입력해주세요.");
            return;
        }

        postAjax("/auth/login", {userId: id, password: pw}, {handleAuthError: false})
            .then(function (res) {
                if (!res.success && res.result !== "NEED_2FA") {
                    $("#msgBox").css("color", "#dc3545").text(res.message || "로그인에 실패했습니다.");
                    return;
                }

                // BYPASS / SUCCESS 시 redirect URL 로 이동
                if (res.result === 'BYPASS' || res.result === 'SUCCESS') {
                    // 로그인 응답 바디로 내려준 accessToken 저장 (Bearer 헤더용, 쿠키 방식과 병행)
                    if (res.data && res.data.accessToken) {
                        window._accessToken = res.data.accessToken;
                    }
                    location.href = _redirectUrl
                        ? decodeURIComponent(_redirectUrl)
                        : contextPath + '/sample';
                } else if (res.result === "NEED_2FA") {
                    // 일반 사용자: 2차 인증 입력창 활성화
                    $("#msgBox").css("color", "#0056b3").text(res.message);
                    $("#secondAuthArea").slideDown();
                    $("#btnAction").text("인증 및 로그인 완료");
                    $("#userId").attr("readonly", true);
                    $("#userPw").attr("readonly", true);
                    isSecondStep = true;
                }
            })
            .catch(function (err) {
                console.log(err);
                $("#msgBox").css("color", "#dc3545").text(err.responseJSON?.message || "로그인 정보가 올바르지 않습니다.");
            });
    }

    // 💡 [Step 2]: 번호 입력 후 최종 로그인 및 JWT 발급 요청
    function handleLoginStep2() {
        const id = $("#userId").val();
        const code = $("#authCode").val();

        if (!code) {
            $("#msgBox").css("color", "#dc3545").text("인증번호를 입력해주세요.");
            return;
        }

        postAjax("/auth/login/step2", {id: id, code: code}, {handleAuthError: false})
            .then(function (res) {
                if (res.status === "SUCCESS") {
                    // 🔑 발급된 JWT 토큰을 브라우저 로컬스토리지나 세션스토리지에 저장합니다.
                    localStorage.setItem("accessToken", res.token);

                    // 🚀 메인 페이지로 대이동
                    location.href = contextPath + res.redirectUrl;
                }
            })
            .catch(function (err) {
                $("#msgBox").css("color", "#dc3545").text(err.responseJSON?.message || "인증번호가 틀렸습니다.");
            });
    }
</script>

<%@ include file="/WEB-INF/views/common/layout/default-bottom.jsp" %>
