<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- global modal box --%>
<style>
    .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        z-index: 999998; /* alert 보다는 한 단계 아래 배치 */
        display: none;
    }

    .modal-container {
        background-color: #ffffff;
        border-radius: 12px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
        width: 640px;
        max-width: 90%;
        max-height: 85vh;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        animation: modalShow 0.2s ease-out;
    }

    .modal-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        border-bottom: 1px solid #eeeeee;
    }

    .modal-title {
        font-size: 18px;
        font-weight: 700;
        color: #111111;
    }

    .modal-close-x {
        font-size: 26px;
        color: #aaaaaa;
        line-height: 1;
    }

    .modal-close-x:hover {
        color: #222222;
    }

    .modal-body {
        padding: 20px;
        overflow-y: auto; /* 본문 내용 길어지면 팝업창 내에서만 스크롤 */
        font-size: 14px;
    }

    @keyframes modalShow {
        from {
            opacity: 0;
            transform: translateY(10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
</style>

<div id="global-content-modal" class="modal-overlay flex-center">
    <div class="modal-container">
        <div class="modal-header">
            <h3 id="global-modal-title" class="modal-title"></h3>
            <button type="button" class="modal-close-x" onclick="closeModal()">&times;</button>
        </div>
        <div id="global-modal-content-body" class="modal-body">
        </div>
    </div>
</div>

<script type="text/javascript">
    function openModal(title, url, params = {}) {
        $("#global-modal-title").text(title);
        $("#global-modal-content-body").html("");

        // 로딩바 작동 (전역 로딩바 컴포넌트 연동)
        if ($("#global-loading-overlay").length) {
            $("#global-loading-overlay").show();
        }

        // 제이쿼리 load 기능을 활용해 다른 JSP 껍데기를 다이렉트로 인클루드 시킵니다.
        $("#global-modal-content-body").load(window._contextPath + url, params, function (response, status, xhr) {
            if ($("#global-loading-overlay").length) {
                $("#global-loading-overlay").hide(); // 로딩 완료 시 끄기
            }

            if (status === "error") {
                openAlert("화면을 불러오는 중 오류가 발생했습니다.");
            } else {
                // 모달 오픈
                $("#global-content-modal").css("display", "flex");
                checkScrollLock();
            }
        });
    }

    function closeModal() {
        $("#global-content-modal").hide();
        $("#global-modal-content-body").html("");
        checkScrollLock();
    }
</script>