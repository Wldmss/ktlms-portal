function isMobile() {
    var result = false;
    if (navigator.userAgent.indexOf('Mobile') > -1) {
        result = true;
    }
    return result;
}

$(function () {
    $('.condition-btn').popup({
        target: $('.l-popup.condition')
    });
    $('.learning-btn').popup({
        target: $('.l-popup.learning')
    });
    if (!isMobile()) {
        $('.level-btn.share-btn').popup({
            target: $('.l-popup.share')
        });
    }

    $('.share .search').popup({
        target: $('.l-popup.member-seach')
    });

    $('.member-seach .search').popup({
        target: $('.l-popup.share-object')
    });
    $('.share-object .research').popup({
        target: $('.l-popup.member-seach')
    });
    $('.btn.share-preview').popup({
        target: $('.l-popup.share-preview')
    });
    $('.btn.select-list').popup({
        target: $('.l-popup.share-object')
    });

});

var contId = "";
var jobCd = "";
var liList = new Array(); // 아래테이블의 li 리스트
var alldownList = new Array(); // 아래테이블의 모든 데이터
function copyClibBoard(text) {
    var temp = document.createElement("textarea");
    document.body.appendChild(temp);
    temp.value = text;
    temp.select();
    document.execCommand("copy");
    document.body.removeChild(temp);
    layerAlert("URL 복사가 완료되었습니다.");
}

$(document).on('click', '.level-btn.share-btn', function () {
    contId = $(this).find(".share-content").val();
    jobCd = $(this).find(".share-job").val();
    if (isMobile()) {
        var url = location.host + "";
        url = "http://" + url + "/mobile/m/educontents/miniLecture/miniLectureDetail.do?contId=" + contId;
        copyClibBoard(url);
    } else {
        contShareBtn(contId, jobCd);
    }
});

//공유 내용 get
$(document).on('click', '.btn.share-preview', function () {
    var userList = "";
    for (var i = 0; i < alldownList.length; i++) {
        if (i < alldownList.length - 1) {
            userList += alldownList[i].user_id + ",";
        } else {
            userList += alldownList[i].user_id;
        }
    }

    contId = $("#shareContId").val();
    var jobCode = $("#shareJobCd").val();
    var map = {};
    map["jobCd"] = jobCode;
    map["contId"] = contId;
    map["rcvUserIdList"] = userList;


    if ((null != contId || "" != contId) && (null != userList || "" != userList)) {
        $.ajax({
            url: '/getShareContentInfoAjax.do',
            type: 'POST',
            dataType: "json",
            data: JSON.stringify(map),
            beforeSend: function (xhr) {
                xhr.setRequestHeader("AJAX", "true");
            },
            error: function (xhr, status, error) {
                if (xhr.status == 999) {
                    setTimeout(function () {
                        alert('세션이 만료되었습니다. \n로그인 화면으로 이동합니다.');
                        location.href = '/login.do';
                    }, 100);
                }
            },
            success: function (data) {
                if (data.contInfo != null) {
                    //var msgText = data.contInfo.msgText.replace("\n", "<br>");
                    //msgText = msgText.replace("${바로가기}", "");
                    var msgText = data.contInfo.msgText.replace("${바로가기}", "");
                    $("#sharePopPreview").html(msgText);
                    $("#sharePopPreview").attr("readonly", true);
                    var shareUrl = "";
                    /*if(data.contInfo.contTypeCd == 'MICRO'){
                        shareUrl = '/educontents/miniLecture/miniLectureDetail.do?contId='+inContId;
                    }else if(data.contInfo.contTypeCd == 'MPACK'){
                        shareUrl = '/educontents/miniLecturePack/miniLecturePackDetail.do?contId='+inContId;
                    }*/
                    $("#contNm").attr("href", shareUrl);
                }
            }
        });
    }
});

$(document).ready(function () {
    studyTime('.condition .percent', '.condition .round-graph1');
});

/* 강좌공유하기 버튼을 클릭시 */
function contShareBtn(inContId, inJobCd) {
    var jobCd = "MICRO_SHARE";
    if (inJobCd) jobCd = inJobCd;
    $("#shareFirst").attr("style", "display:block;");
    liList = [];
    alldownList = [];
    $("label#contNm").val(inContId);
    $("#shareContId").val(inContId);
    $("#shareJobCd").val(jobCd);
}

/* 최상위 부서 조회 */
function memberSeach() {
    $.ajax({
        url: '/getSearchRootDeptAjax.do',
        type: 'POST',
        dataType: "json",
        error: function (xhr) {
            //console.log(xhr.status + " " + xhr.statusText);
        },
        success: function (data) {
            var str = "";
            for (var i = 0; i < data.length; i++) {
                var classNm = 'tree';
                if (data[i].leaf_yn == 'Y') {
                    classNm += ' leaf';
                }
                str += "<li id=li_" + i + "  class='tree-list on'>";
                //str += "<span class='tree' id=" + data[i].comp_cd
                str += "<span class='" + classNm + "' id=" + data[i].comp_cd
                    + " onclick=clickDeptSpan(" + i + ");>"
                    + data[i].org_nm;
                if (data[i].leaf_yn == 'N') {
                    /*str += "<img src='/newPortal/icons/tree-btn-on.png'  class='share-img' id=deptBtn_"*/
                    +i + " />";
                } else {
                    /*str += "<img src='/newPortal/icons/tree-btn.png'  class='share-img' id=deptBtn_"*/
                    +i + " />";
                }// end else
                str += "</span>";
                if (data[i].dept_cd == null) {
                    str += "<input type='hidden' value=" + i
                        + " id=hiddenDeptCd_" + i + ">";
                }// end if
                str += "<input type='hidden' value=" + data[i].comp_cd
                    + " id=hiddenCompCd_" + i + ">";
                str += "<input type='hidden' value="
                    + (data[i].org_level)
                    + " id=hiddenOrgLever_" + i + ">";
                str += "<input type='hidden' value=" + data[i].leaf_yn
                    + " id=hiddenLeafYN_" + data[i].comp_cd + ">";
                str += "<input type='hidden' value='false' id=deptClickFlag_"
                    + i + "_" + data[i].comp_cd + ">";
                str += "</li>";
            }// end for
            $("#rootUl").html(str);
        }
    });
}

/* 페이징 함수 */
function pagingKo(data, name, temp, curpage) {
    $("#divPageSub").remove();
    var ckEvent = "";
    if (temp == '1') {
        ckEvent = "pageNameClick";
    }
    if (temp == '2') {
        ckEvent = "pageDeptClick";
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
function clickDeptSpan(data) {
    $(".tree-list").not('.leaf').css('color', '#666666');
    $(".tree-list.leaf").css('color', '#ACACAC');
    $("#deptNmText_" + data).css('color', '#4E79B7');

    var comp = $("#hiddenCompCd_" + data).val();
    var dept = $("#hiddenDeptCd_" + data).val();
    var org_level = $("#hiddenOrgLever_" + data).val();
    var leaf = $("#hiddenLeafYN_" + comp).val();
    if ($("#deptClickFlag_" + data).val() == "true") {
        $("#ul_" + data + "_" + comp).remove();
        $("#deptClickFlag_" + data).val("false");
        return;
    }
    if ($("#deptClickFlag_" + data + "_" + comp).val() == "true") {
        $("#ul_" + data + "_" + comp).remove();
        $("#deptClickFlag_" + data + "_" + comp).val("false");
        return;
    }

    if (data == dept) {
        dept = "";
        leaf = $("#hiddenLeafYN_" + comp).val();
        org_level = $("#hiddenOrgLever_" + data).val();
        $("#deptClickFlag_" + data).val("true");
    } else {
        dept = data;
        comp = $("#hiddenCompCd_" + dept).val();
        leaf = $("#hiddenLeaf_" + dept).val();
        $("#deptClickFlag_" + data + "_" + comp).val("true");
        org_level = parseInt($("#hiddenLevel_" + dept).val()) + 1;
    }
    var evData = {
        temp: data,
        comp: comp
    };
    if (dept == null || dept == "") {
        dept = "";
    } else {
        dept = "." + dept;
    }

    var a = $("#deptBtn_" + data).attr("src");
    if (a == '/newPortal/icons/tree-btn-on.png') {
        if (leaf == 'N') {
            /*$("#deptBtn_" + data).attr("src",
                    "/newPortal/icons/tree-btn-on.png");*/
        } else {
            /*$("#deptBtn_" + data).attr("src", "/newPortal/icons/tree-btn.png");*/
        }
    }

    var deptData = {
        p_org_cd: comp + dept,
        org_level: org_level
    };
    var userData = {
        dataCode: comp + dept,
        curPage: 1
    };
    getDeptList(deptData, evData);
    if (data < 50) {
        return;
    } else {
        getUserList(userData);
    }

}

function getDeptList(reqData, evData) {
    $
        .ajax({
            url: '/getDeptCdLowerAjax.do',
            type: 'POST',
            data: JSON.stringify(reqData),
            error: function (xhr) {
                //console.log(xhr.status + " " + xhr.statusText);
            },
            success: function (data) {
                if (data.length > 0) {
                    var str = "<ul id=ul_"
                        + evData.temp
                        + "_"
                        + evData.comp
                        + "><input type='hidden' value='false' id='deptBtnClick'/>";
                    str += "<input type='hidden' value='false' id='deptClickFlag_"
                        + evData.temp + "_" + evData.comp + "'>";
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
                        if (data[i].org_level > 2) {
                            var plus = ((data[i].org_level * 5) + (data[i].org_level - 2) * 10) - 10;
                            str += "<span  id=deptNmText_" + data[i].dept_cd
                                + " style=margin-left:" + plus
                                /*+ "px; class=tree-chd" + 1*/
                                + "px; class=tree-list"
                                + " onclick=clickDeptSpan('"
                                + data[i].dept_cd + "');>";
                            str += "<img src='/anymobi/img/icon_node.png'> &nbsp;" + data[i].org_nm;
                        } else {
                            str += "<span  id=deptNmText_" + data[i].dept_cd
                                + " style=margin-left:3px; class=tree-chd"
                                + 1 + " onclick=clickDeptSpan('"
                                + data[i].dept_cd + "');>" + "<img src='/newPortal/icons/icon_node.png'> &nbsp;"
                                + data[i].org_nm;
                        }
                        if (data[i].leaf_yn != 'Y') {
                            /*str += "	<img src='/newPortal/icons/tree-btn-on.png' class='share-img' id=deptBtn_"
                                    + data[i].dept_cd + "/>";*/
                        } else {
                            /*str += "	<img src='/newPortal/icons/tree-btn.png' class='share-img' id=deptBtn_"
                                    + data[i].dept_cd + "/>";*/
                        }

                        str += "</span></div></li>";
                    }// end for
                    str += "</ul>";

                    $("#li_" + evData.temp).append(str);
                    $("#deptClickFlag_" + evData.temp + "_" + evData.comp).val(
                        "true");
                    for (var i = 0; i < data.length; i++) {
                        if (data[i].leaf_yn == 'Y') {
                            $("#deptNmText_" + data[i].dept_cd).css("color",
                                "#ACACAC");
                            $("#deptNmText_" + data[i].dept_cd).addClass("leaf");
                        }
                    }
                } else {
                    //마지막 부서일 때 플래그 무조건 false 20.12.10. 수정
                    $("#deptClickFlag_" + evData.temp + "_" + evData.comp).val("false");
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

function getUserList(reqData) {
    $("#deptBtnNameClick").val("false");
    $("#deptBtnClick").val("true");
    $
        .ajax({
            url: '/getDeptClickSearchAjax.do',
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
                    pagingKo("", "", "", "");
                    $("#empSearchList").append(str);
                    return;
                }
                for (var i = 0; i < data.length - 1; i++) {
                    str += " <li class='t-body-list'>";
                    str += " <div class='td part'><div class='inpt'>";
                    str += " <input type='checkbox' id=userTopTbChk_"
                        + (i + 1) + " value=" + data[i].user_id
                        + " name=userTopChk >";
                    str += "<label for=userTopTbChk_" + (i + 1)
                        + "><span id=tbDeptNM_" + data[i].user_id + " style='text-align:left;'>"
                        + data[i].dept_nm
                        + "</span></label></div></div>";
                    str += " <div class='td name'><span id=tbUserNm_"
                        + data[i].user_id + ">" + data[i].user_nm
                        + "</span></div>";
                    str += " <div class='td position'><span id=tbTitleNm_"
                        + data[i].user_id + ">" + data[i].title_nm
                        + "/" + data[i].lvl_nm + "</span></div>";
                    str += "<div class='td company'><span id=tbCompNm_"
                        + data[i].user_id + ">" + data[i].comp_nm
                        + "</span></div>";
                    str += "<input type='hidden' value='" + data[i].email
                        + "' id=email_" + data[i].user_id + ">";
                    str += "<input type='hidden' value='" + data[i].lvl_nm
                        + "' id=lvlNm" + data[i].user_id + ">";
                    str += "<input type='hidden' value='"
                        + data[i].title_nm + "' id=titleNm"
                        + data[i].user_id + ">";
                    str += "</li>";
                }
                $("#empSearchList").append(str);
                pagingKo(data[data.length - 1].paging, reqData.dataCode,
                    '2', reqData.curPage);

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

$(function () {
    $("#selectDeptNameBtn").click(
        function () {
            $("#deptBtnNameClick").val("true");
            $("#deptBtnClick").val("false");
            var reqData = {
                name: $("#deptUserName").val(),
                curPage: 1
            };

            if ($("#deptUserName").val() == ""
                || $("#deptUserName").val() == null) {
                layerAlert("성명을 입력해주세요.");
                return;
            }
            getDeptNameList(reqData);
        });
});

function pageNameClick(data, name) {
    $(this).addClass("on");
    if (data < 0) {
        data = 0;
    }// end if
    var reqData = {
        name: name,
        curPage: data
    };
    getDeptNameList(reqData);
}

function getDeptNameList(reqData) {
    var tr = {
        name: escape(encodeURIComponent(reqData.name)),
        curPage: reqData.curPage
    };
    $
        .ajax({
            url: '/getDeptNmSearchAjax.do',
            type: 'POST',
            data: JSON.stringify(tr),
            contenType: "application/x-www-form-urlencoded; charset=euc-kr",
            error: function (request, status, error) {
                //console.log("code:" + request.status + " error:" + error);
            },
            success: function (data) {
                var str = "";
                $("#empSearchList").empty();
                if (data.length == 1) {
                    str += "<li class='t-body-list' id='topNoData'><div class='td' ><span style='width:200px'>데이터가 없습니다.</span></div></li>";
                    pagingKo("", "", "", "");
                    $("#empSearchList").append(str);
                    return;
                }// end if
                for (var i = 0; i < data.length - 1; i++) {
                    str += "<li class='t-body-list'>";
                    str += " <div class='td part'><div class='inpt'>";
                    str += " <input type='checkbox' id=userTopTbChk_"
                        + (i + 1) + " value=" + data[i].user_id
                        + " name=userTopChk>";
                    str += "<label for=userTopTbChk_" + (i + 1)
                        + "><span id=tbDeptNM_" + data[i].user_id + ">"
                        + data[i].dept_nm
                        + "</span></label></div></div>";
                    str += " <div class='td name'><span id=tbUserNm_"
                        + data[i].user_id + ">" + data[i].user_nm
                        + "</span></div>";
                    str += " <div class='td position'><span id=tbTitleNm_"
                        + data[i].user_id + ">" + data[i].title_nm
                        + "/" + data[i].lvl_nm + "</span></div>";
                    str += "<div class='td company'><span id=tbCompNm_"
                        + data[i].user_id + ">" + data[i].comp_nm
                        + "</span></div>";
                    str += "<input type='hidden' value='" + data[i].email
                        + "' id=email_" + data[i].user_id + ">";
                    str += "<input type='hidden' value='" + data[i].lvl_nm
                        + "' id=lvlNm" + data[i].user_id + ">";
                    str += "<input type='hidden' value='"
                        + data[i].title_nm + "' id=titleNm"
                        + data[i].user_id + ">";
                    str += "</li>";
                }
                $("#empSearchList").append(str);
                pagingKo(data[data.length - 1].paging, reqData.name, '1',
                    reqData.curPage);
                //$("#deptUserName").val("");
            }, beforeSend: function () {
                $("#cover-spin").css('display', 'block');
                $(".search-form").css('display', 'none');
                $(".table-wrap").css('display', 'none');

            }, complete: function () {
                $("#cover-spin").css('display', 'none');
                $(".search-form").css('display', '');
                $(".table-wrap").css('display', '');
            }// end
        });
}

function pageDeptClick(data, dataCode) {
    $(this).addClass("on");
    if (data < 0) {
        data = 0;
    }// end if
    var reqData = {
        curPage: data,
        dataCode: dataCode
    };
    getUserList(reqData);
}

$(function () {
    /* 추가버튼을 클릭시 */
    $("#addDeptBtn")
        .click(
            function () {
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
                $("#downNoData").remove();
                if (alldownList.length != 0) {
                    for (var i = 0; i < alldownList.length; i++) {
                        for (var j = 0; j < chkValueArr.length; j++) {
                            if (alldownList[i].user_id == chkValueArr[j]) {
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
                        title_nm: $('#titleNm' + chkValueArr[i]).val(),
                        email: $('#email_' + chkValueArr[i]).val(),
                        dept_nm: $('#tbDeptNM_' + chkValueArr[i])
                            .html(),
                        lvl_nm: $('#lvlNm' + chkValueArr[i]).val(),
                        r: 1,
                        user_id: chkValueArr[i],
                        user_nm: $('#tbUserNm_' + chkValueArr[i])
                            .html(),
                        comp_nm: $('#tbCompNm_' + chkValueArr[i])
                            .html()
                    };
                    str += "<li class='t-body-list' id=tr_"
                        + chkValueArr[i] + ">";
                    str += "<div class='td part'><div class='inpt' style='top:85%;'>";
                    str += " <input type='checkbox' id=userDownTbChk_"
                        + (downLength + (i + 1)) + " value="
                        + chkValueArr[i] + " name=userDownChk>";
                    str += "<label for=userDownTbChk_"
                        + (downLength + (i + 1)) + "><span>"
                        + $('#tbDeptNM_' + chkValueArr[i]).html();
                    +"</span></label>";
                    str += "</div></div>";
                    str += " <div class='td name'><span id=downName_"
                        + chkValueArr[i] + ">"
                        + $('#tbUserNm_' + chkValueArr[i]).html();
                    str += "</span></div>";
                    str += " <div class='td position'><span id=downPosition_"
                        + chkValueArr[i]
                        + ">"
                        + $('#tbTitleNm_' + chkValueArr[i]).html();
                    str += "</span></div>";
                    str += " <div class='td company'><span id=downCompany_"
                        + chkValueArr[i]
                        + ">"
                        + $('#tbCompNm_' + chkValueArr[i]).html();
                    str += "</span></div>";
                    str += "<input type='hidden' id=email_"
                        + chkValueArr[i] + " value="
                        + $('#email_' + chkValueArr[i]).val();
                    str += "/></li>";
                    cutStr = str;
                    liList.push(cutStr);
                    alldownList.push(checkList[i]);
                }
                pageNumClick(1);
                $("input:checkbox[name='userTopChk']").prop("checked",
                    false);
            });

    /* 삭제버튼을 클릭시 */
    $("#delDeptBtn")
        .click(
            function () {
                var chkValueArr = new Array();
                if ($("input:checkbox[name='userDownChk']:checked").length == 0) {
                    layerAlert("삭제하실 데이터를 선택해주세요. ");
                    return;
                }
                $("input:checkbox[name='userDownChk']:checked").each(
                    function () {
                        chkValueArr.push($(this).val());
                    });
                for (var i = 0; i < alldownList.length; i++) {
                    for (var j = 0; j < chkValueArr.length; j++) {
                        if (alldownList[i].user_id == chkValueArr[j]) {
                            alldownList.splice(i, 1);
                        }
                    }
                }// end for

                var cutStr = "";
                liList = [];
                for (var i = 0; i < alldownList.length; i++) {
                    var str = "";
                    str += "<li class='t-body-list' id=tr_"
                        + alldownList[i].user_id + ">";
                    str += "<div class='td part'><div class='inpt' style='top:85%;'>";
                    str += " <input type='checkbox' id=userDownTbChk_"
                        + (i + 1) + " value="
                        + alldownList[i].user_id
                        + " name=userDownChk>";
                    str += "<label for=userDownTbChk_" + (i + 1)
                        + "><span>" + alldownList[i].dept_nm
                        + "</span></label>";
                    str += "</div></div>";
                    str += " <div class='td name'><span id=downName_"
                        + alldownList[i].user_id + ">"
                        + alldownList[i].user_nm;
                    str += "</span></div>";
                    str += " <div class='td position'><span id=downPosition_"
                        + alldownList[i].user_id
                        + ">"
                        + alldownList[i].title_nm
                        + "/"
                        + alldownList[i].lvl_nm;
                    str += "</span></div>";
                    str += " <div class='td company'><span id=downCompany_"
                        + alldownList[i].user_id
                        + ">"
                        + alldownList[i].comp_nm;
                    str += "</span></div>";
                    str += "<input type='hidden' value="
                        + alldownList[i].email + "/>";
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
            });
    /* 전체선택을 클릭시 */
    $("#allAddBtn")
        .click(
            function () {
                var downTbData = new Array();
                var code = $("input:checkbox[name='userTopChk']").val();
                var reqData = {};
                var ajaxurl = "";

                $("input:checkbox[name='userTopChk']").prop("checked",
                    false);
                $("input:checkbox[name='userDownChk']").each(
                    function () {
                        downTbData.push($(this).val());
                    });


                if ($("#deptBtnClick").val() == "true") {
                    $("#downNoData").remove();
                    ajaxurl = "/getDeptClickSearchAjax.do";
                    reqData = {
                        dataCode: $("#dataCode").val(),
                        allnum: "all",
                        curPage: 1
                    };
                } else if ($("#deptBtnNameClick").val() == "true") {
                    $("#downNoData").remove();
                    ajaxurl = "/getDeptNmSearchAjax.do";
                    reqData = {
                        name: escape(encodeURIComponent($("#tbUserNm_" + code).html())),
                        allnum: "all",
                        curPage: 1
                    };
                }
                if (ajaxurl != "") {
                    $
                        .ajax({
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
                                            if (alldownList[j].user_id == data[i].user_id) {// 중복인
                                                // 데이터
                                                alldownList
                                                    .splice(j, 1);
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
                                            + ovList[i].user_id + ">";
                                        str += "<div class='td part'><div class='inpt' style='top:85%;'>";
                                        str += " <input type='checkbox' id=userDownTbChk_"
                                            + (downTbData.length + i + 1)
                                            + " value="
                                            + ovList[i].user_id
                                            + " name=userDownChk>";
                                        str += "<label for=userDownTbChk_"
                                            + (downTbData.length + i + 1)
                                            + "><span>"
                                            + ovList[i].dept_nm
                                            + "</span></label>";
                                        str += "</div></div>";
                                        str += " <div class='td name'><span id=downName_"
                                            + ovList[i].user_id
                                            + ">"
                                            + ovList[i].user_nm;
                                        str += "</span></div>";
                                        str += " <div class='td position'><span id=downPosition_"
                                            + ovList[i].user_id
                                            + ">"
                                            + ovList[i].title_nm
                                            + "/"
                                            + ovList[i].lvl_nm;
                                        str += "</span></div>";
                                        str += " <div class='td company'><span id=downCompany_"
                                            + ovList[i].user_id
                                            + ">"
                                            + ovList[i].comp_nm;
                                        str += "</span></div>";
                                        str += "<input type='hidden' value="
                                            + ovList[i].email + "/>";
                                        str += "</li>";
                                        cutStr = str;
                                        liList.push(cutStr);
                                        alldownList.push(ovList[i]);
                                    }
                                    pageNumClick(1);
                                } else {
                                    noData();
                                }
                            }// end
                        });
                } else {
                    noData();
                }// ajaxurl!=""
            });
});

function noData() {
    var noData = "<li class='t-body-list' id='downNoData'><div class='td' ><span style='width:200px'>데이터가 없습니다.</span></div></li>";
    $("#empChoiceList").html(noData);
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

$(function () {
    /* 선택등록 버튼을 클릭시 */
    $("#choiceShareBtn").click(
        function () {

            $(".rowList").remove();
            var str = "";
            for (var i = 0; i < alldownList.length; i++) {
                str += "<li class='member-list rowList' >";
                str += "<span class='member'>" + alldownList[i].user_nm;
                str += "(" + alldownList[i].comp_nm;
                str += "," + alldownList[i].dept_nm;
                str += ")</span>";
                str += "<input type='hidden' value=" + contId
                    + " id=userContId_" + i + ">";
                str += "</li>";
            }
            $("#choiceDeptList").append(str);
        });

});

/* 공유버튼을 클릭시 */
function shareSubmit() {
    if (alldownList.length == 0) {
        layerAlert("공유할 대상을 선택해주세요.");
        return;
    }
    var userList = "";
    for (var i = 0; i < alldownList.length; i++) {
        /*var reqData = {
            map1 : alldownList[i].user_nm,
            map2 : $("#userContId_" + i).val(),
            email : alldownList[i].email,
            toId : alldownList[i].user_id
        };*/
        if (i < alldownList.length - 1) {
            userList += alldownList[i].user_id + ",";
        } else {
            userList += alldownList[i].user_id;
        }

    }
    //console.log(reqData);
    //console.log(userList);

    var msgText = $("#sharePopPreview").val() + "\n${바로가기}";
    var map = {
        //contId : $("#userContId_" + 0).val(),
        jobCd: $("#shareJobCd").val(),
        contId: $("#shareContId").val(),
        userList: userList,
        inMsgText: escape(encodeURIComponent(msgText))
    };

    $.ajax({
        url: '/shareMailAjax.do',
        type: 'POST',
        async: false,
        data: JSON.stringify(map),
        contenType: "application/x-www-form-urlencoded; charset=euc-kr",
        error: function (xhr) {
            //console.log(xhr.status + " " + xhr.statusText);
        },
        success: function (data) {
            layerAlert2("강좌가 공유되었습니다.", closeBtn);

        }
    });
    //closeBtn();
}

function fn_callback() {
    $("#sharePop").attr("style", "display:none;");
    $("#sharePopPreview").attr("style", "display:none;");
//	top.document.location.reload();
}

function closeBtn() { // 초기화
    $('.l-popup').hide();
    $("body").removeClass("no-scroll");
    $("#empSearchList").html("<li class='t-body-list' id='downNoData'><div class='td' ><span style='width:200px'>데이터가 없습니다.</span></div></li>");
    noData();
    $(".l-member-seach .pagenation").remove();
    $("#deptUserName").val("");
}