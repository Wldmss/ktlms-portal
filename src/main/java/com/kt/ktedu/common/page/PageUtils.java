package com.kt.ktedu.common.page;

import java.util.Map;

import com.kt.ktedu.common.util.core.MapUtil;

/**
 * 페이징 계산 static 헬퍼. genius/lms 레거시 {@code PageUtils} 의 순수 계산 메서드를 유지한다.
 * <p>
 * 레거시 {@code PageUtils} 의 HTML 생성 메서드(generatePageIndexes/generateScript/generateInput 등,
 * &lt;a&gt;/&lt;img&gt;/&lt;script&gt; 문자열 + pagingProperties 이미지 의존)는 <b>이관하지 않는다.</b>
 * 페이지 이동 UI 는 {@link Pagination} + {@code /WEB-INF/views/common/util/pagination.jsp} 로 대체한다.
 */
public final class PageUtils {

    /** 페이지당 기본 행 수 */
    public static final int ROWS_PER_PAGE_DEFAULT = 10;
    /** 블록당 기본 페이지 수 */
    public static final int PAGES_PER_INDEX_DEFAULT = 10;

    private PageUtils() {
    }

    /**
     * 페이지 번호 → 그 페이지 첫 행의 1-based 위치
     * <pre>getFirstRowOfPage(3, 10) → 21</pre>
     */
    public static long getFirstRowOfPage(long page, int rowsPerPage) {
        return (page - 1) * rowsPerPage + 1;
    }

    /**
     * 블록(index) 번호 → 그 블록 첫 페이지 번호
     */
    public static long getFirstPageOfIndex(long index, int pagesPerIndex) {
        return (index - 1) * pagesPerIndex + 1;
    }

    /**
     * 페이지 수 → 그 페이지가 속한 블록 수 (올림)
     */
    public static long getIndexOfPage(long pages, int pagesPerIndex) {
        return (long) Math.ceil((double) pages / pagesPerIndex);
    }

    /**
     * 전체 행 수 → 전체 페이지 수 (올림)
     * <pre>getPageOfRow(53, 10) → 6</pre>
     */
    public static long getPageOfRow(long rows, int rowsPerPage) {
        return (long) Math.ceil((double) rows / rowsPerPage);
    }

    /**
     * 요청 page 가 전체 페이지 수를 넘으면 마지막 페이지로 보정 (레거시 호환).
     * input 의 "page"/"rowsPerPage", output 의 "count"(전체 건수)를 읽어 input 의 "page" 를 조정한다.
     */
    public static void adjustPage(Map<String, Object> input, Map<String, Object> output) {
        if (input == null || output == null) return;

        String pageStr = MapUtil.getString(input, "page");
        if (pageStr.isBlank()) return;

        long page = Long.parseLong(pageStr);
        int rowsPerPage = MapUtil.getInt(input, "rowsPerPage", ROWS_PER_PAGE_DEFAULT);
        long count = MapUtil.getLong(output, "count", 0L);

        long totalPage = getPageOfRow(count, rowsPerPage);
        if (totalPage < 1) totalPage = 1;
        if (page > totalPage) {
            input.put("page", String.valueOf(totalPage));
        }
    }
}
