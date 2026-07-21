<%-- [검증용] 모바일 일반 m-top / m-bottom 레이아웃 샘플 --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/tiles/mobile/m-top.jsp" %>

<div style="padding:20px;">
    <h2>모바일 일반 레이아웃 — <code>m-top</code> / <code>m-bottom</code></h2>
    <ul>
        <li>헤더: <code>KT_GNB_Mobile.jsp</code> (서브페이지용 · 뒤로가기/페이지 타이틀)</li>
        <li>통합검색 팝업: <code>KT_SearchPop.jsp</code> include</li>
        <li>푸터: <code>defaultMobileFooterLayout.jsp</code></li>
    </ul>
    <p>이 문단이 <code>#content</code> 안 body 영역입니다.</p>
    <p>device: <strong>${deviceType}</strong></p>
</div>

<%@ include file="/WEB-INF/views/common/tiles/mobile/m-bottom.jsp" %>
