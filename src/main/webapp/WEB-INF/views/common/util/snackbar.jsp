<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- global snackbar --%>
<style>
    .snackbar-container {
        position: fixed;
        bottom: -60px;
        left: 50%;
        transform: translateX(-50%);
        background-color: #333333;
        color: #ffffff;
        padding: 14px 28px;
        border-radius: 30px;
        font-size: 14px;
        font-weight: 400;
        z-index: 9999999;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        display: flex;
        align-items: center;
        gap: 10px;
        transition: bottom 0.3s ease-out, opacity 0.3s ease-out;
        opacity: 0;
        pointer-events: none;
    }

    .snackbar-container.show {
        bottom: 40px;
        opacity: 1;
    }

    .snackbar-badge {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background-color: var(--primary-color);
        display: inline-block;
    }
</style>

<div id="global-snackbar" class="snackbar-container">
    <span class="snackbar-badge"></span>
    <span id="global-snackbar-text"></span>
</div>

<script type="text/javascript">
    let snackbarTimer = null;

    /**
     * show snackbar (기본 2.5초 후 소멸, 0 주면 무한 유지)
     * @param {string} message - 노출할 알림 문구
     * @param {number} duration - 화면에 머무를 시간 (ms 단위 / 0을 주면 안 사라짐)
     */
    function showSnackbar(message, duration = 2500) {
        const $snackbar = $("#global-snackbar");

        // 진행 중인 타이머가 있었다면 초기화하여 중첩 방지
        if (snackbarTimer) {
            clearTimeout(snackbarTimer);
            snackbarTimer = null;
        }

        // 내용 먼저 세팅
        changeSnackbar(message);

        // 화면에 부드럽게 올리기
        setTimeout(() => {
            $snackbar.addClass("show");
        }, 10);

        // duration 이 0보다 클 때만 자동 소멸 타이머 가동 (0이면 계속 떠 있음)
        if (duration > 0) {
            snackbarTimer = setTimeout(() => {
                hideSnackbar();
            }, duration);
        }
    }

    /** 내용 변경
     * @param {string} newMessage - 새로 바꿀 문구
     */
    function changeSnackbar(newMessage) {
        $("#global-snackbar-text").text(newMessage);
    }

    /** 닫기 */
    function hideSnackbar() {
        const $snackbar = $("#global-snackbar");
        $snackbar.removeClass("show");

        if (snackbarTimer) {
            clearTimeout(snackbarTimer);
            snackbarTimer = null;
        }
    }
</script>