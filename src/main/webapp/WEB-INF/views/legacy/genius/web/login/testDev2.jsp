<%--
  Migrated from genius: WebContent/WEB-INF/jsp/web/login/testDev2.jsp
  Encoding converted to UTF-8. This file is kept as legacy lineage/reference;
  active login is routed through Spring Security + JWT in /WEB-INF/views/login/login.jsp.
--%>
<%@ page import="com.anymobi.common.Constants" %>
<%@ page pageEncoding="UTF-8" %>
<%-- 공통 동적 리소스 정의 시작  --%>
<%@ include file="/WEB-INF/jsp/web/common/INC_Header.jsp" %>
<!-- 공통 동적 리소스 정의 끝 -->

<!-- 동적 리소스 정의 시작 -->
<link rel="stylesheet" type="text/css" href="/newPortal/css/common.css"/>
<link rel="stylesheet" type="text/css" href="/anymobi/css/login.css?ver=1"/>
<script type="text/javascript">
    if (location.href.match('ktedu.kt.co.kr')) {
        alert('접속하신 도메인은 미사용 도메인으로 사용이 불가합니다.\nktedu.kt.com으로 접속 부탁드립니다.\n잠시 후 ktedu.kt.com으로 연결됩니다.');
        window.location.href = 'https://ktedu.kt.com';
    }
</script>
<style>
    body {
        overflow-y: hidden !important;
    }

    #wrap-login {
        width: 72.916%;
        padding: 0 0;
        border-radius: 40px;
        font-family: 'NotoSansKR', sans-serif;
    }

    @media (max-width: 1199px) {
        #wrap-login {
            background-color: #Ccc;
        }
    }

    #footer > div {
        padding-left: 0px;
    }

    .modal-con-otp {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 30%;
        z-index: 150;
        display: -webkit-box;
        display: -ms-flexbox;
        display: -webkit-flex;
        display: flex;
        -webkit-box-pack: center;
        -ms-flex-pack: center;
        -webkit-justify-content: center;
        justify-content: center;
        -webkit-box-align: center;
        -ms-flex-align: center;
        -webkit-align-items: center;
        align-items: center;
        visibility: hidden;
    }

    .modal-con-otp > div {
        position: relative;
        width: 380px;
        font-size: 14px;
        line-height: 1.5;
        background-color: #fff;
        border-radius: 6px;
        -webkit-box-shadow: 0 0 15px #d2d2d2;
        -moz-box-shadow: 0 0 15px #d2d2d2;
        -o-box-shadow: 0 0 15px #d2d2d2;
        -ms-box-shadow: 0 0 15px #d2d2d2;
        box-shadow: 0 0 15px #d2d2d2;
        word-break: keep-all;
        visibility: hidden;
        -webkit-transform: scale(0);
        transform: scale(0);
        -webkit-transition: -webkit-transform 0.3s, visibility 0s 0.3s;
        transition: transform 0.3s, visibility 0s 0.3s;
    }

    .md-close {
        display: block;
        position: absolute;
        top: 0;
        right: 0;
        width: 44px;
        height: 53px;
        background: url(/anymobi/img/btn_popup_x.png) no-repeat center;
    }

    .md-title {
        padding: 15px 55px 15px 15px;
        font-size: 15px;
        color: #e90811;
        border-bottom: 1px solid #bdbdbd;
    }

    .md-txt {
        padding: 15px;
    }

    .modal-con-otp.open {
        visibility: visible;
    }

    .modal-con-otp.open > div {
        visibility: visible;
        -webkit-transform: scale(1);
        transform: scale(1);
        -webkit-transition: -webkit-transform 0.4s;
        transition: transform 0.4s;
    }

    #footer {
        width: 100%;
    }

    #footer > div {
        width: 100%;
        padding: 12px 0 40px 0;
        margin-top: -20px;
        text-align: center;
    }

    .enter-box {
        position: relative;
        margin-top: 0;
        margin-left: 0;
        background-image: none !important;
        background-color: #fff;
        /* box-shadow: 0px 4px 16px 0px rgba(221, 221, 221, 0.60); */
        box-shadow: none !important;
    }


    .enter-box input {
        background-color: #E8F0FE;
        border-radius: 4px;
        box-shadow: none;
    }

    #footer .policy-btn {
        top: -30px;
        left: 50%;
        transform: translateX(-50%);
        right: auto;
        bottom: auto;
        margin-top: 0;
        border-radius: 4px;
        background: rgba(0, 0, 0, 0.45);
        border: 0 none;
    }

    .section {
        height: 750px;
        background-color: transparent;
        padding-top: 0;
    }

    #footer p {
        font-size: 14px;
        line-height: 22px;
        font-weight: 400;
    }

    #footer p:last-child {
        font-weight: 400;
    }
</style>


<% pageContext.setAttribute("replaceChar", "\n");
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", -1);
%>

<!-- 동적 리소스 정의 끝 -->
<body>
<div id="wrap-login">
    <!-- 		<div class="header"> -->
    <!-- 			<p style="color:red;font-size: 20pt;font-weight: bold;">프로젝트팀 소스</p> -->
    <!-- 			<div class="nav" style="display: none;"> -->
    <!-- <a onclick="javascript:PopExecutor.goHistory();">연혁</a> -->
    <!-- <span>|</span> -->
    <!-- <a onclick="javascript:PopExecutor.goFacilitiesGuide();">연수원안내</a> -->
    <!-- 			</div> -->
    <!-- 		</div> -->

    <form id="loginForm" name="loginForm" method="post" autocomplete="off">

        <input type="hidden" id="loginUserId" name="loginUserId" value="<c:out value='${input.userid}'/>"/>

        <div class="section">
            <div class="enter-box" style="padding-top: 30px; padding-bottom: 20px; width:100%;">
                <p style="font-size: 33px; height: auto !important; font-weight: bold; margin-bottom:20px; width:100%;">
                    2025 KICE 결과 조회</p>
                <p><input id="userid" name="userid" type="text"
                          onkeypress="if(event.keyCode==13) CtlExecutorLoginProc();"></p>
                <p class="def"><input id="pwd" name="pwd" type="password"
                                      onkeypress="if(event.keyCode==13) CtlExecutorLoginProc();"></p>
                <a class="login-btn" href="javascript:CtlExecutorLoginProc();" id="login-btn-id"
                   style="width: 100%;font-size: 18px; background-color: #E15257; width: 250px; line-height: 35px; color:#fff; border-radius: 8px;">로그인</a>
                <a class="login-btn" href="javascript:CtlExecutorLoginProc();" id="login-btn-id-grey"><img
                        src="/anymobi/img/login/btn_login_grey.png" alt="로그인"></a>
                <span id="loginMsg">* 로그인 아이디, 비밀번호는<br>KATE/KTalk 아이디(사번), 비밀번호와 동일합니다.</span>
                <a class="inquiry-btn"
                   style="font-size: 12px; border-radius: 8px; border: 1px solid #999; height: auto; padding: 8px 10px; margin-right: 20px;">
                    문의 및 연락처
                </a>
                <div class="inquiry-popup">
                    <p>문의 및 연락처<a class="popup-close"></a></p>
                    <ul>
                        <li>
                            <div>○ 로그인 문의</div>
                            <p>- 아이디를 모르는 경우, 조직의 인사담당자에게<br>문의 하시기 바랍니다.</p>
                            <p>- 비밀번호를 분실한 경우, <a href="https://idms.kt.com/im/" target="_blank">idms.kt.com</a>에서<br>비밀번호
                                변경 또는 초기화 하시기 바랍니다.</p>
                            <p>* 사외망 등의 사유로 <a href="https://idms.kt.com/im/" target="_blank">idms.kt.com</a> 접속이<br>불가할
                                경우 조직의 인사담당자 또는 아래<br>연락처로 문의 하시기 바랍니다.<br>※ 1588-3391 -> 1번(KOS무선)</p>
                        </li>
                        <li>○ 시스템 문의 : 1588-3391 (1 > 1)</a></li>
                        <li>○ 이러닝 문의 : 1577-0263</li>
                        <li>○ KT AIVLE스쿨 문의처 : 담당 에이블 매니저</li>
                    </ul>
                </div>            <!--// 추가_2015-11-24 : 끝 //-->
                <!--// step 1 ///-->
                <br/>

                <!--// step 2 SMS///-->

                <a id="reSendSmsAuth" class="login-btn" href="javascript:sendSmsAuth('R');" style="display: none;"><img
                        src="/anymobi/img/main/btn_popup_resend_code.png" alt="인증번호 재전송"/></a>
                <span id="authTime" style="display: none;">인증번호가 전송되었습니다. (남은시간: <span id="time">120</span>초)</span>
                <input id="smsSerial" class="login-btn" type="text" placeholder="인증번호입력" style="display: none;"
                       onkeypress="if(event.keyCode==13) sendSmsAuthCheck();"/>
                <a id="smsAuthConnect" class="login-btn" href="javascript:sendSmsAuthCheck();"
                   style="display: none;"><img src="/anymobi/img/login/btn_popup_code_confirm_off.png" alt="접속"/></a>

            </div>
        </div>
        <input type="hidden" name="url" id="url" value="<c:out value='${input.url}'/>">
    </form>
    <!-- 하단영역 정의 시작 -->
    <!--//  푸터 시작-->
    <div id="footer">
        <div>
            <p class="f-nbg" style="color: #EB676A;">본 플랫폼은 구글 크롬브라우저 1920*1080 해상도(배율100%)에 최적화 되어 있습니다.</p>
            <p class="address">(주)케이티 경기도 성남시 분당구 불정로 90 (정자동 206번지)</p>
            <p>Copyright ⓒ2020 kt corp. All rights reserved.</p>
            <a class="policy-btn" onclick="javascript:goPrivacyPolicy();">개인정보처리방침</a>

            <!--// 추가 //-->
        </div>
    </div>
    <!-- 푸터 끝//-->
    <!-- 하단영역 정의 끝0 -->
</div>

<form id="mainForm" name="mainForm" method="post">
</form>
<div class="modal-con-otp">
    <div>
        <a href="javascript:void(0);" class="md-close"></a>
        <p class="md-title">인증번호 발송</p>
        <div class="md-txt">기본 데이터 입니다.</div>
    </div>
</div>
</body>
</html>
<!-- 스크립트 정의 시작 -->
<script type="text/javascript">
    var failOptYn = 'N';
    var isDevice = "N";
    var preUrl = "/login";
    var failOptYn = 'N';

    if (isMobile()) {
        isDevice = "Y";
        preUrl = "/mobile/m/login";
    }

    $(document).ready(function () {

        // 문의 및 연락처 팝업 (추가_2015-11-24)
        $(".inquiry-btn").click(function () {
            $(".inquiry-popup").show();
        });
        // 문의 및 연락처 팝업 닫기 (추가_2015-11-24)
        $(".popup-close").click(function () {
            $(".inquiry-popup").hide();
        });

        $("#smsSerial").numeric();

        if ($.trim($("#loginUserId").val()).length > 0) {
            $('#userid').val($("#loginUserId").val());
        }

        //2018-08-20 로그인시 패스워드 문제로 ajax로 변경
        $("#login-btn-id").show();
        $("#login-btn-id-grey").hide();

        //모달 팝업 닫기
        $('.md-close').click(function () {
            $('.modal-con-otp').removeClass('open');
        });

        //인증번호 입력 시 팝업 닫기
        $(document).on('keydown', '#smsSerial', function () {
            $('.modal-con-otp').removeClass('open');
        });

        //외부영역 클릭 시 팝업 닫기
        $(document).mouseup(function (e) {
            if ($('.modal-con-otp').has(e.target).length === 0) {
                $('.modal-con-otp').removeClass('open');
            }
        });

    });

    //팝업 닫기
    $('.l-popup.notice .close-btn').on('click', function (e) {
        $('.l-popup.notice').css('display', 'none');
    });

    //외부영역 클릭 시 팝업 닫기
    $(document).mouseup(function (e) {
        if ($('.l-popup').has(e.target).length === 0) {
            $('.l-popup').css('display', 'none');
        }
    });

    function loginEndpoint(action) {
        return "/login/" + action;
    }

    CtlExecutor = {
        doLoginProc: function () {
            var p_pwd = $.trim($("#pwd").val());
            var userId = $.trim($("#userid").val());

            if (userId == "") {
                alert("아이디를 입력해 주세요");
                $("#userid").focus();
                return;
            } else if (p_pwd == "") {
                alert("비밀번호를 입력해 주세요.");
                $("#pwd").focus();
                return;
            }
            p_pwd = p_pwd.replace(/%/gi, '%25');
            p_pwd = p_pwd.replace(/\+/gi, '%2B');
            p_pwd = escape(encodeURIComponent(p_pwd));

            var requestData = {
                userid: userId,
                pwd: p_pwd,
                url: escape(encodeURIComponent($("#url").val()))
            };

            $.ajax({
                url: loginEndpoint("examLoginProcAjax"),
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
                            $("#loginMsg").hide();
                            $(".inquiry-btn").hide();
                            $("#login-btn-id").hide();
                            $("#login-btn-id-grey").show();
                            if (data.lgFailCnt == 5) {
                                failOptYn = 'Y';
                                alert('비밀번호 5회 연속 오류로 계정잠금되어 로그인이 불가합니다.\nSMS 번호인증으로 계정잠금을 해제해 주시기 바랍니다.\n비밀번호 분실에 따른 변경, 초기화는 `문의 및 연락처`를 참고하세요.');
                            }
                            sendSmsAuth('N');
                        } else {
                            alert(data.message);
                            $("#login-btn-id").show();
                            $("#login-btn-id-grey").hide();
                            $("#pwd").val('');

                            if (data.errorCode == 'O') {
                                $("#reSendSmsAuth").hide();
                                $("#authTime").hide();
                                $("#smsSerial").hide();
                                $("#login-btn-id-grey").hide();
                                $("#smsAuthConnect").hide();
                            }
                            if (data.isOk == -9) {
                                window.open('https://ktsso.kt.com/ssologin/guide/pwdTabOrg.html', 'popPwdExpire', 'width:520px, heigth:590');
                            }
                        }
                    } else if (data.result == 'Y') {
                        $("#mainForm").attr({
                            action: data.resultUrl
                        }).submit();
                    } else {
                        alert('로그인시 오류가 발생하였습니다. 관리자에게 문의바랍니다.');
                        $("#login-btn-id").show();
                        $("#login-btn-id-grey").hide();
                    }
                }
            });

        }
    };

    PopExecutor = {
        goHistory: function () {
            var url = "<c:url value="/academy/history/popHistory" />";
            window.open(url, "history", "toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1100,height=600,left=0,top=0");
        }
    };

    function goPrivacyPolicy() {
        var url = "<c:url value="/resources/html/policy/privacyPolicy.html" />";
        window.open(url, "history", "toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=no,width=1100,height=600,left=0,top=0");
    }

    var initTime = 180;
    var timeCheck = initTime;
    var timeInterval = null;
    var isAdmin = '${sessionScope.sessionMemberInfo.isadmin}';

    function sendSmsAuth() {

        $.ajax({
            url: loginEndpoint('sendSmsOptAuthAjax'),
            async: false,
            type: 'POST',
            timeout: 3000,
            dataType: "json",
            //data : JSON.stringify(requestData),
            error: function (e) {
            },
            success: function (data) {
                if (data.resultCode == "1") {
                    $("#authTime").show();
                    $("#smsSerial").show();

                    $("#reSendSmsAuth").show();
                    $("#smsAuthConnect").show();

                    $("#time").html(timeCheck);
                    clearInterval(timeInterval);
                    timeCheck = initTime;
                    $("#time").html(timeCheck);
                    $("#smsSerial").val("");
                    $("#smsSerial").focus();
                    timeInterval = setInterval(function () {
                        if (timeCheck > 0) {
                            // 30초 경과 후 팝업 닫히게
                            if (timeCheck == 150) {
                                $('.modal-con-otp').removeClass('open');
                            }
                            timeCheck--;
                            $("#time").html(timeCheck);
                        } else {
                            clearInterval(timeInterval);
                            $("#authTime").hide();
                            timeCheck = initTime;
                            alert("SMS 인증번호 입력 대기시간이 3분을 초과했습니다. \n처음부터 다시 진행해 주시기 바랍니다.");
                        }
                    }, 1000);
                    var success_msg = (data.message).replace('\n', '<br>');
                    $('.md-txt').html(success_msg);
                    $('.modal-con-otp').addClass('open');
                } else if (data.resultCode == "0") {
                    $("#login-btn-id").show();
                    $("#login-btn-id-grey").hide();
                    $("#loginMsg").show();
                    $(".inquiry-btn").show();

                    if (data.errorCode == 'O') {
                        $("#reSendSmsAuth").hide();
                        $("#authTime").hide();
                        $("#smsSerial").hide();
                        $("#login-btn-id-grey").hide();
                        $("#smsAuthConnect").hide();
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
        if ($("#smsSerial").val() == null || $("#smsSerial").val() == '') {
            alert("문자로 발송된 인증번호를 입력하세요.");
            return;
        }

        var flag = '0';
        if (failOptYn == 'Y')
            flag = '2';
        else
            flag = '1';

        var requestData = {
            serial: $("#smsSerial").val(),
            flag: flag
        };

        $.ajax({
            url: loginEndpoint('sendSmsAuthCheckAjax'),
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
                    $("#mainForm").attr({
                        action: data.resultUrl
                    }).submit();
                } else if (data.resultCode == "2") {
                    $("#loginForm").attr({
                        action: '/login/testDev'
                    }).submit();
                }
            }
        });
    }

    function isMobile() {
        // 모바일&태블릿검사
        if ((navigator.userAgent.toLowerCase().indexOf('ipad')) > -1 ||
            (navigator.userAgent.toLowerCase().indexOf('iphone')) > -1 ||
            (navigator.userAgent.toLowerCase().indexOf('ipad') > -1 && navigator.maxTouchPoints > 1) ||
            (navigator.userAgent.toLowerCase().indexOf('iphone') > -1 && navigator.maxTouchPoints > 1) ||
            (navigator.userAgent.toLowerCase().indexOf('mac os') > -1 && navigator.maxTouchPoints > 1) ||
            (navigator.userAgent.toLowerCase().indexOf('android') > -1 && navigator.userAgent.toLowerCase().indexOf('mobile') == -1)) {
            return true;
        }

        var galaxyTabModel = new Array('shw-', 'tab', 'tablet', 'pad');
        for (var i = 0; i < galaxyTabModel.length; i++) {
            if (navigator.userAgent.toLowerCase().indexOf(galaxyTabModel[i]) > -1) {
                return true;
            }
        }

        // 모바일검사
        var mobileModel = new Array('iphone', 'ipod', 'ipad', 'android', 'blackberry', 'windows ce', 'nokia', 'webos', 'opera mini', 'sonyericsson', 'opera mobi', 'iemobile')
        for (var j = 0; j < mobileModel.length; j++) {
            if (navigator.userAgent.toLowerCase().indexOf(mobileModel[j]) > -1) {
                return true;
            }
        }

        return false;
    }
</script>
<!-- 스트립트 정의 끝 -->