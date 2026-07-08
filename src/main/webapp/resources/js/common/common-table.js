/**
 * 공통 테이블(jQuery DataTables) 표준 래퍼.
 * 레거시 KtLms.datatable 을 대체한다 — 단 KtLms god object(팝업/AUTH 등)는 가져오지 않고 "테이블"만 담는다.
 *
 * 의존성: jQuery, jQuery DataTables, common-ajax.js(postAjax). DataTables 미로딩이면 안전하게 no-op.
 *
 * 서버 규격은 portal 표준(PageRequest/PageResponse)에 맞춘다:
 *  - 요청: {page, size, ...검색조건}  (DataTables 의 start/length 를 page/size 로 자동 변환)
 *  - 응답: ResponseDTO {data: PageResponse{list, totalCount, ...}}  (DataTables 형식으로 자동 매핑)
 * 즉 서버 컨트롤러는 DataTables 를 몰라도 되고, 이 파일이 양방향 변환을 흡수한다.
 */
(function ($) {
    "use strict";

    // DataTables 미로딩 환경(비-admin 페이지 등)에서 로드돼도 에러 없이 넘어가도록 가드
    if (!$.fn || !$.fn.dataTable) {
        return;
    }

    const CommonTable = window.CommonTable = {};

    // 한글 language 팩 (레거시와 동일한 표기 유지)
    const LANG_KO = {
        emptyTable: "데이터가 없습니다.",
        info: "총 <span class='table-total'>_TOTAL_</span> 건",
        infoEmpty: "총 <span class='table-total'>0</span> 건",
        infoFiltered: "",
        thousands: ",",
        lengthMenu: "_MENU_ 개씩 보기",
        loadingRecords: "로딩중...",
        processing: "처리중...",
        zeroRecords: "검색된 데이터가 없습니다.",
        paginate: {first: "처음", last: "마지막", next: "다음", previous: "이전"}
    };

    // 전역 기본값: 기본 검색창 숨김(커스텀 검색폼 사용) + 처리중 표시 + 정렬 off + 한글 + 체크박스 콜백.
    // serverSide 는 전역이 아니라 init 에서 url 이 있을 때만 켠다(클라이언트 테이블도 안전하게).
    $.extend(true, $.fn.dataTable.defaults, {
        dom: 't<"row table-foot"<"col-md-6"><"col-md-6 text-end"p>>',
        processing: true,
        ordering: false,
        language: LANG_KO,
        drawCallback: function () {
            const $table = $(this[0]);
            $("thead th input[type=checkbox]", $table).off("click.commonTable").on("click.commonTable", function (e) {
                toggleAll(e, $table);
            });
            $("tbody tr input[type=checkbox]", $table).off("click.commonTable").on("click.commonTable", function () {
                syncHeaderCheckbox($table);
            });
        }
    });

    /**
     * 테이블 생성.
     * @param {string} selector - 대상 table 선택자
     * @param {Object} options
     * @param {Array}  options.columns  - DataTables columns 정의 (필수)
     * @param {string} [options.url]    - 서버 페이징 URL. 지정 시 serverSide 로 동작
     * @param {Object|Function} [options.data] - 매 요청에 함께 보낼 검색조건(객체 또는 () => 객체)
     * @param {number} [options.pageLength=10]
     * @param {boolean} [options.ordering=false]
     * @param {Object} [options.dataTableOptions] - 그 외 DataTables 원본 옵션(병합)
     * @returns {DataTables.Api} 생성된 테이블 인스턴스
     */
    CommonTable.init = function (selector, options) {
        const opts = options || {};

        const config = $.extend({
            columns: opts.columns,
            pageLength: opts.pageLength || 10,
            ordering: opts.ordering === true
        }, opts.dataTableOptions);

        if (opts.url) {
            config.serverSide = true;
            config.ajax = buildServerAjax(opts);
        }

        return $(selector).DataTable(config);
    };

    /** 체크된 행의 value 배열 반환 */
    CommonTable.getCheckedValues = function (selector) {
        return $(selector).find("tbody input[type=checkbox]:checked").map(function () {
            return this.value;
        }).get();
    };

    /** 체크된 행이 하나라도 있는지 */
    CommonTable.isSelected = function (selector) {
        return $(selector).find("tbody input[type=checkbox]:checked").length > 0;
    };

    // ===== private =====

    /**
     * DataTables serverSide ajax 함수.
     * DataTables 파라미터(start/length/draw/search) → portal {page,size,keyword} 로 변환해 POST,
     * ResponseDTO{data: PageResponse} 응답 → DataTables {draw, recordsTotal, recordsFiltered, data} 로 매핑.
     */
    function buildServerAjax(opts) {
        return function (dtParams, callback) {
            const size = dtParams.length > 0 ? dtParams.length : 10;
            const page = Math.floor((dtParams.start || 0) / size) + 1;

            const extra = typeof opts.data === "function" ? opts.data() : (opts.data || {});
            const body = $.extend({}, extra, {
                page: page,
                size: size,
                keyword: dtParams.search ? dtParams.search.value : ""
            });

            postAjax(opts.url, body)
                .then(function (res) {
                    const pageData = (res && res.data) ? res.data : {};
                    const list = pageData.list || [];
                    const total = (pageData.totalCount != null) ? pageData.totalCount : list.length;
                    callback({
                        draw: dtParams.draw,
                        recordsTotal: total,
                        recordsFiltered: total, // 서버 검색 결과 기준(별도 필터 미사용)
                        data: list
                    });
                })
                .catch(function () {
                    // 오류 시 빈 결과로 그려 테이블이 깨지지 않게 (인증오류 리다이렉트는 common-ajax 가 처리)
                    callback({draw: dtParams.draw, recordsTotal: 0, recordsFiltered: 0, data: []});
                });
        };
    }

    /** 헤더 전체선택 체크박스 → 본문 전체 토글 */
    function toggleAll(event, $table) {
        const checked = event.target.checked;
        $table.find("tbody input[type=checkbox]:not([disabled])").prop("checked", checked);
    }

    /** 본문 체크 상태에 따라 헤더 전체선택 체크박스 동기화 */
    function syncHeaderCheckbox($table) {
        const $items = $table.find("tbody input[type=checkbox]:not([disabled])");
        const $checked = $items.filter(":checked");
        $table.find("thead input[type=checkbox]:not([disabled])")
            .prop("checked", $items.length > 0 && $items.length === $checked.length);
    }
})(jQuery);
