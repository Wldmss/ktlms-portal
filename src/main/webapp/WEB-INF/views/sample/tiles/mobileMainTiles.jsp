<%-- [검증용] 모바일 메인 m-main-top / m-main-bottom 레이아웃 샘플 --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/tiles/mobile/m-main-top.jsp" %>

<div style="padding:20px;">
    <h2>모바일 메인 레이아웃 — <code>m-main-top</code> / <code>m-main-bottom</code></h2>
    <ul>
        <li>헤더: <code>KT_GNB_Mobile_Main.jsp</code> (풀 GNB · 햄버거 전체메뉴/알림/사용자)</li>
        <li>하단 네비바 + 교육링크 모달: <code>m-main-bottom.jsp</code></li>
        <li>통합검색 팝업: <code>KT_SearchPop.jsp</code> include</li>
        <li>푸터: <code>defaultMobileFooterLayout.jsp</code></li>
    </ul>
    <p>이 문단이 <code>#content.mobile-main</code> 안 body 영역입니다.</p>
    <p>device: <strong>${deviceType}</strong></p>
</div>

<%@ include file="/WEB-INF/views/common/tiles/mobile/m-main-bottom.jsp" %>
