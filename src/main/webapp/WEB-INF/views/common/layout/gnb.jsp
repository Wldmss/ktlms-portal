<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .gnb-header {
        width: 100%;
        /*height: 70px;*/
        background-color: #ffffff;
        border-bottom: 1px solid #e9ecef;
        padding: 0 40px;
        position: sticky; /* 스크롤을 내려도 상단에 고정되는 최신 트렌드 */
        top: 0;
        z-index: 9999;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        box-sizing: border-box;
    }

    .gnb-logo {
        font-size: 22px;
        font-weight: 700; /* 전역 두께 시스템 활용 */
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
        font-size: 14px;
        color: #666666;
        gap: 15px;
    }

    .gnb-logout-btn {
        background-color: #f1f3f5;
        border: none;
        padding: 6px 12px;
        border-radius: 4px;
        font-size: 13px;
        color: #495057;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .gnb-logout-btn:hover {
        background-color: #e9ecef;
    }
</style>

<body class="font-normal"><%--body 시작--%>
<div class="main-wrapper"><%--content 시작--%>
    <header class="gnb-header d-flex jc-between ai-center">

        <div class="gnb-logo" onclick="location.href=window._contextPath + '/sample'">
            KT LMS <span class="font-light" style="color:#999; font-size:14px; margin-left:4px;">Edu Portal</span>
        </div>

        <nav>
            <ul class="gnb-menu-list">
                <li class="gnb-menu-item font-normal" onclick="showSnackbar('나의 강의실로 이동합니다.')">나의 강의실</li>
                <li class="gnb-menu-item font-normal" onclick="showSnackbar('과정 신청 페이지로 이동합니다.')">과정신청</li>
                <li class="gnb-menu-item font-normal" onclick="showSnackbar('학습지원센터로 이동합니다.')">학습지원센터</li>
                <li class="gnb-menu-item font-normal" onclick="showSnackbar('시험 메인 화면으로 이동합니다.')">시험평가</li>
            </ul>
        </nav>

        <div class="gnb-user-info d-flex ai-center">
            <span class="font-medium"><strong style="color:#111;">홍길동</strong> 과장님</span>
            <button type="button" class="gnb-logout-btn font-normal" onclick="handleGnbLogout()">로그아웃</button>
        </div>

    </header>

    <script type="text/javascript">
        function handleGnbLogout() {
            // 우리가 커스텀해 둔 전역 openAlert(메시지, isConfirm) 기믹 작동!
            if (typeof openAlert === 'function') {
                openConfirm("정말로 로그아웃 하시겠습니까?").then(function (isConfirm) {
                    if (isConfirm) {
                        showSnackbar("🔒 안전하게 로그아웃 중입니다...");
                        setTimeout(function () {
                            location.href = window._contextPath + "/logout";
                        }, 1200);
                    }
                });
            } else {
                if (confirm("정말로 로그아웃 하시겠습니까?")) {
                    location.href = window._contextPath + "/logout";
                }
            }
        }
    </script>