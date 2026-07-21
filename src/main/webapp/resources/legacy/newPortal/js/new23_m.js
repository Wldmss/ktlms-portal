$(document).ready(function () {
    // gnb
    var didScroll;
    var lastScrollTop = 0;
    var delta = 120;
    var navbarHeight = $('.l-gnb').outerHeight();

    $(window).scroll(function (event) {
        didScroll = true;
    });

    setInterval(function () {
        if (didScroll) {
            hasScrolled();
            didScroll = false;
        }
    }, 200);

    function hasScrolled() {
        var st = $(window).scrollTop();
        if (Math.abs(lastScrollTop - st) <= delta)
            return;
        if (st > lastScrollTop) {
            $('.l-gnb').removeClass('nav-down').addClass('nav-up');
        } else {
            $('.l-gnb').removeClass('nav-up').addClass('nav-down');
        }
        lastScrollTop = st;
    }

    // 3depth�� ������ ���
    $('.new2023-wrap .menu-1chd-list .l-menu-2chd').closest('.menu-1chd-list').find('.menu-1chd').addClass('has-icon');

    // ���հ˻�
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

    $('.gnb-top .search-btn').popup({
        target: $('.l-popup.search')
    })

    // �˻���ư
    function search() {
        var form = document.getElementById("totalSearchForm");
        var sword = $("#swordNm").val().trim();

        if (sword != "") {
            var objType = "SWORD";
            $("#objType").val(objType);
            form.action = preUrl + "/search/searchDetail.do";
            form.method = "post";
            form.submit();
        } else {
            layerAlert("�˻�� �Է��ϼ���.");
            return;
        }
    }

    $("#searchBtn").click(function () {
        search();
    });

    // gnb timeline menu
    $(".gnb-timeline .btn").off("click").on("click", function () {
        $(".gnb-timeline-wrap").addClass('on')
        $(".l-wrap").addClass('open-timeline-menu')
    });
    $(".gnb-timeline-close").off("click").on("click", function () {
        $(".gnb-timeline-wrap").removeClass('on')
        $(".l-wrap").removeClass('open-timeline-menu')
    });

    // tab
    $(".tab").not(".list-category.tab").click(function () {
        var tabId = $(this).data("tab");
        var $container = $(this).closest('.tabs').parent();

        $container.find(".tab").removeClass("active");
        $(this).addClass("active");

        $container.find(".tab-content").hide();
        $container.find("#" + tabId).show();
    });
    $(".tabs").not(".list-category").each(function () {
        $(this).parent().find(".tab-content:first").show();
    });

    //timeline
    $(".nested-accordion-header").addClass('active');

    $(".accordion-header .arrow").click(function () {
        var isActive = $(this).hasClass('active');
        if (!isActive) {
            $(this).closest('.accordion-header').toggleClass('active');
            $(this).closest('.accordion-header').next('.accordion-content').slideToggle();
            $(".timeline-item:not(.accordion-item) a").removeClass("selected");
        }
    });
    $(".nested-accordion-header .arrow").click(function () {
        $(this).toggleClass('active');
        $(this).closest('.nested-accordion-header').next('.nested-accordion-content').slideToggle();
    });

    // Ÿ�Ӷ��� lnb ��� ��ũ�� �̵� ȿ�� x / �и�
    $(".timeline-item:not(.accordion-item) a").click(function () {
        $(".timeline-item.accordion-item a").removeClass("selected");
        $(".timeline-item:not(.accordion-item) a").removeClass("selected");
        $(this).addClass("selected");
    });
    $(".timeline-item.accordion-item a").click(function () {
        $(".timeline-item.accordion-item a").removeClass("selected");
        $(".timeline-item:not(.accordion-item) a").removeClass("selected");
        $(this).addClass("selected");
        $('html, body').animate({scrollTop: 0}, 300);
    });

    // modal
    $('.open-modal').off("click").on("click", function () {
        var targetModal = $('#' + $(this).data('target'));
        targetModal.fadeIn()
        $('.modal-wrap').addClass('open');
        $('.new2023.l-gnb').addClass('on');
    });

    // ������ũ modal bottomsheet
    $('.navi-bottom-link').off("click").on("click", function () {
        var targetModal = $('#' + $(this).data('target'));
        if (targetModal.is(':visible')) {
            targetModal.fadeOut();
            $(this).closest('li').addClass('on');
            $('.modal-wrap').removeClass('open');
        } else {
            targetModal.fadeIn();
            $('.modal-wrap').addClass('open');
            $(this).closest('li').removeClass('on');
        }
        $('.new2023.l-gnb').addClass('on');
    });

    $('.close-modal').not('.alert').off("click").on("click", function () {
        var targetModal = $('#' + $(this).data('target'));
        targetModal.fadeOut()
        $('.modal-wrap').removeClass('open');
        $(this).closest('.modal-wrap').fadeOut();
        $('.new2023.l-gnb').removeClass('on');
        $('.navigation-tab-bar ul li').removeClass('on');
        $('.modal-select').removeClass('on')
    });
    $('.close-modal.alert').off("click").on("click", function () {
        var userConfirmed;

        if (userConfirmed) {
            $(this).closest('.modal-wrap').fadeOut();
            $('.new2023.l-gnb').removeClass('on');
        }
    });
    // ��� ���� ���� Ŭ�� �� ��� �ݱ�
    $(document).mouseup(function (e) {
        var $modalWrapFullBody = $('.modal-wrap.full > .modal-body');

        // Ŭ���� ��Ұ� .modal-wrap.full�� .modal-body �ȿ� �ִ� ��� �ƹ� �۾��� ���� ����
        if ($modalWrapFullBody.is(e.target) || $modalWrapFullBody.has(e.target).length > 0) {
            return;
        }
        // .modal-wrap.full�� modal-body�� �ƴ� ������ Ŭ���� ��� ��� �ݱ�
        $('.modal-wrap').not('.full').each(function () {
            var $modalBody = $(this).find('.modal-body');
            if (!$modalBody.is(e.target) && $modalBody.has(e.target).length === 0) {
                $(this).fadeOut().removeClass('open');
                $('.new2023.l-gnb').removeClass('on');
                $('.navigation-tab-bar ul li').removeClass('on');
                $('.modal-select').removeClass('on');
            }
        });
    });

    if ($('.sticky-bottom').length > 0) {
        $('.top-content > .ly-inner').css('padding-bottom', '130px');
    }
    // modal full
    $('.modal-wrap.full').each(function () {
        if (!$(this).find('.sticky-bottom').length) {
            $(this).find('.modal-body').css('height', '100%');
        }
    });
    // modal-select
    $('.modal-select').on("click", function () {
        $(this).addClass('on');
    });
    $('.modal-btn-wrap button').on("click", function () {
        $('.modal-select').removeClass('on')
    });

    // Ŀ�´�Ƽ ���ƿ� ��ư
    $('.feed-bottom-info .like').click(function () {
        $(this).toggleClass('on');
    });
    // Ŀ�´�Ƽ �������� �����ϱ�
    $('.hash-txt').click(function () {
        $(this).toggleClass('on');
    });
    // Ŀ�´�Ƽ ��� ���� �ݱ�
    $('.comment-wrap .comment-toggle').click(function () {
        const commentLists = $(this).siblings('.comment-list');
        const toggleText = $(this);

        commentLists.slideToggle(function () {
            toggleText.toggleClass('on');
            if (toggleText.hasClass('on')) {
                toggleText.text('��� ����');
            } else {
                toggleText.text('��� ����');
            }
        });
    });
    $('.comment-wrap .comment-toggle').click();

    // ���� ��ũ�� �� Ŭ�� �� ��� ����
    var $scrItem = $('.fluid-scroll .tab');
    var $scrItemFirst = $('.fluid-scroll .tab:first');
    var scrIWidth = 0;

    for (var i = 0; i < $scrItem.length; i++) {
        scrIWidth += $scrItem.eq(i).outerWidth(true);
    }
    var $tabsContainer = $('.fluid-scroll .tabs');
    $tabsContainer.css('width', scrIWidth);

    $scrItem.click(function () {
        var target = $(this);
        muCenter(target);
    });

    function muCenter(target) {
        var box = $('.fluid-scroll');
        var boxItem = box.find('.tab');
        var boxHarf = box.width() / 2;
        var pos;
        var listWidth = 0;
        var targetLeft = 0;

        boxItem.each(function () {
            listWidth += $(this).outerWidth(true);
        });

        for (var i = 0; i < target.index(); i++) targetLeft += boxItem.eq(i).outerWidth(true);

        var selectTargetPos = targetLeft + target.outerWidth(true) / 2;

        if (selectTargetPos <= boxHarf) {
            pos = 0;
        } else if (listWidth - selectTargetPos <= boxHarf) {
            pos = listWidth - box.width();
        } else {
            pos = selectTargetPos - boxHarf;
        }

        setTimeout(function () {
            box.animate({scrollLeft: pos}, 300);
        }, 200);
    }

    // �� timer fiexd
    var evaluateSolveWrap = $(".timer");

    if (evaluateSolveWrap.length) {
        var timer = evaluateSolveWrap.offset().top - 40;

        $(window).scroll(function () {
            var windowScroll = $(this).scrollTop();

            if (timer <= windowScroll) {
                evaluateSolveWrap.addClass("fixed");
                $(".paper-wrap").addClass("on");
            } else {
                evaluateSolveWrap.removeClass("fixed");
                $(".paper-wrap").removeClass("on");
            }
        });
    }
    // ������ ����Ǯ��(single) ������ ����
    var swiper6 = new Swiper(".evaluteSolveContSwiper .swiper-container", {
        slidesPerView: 1,
        autoHeight: true,
        effect: 'fade',
        allowTouchMove: false,
        on: {
            slideChange: function () {
                this.update(); // Swiper ������Ʈ
                // ������ �����̵��϶� �����ϱ�
                if (this.isEnd) {
                    $('.btn-evalute-solve .btn-submit').show();
                } else {
                    $('.btn-evalute-solve .btn-submit').hide();
                }
            },
        },
        navigation: {
            nextEl: '.btn-evalute-solve .swiper-button-next',
            prevEl: '.btn-evalute-solve .swiper-button-prev',
        },
    });

    //ī�崺��
    var swiper3 = new Swiper(".cardnewsSwiper", {
        slidesPerView: "auto",
        centeredSlides: true,
        spaceBetween: 16,
        slidesOffsetBefore: -5,
        breakpoints: {
            768: {},
        },
    });
    //3�� ���� ���� keyvisaul
    var swiper3 = new Swiper(".mmainVisualSwiper", {
        slidesPerView: 1,
        centeredSlides: true,
        autoplay: true,
        loop: false,
        breakpoints: {
            768: {},
        },
    });
    //3�� ���� ���� list
    var swiper4 = new Swiper(".courseListSwiper:not(.focus-on)", {
        slidesPerView: 2.3,
        autoplay: false,
        initialSlide: 0,
        loop: false,
        spaceBetween: 12,
        centeredSlides: false,
        slidesOffsetBefore: 20,
        slidesOffsetAfter: 20,
        breakpoints: {
            768: {
                // slidesPerView: 2.7,
                // slidesOffsetBefore: 120,
                // spaceBetween: 20,
                slidesOffsetBefore: 40,
            },
        },
    });
    var swiper5 = new Swiper(".courseListSwiper.focus-on", {
        slidesPerView: 1.15,
        autoplay: false,
        initialSlide: 0,
        loop: false,
        spaceBetween: 12,
        slidesOffsetAfter: 20,
        centeredSlides: false,
        slidesOffsetBefore: 20,
        breakpoints: {
            768: {
                slidesPerView: 2.2,
                // slidesOffsetBefore: 120,
                slidesOffsetBefore: 40,
            },
        },
    });
    var swiper6 = new Swiper(".tutorialSwiper", {
        slidesPerView: 1,
        autoplay: false,
        initialSlide: 0,
        loop: false,
        pagination: {
            el: ".swiper-pagination",
            clickable: true,
        },
        navigation: {
            nextEl: ".swiper-button-next",
            prevEl: ".swiper-button-prev",
        },
        on: {
            slideChangeTransitionStart() {
                var isLastSlide = swiper6.slides.length === (swiper6.activeIndex + 1);
                if (isLastSlide) {
                    $('.tutorial-wrap .btn-box.sticky-bottom,.no-display').css('display', 'block');
                } else {
                    $('.tutorial-wrap .btn-box.sticky-bottom,.no-display').css('display', 'none');
                }
            }
        },
    });
});


$(function () {
    //�ܹ��� �޴� gnb
    $('.header_gnb_list .gnb_1depth a').click(function () {
        $('.header_gnb_list .gnb_1depth li').removeClass('active');
        $(this).parent('li').toggleClass('active');
        var idx = $(this).parent('li').index();
        activeReset('.header_gnb_list .gnb_2depth');
        $('.header_gnb_list .gnb_2depth .depth_1_title .sub_menu').css('display', 'none');
        $('.header_gnb_list .gnb_2depth > li:nth-child(' + (idx + 1) + ')').addClass('active');
    });

    //active �ʱ�ȭ
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
    $('.navigation-tab-bar ul li').off("click").on("click", function (e) {
        e.preventDefault
        $(this).toggleClass('on');
        $('.navigation-tab-bar ul li').not(this).removeClass('on');
    });
    // �ϴ� �׺���̼� �ٰ� ������ Ǫ��
    if ($('.navigation-tab-bar').length > 0) {
        $('.l-footer,.l-content').css('padding-bottom', '64px');
        $('.sticky-bottom').css('bottom', '60px');
    }
    // �ϴ� �׺���̼� ī�װ���
    $(".navi-bottom-menu").off("click").on("click", function (e) {
        e.preventDefault
        $(".nav-category-wrap").toggleClass('active');
    });

    // ���հ˻� �ֱٰ˻��� toggle
    $(".integrated-search-arrow").off("click").on("click", function (e) {
        e.stopPropagation();
        $(this).toggleClass('on');
        $(this).closest('.input-search-wrap').siblings('.intregrated-search-result-box').toggleClass('on');
    });
    $(document).mouseup(function (e) {
        var _arrow = $('.integrated-search-arrow');
        var _target = $('.intregrated-search-result-box');
        if (_target.has(e.target).length === 0 && !_arrow.is(e.target)) {
            _target.removeClass('on');
            _arrow.removeClass('on');
        }
    });
    // ���հ˻� �����ϱ� toggle
    $(".intregrated-search-result-item .related-list .right-list .ico.ico1").click(function () {
        $(this).toggleClass("on");
    });
    // ���հ˻� ��ü��� ����,�ݱ� : �Լ�ȣ��
//	$(document).ready(function() {
//		moveMoreClickEvent();
//	});

    // tooltip
    $('.tooltip-open').click(function () {
        var $tooltipInner = $(this).siblings('.tooltip-inner');
        var tooltipPos = $tooltipInner.data('js-tooltip-pos');
        var tooltipSize = $tooltipInner.data('js-tooltip-size');

        $(this).toggleClass('open');
        $tooltipInner.toggleClass('on');

        // ���� ��ġ ����
        $tooltipInner.css(Object.assign({}, tooltipPos, tooltipSize));
    });
    $(document).click(function (event) {
        if (!$(event.target).closest(".tooltip-wrap").length) {
            $(".tooltip-wrap .tooltip-inner").removeClass('on');
            $('.tooltip-open').removeClass('open');
        }
    });
    $('.top-level-btn .tooltip-btns').each(function () {
        $(this).off("click").on("click", function () {
            $(this).toggleClass('on')
        });
    });

    // �˸�
    $('.m-alarm-txt.cancel').hide();
    $('.alarm-top-button-wrap').hide();
    $('.m-alarm-txt.edit').click(function () {
        $('.alarm-checkbox-wrap').show();
        $('.m-alarm-txt.edit').hide();
        $('.m-alarm-txt.cancel').show();
        $('.alarm-top-button-wrap').show();
    });
    $('.m-alarm-txt.cancel').click(function () {
        $('.alarm-checkbox-wrap').hide();
        $('.m-alarm-txt.cancel').hide();
        $('.m-alarm-txt.edit').show();
        $('.alarm-top-button-wrap').hide();
    });

    $('#alarmDelete-mo').click(function () {
        var checkedItems = $('.alarm-checkbox-wrap .checkbox input[type=checkbox]:checked');

        checkedItems.closest('.box-line-gray').remove();
        $('.m-alarm-txt.cancel').hide();
        $('.m-alarm-txt.edit').show();
        $('.alarm-checkbox-wrap').hide();
        $('.alarm-top-button-wrap').hide();

        if ($('.box-line-gray').length === 0) {
            $('.alarm-top-button-wrap').hide();
            $('.alarm-list-wrap .nolist').addClass('on');
            $('.m-alarm-txt').hide();
        }
    });
    if ($('.alarm-list-wrap .box-line-gray').length == 0) {
        $('.nolist').addClass('on');
        $('.m-alarm-txt.edit').css('display', 'none');
    }

});


