var playerTimer;
$(document).ready(function () {
    clearInterval(playerTimer);
    var microId = $("input[name=microId]").val();
    var player = document.getElementById("myvideo");

    var checkFlag = "Y";
    /* 플레이어 시작 (START) */
    player.onplay = function () {
        playerTimer = setInterval(checkStatus, 30000); 	//30초
        $(".videoDim").css("display", "none");
        var currentPlayTime = player.currentTime;
        var requestData = {
            microId: microId,
            eventType: "START",
            eventFrameTime: currentPlayTime
        };
        insertMicroLog(requestData);
    };

    /* 플레이어 끝(END) */
    player.onended = function () {
        var currentEndTime = player.currentTime;
        var requestData = {
            microId: microId,
            eventType: "END",
            eventFrameTime: currentEndTime
        };
        insertMicroLog(requestData);

        setTimeout(function () {
            changeStatus();
        }, 5000);

        clearInterval(playerTimer);
    };

    /* 플레이어 중지(PAUSE)*/
    player.onpause = function () {
        var currentPauseTime = player.currentTime;
        var requestData = {
            microId: microId,
            eventType: "PAUSE",
            eventFrameTime: currentPauseTime
        };
        insertMicroLog(requestData);
        clearInterval(playerTimer);
    };

    window.addEventListener("pagehide", function () {
        var currentExitTime = player.currentTime;
        var requestData = {
            microId: microId,
            eventType: "EXIT",
            eventFrameTime: currentExitTime
        };
        $.ajax({
            url: preUrl + "/insertEduLogAjax.do",
            type: 'POST',
            dataType: "json",
            data: JSON.stringify(requestData),
            error: function (e) {
                //console.log(e);
            },
            success: function (data) {
                return;
            }
        });
    });

    /* 재생바 이동금지 조건 */
    if ($("input[name=controlUnableYn]").val() == "Y") {
        var supposedCurrentTime = 0;
        var player = document.getElementById("myvideo");

        player.addEventListener('timeupdate', function () {
            if (!player.seeking) {
                supposedCurrentTime = player.currentTime;
            }
        });

        player.addEventListener('seeking', function () {
            var delta = player.currentTime - supposedCurrentTime;
            var realTime = $("input[name=realEduTime]").val();
            if ((Math.abs(delta) > 0.01 && checkFlag == "N") ||
                Math.abs(delta) > 0.01 && checkFlag == "Y" && realTime == 0) {
                player.currentTime = supposedCurrentTime;
            }
        });

        if ($("input[name=controlUnableYn]").val() == "Y") {
            player.addEventListener('seeked', function () {
                // 실제 학습시간이 있는 경우
                if ($("input[name=realEduTime]").val() > 0 && checkFlag == "Y") {

                } else {
                    layerAlert("재생바 이동이 불가한 강좌입니다.");
                }
                checkFlag = "N";
            });
        }

        player.addEventListener('ended', function () {
            supposedCurrentTime = 0;
        });
    }
});

function moveFrame(time) {
    var player = document.getElementById("myvideo");
    player.pause();
    player.currentTime = time;
}

function moveFrameOption(time) {
    var player = document.getElementById("myvideo");
    player.load();
    player.pause();
    player.currentTime = time;
}

//재생중일때 ing 이벤트
function checkStatus() {
    if (!player) {
        clearInterval(playerTimer);
    } else {
        if (!player.paused) { // 재생중인 상태
            callBeforeUnload('ING');
        } else {
            clearInterval(playerTimer);
        }
    }
}
