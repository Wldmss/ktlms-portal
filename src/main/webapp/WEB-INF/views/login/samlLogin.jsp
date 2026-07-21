<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/common/layout/default-top.jsp" %>

<style>
    .saml-login-page {
        min-height: 70vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 40px 20px;
        background: #f6f7fb;
    }
    .saml-login-box {
        width: min(520px, 100%);
        background: #fff;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        padding: 32px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.08);
    }
    .saml-login-box h1 {
        margin: 0 0 12px;
        font-size: 24px;
    }
    .saml-login-box p {
        margin: 0 0 18px;
        color: #555;
        line-height: 1.6;
    }
    .saml-login-box a {
        display: inline-flex;
        height: 42px;
        align-items: center;
        justify-content: center;
        padding: 0 18px;
        border-radius: 6px;
        background: #e15257;
        color: #fff;
        text-decoration: none;
        font-weight: 700;
    }
</style>

<div class="saml-login-page">
    <div class="saml-login-box">
        <h1>SAML 로그인</h1>
        <p>채널 <strong><c:out value="${channel}" /></strong> 연동 로그인 진입점입니다.</p>
        <p>SDK/채널별 SAML 처리 모듈이 연결되면 이 화면에서 SAML 응답 생성 또는 IdP/SP 연계를 이어가면 됩니다.</p>
        <a href="<c:url value='/' />">로그인 화면</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/layout/default-bottom.jsp" %>
