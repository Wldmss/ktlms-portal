<%-- genius web 공통 bottom (tiles pageLayout/main/miniLecture/myCourse 통합) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
</div><%--content end--%>
</main>
<%-- footer: 기존 defaultFooterLayout --%>
<%@ include file="/WEB-INF/views/common/tiles/web/defaultFooterLayout.jsp" %>
<%-- 기존 defaultJsLayout(하단 스크립트) 대체 --%>
<%@ include file="/WEB-INF/views/common/core/script.jsp" %>
<%-- genius 레거시 레이아웃 JS --%>
<%@ include file="/WEB-INF/views/common/tiles/web/tiles-js.jsp" %>
</body>
</html>
