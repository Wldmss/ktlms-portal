$(document).ready(function() {

	// 썸네일 관련 컨텐츠 팝업 열기
	$('.larger-pop').click(function() {		// 20190220_수정
		$('body').addClass('over-hidden');
		$(this).next('.pop-wrap').fadeIn(100);

		// 제도인경우 이벤트 참여 버튼 노출
		var ele = $(this).parent();
		if ( ele.hasClass('institution') ) {
			$(this).next().find('.pop-box').addClass('event');
		}
		// 팝업 바로가기(링크) 버튼 숨김
		if ( ele.hasClass('no-link') ) {
			$(this).next().find('.pop-box').addClass('link-hide');
		}

		// 팝업별 닫기 버튼 위치
		if ( $(this).hasClass('big') ) {
			$('.wrapper').append('<div class="fixed-con group r42"><div><a class="btn-close" href="javascript:void(0);"></a></div></div>');
		} else {
			$('.wrapper').append('<div class="fixed-con"><div><a class="btn-close" href="javascript:void(0);"></a></div></div>');
		}

		$('.pop-wrap').scrollTop(0);
	});
	// 썸네일 관련 컨텐츠 팝업 닫기
	$('.wrapper').on('click', '.btn-close', function() {
		$('body').removeClass('over-hidden');
		$('.fixed-con').remove();
		$('.pop-wrap').fadeOut(0);
		$('.pop-apply-con').hide();
	});

	// 맨위로 이동 버튼
	$(document).on('click', '.btn-scrollTop', function() {
		$('html, body').animate({ scrollTop: 0 }, 400);
	});

	$(window).scroll(function() {
		var scrTop = $(window).scrollTop();
		var secTop = $('.section').offset().top;

		// main banner
		if (scrTop > 115 ) {	// 20190220_수정
			$('body').addClass('scroll');
		} else {
			$('body').removeClass('scroll');
		}

		// Quick menu
		if (scrTop > secTop) {
			$('.quick-menu').addClass('fixed');
		} else {
			$('.quick-menu').removeClass('fixed');
		}
	});

	// 모집 수강신청 팝업 닫기
	$('.btn-pop-apply-close').click(function() {
		$(this).closest('.pop-apply-con').fadeOut(300);
	});

	// 20190219_추가
	// 취득도전, 수강신청 등록 팝업
	$('.btn-pop-apply').click(function() {
		$(this).parent('.pop-box').next().fadeIn(300);
	});

});

function open_popup(popName) {
	$('body').addClass('over-hidden');
	$('.' + popName).fadeIn(300);
	$('.wrapper').append('<div class="fixed-con group r42"><div><a class="btn-close" href="javascript:void(0);"></a></div></div>');
}

function move_section(secName) {
	var posY = $('[data-scroll-section="' + secName + '"]').offset().top;
	$('html, body').animate({ scrollTop: posY - 70 }, 400);
}

// 20190220_추가
function popup_trigger(item) {
	$('#' + item).trigger('click');
}