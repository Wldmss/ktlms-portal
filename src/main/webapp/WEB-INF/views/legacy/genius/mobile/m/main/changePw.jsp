<%--
  Migrated from genius: WebContent/WEB-INF/jsp/mobile/mobile/m/main/changePw.jsp
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
 * @menu    : 최초 로그인 시 P/W변경
 * @source  : changePw.jsp
 * @desc    : aivle, 경기도교육청 계정으로 최초 로그인 시 비밀번호 변경
 * -----------------------------------------------------------------------------
 * VER  DATE               AUTHOR             DESCRIPTION
 * ---  -----------------  -----------------  ----------------------------------
 * 1.0  2015-10-03         David.kim          최초 작성
 * 1.1  
 * -------------------------------------------------------------------------- */
--%>
<%@ page import="com.anymobi.common.Constants" %>
<%@ page pageEncoding="UTF-8" %>

<%@ include file="/WEB-INF/jsp/mobile/common/INC_Header.jsp" %>
<!-- 동적 리소스 정의 시작 -->
<script src="/anymobi/lib/js/jquery/jquery.numeric.min.js"></script>
<script src="/anymobi/lib/js/jquery-ui-1.11.4/jquery-ui.min.js"></script>
<script src="/anymobi/lib/js/shortcut.js"></script>
<link rel="stylesheet" type="text/css" href="/anymobi/mobile/css/login.css"/>


<script>

    if (navigator.userAgent.toLowerCase().indexOf('ipad') > -1 || navigator.userAgent.toLowerCase().indexOf('ipod') > -1 || navigator.userAgent.toLowerCase().indexOf('iphone') > -1) {
        document.write('<link rel="apple-touch-icon" sizes="29x29" href="/anymobi/mobile/img/icon/ios_29.png" />');
        document.write('<link rel="apple-touch-icon" href="/anymobi/mobile/img/icon/ios_58.png" />');
        document.write('<link rel="apple-touch-icon" sizes="76x76" href="/anymobi/mobile/img/icon/ios_76.png" />');
        document.write('<link rel="apple-touch-icon" sizes="87x87" href="/anymobi/mobile/img/icon/ios_87.png" />');
        document.write('<link rel="apple-touch-icon" sizes="152x152" href="/anymobi/mobile/img/icon/ios_152.png" />');
        document.write('<link rel="apple-touch-icon" sizes="180x180" href="/anymobi/mobile/img/icon/ios_180.png" />');
        document.write('<link rel="apple-touch-icon" sizes="1024x1024" href="/anymobi/mobile/img/icon/ios_1024.png" />');
    } else {
        document.write('<link rel="apple-touch-icon-precomposed" href="/anymobi/mobile/img/icon/and_72.png" />');
        document.write('<link rel="apple-touch-icon-precomposed" sizes="96x96" href="/anymobi/mobile/img/icon/and_96.png" />');
        document.write('<link rel="apple-touch-icon-precomposed" sizes="144x144" href="/anymobi/mobile/img/icon/and_144.png" />');
        document.write('<link rel="apple-touch-icon-precomposed" sizes="1024x1024" href="/anymobi/mobile/img/icon/and_1024.png" />');
    }
</script>
<link rel="apple-touch-icon" sizes="29x29" href="/anymobi/mobile/img/icon/ios_29.png">
<link rel="apple-touch-icon" href="/anymobi/mobile/img/icon/ios_58.png">
<link rel="apple-touch-icon" sizes="76x76" href="/anymobi/mobile/img/icon/ios_76.png">
<link rel="apple-touch-icon" sizes="87x87" href="/anymobi/mobile/img/icon/ios_87.png">
<link rel="apple-touch-icon" sizes="152x152" href="/anymobi/mobile/img/icon/ios_152.png">
<link rel="apple-touch-icon" sizes="180x180" href="/anymobi/mobile/img/icon/ios_180.png">
<link rel="apple-touch-icon" sizes="1024x1024" href="/anymobi/mobile/img/icon/ios_1024.png">


<link rel="stylesheet" href="/newPortal/css/common.css">
<link rel="stylesheet" href="/newPortal/css/page.css">
<style>
    .icon::before {
        display: initial;

    }

    .form-wrap .select-wrap:not(:first-child) {
        float: right;
    }

    ul {
        font-size: 15px;
    }

    .sub-title {
        padding: 0px;
    }
</style>
<link rel="stylesheet" type="text/css" href="/anymobi/mobile/css/login.css">

<!--마크업확인용 스타일 : 개발반영 안함-->
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

    .mobile-pwchange {
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
    }

    .login-form-wrap.on {
        margin-top: 12px;
        opacity: 1;
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
    }

    .inp-text input:focus {
        outline: none;
    }

    .inp-text input + input {
        margin-top: 8px;
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

    .mobile-pwchange .desc {
        padding: 0 5px 15px 5px;
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

    #pwchangePopup .popup-content {
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

    .btn-box {
        margin-top: 12px;
        margin-bottom: 15px;
        display: flex;
        justify-content: space-between;
    }

    .btn-box button {
        height: 40px;
        line-height: 38px;
        border-radius: 8px;
        color: #fff;
        flex: 0 0 49%;
    }

    .btn-box .btn-reset {
        background-color: #4c4c4c;
    }

    .btn-box .btn-save {
        background-color: #FE2E36;
    }
</style>
<!--마크업확인용 스타일 : css에 별도 저장-->


<body>
<div id="wrap">

    <form id="loginForm" name="loginForm" method="post" autocomplete="off">
        <section class="mobile-pwchange">
            <h1 class="m-logo">
                <a href="#none">
                    <img src="/newPortal/images/new23_login_top_title.svg" alt="">
                </a>
            </h1>
            <article class="login-form-wrap">
                <a href="#none"><img src="/newPortal/icons/new23_logo.png" alt=""></a>
                <div class="inp-wrap">
                    <div class="inp-text">
                        <input type="password" id="userPW1" name="userPW1" class="j-id" placeholder="기존비밀번호">
                        <input type="password" id="userPW2" name="userPW2" class="j-pw" placeholder="새 비밀번호">
                        <input type="password" id="userPW3" name="userPW3" class="j-pw" placeholder="새 비밀번호 확인">
                    </div>
                </div>
                <div class="btn-box">
                    <button type="button" class="btn-reset" onclick="javascript:doReset();">초기화</button>
                    <button type="button" class="btn-save" onclick="javascript:doConfirmPwd();">저장</button>
                </div>
                <div class="info-wrap">
                    <!-- [D] 태블릿에서만 노출 -->
                    <p class="desc">
                        * 로그인 아이디, 비밀번호는<br>KATE/KTalk 아이디(사번), 비밀번호와 동일합니다.
                    </p>
                    <!-- // [D] 태블릿에서만 노출 -->
                    <div class="btn-wrap">
                        <a href="#pwchangePopup" class="btn-inquiry">
                            문의 및 연락처
                        </a>
                    </div>
                </div>

            </article>

        </section>

        <input type="hidden" name="url" id="url" value="${input.url}">
    </form>

</div>

<!--// 로그인, 인증번호 팝업 시작 -->
<div class="cc-popup-con j-pop-alert jc-hide">
    <p class="txt-alert"></p>
    <div class="btn-box"><a class="submit j-pop-close" href="javascript:void(0);">확 인</a></div>
</div>
<!-- 로그인, 인증번호 팝업 끝 //-->

<!--// 문의및연락처 팝업 시작 -->
<div id="pwchangePopup" class="popup-container">
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
<!-- 문의및연락처 팝업 끝 //-->


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
    if (isMobile())
        url = '/mobile/m/login';

    $(document).ready(function () {

        $("body").append("<div id='coverbox'></div>");

        // 팝업 : 닫기
        $(".j-pop-close").click(function () {
            $(".jc-hide, #coverbox").hide();
        });

        // 문의및연락처 팝업 : 열기
        $(".j-btn-contact").click(function () {
            $(".j-pop-contact, #coverbox").show();
        });

    });

    PopExecutor = {
        goRoadmapSet: function (userid) {
            var url = "http://localhost:8081/nsso_auth.jsp?target=kate_return&p_userid=" + userid + "&p_year=2016&p_orderseq=1&sso_yn=Y&p_head_yn=N&return_page=roadmap_set";
            window.open(url, "roadmap", "toolbar=no,status=no,menubar=no,scrollbars=no,location=no,resizable=yes,width=1100,height=600,left=0,top=0");
        },
        goRoadmapExam: function (userid) {
            var url = "http://localhost:8081/nsso_auth.jsp?target=kate_return&p_userid=" + userid + "&p_year=2016&p_orderseq=1&sso_yn=Y&p_head_yn=Y&return_page=roadmap_exam";
            window.open(url, "roadmap", "toolbar=no,status=no,menubar=no,scrollbars=no,location=no,resizable=yes,width=1100,height=600,left=0,top=0");
        },
        goRoadmapResult: function (userid) {
            var url = "http://localhost:8081/nsso_auth.jsp?target=kate_return&p_userid=" + userid + "&p_year=2016&p_orderseq=1&sso_yn=Y&p_head_yn=Y&return_page=roadmap_result";
            window.open(url, "roadmap", "toolbar=no,status=no,menubar=no,scrollbars=no,location=no,resizable=yes,width=1100,height=600,left=0,top=0");
        },
        goHistory: function () {
            var url = "<c:url value="/academy/history/popHistory" />";
            window.open(url, "history", "toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1100,height=600,left=0,top=0");
        },

        goFacilitiesGuide: function () {
            var url = "<c:url value="/academy/facilitiesGuide/popFacilitiesGuideWonju" />";
            window.open(url, "facilitiesLeased", "toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=no,width=1100,height=910,left=0,top=0");
        }
    };

    /* shortcut.add("Enter", function() {
        CtlExecutorLoginProc();
    }); */

    function goPrivacyPolicy() {
        var url = "<c:url value="/resources/html/policy/privacyPolicy.html" />";
        window.open(url, "history", "toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1100,height=600,left=0,top=0");
    };

    function doReset() {
        $("#userPW1").val("");
        $("#userPW2").val("");
        $("#userPW3").val("");
    }

    //초기 비번변경
    function doConfirmPwd() {
        var p_pwd = $.trim($("#userPW1").val());
        var n_pwd = $.trim($("#userPW2").val());
        var c_pwd = $.trim($("#userPW3").val());
        var compCd = "${output.s_comp}";
        var retUrl = "";

        if (compCd == "8888") {
            retUrl = "/education/list/courseList?type=MPACK";
        }

        if (p_pwd == "") {
            alert("기존 비밀번호를 입력해 주세요.");
            $("#userPW1").focus();
            return;
        }

        var s_pwd = "${output.s_pwd}";
        if (p_pwd != s_pwd) {
            alert("기존 비밀번호가 틀립니다.");
            $("#userPW1").focus();
            return;
        }

        if (n_pwd == "") {
            alert("새 비밀번호를 입력해 주세요.");
            $("#userPW2").focus();
            return;
        }

        if (c_pwd == "") {
            alert("새 비밀번호 확인을 입력해 주세요.");
            $("#userPW3").focus();
            return;
        }

        if (n_pwd != c_pwd) {
            alert("새 비밀번호 확인이 맞지않습니다.\n다시 입력해주세요.");
            $("#userPW3").focus();
            return;
        }

        if (s_pwd == c_pwd) {
            alert("변경할 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
            $("#userPW2").focus();
            return;
        }

        if (c_pwd.length < 8) {
            alert("비밀번호의 길이는 8자 이상 이어야 합니다.");
            $("#userPW2").focus();
            return;
        }

        var regPass = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*\W)/;
        if (!regPass.test(c_pwd)) {
            alert("비밀번호에는 영문자, 숫자, 특수문자가 최소 1개씩 포함 되어야 합니다.");
            $("#userPW2").focus();
            return;
        }

        c_pwd = c_pwd.replace(/%/gi, '%25');
        c_pwd = c_pwd.replace(/\+/gi, '%2B');
        s_pwd = s_pwd.replace(/%/gi, '%25');
        s_pwd = s_pwd.replace(/\+/gi, '%2B');

        var requestData = {
            conf_userid: "${output.s_userid}",
            conf_pwd: escape(encodeURIComponent(c_pwd)),
            userpwd: escape(encodeURIComponent(s_pwd))
        };

        $.ajax({
            url: '/mobile/m/login/confirmPwdAjax',
            async: false,
            type: 'POST',
            timeout: 3000,
            dataType: "json",
            data: JSON.stringify(requestData),
            error: function (e) {
                //console.log(e);
            },
            success: function (data) {

                if (data.error == "0") {//에러
                    alert("에러 관리자에게 문의바랍니다");
                } else if (data.error == "1") {//성공
                    alert("비밀번호 변경이 완료되었습니다.");
                    $("#loginForm").attr({
                        action: "/mobile/m/login"
                    }).submit();
                } else if (data.error == "2") {//비밀번호 동일
                    alert("변경할 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
                    $("#userPW2").focus();
                } else if (data.error == "3") {//비밀번호 복잡성 체크
                    alert("비밀번호의 최소 길이는 8자이상이며 영문자, 숫자, 특수문자가 최소 1개씩 포함 되어야 합니다.");
                    $("#userPW2").focus();
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