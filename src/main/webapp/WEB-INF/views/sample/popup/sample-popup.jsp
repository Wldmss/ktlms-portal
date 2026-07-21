<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .popup-user-profile {
        margin-bottom: 24px;
    }

    .popup-user-avatar {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        background-color: var(--primary-color);
        color: #ffffff;
        font-size: 24px;
        font-weight: 700;
        margin-right: 16px;
    }

    .popup-info-table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 24px;
    }

    .popup-info-table th {
        background-color: #f8f9fa;
        color: #555555;
        text-align: left;
        padding: 12px 16px;
        border-bottom: 1px solid #eeeeee;
        width: 30%;
    }

    .popup-info-table td {
        padding: 12px 16px;
        border-bottom: 1px solid #eeeeee;
        color: #222222;
    }

    .popup-action-area {
        margin-top: 20px;
        padding-top: 16px;
        border-top: 1px solid #eeeeee;
    }
</style>

<div class="popup-wrapper">

    <div class="popup-user-profile d-flex ai-center">
        <div class="popup-user-avatar flex-center">홍</div>
        <div>
            <h4 class="font-bold" style="font-size: 18px; margin-bottom: 4px;">홍길동 과장</h4>
            <p class="font-light" style="font-size: 13px; color: #777777;">KT 인재개발원 / 미래기술교육팀</p>
        </div>
    </div>

    <table class="popup-info-table">
        <tr>
            <th class="font-medium">사원번호</th>
            <td class="font-normal">20260608</td>
        </tr>
        <tr>
            <th class="font-medium">이메일</th>
            <td class="font-normal">gildong.hong@kt.com</td>
        </tr>
        <tr>
            <th class="font-medium">연락처</th>
            <td class="font-normal">010-1234-5678</td>
        </tr>
        <tr>
            <th class="font-medium">담당 업무</th>
            <td class="font-normal">LMS 시스템 운영 및 프론트엔드 공통 아키텍처 설계</td>
        </tr>
    </table>

    <div style="background-color: #f1f3f5; padding: 12px; border-radius: 6px; font-size: 13px; color: #666666;">
        💡 위 정보는 테스트용 Mock 데이터입니다. 아래 버튼을 누르면 연동된 전역 시스템들이 반응합니다.
    </div>

    <div class="popup-action-area d-flex jc-end gap10">
        <button type="button" class="modal-btn btn-secondary" onclick="popupTestSnackbar()">스낵바 테스트</button>
        <button type="button" class="modal-btn btn-primary" onclick="popupTestAlert()">정보 수정</button>
    </div>

</div>

<script nonce="${cspNonce}">
    // 팝업 내부에서 상위 전역 함수인 showSnackbar 호출 테스트
    function popupTestSnackbar() {
        showSnackbar("⚡ 팝업창에서 보낸 스낵바 알림입니다!");
    }

    // 팝업 내부에서 상위 전역 함수인 openAlert(Confirm) 호출 및 모달 닫기 테스트
    function popupTestAlert() {
        openConfirm("사원 정보를 수정하시겠습니까?").then((isConfirm) => {
            if (isConfirm) {
                showSnackbar("✅ 성공적으로 수정되었습니다.");
                closeModal(); // 수정 완료 후 모달창을 닫는 공통 함수 호출
            }
        });
    }
</script>