<%--
  Migrated from genius: WebContent/WEB-INF/jsp/mobile/login/testDev.jsp
  Encoding converted to UTF-8. This file is kept as legacy lineage/reference;
  active login is routed through Spring Security + JWT in /WEB-INF/views/login/login.jsp.
--%>
<%--
/*
 * login.jsp
 *
 * Copyright (c) 2010 ANYMOBI. All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of ANYMOBI. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with ANYMOBI.
 */

/* -----------------------------------------------------------------------------
 * @system  : KT교육포털
 * @menu    : 
 * @source  : login.jsp
 * @desc    : 
 * -----------------------------------------------------------------------------
 * VER  DATE               AUTHOR             DESCRIPTION
 * ---  -----------------  -----------------  ----------------------------------
 * 1.0  2015-10-03         David.kim          최초 작성
 * 1.1  
 * -------------------------------------------------------------------------- */
--%>
<%@ page pageEncoding="UTF-8" %>

<%@ include file="/WEB-INF/jsp/mobile/common/INC_Header.jsp" %>
<!-- 동적 리소스 정의 시작 -->
<script src="/anymobi/lib/js/jquery/jquery.numeric.min.js"></script>
<script src="/anymobi/lib/js/jquery-ui-1.11.4/jquery-ui.min.js"></script>
<script src="/anymobi/lib/js/shortcut.js"></script>
<script src="/common/js/jsencrypt.min.js"></script>
<link rel="stylesheet" type="text/css" href="/anymobi/mobile/css/login23.css"/>
<%
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", -1);
%>

<style>
    body {
        height: 100vh;
    }

    #wrap {
        position: relative;
        width: 100%;
        height: 100%;
        background: #F3EDEE url(/newPortal/images/new23_img_login_full_mobile.png) no-repeat center bottom / cover;


    }

    .mobile-login {
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        width: calc(100% - 40px);
    }

    .m-logo {
        text-align: center;
    }

    .m-logo img {
        width: 200px;
    }

    .login-form-wrap {
        background-color: #Fff;
        background: #FFF;
        width: min(90%, 400px);
        margin: 30px auto 0;
        padding: 30px 25px;
        border-radius: 20px;
        text-align: center;
        box-shadow: 0px 4px 16px 0px rgba(221, 221, 221, 0.60);
        margin-top: -85%;
        opacity: 0;
    }

    .login-form-wrap.on {
        margin-top: 12px;
        opacity: 1;
        transition: all .7s ease-in-out .3s;
    }

    .inp-wrap {
        position: relative;
    }

    .inp-text {
        position: relative;
        margin-top: 20px;
    }

    .inp-text input {
        width: 100%;
        padding: 13px 15px;
        vertical-align: top;
        font-size: 12px;
        line-height: 22px;
        height: 36px;
        border-radius: 4px;
        border: 0 none;
        background: #E8F0FE;
        box-sizing: border-box;
        font-family: 'NotoSansKR';
        border: 0 none !important;
    }

    .inp-text input:focus {
        outline: none;
    }

    .inp-text input + input {
        margin-top: 12px;
    }

    .certification-number .inp-text input:focus {
        border-color: #ed2024 !important;
    }

    /* placeholder */
    input::placeholder,
    textarea::placeholder {
        color: #bbb !important;
        opacity: 1;
    }

    input::-webkit-input-placeholder,
    textarea::-webkit-input-placeholder {
        color: #bbb !important
    }

    input:-ms-input-placeholder,
    textarea:-ms-input-placeholder {
        color: #bbb !important
    }

    input:-moz-input-placeholder,
    textarea:-moz-input-placeholder {
        color: #bbb !important
    }

    .btn-m-login {
        margin-top: 20px;
        display: inline-block;
        padding: 0 10px;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        font-size: 15px;
        border: 1px solid transparent;
        box-sizing: border-box;
        border-radius: 6px;
        cursor: pointer;
        background-color: #FE2E36;
        width: 100%;
        height: 40px;
        line-height: 38px;
        color: #fff;
    }

    .btn-m-login:disabled {
        background-color: #ccc;
    }

    .mobile-login .desc {
        padding: 10px 5px 15px 5px;
        font-size: 12px;
        line-height: 1.5;
        text-align: center;
    }

    .btn-wrap {
        text-align: center;
    }

    .btn-wrap .btn-inquiry {
        text-align: center;
        padding: 7px 11px;
        font-size: 12px;
        color: #222;
        border-radius: 4px;
        border: 1px solid #CCC;
        background: #FFF;
        height: 27px;
        display: inline-block;
    }

    button {
        font-family: 'NotoSansKR';
    }

    .certification-number {
        margin-top: 20px;
        display: none;
    }

    .popup-container {
        visibility: hidden;
        opacity: 0;
        transition: all 0.3s ease-in-out;
        transform: scale(1);
        position: fixed;
        z-index: 1;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(21, 17, 17, 0.61);
        display: flex;
        align-items: center;
    }

    .btn-m-certification {
        margin-top: 15px;
        display: inline-block;
        padding: 0 10px;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        font-size: 15px;
        border: 1px solid transparent;
        box-sizing: border-box;
        border-radius: 6px;
        cursor: pointer;
        background-color: #ed2024;
        width: 100%;
        height: 40px;
        line-height: 38px;
        color: #fff;
    }

    .certification-time {
        display: block;
        text-align: left;
        margin-top: 5px;
        font-size: 14px;
    }

    .certification-time em {
        font-style: normal;
        font-weight: bold;
    }

    .popup-container:target {
        visibility: visible;
        opacity: 1;
        transform: scale(1);
    }

    #loginInfoPopup .popup-content {
        background-color: #fff;
        width: 80%;
        max-width: 500px;
        margin: 0 auto;
        border-radius: 10px;
        padding: 20px;
        position: relative;
    }

    .popup-container .close {
        position: absolute;
        right: 20px;
        top: 20px;
    }

    .popup-container .info {
        font-size: 12px;
        line-height: 18px;
        padding-top: 10px;
        color: #666;
    }

    .popup-container .info strong {
        font-weight: 500;
    }

    .popup-container .info a {
        color: #333;
    }

    .popup-container .btn-close {
        margin-top: 20px;
        display: inline-block;
        padding: 0 10px;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        font-size: 15px;
        border: 1px solid transparent;
        box-sizing: border-box;
        border-radius: 6px;
        cursor: pointer;
        background-color: #FE2E36;
        width: 100%;
        height: 40px;
        line-height: 38px;
        color: #fff;
    }

</style>
<body>
<div id="wrap">
    <!--// 콘텐츠 영역 시작 -->
    <form id="loginForm" name="loginForm" method="post" data-ajax="false" autocomplete="off">

        <section class="mobile-login">
            <h1 class="m-logo">
                <a href="#none">
                    <img src="/newPortal/images/new23_login_top_title.svg" alt="">
                </a>
            </h1>
            <article class="login-form-wrap">
                <a href="#none"><img src="/newPortal/icons/new23_logo.png" alt=""></a>
                <div class="inp-wrap">
                    <div class="inp-text">
                        <input type="text" name="userid" id="userid" placeholder="아이디를 입력하세요."
                               value="<c:out value='${input.userid}'/>">
                        <input type="password" name="pwd" id="pwd" placeholder="비밀번호를 입력하세요.">
                    </div>
                </div>
                <button type="button" class="btn-m-login" onClick="doLoginProc();">로그인</button>
                <!-- 로그인 클릭시 인증번호 입력 영역 S -->
                <div class="certification-number">
                    <div class="inp-text">
                        <input type="number" id="serial" placeholder="인증번호를 입력하세요.">
                    </div>
                    <span class="certification-time">
	                       	 남은시간: <em class="timer"></em>초
	                    </span>
                    <button type="button" class="btn-m-certification" onClick="sendSmsAuthCheck();">인증번호 확인</button>
                </div>
                <!-- // 로그인 클릭시 인증번호 입력 영역 E -->

                <!-- 문구 & 문의 및 연락처 S -->
                <div class="info-wrap">
                    <p class="desc">
                        로그인 아이디, 비밀번호는<br>KATE/KTalk 아이디(사번), 비밀번호와 동일합니다.
                    </p>
                    <div class="btn-wrap">
                        <a href="#loginInfoPopup" class="btn-inquiry">
                            문의 및 연락처
                        </a>
                    </div>
                </div>
                <!-- 문구 & 문의 및 연락처 E -->
            </article>
        </section>
        <input type="hidden" name="url" id="url" value="<c:out value='${input.url}'/>">
        <input type="hidden" id="sPublicKey" name="sPublicKey"
               value="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr2WMocbcFX4jAWVWaUU7ir9/+/Qo6g6rHg6EWSIdWsMwVKv9JvaEBIAFAqwdpPjagqiuchN4VscaQKCFVpDCM0SfIJ7jqpBwbl2su4MNH6GEFkGy4qZ3GVilNN3ly2gPuVP33fJaKg+N1KSNjMpEM5AUELQ0nCUwOkpF950dfld1HrhJWpyh64dXcq/4LUS69g28H2jzOmAhzQr6oHtzcxbwwoDSolesAi+oBYd1uQ4Yc4oKq0DkcWJG6IshsAw0YHlbhGq26W+/cbXbBzju+eJRL52UQ6EUkpobTIT7KI/ggQYJGWWw9ec7aHl5ksaf9DwLDIC33LO6R0vQdlNchQIDAQAB"/>
        <%-- <input type="hidden" id="sSeqPrivate" name="sSeqPrivate" value="${sSeqPrivate}" /> --%>
    </form>
</div>

<!-- 인증번호 팝업 S -->
<div class="modal-con">
    <div>
        <a href="javascript:void(0);" class="md-close"></a>
        <p class="md-title">인증번호 발송</p>
        <div class="md-txt">기본 데이터 입니다.</div>
    </div>
</div>
<!-- 인증번호 팝업 E -->

<div class="cc-popup-con j-pop-alert jc-hide">
    <p class="txt-alert"></p>
    <div class="btn-box"><a class="submit j-pop-close" href="javascript:void(0);">확 인</a></div>
</div>

<!-- 문의및연락처 팝업 S -->
<div id="loginInfoPopup" class="popup-container">
    <div class="popup-content">
        <h3>문의 및 연락처</h3>
        <a href="#" class="close">&times;</a>
        <ul class="info">
            <li>
                <strong>로그인 문의</strong>
                <p>- 아이디를 모르는 경우, 조직의 인사담당자에게 문의 하시기 바랍니다.</p>
                <p>- 비밀번호를 분실한 경우, idms.kt.com 에서 비밀번호 변경 또는 초기화 하시기 바랍니다.</p>
                <p>* 사외망 등의 사유로 idms.kt.com 접속이 불가할 경우 조직의 인사담당자 또는 아래 연락처로 문의 하시기 바랍니다.<br><br>※ 1588-3391 -&gt;
                    1번(KOS무선)</p>
                <hr/>
            </li>
            <li>시스템 문의 : <a href="tel:1588-3391">1588-3391 (1 &gt; 1)</a></li>
            <li>이러닝 문의 : <a href="tel:1577-0263">1577-0263</a></li>
            <li>KT AIVLE스쿨 문의처 : 담당 에이블 매니저</li>
        </ul>
        <a href="#" class="btn-close">확인</a>
    </div>
</div>
<!-- 문의및연락처 팝업 E -->


<%--2016-2-12 직무역량 연동 추가 --%>
<form id="form1" name="form1" method="post" autocomplete="off">
    <input type="hidden" name="p_userid">
    <input type="hidden" name="p_head_yn">
    <input type="hidden" name="sso_yn">
    <input type="hidden" name="return_page">
</form>

</body>
</html>
<script type="text/javascript">
    var url = '';
    var failOptYn = 'N';

    if (isMobile())
        url = '/mobile/m/login';

    $(window).on('load', function () {
        $('.login-form-wrap').addClass('on');
    });

    $(document).ready(function () {

        $("body").append("<div id='coverbox'></div>");

        $("#userid").focus();

        $("#serial").numeric();
        if ("${input.optCnt eq 0}" == 'true' || "${input.lgFailCnt eq 5}" == 'true') {
            //if("${input.lgFailCnt eq 5}" == 'true'){
            $('.certification-number').css('display', 'block');
            $(".btn-m-login").attr("disabled", true);
            $('.info-wrap').css('display', 'none');

            if ("${input.lgFailCnt eq 5}" == 'true')
                alert('비밀번호 5회 연속 오류로 계정잠금되어 로그인이 불가합니다.\nSMS 번호인증으로 계정잠금을 해제해 주시기 바랍니다.\n비밀번호 분실에 따른 변경, 초기화는 `문의 및 연락처`를 참고하세요.');

            sendSmsAuth('N');
        } else {
            $('.certification-number').css('display', 'none');
            $(".btn-m-login").attr("disabled", false);
        }

        // 팝업 : 닫기
        $(".j-pop-close").click(function () {
            $(".jc-hide, #coverbox").hide();
        });

        // 문의및연락처 팝업 : 열기
        $(".j-btn-contact").click(function () {
            $(".j-pop-contact, #coverbox").show();
        });

        //모달 팝업 닫기
        $('.md-close').click(function () {
            $('.modal-con').removeClass('open');
        });

        //인증번호 입력 시 팝업 닫기
        $(document).on('keydown', '#serial', function () {
            $('.modal-con').removeClass('open');
        });

        //외부영역 클릭 시 팝업 닫기
        $(document).mouseup(function (e) {
            if ($('.modal-con').has(e.target).length === 0) {
                $('.modal-con').removeClass('open');
            }
        });

    });

    function loginMobileEndpoint(action) {
        return "/mobile/m/login/" + action;
    }

    function doLoginProc() {
        var p_pwd = $.trim($("#pwd").val());
        var userId = $.trim($("#userid").val());
        var crypt = new JSEncrypt();

        crypt.setPrivateKey($('#sPublicKey').val());

        if (userId == "") {
            alert("아이디를 입력해 주세요");
            $("#userid").focus();
            return;
        } else if (p_pwd == "") {
            alert("비밀번호를 입력해 주세요.");
            $("#pwd").focus();
            return;
        }
        p_pwd = crypt.encrypt(p_pwd);
        p_pwd = p_pwd.replace(/%/gi, '%25');
        p_pwd = p_pwd.replace(/\+/gi, '%2B');
        p_pwd = escape(encodeURIComponent(p_pwd));

        var requestData = {
            userid: userId,
            pwd: p_pwd,
            url: $("#url").val()
            //seqPrivate : $("#sSeqPrivate").val()
        };

        $.ajax({
            url: loginMobileEndpoint('loginProcAjax'),
            async: false,
            type: 'POST',
            timeout: 3000,
            dataType: "json",
            data: JSON.stringify(requestData),
            error: function (e) {
                //console.log(e);
            },
            success: function (data) {
                if (data.result == 'N') {
                    if (data.optCnt == 0 || data.lgFailCnt == 5) {
                        $(".btn-m-login").attr("disabled", true);
                        $('.info-wrap').css('display', 'none');
                        if (data.lgFailCnt == 5) {
                            failOptYn = 'Y';
                            alert('비밀번호 5회 연속 오류로 계정잠금되어 로그인이 불가합니다.\nSMS 번호인증으로 계정잠금을 해제해 주시기 바랍니다.\n비밀번호 분실에 따른 변경, 초기화는 `문의 및 연락처`를 참고하세요.');
                        }
                        sendSmsAuth('N');
                    } else {
                        alert(data.message);
                        $(".btn-m-login").attr("disabled", false);
                        $("#pwd").val('');

                        if (data.errorCode == 'O') {
                            $('.certification-number').css('display', 'none');
                        }
                        if (data.isOk == -9) {
                            window.open('https://ktsso.kt.com/ssologin/guide/pwdTabOrg.html', 'popPwdExpire', 'width:520px, heigth:590');
                        }
                    }
                } else if (data.result == 'Y') {
                    $("#loginForm").attr({
                        action: data.resultUrl
                    }).submit();
                } else {
                    alert('로그인시 오류가 발생하였습니다. 관리자에게 문의바랍니다.');
                    $(".btn-m-login").attr("disabled", false);
                }
            }
        });

    }

    function goPrivacyPolicy() {
        var url = "<c:url value="/resources/html/policy/privacyPolicy.html" />";
        window.open(url, "history", "toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1100,height=600,left=0,top=0");
    };

    let initTime = 180;
    let timeCheck = initTime;
    let timeInterval = null;

    function sendSmsAuth(s) {

        $.ajax({
            url: loginMobileEndpoint('sendSmsOptAuthAjax'),
            async: false,
            type: 'POST',
            timeout: 3000,
            dataType: "json",
            //data : JSON.stringify(requestData),
            error: function (e) {
                //console.log(e);
            },
            success: function (data) {
                if (data.resultCode == "1") {
                    $('.certification-number').css('display', 'block');
                    clearInterval(timeInterval);
                    timeCheck = initTime;
                    $("#serial").val("");
                    //$("#serial").focus(); // 인증번호 입력 칸
                    timeInterval = setInterval(function () {
                        if (timeCheck > 0) {
                            // 30초 경과 후 팝업 닫히게
                            if (timeCheck == 150) {
                                $('.modal-con').removeClass('open');
                            }
                            timeCheck--;
                            $(".timer").html(timeCheck);
                        } else {
                            clearInterval(timeInterval);
                            timeCheck = initTime;
                            alert("SMS 인증번호 입력 대기시간이 3분을 초과했습니다. \n처음부터 다시 진행해 주시기 바랍니다.");
                            location.reload();
                        }
                    }, 1000);
                    var success_msg = (data.message).replace('\n', '<br>');
                    $('.md-txt').html(success_msg);
                    $('.modal-con').addClass('open');
                } else if (data.resultCode == "0") {
                    $('.info-wrap').css('display', 'block');
                    $('.certification-number').css('display', 'none');
                    $(".btn-m-login").attr("disabled", false);

                    if (data.errorCode == 'O') {
                        $('.certification-number').css('display', 'none');
                    }

                    alert(data.message);
                } else {
                    alert(data.message);
                }

                //alert(data.message);
            }
        });
    }

    function sendSmsAuthCheck() {

        if ($("#serial").val() == null || $("#serial").val() == '') {
            alert("문자로 발송된 인증번호를 입력하세요.");
            return;
        }

        var flag = '0';
        if (failOptYn == 'Y')
            flag = '2';
        else
            flag = '1';

        var requestData = {
            serial: $("#serial").val(),
            flag: flag
        };

        $.ajax({
            url: loginMobileEndpoint('sendSmsAuthCheckAjax'),
            async: false,
            type: 'POST',
            timeout: 3000,
            dataType: "json",
            data: JSON.stringify(requestData),
            error: function (e) {
                //console.log(e);
            },
            success: function (data) {
                if (data.message != null && data.message != '')
                    alert(data.message);

                if (data.resultCode == "1") {
                    $("#loginForm").attr({
                        action: data.resultUrl
                    }).submit();
                } else if (data.resultCode == "2") {
                    $("#loginForm").attr({
                        action: url
                    }).submit();
                }
            }
        });
    }

    if (location.href.match('ktedu.kt.co.kr')) {
        alert('접속하신 도메인은 미사용 도메인으로 사용이 불가합니다.\nktedu.kt.com으로 접속 부탁드립니다.\n잠시 후 ktedu.kt.com으로 연결됩니다.');
        window.location.href = 'https://ktedu.kt.com';
    }
</script>
<!-- 스트립트 정의 끝 -->