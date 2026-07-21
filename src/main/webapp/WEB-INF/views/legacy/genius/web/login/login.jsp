<%--<%@ page pageEncoding="UTF-8" %>--%>
<%--&lt;%&ndash; 공통 동적 리소스 정의 시작  &ndash;%&gt;--%>
<%--<%@ include file="/WEB-INF/jsp/web/common/INC_Header.jsp" %>--%>
<%--<script src="/common/js/jsencrypt.min.js"></script>--%>

<%--<!-- 공통 동적 리소스 정의 끝 -->--%>

<%--<!-- 동적 리소스 정의 시작 -->--%>
<%--<link rel="stylesheet" type="text/css" href="/anymobi/css/login.css?ver=1"/>--%>
<%--<script type="text/javascript">--%>
<%--    if (location.href.match('ktedu.kt.co.kr')) {--%>
<%--        alert('접속하신 도메인은 미사용 도메인으로 사용이 불가합니다.\nktedu.kt.com으로 접속 부탁드립니다.\n잠시 후 ktedu.kt.com으로 연결됩니다.');--%>
<%--        window.location.href = 'https://ktedu.kt.com';--%>
<%--    }--%>
<%--</script>--%>
<%--<style>--%>
<%--    html, body {--%>
<%--        /* min-width: auto; */--%>
<%--        overflow: hidden !important;--%>
<%--        height: 100%;--%>
<%--    }--%>

<%--    html::-webkit-scrollbar, body::-webkit-scrollbar {--%>
<%--        display: none;--%>
<%--    }--%>

<%--    /*** 모바일웹 : 접속 차단안내 팝업 S ***/--%>
<%--    .pop-info {--%>
<%--        position: fixed;--%>
<%--        width: 100%;--%>
<%--        height: 100%;--%>
<%--        background-color: #ffffff;--%>
<%--        z-index: 1;--%>
<%--    }--%>

<%--    .pop-info p {--%>
<%--        position: absolute;--%>
<%--        transform: translate(-50%, -50%);--%>
<%--        top: 45%;--%>
<%--        left: 50%;--%>
<%--        width: min(90%, 700px);--%>
<%--        font-size: 18px;--%>
<%--        line-height: 26px;--%>
<%--        text-align: center;--%>
<%--        padding: 30px 10px;--%>
<%--        border-radius: 8px;--%>
<%--        background: #eee;--%>
<%--    }--%>

<%--    .pop-info p span {--%>
<%--        font-size: 20px;--%>
<%--    }--%>

<%--    .pop-info strong {--%>
<%--        font-weight: 500;--%>
<%--    }--%>

<%--    @media (max-width: 768px) {--%>
<%--        .pop-info p {--%>
<%--            font-size: 14px;--%>
<%--            line-height: 1.5;--%>
<%--        }--%>

<%--        .pop-info p span {--%>
<%--            font-size: 16px;--%>
<%--        }--%>
<%--    }--%>

<%--    /*** //모바일웹 : 접속 차단안내 팝업 E ***/--%>

<%--    #wrap-login {--%>
<%--        width: 72.916%;--%>
<%--        padding: 0 0;--%>
<%--        border-radius: 40px;--%>
<%--        font-family: 'NotoSansKR', sans-serif;--%>
<%--    }--%>

<%--    @media (max-width: 1199px) {--%>
<%--        #wrap-login {--%>
<%--            background-color: #Ccc;--%>
<%--        }--%>
<%--    }--%>

<%--    #footer > div {--%>
<%--        padding-left: 0px;--%>
<%--    }--%>

<%--    .modal-con-otp {--%>
<%--        position: fixed;--%>
<%--        top: 0;--%>
<%--        left: 0;--%>
<%--        width: 100%;--%>
<%--        height: 30%;--%>
<%--        z-index: 150;--%>
<%--        display: -webkit-box;--%>
<%--        display: -ms-flexbox;--%>
<%--        display: -webkit-flex;--%>
<%--        display: flex;--%>
<%--        -webkit-box-pack: center;--%>
<%--        -ms-flex-pack: center;--%>
<%--        -webkit-justify-content: center;--%>
<%--        justify-content: center;--%>
<%--        -webkit-box-align: center;--%>
<%--        -ms-flex-align: center;--%>
<%--        -webkit-align-items: center;--%>
<%--        align-items: center;--%>
<%--        visibility: hidden;--%>
<%--    }--%>

<%--    .modal-con-otp > div {--%>
<%--        position: relative;--%>
<%--        width: 380px;--%>
<%--        font-size: 14px;--%>
<%--        line-height: 1.5;--%>
<%--        background-color: #fff;--%>
<%--        border-radius: 6px;--%>
<%--        -webkit-box-shadow: 0 0 15px #d2d2d2;--%>
<%--        -moz-box-shadow: 0 0 15px #d2d2d2;--%>
<%--        -o-box-shadow: 0 0 15px #d2d2d2;--%>
<%--        -ms-box-shadow: 0 0 15px #d2d2d2;--%>
<%--        box-shadow: 0 0 15px #d2d2d2;--%>
<%--        word-break: keep-all;--%>
<%--        visibility: hidden;--%>
<%--        -webkit-transform: scale(0);--%>
<%--        transform: scale(0);--%>
<%--        -webkit-transition: -webkit-transform 0.3s, visibility 0s 0.3s;--%>
<%--        transition: transform 0.3s, visibility 0s 0.3s;--%>
<%--    }--%>

<%--    .md-close {--%>
<%--        display: block;--%>
<%--        position: absolute;--%>
<%--        top: 0;--%>
<%--        right: 0;--%>
<%--        width: 44px;--%>
<%--        height: 53px;--%>
<%--        background: url(/anymobi/img/btn_popup_x.png) no-repeat center;--%>
<%--    }--%>

<%--    .md-title {--%>
<%--        padding: 15px 55px 15px 15px;--%>
<%--        font-size: 15px;--%>
<%--        color: #e90811;--%>
<%--        border-bottom: 1px solid #bdbdbd;--%>
<%--    }--%>

<%--    .md-txt {--%>
<%--        padding: 15px;--%>
<%--    }--%>

<%--    .modal-con-otp.open {--%>
<%--        visibility: visible;--%>
<%--    }--%>

<%--    .modal-con-otp.open > div {--%>
<%--        visibility: visible;--%>
<%--        -webkit-transform: scale(1);--%>
<%--        transform: scale(1);--%>
<%--        -webkit-transition: -webkit-transform 0.4s;--%>
<%--        transition: transform 0.4s;--%>
<%--    }--%>

<%--    #footer {--%>
<%--        width: 100%;--%>
<%--        border-top: 0 none;--%>
<%--    }--%>

<%--    #footer > div {--%>
<%--        width: 100%;--%>
<%--        padding: 12px 0 40px 0;--%>
<%--    }--%>

<%--    .enter-box {--%>
<%--        position: relative;--%>
<%--        margin-top: 0;--%>
<%--        margin-left: 0;--%>
<%--        background-image: url(/newPortal/icons/new23_logo.png);--%>
<%--        background-color: #fff;--%>
<%--        box-shadow: 0px 4px 16px 0px rgba(221, 221, 221, 0.60);--%>
<%--        animation: fadein 1.5s;--%>
<%--    }--%>

<%--    @keyframes fadein {--%>
<%--        from {--%>
<%--            opacity: 0;--%>
<%--        }--%>
<%--        to {--%>
<%--            opacity: 1;--%>
<%--        }--%>
<%--    }--%>

<%--    .enter-box::before {--%>
<%--        content: "";--%>
<%--        position: absolute;--%>
<%--        background: url(/newPortal/images/new23_login_top_title.svg) no-repeat center / contain;--%>
<%--        width: 260px;--%>
<%--        height: 37px;--%>
<%--        top: -46px;--%>
<%--        left: 50%;--%>
<%--        transform: translateX(-50%);--%>
<%--    }--%>

<%--    .enter-box input {--%>
<%--        background-color: #E8F0FE;--%>
<%--        border-radius: 4px;--%>
<%--        box-shadow: none;--%>
<%--        width: 250px !important;--%>
<%--    }--%>

<%--    #footer .policy-btn {--%>
<%--        top: -50px;--%>
<%--        left: 40px;--%>
<%--        right: auto;--%>
<%--        bottom: auto;--%>
<%--        margin-top: 0;--%>
<%--        border-radius: 4px;--%>
<%--        background: rgba(0, 0, 0, 0.45);--%>
<%--        border: 0 none;--%>
<%--    }--%>

<%--    .section {--%>
<%--        height: 800px;--%>
<%--        background-color: transparent;--%>
<%--        padding-top: 0;--%>
<%--    }--%>

<%--    #footer p {--%>
<%--        font-size: 14px;--%>
<%--        line-height: 22px;--%>
<%--        font-weight: 400;--%>
<%--    }--%>

<%--    #footer p:last-child {--%>
<%--        font-weight: 400;--%>
<%--    }--%>

<%--    /* 240703 추가 */--%>
<%--    @media (max-width: 1100px) {--%>
<%--        #wrap-login {--%>
<%--            width: 100%;--%>
<%--            margin: 0;--%>
<%--            height: 100vh;--%>
<%--            border-radius: 0;--%>
<%--        }--%>

<%--        .section {--%>
<%--            height: 65vh !important;--%>
<%--        }--%>

<%--        #footer {--%>
<%--            position: absolute;--%>
<%--            bottom: 0;--%>
<%--        }--%>

<%--        #footer > div {--%>
<%--            display: flex;--%>
<%--            flex-direction: column;--%>
<%--            padding-bottom: 12px !important;--%>
<%--        }--%>

<%--        #footer p:last-child {--%>
<%--            position: static;--%>
<%--        }--%>

<%--        #footer p {--%>
<%--            text-align: center;--%>
<%--            padding: 0 20px;--%>
<%--            font-size: 12px !important;--%>
<%--        }--%>

<%--        #footer .policy-btn {--%>
<%--            display: none !important;--%>
<%--            /* left: 50% !important;--%>
<%--            transform: translateX(-50%); */--%>
<%--        }--%>

<%--        .f-nbg {--%>
<%--            display: none;--%>
<%--        }--%>
<%--    }--%>

<%--    @media (max-width: 360px) {--%>
<%--        .enter-box {--%>
<%--            padding: 70px 0 0;--%>
<%--            width: min(90%, 330px)--%>
<%--        }--%>

<%--        #footer p {--%>
<%--            font-size: 11px !important;--%>
<%--        }--%>
<%--    }--%>

<%--    /* 문의 및 연락처 팝업 */--%>
<%--    .inquiry-popup {--%>
<%--        margin-left: 0px !important;--%>
<%--        left: 50% !important;--%>
<%--        transform: translateX(-50%) !important;--%>
<%--        box-shadow: 3px 3px 8px #ccc !important;--%>
<%--        width: min(95%, 330px) !important;--%>
<%--    }--%>

<%--    /*** 모바일웹 : 앱 출시 팝업 css ***/--%>
<%--    .modal-wrap {--%>
<%--        display: none;--%>
<%--        position: fixed;--%>
<%--        left: 50%;--%>
<%--        top: 50%;--%>
<%--        transform: translate(-50%, -50%);--%>
<%--        width: 100%;--%>
<%--        height: 100%;--%>
<%--        background-color: rgba(0, 0, 0, 0.5);--%>
<%--        z-index: 1000;--%>
<%--        overflow: hidden;--%>
<%--    }--%>

<%--    #modal9 ~ .navigation-tab-bar {--%>
<%--        z-index: 99;--%>
<%--    }--%>

<%--    #modal9 .modal-body {--%>
<%--        background-color: #fff;--%>
<%--        width: 100%;--%>
<%--        height: 100%;--%>
<%--        transform: translate(0, 0);--%>
<%--        position: absolute;--%>
<%--        left: 0;--%>
<%--        top: 0;--%>
<%--        /* animation: modal-flyout 0.2s; */--%>
<%--        border-radius: 0;--%>
<%--        bottom: 0;--%>
<%--        overflow-y: auto;--%>
<%--        -webkit-overflow-scrolling: touch;--%>
<%--        z-index: 999999;--%>
<%--    }--%>

<%--    #modal9 .modal-container {--%>
<%--        margin-top: 0;--%>
<%--    }--%>

<%--    #modal9 .visit-app {--%>
<%--        display: block;--%>
<%--        position: relative;--%>
<%--        width: 100%;--%>
<%--        height: 0;--%>
<%--        padding-top: calc(300 / 375 * 100%);--%>
<%--    }--%>

<%--    #modal9 .visit-app img {--%>
<%--        position: absolute;--%>
<%--        top: 0;--%>
<%--        left: 0;--%>
<%--        width: 100%;--%>
<%--        height: 100%;--%>
<%--        object-fit: contain;--%>
<%--        /* background: url(/newPortal/images/m/bg_app_launch.png) repeat-x; */--%>
<%--        background: linear-gradient(#ffffff, #DBD3FE);--%>
<%--        background-size: contain;--%>
<%--    }--%>

<%--    .desc-app {--%>
<%--        text-align: center;--%>
<%--        margin-top: 40px;--%>
<%--    }--%>

<%--    .desc-app strong {--%>
<%--        color: #7132D7;--%>
<%--        font-size: 22px;--%>
<%--        font-style: normal;--%>
<%--        font-weight: 700;--%>
<%--        line-height: 30px;--%>
<%--    }--%>

<%--    .desc-app p {--%>
<%--        margin-top: 28px;--%>
<%--    }--%>

<%--    .desc-app span {--%>
<%--        display: block;--%>
<%--        margin-top: 20px;--%>
<%--        color: #4C4C4E;--%>
<%--        font-size: 14px;--%>
<%--        line-height: 20px;--%>
<%--    }--%>

<%--    .btn-app button {--%>
<%--        position: absolute;--%>
<%--        bottom: 40px;--%>
<%--        font-size: 16px;--%>
<%--        line-height: 22px;--%>
<%--        color: #191919;--%>
<%--    }--%>

<%--    .btn-app .display-no {--%>
<%--        left: 20px;--%>
<%--    }--%>

<%--    .btn-app .display-close {--%>
<%--        right: 20px;--%>
<%--    }--%>

<%--    @media ( max-width: 320px) {--%>
<%--        .desc-app strong {--%>
<%--            font-size: 20px;--%>
<%--        }--%>

<%--        .desc-app p {--%>
<%--            font-size: 14px;--%>
<%--        }--%>
<%--    }--%>

<%--    @media ( orientation: landscape) and (min-width: 768px) {--%>
<%--        #modal9 .visit-app {--%>
<%--            padding-top: 45vh;--%>
<%--        }--%>
<%--    }--%>

<%--    /*** //모바일웹 : 앱 출시 팝업 css ***/--%>
<%--</style>--%>


<%--<% pageContext.setAttribute("replaceChar", "\n");--%>
<%--    response.setHeader("Cache-Control", "no-cache");--%>
<%--    response.setHeader("Pragma", "no-cache");--%>
<%--    response.setDateHeader("Expires", -1);--%>
<%--%>--%>

<%--<!-- 동적 리소스 정의 끝 -->--%>
<%--<body>--%>

<%--<!-- 임시 차단 팝업 S -->--%>
<%--<!-- <div class="pop-info">--%>
<%--    <p>--%>
<%--        <span><strong>[공지]</strong> 그룹교육포털(지니어스)<br><br></span>--%>
<%--        E부문 업무지식 평가에 따른 학습 차단 안내<br><br>--%>

<%--        KT그룹교육포털 (지니어스) 학습 차단 안내 드립니다.<br><br>--%>

<%--        1. 내용 : KT그룹교육포털 (지니어스) 온라인 학습 불가<br>--%>
<%--        2. 일정 : '24년 9월 9일(월)/10일(화) 15:30 ~ 17:30(2회)<br>--%>
<%--        3. 사유 : Enterprise부문 하반기 업무지식 평가 시행--%>
<%--      </p>--%>
<%--</div> -->--%>
<%--<!-- 임시 차단 팝업 E -->--%>

<%--<!-- 모바일웹 : 앱 출시 팝업 -->--%>
<%--<div class="modal-wrap full" id="modal9" style="display: none;">--%>
<%--    <div class="modal-body">--%>
<%--        <div class="modal-container">--%>
<%--            <div class="app-launch">--%>
<%--                <a href="https://mkate.kt.com" class="visit-app"> <img src="/vod/display/img_app_launch.png"--%>
<%--                                                                       alt="app launching"></a>--%>
<%--                <div class="desc-app">--%>
<%--                    <strong> 이제 지니어스를 모바일 앱으로<br>보다 편리하게 이용하세요!</strong>--%>
<%--                    <p>상단 이미지를 클릭하면 설치 사이트로 이동합니다.</p>--%>
<%--                </div>--%>
<%--                <div class="btn-app">--%>
<%--                    <button type="button" class="display-no close-modal" id="btnTodayClose">오늘 하루 보지 않기</button>--%>
<%--                    <button type="button" class="display-close close-modal">닫기</button>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </div>--%>
<%--</div>--%>
<%--<!-- //모바일웹 : 앱 출시 팝업 -->--%>

<%--<div id="wrap-login">--%>
<%--    <form id="loginForm" name="loginForm" method="post" autocomplete="off">--%>

<%--        <input type="hidden" id="loginUserId" name="loginUserId" value="<c:out value='${input.userid}'/>"/>--%>

<%--        <div class="section">--%>
<%--            <div class="enter-box">--%>
<%--                <p><input id="userid" name="userid" type="text"--%>
<%--                          onkeypress="if(event.keyCode==13) CtlExecutorLoginProc();"></p>--%>
<%--                <p class="def"><input id="pwd" name="pwd" type="password"--%>
<%--                                      onkeypress="if(event.keyCode==13) CtlExecutorLoginProc();"></p>--%>
<%--                <a class="login-btn" href="javascript:CtlExecutorLoginProc();" id="login-btn-id"><img--%>
<%--                        src="/anymobi/img/login/btn_login.png" alt="로그인"></a>--%>
<%--                <a class="login-btn" href="javascript:CtlExecutorLoginProc();" id="login-btn-id-grey"><img--%>
<%--                        src="/anymobi/img/login/btn_login_grey.png" alt="로그인"></a>--%>
<%--                <span id="loginMsg">* 로그인 아이디, 비밀번호는<br>KATE/KTalk 아이디(사번), 비밀번호와 동일합니다.</span>--%>
<%--                <a class="inquiry-btn"><img src="/anymobi/img/login/btn_popup_account_info.png" alt="문의 및 연락처"></a>--%>
<%--                <div class="inquiry-popup">--%>
<%--                    <p>문의 및 연락처<a class="popup-close"></a></p>--%>
<%--                    <ul>--%>
<%--                        <li>--%>
<%--                            <div>○ 로그인 문의</div>--%>
<%--                            <p>- 아이디를 모르는 경우, 조직의 인사담당자에게<br>문의 하시기 바랍니다.</p>--%>
<%--                            <p>- 비밀번호를 분실한 경우, <a href="https://idms.kt.com/im/" target="_blank">idms.kt.com</a>에서<br>비밀번호--%>
<%--                                변경 또는 초기화 하시기 바랍니다.</p>--%>
<%--                            <p>* 사외망 등의 사유로 <a href="https://idms.kt.com/im/" target="_blank">idms.kt.com</a> 접속이<br>불가할--%>
<%--                                경우 조직의 인사담당자 또는 아래<br>연락처로 문의 하시기 바랍니다.<br>※ 1588-3391 -> 1번(KOS무선)</p>--%>
<%--                        </li>--%>
<%--                        <li>○ 시스템 문의 : 1588-3391 (1 > 1)</a></li>--%>
<%--                        <li>○ 이러닝 문의 : 1577-0263</li>--%>
<%--                        <li>○ KT AIVLE스쿨 문의처 : 담당 에이블 매니저</li>--%>
<%--                    </ul>--%>
<%--                </div>            <!--// 추가_2015-11-24 : 끝 //-->--%>
<%--                <!--// step 1 ///-->--%>
<%--                <br/>--%>

<%--                <!--// step 2 SMS///-->--%>

<%--                <a id="reSendSmsAuth" class="login-btn" href="javascript:sendSmsAuth('R');" style="display: none;"><img--%>
<%--                        src="/anymobi/img/main/btn_popup_resend_code.png" alt="인증번호 재전송"/></a>--%>
<%--                <span id="authTime" style="display: none;">인증번호가 전송되었습니다. (남은시간: <span id="time">120</span>초)</span>--%>
<%--                <input id="smsSerial" class="login-btn" type="text" placeholder="인증번호 입력" maxlength="6"--%>
<%--                       autocomplete="off" style="display: none;"--%>
<%--                       onkeypress="if(event.keyCode==13) sendSmsAuthCheck();"/>--%>
<%--                <a id="smsAuthConnect" class="login-btn" href="javascript:sendSmsAuthCheck();"--%>
<%--                   style="display: none;"><img src="/anymobi/img/login/btn_popup_code_confirm_off.png" alt="접속"/></a>--%>

<%--            </div>--%>
<%--        </div>--%>
<%--        <input type="hidden" name="url" id="url" value="<c:out value='${input.url}'/>">--%>
<%--    </form>--%>
<%--    <!-- 하단영역 정의 시작 -->--%>
<%--    <%@ include file="/WEB-INF/jsp/web/common/INC_Footer.jsp" %>--%>
<%--    <!-- 하단영역 정의 끝0 -->--%>
<%--</div>--%>

<%--<form id="mainForm" name="mainForm" method="post">--%>
<%--</form>--%>
<%--<div class="modal-con-otp">--%>
<%--    <div>--%>
<%--        <a href="javascript:void(0);" class="md-close"></a>--%>
<%--        <p class="md-title">인증번호 발송</p>--%>
<%--        <div class="md-txt">기본 데이터 입니다.</div>--%>
<%--    </div>--%>
<%--</div>--%>
<%--</body>--%>
<%--</html>--%>
<%--<c:if test="${not empty popupInfo}">--%>
<%--    <!-- 팝업 -->--%>
<%--    <div class="l-popup notice dim" style="display: block;">--%>
<%--        <div class="popup">--%>
<%--            <button type="button" class="close-btn"></button>--%>
<%--            <div class="notice-content">--%>
<%--                <div>--%>
<%--                    <div>--%>
<%--                        <div class="new-pop-header">--%>
<%--                            <div>공지사항</div>--%>
<%--                        </div>--%>
<%--                        <div class="new-pop-info">--%>
<%--                            <p style="color:${popupInfo.textColor};">${fn:replace(popupInfo.overlayText, replaceChar, "<br/>")}</p>--%>
<%--                            <c:if test="${not empty popupInfo.slotImgSfileNm}">--%>
<%--                                <img src="<c:out value="${popupInfo.slotImgSfileNm}"/>">--%>
<%--                            </c:if>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </div>--%>
<%--    <!-- 팝업 -->--%>
<%--</c:if>--%>
<%--<!-- 스크립트 정의 시작 -->--%>
<%--<script type="text/javascript">--%>
<%--    var isDevice = "N";--%>
<%--    var preUrl = "/login";--%>
<%--    var failOptYn = 'N';--%>
<%--    var isBtnBlocking = false;--%>

<%--    var url = $("#url").val();--%>
<%--    if (isMobile()) {--%>
<%--        isDevice = "Y";--%>
<%--        preUrl = "/mobile/m/login";--%>
<%--        if (!url.includes('/exam/course/examMain')) {--%>
<%--            pevent();--%>
<%--        }--%>
<%--    }--%>

<%--    $(document).ready(function () {--%>
<%--        /*이 페이지로 온 순간 native 호출*/--%>
<%--        if (typeof window.ReactNativeWebView !== 'undefined') {--%>
<%--            window.ReactNativeWebView.postMessage(--%>
<%--                JSON.stringify({type: 'changeMobileLogin'})--%>
<%--            )--%>
<%--        }--%>

<%--        $("#wrap-login").css({"background-image": "url(/newPortal/images/new23_img_login_full.png)"});--%>

<%--        // 문의 및 연락처 팝업 (추가_2015-11-24)--%>
<%--        $(".inquiry-btn").click(function () {--%>
<%--            $(".inquiry-popup").show();--%>
<%--        });--%>
<%--        // 문의 및 연락처 팝업 닫기 (추가_2015-11-24)--%>
<%--        $(".popup-close").click(function () {--%>
<%--            $(".inquiry-popup").hide();--%>
<%--        });--%>

<%--        // 숫자만 입력가능--%>
<%--        $("#smsSerial").on("input", function () {--%>
<%--            let inputValue = $(this).val().replace(/[^0-9]/g, "");--%>
<%--            $(this).val(inputValue);--%>
<%--        });--%>

<%--        if ($.trim($("#loginUserId").val()).length > 0) {--%>
<%--            $('#userid').val($("#loginUserId").val());--%>
<%--        }--%>

<%--        //2018-08-20 로그인시 패스워드 문제로 ajax로 변경--%>
<%--        $("#login-btn-id").show();--%>
<%--        $("#login-btn-id-grey").hide();--%>

<%--        //모달 팝업 닫기--%>
<%--        $('.md-close').click(function () {--%>
<%--            $('.modal-con-otp').removeClass('open');--%>
<%--        });--%>

<%--        //인증번호 입력 시 팝업 닫기--%>
<%--        $(document).on('keydown', '#smsSerial', function () {--%>
<%--            $('.modal-con-otp').removeClass('open');--%>
<%--        });--%>

<%--        //외부영역 클릭 시 팝업 닫기--%>
<%--        $(document).mouseup(function (e) {--%>
<%--            if ($('.modal-con-otp').has(e.target).length === 0) {--%>
<%--                $('.modal-con-otp').removeClass('open');--%>
<%--            }--%>
<%--        });--%>

<%--        $('.close-modal').not('.alert').click(function () {--%>
<%--            $(this).closest('.modal-wrap').fadeOut();--%>
<%--            $('.new2023.l-gnb').removeClass('on');--%>
<%--        });--%>

<%--        $('#btnTodayClose').click(function () {--%>
<%--            setCookie("appNotiPop", "done", 1);--%>
<%--        });--%>

<%--    });--%>

<%--    //팝업 닫기--%>
<%--    $('.l-popup.notice .close-btn').on('click', function (e) {--%>
<%--        $('.l-popup.notice').css('display', 'none');--%>
<%--    });--%>

<%--    //외부영역 클릭 시 팝업 닫기--%>
<%--    $(document).mouseup(function (e) {--%>
<%--        if ($('.l-popup').has(e.target).length === 0) {--%>
<%--            $('.l-popup').css('display', 'none');--%>
<%--        }--%>
<%--    });--%>

<%--    function loginEndpoint(action) {--%>
<%--        return (isDevice == "Y" ? "/mobile/m/login/" : "/login/") + action;--%>
<%--    }--%>

<%--    CtlExecutor = {--%>
<%--        doLoginProc: function () {--%>
<%--            login();--%>
<%--        }--%>
<%--    };--%>

<%--    PopExecutor = {--%>
<%--        goFresh: function () {--%>
<%--            var url = "<c:url value="http://fresh.ncs-slp.com" />";--%>
<%--            window.open(url, "fresh", "");--%>
<%--        },--%>
<%--        goHistory: function () {--%>
<%--            var url = "<c:url value="/academy/history/popHistory" />";--%>
<%--            window.open(url, "history", "toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1100,height=600,left=0,top=0");--%>
<%--        },--%>

<%--        goFacilitiesGuide: function () {--%>
<%--            var url = "<c:url value="/academy/facilitiesGuide/popFacilitiesGuideWonju" />";--%>
<%--            window.open(url, "facilitiesLeased", "toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=no,width=1100,height=910,left=0,top=0");--%>
<%--        }--%>
<%--    };--%>

<%--    // 일반 로그인--%>
<%--    function login() {--%>
<%--        var p_pwd = $.trim($("#pwd").val());--%>
<%--        var userId = $.trim($("#userid").val());--%>

<%--        if (userId == "") {--%>
<%--            alert("아이디를 입력해 주세요");--%>
<%--            $("#userid").focus();--%>
<%--            return;--%>
<%--        } else if (p_pwd == "") {--%>
<%--            alert("비밀번호를 입력해 주세요.");--%>
<%--            $("#pwd").focus();--%>
<%--            return;--%>
<%--        }--%>

<%--        var sPublicKey = getLoginKey();--%>
<%--        if (sPublicKey == "") { // 키 생성 실패--%>
<%--            location.reload();--%>
<%--            return;--%>
<%--        }--%>

<%--        var crypt = new JSEncrypt();--%>
<%--        crypt.setPrivateKey(sPublicKey);--%>

<%--        p_pwd = crypt.encrypt(p_pwd);--%>
<%--        p_pwd = p_pwd.replace(/%/gi, '%25');--%>
<%--        p_pwd = p_pwd.replace(/\+/gi, '%2B');--%>
<%--        p_pwd = escape(encodeURIComponent(p_pwd));--%>

<%--        var requestData = {--%>
<%--            userid: userId,--%>
<%--            pwd: p_pwd,--%>
<%--            url: escape(encodeURIComponent($("#url").val())),--%>
<%--            isDevice: isDevice,--%>
<%--        };--%>

<%--        $.ajax({--%>
<%--            url: preUrl + 'loginProcAjax',--%>
<%--            async: false,--%>
<%--            type: 'POST',--%>
<%--            timeout: 3000,--%>
<%--            dataType: "json",--%>
<%--            data: JSON.stringify(requestData),--%>
<%--            error: function (e) {--%>
<%--                //console.log(e);--%>
<%--            },--%>
<%--            success: function (data) {--%>
<%--                if (data.result == 'N') {--%>
<%--                    if (data.optCnt == 0 || data.lgFailCnt == 5) {--%>
<%--                        var optSessionCheck = data.otpError || ""; // optError 체크--%>
<%--                        $("#loginMsg").hide();--%>
<%--                        $(".inquiry-btn").hide();--%>
<%--                        $("#login-btn-id").hide();--%>
<%--                        $("#login-btn-id-grey").show();--%>
<%--                        if (data.lgFailCnt == 5 && optSessionCheck != "error") {--%>
<%--                            failOptYn = 'Y';--%>
<%--                            alert('비밀번호 5회 연속 오류로 계정잠금되어 로그인이 불가합니다.\nSMS 번호인증으로 계정잠금을 해제해 주시기 바랍니다.\n비밀번호 분실에 따른 변경, 초기화는 `문의 및 연락처`를 참고하세요.');--%>
<%--                        } else if (data.lgFailCnt == 5 && optSessionCheck == "error") {--%>
<%--                            failOptYn = 'Y';--%>
<%--                            alert(data.message);--%>
<%--                            $("#login-btn-id-grey").hide();--%>
<%--                            $("#login-btn-id").show();--%>
<%--                            $("#loginMsg").show();--%>
<%--                            $(".inquiry-btn").show();--%>
<%--                        }--%>
<%--                        // otp 장애일경우 otp 인증 불가능--%>
<%--                        if (optSessionCheck != "error") {--%>
<%--                            sendSmsAuth('N');--%>
<%--                        }--%>
<%--                    } else {--%>
<%--                        alert(data.message);--%>
<%--                        $("#login-btn-id").show();--%>
<%--                        $("#login-btn-id-grey").hide();--%>
<%--                        $("#pwd").val('');--%>

<%--                        if (data.errorCode == 'O') {--%>
<%--                            $("#reSendSmsAuth").hide();--%>
<%--                            $("#authTime").hide();--%>
<%--                            $("#smsSerial").hide();--%>
<%--                            $("#login-btn-id-grey").hide();--%>
<%--                            $("#smsAuthConnect").hide();--%>
<%--                        }--%>
<%--                        if (data.isOk == -9) {--%>
<%--                            window.open('https://ktsso.kt.com/ssologin/guide/pwdTabOrg.html', 'popPwdExpire', 'width:520px, heigth:590');--%>
<%--                        } else if (data.isOk == -99) {--%>
<%--                            location.reload();--%>
<%--                        }--%>
<%--                    }--%>
<%--                } else if (data.result == 'Y') {--%>
<%--                    // confirm창 노출(중복된 경우)--%>
<%--                    /* if(data.sessionDupChk === true) {--%>
<%--                        if(confirm("현재 다른 기기에서 로그인되어 있습니다.\n기존 세션을 종료하고 이 기기에서 로그인하시겠습니까?")) {--%>
<%--                            $.ajax({--%>
<%--                                url : preUrl + 'forceLoginAjax',--%>
<%--                                async : false,--%>
<%--                                type : 'POST',--%>
<%--                                timeout : 3000,--%>
<%--                                dataType : "json",--%>
<%--                                data : JSON.stringify(requestData),--%>
<%--                                error : function(e) {--%>
<%--                                    //console.log(e);--%>
<%--                                },--%>
<%--                                success: function(res) {--%>
<%--                                    if(res.result == 'Y') {--%>
<%--                                        $("#mainForm").attr({ action : data.resultUrl }).submit();--%>
<%--                                    } else {--%>
<%--                                        alert("세션 종료에 실패했습니다.");--%>
<%--                                    }--%>
<%--                                }--%>
<%--                            });--%>
<%--                        } else {--%>
<%--                            // 아니오--%>
<%--                            alert("로그인이 취소되었습니다.");--%>
<%--                            return;--%>
<%--                        }--%>
<%--                        $("#mainForm").attr({--%>
<%--                            action : data.resultUrl--%>
<%--                        }).submit();--%>
<%--                    } else {--%>
<%--                        // 정상로그인(미리 넣어둠!)--%>
<%--                        $("#mainForm").attr({--%>
<%--                            action : data.resultUrl--%>
<%--                        }).submit();--%>
<%--                    } */--%>
<%--                    $("#mainForm").attr({--%>
<%--                        action: data.resultUrl--%>
<%--                    }).submit();--%>
<%--                } else {--%>
<%--                    alert('로그인시 오류가 발생하였습니다. 관리자에게 문의바랍니다.');--%>
<%--                    $("#login-btn-id").show();--%>
<%--                    $("#login-btn-id-grey").hide();--%>
<%--                }--%>
<%--            }--%>
<%--        });--%>
<%--    }--%>

<%--    function getLoginKey() {--%>
<%--        var sEncKey = "";--%>

<%--        $.ajax({--%>
<%--            url: preUrl + 'getEncKeyAjax',--%>
<%--            async: false,--%>
<%--            type: 'POST',--%>
<%--            timeout: 3000,--%>
<%--            dataType: "json",--%>
<%--            //data : JSON.stringify(requestData),--%>
<%--            error: function (e) {--%>
<%--            },--%>
<%--            success: function (data) {--%>
<%--                if (data.result == "Y") {--%>
<%--                    sEncKey = data.encKey;--%>
<%--                } else {--%>
<%--                    alert(data.message);--%>
<%--                }--%>
<%--            }--%>
<%--        });--%>

<%--        return sEncKey;--%>
<%--    }--%>

<%--    function goPrivacyPolicy() {--%>
<%--        var url = "<c:url value="/resources/html/policy/privacyPolicy.html" />";--%>
<%--        window.open(url, "history", "toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=no,width=1100,height=600,left=0,top=0");--%>
<%--    }--%>

<%--    var initTime = 180;--%>
<%--    var timeCheck = initTime;--%>
<%--    var timeInterval = null;--%>
<%--    var isAdmin = '${sessionScope.sessionMemberInfo.isadmin}';--%>

<%--    function sendSmsAuth() {--%>

<%--        $.ajax({--%>
<%--            url: preUrl + 'sendSmsOptAuthAjax',--%>
<%--            async: false,--%>
<%--            type: 'POST',--%>
<%--            timeout: 3000,--%>
<%--            dataType: "json",--%>
<%--            //data : JSON.stringify(requestData),--%>
<%--            error: function (e) {--%>
<%--            },--%>
<%--            success: function (data) {--%>
<%--                if (data.resultCode == "1") {--%>
<%--                    $("#authTime").show();--%>
<%--                    $("#smsSerial").show();--%>

<%--                    $("#reSendSmsAuth").show();--%>
<%--                    $("#smsAuthConnect").show();--%>

<%--                    $("#time").html(timeCheck);--%>
<%--                    clearInterval(timeInterval);--%>
<%--                    timeCheck = initTime;--%>
<%--                    $("#time").html(timeCheck);--%>
<%--                    if ('<spring:eval expression="@config[\'server.type\']"/>' === 'local' ||--%>
<%--                        '<spring:eval expression="@config[\'server.type\']"/>' === 'test') {--%>
<%--                        $("#smsSerial").val(data.serial);--%>
<%--                    } else {--%>
<%--                        $("#smsSerial").val("");--%>
<%--                    }--%>
<%--                    $("#smsSerial").focus();--%>
<%--                    timeInterval = setInterval(function () {--%>
<%--                        if (timeCheck > 0) {--%>
<%--                            // 30초 경과 후 팝업 닫히게--%>
<%--                            if (timeCheck == 150) {--%>
<%--                                $('.modal-con-otp').removeClass('open');--%>
<%--                            }--%>
<%--                            timeCheck--;--%>
<%--                            $("#time").html(timeCheck);--%>
<%--                        } else {--%>
<%--                            clearInterval(timeInterval);--%>
<%--                            $("#authTime").hide();--%>
<%--                            timeCheck = initTime;--%>
<%--                            alert("SMS 인증번호 입력 대기시간이 3분을 초과했습니다. \n인증번호를 다시 요청해 주시기 바랍니다.");--%>
<%--                        }--%>
<%--                    }, 1000);--%>
<%--                    var success_msg = (data.message).replace('\n', '<br>');--%>
<%--                    $('.md-txt').html(success_msg);--%>
<%--                    $('.modal-con-otp').addClass('open');--%>
<%--                    //alert(data.message);--%>
<%--                } else if (data.resultCode == "0") {--%>
<%--                    $("#login-btn-id").show();--%>
<%--                    $("#login-btn-id-grey").hide();--%>
<%--                    $("#loginMsg").show();--%>
<%--                    $(".inquiry-btn").show();--%>

<%--                    if (data.errorCode == 'O') {--%>
<%--                        $("#reSendSmsAuth").hide();--%>
<%--                        $("#authTime").hide();--%>
<%--                        $("#smsSerial").hide();--%>
<%--                        $("#login-btn-id-grey").hide();--%>
<%--                        $("#smsAuthConnect").hide();--%>
<%--                    }--%>

<%--                    alert(data.message);--%>
<%--                } else {--%>
<%--                    alert(data.message);--%>
<%--                }--%>
<%--                //alert(data.message);--%>
<%--            }--%>
<%--        });--%>
<%--    }--%>


<%--    function sendSmsAuthCheck() {--%>
<%--        if (isBtnBlocking) return;--%>

<%--        if ($("#smsSerial").val() == null || $("#smsSerial").val() == '') {--%>
<%--            alert("문자로 발송된 인증번호를 입력하세요.");--%>
<%--            return;--%>
<%--        }--%>

<%--        isBtnBlocking = true;--%>

<%--        var flag = '0';--%>
<%--        if (failOptYn == 'Y')--%>
<%--            flag = '2';--%>
<%--        else--%>
<%--            flag = '1';--%>

<%--        var requestData = {--%>
<%--            serial: $("#smsSerial").val(),--%>
<%--            flag: flag,--%>
<%--            isDevice: isDevice--%>
<%--        };--%>

<%--        $.ajax({--%>
<%--            url: preUrl + 'sendSmsAuthCheckAjax',--%>
<%--            async: false,--%>
<%--            type: 'POST',--%>
<%--            timeout: 3000,--%>
<%--            dataType: "json",--%>
<%--            data: JSON.stringify(requestData),--%>
<%--            error: function (e) {--%>
<%--                //console.log(e);--%>
<%--                isBtnBlocking = false;--%>
<%--            },--%>
<%--            success: function (data) {--%>
<%--                if (data.message != null && data.message != '')--%>
<%--                    alert(data.message);--%>

<%--                if (data.resultCode == "1") {--%>
<%--                    $("#mainForm").attr({--%>
<%--                        action: data.resultUrl--%>
<%--                    }).submit();--%>
<%--                } else if (data.resultCode == "2") {--%>
<%--                    $("#loginForm").attr({--%>
<%--                        action: '/login'--%>
<%--                    }).submit();--%>
<%--                } else {--%>
<%--                    isBtnBlocking = false;--%>
<%--                }--%>
<%--            }--%>
<%--        });--%>
<%--    }--%>

<%--    function isMobile() {--%>
<%--        // 모바일&태블릿검사--%>
<%--        if ((navigator.userAgent.toLowerCase().indexOf('ipad')) > -1 ||--%>
<%--            (navigator.userAgent.toLowerCase().indexOf('iphone')) > -1 ||--%>
<%--            (navigator.userAgent.toLowerCase().indexOf('ipad') > -1 && navigator.maxTouchPoints > 1) ||--%>
<%--            (navigator.userAgent.toLowerCase().indexOf('iphone') > -1 && navigator.maxTouchPoints > 1) ||--%>
<%--            (navigator.userAgent.toLowerCase().indexOf('mac os') > -1 && navigator.maxTouchPoints > 1) ||--%>
<%--            (navigator.userAgent.toLowerCase().indexOf('android') > -1 && navigator.userAgent.toLowerCase().indexOf('mobile') == -1)) {--%>
<%--            return true;--%>
<%--        }--%>

<%--        var galaxyTabModel = new Array('shw-', 'tab', 'tablet', 'pad');--%>
<%--        for (var i = 0; i < galaxyTabModel.length; i++) {--%>
<%--            if (navigator.userAgent.toLowerCase().indexOf(galaxyTabModel[i]) > -1) {--%>
<%--                return true;--%>
<%--            }--%>
<%--        }--%>

<%--        // 모바일검사--%>
<%--        var mobileModel = new Array('iphone', 'ipod', 'ipad', 'android', 'blackberry', 'windows ce', 'nokia', 'webos', 'opera mini', 'sonyericsson', 'opera mobi', 'iemobile')--%>
<%--        for (var j = 0; j < mobileModel.length; j++) {--%>
<%--            if (navigator.userAgent.toLowerCase().indexOf(mobileModel[j]) > -1) {--%>
<%--                return true;--%>
<%--            }--%>
<%--        }--%>

<%--        return false;--%>
<%--    }--%>

<%--    function setCookie(name, value, expiredays) {--%>
<%--        var todayDate = new Date();--%>
<%--        todayDate = new Date(parseInt(todayDate.getTime() / 86400000) * 86400000 + 54000000);--%>
<%--        if (todayDate > new Date()) {--%>
<%--            expiredays = expiredays - 1;--%>
<%--        }--%>
<%--        todayDate.setDate(todayDate.getDate() + expiredays);--%>
<%--        document.cookie = name + "=" + escape(value) + "; path=/; expires=" + todayDate.toGMTString() + ";";--%>
<%--    }--%>

<%--    function pevent() {--%>
<%--        function getCookie(name) {--%>
<%--            var nameOfCookie = name + "=";--%>
<%--            var x = 0;--%>
<%--            while (x <= document.cookie.length) {--%>
<%--                var y = x + nameOfCookie.length;--%>
<%--                if (document.cookie.substring(x, y) == nameOfCookie) {--%>
<%--                    var endOfCookie = document.cookie.indexOf(";", y);--%>
<%--                    if (endOfCookie == -1) {--%>
<%--                        endOfCookie = document.cookie.length;--%>
<%--                    }--%>
<%--                    return unescape(document.cookie.substring(y, endOfCookie));--%>
<%--                }--%>
<%--                x = document.cookie.indexOf(" ", x) + 1;--%>
<%--                if (x == 0) break;--%>
<%--            }--%>
<%--            return "";--%>
<%--        }--%>

<%--        if (getCookie("appNotiPop") != "done") {--%>
<%--            $("#modal9").css('display', 'block');--%>
<%--        }--%>
<%--    }--%>

<%--    function deleteCookie(cookieName) {--%>
<%--        var expireDate = new Date();--%>
<%--        expireDate.setDate(expireDate.getDate() - 1);--%>
<%--        document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString() + "; path=/";--%>
<%--    }--%>
<%--</script>--%>
<%--<!-- 스트립트 정의 끝 -->--%>