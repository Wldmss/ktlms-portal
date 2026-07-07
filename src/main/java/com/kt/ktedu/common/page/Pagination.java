package com.kt.ktedu.common.page;

import lombok.Getter;

/**
 * 페이징 계산 클래스. genius/lms 레거시 {@code Pagination} 의 API 를 그대로 유지하여
 * <b>이관 시 컨트롤러/JSP 수정 없이</b> 쓸 수 있게 한다.
 * <p>
 * 사용법(레거시와 동일):
 * <pre>{@code
 * // Controller
 * int totalCount = boardService.countList(param);   // 전체 건수
 * Pagination paging = new Pagination(totalCount, curPage, pageSize);
 * model.addAttribute("paging", paging);
 * // 목록 조회에는 시작 위치가 필요하면 paging.getStartIndex()(0-based) 사용
 * }</pre>
 * <pre>{@code
 * <%-- JSP: 숫자 getter 로 직접 렌더링하거나, 공통 fragment 사용 --%>
 * ${paging.startPage} ~ ${paging.endPage} / 총 ${paging.listCnt}건
 * <%@ include file="/WEB-INF/views/common/util/pagination.jsp" %>  <%-- pagination 이름으로 넘길 때 --%>
 * }</pre>
 *
 * <p>서버가 페이지 이동 HTML(&lt;a&gt;/&lt;img&gt;/&lt;script&gt;)을 문자열로 생성하던
 * 레거시 {@code PageNavigator}/{@code PageUtils.generateXXX} 방식은 구식이므로,
 * 이 클래스(숫자 계산) + {@code pagination.jsp}(렌더링) 조합으로 대체한다.</p>
 *
 * <p>생성자에서 값을 계산해 확정하는 불변 객체다. 필드 getter 는 Lombok {@code @Getter} 로 생성하며,
 * setter 는 두지 않는다(값을 임의로 바꾸면 다른 계산값과 어긋남).</p>
 */
@Getter
public class Pagination {

    /** 페이지 번호 블록 크기 (한 번에 보여줄 페이지 수). 레거시 기본값 5 유지 */
    private static final int DEFAULT_RANGE_SIZE = 5;

    private final int pageSize;    // 페이지당 행 수
    private final int rangeSize;   // 페이지 번호 블록 크기
    private final int curPage;     // 현재 페이지 (1부터)
    private final int listCnt;     // 전체 건수
    private final int pageCnt;     // 전체 페이지 수
    private final int rangeCnt;    // 전체 블록 수
    private final int curRange;    // 현재 블록 번호
    private final int startPage;   // 현재 블록 시작 페이지
    private final int endPage;     // 현재 블록 끝 페이지
    private final int startIndex;  // 현재 페이지 첫 행의 0-based offset
    private final int prevPage;    // 이전 페이지 (curPage-1)
    private final int nextPage;    // 다음 페이지 (curPage+1)
    private final int lastPage;    // 다음 블록으로 점프할 목표 페이지

    public Pagination(int listCnt, int curPage, int pageSize) {
        this(listCnt, curPage, pageSize, DEFAULT_RANGE_SIZE);
    }

    public Pagination(int listCnt, int curPage, int pageSize, int rangeSize) {
        // 방어: 잘못된 입력이 와도 0-나눗셈/음수 없이 동작하도록 보정 (정상 입력에는 영향 없음)
        this.pageSize = pageSize < 1 ? 10 : pageSize;
        this.rangeSize = rangeSize < 1 ? DEFAULT_RANGE_SIZE : rangeSize;
        this.listCnt = Math.max(listCnt, 0);

        this.pageCnt = (int) Math.ceil(this.listCnt * 1.0 / this.pageSize);
        this.rangeCnt = (int) Math.ceil(this.pageCnt * 1.0 / this.rangeSize);

        int page = Math.max(curPage, 1);
        this.curPage = page;

        this.curRange = (page - 1) / this.rangeSize + 1;
        this.startPage = (this.curRange - 1) * this.rangeSize + 1;
        int end = this.startPage + this.rangeSize - 1;
        this.endPage = Math.min(end, this.pageCnt);

        this.startIndex = (page - 1) * this.pageSize;
        this.prevPage = page - 1;
        this.nextPage = page + 1;
        this.lastPage = Math.min(page + this.rangeSize, this.pageCnt);
    }

    // 레거시 호환 getter(getPageSize/getStartPage/getEndPage/getListCnt/getPageCnt/getCurPage 등)는
    // 필드명이 레거시와 동일하므로 @Getter 가 자동 생성한다. JSP EL(${paging.startPage} 등)도 그대로 동작.

    // ===== 신규 코드용 별칭/편의 (의미가 명확한 이름) =====
    /** 전체 페이지 수 ({@link #getPageCnt} 별칭) */
    public int getTotalPages() { return pageCnt; }
    /** 전체 건수 ({@link #getListCnt} 별칭) */
    public int getTotalCount() { return listCnt; }
    /** 조회 시작 offset(0-based). SQL OFFSET/ROWNUM 시작값 ({@link #getStartIndex} 별칭) */
    public int getOffset()     { return startIndex; }
    /** 이전 페이지 존재 여부 */
    public boolean isHasPrev() { return curPage > 1; }
    /** 다음 페이지 존재 여부 */
    public boolean isHasNext() { return curPage < pageCnt; }

    /**
     * 현재 페이지 첫 행의 화면 표시용 번호(내림차순 목록의 "No" 컬럼용).
     * <pre>총 53건, curPage=2, pageSize=10 → 43 (43,42,41...)</pre>
     */
    public long getStartRowNum() {
        return listCnt - (long) (curPage - 1) * pageSize;
    }
}
