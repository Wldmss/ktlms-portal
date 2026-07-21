var uAgent = navigator.userAgent.toLowerCase();
var mobChk = false

var mobilePhones = new Array('iphone', 'ipod', 'ipad', 'android', 'blackberry', 'windows ce', 'nokia', 'webos', 'opera mini', 'sonyericsson', 'opera mobi', 'iemobile');
for (var i = 0; i < mobilePhones.length; i++) {
    if (uAgent.indexOf(mobilePhones[i]) != -1) {
        mobChk = true;
    }
}
;

var viewportWidth = $(window).width();
var viewChk = false
if (viewportWidth < 1200) {
    viewChk = true
}

var wH = $(window).height();

// 2020.08.20 
$(document).ready(function () {
    var tH = $('.l-gnb').height();
    var fH = $('.l-footer').height();
    var conH = wH - tH - fH;
    var gnbH = wH + tH;
    var subV = $('.l-gnb').next().hasClass('sub-visual');
    var subVH = $('.sub-visual').height();
    if (subV) {
        conH = wH - tH - fH - subVH;
    } else {
        conH = wH - tH - fH;
    }

    $('.l-content').css('min-height', conH);
    $('.gnb-container').css('height', wH);
})
$(window).resize(function () {
    var tH = $('.l-gnb').height();
    var fH = $('.l-footer').height();
    var conH = wH - tH - fH;
    var gnbH = wH + tH;
    $('.l-content').css('min-height', conH);
    $('.gnb-container').css('height', gnbH);


    viewportWidth = $(window).width();
    if (viewportWidth < 1200) {
        viewChk = true;
    } else {
        viewChk = false;
    }
})
$.fn.popup = function (opt) {
    var setting = $.extend({
        $el: this,
        close: '.close-btn',
        target: '',
        type: '',
        openpopup: function () {
            $(setting.$el).on({
                click: function () {
                    $('.l-popup').hide();
                    setting.target
                        .addClass(setting.type)
                        .show();
                    // console.log($(setting.target));
                    if ($(setting.target).hasClass('dim')) {
                        $('body').addClass('no-scroll');
                    }
                }
            })
            setting.closepopup();
        },
        closepopup: function () {
            setting.target.find(setting.close).on({
                click: function () {
                    setting.target.hide().removeClass(setting.type)
                    if ($(setting.target).hasClass('dim')) {
                        $('body').removeClass('no-scroll');
                    }
                }
            })
        }
    }, opt)
    setting.openpopup()
    return this;
}

function studyTime(studyClass, target) {
    if ($(studyClass).text() !== '') {
        var studyTime = Number($(studyClass).text());
        var studyTimePercent = 672 / 100 * studyTime;
        $(target).css('stroke-dasharray', Number(studyTimePercent.toFixed(1)) + ', 672')
    }
}


function tab(tabIdx, contentIdx) {
    $("#" + tabIdx).parent().find('li').removeClass('on')
    $('#' + tabIdx).addClass('on');
    // content

    var tabContentClass = "." + $("#" + contentIdx)[0].classList[0];
    $(tabContentClass).hide();
    $("#" + contentIdx).show();

}


//최근 검색 리스트 ajax		
function swordList() {
    /*var preUrl = isMobile();*/
    $.ajax({
        url: '/a/search/swordListAjax.do',
        async: true,
        type: 'POST',
        timeout: 3000,
        dataType: "json",
        //data : JSON.stringify(requestData),
        error: function (e) {
            //console.log(e);
        },
        success: function (data) {
            if (data.swordList.length > 0) {
                var swordList = "";
                for (var i = 0; i < data.swordList.length; i++) {
                    var sword = data.swordList[i];
                    swordList += '<li class="recent-search-list">';
                    swordList += '<button type="button" class="btn search-list">';
                    swordList += '<span>' + sword.swordNm + '</span>';
                    swordList += '</button>';
                    swordList += '<button type="button" class="btn close">' + sword.swordNm + '</button>';
                    swordList += '</li>';
                }
                $(".recent-search-ul").eq(0).html(swordList);
            } else {
                $(".recent-search-ul").eq(0).html('');
            }
            if (data.htagList.length > 0) {
                var htagList = "";
                for (var i = 0; i < data.htagList.length; i++) {
                    var htag = data.htagList[i];
                    htagList += ' <li class="popular-search-list">';
                    htagList += ' <button type="button" class="btn-htag"><span>#' + htag.htagNm + '</span></button>';
                    htagList += ' </li>';
                }
                $(".search-hashtag-ul").eq(0).html(htagList);

            }
        }
    });
};

function myEduCon() {
    $.ajax({
        url: '/a/main/myEduCon.do',
        async: false,
        type: 'POST',
        timeout: 3000,
        dataType: "json",
        //data : JSON.stringify(requestData),
        error: function (e) {
            //console.log(e);
        },
        success: function (data) {
            if (null != data.eduTimeInfo) {
                $("#totalTime").text(data.eduTimeInfo.certiHours + "시간");
                $("#recomStudytime").text(data.eduTimeInfo.reqEduHours + "시간");
                $("#eduRate").text(data.eduTimeInfo.eduRate + "%");
                $(".percent").eq(0).html(data.eduTimeInfo.eduRate);
            }
            $("#mid-circle-1").html(data.ingCourseCount);
            $("#mid-circle-2").html(data.applyCourseCount);
            $("#mid-circle-3").html(data.finishCourseCount);
            studyTime('.condition .percent', '.condition .round-graph1');
        }
    });
}

function myLearn() {
    $.ajax({
        url: '/a/getMyLearnList.do',
        async: false,
        type: 'POST',
        timeout: 3000,
        dataType: "json",
        //data : JSON.stringify(requestData),
        error: function (e) {
            //console.log(e);
        },
        success: function (data) {
            var list = "";
            if (data.myLearnList.length != 0) {
                for (var i = 0; i < data.myLearnList.length; i++) {
                    var learn = data.myLearnList[i];
                    list += '<li class="learning-list">';
                    list += '<a href="#" onclick="location.href=\'' + learn.linkUrl + '\'" class="ellipsis">';
                    list += '<span>[' + learn.eduTypeNm + '] ' + learn.contTitle + '</span>';
                    list += '</a>';
                    list += '</li>';
                }
            } else {
                list += '<li class="learning-list">';
                list += '<a href="javascript:void(0)"><span>학습중인 과정이 없습니다.</span></a>';
                list += '</li>';
            }
            $("#learnList").eq(0).html(list);
        }
    });

}

//로딩페이지 만들기
function loadMaker() {
    html = '<div class="loading-block" id="loading-block"><img src="/anymobi/adm/images/loading-1.gif" alt=""/><p>불러오는 중 입니다.</p></div>';
    $('body').append(html);
}

function checkFields_special() {
    var special = /[\<>$%'"]/gi;
    var special_str = ['src', 'eval', 'expression', 'script', 'onload', 'xss', 'alert', 'iframe', 'frameset', 'bgsound', 'onblur', 'onchange', 'onclick', 'ondblclick', 'enerror', 'onfocus', 'onmouse', 'onscroll', 'onsubmit', 'onunload', '&lt', '&gt', '&#60', '&#62', '&#34', '&#39', '&#37', '&#40', '&#41', '&amp', '&#38', '&#43'];
    var result = true;

    var formInput = document.getElementsByTagName("input");
    var formTextarea = document.getElementsByTagName("textarea");

    for (i = 0; i < formInput.length; i++) {
        if (formInput[i].type == "text") {
            if (special.test(formInput[i].value.trim())) {
                formInput[i].focus();
                /*alert(formInput[i].title + "에는 특수문자(<, >, $, %, \',\")를 사용할 수 없습니다.");
                result = false;*/
                return result;
            }

            for (j = 0; j < special_str.length; j++) {
                if (formInput[i].value.trim().toLowerCase().indexOf(special_str[j]) > -1) {
                    formInput[i].focus();
                    /*alert(formInput[i].title + "에는 "+special_str[j]+" 문자를 사용할 수 없습니다.");
                    result = false;*/
                    return result;
                }
            }
        }
    }

    for (i = 0; i < formTextarea.length; i++) {
        if (special.test(formTextarea[i].value.trim())) {
            formTextarea[i].focus();
            /*alert(formTextarea[i].title + "에는 특수문자(<, >, $, %, \',\")를 사용할 수 없습니다.");
            result = false;*/
            return result;
        }

        for (j = 0; j < special_str.length; j++) {
            if (formTextarea[i].value.trim().toLowerCase().indexOf(special_str[j]) > -1) {
                formTextarea[i].focus();
                /*alert(formTextarea[i].title + "에는 "+special_str[j]+" 문자를 사용할 수 없습니다.");
                result = false;*/
                return result;
            }
        }
    }

    return result;
}

function examLog() {
    var preUrl = "";
    if (navigator.userAgent.toLowerCase().indexOf('ipad') > -1 ||
        (navigator.userAgent.toLowerCase().indexOf('android') > -1 && navigator.userAgent.toLowerCase().indexOf('mobile') == -1)) {
        preUrl = "/mobile/t";
    } else if (navigator.userAgent.indexOf('Mobile') > -1) {
        preUrl = "/mobile/m";
    }

    var requestData = {
        examId: $("input[name=examId]").val(),
        buttonType: 'FINISH'
    };
    $.ajax({
        url: preUrl + "/common/examLogAjax.do",
        type: 'POST',
        dataType: "json",
        data: JSON.stringify(requestData),
        error: function (e) {
            //console.log(e);
        },
        success: function (data) {
        }
    });
}

function refreshEvent() {
    if (event.keyCode == 116) {
        event.keyCode = 2;
        return false;
    } else if (event.ctrlKey && (event.keyCode == 78 || event.keyCode == 82)) {
        return false;
    }
}


//===================================================================================================================================
//app file down
function common_fileDown(fileList, fileNm) {
    if (fileList == '') {
        layerAlert("등록된 자료가 없습니다.");
        return;
    }

    var reqId = new Date().getTime();	// 다운로드 진행률 확인용 token
    var cookieName = "DOWNLOAD_" + reqId;
    var fileName = (fileNm != undefined && fileNm != null ? (fileNm + ' ') : '');

    showLoadingBar();
    resetCookie(cookieName);

    // 파일 다운로드 요청
    var url = '/file/download.do';
    if (typeof window.ReactNativeWebView !== 'undefined') {
        // App
        $.ajax({
            url: '/file/prepareDownload.do',
            type: 'POST',
            dataType: 'json',
            data: {
                token: fileList
            },
            success: function (res) {
                if (!res.success) {
                    hideLoadingBar();

                    var message = getErrorMessage(res.message);
                    alert(message);
                    return;
                }

                url += '?token=' + fileList + '&reqId=' + reqId;
                if (res.zipKey != null) url += '&zip=' + encodeURIComponent(res.zipKey);

                window.ReactNativeWebView.postMessage(
                    JSON.stringify({type: 'fileDown', data: {fileNm: fileNm}, url: url})
                );
            },
            error: function () {
                hideLoadingBar();
                alert('다시 시도해주세요.');
                return;
            }
        });
    } else {
        // Web/Mobile Web
//        location.href = url;

        var is_mobile = isMobile();
        var preUrl = is_mobile ? "/mobile/m" : "";

        // iframe 생성
        var iframe = document.createElement("iframe");
        var iframeId = "donwloadFrame_" + reqId;

        iframe.style.display = "none";
        iframe.id = iframeId;
        iframe.name = iframeId;

        document.body.appendChild(iframe);

        var form = document.createElement("form");
        form.action = preUrl + url;
        form.target = iframeId;
        form.method = "POST";

        addHiddenInput(form, "reqId", reqId);
        addHiddenInput(form, "token", fileList);
        addHiddenInput(form, "type", "web");

        document.body.appendChild(form);

        form.submit();
        form.remove();
    }

    // 다운로드 시작 감지
    var downloadCheck = setInterval(function () {
        var cookieValue = getCookie(cookieName);

        if (cookieValue && cookieValue.indexOf("done") > -1) {
            // 파일 다운로드 시작
            clearInterval(downloadCheck);

            $("#" + iframeId).remove();
            resetCookie(cookieName);

            hideLoadingBar();
        } else if (cookieValue && cookieValue.indexOf("error") > -1) {
            // 파일 다운로드 에러
            clearInterval(downloadCheck);

            $("#" + iframeId).remove();
            resetCookie(cookieName);

            hideLoadingBar();

            var errorType = cookieValue.split(":")[1];
            errorType = errorType.replace(/"/g, '').trim();

            var message = getErrorMessage(errorType);

            alert(message);
        }
    }, 500);
}

function getErrorMessage(errorType) {
    switch (errorType) {
        case "FILE_NOT_FOUND":
            return "존재하지 않는 파일입니다.";

        case "EXPIRED_TOKEN":
            return "유효하지 않은 토큰정보 입니다.";

        case "ACCESS_DENIED":
            return "파일에 대한 접근 권한이 없습니다.";

        case "FILE_TOO_LARGE":
            return "다운로드 가능한 최대 용량(25MB)을 초과했습니다. 웹 브라우저 환경에서 다운로드 해주세요.";

        case "FAILED":
            return "다시 시도해주세요.";

        default:
            return errorType;
    }
}

function getCookie(name) {
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");

    if (parts.length === 2) {
        return parts.pop().split(";").shift();
    }

    return null;
}

function resetCookie(id) {
    document.cookie = id + "=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; Max-Age=0";
}

function addHiddenInput(form, name, value) {
    var input = document.createElement("input");
    input.type = "hidden";
    input.name = name;
    input.value = value;

    form.appendChild(input);
}

/* show loading bar full screen */
function showLoadingBar() {
    html = '<div class="loading-bar-wrap"><div class="inner"><svg viewBox="25 25 50 50"><circle r="20" cy="50" cx="50"></circle></svg><p>파일 다운로드 중입니다.</p></div></div>';
    $('body').append(html);
}

/* hide loading bar */
function hideLoadingBar() {
    $(".loading-bar-wrap").remove();
}

function hiddenMaker(name, value, formId) {
    var inputTag = "<input type='hidden' name='" + name + "' value='" + value + "'/>";
    $("#" + formId).append(inputTag);
}

//===================================================================================================================================

//===================================================================================================================================
//공통
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

//===================================================================================================================================

