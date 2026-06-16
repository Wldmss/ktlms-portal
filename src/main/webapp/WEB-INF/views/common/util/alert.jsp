<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- global alert box --%>
<style>
    .alert-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.4);
        z-index: 999999; /* 최상위 배치 */
        display: none; /* 제이쿼리 제어를 위해 기본 숨김 */
    }

    .alert-container {
        background-color: #ffffff;
        border-radius: 12px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        width: 360px;
        max-width: 90%;
        padding: 24px;
        animation: alertPop 0.2s ease-out;
    }

    .alert-body {
        font-size: 15px;
        line-height: 1.6;
        color: #222222;
        text-align: center;
        padding: 10px 0 20px 0;
        word-break: keep-all;
    }

    .alert-footer {
        display: flex;
        justify-content: center;
        gap: 8px;
    }

    .alert-btn {
        flex: 1;
        padding: 10px;
        border-radius: 6px;
        font-size: 14px;
        font-weight: 500;
        text-align: center;
    }

    .alert-btn-primary {
        background-color: var(--primary-color);
        color: #ffffff;
    }

    .alert-btn-primary:hover {
        background-color: var(--primary-light);
    }

    .alert-btn-secondary {
        background-color: #e9ecef;
        color: #495057;
    }

    .alert-btn-secondary:hover {
        background-color: #dee2e6;
    }

    @keyframes alertPop {
        from {
            opacity: 0;
            transform: scale(0.95);
        }
        to {
            opacity: 1;
            transform: scale(1);
        }
    }
</style>

<div id="global-alert-modal" class="alert-overlay flex-center">
    <div class="alert-container">
        <div class="alert-body">
            <p id="global-alert-message"></p>
        </div>
        <div class="alert-footer">
            <button type="button" id="global-alert-cancel-btn" class="alert-btn alert-btn-secondary"
                    style="display: none;">취소
            </button>
            <button type="button" id="global-alert-confirm-btn" class="alert-btn alert-btn-primary">확인</button>
        </div>
    </div>
</div>

<script type="text/javascript">
    // alert open
    function openAlert(message, isConfirm = false) {
        return new Promise((resolve) => {
            // \n 문자를 HTML 줄바꿈 태그(<br>)로 자동 전환
            $("#global-alert-message").html(message.replace(/\n/g, "<br>"));
            $("#global-alert-modal").css("display", "flex"); // 중앙 정렬 상태로 표시

            checkScrollLock();

            // Confirm 모드(확인/취소 둘 다 필요) 제어
            if (isConfirm) {
                $("#global-alert-cancel-btn").show();
            } else {
                $("#global-alert-cancel-btn").hide();
            }

            // 확인 버튼 이벤트
            $("#global-alert-confirm-btn").off("click").on("click", function () {
                $("#global-alert-modal").hide();
                checkScrollLock();
                resolve(true);
            });

            // 취소 버튼 이벤트
            $("#global-alert-cancel-btn").off("click").on("click", function () {
                $("#global-alert-modal").hide();
                checkScrollLock();
                resolve(false);
            });
        });
    }

    // confirm open
    function openConfirm(message) {
        return openAlert(message, true);
    }
</script>