<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/layout/common-top.jsp" %>
<style>
    .group-box {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        margin: 20px 0;
        padding: 15px;
        background: #ffffff;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
    }

    .btn-test {
        padding: 10px 16px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-weight: bold;
        color: white;
    }
</style>

<div>
    <div><h3>버튼 모음집</h3></div>

    <div class="group-box">
        <button class="btn-test" style="background-color: #007bff;" onclick="openAlert('알림창입니다.');">🚨 openAlert (확인전용)
        </button>
        <button class="btn-test" style="background-color: #ec9f46;" onclick="testOpenAlertCancel();">❓ openAlert
            (확인/취소)
        </button>
        <button class="btn-test" style="background-color: #17a2b8;"
                onclick="openModal('팝업창입니다.','popup/sampleDetail');">🔲 openModal 호출
        </button>
        <button class="btn-test" style="background-color: #28a745;" onclick="showSnackbar('snackbar 입니다.');">💬
            showSnackbar 호출
        </button>
        <button class="btn-test" style="background-color: #dd00ff;" onclick="testLoadingTimer()">⏳ 로딩바 (3초 타이머형)
        </button>
        <button class="btn-test" style="background-color: #ffc400;" onclick="testLoadingAjax()">🌐 로딩바 (Ajax 실전형)
        </button>
    </div>

    <div><h3>date picker</h3></div>

    <div class="group-box" style="flex-direction: column;">
        <div>
            <h3>1. 하루만 선택 (Single)</h3>
            <label class="common-date">
                <input type="text" class="date-single" placeholder="날짜 선택">
            </label>
        </div>


        <div>
            <h3>2. 기간 선택 (Range)</h3>
            <label class="common-date" style="width: 210px;">
                <input type="text" class="date-range" placeholder="시작일 ~ 종료일 선택">
            </label></div>

        <div>
            <h3>3. 시작-종료 분리형 선택 (Split)</h3>
            <div class="date-split">
                <label class="common-date">
                    <input type="text" class="date-start" placeholder="시작일">
                </label>
                <span>~</span>
                <label class="common-date">
                    <input type="text" class="date-end" placeholder="종료일">
                </label>
            </div>
        </div>

        <div>
            <h3>4. 시작-종료 분리형 선택 (Split2)</h3>
            <div class="date-split">
                <label class="common-date">
                    <input type="text" class="date-start" placeholder="시작일">
                </label>
                <span>~</span>
                <label class="common-date">
                    <input type="text" class="date-end" placeholder="종료일">
                </label>
            </div>
        </div>
    </div>
    <%@include file="/WEB-INF/views/sample/sampleCommon.jsp" %>
    <%@include file="/WEB-INF/views/sample/sampleSub.jsp" %>
</div>

<script nonce="${cspNonce}">
    $(document).ready(function () {
        // datepicker 초기화
        CommonDatepicker.init('.date-single', 'single');
        CommonDatepicker.init('.date-range', 'range');
        CommonDatepicker.init('.date-split', 'split');
    });

    /*확인/취소 alert*/
    function testOpenAlertCancel() {
        openConfirm("알림창입니다.\n확인, 취소 누르기")
            .then((result) => {
                if (result) {
                    showSnackbar('확인을 눌렀습니다.');
                } else {
                    showSnackbar('취소를 눌렀습니다.');
                }
            });
    }

    // 로딩바
    function testLoadingTimer() {
        showLoading();

        setTimeout(function () {
            hideLoading();
        }, 3000);
    }

    // ajax 로딩바
    function testLoadingAjax() {
        postAjax("/ajaxTest", {id: "test", pw: "1234"}).then(res => {
            if (res.success) {
                showSnackbar('성공했습니다.')
            } else {
                openAlert('실패했습니다.')
            }
        });
    }
</script>

<%@ include file="/WEB-INF/views/common/layout/common-bottom.jsp" %>