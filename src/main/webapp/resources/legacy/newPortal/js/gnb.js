var viewportWidth = $(window).width();
var viewChk = false
if (viewportWidth < 1200) {
    viewChk = true
}
$(function () {
    if (!viewChk) {
        $(window).scroll(function () {
            var scrollTop = $(this).scrollTop();
            var noticeH = $(".l-pop-notice").height();
            var searchH = 140 - scrollTop;
            if ($(".l-pop-notice").hasClass("on") && noticeH >= scrollTop) {
                $(".l-gnb").addClass("notice");
                $(".l-popup.search .popup.full.notice-on").css('top', searchH + "px");
            } else {
                $(".l-gnb").removeClass("notice");
                $(".l-popup.search .popup.full.notice-on").css('top', '20px');
            }
        });

        var gnbH = $(".l-gnb").height();
        $(".main-visual").css("padding-top", gnbH);
        if ($(".sub-visual").css("margin-top") == gnbH + "px") {
            $(".l-gnb").removeClass("notice");
        }
    }
    $(".l-sub-menu-content > .sub-menu-content").hide();


    $(document).on("mouseenter", ".l-menu > .menu-ul > .menu-list.slide", function () {
        if (!viewChk) {
            var menuCurrentIndex = $(".l-menu > .menu-ul > .menu-list").index(this);
            $(".l-gnb").addClass("on");
            $(".l-menu > .menu-ul > .menu-list").not(".management").removeClass("on");
            $(this).addClass('on');
            $(".l-sub-menu-content > .sub-menu-content").hide();
            $(".l-sub-menu-content > .sub-menu-content").eq(menuCurrentIndex).show();

            $('.l-sub-menu-content').slideDown(
                {
                    duration: 100,
                    complete: function () {
                        $(".l-sub-menu-content > .sub-menu-content").hide();
                        $(".l-sub-menu-content > .sub-menu-content").eq(menuCurrentIndex).show();
                    }
                });

            return false;
        }
    })
//
    $(document).on("mouseenter", ".l-menu > .menu-ul > .menu-list.no-slide", function () {
        $(".l-gnb").addClass("on");
        $(".l-menu > .menu-ul > .menu-list").removeClass("on");
        $(this).addClass('on');
        $(".l-sub-menu-content > .sub-menu-content").hide();
        //$(".l-sub-menu-content").slideUp();
    });

    $(document).on("mouseleave", ".l-gnb", function () {
        if (!viewChk) {
            $(this).removeClass('on');
            $(this).find('.l-sub-menu-content').slideUp();
            return false;
        }
    });


    $(document).on("mouseenter", ".menu-2chd-list", function () {
        if (!viewChk) {
            $(this).parents('.menu-1chd-list').addClass('on');
            $(this).addClass('on');
        }
    })
    $(document).on("mouseleave", ".menu-2chd-list", function () {
        if (!viewChk) {
            $(this).parents('.menu-1chd-list').removeClass('on');
            $(this).removeClass('on');
        }
    })
    $('.m-gnb-btn').off().click(function () {
        if (viewChk) {
            if ($(this).parents('.l-gnb').hasClass('on')) {
                $(this).parents('.l-gnb').removeClass('on')

                $(this).parents('.l-gnb').find('.gnb-content').animate({
                        right: '-100%'
                    },
                    {
                        duration: 500,
                        complete: function () {
                            $('.gnb-container').hide();

                        }
                    }
                );
                $('body').removeClass('no-scroll')
            } else {
                $(this).parents('.l-gnb').addClass('on');
                $('.gnb-container').show();
                $(this).parents('.l-gnb').find('.gnb-content').animate({
                    right: 0
                }, 500);
                $('body').addClass('no-scroll')

            }
            return false;
        }
    })
    $(document).on('click', '.menu-list', function (event) {
        event.stopPropagation();
        if (viewChk) {
            $(this).toggleClass('on');
        }
    })
    $(document).on('click', '.menu-1chd-list', function (event) {
        event.stopPropagation();
        if (viewChk) {
            $(this).toggleClass('on');
        }
    })
    $(document).on('click', '.menu-2chd-list', function (event) {
        event.stopPropagation();
        if (viewChk) {
            $(this).toggleClass('on');
        }
    })
})
$(document).on('click', '.l-gnb .gnb.mob', function (e) {
    if (viewChk) {
        if (e.target.className == "gnb-container") {
            // $('.l-gnb').removeClass('on')
            var tH = $('.l-gnb').height();
            $('.l-gnb').find('.gnb-content').animate({
                    right: '-100%',
                    height: tH
                },
                {
                    duration: 500,
                    complete: function () {
                        $('.gnb-container').hide();
                        $('body').addClass('no-scroll');
                        $('.menu-list').removeClass('on');
                        $('.menu-1chd-list').removeClass('on');
                        $('.menu-2chd-list').removeClass('on');
                    }
                }
            );
            $('body').removeClass('no-scroll');
        } else {
            return false;
        }
    }
});
$(document).ready(function () {
    $('.l-pop-notice .btn.close').click(function () {
        $(this).parents('.l-pop-notice').hide();
        var tH = $('.l-gnb').height();
        $('.l-popup.learning .popup').css({
            'top': tH
        });
    })
})
