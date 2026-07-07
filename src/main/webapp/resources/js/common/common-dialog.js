/**
 * 공통 dialog/snackbar/modal 유틸
 * (마크업은 alert.jsp / snackbar.jsp / modal.jsp 공통 include 가 제공)
 */

let snackbarTimer = null;

/**
 * 공통 알림창. 메시지는 escapeHtml 처리되고 \n 은 줄바꿈으로 표시
 * @param {string} message - 표시할 메시지
 * @param {boolean} isConfirm - true 면 취소 버튼 노출 (직접 호출보다 openConfirm 사용 권장)
 * @returns {Promise<boolean>} 확인=true, 취소=false
 * @example openAlert("저장되었습니다.").then(() => location.reload());
 */
function openAlert(message, isConfirm = false) {
    return new Promise((resolve) => {
        const safeMessage = window.Formatter && typeof window.Formatter.escapeHtml === "function"
            ? window.Formatter.escapeHtml(message)
            : String(message || "");

        $("#global-alert-message").html(safeMessage.replace(/\n/g, "<br>"));
        $("#global-alert-modal").css("display", "flex");

        checkScrollLock();

        if (isConfirm) {
            $("#global-alert-cancel-btn").show();
        } else {
            $("#global-alert-cancel-btn").hide();
        }

        $("#global-alert-confirm-btn").off("click").on("click", function () {
            $("#global-alert-modal").hide();
            checkScrollLock();
            resolve(true);
        });

        $("#global-alert-cancel-btn").off("click").on("click", function () {
            $("#global-alert-modal").hide();
            checkScrollLock();
            resolve(false);
        });
    });
}

/**
 * 확인/취소 선택창 (openAlert 의 confirm 모드)
 * @returns {Promise<boolean>} 확인=true, 취소=false
 * @example openConfirm("삭제하시겠습니까?").then(ok => { if (ok) remove(); });
 */
function openConfirm(message) {
    return openAlert(message, true);
}

/**
 * 스낵바(하단 토스트) 표시. 연속 호출 시 이전 타이머는 초기화
 * @param {string} message - 표시할 메시지
 * @param {number} duration - 유지 시간(ms). 0 이하면 자동으로 사라지지 않음 (hideSnackbar 로 직접 닫기)
 * @example showSnackbar("저장되었습니다.");
 */
function showSnackbar(message, duration = 2500) {
    const $snackbar = $("#global-snackbar");

    if (snackbarTimer) {
        clearTimeout(snackbarTimer);
        snackbarTimer = null;
    }

    changeSnackbar(message);

    setTimeout(() => {
        $snackbar.addClass("show");
    }, 10);

    if (duration > 0) {
        snackbarTimer = setTimeout(() => {
            hideSnackbar();
        }, duration);
    }
}

/**
 * 표시 중인 스낵바의 문구만 교체 (진행 상태 갱신용)
 */
function changeSnackbar(newMessage) {
    $("#global-snackbar-text").text(newMessage);
}

/**
 * 스낵바 즉시 닫기 (duration=0 으로 띄운 스낵바를 닫을 때 사용)
 */
function hideSnackbar() {
    const $snackbar = $("#global-snackbar");
    $snackbar.removeClass("show");

    if (snackbarTimer) {
        clearTimeout(snackbarTimer);
        snackbarTimer = null;
    }
}

/**
 * 공통 모달 열기. url 을 POST 로드해서 모달 본문에 삽입 (컨텍스트 패스 자동 결합)
 * @param {string} title - 모달 제목
 * @param {string} url - 본문으로 로드할 화면 URL (컨트롤러가 fragment JSP 반환)
 * @param {Object} params - 로드 시 전달할 파라미터
 * @example openModal("상세보기", "/popup/sampleDetail", {id: 3});
 */
function openModal(title, url, params = {}) {
    $("#global-modal-title").text(title);
    $("#global-modal-content-body").html("");

    if ($("#global-loading-overlay").length) {
        $("#global-loading-overlay").show();
    }

    $("#global-modal-content-body").load(resolveCommonUrl(url), params, function (response, status) {
        if ($("#global-loading-overlay").length) {
            $("#global-loading-overlay").hide();
        }

        if (status === "error") {
            openAlert("화면을 불러오는 중 오류가 발생했습니다.");
            return;
        }

        $("#global-content-modal").css("display", "flex");
        checkScrollLock();
    });
}

/**
 * 공통 모달 닫기 (본문 내용도 비움)
 */
function closeModal() {
    $("#global-content-modal").hide();
    $("#global-modal-content-body").html("");
    checkScrollLock();
}

/**
 * URL 에 컨텍스트 패스 자동 결합 (절대 URL/이미 결합된 URL 은 그대로)
 */
function resolveCommonUrl(url) {
    if (!url) return "";
    if (/^(https?:)?\/\//.test(url)) return url;

    const contextPath = window._contextPath || "";
    return url.startsWith(contextPath) ? url : contextPath + (url.startsWith("/") ? url : "/" + url);
}

window.openAlert = openAlert;
window.openConfirm = openConfirm;
window.showSnackbar = showSnackbar;
window.changeSnackbar = changeSnackbar;
window.hideSnackbar = hideSnackbar;
window.openModal = openModal;
window.closeModal = closeModal;
