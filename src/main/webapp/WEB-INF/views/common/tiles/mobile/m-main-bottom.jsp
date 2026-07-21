<%-- genius 모바일 메인 bottom (footer + 하단 네비바 + 교육링크 모달) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
    </div><%--content end--%>

    <%-- footer: 기존 defaultMobileFooterLayout --%>
    <%@ include file="/WEB-INF/views/common/tiles/mobile/defaultMobileFooterLayout.jsp" %>
</div><%--l-wrap end--%>

<%-- 메인 전용: 하단 네비게이션 바 --%>
<div class="navigation-tab-bar">
    <ul>
        <li><a href="/mobile/m/common/userInfoDetail" class="navi-bottom navi-bottom-status"><span>학습현황</span></a></li>
        <li><a href="/mobile/m/education/list/courseList" class="navi-bottom navi-bottom-participation"><span>학습참여</span></a></li>
        <li><a href="/mobile/m/main/portalMain" class="navi-bottom navi-bottom-home"><span>홈</span></a></li>
        <c:if test="${comp eq '1001'}">
            <li><a href="#none" class="navi-bottom navi-bottom-link" data-target="modalMainExternallink"><span>교육링크</span></a></li>
        </c:if>
        <li><a href="/mobile/m/myclass/course/myCourse/myCourseList" class="navi-bottom navi-bottom-mycourse"><span>나의강의실</span></a></li>
    </ul>
</div>

<%-- 메인 전용: 교육링크 모달 --%>
<div class="modal-wrap bottomsheet" id="modalMainExternallink">
    <div class="modal-body">
        <div class="modal-header">
            <p>교육링크</p>
        </div>
        <div class="modal-container">
            <div class="modal-edulink-wrap">
                <a href="javascript:void(0);" class="link-item" onclick="javascript:linkUrl('popupKyoboAJAX' , 'M.LEARNING.GENIEBOOKS', '지니북스')">
                    <img src="/resources/legacy/newPortal/images/link-edu-1.png" alt="">
                    <span>지니북스</span>
                </a>
                <a href="javascript:void(0);" class="link-item" onclick="javascript:goOutAice()">
                    <img src="/resources/legacy/newPortal/images/link-edu-4.png" alt="">
                    <span>AICE</span>
                </a>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/core/script.jsp" %>
<%-- genius 레거시 모바일 레이아웃 JS --%>
<%@ include file="/WEB-INF/views/common/tiles/mobile/m-js.jsp" %>
</body>
</html>
