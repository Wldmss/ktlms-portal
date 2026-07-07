<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--
    공통 페이지네이션 렌더 fragment (Pagination 과 세트, 신규 표준).
    레거시 PageNavigator/${paging.pageIndexes}(서버 HTML 문자열 생성) 방식을 대체한다.

    사용법:
      <c:set var="pagination" value="${paging}"/> # Pagination 객체
      <c:set var="pageFunc" value="goPage"/>      # 선택: 페이지 이동 JS 함수명 (기본 goPage)
      <%@ include file="/WEB-INF/views/common/util/pagination.jsp" %>

    페이지는 이동 함수를 정의해야 한다(검색조건 유지·목록 재조회 방식은 화면마다 다르므로):
      function goPage(page) { $("#page").val(page); searchList(); }   // 예시
--%>
<c:if test="${not empty pagination and pagination.pageCnt > 0}">
    <c:set var="_pf" value="${empty pageFunc ? 'goPage' : pageFunc}"/>
    <nav class="pagination" aria-label="페이지 이동">
        <button type="button" class="pagination-btn page-first" title="처음"
                onclick="${_pf}(1)" ${pagination.curPage <= 1 ? 'disabled' : ''}>&laquo;</button>
        <button type="button" class="pagination-btn page-prev" title="이전"
                onclick="${_pf}(${pagination.prevPage})" ${pagination.curPage <= 1 ? 'disabled' : ''}>&lsaquo;</button>

        <c:forEach var="p" begin="${pagination.startPage}" end="${pagination.endPage}">
            <button type="button" class="pagination-btn page-num ${p == pagination.curPage ? 'on' : ''}"
                    <c:if test="${p == pagination.curPage}">aria-current="page"</c:if>
                    onclick="${_pf}(${p})">${p}</button>
        </c:forEach>

        <button type="button" class="pagination-btn page-next" title="다음"
                onclick="${_pf}(${pagination.nextPage})" ${pagination.curPage >= pagination.pageCnt ? 'disabled' : ''}>&rsaquo;</button>
        <button type="button" class="pagination-btn page-last" title="마지막"
                onclick="${_pf}(${pagination.pageCnt})" ${pagination.curPage >= pagination.pageCnt ? 'disabled' : ''}>&raquo;</button>
    </nav>
</c:if>
