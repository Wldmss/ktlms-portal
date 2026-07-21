<%--
  Migrated from genius: WebContent/WEB-INF/jsp/web/main/changePw.jsp
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
<%-- 공통 동적 리소스 정의 시작  --%>
<%@ include file="/WEB-INF/jsp/web/common/INC_Header.jsp" %>
<!-- 공통 동적 리소스 정의 끝 -->


<script src="/anymobi/lib/js/jquery/jquery.numeric.min.js"></script>
<script src="/anymobi/lib/js/jquery-ui-1.11.4/jquery-ui.min.js"></script>

<script src="/anymobi/lib/placeholder/jquery.placeholder.js"></script>
<script src="/anymobi/lib/js/shortcut.js"></script>
<script src="/anymobi/lib/js/common.js"></script>
<script src="/anymobi/js/ktlms.common.js"></script>
<!-- 공통 동적 리소스 정의 끝 -->


<!-- 동적 리소스 정의 시작 -->
<link rel="stylesheet" type="text/css" href="/anymobi/css/login.css"/>
<script type="text/javascript">
    if (location.href.match('ktedu.kt.co.kr')) {
        alert('접속하신 도메인은 미사용 도메인으로 사용이 불가합니다.\nktedu.kt.com으로 접속 부탁드립니다.\n잠시 후 ktedu.kt.com으로 연결됩니다.');
        window.location.href = 'https://ktedu.kt.com';
    }
</script>

<style type="text/css">
    .ui-widget.ui-widget-content {
        font-size: 15px;
        border-color: #ededed;
        box-shadow: 0 2px 20px 0 rgba(0, 0, 0, 0.2);
        border-radius: 5px;
    }

    .ui-widget-header {
        background-color: #fff;
        border: none;
    }

    .ui-dialog .ui-dialog-titlebar-close {
        border: none;
        background-color: transparent;
    }

    /* .ui-icon, .ui-widget-content .ui-icon {
        background: url(/newPortal/icons/popup-close-icon.png) no-repeat center/100%; }

    .ui-state-hover .ui-icon, .ui-state-focus .ui-icon, .ui-button:hover .ui-icon, .ui-button:focus .ui-icon {
        background: url(/newPortal/icons/popup-close-icon.png) no-repeat center/100%; } */

    .ui-dialog .ui-dialog-content {
        font-weight: 400;
        vertical-align: baseline;
        box-sizing: border-box;
        letter-spacing: -0.8px;
        font-size: 14px;
    }

    .ui-dialog .ui-dialog-buttonpane button {
        margin: 0;
        margin-top: 8px;
    }

    .ui-state-active,
    .ui-widget-content .ui-state-active,
    .ui-widget-header .ui-state-active,
    a.ui-button:active,
    .ui-button:active,
    .ui-button.ui-state-active:hover,
    .ui-state-hover,
    .ui-widget-content .ui-state-hover,
    .ui-widget-header .ui-state-hover,
    .ui-state-focus,
    .ui-widget-content .ui-state-focus,
    .ui-widget-header .ui-state-focus,
    .ui-button:hover,
    .ui-button:focus {
        border-color: #EB676A;
        background-color: #EB676A;
        color: #fff;
    }

    .ui-button.ui-dialog-titlebar-close:active,
    .ui-button.ui-dialog-titlebar-close:hover,
    .ui-button.ui-dialog-titlebar-close:focus {
        background-color: transparent;
        border-color: none;
    }

    .ui-dialog .ui-dialog-buttonpane {
        border: none;
    }

    .ui-dialog-buttonpane .ui-button:last-child {
        margin-left: 10px;
        border-color: #EB676A;
        background-color: #fff;
        color: #EB676A;
    }

    .ui-dialog-buttonpane .ui-button:first-child {
        border-color: #EB676A;
        background-color: #EB676A;
        color: #fff;
    }
</style>
<style>
    body {
        min-width: auto;
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

    .modal-con {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
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

    .modal-con > div {
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

    .modal-con.open {
        visibility: visible;
    }

    .modal-con.open > div {
        visibility: visible;
        -webkit-transform: scale(1);
        transform: scale(1);
        -webkit-transition: -webkit-transform 0.4s;
        transition: transform 0.4s;
    }

    #footer {
        width: 100%;
        border-top: 0 none;
    }

    #footer > div {
        width: 100%;
        padding: 12px 0 40px 0;
    }

    .enter-box {
        position: relative;
        margin-top: 0;
        margin-left: 0;
        background-image: url(/newPortal/icons/new23_logo.png);
        background-color: #fff;
        box-shadow: 0px 4px 16px 0px rgba(221, 221, 221, 0.60);
        animation: fadein 1.5s;
    }

    @keyframes fadein {
        from {
            opacity: 0;
        }
        to {
            opacity: 1;
        }
    }

    .enter-box::before {
        content: "";
        position: absolute;
        background: url(/newPortal/images/new23_login_top_title.svg) no-repeat center / contain;
        width: 260px;
        height: 37px;
        top: -46px;
        left: 50%;
        transform: translateX(-50%);
    }

    .enter-box input {
        background-color: #E8F0FE;
        border-radius: 4px;
        box-shadow: none;
        width: 250px !important;
    }

    #footer .policy-btn {
        top: -50px;
        left: 40px;
        right: auto;
        bottom: auto;
        margin-top: 0;
        border-radius: 4px;
        background: rgba(0, 0, 0, 0.45);
        border: 0 none;
    }

    .section {
        height: 800px;
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
</head>
<!-- 동적 리소스 정의 끝 -->
<body>
<c:set var="service_type" value="<%=Constants.VL_SERVER_TYPE %>"/>
<div id="wrap-login">
    <!-- 		<div class="header"> -->
    <!-- <div class="nav">
        <a onclick="javascript:PopExecutor.goHistory();">연혁</a>
        <span>|</span>
        <a onclick="javascript:PopExecutor.goFacilitiesGuide();">연수원안내</a>
    </div> -->
    <!-- 		</div> -->

    <form id="loginForm" name="loginForm" method="post" autocomplete="off">

        <input type="hidden" id="loginUserId" name="loginUserId" value="">
        <input type="hidden" id="loginPwd" name="loginPwd" value="">
        <!-- 20211101 : 마크업 확인용. 내부 스타일 개발 반영 없음  -->
        <style>
            .pw-change {
                width: 455px;
                padding-left: 50px;
            }

            .pw-change .def {
                position: relative;
            }

            .pw-change .def:first-child {
                margin-top: 10px;
            }

            .pw-change .def label {
                position: absolute;
                left: -110px;
                top: 19px;
                color: #333333;
                font-size: 15px;
            }

            .pw-change.enter-box > p {
                margin-right: 0
            }

            .btn-wrap {
                display: table;
                width: calc(100% + 5px);
                border-spacing: 5px;
                margin-left: -10px;
            }

            .btn-wrap .btn {
                display: table-cell;
                border-radius: 5px;
                background: #777;
                height: 37px;
                vertical-align: middle;
                color: #fff;
                font-size: 15px;
                font-weight: 600;
            }

            .btn-wrap .btn.btn-red {
                background: #ee0000;
            }

            /* 1219 추가 */
            .btn-wrap {
                margin-top: 10px !important;
            }

            .btn-wrap .btn {
                color: #fff !important;
                border: 0 none !important;
                font-weight: 400 !important;
                font-size: 16px;
            }

            .btn-bg-gray {
                background-color: #4c4c4c !important;
            }

            .btn-bg-red {
                background-color: #FE2E36 !important;
            }
        </style>
        <!-- //20211101 : 마크업 확인용. 내부 스타일 개발 반영 없음  -->
        <div class="section">
            <div class="enter-box pw-change"><!-- 20211101 : 클래스 추가 -->
                <!-- 20211101 : 추가 -->
                <p class="def">
                    <label for="pwd">기존 비밀번호</label>
                    <input id="pwd" name="pwd" type="password">
                </p>
                <p class="def">
                    <label for="newPwd">새 비밀번호</label>
                    <input id="newPwd" name="newPwd" type="password">
                </p>
                <p class="def">
                    <label for="newPwdConfirm">새 비밀번호 확인</label>
                    <input id="newPwdConfirm" name="newPwdConfirm" type="password">
                </p>
                <!-- //20211101 : 추가 -->
                <div class="btn-wrap">
                    <a class="btn btn-gray" href="" id="">취소</a>
                    <a class="btn btn-red" href="javascript:doConfirmPwd();" id="">저장</a>

                </div>


                <span></span>
                <a class="inquiry-btn"><img src="/anymobi/img/login/btn_popup_account_info.png" alt="문의 및 연락처"></a>
                <div class="inquiry-popup">
                    <!--// 추가_2015-11-24 : 시작 //-->
                    <p>문의 및 연락처<a class="popup-close"></a></p>
                    <ul>
                        <li>
                            <div>○ 로그인 문의</div>
                            <p>- 아이디를 모르는 경우, 조직의 인사담당자에게<br>문의 하시기 바랍니다.</p>
                            <p>- 비밀번호를 분실한 경우, <a href="https://idms.kt.com/im/" target="_blank">idms.kt.com</a>에서<br>비밀번호
                                변경 또는 초기화 하시기 바랍니다.</p>
                            <p>* 사외망 등의 사유로 <a href="https://idms.kt.com/im/" target="_blank">idms.kt.com</a> 접속이<br>불가할
                                경우 조직의 인사담당자 또는 아래<br>연락처로 문의 하시기 바랍니다.<br>※ 1588-3391 -&gt; 1번(KOS무선)</p>
                        </li>
                        <li>○ 시스템 문의 : 1588-3391 (1 &gt; 1)</li>
                        <li>○ 이러닝 문의 : 1577-0263</li>
                        <li>○ KT AIVLE스쿨 문의처 : 담당 에이블 매니저</li>
                    </ul>
                </div>
                <!--// 추가_2015-11-24 : 끝 //-->
                <!--// step 1 ///-->
                <br>

            </div>
        </div>
        <input type="hidden" name="url" id="url" value="/myclass/course/myCourse/myCourseList?eduStep=MID">
    </form>
    <!-- 하단영역 정의 시작 -->
    <%@ include file="/WEB-INF/jsp/web/common/INC_Footer.jsp" %>
    <!-- 하단영역 정의 끝0 -->
</div>

<form id="mainForm" name="mainForm" method="post">
</form>
</body>
</html>
<!-- 스크립트 정의 시작 -->
<script type="text/javascript">

    $(document).ready(function () {
        $("#wrap-login").css({"background-image": "url(/newPortal/images/new23_img_login_full.png)"});

        // 문의 및 연락처 팝업 (추가_2015-11-24)
        $(".inquiry-btn").click(function () {
            $(".inquiry-popup").show();
        });
        // 문의 및 연락처 팝업 닫기 (추가_2015-11-24)
        $(".popup-close").click(function () {
            $(".inquiry-popup").hide();
        });

    });

    PopExecutor = {
        goFresh: function () {
            var url = "<c:url value="http://fresh.ncs-slp.com" />";
            window.open(url, "fresh", "");
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

    function goPrivacyPolicy() {
        var url = "<c:url value="/resources/html/policy/privacyPolicy.html" />";
        window.open(url, "history", "toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1100,height=600,left=0,top=0");
    }

    //초기 비번변경
    function doConfirmPwd() {
        var p_pwd = $.trim($("#pwd").val());
        var n_pwd = $.trim($("#newPwd").val());
        var c_pwd = $.trim($("#newPwdConfirm").val());
        var compCd = "${output.s_comp}";
        var retUrl = "";

        if (compCd == "8888") {
            retUrl = "/education/list/courseList?type=MPACK";
        }

        if (p_pwd == "") {
            alert("기존 비밀번호를 입력해 주세요.");
            $("#pwd").focus();
            return;
        }

        var s_pwd = "${output.s_pwd}";
        if (p_pwd != s_pwd) {
            alert("기존 비밀번호가 틀립니다.");
            $("#pwd").focus();
            return;
        }

        if (n_pwd == "") {
            alert("새 비밀번호를 입력해 주세요.");
            $("#newPwd").focus();
            return;
        }

        if (c_pwd == "") {
            alert("새 비밀번호 확인을 입력해 주세요.");
            $("#newPwdConfirm").focus();
            return;
        }

        if (n_pwd != c_pwd) {
            alert("새 비밀번호 확인이 맞지않습니다.\n다시 입력해주세요.");
            $("#newPwdConfirm").focus();
            return;
        }

        if (s_pwd == c_pwd) {
            alert("변경할 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
            $("#newPwd").focus();
            return;
        }

        if (c_pwd.length < 8) {
            alert("비밀번호의 길이는 8자 이상 이어야 합니다.");
            $("#newPwd").focus();
            return;
        }

        var regPass = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*\W)/;
        if (!regPass.test(c_pwd)) {
            alert("비밀번호에는 영문자, 숫자, 특수문자가 최소 1개씩 포함 되어야 합니다.");
            $("#newPwd").focus();
            return;
        }

        c_pwd = c_pwd.replace(/%/gi, '%25');
        c_pwd = c_pwd.replace(/\+/gi, '%2B');

        var requestData = {
            conf_userid: "${output.s_userid}",
            conf_pwd: escape(encodeURIComponent(c_pwd))
        };

        $.ajax({
            url: '/login/confirmPwdAjax',
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
                        action: "/login"
                    }).submit();
                } else if (data.error == "2") {//비밀번호 동일
                    alert("변경할 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
                    $("#newPwd").focus();
                } else if (data.error == "3") {//비밀번호 복잡성 체크
                    alert("비밀번호의 최소 길이는 8자이상이며 영문자, 숫자, 특수문자가 최소 1개씩 포함 되어야 합니다.");
                    $("#newPwd").focus();
                }
            }
        });
    }
</script>
<!-- 스트립트 정의 끝 -->