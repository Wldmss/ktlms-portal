<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .gnb-header {
        width: 100%;
        background-color: #ffffff;
        border-bottom: 1px solid #e9ecef;
        padding: 0 40px;
        position: sticky; /* 스크롤을 내려도 상단에 고정되는 최신 트렌드 */
        top: 0;
        z-index: 9999;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        box-sizing: border-box;
        /*min-width: 440px;*/
    }

    .gnb-top {
        display: flex;
        justify-content: space-between;
        gap: 10px;
        margin-top: 10px;
    }

    .gnb-logo {
        font-size: 22px;
        font-weight: 700;
        color: var(--primary-color, #ff6b71); /* 지정된 브랜드 메인컬러, 없으면 핑크레드 */
        cursor: pointer;
    }

    .gnb-menu-list {
        display: flex;
        list-style: none;
        gap: 35px;
        margin: 0;
        padding: 0;
    }

    .gnb-menu-item {
        font-size: 16px;
        color: #444444;
        position: relative;
        padding: 23px 0;
        cursor: pointer;
        transition: color 0.2s ease;
    }

    /* 메뉴 마우스 호버 시 포인트 색상 변환 및 하단 언더라인 애니메이션 */
    .gnb-menu-item:hover {
        color: var(--primary-color, #ff6b71);
        font-weight: 500;
    }

    .gnb-menu-item:hover::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        height: 3px;
        background-color: var(--primary-color, #ff6b71);
        border-radius: 3px 3px 0 0;
    }

    .gnb-user-info {
        display: flex;
        font-size: 14px;
        color: #666666;
        gap: 15px;
    }

    .gnb-user-info span {
        margin: auto;
    }

    .gnb-logout-btn {
        padding: 6px 12px;
        border-radius: 4px;
        font-size: 13px;
        color: #000000;
        cursor: pointer;
        transition: background-color 0.2s;
        border: 1px solid #e9ecef;
    }

    .gnb-logout-btn:hover {
        color: var(--primary-color);
        border-color: var(--primary-color);
    }
</style>

<header class="gnb-header d-flex ai-center" style="justify-content: space-between;">

    <div class="gnb-top">
        <div class="gnb-logo" onclick="location.href=window._contextPath + window._mainUrl">
            KT LMS <span class="font-light" style="color:#999; font-size:14px; margin-left:4px;">Edu Portal</span>
        </div>

        <div class="gnb-user-info d-flex ai-center" style="gap: 15px;">
            <span class="font-medium"><strong style="color:#111;">홍길동</strong> 과장님</span>
            <button type="button" class="gnb-logout-btn font-bold" onclick="handleGnbLogout()">로그아웃</button>
        </div>
    </div>
    <nav>
        <ul class="gnb-menu-list">
            <li class="gnb-menu-item font-normal" onclick="showSnackbar('나의 강의실로 이동합니다.')">나의 강의실</li>
            <li class="gnb-menu-item font-normal" onclick="showSnackbar('과정 신청 페이지로 이동합니다.')">과정신청</li>
            <li class="gnb-menu-item font-normal" onclick="showSnackbar('학습지원센터로 이동합니다.')">학습지원센터</li>
            <li class="gnb-menu-item font-normal" onclick="showSnackbar('시험 메인 화면으로 이동합니다.')">시험평가</li>
        </ul>
    </nav>

    <button type="button" class="float-btn" onclick="window.scrollTo({top: 0, behavior: 'smooth'})" title="위로 가기">
        △
    </button>
</header>

<script nonce="${cspNonce}">
    function handleGnbLogout() {
        if (typeof openConfirm === 'function') {
            openConfirm("로그아웃 하시겠습니까?").then(function (isConfirm) {
                if (isConfirm) {
                    showSnackbar("🔒 안전하게 로그아웃 중입니다...");
                    setTimeout(doLogout, 1200);
                }
            });
        } else {
            if (confirm("로그아웃 하시겠습니까?")) {
                doLogout();
            }
        }
    }

    function doLogout() {
        postAjax("/auth/logout").then(res => {
            location.href = window._contextPath + "/";
        });
    }
</script>