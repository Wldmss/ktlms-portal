if (typeof Swiper === 'undefined') {
    window.Swiper = function () {
        return {};
    };
}

// timeline
$(document).ready(function () {
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

    // 타임라인 lnb 상단 스크롤 이동 효과 x / 분리
    $(".timeline-item:not(.accordion-item) a").click(function () {
        $(".timeline-item.accordion-item a").removeClass("selected"); /* 추가 */
        $(".timeline-item:not(.accordion-item) a").removeClass("selected");
        $(this).addClass("selected");
    });
    $(".timeline-item.accordion-item a").click(function () {
        $(".timeline-item.accordion-item a").removeClass("selected");
        $(".timeline-item:not(.accordion-item) a").removeClass("selected");
        $(this).addClass("selected");
        $('html, body').animate({scrollTop: 0}, 300);
    });
    // 타임라인 lnb 아이콘
    $('.info-top .inner .top-level-btn .level-btn').each(function () {
        $(this).find('button').off("click").on("click", function () {
            $(this).toggleClass('on')
        });
    });
    $(document).on("click", ".new2023-wrap.micro-running .left-content > img", function () {
        const targetH = $(this).closest('.left-content').outerWidth();
        $(this).closest('.left-content').toggleClass('on')
    });

    // selectbox
    if ($('.select-area select').length > 0) {
        $(".select-area select").selectric({});
    }
    if ($('.selectric-items li').length == 1) {
        console.log('한개')
        $('.selectric-items').remove();
    }
    // custom selectbox
    $('.custom-select-box').click(function () {
        const customOptions = $(this).find('.custom-options');
        customOptions.toggle();
        $(this).toggleClass('on');
        $('.custom-select-box').not(this).find('.custom-options').hide();
        $('.custom-select-box').not(this).removeClass('on');
    });

    $(document).click(function (event) {
        if (!$(event.target).closest('.custom-select-box').length) {
            $('.custom-options').hide();
            $('.custom-select-box').removeClass('on');
        }
    });
    $('.custom-select-box .categoryname').change(function () {
        if ($(this).is(':checked')) {
            $(this).closest('label.checkbox').addClass('checked');
        } else {
            $(this).closest('label.checkbox').removeClass('checked');
        }
    });

    $("#fileSearch").on('change', function () {
        var fileName = $("#fileSearch").val();
        $(".upload-name").val(fileName);
    });


});

// main 및 공통
$(document).ready(function () {
    //input typing
    $(".input-search-wrap .search-space").on("input", function () {
        if ($(this).val().trim() !== "") {
            $(this).closest('.input-search-wrap').addClass("on");
        } else {
            $(this).closest('.input-search-wrap').removeClass("on");
        }
    });


    // modal
    // 팝업 안에 있는 selectric
    function onPopupselecric() {
        if ($('.modal-wrap .selectric-items li').length === 1) {
            console.log('한개');
            $('.modal-wrap .selectric-items').remove();
        }
    }

    var isModalOpen = false;

    $('.open-modal').click(function () {
        onPopupselecric(); // 팝업 호출
        var targetModal = $('#' + $(this).data('target'));
        targetModal.fadeToggle();
        $('.new2023.l-gnb').addClass('on');
        isModalOpen = !isModalOpen;
    });

    $('.close-modal').not('.alert').click(function () {
        $(this).closest('.modal-wrap').fadeOut();
        $('.new2023.l-gnb').removeClass('on');
        isModalOpen = false;
    });

    $('.close-modal.alert').click(function () {
        var userConfirmed;
        if (userConfirmed) {
            $(this).closest('.modal-wrap').fadeOut();
            $('.new2023.l-gnb').removeClass('on');
            isModalOpen = false;
        }
    });

    // 모달 외의 영역 클릭 시 모달 닫기
    $(document).mouseup(function (e) {
        var modalContent = $('.modal-content');
        if (isModalOpen && !modalContent.is(e.target) && modalContent.has(e.target).length === 0) {
            $('.modal-wrap').fadeOut();
            $('.new2023.l-gnb').removeClass('on');
            isModalOpen = false;
        }
    });

    // gnb
    $(".gnb-search").off("click").on("click", function () {
        $(this).addClass('on');
        $(".l-popup.search").addClass('on');

        var isOpen = $(".gnb-search").hasClass("on");
        if (isOpen) {
            $(".l-popup.search").css('display', 'block');
        } else {
            $(".l-popup.search").css('display', 'none');
        }
    });
    $(".l-popup.search .close-btn").off("click").on("click", function () {
        $(".l-popup.search").css('display', 'none');
        $(".gnb-search").removeClass('on')
    });
    $(".l-pop-notice .btn-wrap").click(function () {
        $(".l-gnb.notice").removeClass('notice');
    });

    $('.gnb-1depth-link,.gnb-2depth').mouseover(function () {
        $(this).parent('.gnb-1depth-item').addClass('on');
    });
    $('.gnb-1depth-link,.gnb-2depth').mouseleave(function () {
        $(this).parent('.gnb-1depth-item').removeClass('on');
    });

    // 메인 그룹인재개발실 대표 신청 과정
    $(".mainSwiper").each(function (index, element) {
        var mainslideNum = $(this).find('.swiper-slide').length;
        var swiper = new Swiper(".mainSwiper", {
            slidesPerView: 'auto',
            centeredSlides: true,
            spaceBetween: 278,
            loop: mainslideNum >= 2,	//두개 이상일때만 loop
            navigation: {
                nextEl: '.mainSwiper .swiper-button-next', // 다음 버튼 클래스명
                prevEl: '.mainSwiper .swiper-button-prev', // 이번 버튼 클래스명
            },
            on: {
                init: function () {
                    if (mainslideNum == 1) {
                        $('.mainSwiper .swiper-button-next').hide();
                        $('.mainSwiper .swiper-button-prev').hide();
                    }
                },
            }
        });
    });

    // 과정상세 문제풀기 체크
    var swiper2 = new Swiper(".evaluteSolveSwiper .swiper-container", {
        slidesPerView: 1,
        // loop: true,
        navigation: {
            nextEl: '.evaluteSolveSwiper .swiper-button-next',
            prevEl: '.evaluteSolveSwiper .swiper-button-prev',
        },
    });

    // 과정상세 문제풀기(single) 컨텐츠 영역
    var swiper3 = new Swiper(".evaluteSolveContSwiper .swiper-container", {
        slidesPerView: 1,
        autoHeight: true,
        effect: 'fade',
        allowTouchMove: false,
        on: {
            slideChange: function () {
                this.update(); // Swiper 업데이트
                // 마지막 슬라이드일때 제출하기
                if (this.isEnd) {
                    $('.btn-submit').show();
                } else {
                    $('.btn-submit').hide();
                }
            },
        },
        navigation: {
            nextEl: '.evaluteSolveContSwiper .swiper-button-next',
            prevEl: '.evaluteSolveContSwiper .swiper-button-prev',
        },
    });

    // 카드뉴스
    function updateImageWidth() {
        var imageWidth = $('.cardnews-wrap .image').width() + 200;
        $('.cardnews-wrap').width(imageWidth);
    }

    $(document).ready(function () {
        // 페이지 로드 시 초기 실행
        updateImageWidth();

        var swiper3 = new Swiper(".cardnewsSwiper", {
            slidesPerView: 1,
            centeredSlides: false,
            loop: false,
            autoHeight: false,
            aspectRatio: 500 / 750,
            navigation: {
                nextEl: '.cardnewsSwiper .swiper-button-next', // 다음 버튼 클래스명
                prevEl: '.cardnewsSwiper .swiper-button-prev', // 이번 버튼 클래스명
            },
        });
        $(window).resize(function () {
            updateImageWidth();
        });
    });

    // 메인 코스리스트
    $(".is-list").each(function (index, element) {
        var $this = $(this);
        var $courseListSwiper = $this.find('.courseListSwiper');
        var slideNum = $courseListSwiper.find('.swiper-slide').length;

        var options = {
            centeredSlides: false,
            loop: slideNum >= 5, // 5개 이상인 경우에만 loop 활성화
            threshold: 5,
            navigation: {
                nextEl: $courseListSwiper.parents('.course-list-wrap').find('.swiper-button-next')
            },
            allowSlidePrev: slideNum >= 5, // 5개 이상인 경우에만 이전 슬라이드로 이동 허용
            allowSlideNext: slideNum >= 5, // 5개 이상인 경우에만 다음 슬라이드로 이동 허용
        };

        if (slideNum >= 5) {
            options.slidesPerView = 'auto';
        } else {
            options.slidesPerView = slideNum;
            nextEl: $courseListSwiper.parents('.course-list-wrap').find('.swiper-button-next').hide();
        }
        var swiper = new Swiper($courseListSwiper, options);
    });

    // accordion
    $(".accordion-list .acco-header").click(function () {
        const item = $(this).closest(".item");
        const isOpen = item.hasClass("open");
        if (!isOpen) {
            item.addClass("open");
            item.find(".acco-content").slideDown();
        } else {
            item.removeClass("open");
            item.find(".acco-content").slideUp();
        }
    });

    $(".acco-sub-header").click(function (e) {
        e.stopPropagation();
        $(".accordion-list-wrap.type2 .acco-sub-content .inner a").removeClass("on");
        const subItem = $(this);
        const subContent = subItem.next(".acco-sub-content");
        const isType2 = subItem.parents(".accordion-list-wrap.type2").length > 0;
        const hasOnlyClass = subItem.hasClass("only");

        if (subContent.length || isType2) {
            if (subItem.hasClass("sub-open")) {
                subItem.removeClass("sub-open");
                subContent.slideUp();
                subItem.find("input[type='checkbox']").prop("checked", false);
                if (hasOnlyClass) {
                    subItem.addClass("sub-open");
                }
            } else {
                $(".acco-sub-header").removeClass("sub-open");
                $(".acco-sub-content").slideUp();
                subItem.addClass("sub-open");
                subContent.slideDown();
                subItem.find("input[type='checkbox']").prop("checked", true);
            }
        }
    });

    $(".accordion-list-wrap.type2 .acco-sub-content .inner a").click(function (e) {
        e.preventDefault();
        $(this).addClass("on").siblings().removeClass("on");
    });
    $(".acco-sub-content").click(function (e) {
        e.stopPropagation();
    });
    $(".acco-sub-header").each(function () {
        const subHeader = $(this);
        const checkbox = subHeader.find("input[type='checkbox']");

        subHeader.on("click", function () {
            if (subHeader.hasClass("sub-open")) {
                checkbox.prop("checked", false);
            }
        });
        checkbox.click(function (e) {
            e.stopPropagation();
        });
    });

    // 컨텐츠 타입
    $(".contentlist-view-type a").click(function () {
        const contentType = $(this).data("content-type");
        const $currentContent = $(".contentlist-veiw .content-type-content.on");
        const $targetContent = $('.contentlist-veiw .content-type-content.' + contentType);

        $(".contentlist-view-type a").removeClass("on");
        $(this).addClass("on");

        if (!$targetContent.hasClass("on")) {
            $currentContent.fadeOut(200, function () {
                $(this).removeClass("on");
                $targetContent.fadeIn(200).addClass("on");
            });
        }
    });
    $('.box-wrap .related-list .right-list .ico.ico1,.box-wrap .related-list .right-list .ico.ico2').click(function (e) {
        e.preventDefault();
    });
})

// 기타 : 추후 개별페이지로 이동 필요시 이동
$(document).ready(function () {
    // 나의학습현황 버튼
    $(".intereste-toggle-btn").click(function () {
        $(".interests-toggle-contents").slideToggle(300);
        $(this).toggleClass("on");
    });
    $(".select-category-wrap ~ button").click(function () {
        $(".interests-toggle-contents").slideUp(300);
    });
    // 나의학습현황 tab
    $(".my-learning-status-wrap .tab").each(function () {
        $(this).click(function () {
            var tabId = $(this).data("tab");

            $(".my-learning-status-wrap .tab").removeClass("active");
            $(this).addClass("active");

            $(".my-learning-status-wrap .tab-content").hide();
            $("#" + tabId).show();
        });
    });
    // 설문 tab
    $(".paper-wrap.survey-new .tab").each(function () {
        $(this).click(function () {
            var tabId = $(this).data("tab");

            $(".paper-wrap.survey-new .tab").removeClass("active");
            $(this).addClass("active");

            $(".paper-wrap.survey-new .tab-content").hide();
            $("#" + tabId).show();
        });
    });
    //종합역량진단 1차진단 역량 탭
    $(".skills-tab-wrap .tab").each(function () {
        $(this).click(function () {
            var tabId = $(this).data("tab");

            $(".skills-tab-wrap .tab").removeClass("active");
            $(this).addClass("active");

            $(".skills-tab-wrap .tab-content").hide();
            $("#" + tabId).show();
        });
    });

    // 초기
    $(".my-learning-status-wrap .tab-content:first").show();
    $(".paper-wrap.survey-new .tab-content:first").show();
    $(".skills-tab-wrap .tab-content:first").show();

    // 저장하기 toggle
    $(".box-wrap .related-list .right-list .ico.ico1").click(function () {
        $(this).toggleClass("on");
    });
    // 통합검색 저장하기 toggle
    $(".intregrated-search-result-item .related-list .right-list .ico.ico1").click(function () {
        $(this).toggleClass("on");
    });
    // 나의학습현황 컨텐츠 filter
    $('.categoryname').on('change', function () {
        const categoryName = $(this).parent().text().trim();
        const isChecked = $(this).prop('checked');

        if (isChecked) {
            var categoryItem = $('<div>').addClass('category-item');
            var categorySpan = $('<span>').text(categoryName);
            var closeButton = $('<button>').attr('type', 'button').addClass('close');
            var closeSpan = $('<span>').addClass('hide').text('닫기');

            closeButton.append(closeSpan);
            categoryItem.append(categorySpan, closeButton);

            $('.select-category-wrap').append(categoryItem);
        } else {
            const categoryTextToRemove = $(this).parent().text().trim();
            $('.select-category-wrap div.category-item span').filter(function () {
                return $(this).text() === categoryTextToRemove;
            }).parent().remove();
        }
        updateCategoryCount();
    });
    // 나의학습현황 카테고리 아이템
    $(document).on("click", ".select-category-wrap .category-item", function () {
        $(this).toggleClass('on');
    });
    $('.interests-result-wrap').on('click', '.close', function () {
        const categoryTextToRemove = $(this).siblings('span').text();
        $('.categoryname').parent().filter(function () {
            return $(this).text().trim() === categoryTextToRemove;
        }).children('.categoryname').prop('checked', false);
        $(this).parent().remove();
        updateCategoryCount();
    });
    $('.interests-result-wrap .top-title button').on('click', function () {
        $('.select-category-wrap').empty();
        $('.categoryname').prop('checked', false);
        updateCategoryCount();
    });

    function updateCategoryCount() {
        const selectedCategoryCount = $('.select-category-wrap div.category-item').length;
        $('.interests-result-wrap .length em').text(selectedCategoryCount);
    }

    // 커뮤니티 좋아요 버튼
    $('.feed-bottom-info .like').click(function () {
        $(this).toggleClass('on');
    });
    // 커뮤니티 고민종류 선택하기
    $('.hash-txt').click(function () {
        $(this).toggleClass('on');
    });
    // 커뮤니티 댓글 열기 닫기
    $('.comment-wrap .comment-toggle').click(function () {
        const commentLists = $(this).siblings('.comment-list');
        const toggleText = $(this);

        commentLists.slideToggle(function () {
            toggleText.toggleClass('on');
            if (toggleText.hasClass('on')) {
                toggleText.text('댓글 접기');
            } else {
                toggleText.text('댓글 보기');
            }
        });
    });
    $('.comment-wrap .comment-toggle').click();

    // 나의강의실 (추후 이슈없으면 공통으로 이동)
    $('.btn-filter').on('click', function (e) {
        e.stopPropagation();
        var $filterContent = $(this).closest('.filter-wrap').find('.filter-content');
        $filterContent.toggleClass('on');

        if ($filterContent.hasClass('on')) {
            $(this).addClass('on');
        } else {
            $(this).removeClass('on');
        }
    });
    $('.filter-content').click(function (e) {
        e.stopPropagation();
    });
    $(document).click(function (event) {
        if (!$(event.target).closest(".filter-wrap").length) {
            $(".filter-wrap .filter-content").removeClass('on');
            $(this).find('.btn-filter').removeClass('on');
        }
    });
    $(".fiter-inner .button-box button:last-child").click(function () {
        $('.btn-filter').removeClass('on').addClass('apply');
    });

    // 나의강의실 필터 checkbox
    $('.tab-blue + .tab-content .filter-wrap .filter-content .checkbox').change(function () {
        $(this).toggleClass('on');
    });

    // 콘텐츠리스트 상단 자율과정/신청과정
    $('.toggle-left-text').click(function () {
        $('.toggle-center-text').removeClass('on');
        $('.toggle-right-text').removeClass('on');
        $('.toggle-right2-text').removeClass('on');
        $('.toggle-left-text').addClass('on');
    });
    $('.toggle-center-text').click(function () {
        $('.toggle-left-text').removeClass('on');
        $('.toggle-right-text').removeClass('on');
        $('.toggle-right2-text').removeClass('on');
        $('.toggle-center-text').addClass('on');
    });
    $('.toggle-right-text').click(function () {
        $('.toggle-center-text').removeClass('on');
        $('.toggle-left-text').removeClass('on');
        $('.toggle-right2-text').removeClass('on');
        $('.toggle-right-text').addClass('on');
    });
    $('.toggle-right2-text').click(function () {
        $('.toggle-center-text').removeClass('on');
        $('.toggle-left-text').removeClass('on');
        $('.toggle-right-text').removeClass('on');
        $('.toggle-right2-text').addClass('on');
    });

    // tooltip
    $('.tooltip-open').click(function () {
        var $tooltipInner = $(this).siblings('.tooltip-inner');
        var tooltipPos = $tooltipInner.data('js-tooltip-pos');
        var tooltipSize = $tooltipInner.data('js-tooltip-size');
        $tooltipInner.toggleClass('on');
        // 툴팁 위치 설정
        $tooltipInner.css(Object.assign({}, tooltipPos, tooltipSize));
    });
    $(document).click(function (event) {
        if (!$(event.target).closest(".tooltip-wrap, .bookmark-wrap").length) {
            // 영상 북마크 리스트 닫기 버튼 클릭 시
            if ($(event.target).closest(".pin-item .close").length) {
            } else {
                $(".tooltip-wrap .tooltip-inner").removeClass('on');
            }
        }
    });

    const elements = $(".introduce-table-wrap .inner.scroll-wrap");
    elements.each(function (index, element) {
        const combinedElement = $(element);
        const tableList = combinedElement.find("table");

        const tableListHeight = tableList.height();
        const declaredHeight = parseFloat(combinedElement.css("height"));

        if (tableListHeight > declaredHeight) {
            combinedElement.css("width", "101%");
        } else {
            combinedElement.css("height", "auto");
            combinedElement.css("width", "100%");
        }
    });

    $(window).on("load", function () {
        const titleWrapElements = $(".title-wrap + .introduce-table-wrap");
        titleWrapElements.each(function (index, element) {
            const introduceTableWrap = $(element);
            const innerScrollWrap = introduceTableWrap.find(".inner.scroll-wrap");
            const tableList = introduceTableWrap.find("table");
            const trCount = tableList.find("tr").length;

            if (trCount > 5) {
                const declaredHeight = parseFloat(innerScrollWrap.css("height"));
                innerScrollWrap.css("width", "101%");
                innerScrollWrap.css("height", declaredHeight + "px");
            } else {
                innerScrollWrap.css("width", "100%");
                innerScrollWrap.css("height", "auto");
            }
        });
    });

    // 학습소개 및 신청페이지 hover관련
    $(".introduce-table-wrap.generation table tr").each(function (index, element) {
        const row = $(element);
        const tds = row.find("td:not(:last-child)");
        tds.hover(function () {
            row.find("td:not(:last-child)").addClass("hover-area");
        }, function () {
            row.find("td:not(:last-child)").removeClass("hover-area");
        });
    });

    // 평가 timer fiexd
    var evaluateSolveWrap = $(".evaluate-solve-wrap > .flex-spacebetween");
    var toastPop = $(".evaluate-solve-wrap .toast-msg");

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
    // 카드뉴스 조절 (개발요청 : 주석처리)
    $(document).ready(function () {
        // function updateImageWidth() {
        // 	var imageWidth = $('.cardnews-wrap .image').width() + 200;
        // 	$('.cardnews-wrap').width(imageWidth);
        //   }

        //   // 초기 실행
        //   updateImageWidth();

        // // 화면 크기가 조절될 때 실행
        // $(window).resize(function(){
        //   updateImageWidth();
        // });
    });

    // 부서검색 tree
    var opener = $(".tree-list-wrap a.open");
    var depth = $(".tree-list-wrap li > ul > li").parent();

    depth.hide();
    opener.each(function () {
        var nestedCont = $(this).parents("li").find("a:last");
        nestedCont.addClass("end");
        $(this).click(function (e) {
            e.preventDefault();
            click(this);
        });
    });

    $(document).on('click', '.tree-list-wrap .end', function () {
        $('.tree-list-wrap .end').removeClass('on');
        $(this).addClass("on");
    });

    function click(_target) {
        var elem = _target;
        $(elem).next().slideToggle(300);
        $(elem).prev().toggleClass("close");
        $(elem).toggleClass("close");
    }

    // 과정상세 과정게시판 toggle width 조절
    var divLength = $(".notice-toggle .toggle-container div").not(".toggle-button").length;
    var containerWidth;

    if (divLength === 3) {
        containerWidth = 216;
    } else if (divLength === 2) {
        containerWidth = 148;
    } else if (divLength === 1) {
        containerWidth = 80;
    }
    $(".notice-toggle .toggle-container").width(containerWidth);

    // 나의디그리, 과정안내, 과정소개 및 신청 드롭다운
    $('.dropdown-toggle').off('click').on('click', function () {
        $(this).toggleClass('on');
        $(this).closest('.dropdown-inner').next('ul').slideToggle(200);
    });
    // 나의 디그리 : 획득한 디그리
    $(".my-degree-list-wrap").each(function (index, element) {
        var $this = $(this);
        var $courseListSwiper = $this.find('.myDegreeSwiper');
        var slideNum = $courseListSwiper.find('.swiper-slide').length;

        var options = {
            spaceBetween: 20,
            centeredSlides: false,
            slidesOffsetBefore: 0,
            loop: slideNum >= 7, // 7개 이상인 경우에만 loop 활성화
            navigation: {
                nextEl: $courseListSwiper.parents('.my-degree-list-wrap').find('.swiper-button-next')
            },
            allowSlidePrev: slideNum >= 7, // 7개 이상인 경우에만 이전 슬라이드로 이동 허용
            allowSlideNext: slideNum >= 7, // 7개 이상인 경우에만 다음 슬라이드로 이동 허용
        };

        if (slideNum >= 7) {
            options.slidesPerView = 'auto';
        } else {
            options.slidesPerView = slideNum;
            nextEl: $courseListSwiper.parents('.my-degree-list-wrap').find('.swiper-button-next').hide();
        }

        var swiper = new Swiper($courseListSwiper, options);
    });

    // 통합검색 최근검색어 toggle
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
    //통합검색 전체결과 보기,닫기 (개발요청 : 주석처리)
    // var infoSub = $('.intregrated-search-result-item .list-inner');
    // var btnMore = $('.intregrated-search-result-item .btn-more');
    // var target = $('.intregrated-search-result-item .player-list-tit');

    // if (target.length > 3) {
    //     btnMore.show();
    // } else {
    //     btnMore.hide();
    // }

    // btnMore.click(function () {
    //     infoSub.toggleClass('expanded');
    // 	if (infoSub.hasClass('expanded')) {
    // 		infoSub.css('max-height', infoSub[0].scrollHeight + 'px');
    // 		btnMore.text("접기");
    // 	} else {
    // 		infoSub.css('max-height', '104px');
    // 		btnMore.text("전체 결과 보기");
    // 	}
    // });

    //시험 관련 탭
    $(".exam-wrap .tab").each(function () {
        $(".exam-wrap .tab-content").hide();
        $(".exam-wrap .tab-content").first().show();
        $(this).click(function () {
            var tabId = $(this).data("tab");

            $(".exam-wrap .tab").removeClass("active");
            $(this).addClass("active");

            $(".exam-wrap .tab-content").hide();
            $("#" + tabId).show();
        });
    });
})


