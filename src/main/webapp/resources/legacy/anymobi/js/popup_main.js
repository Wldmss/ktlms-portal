$(function () {
    prevent();
});

function prevent() {
    function getCookie(name) {
        var nameOfCookie = name + "=";
        var x = 0;
        while (x <= document.cookie.length) {
            //alert(document.cookie);
            var y = (x + nameOfCookie.length);
            if (document.cookie.substring(x, y) == nameOfCookie) {
                if ((endOfCookie = document.cookie.indexOf(";", y)) == -1) {
                    endOfCookie = document.cookie.length;
                }
                return unescape(document.cookie.substring(y, endOfCookie));
            }
            x = document.cookie.indexOf(" ", x) + 1;
            if (x == 0) break;
        }
        return "";
    }

    // 메인 배너 팝업 (Open Promotion)
    if (getCookie("popname") != "done") {
        var popOption = "width=524, height=680, left=50, top=50, scrollbars=no, location=no";     //팝업창 옵션(optoin)
        var popUrl = "/popupOpen.do"; //팝업창에 출력될 페이지 URL 2016-10-31 로그인페이지에서 띄우기 추가

        //window.open(popUrl, "pop", popOption);
    }

    // 메인 배너 팝업1 (Open Promotion)
    if (getCookie("popname1") != "done") {
        var popUrl = "/main/popupOpenPromotion1.do"; //팝업창에 출력될 페이지 URL
        var popOption = "width=481 height=460, left=150, top=150";     //팝업창 옵션(optoin) 스크롤시 약 16-17더 넣음

        //window.open(popUrl, "pop1", popOption);
    }
    // 메인 배너 팝업2 (Open Promotion)
    if (getCookie("popname2") != "done") {
        var popUrl = "/main/popupOpenPromotion2.do"; //팝업창에 출력될 페이지 URL
        var popOption = "width=680, height=500, scrollbars=yes, location=no, left=880, top=50";     //팝업창 옵션(optoin)
        //window.open(popUrl, "_blank", popOption);
    }
    // 메인 배너 팝업3 (Open Promotion)
    if (getCookie("popname3") != "done") {
        var popUrl = "/main/popupOpenPromotion3.do"; //팝업창에 출력될 페이지 URL
        var popOption = "width=550, height=688, left=600, top=50";     //팝업창 옵션(optoin)
        //window.open(popUrl, "_blank", popOption);
    }
    // 메인 배너 팝업4 (Open Promotion)
    if (getCookie("popname4") != "done") {
        var popUrl = "/main/popupOpenPromotion4.do"; //팝업창에 출력될 페이지 URL
        var popOption = "width=550, height=688, left=600, top=50";     //팝업창 옵션(optoin)
        //window.open(popUrl, "_blank", popOption);
    }
}

function deleteCookie(cookieName) {
    var expireDate = new Date();

    //어제 날짜를 쿠키 소멸 날짜로 설정한다.
    expireDate.setDate(expireDate.getDate() - 1);
    document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString() + "; path=/";
}
