$(function () {
    // 20190128_삭제
    //$("input, textarea").placeholder();

    // 20190128_삭제
    /*
    // nav main (left menu)
    $(".m-menu > li > a").click(function() {
        $(this).toggleClass("acticve");
        $(this).next().slideToggle(0);
    });
    $(".m-menu > li:first").find("a").trigger("click");
    */

    // 20190128_삭제
    /*
    // mCustomScrollbar plugin (nav)
    $(".j-scrollbar-nav").mCustomScrollbar({
        theme: "minimal-dark",
        scrollInertia: 300
    });
    */

    // mCustomScrollbar plugin (content)
    /*$(".j-scrollbar-content").mCustomScrollbar({
        theme: "minimal-dark",
        scrollInertia: 300,
        mouseWheelPixels: 300,
        keyboard : {enable : false}
    });*/

    // 20190128_추가
    // scrolling header animate
    var header = $('#header');
    var headerHeight = header.find('.top').height();
    $(window).scroll(function (event) {
        var scrollTop = $(window).scrollTop();

        if (scrollTop > headerHeight)
            header.addClass('hide');
        else
            header.removeClass('hide');
    });

    // tabs
    $(".tab-pannel > div").hide();
    $(".tab-pannel > div:nth-of-type(1)").show();
    $(".tabs > li:nth-of-type(1)").addClass("tab-active");

    $(".tabs li").click(function () {
        var this_prt = $(this).parent();
        var this_cdr = $(this).parent().children();

        $(this_prt).next().children().hide();

        var activeTab = $(this).attr("rel");
        if ($(this).hasClass("tab-active")) {
            $("#" + activeTab).fadeIn(0);
        } else {
            $("#" + activeTab).fadeIn();
        }
        ;

        $(this_cdr).removeClass("tab-active");
        $(this).addClass("tab-active");
    });

    // 20190128_삭제

    // 관리자페이지 접속 입력창 열기
    $(".jc-admin-btn").click(function () {
        $(".pop-admin-con").show().animate({top: 70}, 300);
        $(".j-scrollbar-content").mCustomScrollbar("disable");
    });
    // 관리자페이지 접속 입력창 닫기
    $(".jc-admin-close, #container").click(function () {
        $(".pop-admin-con").hide().animate({top: 45});
        $(".j-scrollbar-content").mCustomScrollbar("update");
    });


    // 개인정보취급방침 팝업
    /*$(".policy-btn").click(function() {
        window.open("../login/privacy_policy.html", "policy_popup", "width=1080, height=700");
        return false;
    });*/
    // text limit
    $(".j-text-limit textarea").keyup(function () {
        var maxLength = $(this).attr("data-maxLength");
        var textlength = this.value.length;
        if (textlength > maxLength) {
            this.value = this.value.substring(0, maxLength);
            alert("최대 " + maxLength + "자까지 등록 가능합니다.");
        } else {
            $(this).prev().find(".j-current").text(textlength);
        }
    });

});

function xssValidationCheck(ele) {
    for (var i = 0; i < ele.length; i++) {
        // console.log($(txtEle[i]).val());
        if ("" == $(ele[i]).val() || null == $(ele[i]).val()) {
            var ele_id = $(ele[i]).attr("id");
            var label_txt = $("label[for='" + ele_id + "']").text();
            console.log("id : " + ele_id + ", label : " + label_txt);
            showAlert(ele_id);

            return true;
        }
    }

    return false;
}

function showAlert(ele_id) {
    alert(ele_id + " is null");
    // 해당 id에 focus.
    $("#" + ele_id).focus();
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
