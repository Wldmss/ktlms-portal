/**
 * 공통 유틸리티
 */

/* body scroll lock 처리 */
function checkScrollLock() {
    const visiblePopups = $(".alert-overlay, .modal-overlay").filter(function () {
        return $(this).css("display") !== "none";
    }).length;

    // 눈에 보이는 팝업이 진짜 0개일 때만 body 잠금을 해제
    if (visiblePopups === 0) {
        $("body").css("overflow", "");
    } else {
        $("body").css("overflow", "hidden");
    }
}