<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .footer-wrapper {
        width: 100%;
        background-color: #2b2d30; /* 신뢰감을 주는 다크 그레이 */
        color: #a6adba;
        padding: 40px;
        border-top: 1px solid #1e2022;
        font-size: 13px;
        line-height: 1.8;
        box-sizing: border-box;
    }

    .footer-inner {
        max-width: 1200px;
        margin: 0 auto;
    }

    .footer-links {
        display: flex;
        gap: 20px;
        margin-bottom: 16px;
        border-bottom: 1px solid #3d4146;
        padding-bottom: 14px;
    }

    .footer-link-item {
        color: #ced4da;
        cursor: pointer;
        transition: color 0.2s;
    }

    .footer-link-item:hover {
        color: #ffffff;
    }

    .footer-copyright {
        color: #747b85;
        margin-top: 12px;
    }
</style>

<footer class="footer-wrapper">
    <div class="footer-inner">

        <div class="footer-links font-medium">
            <span class="footer-link-item" style="color: #ff6b71 !important;" onclick="openAlert('개인정보처리방침 구역입니다.')">개인정보처리방침</span>
            <span class="footer-link-item" onclick="openAlert('이용약관 구역입니다.')">이용약관</span>
            <span class="footer-link-item" onclick="openAlert('이메일무단수집거부 안내 구역입니다.')">이메일무단수집거부</span>
            <span class="footer-link-item" onclick="showSnackbar('고객센터 연락처: 1588-XXXX')">고객센터</span>
        </div>

        <div class="footer-info font-light">
            <p>(우) 06763 서울특별시 서초구 마방로 10길 5 (양재동) 주식회사 케이티 | 대표이사: 김영섭</p>
            <p>사업자등록번호: 102-81-42945 | 통신판매업신고: 서초 제 2026-XXXX호</p>
            <p class="footer-copyright font-normal">Copyright © 2026 KT Corp. All rights reserved.</p>
        </div>

    </div>
</footer>