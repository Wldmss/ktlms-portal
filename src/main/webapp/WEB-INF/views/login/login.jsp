<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%@ include file="/WEB-INF/views/common/layout/default-top.jsp" %>
<link rel="stylesheet" href="<c:url value='/resources/css/pages/login/login.min.css' />">
<script src="<c:url value='/resources/vendor/jsencrypt/3.5.4/jsencrypt.min.js' />"></script>

<%-- 로그인 이후 redirect url--%>
<c:set var="redirectUrl" value="${input.url}"/>
<c:if test="${empty redirectUrl}">
    <c:set var="redirectUrl" value="${param.redirect}"/>
</c:if>

<div id="wrap-login">
    <div id="loginTitle" class="login-title"></div>

    <form id="loginForm" name="loginForm" method="post" autocomplete="off" onsubmit="return false;">
        <input type="hidden" id="loginMode" value="<c:out value='${loginMode}' />"/>
        <input type="hidden" id="loginUserId" name="loginUserId" value="<c:out value='${input.userid}' />"/>
        <input type="hidden" id="ssoMessage" name="ssoMessage" value="<c:out value='${ssoMessage}' />"/>

        <div class="enter-box">
            <img class="login-logo" src="<c:url value='/resources/legacy/newPortal/icons/new23_logo.png' />" alt="KT"/>

            <%-- 알림 메시지 영역 --%>
            <div id="loginProcMsg" class="login-proc-msg ${not empty ssoMessage ? 'error' : ''}"
                 aria-live="polite"></div>

            <%-- ldap 로그인 --%>
            <div class="login-field">
                <input id="userid" name="userid" type="text" placeholder="아이디 (사번)"
                       value="<c:out value='${input.userid}' />" autocomplete="username"/>
            </div>
            <div class="login-field">
                <input id="pwd" name="pwd" type="password" placeholder="비밀번호" autocomplete="current-password"/>
            </div>

            <a class="btn-login" id="login-btn-id" role="button">로그인</a>

            <%-- 인증번호(SMS OTP) 영역 --%>
            <div id="otpArea" style="display: none;">
                <span id="authTime"><span id="optMsg">인증번호가 전송되었습니다.</span> (남은시간: <span id="time">180</span>초)</span>

                <input id="smsSerial" name="serial" type="text" placeholder="인증번호 6자리" maxlength="6"
                       autocomplete="off" inputmode="numeric"/>
                <a class="btn-login" id="smsAuthCheck" role="button">인증번호 확인</a>
                <a id="reSendSmsAuth">인증번호 재전송</a>
            </div>

            <span id="loginMsg">로그인 아이디·비밀번호는<br/>KATE/KTalk 아이디(사번)와 동일합니다.</span>

            <div class="login-divider">또는</div>

            <%-- MS Entra SSO --%>
            <a class="entra-sso-btn" id="btnEntraSso" role="button">
                <svg width="18" height="18" viewBox="0 0 23 23" aria-hidden="true">
                    <path fill="#f35325" d="M1 1h10v10H1z"/>
                    <path fill="#81bc06" d="M12 1h10v10H12z"/>
                    <path fill="#05a6f0" d="M1 12h10v10H1z"/>
                    <path fill="#ffba08" d="M12 12h10v10H12z"/>
                </svg>
                Microsoft 로그인
            </a>

            <%-- 문의 및 연락처 --%>
            <a class="inquiry-btn">문의 및 연락처</a>
            <div class="inquiry-popup">
                <p>문의 및 연락처<a class="popup-close"></a></p>
                <ul>
                    <li>
                        <div>○ 로그인 문의</div>
                        <p>- 아이디를 모르는 경우, 조직의 인사담당자에게 문의 하시기 바랍니다.</p>
                        <p>- 비밀번호를 분실한 경우, <a href="https://idms.kt.com/im/" target="_blank"
                                              rel="noopener">idms.kt.com</a>에서 비밀번호 변경 또는 초기화 하시기 바랍니다.</p>
                        <p>* 사외망 등의 사유로 <a href="https://idms.kt.com/im/" target="_blank" rel="noopener">idms.kt.com</a>
                            접속이 불가할 경우 조직의 인사담당자 또는 아래 연락처로 문의 하시기 바랍니다.<br/>※ 1588-3391 → 1번(KOS무선)</p>
                    </li>
                    <li>○ 시스템 문의 : 1588-3391 (1 &gt; 1)</li>
                    <li>○ 이러닝 문의 : 1577-0263</li>
                </ul>
            </div>
        </div>
        <input type="hidden" name="url" id="url" value="<c:out value='${redirectUrl}' />"/>
    </form>

    <div id="footer">
        <p class="address">(주)케이티 경기도 성남시 분당구 불정로 90 (정자동 206번지)</p>
        <p>Copyright ⓒ2026 kt corp. All rights reserved.</p>
        <a class="policy-btn">개인정보처리방침</a>
    </div>
</div>

<script nonce="${cspNonce}">
    let otpTimerId = null;
    let remainingSeconds = 180;
    let otpToken = null;
    const isDevice = window._isMobile ? "Y" : "N";

    // 로그인 mode check
    const loginMode = $("#loginMode").val();
    let actionUrl, loginTitle;
    switch (loginMode) {
        case "cds":
            actionUrl = "cdsLoginProcAjax";
            loginTitle = "G직군 역량진단 결과 조회";
            break;
        case "exam":
            actionUrl = "examLoginProcAjax";
            loginTitle = "대량평가 결과 조회";
            break;
        default:
            actionUrl = "loginProcAjax";
    }

    // OTP 우회 로그인 페이지 title 설정
    if (loginTitle != null) {
        $("#loginTitle").show();
        $("#loginTitle").text(new Date().getFullYear() + " " + loginTitle);
    } else {
        $("#loginTitle").hide();
    }

    // message 설정
    const ssoMessage = $("#ssoMessage").val();
    setMessage(ssoMessage);

    /* 알림 메시지 설정 */
    function setMessage(message, ok, id) {
        if (id === undefined || id === null) id = "loginProcMsg";
        let $message = $("#" + id).removeClass("ok error");
        if (!message) {
            $message.empty();
            return;
        }
        $message.addClass(ok ? "ok" : "error").text(message);
    }

    /* 로그인 버튼 text */
    function setLoginBusy(busy) {
        $("#login-btn-id")
            .toggleClass("is-busy", busy)
            .attr("aria-disabled", busy ? "true" : "false")
            .text(busy ? "로그인 중..." : "로그인");
    }

    /* login ajax */
    function postLoginAjax(url, data) {
        let actionUrl = (window._isMobile ? "/mobile/m" : "") + "/login/" + url;

        return postAjax(actionUrl, $.param(data), {
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            processData: false,
            handleAuthError: false,
            headers: {"X-Requested-With": "XMLHttpRequest"}
        });
    }

    /* login payload */
    function payload() {
        const userId = $.trim($("#userid").val());
        $("#loginUserId").val(userId);
        return {
            userid: userId,
            loginUserId: userId,
            pwd: $("#pwd").val(),
            url: $("#url").val(),
            isDevice: isDevice
        };
    }

    /* otp 영역 open */
    function showOtp(serial) {
        $("#otpArea").show();
        $("#loginMsg").hide();
        if (serial) {
            $("#smsSerial").val(serial);
        }
        $("#smsSerial").focus();
        startOtpTimer();
    }

    /* otp 인증 시간 */
    function startOtpTimer() {
        remainingSeconds = 180;
        $("#time").text(remainingSeconds);
        if (otpTimerId) {
            clearInterval(otpTimerId);
        }
        otpTimerId = setInterval(function () {
            remainingSeconds -= 1;
            $("#time").text(Math.max(remainingSeconds, 0));
            if (remainingSeconds <= 0) {
                clearInterval(otpTimerId);
                otpTimerId = null;
            }
        }, 1000);
    }

    /* RSA 공개키 조회 (없으면 null → 평문 폴백) */
    function getLoginKey() {
        return postLoginAjax("getEncKeyAjax", {isDevice: isDevice})
            .then(function (res) {
                return (res && res.result === "Y" && res.encKey) ? res.encKey : null;
            })
            .catch(function () {
                return null;
            });
    }

    /* 공개키로 비밀번호 RSA 암호화 (실패 시 null) */
    function encryptPwd(raw, publicKey) {
        try {
            let crypt = new JSEncrypt();
            crypt.setPublicKey(publicKey);
            let enc = crypt.encrypt(raw);
            return enc || null;
        } catch (e) {
            return null;
        }
    }

    /* 로그인 */
    function doLogin() {
        const userId = $.trim($("#userid").val());
        const rawPwd = $("#pwd").val();
        if (!userId || !rawPwd) {
            setMessage("아이디와 비밀번호를 모두 입력해주세요.");
            return;
        }

        setMessage("");
        setLoginBusy(true);

        getLoginKey()
            .then(function (publicKey) {
                let data = payload();
                let enc = publicKey ? encryptPwd(rawPwd, publicKey) : null;
                if (enc) {
                    data.pwd = enc;
                    data.encYn = "Y";
                } else {
                    // 공개키 없음/암호화 실패 → 평문 전송(TLS 전제, 로컬 등)
                    data.encYn = "N";
                }
                return postLoginAjax(actionUrl, data);
            })
            .then(function (res) {
                if (res.accessToken) {
                    window._accessToken = res.accessToken;
                }

                if (res.result === "Y") {
                    location.href = resolveUrl(res.resultUrl || window._mainUrl);
                    return;
                }

                // LDAP 비밀번호 만료 안내
                if (Number(res.isOk) === -9) {
                    const expMessage = res.message || "비밀번호가 만료되었습니다.\n비밀번호를 변경해주세요.";
                    setMessage(expMessage);
                    openAlert(expMessage).then(() => {
                        window.open("https://ktsso.kt.com/ssologin/guide/pwdTabOrg.html", "popPwdExpire", "width=520,height=590");
                    });
                }

                // 계정 잠금 해제
                if (res.lgFailCnt) {
                    if (res.otpError === "error") {
                        // SMS 우회 설정이면 OTP 화면을 열지 않고 관리자 문의 안내만 표시
                        setMessage(res.message || "계정이 잠겼습니다. 관리자에게 문의해 주세요.");
                        return;
                    }
                    otpToken = res.otpToken || null;
                    if (!otpToken) {
                        setMessage("로그인 인증 정보가 없습니다. 처음부터 다시 시도해 주세요.");
                        return;
                    }
                    sendSmsAuth(res.message);
                    return;
                }

                // 인증번호 발송
                if (res.otpCnt !== undefined && res.isOk === 1) {
                    otpToken = res.otpToken || null;
                    if (!otpToken) {
                        setMessage("로그인 인증 정보가 없습니다. 처음부터 다시 시도해 주세요.");
                        return;
                    }
                    sendSmsAuth();
                    return;
                }

                setMessage(res.message || "로그인에 실패했습니다.");
            })
            .catch(function (xhr) {
                let response = xhr.responseJSON || {};
                setMessage(response.message || "로그인 처리 중 오류가 발생했습니다.");
            })
            .finally(function () {
                setLoginBusy(false);
            });
    }

    /* 인증번호 발송 */
    function sendSmsAuth(isLockMessage) {
        let data = payload();
        postLoginAjax("sendSmsOptAuthAjax", {
            userid: data.userid,
            loginUserId: data.userid,
            isDevice: data.isDevice,
            otpToken: otpToken
        }).then(function (res) {
            otpToken = res.otpToken || otpToken;
            if (res.resultCode === "1") {
                showOtp(res.serial);
                setMessage(isLockMessage || res.message || "인증번호가 발송되었습니다.", true);
            } else {
                setMessage(res.message || "인증번호 발송에 실패했습니다.");
            }
        }).catch(function () {
            setMessage("인증번호 발송 중 오류가 발생했습니다.");
        });
    }

    function sendSmsAuthCheck() {
        let data = payload();
        let serial = $.trim($("#smsSerial").val());
        if (!serial) {
            setMessage("인증번호를 입력해주세요.");
            return;
        }

        postLoginAjax("sendSmsAuthCheckAjax", {
            userid: data.userid,
            s_userid: data.userid,
            serial: serial,
            isDevice: data.isDevice,
            otpToken: otpToken
        }).then(function (res) {
            if (res.accessToken) {
                window._accessToken = res.accessToken;
            }
            if (res.resultCode === "1") {
                location.href = resolveUrl(res.resultUrl || window._mainUrl);
            } else if (res.resultCode === "2") {
                clearInterval(otpTimerId);
                otpTimerId = null;
                $("#otpArea").hide();
                otpToken = null;
                setMessage(res.message || "계정잠금이 해제되었습니다. 다시 로그인해 주세요.");
            } else {
                setMessage(res.message || "인증번호가 올바르지 않습니다.");
            }
        }).catch(function () {
            setMessage("인증번호 확인 중 오류가 발생했습니다.");
        });
    }

    /* 개인정보처리방침 */
    function goPrivacyPolicy() {
        window.open("<c:url value='/resources/html/policy/privacyPolicy.html' />", "privacyPolicy", "width=1080,height=700");
    }

    // 로그인 button click
    $("#login-btn-id").on("click", function () {
        if ($(this).hasClass("is-busy")) {
            return;
        }
        doLogin();
    });

    $("#reSendSmsAuth").on("click", sendSmsAuth);       // 인증번호 재전송 click
    $("#smsAuthCheck").on("click", sendSmsAuthCheck);   // 인증번호 확인

    // MS Entra SSO 로그인 click
    $("#btnEntraSso").on("click", function () {
        var target = "/sso/entra-sso/login";
        var redirectUrl = $("#url").val();      // 로그인 후 돌아갈 url
        if (redirectUrl) {
            target += "?url=" + encodeURIComponent(redirectUrl);
        }
        location.href = resolveUrl(target);
    });

    // 문의 및 연락처 open
    $(".inquiry-btn").on("click", function () {
        $(".inquiry-popup").show();
    });

    // 문의 및 연락처 close
    $(".popup-close").on("click", function () {
        $(".inquiry-popup").hide();
    });

    // 개인정보처리방침 (inline onclick 제거 → CSP 대응)
    $(".policy-btn").on("click", goPrivacyPolicy);

    // enter 처리
    $("#loginForm").on("keydown", "input", function (event) {
        if (event.key === "Enter" || event.keyCode === 13) {
            event.preventDefault();
            $("#smsSerial").is(":visible") && this.id === "smsSerial" ? sendSmsAuthCheck() : doLogin();
        }
    });
</script>

<%@ include file="/WEB-INF/views/common/layout/default-bottom.jsp" %>
