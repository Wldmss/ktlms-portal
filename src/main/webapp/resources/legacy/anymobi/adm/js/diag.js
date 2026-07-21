/****************************************************
 * 진단관리 관련 스크립트
 *
 * 작성자: written by bkLove(최병국)
 * 작성일: 2016-04-11
 ****************************************************/


/*
 * 진단관리
 * 
 * 작성자: written by bkLove(최병국)
 * 작성일: 2016-04-11
 */
var DIAG = {

    /*
     * 진단관리 탭 정보를 보여준다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-04-11
     */
    fn_tabView: function (selector) {

        try {

            if (selector && selector.length > 0) {
                var tabForm = jQuery("#tabForm");
                var action = "";

                switch (selector.attr("id")) {//alert(selector.attr("id"));

                    /* 사용자 > 역량진단 실시 탭 */
                    case    "usrDiagTab1":
                        action = "/taskCapability/usrDiagTargetList.do";
                        break;

                    /* 사용자 > 진단결과 확인 탭 */
                    case    "usrDiagTab2":
                        action = "/taskCapability/usrDiagResultList.do";
                        break;

                    /* 관리자 > 진단관리 탭 */
                    case    "admDiagTab1":
                        action = "/adm/taskCapability/diagmgr/diagList.do";
                        break;

                    /* 관리자 > 대상관리 탭 */
                    case    "admDiagTab2":
                        action = "/adm/taskCapability/diag/admDiagTargetList.do?INIT=true";
                        break;

                    /* 관리자 > 진단현황 탭 */
                    case    "admDiagTab2-2":
                        action = "/adm/taskCapability/diagStatus/admDiagStatus.do?INIT=true";
                        break;

                    /* 관리자 > 진단결과관리 탭 */
                    case    "admDiagTab3":
                        action = "/adm/taskCapability/diagResult/diagResultList.do?INIT=true";
                        break;

                }

                tabForm.attr(
                    {"action": action}
                ).submit();
            } else {
                throw "인자값을 확인해 주세요";
            }

        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * 진단지에서 역량별 필수정보를 체크한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-04-11
     */
    , fn_diagShowCheck: function (data) {

        try {

            if (data
                && data.id
                && data.showJson) {

                var selector = data.id;
                var inputDatas = jQuery("input", selector);

                if (inputDatas.length > 0) {

                    var jsonData = JSON.parse(data.showJson);
                    if (jsonData && jsonData.length != 0) {

                        for (var i = 0; i < jsonData.length; i++) {

                            var cpbIndiCode = jQuery("input[name='" + jsonData[i].cpbIndiCode + "']:checked", selector);
                            if (cpbIndiCode.length == 0 || !cpbIndiCode.val()) {
                                alert("[수준단계]를 선택해 주세요.");
                                jQuery("input[name='" + jsonData[i].cpbIndiCode + "']:eq(0)", selector).focus();

                                return false;
                            }
                        }
                    }
                }
            }

            return true;

        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * 진단지에서 역량별 필수정보를 체크한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-04-11
     */
    , fn_diagCheck: function (selector, data) {

        try {

            if (data
                && data.id
                && data.showJson) {

                if (selector) {

                    var group = jQuery(selector).attr("group");
                    var groupInx = jQuery(selector).attr("groupInx");
                    var inx = parseInt(jQuery(selector).attr("inx"));

                    /*
                     * 현재 위치 기준으로 -1, -2 단계 데이터를 비교한다. ( 인덱스가 2 이상부터 가능 )
                     * 2016-04-26	written by bkLove(최병국)
                     */
                    if (inx > 2) {

                        var cpbIndiCodeGroup1 = jQuery("input[group=" + group + "][groupIdx=" + selector.val() + "][inx=" + (inx - 1) + "]:checked", data.id);
                        var cpbIndiCodeGroup2 = jQuery("input[group=" + group + "][groupIdx=" + selector.val() + "][inx=" + (inx - 2) + "]:checked", data.id);

                        if (cpbIndiCodeGroup1 && cpbIndiCodeGroup2 && cpbIndiCodeGroup1.length > 0 && cpbIndiCodeGroup2.length > 0) {

                            if (selector.val() == jQuery(cpbIndiCodeGroup1).val()
                                && selector.val() == jQuery(cpbIndiCodeGroup2).val()) {

                                if (!confirm("동일 수준을 연달아 3회 이상 체크하셨습니다. 정말로 그렇게 하시겠습니까?")) {
                                    selector.attr("checked", false);
                                    return false;
                                } else {
                                    return false;
                                }
                            }
                        }
                    }


                    var jsonData = JSON.parse(data.showJson);
                    if (jsonData && jsonData.length != 0) {

                        /*
                         * 현재 위치 기준으로 -1, +1 단계 데이터를 비교한다. ( 전체갯수-현재인덱스 가 0보다 큰경우 )
                         * 2016-04-26	written by bkLove(최병국)
                         */
                        if (jsonData.length - inx > 0) {

                            var cpbIndiCodeGroup1 = jQuery("input[group=" + group + "][groupIdx=" + selector.val() + "][inx=" + (inx - 1) + "]:checked", data.id);
                            var cpbIndiCodeGroup2 = jQuery("input[group=" + group + "][groupIdx=" + selector.val() + "][inx=" + (inx + 1) + "]:checked", data.id);

                            if (cpbIndiCodeGroup1 && cpbIndiCodeGroup2 && cpbIndiCodeGroup1.length > 0 && cpbIndiCodeGroup2.length > 0) {

                                if (selector.val() == jQuery(cpbIndiCodeGroup1).val()
                                    && selector.val() == jQuery(cpbIndiCodeGroup2).val()) {

                                    if (!confirm("동일 수준을 연달아 3회 이상 체크하셨습니다. 정말로 그렇게 하시겠습니까?")) {
                                        selector.attr("checked", false);
                                        return false;
                                    } else {
                                        return false;
                                    }
                                }
                            }
                        }

                        /*
                         * 현재 위치 기준으로 +1, +2 단계 데이터를 비교한다. ( 전체갯수-현재인덱스 가 1보다 큰경우 )
                         * 2016-04-26	written by bkLove(최병국)
                         */
                        if (jsonData.length - inx > 1) {

                            var cpbIndiCodeGroup1 = jQuery("input[group=" + group + "][groupIdx=" + selector.val() + "][inx=" + (inx + 1) + "]:checked", data.id);
                            var cpbIndiCodeGroup2 = jQuery("input[group=" + group + "][groupIdx=" + selector.val() + "][inx=" + (inx + 2) + "]:checked", data.id);

                            if (cpbIndiCodeGroup1 && cpbIndiCodeGroup2 && cpbIndiCodeGroup1.length > 0 && cpbIndiCodeGroup2.length > 0) {

                                if (selector.val() == jQuery(cpbIndiCodeGroup1).val()
                                    && selector.val() == jQuery(cpbIndiCodeGroup2).val()) {

                                    if (!confirm("동일 수준을 연달아 3회 이상 체크하셨습니다. 정말로 그렇게 하시겠습니까?")) {
                                        selector.attr("checked", false);
                                        return false;
                                    } else {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return true;

        } catch (e) {
            console.log(e);
            throw e;
        }
    }
}