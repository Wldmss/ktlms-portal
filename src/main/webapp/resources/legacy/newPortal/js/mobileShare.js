function contShareBtn(contId) {
    var map = {};
    map["contId"] = contId;
    $.ajax({
        url: preUrl + '/getShareLinkAjax.do',
        type: 'POST',
        dataType: "json",
        data: JSON.stringify(map),
        error: function (xhr) {
            //console.log(xhr.status + " " + xhr.statusText);
        },
        success: function (data) {
            var temp = document.createElement("textarea");
            document.body.appendChild(temp);
            temp.value = "http://" + location.host + getPreURL() + data.linkUrl;
            temp.select();
            temp.setSelectionRange(0, 9999);
            document.execCommand("copy");
            document.body.removeChild(temp);
            layerAlert("URL 복사가 완료되었습니다.");
        }
    });

}

function contSharePack(args) {
    var contId = $(args).parent().find(".share-content").val();
    var map = {};
    map["contId"] = contId;
    $.ajax({
        url: preUrl + '/updateShareHitAjax.do',
        type: 'POST',
        dataType: "json",
        data: JSON.stringify(map),
        error: function (xhr) {
            //console.log(xhr.status + " " + xhr.statusText);
        },
        success: function (data) {
            var temp = document.createElement("textarea");
            document.body.appendChild(temp);
            temp.value = "https://" + location.host + getPreURL() + "/educontents/miniLecturePack/miniLecturePackDetail.do?contId=" + contId;
            temp.select();
            temp.setSelectionRange(0, 9999);
            document.execCommand("copy");
            document.body.removeChild(temp);
            layerAlert("URL 복사가 완료되었습니다.");
        }
    });

}

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

function getPreURL() {
    var url = '';
    if (isMobile())
        url = '/mobile/m';
    return url;
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