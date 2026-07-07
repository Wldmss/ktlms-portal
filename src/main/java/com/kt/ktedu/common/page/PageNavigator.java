package com.kt.ktedu.common.page;

import jakarta.servlet.http.HttpServletRequest;

/**
 * (레거시 호환) lms 의 {@code PageNavigator} 대체 어댑터.
 * <p>
 * 레거시는 서버에서 페이지 이동 HTML(&lt;a&gt;/&lt;img&gt;/&lt;script&gt;)을 문자열로 만들어
 * JSP 가 {@code ${paging.pageIndexes}}, {@code ${paging.hiddenInput}}, {@code ${paging.script}} 로 출력했다.
 * 이관 중 기존 JSP 가 깨지지 않도록 같은 getter 를 제공하되, 출력은 레거시 이미지가 아닌
 * 깨끗한 마크업으로 만든다.
 *
 * @deprecated 서버 HTML 생성 방식은 구식이다. 신규 코드는
 * {@link Pagination}(계산) + {@code /WEB-INF/views/common/util/pagination.jsp}(렌더) 조합을 사용한다.
 * <pre>{@code
 * // before
 * PageNavigator paging = new PageNavigator(request, page, rows, rowsPerPage, pagesPerIndex, orderBy, location, method).generatePageBean();
 * // after
 * Pagination paging = new Pagination(totalCount, curPage, pageSize);   // 컨트롤러
 * // JSP: <c:set var="pagination" value="${paging}"/> 후 pagination.jsp include
 * }</pre>
 */
@Deprecated(since = "2026-07-07")
public class PageNavigator {

    private final int page;
    private final int rows;
    private final int rowsPerPage;
    private final int pagesPerIndex;
    private final String orderBy;
    private final String location;

    private Pagination pagination;

    public PageNavigator() {
        this(1, 0, PageUtils.ROWS_PER_PAGE_DEFAULT, PageUtils.PAGES_PER_INDEX_DEFAULT, "", "");
    }

    public PageNavigator(HttpServletRequest request, String currPage, String rows, String rowsPerPage,
                         String pagesPerIndex, String orderBy, String location, String method) {
        this(parseInt(currPage, 1), parseInt(rows, 0), parseInt(rowsPerPage, PageUtils.ROWS_PER_PAGE_DEFAULT),
                parseInt(pagesPerIndex, PageUtils.PAGES_PER_INDEX_DEFAULT), nvl(orderBy), nvl(location));
    }

    public PageNavigator(String currPage, String rows, String rowsPerPage, String pagesPerIndex) {
        this(parseInt(currPage, 1), parseInt(rows, 0), parseInt(rowsPerPage, PageUtils.ROWS_PER_PAGE_DEFAULT),
                parseInt(pagesPerIndex, PageUtils.PAGES_PER_INDEX_DEFAULT), "", "");
    }

    public PageNavigator(String currPage, String rows, String rowsPerPage) {
        this(parseInt(currPage, 1), parseInt(rows, 0), parseInt(rowsPerPage, PageUtils.ROWS_PER_PAGE_DEFAULT),
                PageUtils.PAGES_PER_INDEX_DEFAULT, "", "");
    }

    public PageNavigator(String currPage, String rows) {
        this(parseInt(currPage, 1), parseInt(rows, 0), PageUtils.ROWS_PER_PAGE_DEFAULT,
                PageUtils.PAGES_PER_INDEX_DEFAULT, "", "");
    }

    private PageNavigator(int page, int rows, int rowsPerPage, int pagesPerIndex, String orderBy, String location) {
        this.page = Math.max(page, 1);
        this.rows = Math.max(rows, 0);
        this.rowsPerPage = rowsPerPage < 1 ? PageUtils.ROWS_PER_PAGE_DEFAULT : rowsPerPage;
        this.pagesPerIndex = pagesPerIndex < 1 ? PageUtils.PAGES_PER_INDEX_DEFAULT : pagesPerIndex;
        this.orderBy = orderBy;
        this.location = location;
    }

    /** 레거시 호환: 계산 후 자기 자신 반환 */
    public PageNavigator generatePageBean() {
        this.pagination = new Pagination(rows, page, rowsPerPage, pagesPerIndex);
        return this;
    }

    private Pagination pagination() {
        if (pagination == null) {
            pagination = new Pagination(rows, page, rowsPerPage, pagesPerIndex);
        }
        return pagination;
    }

    // ===== 레거시 JSP getter =====

    /**
     * 페이지 이동 버튼 HTML.
     * @deprecated {@code pagination.jsp} include 로 대체.
     */
    @Deprecated(since = "2026-07-07")
    public String getPageIndexes() {
        Pagination p = pagination();
        if (p.getPageCnt() <= 0) return "";

        StringBuilder sb = new StringBuilder("<nav class=\"pagination\" aria-label=\"페이지 이동\">");
        appendBtn(sb, "page-first", "처음", 1, p.getCurPage() <= 1, "&laquo;");
        appendBtn(sb, "page-prev", "이전", p.getPrevPage(), p.getCurPage() <= 1, "&lsaquo;");
        for (int i = p.getStartPage(); i <= p.getEndPage(); i++) {
            String cls = "page-num" + (i == p.getCurPage() ? " on" : "");
            appendBtn(sb, cls, i + "페이지", i, false, String.valueOf(i));
        }
        appendBtn(sb, "page-next", "다음", p.getNextPage(), p.getCurPage() >= p.getPageCnt(), "&rsaquo;");
        appendBtn(sb, "page-last", "마지막", p.getPageCnt(), p.getCurPage() >= p.getPageCnt(), "&raquo;");
        sb.append("</nav>");
        return sb.toString();
    }

    /**
     * 페이지 파라미터 hidden input.
     * @deprecated 페이지 이동은 {@code pagination.jsp} + 화면의 이동 함수로 처리.
     */
    @Deprecated(since = "2026-07-07")
    public String getHiddenInput() {
        return "<input type=\"hidden\" id=\"page\" name=\"page\" value=\"" + pagination().getCurPage() + "\">"
                + "<input type=\"hidden\" id=\"rowsPerPage\" name=\"rowsPerPage\" value=\"" + rowsPerPage + "\">"
                + "<input type=\"hidden\" id=\"orderBy\" name=\"orderBy\" value=\"" + escapeAttr(orderBy) + "\">";
    }

    /**
     * 페이지 이동 스크립트(pageExecutor.showPage).
     * @deprecated 화면별 이동 함수(goPage 등)로 대체. {@code pagination.jsp} 의 pageFunc 참고.
     */
    @Deprecated(since = "2026-07-07")
    public String getScript() {
        String action = escapeJs(location);
        return "<script>var pageExecutor={showPage:function(page){"
                + "var $f=$('#page').closest('form');$f.find('#page').val(page);"
                + (action.isBlank() ? "$f.submit();" : "$f.attr('action','" + action + "').submit();")
                + "}};</script>";
    }

    /** 전체 건수 표시용 (레거시 호환) */
    public long getRows() { return rows; }
    public int getPage() { return pagination().getCurPage(); }
    public long getPages() { return pagination().getPageCnt(); }
    public int getRowsPerPage() { return rowsPerPage; }
    public int getPagesPerIndex() { return pagesPerIndex; }
    public String getOrderBy() { return orderBy; }
    /** 계산된 Pagination (신규 코드는 이걸 꺼내 pagination.jsp 로 렌더) */
    public Pagination getPagination() { return pagination(); }

    // ===== private =====

    private void appendBtn(StringBuilder sb, String cls, String title, int targetPage, boolean disabled, String label) {
        sb.append("<button type=\"button\" class=\"pagination-btn ").append(cls).append("\" title=\"").append(title).append("\"")
                .append(" onclick=\"pageExecutor.showPage(").append(targetPage).append(")\"")
                .append(disabled ? " disabled" : "").append(">").append(label).append("</button>");
    }

    private static int parseInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }

    private static String escapeAttr(String value) {
        return nvl(value).replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String escapeJs(String value) {
        return nvl(value).replace("\\", "\\\\").replace("'", "\\'").replace("<", "\\x3C");
    }
}
