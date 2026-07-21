// 기존플레이어, Broadcast 없애기
if (typeof player !== "undefined" && player) {
    player.dispose();   // videojs 인스턴스 제거
    player = null;
}

if (typeof channel !== "undefined" && channel) {
    channel.close();   // 기존 BroadcastChannel 닫기
    channel = null;
}

var player = videojs("myvideo", {
    controls: true,
    playbackRates: [0.25, 0.5, 0.75, 1, 1.25, 1.5, 1.75, 2],
    autoplay: false
});


var checkFlag = "Y";

var channel = new BroadcastChannel('micro_channel');
var isOtherTabPlaying = false;
var waitingForStatus = false;

//재생 시작 시 알림
function startVideo() {
    channel.postMessage({action: "statusRequest"});
    waitingForStatus = true;
}

//메시지 수신
$(channel).on("message", function (event) {
    if (event.originalEvent.data.action === "play") {
        player.pause();
    }
});

//메시지 수신 핸들러 따로 정의
function handleMessage(event) {
    if (event.data.action === "play") {
        isOtherTabPlaying = true;
    }

    if (event.data.action === "stop") {
        isOtherTabPlaying = false;
    }

    if (waitingForStatus) {
        waitingForStatus = false;
        if (isOtherTabPlaying) {
            var proceed = confirm("학습 중인 VOD가 있으니 기존 학습을 종료시키겠습니까?");
            if (!proceed) {
                player.pause();
                return; // 사용자가 취소하면 아무 동작 안 함
            } else {
                // 다른 탭 멈추기
                channel.postMessage({action: "forceStop"});
                // 현재 탭 재생 시작
                player.play();
            }
        } else {
            // 현재 탭 재생 시작
            player.play();

        }

        // 현재 탭 재생 시작 알림
        var message = {action: 'play'};
        channel.postMessage(message);
    }


    if (event.data.action === "forceStop") {
        // 다른 탭에서 강제 정지 요청
        player.pause();
        isOtherTabPlaying = false;
    }

    if (event.data.action === "statusRequest") {
        // 현재 탭이 재생중인지 상태
        if (!player.paused()) {
            channel.postMessage({action: "play"});
        } else {
            channel.postMessage({action: "stop"});
        }
    }
}

// BroadcastChannel 이벤트 연결
channel.onmessage = handleMessage;

//
var isPipActive = false;
var originalVideoTop = null;
var originalVideoLeft = null;

var isDragging = false;
var dragOffsetX = 0;
var dragOffsetY = 0;
var isUnauthorized = false; // 401 상태 여부 : false = 정상상태, true = 여러탭상태

$(window).scroll(function () {
    var scrollTop = $(this).scrollTop();
    var windowHeight = $(window).height();
    var $videoWrapper = $('.video-wrapper');
    var $placeholder = $('.video-placeholder');

    if ($videoWrapper.length === 0) return;

    if (originalVideoTop === null) {
        var offset = $videoWrapper.offset();
        originalVideoTop = offset.top;
        originalVideoLeft = offset.left;
    }

    var videoHeight = $videoWrapper.outerHeight();
    var videoBottom = originalVideoTop + videoHeight;

    var triggerPoint = originalVideoTop + videoHeight * 0.9;
    var isVideoVisible = (videoBottom > scrollTop) && (originalVideoTop < scrollTop + windowHeight);

    // PIP 켜기
    if (!isUnauthorized && !isPipActive && scrollTop > triggerPoint && !isVideoVisible) {
        $placeholder.height(videoHeight);
        $videoWrapper.addClass('pip');

        // 현재 화면 위치를 기준으로 top/left 초기화
        var offset = $videoWrapper.offset();
        $videoWrapper.css({
            top: 'auto',
            left: 'auto',
            right: '20px',  // 오른쪽 여백
            bottom: '100px'  // 아래쪽 여백
        });

        isPipActive = true;
    }

    // PIP 끄기
    if (isPipActive && isVideoVisible) {
        $videoWrapper.removeClass('pip');
        $placeholder.height(0);

        // CSS 초기화
        $videoWrapper.css({
            top: '',
            left: '',
            right: '',
            bottom: ''
        });

        isPipActive = false;

        // true 상태면 원래 위치에 다시 overlay 그림
        if (isUnauthorized) {
            drawVideoOverlay();
        }

    }
});

// 드래그 시작
$(document).on('mousedown', '.video-wrapper.pip', function (e) {
    isDragging = true;
    var $pip = $(this);

    // 현재 top/left 기준으로 offset 계산
    dragOffsetX = e.pageX - parseInt($pip.css('left'), 10);
    dragOffsetY = e.pageY - parseInt($pip.css('top'), 10);

    e.preventDefault();
});

// 드래그 ing
$(document).on('mousemove', function (e) {
    if (isDragging) {
        var $pip = $('.video-wrapper.pip');
        var left = e.pageX - dragOffsetX;
        var top = e.pageY - dragOffsetY;

        $pip.css({left: left + 'px', top: top + 'px', right: 'auto', bottom: 'auto'});
    }
});

//드래그 종료
$(document).on('mouseup', function () {
    isDragging = false;
});


function isMobile() {
    var result = false;
    if (navigator.userAgent.indexOf('Mobile') > -1) {
        result = true;
    }
    return result;
}

function moveFrame(time) {
    if ($("#playType").val() != "STREAM") {
        player.load();
    }
    player.pause();
    player.currentTime(time);
}

var playerTimer;
$(document).ready(function () {
    clearInterval(playerTimer);
    player.bigPlayButton.on("click", function () {
        if (this.hasClass("vjs-playing")) {
            player.pause();
        } else {
            player.play();
        }
    });

    //맨 처음으로
    $(".backward").click(function () {
        player.currentTime(0);
    });

    //맨 끝으로
    $(".forward").click(function () {
        player.currentTime($("input[name=playTime]").val());
    });

    $('.video-wrapper').click(function () {
        $('.video-play').show();
    });

    /* 시청 시작 (START) */
    player.on("play", function () {
        playerTimer = setInterval(checkStatus, 30000); 	//30초
        this.bigPlayButton.addClass('vjs-playing');
        this.bigPlayButton.addClass('vjs-user-inactive');
        this.bigPlayButton.removeClass('vjs-paused');
        $(".video-beforeafter").addClass('on');

        var currentPlayTime = player.currentTime();
        var requestData = {
            microId: $("input[name=microId]").val(),
            eventType: "START",
            eventFrameTime: currentPlayTime
        };
        insertMicroLog(requestData);

        startVideo();
    });

    /* 시청 끝 (END) */
    player.on("ended", function () {
        var currentEndTime = player.currentTime();
        var requestData = {
            microId: $("input[name=microId]").val(),
            eventType: "END",
            eventFrameTime: currentEndTime
        };
        insertMicroLog(requestData);

        setTimeout(function () {
            changeStatus();
        }, 5000);

        clearInterval(playerTimer);
        channel.postMessage({action: "stop"});
    });

    /* 시청 도중 종료(PAUSE)*/
    /* 영상이 끝났을때는 중단처리가 아님 */
    player.on("pause", function () {
        this.bigPlayButton.addClass('vjs-paused');
        this.bigPlayButton.removeClass('vjs-playing');
        this.bigPlayButton.removeClass('vjs-user-inactive');
        var currentPauseTime = player.currentTime();
        var requestData = {
            microId: $("input[name=microId]").val(),
            eventType: "PAUSE",
            eventFrameTime: currentPauseTime
        };
        insertMicroLog(requestData);
        clearInterval(playerTimer);
        channel.postMessage({action: "stop"});
    });

    /* 시청 중 빠져나감(EXIT)*/
    player.on("exit", function () {
        var currentExitTime = player.currentTime();
        var requestData = {
            microId: $("input[name=microId]").val(),
            eventType: "EXIT",
            eventFrameTime: currentExitTime
        };
        insertMicroLog(requestData);
        clearInterval(playerTimer);
        channel.postMessage({action: "stop"});
    });

    // 재생바 이동금지 조건
    if ($("input[name=controlUnableYn]").val() == "Y") {
        var supposedCurrentTime = 0;
        player.on('timeupdate', function () {
            if (!player.seeking()) {
                supposedCurrentTime = player.currentTime();
            }
        });

        player.on('seeking', function () {
            var delta = player.currentTime() - supposedCurrentTime;
            var realTime = $("input[name=realEduTime]").val();
            if ((Math.abs(delta) > 0.01 && checkFlag == "N") ||
                Math.abs(delta) > 0.01 && checkFlag == "Y" && realTime == 0) {
                player.currentTime(supposedCurrentTime);
            }
        });

        if ($("input[name=controlUnableYn]").val() == "Y") {
            player.on('seeked', function () {
                // 실제 학습시간이 있는 경우
                if ($("input[name=realEduTime]").val() > 0 && checkFlag == "Y") {

                } else {
                    alert("재생바 이동이 불가한 강좌입니다.");
                }
                checkFlag = "N";
            });
        }
        player.on('ended', function () {
            supposedCurrentTime = 0;
        });
    }
    channel.postMessage({action: "statusRequest"});

});

$(document).ready(function () {
    $(' .vjs-volume-panel.vjs-control.vjs-volume-panel-horizontal').mouseenter(function () {
        $('.video-time').hide();
    });

    $(' .vjs-volume-panel.vjs-control.vjs-volume-panel-horizontal').mouseleave(function () {
        $('.video-time').show();
    });


    /* 2023-05-22 : 플레이어 일시정지 버튼 메시지 관련 */
    $(document).on("click", "button.vjs-paused", function () { // 재생
        $('.puase-txt').removeClass('on');
    });

    $(document).on("click", "button.vjs-playing", function () { //일시정지
        $('.puase-txt').addClass('on');
    });
});

var swiper = new Swiper('.swiper-wrap', {
    speed: 1000,
    slidesPerView: 'auto',
    navigation: {
        nextEl: '.next-wrap',
        prevEl: '.prev-wrap',
    },
});

//재생중일때 ing 이벤트
function checkStatus() {
    if (!player) {
        clearInterval(playerTimer);
    } else {
        if (!player.paused()) { // 재생중인 상태
            callBeforeUnload('ING');
        } else {
            clearInterval(playerTimer);
        }
    }
}

function drawVideoOverlay() {
    var $videoWrapper = $('.video-wrapper');
    var offset = $videoWrapper.offset();
    var width = $videoWrapper.outerWidth();
    var height = $videoWrapper.outerHeight();

    $('#video-overlay').remove(); // 중복 방지

    $('<div id="video-overlay"></div>')
        .css({
            width: width + 'px',
            height: height + 'px',
            backgroundColor: 'black',
            position: 'absolute',
            top: offset.top + 'px',
            left: offset.left + 'px',
            zIndex: 9,
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            color: 'white',
            fontSize: '17px',
            textAlign: 'center',
            padding: '10px',
            lineHeight: '1.3'
        })
        .html('중복 로그인한 브라우저/앱에서는 영상을 재생할 수 없습니다.<br>재 로그인 하여 주십시요.')
        .appendTo('body');
}

//overay 화면도 동영상크기에 맞게 수정
$(window).on('resize orientationchange', function () {
    if (isUnauthorized) {
        drawVideoOverlay();
    }
});
;
