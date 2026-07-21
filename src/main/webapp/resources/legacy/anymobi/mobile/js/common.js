$(document).ready(function () {
    // side(left) menu
    $("#wrapper").before("<div id='overlay' class='j-cover-close'></div>");
    var Nav_width = $("nav").width();

    function smenu_open() {
        $("body").css("overflow", "hidden");
        $("#wrapper").bind("touchmove", function (event) {
            event.preventDefault();
        });
        $("#overlay").fadeIn();
        $("nav").show().animate({left: 0}, 300);
    };

    function smenu_close() {
        $("body").css("overflow", "auto");
        $("#wrapper").unbind("touchmove");
        $("nav").animate({left: -Nav_width}, 300).hide();
        $("#overlay").fadeOut();
    };
    $("nav").css("left", -Nav_width);
    $(".j-smenu-open").click(function () {
        smenu_open();
    });
    $(".j-cover-close").click(function () {
        smenu_close();
    });

    // 2016-04-22
    // popup position center
    var docW = $(document).width();
    var popW = $(".cc-popup-con").outerWidth();
    var popC = (docW - popW) / 2;
    //$(".cc-popup-con").css("left", popC);

});

Date.prototype.yyyymmdd = function () {
    var yyyy = this.getFullYear().toString();
    var mm = (this.getMonth() + 1).toString(); // getMonth() is zero-based
    var dd = this.getDate().toString();
    return yyyy + "-" + (mm[1] ? mm : "0" + mm[0]) + "-" + (dd[1] ? dd : "0" + dd[0]); // padding
};

var toDate = function (mmDDYYY) {
    var yyyy = mmDDYYY.substring(0, 4);
    var mm = mmDDYYY.substring(4, 6);
    var dd = mmDDYYY.substring(6, 8);
    return yyyy + "." + mm + "." + dd;
};

function windowScroll() {
    $(window).scroll(function () {
        if ($(window).scrollTop() >= $(document).height() - $(window).height() - 100) {
            if (paging.totalCount == 10 && paging.searchTextFlag) {
                if (paging.totalCount == paging.pageTotalCnt) {
                    if (paging.searchTextFlag) {
                        paging.searchTextFlag = false;
                    }
                } else if (paging.pageTotalCnt <= paging.currentLength) {
                    return;
                } else {
                    if (!paging.loading) loadNextPage();
                }
            } else {
                if (paging.searchTextFlag) {
                    paging.searchTextFlag = false;
                }
            }
        }
    });
}

//Load content for next page
function loadNextPage() {
    paging.param.page = ++paging.param.page;
    paging.ajaxProc(paging.param, fnAjaxPagingCallback);
}

var paging = {
    per_page: "",
    page: 1,
    totalCount: 10,
    searchTextFlag: true,
    url: "",
    param: {},
    html: "#list-lazyloader",
    loading: false,
    pageTotalCnt: 0,
    ajaxProc: function (param, callback) {
        this.loading = true; //interlock to prevent multiple calls
        //$.mobile.loading('show');
        $.ajax({
            method: 'GET',
            url: this.url,
            dataType: 'json',
            data: param,
            success: function (data) {
                callback(data);
            },
            error: function () {

            },
            progress: function (e) {
            }
        });
    }
};


Number.prototype.format = function () {
    if (this == 0) return 0;

    var reg = /(^[+-]?\d+)(\d{3})/;
    var n = (this + '');

    while (reg.test(n)) n = n.replace(reg, '$1' + ',' + '$2');

    return n;
};


String.prototype.format = function () {
    var num = parseFloat(this);
    if (isNaN(num)) return "0";

    return num.format();
};


function getEdutimesFormat(edutimes) {
    return edutimes.toFixed(2).format();
}


//===================================================================================================================================
// IOS Link
function runApp_ios(code, userid) {
    //slp 3.0
    var ios_appUrl = "http://edu.ncs-slp.com/m/main/MobileSso.jsp?companyCd=" + code + "&empNo=" + userid;

    if (userid == "test1062") {
        if (typeof window.ReactNativeWebView !== 'undefined') {
            window.ReactNativeWebView.postMessage(
                JSON.stringify({type: 'openUrl', url: ios_appUrl})
            );
        } else {
            location.href = ios_appUrl;
        }
    } else if (userid == "10062230") {
        if (typeof window.ReactNativeWebView !== 'undefined') {
            window.ReactNativeWebView.postMessage(
                JSON.stringify({type: 'openUrl', url: ios_appUrl})
            );
        } else {
            location.href = ios_appUrl;
        }
    } else {
        if (typeof window.ReactNativeWebView !== 'undefined') {
            window.ReactNativeWebView.postMessage(
                JSON.stringify({type: 'openUrl', url: ios_appUrl})
            );
        } else {
            location.href = ios_appUrl;
        }
    }

}

//===================================================================================================================================
// android old link

var android_marketUrl = "http://edu.kt.com/upload/appdown/edu/edu.html";
var android_appUrl = "kthrd.edu://";
var android_intent = "kthrd.edu://";

var timer;
var heartbeat;
var iframe_timer;

function clearTimers() {
    clearTimeout(timer);
    clearTimeout(heartbeat);
    clearTimeout(iframe_timer);
}

function intervalHeartbeat() {
    if (document.webkitHidden || document.hidden) {
        clearTimers();
    }
}

function tryIframeApproach() {
    var iframe = document.createElement("iframe");
    iframe.style.border = "none";
    iframe.style.width = "1px";
    iframe.style.height = "1px";
    iframe.onload = function () {
        document.location = android_marketUrl;
    };
    iframe.src = android_appUrl;
    document.body.appendChild(iframe);
}

function tryWebkitApproach() {

    location.href = 'intent://kthrd.edu#Intent;scheme=kthrd.edu;package=com.ktinnoedu.kthrd.edu;S.browser_fallback_url=http%3A%2F%2Fedu.kt.com/upload/appdown/edu/edu.html;end';

    /*document.location = android_appUrl;
    timer = setTimeout(function () {
        document.location = android_marketUrl;
    }, 2500);*/
}

function useIntent() {
    document.location = android_intent;
}

//===================================================================================================================================
//android link
function launch_app_or_alt_url(el, code, userid) {
    // slp 3.0
    var and_appUrl = "http://edu.ncs-slp.com/m/main/MobileSso.jsp?companyCd=" + code + "&empNo=" + userid;

    var index = navigator.userAgent.toLowerCase().indexOf('chrome');
    var index2 = navigator.userAgent.toLowerCase().indexOf('safari');
    var version = 0;
    var version2 = 0;
    if (index > 0)
        version = navigator.userAgent.toLowerCase().substring(index + 7, index + 9);
    if (index2 > 0)
        version2 = navigator.userAgent.toLowerCase().substring(index2 + 7, index2 + 10);

    if (typeof window.ReactNativeWebView !== 'undefined') {
        window.ReactNativeWebView.postMessage(
            JSON.stringify({type: 'openUrl', url: and_appUrl})
        );
    } else {
        location.href = and_appUrl;
    }

}

//===================================================================================================================================
// old version browser
function chkIsSupportingFallbackURL(isSupportedFallbackURL) {
    var SCHEME_FOR_TEST = 'intent://test.kthrd.edu#Intent;scheme=;end';

    var iframe = document.createElement('IFRAME');
    iframe.style.display = 'none';
    self = this;

    iframe.addEventListener('load', function onload() {

        if (iframe.src === SCHEME_FOR_TEST) {
            isSupportedFallbackURL.bool = true;
        }
        iframe.removeEventListener('load', onload);
        document.body.removeChild(iframe);
    });

    iframe.src = SCHEME_FOR_TEST;

    document.body.appendChild(iframe);
    setTimeout(function () {
        iframe.src = '';
    }, 10);
}

//===================================================================================================================================
// run app
function runApp(code, userid) {
    var devName = OSInfoDev();

    if (devName.indexOf("MacOS") != -1 || devName.indexOf("iPad") != -1 || devName.indexOf("iPhone") != -1) {
        runApp_ios(code, userid);
    } else if (devName.indexOf("Android") != -1) {
        launch_app_or_alt_url($(this), code, userid);
        event.preventDefault();
    }
}

//===================================================================================================================================
function runApp_kate(code, userid) {
    //alert('KT 이러닝 App 설치 후 학습하시기 바랍니다. 이러닝 App은 mkate.kt.com에서 다운로드 받으실 수 있습니다');
    var devName = OSInfoDev();
    if (devName.indexOf("MacOS") != -1 || devName.indexOf("iPad") != -1 || devName.indexOf("iPhone") != -1) {
        runApp_kate_and(code, userid);
        //location.href='mkate://app_in_app?info=com.ktinnoedu.ncsslp.edu||http://edu.ncs-slp.com/down/Appdown.jsp||ncsslp.edu://?companyCd=1001&empNo='+userid;
    } else if (devName.indexOf("Android") != -1) {
        runApp_kate_and(code, userid);
        //location.href='mkate://app_in_app?info=com.ktinnoedu.ncsslp.edu||http://edu.ncs-slp.com/down/Appdown.jsp||ncsslp.edu://?companyCd=1001&empNo='+userid;
        event.preventDefault();
    }
}

function runApp_kate_ios(code, userid) {
    var mkate_url = '';
    mkate_url = 'mkate://external_browser?info=http://edu.ncs-slp.com/m/main/MobileSso.jsp?companyCd=' + code + '&empNo=' + userid;

    if (typeof window.ReactNativeWebView !== 'undefined') {
        window.ReactNativeWebView.postMessage(
            JSON.stringify({type: 'openUrl', url: mkate_url})
        );
    } else {
        location.href = mkate_url;
    }

}

function runApp_kate_and(code, userid) {
    var mkate_url = '';
    mkate_url = 'mkate://external_browser?info=http://edu.ncs-slp.com/m/main/MobileSso.jsp?companyCd=' + code + '&empNo=' + userid;

    if (typeof window.ReactNativeWebView !== 'undefined') {
        window.ReactNativeWebView.postMessage(
            JSON.stringify({type: 'openUrl', url: mkate_url})
        );
    } else {
        location.href = mkate_url;
    }
}

//기기 체크
function isTabletDevice() {
    // 태블릿검사

    if (navigator.userAgent.toLowerCase().indexOf('ipad') > -1 ||
        (navigator.userAgent.toLowerCase().indexOf('android') > -1 && navigator.userAgent.toLowerCase().indexOf('mobile') == -1)) {
        return true;
    }

    // 갤럭시 탭만을 위한 리다이렉트. Mobile 이라는 단어가 안들어오게 되면 지우셔도 됨

    var galaxyTabModel = new Array('shw-', 'tab', 'tablet', 'pad');
    for (i = 0; i < galaxyTabModel.length; i++) {
        if (navigator.userAgent.toLowerCase().indexOf(galaxyTabModel[i]) > -1) {
            return true;
        }
    }
    return false;
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
                alert(formInput[i].title + "에는 특수문자(<, >, $, %, \',\")를 사용할 수 없습니다.");
                result = false;
                return result;
            }

            for (j = 0; j < special_str.length; j++) {
                if (formInput[i].value.trim().toLowerCase().indexOf(special_str[j]) > -1) {
                    formInput[i].focus();
                    alert(formInput[i].title + "에는 " + special_str[j] + " 문자를 사용할 수 없습니다.");
                    result = false;
                    return result;
                }
            }
        }
    }

    for (i = 0; i < formTextarea.length; i++) {
        if (special.test(formTextarea[i].value.trim())) {
            formTextarea[i].focus();
            alert(formTextarea[i].title + "에는 특수문자(<, >, $, %, \',\")를 사용할 수 없습니다.");
            result = false;
            return result;
        }

        for (j = 0; j < special_str.length; j++) {
            if (formTextarea[i].value.trim().toLowerCase().indexOf(special_str[j]) > -1) {
                formTextarea[i].focus();
                alert(formTextarea[i].title + "에는 " + special_str[j] + " 문자를 사용할 수 없습니다.");
                result = false;
                return result;
            }
        }
    }

    return result;
}


var preUrl = '';
if (isMobile())
    preUrl = '/mobile/m';
