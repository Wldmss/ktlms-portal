<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<script src="/resources/legacy/newPortal/js/mobileShare.js"></script>
<script src="/resources/legacy/anymobi/mobile/js/gnb.js"></script>
<script src="/resources/legacy/newPortal/js/new23_m.js"></script>
<style>
    .icon.btn {
        position: relative;
        float: left;
        top: 27px;
        left: 8px;
    }
</style>
<div class="l-gnb main-type">
    <div class="gnb">
        <div class="gnb-top">
            <div class="left gnb-menu">
                <button type="button" class="btn m-gnb-btn"><span class="hide">메뉴</span></button>
            </div>
            <div class="center">
                <h1 class="gnb-logo">
                    <a href="#" onclick="javascript:CtlExecutor3.goMain();"><span class="hide">kt 그룹교육 포털 로고</span></a>
                </h1>
            </div>
            <div class="right">
                <c:if test="${comp ne '8888'}">
                    <!-- 알람 아이콘 new 클래스 삭제 시 뱃지 카운트영역 비활성화 -->
                    <button type="button" class="btn alarm-btn" onclick="javascript:CtlExecutor3.goAlarm();"
                            id="alarmFrame"><span class="num"></span></button>
                    <button type="button" class="btn search-btn"><span class="hide">통합검색</span></button>
                </c:if>
            </div>
        </div>
        <div class="gnb-container gnb-inner">
            <div class="gnb-content">
                <div class="l-user">
                    <div class="user">
                        <h4><c:out value="${maskingNm}"/>님</h4>
                    </div>
                    <!-- <div class="log">
                        <button type="button" onclick="javascript:CtlExecutor3.goLogout();"><span>로그아웃</span></button>
                    </div> -->
                </div>
                <div class="header_gnb_list">
                    <button id="btn_qrcodeAttend" type="button" class="qr-check-btn">
                        <span>QR 출석</span>
                    </button>
                    <ul class="gnb_depth gnb_1depth"></ul>
                    <ul class="gnb_depth gnb_2depth"></ul>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/tiles/mobile/KT_SearchPop.jsp" %>
<!-- 통합검색 팝업 -->

<input type="hidden" value="<c:out value='${comp}'/>" id="comp"/>
<script nonce="${cspNonce}">
    var preUrl = getPreURL();
    var CtlExecutor3 = {
        goMain: function () {
            location.href = preUrl + '<c:url value="/main/portalMain" />';
        },
        goApplyList: function () {
            location.href = preUrl + "/myclass/course/myCourse/myCourseList?eduStep=BEF";
        },
        goIngList: function () {
            location.href = preUrl + "/myclass/course/myCourse/myCourseList?eduStep=MID";
        },
        goFinishList: function () {
            location.href = preUrl + "/myclass/course/myCourse/myCourseList?eduStep=AFT";
        },
        goAlarm: function () {
            location.href = preUrl + '<c:url value="/alarm/alarmList" />';
        },
        goLogout: function () {
            if (confirm("로그아웃 하시겠습니까?")) {
                if (typeof window.ReactNativeWebView !== 'undefined') {
                    window.ReactNativeWebView.postMessage(
                        JSON.stringify({type: 'logout'})
                    );
                } else {
                    location.href = preUrl + "/logout";
                }
            }
        }
    };

    var url = location.href.split(location.host)[1];
    var temp = url.split('?');
    if (temp.length > 1) {
        url = temp[0];
    }
    if (url.indexOf("/mobile/t") != -1) {
        var tempUrl = url.replace('/mobile/t', '/mobile/m');
        url = tempUrl;
    }

    $(function () {

        $("body").append("<div id='dimbox'></div>");
        $('.condition-btn').popup({
            target: $('.l-popup.condition')
        })
        $('.learning-btn').click(function () {
            $('#dimbox').show();
            $(".l-popup.learning").css('display', 'block');
        });
        $(".l-popup.learning .close-btn").click(function () {
            $('#dimbox').hide();
            $(".l-popup.learning").css('display', 'none');
        });

        var requestData = {
            mwFlag: 'M'
        };

        $.ajax({
            url: preUrl + '/a/layout/gnbListAjax',
            async: false,
            //type : 'GET',
            timeout: 3000,
            dataType: "json",
            data: requestData,
            error: function (xhr, status, error) {
                if (xhr.status == 999) {
                    setTimeout(function () {
                        alert('세션이 만료되었습니다. \n로그인 화면으로 이동합니다.');
                        location.href = '/login';
                    }, 100);
                }
            },
            success: function (data) {
                /** mobile menu **/
                var tutorYn = data.tutorYn;
                var appYn = data.appCheck;
                var depth2PmenuId = "";

                for (var i = 0; i < data.menuList.length; i++) {
                    var menu = data.menuList[i];
                    var menu1chd = "";
                    var menu2chd = "";
                    var menu3chd = "";
                    var dropDown = "";
                    var comp = $("#comp").val();

                    if (menu.menuLevel == '1') {
                        if (menu.menuId == 'M.CHANGEPW') { // P/W변경
                            menu1chd += '<li>';
                            menu1chd += '	<a href="javascript:void(0);" onclick="javascript:linkUrl(\'' + menu.linkUrl + '\' , \'' + menu.menuId + '\', \'' + menu.menuNm + '\')">' + menu.menuNm + '</a>';
                            menu1chd += '</li>';

                            dropDown += '<li>';
                            dropDown += '	<div class="dropdown">';
                            dropDown += '		<ul>';
                            dropDown += '		</ul>';
                            dropDown += '	</div>';
                            dropDown += '</li>';

                            $(".header_gnb_list .gnb_depth.gnb_2depth").append(dropDown);
                        } else {
                            if (menu.menuId == 'M.SUPPORT.HELP') { // 헬프센터
                                menu1chd += '<li class="active">';
                            } else {
                                menu1chd += '<li>';
                            }
                            menu1chd += '	<a href="javascript:void(0);">' + menu.menuNm + '</a>';
                            menu1chd += '</li>';
                        }

                        $(".header_gnb_list .gnb_depth.gnb_1depth").append(menu1chd);
                    } else if (menu.menuLevel == '2') {

                        if (depth2PmenuId != menu.pMenuId) {
                            if (menu.pMenuId == 'M.SUPPORT.HELP') { // 헬프센터
                                dropDown += '<li class="active" id="' + menu.pMenuId + '">';
                            } else {
                                dropDown += '<li id="' + menu.pMenuId + '">';
                            }

                            dropDown += '	<div class="dropdown">';
                            dropDown += '		<ul>';
                            dropDown += '		</ul>';
                            dropDown += '	</div>';
                            dropDown += '</li>';

                            $(".header_gnb_list .gnb_depth.gnb_2depth").append(dropDown);
                        }
                        depth2PmenuId = menu.pMenuId;

                        if (menu.menuId == "M.LEARNING.ACADEMY") {
                            // 어학아카데미
                            menu2chd += '<li class="depth_title depth_1_title depth_only">';
                            menu2chd += '	<a href="javascript:void(0);" onclick="javascript:ssoLoginToHunet()">' + menu.menuNm + '</a>';
                            menu2chd += '</li>';
                        } else if (menu.menuId == "M.MY_PAGE.TUTOR_CLASSROOM") {
                            // 강의관리
                            if (tutorYn == "Y" && comp == "1001") {
                                menu2chd += '<li class="depth_title depth_1_title depth_only">';
                                menu2chd += '	<a href="javascript:void(0);" onclick="javascript:linkUrl(\'' + menu.linkUrl + '\' , \'' + menu.menuId + '\', \'' + menu.menuNm + '\')">' + menu.menuNm + '</a>';
                                menu2chd += '</li>';
                            }
                        } else if (menu.menuId == "M.SETTING.SETLOGIN" || menu.menuId == "M.SETTING.PUSH") {
                            // 간편로그인 설정, Push 알림 설정
                            if (appYn == "Y") {
                                menu2chd += '<li class="depth_title depth_1_title depth_only">';
                                menu2chd += '	<a href="javascript:void(0);" onclick="javascript:linkUrl(\'' + menu.linkUrl + '\' , \'' + menu.menuId + '\', \'' + menu.menuNm + '\')">' + menu.menuNm + '</a>';
                                menu2chd += '</li>';
                            }
                        } else {
                            if (menu.leafYn == 'N') { // 3depth exist
                                menu2chd += '<li class="depth_title depth_1_title" id="' + menu.menuId + '">';
                                menu2chd += '	<a href="javascript:void(0);">' + menu.menuNm + '</a>';
                                menu2chd += '	<ul class="sub_menu"></ul>';
                                menu2chd += '</li>';
                            } else { // 3depth not exist
                                menu2chd += '<li class="depth_title depth_1_title depth_only">'
                                menu2chd += '	<a href="javascript:void(0);" onclick="javascript:linkUrl(\'' + menu.linkUrl + '\' , \'' + menu.menuId + '\', \'' + menu.menuNm + '\')">' + menu.menuNm + '</a>';
                                menu2chd += '</li>';
                            }
                        }

                        $("#" + menu.pMenuId.split('.').join('\\.') + " .dropdown > ul").append(menu2chd);
                    } else if (menu.menuLevel == '3') {
                        if (menu.menuId == "M.SUPPORT.POLICY.HRDER") { // 교육/1등워크숍리뷰
                            if (data.hrdCheck == "Y") {
                                menu3chd += '<li class="depth_title depth_2_title depth_only">';
                                menu3chd += '	<a href="javascript:void(0);" onclick="javascript:linkUrl(\'' + menu.linkUrl + '\' , \'' + menu.menuId + '\', \'' + menu.menuNm + '\')">' + menu.menuNm + '</a>';
                                menu3chd += '</li>';
                            }
                        } else {
                            menu3chd += '<li class="depth_title depth_2_title depth_only">';
                            menu3chd += '	<a href="javascript:void(0);" onclick="javascript:linkUrl(\'' + menu.linkUrl + '\' , \'' + menu.menuId + '\', \'' + menu.menuNm + '\')">' + menu.menuNm + '</a>';
                            menu3chd += '</li>';
                        }

                        $("#" + menu.pMenuId.split('.').join('\\.') + " .sub_menu").append(menu3chd);
                    }
                }
            }
        });

        $('.header_gnb_list .gnb_1depth a').click(function () {
            $('.header_gnb_list .gnb_1depth li').removeClass('active');
            $(this).parent('li').toggleClass('active');
            var idx = $(this).parent('li').index();
            activeReset('.header_gnb_list .gnb_2depth');
            $('.header_gnb_list .gnb_2depth .depth_1_title .sub_menu').css('display', 'none');
            $('.header_gnb_list .gnb_2depth > li:nth-child(' + (idx + 1) + ')').addClass('active');
        });

        // active 초기화
        function activeReset(parentName) {
            var findActive = $(parentName + ' *').hasClass('active');
            if (findActive) {
                $(parentName + ' *').removeClass('active');
            }
        }

        $(".dropdown .depth_title > a").off('click').on('click', function () {
            $(this).toggleClass('active');
            var subMenu = $(this).siblings('.sub_menu');
            subMenu.slideToggle();
        });

        $("#btn_qrcodeAttend").click(function () {
            window.ReactNativeWebView.postMessage(
                JSON.stringify(
                    {
                        type: 'openCamera'
                    }
                )
            );
        });

        $.ajax({
            url: preUrl + '/a/alarm/alarmCntAjax',
            async: true,
            type: 'POST',
            timeout: 3000,
            dataType: "json",
            data: JSON.stringify({url: url}),
            error: function (e) {
// 			console.log(e);
            },
            success: function (data) {
                if (data.alarmCnt != null) {
                    if (data.alarmCnt.unreadCnt == 0) {

                    } else if (data.alarmCnt.unreadCnt >= 1 && data.alarmCnt.unreadCnt <= 99) {
                        $("#alarmFrame").addClass('new');
                        // 알림에 숫자 입력
                        $("#alarmFrame .num").text(data.alarmCnt.unreadCnt);
                    } else {// 99가 넘을경우 99로만 표기
                        $("#alarmFrame").addClass('new');
                        // 알림에 숫자 입력
                        $("#alarmFrame .num").text('99');
                    }
                }
            }
        });

    });

    function linkUrl(url, menuId, menuNm) {
        $.ajax({
            url: preUrl + '/a/insertMenuLogAjax',
            async: true,
            type: 'POST',
            timeout: 3000,
            dataType: "json",
            data: JSON.stringify({
                menuId: menuId,
                menuNm: escape(encodeURIComponent(menuNm))
            }),
            error: function (e) {
                //console.log(e);
            },
            success: function (data) {
                if (url == "popupKyoboAJAX" || url == "oddEyeFactoryAjax" || url == '/mobile/m/a/main/popupKyobo.do' || url == '/mobile/m/a/main/oddEyeFactory.do') {
                    goOutLink(url);
                } else {
                    location.href = url;
                }


            }
        });
    }

    //휴넷
    function ssoLoginToHunet() {
        var hunetUserId = "kt_" + "<c:out value='${sessionScope.sessionMemberInfo.userid} '/>";
        var locat = "hunetmlc1621://?cSeq=1621&userId=" + hunetUserId;
        var appDownUrl = "http://mlc.hunet.co.kr/areas/1621/down.html";
        if (typeof window.ReactNativeWebView !== 'undefined') {
            // 네이티브로 앱웹 분기처리
            window.ReactNativeWebView.postMessage(
                JSON.stringify({type: 'openApp', data: {app_link: locat, web_link: appDownUrl}})
            );
        } else {
            var timer;
            var schInterval;

            // 인터벌, 타이머 삭제
            function clearTimer() {
                clearInterval(schInterval);
                clearTimeout(timer);
            }

            // 인터벌 마다 동작할 기능
            function intervalSch() {
                // 매 인터벌 마다 웹뷰가 활성화인지 체크
                if (document.webkitHidden || document.hidden) { // 웹뷰 비활성화
                    clearTimer(); // 앱이 설치되어 있을 경우 타이머 제거
                }
            }

            // 앱 실행
            location.href = locat;

            // 앱이 설치 되어 있는지 체크
            schInterval = setInterval(intervalSch, 500);

            // 앱 설치 페이지로 이동
            timer = setTimeout(function () {
                location.href = appDownUrl;
            }, 2000);
        }
    }

    // 교보문고,vr 외부브라우저 분기처리
    function goOutLink(url) {
        if (url == 'popupKyoboAJAX' || url.includes('/a/main/popupKyobo')) {
            $.ajax({
                url: '/mobile/m/main/popupKyoboAjax',
                async: true,
                type: 'POST',
                timeout: 3000,
                dataType: "json",
                data: JSON.stringify({url: url}),
                error: function (e) {
//	 			console.log(e);
                },
                success: function (data) {
                    if (typeof window.ReactNativeWebView !== 'undefined') {
                        window.ReactNativeWebView.postMessage(
                            JSON.stringify({type: 'openUrl', url: data.url})
                        );
                    } else {
                        window.open(data.url, '_blank');
                    }

                }
            });
        } else if (url == 'oddEyeFactoryAjax' || url == '/mobile/m/a/main/oddEyeFactory' || url == '/mobile/m/a/main/oddEyeFactory.do') {
            $.ajax({
                url: '/mobile/m/a/main/oddEyeFactoryAjax',
                async: true,
                type: 'POST',
                timeout: 3000,
                dataType: "json",
                data: JSON.stringify({url: url}),
                error: function (e) {
//	 			console.log(e);
                },
                success: function (data) {
                    if (typeof window.ReactNativeWebView !== 'undefined') {
                        window.ReactNativeWebView.postMessage(
                            JSON.stringify({type: 'openUrl', url: data.url})
                        );
                    } else {
                        location.href = preUrl + "/a/main/oddEyeFactory";
                    }

                }
            });
        }


    }

    //특수문자 입력불가능
    function removeSpecialChars(input) {
        input.value = input.value.replace(/[^a-zA-Z0-9ㄱ-ㅎ가-힝\s]/g, '');
    }

    function goOutAice() {
        if (typeof window.ReactNativeWebView !== 'undefined') {
            window.ReactNativeWebView.postMessage(
                JSON.stringify({type: 'openUrl', url: "https://aice.study"})
            );
        } else {
            location.href = "https://aice.study";
        }
    }

</script>