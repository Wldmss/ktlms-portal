/**
 * 공통 checkbox/radio 유틸
 */
const CommonCheck = window.CommonCheck = {
    /**
     * 체크 여부 확인 (radio 는 그룹 중 선택된 게 있는지)
     * @example CommonCheck.isChecked("#agree") → true/false
     */
    isChecked: function (selector) {
        return $(selector).is(":checked");
    },

    /**
     * 체크된 항목들의 value 배열 반환 (없으면 빈 배열)
     * @example CommonCheck.getCheckedValues("input[name='ids']") → ["1", "3"]
     */
    getCheckedValues: function (selector) {
        return $(selector)
            .filter(":checked")
            .map(function () {
                return this.value;
            })
            .get();
    },

    /**
     * 체크 상태 설정 (change 이벤트도 발생시켜 연동 로직이 같이 동작)
     * @example CommonCheck.setChecked("#agree");         // 체크
     * @example CommonCheck.setChecked("#agree", false);  // 해제
     */
    setChecked: function (selector, checked = true) {
        $(selector).prop("checked", checked).trigger("change");
    },

    /**
     * 전체선택 체크박스 상태를 하위 항목들에 일괄 적용 (master 의 change 시점에 호출)
     * — 보통 직접 쓰기보다 bindAll 로 묶는 것을 권장
     */
    toggleAll: function (masterSelector, itemSelector) {
        const checked = $(masterSelector).is(":checked");
        $(itemSelector).prop("checked", checked).trigger("change");
    },

    /**
     * 전체선택 ↔ 하위 항목 양방향 연동 바인딩 (한 번만 호출하면 됨)
     * - 전체선택 클릭 → 하위 전체 체크/해제
     * - 하위 개별 클릭 → 전부 체크되면 전체선택도 체크, 하나라도 풀리면 해제
     * - document 위임이라 동적으로 추가된 행에도 동작, 재호출 시 기존 바인딩은 교체됨
     * @example CommonCheck.bindAll("#checkAll", "input[name='ids']");
     */
    bindAll: function (masterSelector, itemSelector) {
        const updateMaster = () => {
            const $items = $(itemSelector);
            const total = $items.length;
            const checkedCount = $items.filter(":checked").length;

            $(masterSelector).prop("checked", total > 0 && total === checkedCount);
        };

        $(document)
            .off("change.commonCheckMaster", masterSelector)
            .on("change.commonCheckMaster", masterSelector, () => {
                this.toggleAll(masterSelector, itemSelector);
            });

        $(document)
            .off("change.commonCheckItem", itemSelector)
            .on("change.commonCheckItem", itemSelector, updateMaster);

        updateMaster();
    }
};
