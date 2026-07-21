<%-- [검증용] 웹 tiles-top / tiles-bottom 레이아웃 샘플 --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- subGNB(서브 비주얼) 분기 확인: 값을 세팅하면 tiles-top 이 조건 include --%>
<c:set var="subGNB" value="/WEB-INF/views/common/tiles/web/defaultSubGNBLayout.jsp"/>

<%@ include file="/WEB-INF/views/common/tiles/web/tiles-top.jsp" %>

<div style="padding:40px;">
    <h2>웹 레이아웃 검증 — <code>tiles-top</code> / <code>tiles-bottom</code></h2>
    <ul>
        <li>상단 GNB: <code>gnbLayout.jsp</code> (AJAX <code>/a/layout/gnbListAjax.do</code> → MOCK HTML)</li>
        <li>서브 비주얼: <code>defaultSubGNBLayout.jsp</code> (<code>${'$'}{subGNB}</code> 세팅 시 노출, AJAX MOCK)</li>
        <li>푸터: <code>defaultFooterLayout.jsp</code></li>
        <li>css/js: <code>core/meta · resources · script</code></li>
    </ul>
    <p>이 문단이 <code>&lt;div class="content"&gt;</code> 안 body 영역입니다.</p>
    <p>device: <strong>${deviceType}</strong> / isApp: <strong>${isApp}</strong></p>
</div>

<%@ include file="/WEB-INF/views/common/tiles/web/tiles-bottom.jsp" %>
