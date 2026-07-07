/**
 * 공통 checkbox/radio 유틸
 */
const CommonCheck = window.CommonCheck = {
    isChecked: function (selector) {
        return $(selector).is(":checked");
    },

    getCheckedValues: function (selector) {
        return $(selector)
            .filter(":checked")
            .map(function () {
                return this.value;
            })
            .get();
    },

    setChecked: function (selector, checked = true) {
        $(selector).prop("checked", checked).trigger("change");
    },

    toggleAll: function (masterSelector, itemSelector) {
        const checked = $(masterSelector).is(":checked");
        $(itemSelector).prop("checked", checked).trigger("change");
    },

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
