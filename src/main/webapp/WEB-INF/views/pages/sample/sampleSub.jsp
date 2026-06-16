<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<style>
    .guide-container {
        max-width: 100%; /* 컨테이너 너비도 화면에 맞게 확장 가능하도록 설정 */
        margin: 0 2rem;
        background: #fff;
        padding: 2.5rem;
        border-radius: 12px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
    }

    /* 🎯 테이블 스타일 업그레이드 */
    .custom-guide-table {
        width: 100% !important; /* 너비 꽉 차게 설정 */
        border-collapse: separate;
        border-spacing: 0;
        border-radius: 8px;
        overflow: hidden;
        border: 1px solid #eef2f5 !important;
    }

    .custom-guide-table thead th {
        background-color: #1e293b !important; /* 세련된 딥 다크 네이비 */
        color: #ffffff !important;
        padding: 1rem 1.25rem !important;
        font-size: 0.95rem;
        font-weight: 600;
        border: none !important;
        text-align: left !important; /* 🎯 헤더 좌측 정렬 */
    }

    .custom-guide-table tbody td {
        padding: 1rem 1.25rem !important;
        font-size: 0.9rem;
        border-bottom: 1px solid #eef2f5 !important;
        text-align: left !important; /* 🎯 본문 내용 좌측 정렬 */
        color: #334155;
    }

    /* 마우스 올렸을 때 부드러운 효과 */
    .custom-guide-table tbody tr {
        transition: background-color 0.2s ease;
    }

    .custom-guide-table tbody tr:hover {
        background-color: #f8fafc !important;
    }

    /* 링크 스타일 고도화 */
    .link-url {
        color: #0ea5e9;
        font-weight: 500;
        transition: color 0.15s ease;
        display: inline-block;
    }

    .link-url:hover {
        color: #0284c7;
        text-decoration: underline !important;
    }

    /* 대분류 배지 디자인 세련되게 변형 */
    .badge-menu {
        background-color: #f1f5f9;
        color: #475569;
        font-size: 0.8rem;
        font-weight: 600;
        padding: 0.4rem 0.75rem;
        border-radius: 6px;
        border: 1px solid #e2e8f0;
        display: inline-block;
    }
</style>

<div class="guide-container">
    <%-- 상단 타이틀 영역 --%>
    <div class="d-flex justify-content-between align-items-center border-bottom pb-4 mb-4">
        <div>
            <h1 class="h4 mb-1 text-dark fw-bold" style="letter-spacing: -0.5px;">
                KT 지니어스 퍼블리싱 가이드 & 페이지 맵
            </h1>
            <p class="text-muted small mb-0">화면별 URL 링크를 클릭하여 퍼블리싱 및 개발 검수를 진행하세요.</p>
        </div>
    </div>

    <div class="d-flex align-items-center mb-3">
        <h3 class="h6 fw-bold text-dark m-0">📂 페이지 리스트</h3>
    </div>

    <div class="table-responsive">
        <table class="table table-hover custom-guide-table w-100">
            <thead>
            <tr>
                <th style="width: 20%">대분류</th>
                <th style="width: 30%">메뉴/화면명</th>
                <th style="width: 50%">URL 연결 링크</th>
            </tr>
            </thead>
            <tbody>
            <%-- 가이드 메인 --%>
            <tr>
                <td><span class="badge-menu">공통2</span></td>
                <td class="fw-bold text-dark">퍼블리싱 가이드</td>
                <td>
                    <a href="${_contextPath}/common/pub-guide" class="link-url">${_contextPath}/common/pub-guide</a>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>