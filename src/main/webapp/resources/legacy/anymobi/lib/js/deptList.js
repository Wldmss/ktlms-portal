function isMobile() {
    var result = false;
    if (navigator.userAgent.indexOf('Mobile') > -1) {
        result = true;
    }
    return result;
}

var liList = new Array(); // 아래테이블의 li 리스트
var alldownList = new Array(); // 아래테이블의 모든 데이터

/* 최상위 부서 조회 */
function deptSearch() {
    $("#deptBtnClick").val("Y");
    $(".l-member-seach .pagenation #divPageSub").remove();
    var reqData = {
        p_dept_cd: 'ROOT'
    };

    $.ajax({
        url: '/getCommonRootSearchDeptAjax.do',
        type: 'POST',
        data: JSON.stringify(reqData),
        dataType: "json",
        error: function (xhr) {
            //console.log(xhr.status + " " + xhr.statusText);
        },
        success: function (data) {
            var str = "";
            $("#rootUl").empty();
            str += "<li id='li_ROOT' class='tree-list on'>";
            str += "<input type='hidden' value='N' id='deptBtnClick'/>";
            str += "<span class='tree' id='ROOT' onclick='deptSearch()'>조직</span>";
            str += "</li>";
            for (var i = 0; i < data.length; i++) {
                str += "<li id=li_" + data[i].comp_cd + "  class='tree-list on'>";
                str += "<span class='tree' id=" + data[i].comp_cd
                    + " onclick=clickDept(" + data[i].org_cd + ");>"
                    + data[i].org_nm;
                str += "</span>";
                if (data[i].dept_cd == null) {
                    str += "<input type='hidden' value=" + i
                        + " id=hiddenDeptCd_" + i + ">";
                }
                str += "</span>";
                str += "<input type='hidden' value=" + data[i].comp_cd
                    + " id=hiddenCompCd_" + i + ">";
                str += "<input type='hidden' value="
                    + (data[i].org_level)
                    + " id=hiddenOrgLever_" + i + ">";
                str += "<input type='hidden' value=" + data[i].leaf_yn
                    + " id=hiddenLeafYN_" + data[i].comp_cd + ">";
                str += "<input type='hidden' value='false' id=deptClickFlag_1001" + ">";
                str += "</li>";
            }// end for
            $("#rootUl").append(str);

            $("#empSearchList").empty();
            for (var i = 0; i < data.length; i++) {
                // 오른쪽 영역에 추가
                var str2 = "<input type='hidden' value=" + reqData.dataCode + " id='dataCode'/>";
                for (var i = 0; i < data.length; i++) {
                    str2 += " <li class='t-body-list'>";
                    str2 += " <div class='td part'><div class='inpt'>";
                    str2 += " <input type='checkbox' id=userTopTbChk_"
                        + (i + 1) + " value=" + data[i].dept_cd
                        + " name=userTopChk >";
                    str2 += "<label for=userTopTbChk_" + (i + 1)
                        + "><span id=tbDeptNM_" + data[i].dept_cd + " style='text-align:left;'>"
                        + data[i].org_full_nm
                        + "</span></label></div></div>";
                    str2 += "</li>";
                }
                $("#empSearchList").append(str2);
            }// end for
        }
    });
}

/* 페이징 함수 */
function pagingDept(data, name, temp, curpage) {
    $("#divPageSub").remove();
    var ckEvent = "";
    if (temp == '2') {
        ckEvent = "pageDept";
    }
    var pageArr = new Array();
    pageArr = data;
    var pageStr = "<div id=divPageSub><div class='prev-btn'>";
    var curPage = curpage;
    var prevPage = pageArr.prevPage;
    var startPage = pageArr.startPage;
    var endPage = pageArr.endPage;
    var pageCnt = pageArr.pageCnt;
    var nextPage = pageArr.nextPage;

    if (prevPage < 0 || prevPage == 0) {
        prevPage = 0;
        curPage = 1;
        nextPage = 2;
    }// end if

    pageStr += "<button type='button' class='prev'  onclick=" + ckEvent + "("
        + prevPage + ",'" + name + "');></button>";
    pageStr += "</div> <ul class='pagenation-ul'>";

    for (var i = startPage; i <= endPage; i++) {
        if (curPage == i) {
            pageStr += "<li class='on'>";
        } else {
            pageStr += "<li>";
        }// end if
        pageStr += " <span onclick=" + ckEvent + "(" + i + ",'" + name + "');>"
            + i + "</span></a></li>";
    }// end for
    pageStr += "</ul> <div class=next-btn>";

    if (curPage == pageCnt) {
        nextPage = endPage;
    }
    pageStr += " <button type='button' class='next' onclick=" + ckEvent + "("
        + nextPage + ",'" + name + "')></button>";
    pageStr += "</div></div>";
    $("#divPage").append(pageStr);
}

/* 부서클릭시 하위부서와 부서원 조회 */
function clickDept(data) {
    $(".tree-list").not('.leaf').css('color', '#666666');
    $(".tree-list.leaf").css('color', '#ACACAC');
    $("#deptNmText_" + data).css('color', '#4E79B7');

    var comp = $("#hiddenCompCd_" + data).val();
    var dept = $("#hiddenDeptCd_" + data).val();
    var org_level = $("#hiddenOrgLever_" + data).val();
    var leaf = $("#hiddenLeafYN_" + comp).val();

    if (data == "1001") {
        if ($("#deptClickFlag_1001").val() == "true") {
            $("#ul_1001").remove();
            $("#deptClickFlag_1001").val("false");
            return;
        }
    }

    if ($("#deptClickFlag_" + dept).val() == "true") {
        $("#ul_" + dept).remove();
        $("#deptClickFlag_" + dept).val("false");
        return;
    }

    if (data == dept) {
        dept = $("#hiddenDeptCd_" + data).val();
        leaf = $("#hiddenLeafYN_" + comp).val();
        org_level = $("#hiddenOrgLever_" + data).val();
        $("#deptClickFlag_" + data).val("true");
        dept = data;
        data = "1001." + dept;
    } else {
        dept = $("#hiddenDeptCd_" + dept).val();
        comp = $("#hiddenCompCd_" + dept).val();
        leaf = $("#hiddenLeaf_" + dept).val();
        $("#deptClickFlag_" + data + "_" + comp).val("true");
        org_level = parseInt($("#hiddenLevel_" + dept).val()) + 1;
    }
    if (data == "1001") {
        dept = data;
        comp = $("#hiddenCompCd_" + dept).val();
        leaf = $("#hiddenLeaf_" + dept).val();
        $("#deptClickFlag_" + data + "_" + comp).val("true");
        org_level = parseInt($("#hiddenLevel_" + dept).val()) + 1;
    }
    var evData = {
        temp: dept,
        comp: comp
    };
    if (dept == null || dept == "") {
        dept = "";
    } else {
        dept = "." + dept;
    }

    var deptData = {
        p_org_cd: data,
        org_level: org_level
    };
    var userData = {
        dataCode: data,
        curPage: 1
    };
    getDeptCommonList(deptData, evData);

    if (data < 50) {
        //getDeptDetailList(userData);
        return;
    } else {
        getDeptDetailList(userData);
    }
}

function getDeptCommonList(reqData, evData) {
    $("#deptBtnClick").val("Y");
    $.ajax({
        url: '/getCommonDeptSearchDeptAjax.do',
        type: 'POST',
        data: JSON.stringify(reqData),
        error: function (xhr) {
            //console.log(xhr.status + " " + xhr.statusText);
        },
        success: function (data) {
            if (data.length > 0) {
                var str = "<ul id=ul_"
                    + evData.temp
                    + ">";
                str += "<input type='hidden' value='false' id='deptClickFlag_"
                    + evData.temp + "'>";
                for (var i = 0; i < data.length; i++) {
                    str += "<li class='tree-chd" + 1 + "-list on' id=li_"
                        + data[i].dept_cd + "> <div>";
                    str += "<input type='hidden' value='" + data[i].leaf_yn
                        + "' id='hiddenLeaf_" + data[i].dept_cd + "'/>";
                    str += "<input type='hidden' value='"
                        + data[i].org_level + "' id='hiddenLevel_"
                        + data[i].dept_cd + "'/>";
                    str += "<input type='hidden' value='" + data[i].comp_cd
                        + "' id='hiddenCompCd_" + data[i].dept_cd
                        + "'/>";
                    str += "<input type='hidden' value='open' id=openYn_"
                        + data[i].dept_cd + ">";
                    str += "<input type='hidden' value='" + data[i].dept_cd
                        + "' id='hiddenDeptCd_" + data[i].dept_cd
                        + "'/>";
                    str += "<input type='hidden' value='false' id=deptClickFlag_"
                        + data[i].dept_cd + ">";

                    if (data[i].org_level > 2) {
                        var plus = ((data[i].org_level * 5) + (data[i].org_level - 2) * 10) - 10;
                        str += "<span  id=deptNmText_" + data[i].dept_cd + " style=margin-left:" + plus + "px; class=tree-list ";
                        if (data[i].leaf_yn != 'Y') {
                            str += " onclick=clickDept('" + data[i].dept_cd + "');>";
                        } else {
                            str += ">";
                        }
                        str += "<img src='/anymobi/img/icon_node.png'> &nbsp;" + data[i].org_nm;
                    } else {
                        var plus = ((data[i].org_level * 5) + (data[i].org_level - 2) * 10) - 10;
                        str += "<span  id=deptNmText_" + data[i].dept_cd + " style=margin-left:" + plus + "px; class=tree-list ";
                        if (data[i].leaf_yn != 'Y') {
                            str += " onclick=clickDept('" + data[i].dept_cd + "');>";
                        } else {
                            str += ">";
                        }
                        str += "<img src='/anymobi/img/icon_node.png'> &nbsp;" + data[i].org_nm;
                    }
                    str += "</span></div></li>";
                }// end for
                str += "</ul>";
                $("#li_" + evData.temp).append(str);
                $("#deptClickFlag_" + evData.temp).val(
                    "true");
                for (var i = 0; i < data.length; i++) {
                    if (data[i].leaf_yn == 'Y') {
                        $("#deptNmText_" + data[i].dept_cd).css("color",
                            "#ACACAC");
                        $("#deptNmText_" + data[i].dept_cd).addClass("leaf");
                    }
                }

                //////////////////////////////////////////////////////////////////
                // 오른쪽 영역에 데이터 추가 START
                $("#empSearchList").empty();
                var str = "<input type='hidden' value=" + reqData.dataCode
                    + " id='dataCode'/>";
                if (data.length == 1) {
                    str += "<li class='t-body-list' id='topNoData'><div class='td' ><span style='width:200px'>데이터가 없습니다.</span></div></li>";
                    //pagingDept("", "", "", "");
                    $("#empSearchList").append(str);
                    return;
                }
                for (var i = 0; i < data.length; i++) {
                    str += " <li class='t-body-list'>";
                    str += " <div class='td part'><div class='inpt'>";
                    str += " <input type='checkbox' id=userTopTbChk_"
                        + (i + 1) + " value=" + data[i].dept_cd
                        + " name=userTopChk >";
                    str += "<label for=userTopTbChk_" + (i + 1)
                        + "><span id=tbDeptNM_" + data[i].dept_cd + " style='text-align:left;'>"
                        + data[i].org_full_nm
                        + "</span></label></div></div>";
                    str += "</li>";
                }
                $("#empSearchList").append(str);
                /*pagingDept(data[data.length - 1].paging, reqData.dataCode,
                        '2', reqData.curPage);*/
            } else {
                //마지막 부서일 때 플래그 무조건 false 20.12.10. 수정
                $("#deptClickFlag_" + evData.temp).val("false");
            }

        }, beforeSend: function () {
            $("#cover-spin").css('display', 'block');
            $(".search-form").css('display', 'none');
            $(".table-wrap").css('display', 'none');

        }, complete: function () {
            $("#cover-spin").css('display', 'none');
            $(".search-form").css('display', '');
            $(".table-wrap").css('display', '');
        }// success
    });// ajax
}

function getDeptDetailList(reqData) {
    $("#deptBtnClick").val("Y");
    $.ajax({
        url: '/getDeptDetailSearchAjax.do',
        type: 'POST',
        data: JSON.stringify(reqData),
        dataType: "json",
        error: function (request, status, error) {
            //console.log("code:" + request.status + " error:" + error);
        },
        success: function (data) {

            $("#empSearchList").empty();
            var str = "<input type='hidden' value=" + reqData.dataCode
                + " id='dataCode'/>";
            if (data.length == 1) {
                str += "<li class='t-body-list' id='topNoData'><div class='td' ><span style='width:200px'>데이터가 없습니다.</span></div></li>";
                //pagingDept("", "", "", "");
                $("#empSearchList").append(str);
                return;
            }
            for (var i = 0; i < data.length - 1; i++) {
                str += " <li class='t-body-list'>";
                str += " <div class='td part'><div class='inpt'>";
                str += " <input type='checkbox' id=userTopTbChk_"
                    + (i + 1) + " value=" + data[i].dept_cd
                    + " name=userTopChk >";
                str += "<label for=userTopTbChk_" + (i + 1)
                    + "><span id=tbDeptNM_" + data[i].dept_cd + " style='text-align:left;'>"
                    + data[i].org_full_nm
                    + "</span></label></div></div>";
                str += "</li>";
            }
            pagingDept(data[data.length - 1].paging, reqData.dataCode,
                '2', reqData.curPage);
            $("#empSearchList").append(str);

        }, beforeSend: function () {
            $("#cover-spin").css('display', 'block');
            $(".search-form").css('display', 'none');
            $(".table-wrap").css('display', 'none');

        }, complete: function () {
            $("#cover-spin").css('display', 'none');
            $(".search-form").css('display', '');
            $(".table-wrap").css('display', '');
        }
    });
}

function pageDept(data, dataCode) {
    $(this).addClass("on");
    if (data < 0) {
        data = 0;
    }// end if
    var reqData = {
        curPage: data,
        dataCode: dataCode
    };
    getDeptDetailList(reqData);
}

// 추가
function addDept() {
    var chkValueArr = new Array();
    var downTbData = new Array();
    if ($("input:checkbox[name='userTopChk']:checked").length == 0) {
        layerAlert("추가하실 데이터를 선택해주세요.");
        return;
    }
    $("input:checkbox[name='userTopChk']:checked").each(
        function () {
            chkValueArr.push($(this).val());
        });
    $("input:checkbox[name='userDownChk']").each(
        function () {
            downTbData.push($(this).val());
        });

    var downLength = downTbData.length;
    if (alldownList.length != 0) {
        for (var i = 0; i < alldownList.length; i++) {
            for (var j = 0; j < chkValueArr.length; j++) {
                if (alldownList[i].dept_cd == chkValueArr[j]) {
                    layerAlert("동일한 데이터가 있습니다.");
                    return;
                }
            }
        }
    }
    var cutStr = "";
    var checkList = new Array();
    for (var i = 0; i < chkValueArr.length; i++) {
        var str = "";
        checkList[i] = {
            org_full_nm: $('#tbDeptNM_' + chkValueArr[i])
                .html(),
            dept_cd: chkValueArr[i]
        };
        str += "<li class='t-body-list' id=tr_"
            + chkValueArr[i] + ">";
        str += "<div class='td part'><div class='inpt' style='top:85%;'>";
        str += " <input type='checkbox' id=userDownTbChk_"
            + (downLength + (i + 1)) + " value="
            + chkValueArr[i] + " name=userDownChk>";
        str += "<label for=userDownTbChk_"
            + (downLength + (i + 1)) + "><span style='text-align:left;'>"
            + $('#tbDeptNM_' + chkValueArr[i]).html();
        +"</span></label>";
        str += "</div></div>";
        str += "</li>";
        cutStr = str;
        liList.push(cutStr);
        alldownList.push(checkList[i]);
    }
    pageNumClick(1);
    $("input:checkbox[name='userTopChk']").prop("checked", false);
}

// 전체 추가
function addAllDept() {
    var downTbData = new Array();
    var reqData = {};
    var ajaxurl = "";

    $("input:checkbox[name='userTopChk']").prop("checked", false);
    $("input:checkbox[name='userDownChk']").each(
        function () {
            downTbData.push($(this).val());
        });

    if ($("#deptBtnClick").val() == "Y") {
        $("#downNoData").remove();
        ajaxurl = "/getDeptDetailSearchAjax.do";
        reqData = {
            dataCode: $("#dataCode").val(),
            allnum: "all",
            curPage: 1
        };
    }
    if (ajaxurl != "") {
        $.ajax({
            url: ajaxurl,
            type: 'POST',
            data: JSON.stringify(reqData),
            error: function (request, status, error) {
                /*console.log("code:" + request.status
                        + " error:" + error);*/
            },
            success: function (data) {
                var ovList = new Array();
                if (alldownList.length != 0) {
                    for (var i = 0; i < data.length - 1; i++) {
                        for (var j = 0; j < alldownList.length; j++) {
                            if (alldownList[j].dept_cd == data[i].dept_cd) {// 중복인 데이터
                                alldownList.splice(j, 1);
                            }
                        }
                    }
                }
                for (var i = 0; i < alldownList.length; i++) {
                    ovList.push(alldownList[i]);
                }
                for (var i = 0; i < data.length - 1; i++) {
                    ovList.push(data[i]);
                }
                if (ovList.length > 0) {
                    var cutStr = "";
                    // 전체 데이터를 다시 넣기위해 초기화
                    liList = [];
                    alldownList = [];
                    for (var i = 0; i < ovList.length; i++) {
                        var str = "";
                        str += "<li class='t-body-list' id=tr_"
                            + ovList[i].dept_cd + ">";
                        str += "<div class='td part'><div class='inpt' style='top:85%;'>";
                        str += " <input type='checkbox' id=userDownTbChk_"
                            + (downTbData.length + i + 1)
                            + " value="
                            + ovList[i].dept_cd
                            + " name=userDownChk>";
                        str += "<label for=userDownTbChk_"
                            + (downTbData.length + i + 1)
                            + "><span style='text-align:left;'>"
                            + ovList[i].org_full_nm
                            + "</span></label>";
                        str += "</div></div>";
                        str += "</li>";
                        cutStr = str;
                        liList.push(cutStr);
                        alldownList.push(ovList[i]);
                    }
                    pageNumClick(1);
                } else {
                    noDataDept();
                }
            }// end
        });
    } else {
        noDataDept();
    }// ajaxurl!=""
}

// 삭제
function delDept() {
    var chkValueArr = new Array();
    if ($("input:checkbox[name='userDownChk']:checked").length == 0) {
        layerAlert("삭제하실 데이터를 선택해주세요.");
        return;
    }

    $("input:checkbox[name='userDownChk']:checked").each(
        function () {
            chkValueArr.push($(this).val());
        });

    for (var i = 0; i < alldownList.length; i++) {
        for (var j = 0; j < chkValueArr.length; j++) {
            if (alldownList[i].dept_cd == chkValueArr[j]) {
                alldownList.splice(i, 1);
            }
        }
    }// end for
    var cutStr = "";
    liList = [];
    for (var i = 0; i < alldownList.length; i++) {
        var str = "";
        str += "<li class='t-body-list' id=tr_"
            + alldownList[i].dept_cd + ">";
        str += "<div class='td part'><div class='inpt' style='top:85%;'>";
        str += " <input type='checkbox' id=userDownTbChk_"
            + (i + 1) + " value="
            + alldownList[i].dept_cd
            + " name=userDownChk>";
        str += "<label for=userDownTbChk_" + (i + 1)
            + "><span style='text-align:left;'>" + alldownList[i].org_full_nm
            + "</span></label>";
        str += "</div></div>";
        str += "</li>";
        cutStr = str;
        liList.push(cutStr);
    }
    var del = "";
    if (alldownList.length == 0) {
        $("#empChoiceList").empty();
        del = "<li class='t-body-list' id='downNoData' value='no'><div class='td' ><span style='width:200px'>데이터가 없습니다.</span></div></li>";
        $("#empChoiceList").append(del);
    }
    pageNumClick(1);
}

/* 아래테이블 페이징처리 */
function pageTemp(totalData, dataPerPage, pageCount, curPage) {
    $("#divPagedown").remove();
    var totalPage = Math.ceil(totalData / dataPerPage);
    var rangeCnt = Math.ceil(totalPage / pageCount);
    var curRange = Math.ceil(curPage / dataPerPage);
    var pageStr = "<div id='divPagedown'><div class='prev-btn'>";
    var first = (curRange - 1) * dataPerPage + 1;
    var last = first + dataPerPage - 1;
    if (rangeCnt == curRange) {
        last = totalPage;
    }
    if (last < pageCount) {
        first = 1;
    } else {//
        first = (curRange - 1) * dataPerPage + 1;
    }
    var next = curPage + 1;
    var prev = curPage - 1;

    if (prev < 0 || prev == 0) {
        prev = 0;
        curPage = 1;
        next = 2;
    }
    pageStr += "<button type='button' class='prev'  onclick=pageNumClick("
        + prev + ")></button>";
    pageStr += "</div> <ul class='pagenation-ul'>";

    for (var i = first; i <= last; i++) {
        if (curPage == i) {
            pageStr += "<li class='on'>";
        } else {
            pageStr += "<li>";
        }// end if
        pageStr += " <span onclick=pageNumClick(" + i + ");>" + i
            + "</span></a></li>";
    }// end for
    pageStr += "</ul> <div class=next-btn>";

    if (curPage == totalPage) {
        next = last;
    }
    pageStr += " <button type='button' class='next' onclick=pageNumClick("
        + next + ")></button>";
    pageStr += "</div></div>";
    $("#divDownPage").append(pageStr);
}

/* 아래테이블 현재페이지가 입력되면 페이징 처리 */
function pageNumClick(num) {
    $(this).addClass("on");
    if (num == 0) {
        num = 1;
    }
    var start = ((num - 1) * 5) + 1;
    var end = num * 5;
    var strTotal = "";
    for (var i = start - 1; i < end; i++) {
        if (liList[i] != undefined) {
            strTotal += liList[i];
        }
    }
    if (liList.length != 0) {
        $("#empChoiceList").empty();
        pageTemp(liList.length, 5, 5, num);
        $("#empChoiceList").append(strTotal);
    }
}

// 선택등록
function choiceDept() {
    var str = "";
    var str2 = "";
    for (var i = 0; i < alldownList.length; i++) {
        var dept_nm = alldownList[i].org_full_nm.replace("&gt;", ">");
        str += dept_nm + "\n";
    }

    for (var i = 0; i < alldownList.length; i++) {
        var deptCd = alldownList[i].dept_cd;
        if (alldownList[i].dept_nm == "KT") {
            str2 += "1001,";
        } else {
            str2 += "1001." + deptCd + ",";
        }

    }
    $("#eduOrgCdNm").val(str);
    $("#eduOrgCdList").val(str2);
    closeDeptBtn();
}

function closeDeptBtn() { // 초기화
    $('.l-popup').hide();
    $("body").removeClass("no-scroll");
    $("#empSearchList").html("<li class='t-body-list' id='downNoData'><div class='td'><span style='width:200px'>데이터가 없습니다.</span></div></li>");
    //noDataDept();
}

function noDataDept() {
    var noData = "<li class='t-body-list' id='downNoData'><div class='td' ><span style='width:200px'>데이터가 없습니다.</span></div></li>";
    $("#empChoiceList").html(noData);
}