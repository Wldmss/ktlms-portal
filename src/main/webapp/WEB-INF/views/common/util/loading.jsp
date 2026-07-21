<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- global loading bar --%>
<style>
    /* 화면 전체를 어둡게 덮는 레이어 */
    #global-loading-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: var(--bg-overlay);
        z-index: 99999; /* 최상단 배치 */
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        transition: background-color 0.15s ease;
    }

    /* 투명 배경 */
    #global-loading-overlay.no-bg {
        background-color: transparent !important;
    }

    /* 스피너 */
    .global-spinner {
        width: 50px;
        height: 50px;
        border: 5px solid rgba(0, 0, 0, 0.1);
        border-radius: 50%;
        border-top-color: var(--primary-color);
        animation: spin 1s ease-in-out infinite;
    }

    /* 투명 배경일 때 스피너 선명도 보정 */
    #global-loading-overlay.no-bg .global-spinner {
        border: 5px solid rgba(0, 0, 0, 0.15);
        border-top-color: var(--primary-color);
    }

    /* 안내 문구 스타일 */
    .global-loading-text {
        color: #ffffff;
        margin-top: 15px;
        font-size: 16px;
        font-weight: 500;
        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
    }

    /* 회전 애니메이션 정의 */
    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }
</style>

<div id="global-loading-overlay" style="display: none;">
    <div class="global-spinner"></div>
    <p id="global-loading-text" class="global-loading-text">잠시만 기다려 주세요...</p>
</div>

<script nonce="${cspNonce}">
    // 무한 로딩 방지용 타이머 변수
    let loadingTimeoutId = null;

    $(document).ready(function () {
        // ajax show loading bar
        $(document).ajaxStart(function () {
            if (typeof showLoading === "function") {
                showLoading({noBg: true});
            }
        });

        // ajax hide loading bar
        $(document).ajaxComplete(function () {
            if (typeof hideLoading === "function") {
                hideLoading();
            }
        });
    });

    /**
     * 전역 로딩바 제어 함수
     * @param {Object} options
     * @param {boolean} options.noBg - 투명 배경 여부 (기본값: false)
     * @param {string} options.text - 표시할 안내 문구 (기본값: "잠시만 기다려 주세요...")
     * @param {number} options.timeout - 자동 꺼짐 시간 (ms 단위, 설정 안 하면 작동 안 함)
     */
    /**
     * 전역 로딩바 제어 함수
     */
    function showLoading(options = {}) {
        if (loadingTimeoutId) clearTimeout(loadingTimeoutId);

        const $overlay = $("#global-loading-overlay");
        const $text = $("#global-loading-text");

        // 문구 커스텀
        const loadingText = options.text || "잠시만 기다려 주세요...";
        $text.text(loadingText);

        // 배경 및 스크롤
        if (options.noBg === true) {
            $overlay.addClass("no-bg");
            $text.hide();

            checkScrollLock();
        } else {
            $overlay.removeClass("no-bg");
            $text.show();

            checkScrollLock();
        }

        $overlay.css("display", "flex");

        // 타임아웃 처리
        if (options.timeout && typeof options.timeout === "number") {
            loadingTimeoutId = setTimeout(function () {
                hideLoading();
            }, options.timeout);
        }
    }

    // hide loading bar
    function hideLoading() {
        if (loadingTimeoutId) clearTimeout(loadingTimeoutId);

        $("#global-loading-overlay").hide().removeClass("no-bg");
        checkScrollLock();
    }
</script>