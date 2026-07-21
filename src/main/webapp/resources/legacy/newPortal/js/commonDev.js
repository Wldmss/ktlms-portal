function existy(x) {
    return x != null
}

function truthy(x) {
    return (x !== false) && existy(x)
}

function doWhen(cond, action) {
    if (truthy(cond)) {
        return action();
    } else {
        return undefined;
    }
}

function executeIfHasField(target, name) {
    return doWhen(existy(target[name]), function () {
        var result = _.result(target, name);
        console.log(['the result is ', result].join(' '));
        return result;
    });
}

function naiveNth(a, index) {
    return a[index];
}

function isIndexed(data) {
    return _.isArray(data) || _.isString(data);
}

function nth(a, index) {
    if (!_.isNumber(index)) fail("Excepted a number as the index");
    if (!isIndexed(a)) fail("Not supperted on non-indexed type");
    if ((index < 0) || (index < a.length - 1))
        fail("Index value is out of bounds");

    return a[index];
}

function fail(thing) {
    console.log(["FAIL:", thing].join(' '));
    throw new Error(thing);
}

function warn(thing) {
    console.log(["WARNING:", thing].join(' '));
}

function note(thing) {
    console.log(["NOTE:", thing].join(' '));
}

function cat() {
    var head = _.first(arguments);
    if (existy(head)) {
        return head.concat.apply(head, _.rest(arguments));
    } else {
        return [];
    }
}

function mapcat(fun, coll) {
    return cat.apply(null, _map(coll, fun));
}


// ----------------------------------------
// 강사관리 공통함수
// ----------------------------------------

// 날짜 포맷팅 : yyyy.mm.dd
var toDate = function (mmDDYYY) {
    if (undefined == mmDDYYY)
        return "..";
    var yyyy = mmDDYYY.substring(0, 4);
    var mm = mmDDYYY.substring(4, 6);
    var dd = mmDDYYY.substring(6, 8);
    return yyyy + "." + mm + "." + dd;
}

// replaceAll 
String.prototype.trim = function () {
    return this.replace(/(^\s*)|(\s*$)/gi, "");
}

String.prototype.replaceAll = function (str1, str2) {
    var temp_str = "";

    if (this.trim() != "" && str1 != str2) {
        temp_str = this.trim();

        while (temp_str.indexOf(str1) > -1) {
            temp_str = temp_str.replace(str1, str2);
        }
    }

    return temp_str;
}

var ajaxAageExecutor = {
    getFirstPageOfIndex: function (index, pagesPerIndex) {
        return (index - 1) * pagesPerIndex + 1;
    },
    getIndexOfPage: function (pages, pagesPerIndex) {
        return Number(Math.ceil(Number(pages) / pagesPerIndex));
    },
    getTargetPage: function (targetIndex, pagesPerIndex) {
        return this.getFirstPageOfIndex(targetIndex, pagesPerIndex);
    },
    showPage: function (page, url) {
        if (url == "spec") {
            this.getAjaxPageList('/adm/tutorManage/getAjaxTutorSpecPageList.do', page, getSpecListDraw);
        } else if (url == "in") {
            this.getAjaxPageList('/adm/tutorManage/getAjaxTutorInlectPageList.do', page, getInlecListDraw);
        } else if (url == "out") {
            this.getAjaxPageList('/adm/tutorManage/getAjaxTutorOutlectPageList.do', page, getOutlecListDraw);
        } else if (url == "mod") {
            this.getAjaxPageList('/adm/tutorManage/getAjaxTutorModhisPageList.do', page, getModhisDraw);
        }
    },
    generatePageIndexes: function (data, url) {

        // page : 현재페이지
        // pages : 페이지수
        // pagesPerIndex : index 당 페이지 설정 값.

        var retStr = new StringBuffer();

        if (data.count == 0)
            return retStr.toString();

        var p = data.paging;
        var pagesPerIndex = p.pagesPerIndex;
        var page = p.page;
        var totalCount = data.count;
        var pages = p.pages
        var index = this.getIndexOfPage(page, pagesPerIndex);
        var startPage, endPage, indexes;


        if (pages < 1) {
            return retStr.toString();
        }

        if (page > 0) {
            startPage = this.getFirstPageOfIndex(index, pagesPerIndex);
            endPage = this.getFirstPageOfIndex(index + 1, pagesPerIndex);
            endPage--;
            indexes = this.getIndexOfPage(pages, pagesPerIndex);
        }

        retStr.append("<div class=\"cc-paging nbg-reg\">").append("\n");

        var MOVE_FIRST_PAGE = "<img src='/anymobi/img/btn_page_first.png' />"; // <<
        var MOVE_END_PAGE = "<img src='/anymobi/img/btn_page_end.png' />"; // >>
        var MOVE_BEFORE_PAGE = "<img src='/anymobi/img/btn_page_prev.png' />"; // <
        var MOVE_NEXT_PAGE = "<img src='/anymobi/img/btn_page_next.png' />"; // >

        // 처음으로
        if (page > 1) { // btn_page_first.png
            retStr.append("<a href='javascript:ajaxAageExecutor.showPage(" + 1 + ",\"" + url + "\")' title='처음으로'>" + MOVE_FIRST_PAGE + "</a>");
        } else {
            retStr.append("<a href='javascript:ajaxAageExecutor.showPage(" + 1 + ",\"" + url + "\")' title='처음으로'>" + MOVE_FIRST_PAGE + "</a>");
        }

        // 이전으로
        var targetIndex = index - 1;
        if (targetIndex > 0) {
            var targetPage = this.getTargetPage(targetIndex, pagesPerIndex);

            retStr.append("<a href='javascript:ajaxAageExecutor.showPage(" + targetPage + ",\"" + url + "\")' title='이전' class=\"mCS_img_loaded\" >" +
                MOVE_BEFORE_PAGE +
                "</a>");
        } else {
            retStr.append("<a href='javascript:void(0)' title='이전' class=\"mCS_img_loaded\" >" +
                MOVE_BEFORE_PAGE +
                "</a>");
        }

        retStr.append("\n");

        // 숫자 1.....10
        if (startPage > 10) {
            for (var targetPage = 1; 3 > targetPage; targetPage++) {
                retStr.append("<a href='javascript:ajaxAageExecutor.showPage(" + targetPage + ",\"" + url + "\")' class=\"num\">" + targetPage + "</a>");
            }
            retStr.append("...");
        }
        for (var targetPage = startPage; targetPage < endPage; targetPage++) {
            if (targetPage <= pages) {

                if (page == targetPage) {
                    retStr.append("<a href='javascript:ajaxAageExecutor.showPage(" + targetPage + ",\"" + url + "\")' class=\"num on\">" + targetPage + "</a>");
                } else {
                    retStr.append("<a href='javascript:ajaxAageExecutor.showPage(" + targetPage + ",\"" + url + "\")' class=\"num\">" + targetPage + "</a>");
                }
            }
        }
        if (endPage < pages - 1) {
            retStr.append("...");
            for (targetPage = pages - 1; targetPage <= pages; targetPage++) {
                retStr.append("<a href='javascript:ajaxAageExecutor.showPage(" + targetPage + ",\"" + url + "\")' class=\"num\">" + targetPage + "</a>");
            }
        }
        retStr.append("\n");

        // 다음
        var targetIndex = index + 1;
        if (targetIndex <= indexes) {
            var targetPage = this.getTargetPage(targetIndex, pagesPerIndex);
            retStr.append("<a href='javascript:ajaxAageExecutor.showPage(" + targetPage + ",\"" + url + "\")' title='다음' class=\"mCS_img_loaded\" >" +
                MOVE_NEXT_PAGE + "</a>");
        } else {
            retStr.append("<a href='javascript:void(0)' title='다음' class=\"mCS_img_loaded\" >" +
                MOVE_NEXT_PAGE +
                "</a>");
        }

        // 끝으로
        var targetPage = pages;
        if (page < targetPage) {
            retStr.append("<a href='javascript:ajaxAageExecutor.showPage(" + targetPage + ",\"" + url + "\")' title='마지막으로'>" +
                MOVE_END_PAGE + "</a>").append("\n");
        } else {
            retStr.append("<a href='#'>" + MOVE_END_PAGE + "</a>").append("\n");
        }

        retStr.append("</div>").append("\n");

        return retStr;
    },
    getAjaxPageList: function (url, page, callback) {
        var f = new fnAjax();
        $("#page").val(page);
        f.setUrl(url)
            .setData($("#frm").serializeObject())
            .setCallback(function (data) {
                callback(data);
            }).getCall();
    }
}

var StringBuffer = function () {
    this.buffer = new Array();
};
StringBuffer.prototype.append = function (str) {
    this.buffer.push(str);
    return this;
};
StringBuffer.prototype.toString = function () {
    return this.buffer.join('');
};

//강사료 숫자만 체크 가능.
function showKeyCode(event) {
    event = event || window.event;
    var keyID = (event.which) ? event.which : event.keyCode;
    if ((keyID >= 48 && keyID <= 57) || (keyID >= 96 && keyID <= 105)) {
        return;
    } else {
        return false;
    }
}

//validation rulesOpt set
function rulesOpt(rules, arrId) {
    var r = new Object();
    r.required = true;
    for (var i = 0; i < arrId.length; i++) {
        rules[arrId[i]] = r;
    }

}

// validation messages set
function msgOpt(rules, arrMsg) {
    var r = new Object();
    r.required = true;
    for (var i = 0; i < arrMsg.length; i++) {
        rules[arrMsg[i]] = r;
    }
}

// 동영상 파일 확장자 검증
function isFileExtCheck(callback, fileExtArr, f) {

    var thumbext = f.val(); //파일을 추가한 input 박스의 값

    // 파일을 선택하지 않으면 그냥 패스, 필수조건이 아님.
    if (thumbext == null || thumbext == "") {
        return true;
    }

    if (typeof fileExtArr != "object") {
        alert("fileExtArr 배열을 넘겨야 합니다.");
        return true;
    }

    thumbext = thumbext.slice(thumbext.indexOf(".") + 1).toLowerCase(); //파일 확장자를 잘라내고, 비교를 위해 소문자로 만듭니다.

    for (var i = 0; i < fileExtArr.length; i++) {
        var fileExt = fileExtArr[i];
        thumbext = thumbext.toUpperCase();
        fileExt = fileExt.toUpperCase();
        if (thumbext != fileExt) {
            if (typeof callbck == "function") {
                callback(false, f);
            } else {
                f.focus();
                alert(callback);
                return false;
            }
        }
    }

    return true;

}

// serializeObject encodeURIComponent 
$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();

    $.each(a, function () {

        var v = this.value;

        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(v || '');
        } else {
            o[this.name] = v || '';
        }
    });
    return o;
};

// json to string 변환
function fnCommonJsonToString(str) {
    if (str != null) {
        return JSON.stringify(str);
    } else {
        return "";
    }
}

//form 전송 함수
function formDataSend(url, frm, method) {
    frm.attr("method", method);
    frm.attr("action", url);
    frm.submit();
}

// 원시 ajax 함수를 함수형으로 감싼다.
function fnAjaxCall(data, type, url, dataType, callback) {
    $.ajax({
        url: url,
        type: type,
        dataType: dataType,
        data: data,
        complete: function (jqXHR, textStatus) {
        },
        success: function (data) {
            callback(data);
        }
    });
}

// fnAjax start --// 
var fnAjax = function () {
    this.url = "";
    this.data = "";
    this.type = "POST";
    this.dataType = "json";
    this.callback = "";
    this.call = "";
};

fnAjax.prototype.setUrl = function (url) {
    this.url = url;
    return this;
};

fnAjax.prototype.setData = function (data) {
    this.data = data;
    return this;
};

fnAjax.prototype.setType = function (type) {
    this.setType = type;
    return this;
};

fnAjax.prototype.setDataType = function (dataType) {
    this.dataType = dataType;
    return this;
};

fnAjax.prototype.setCallback = function (callback) {
    this.callback = callback;
    return this;
};

fnAjax.prototype.getCall = function () {
    fnAjaxCall(this.data, this.type, this.url, this.dataType, this.callback);
    return this;
};
// fnAjax end --// 

// 특수문자 체크
function checkFields_special() {
    var special = /[\<>$%'"]/gi;
    var special_str = ['src', 'eval', 'expression', 'script', 'onload', 'xss', 'alert', 'iframe', 'frameset', 'bgsound', 'onblur', 'onchange', 'onclick', 'ondblclick', 'enerror', 'onfocus', 'onmouse', 'onscroll', 'onsubmit', 'onunload', '&lt', '&gt', '&#60', '&#62', '&#34', '&#39', '&#37', '&#40', '&#41', '&amp', '&#38', '&#43'];
    var result = true;

    var formInput = document.getElementsByTagName("input");
    var formTextarea = document.getElementsByTagName("textarea");

    for (i = 0; i < formInput.length; i++) {
        if (formInput[i].type == "text") {
            if (special.test(formInput[i].value.trim())) {
                formInput[i].focus();
                alert(formInput[i].title + "에는 특수문자(<, >, $, %, \',\")를 사용할 수 없습니다.test1");
                //result = false;
                return result;
            }

            for (j = 0; j < special_str.length; j++) {
                if (formInput[i].value.trim().toLowerCase().indexOf(special_str[j]) > -1) {
                    formInput[i].focus();
                    alert(formInput[i].title + "에는 " + special_str[j] + " 문자를 사용할 수 없습니다.test2");
                    //result = false;
                    return result;
                }
            }
        }
    }

    for (i = 0; i < formTextarea.length; i++) {
        if (special.test(formTextarea[i].value.trim())) {
            formTextarea[i].focus();
            alert(formTextarea[i].title + "에는 특수문자(<, >, $, %, \',\")를 사용할 수 없습니다.test3");
            //result = false;
            return result;
        }

        for (j = 0; j < special_str.length; j++) {
            if (formTextarea[i].value.trim().toLowerCase().indexOf(special_str[j]) > -1) {
                formTextarea[i].focus();
                alert(formTextarea[i].title + "에는 " + special_str[j] + " 문자를 사용할 수 없습니다.test4");
                //result = false;
                return result;
            }
        }
    }
    return result;
}

// 로딩페이지 만들기
function loadMaker() {
    html = '<div class="loading-block" id="loading-block"><img src="/anymobi/adm/images/loading-1.gif" alt=""/><p>불러오는 중 입니다.</p></div>';
    $('body').append(html);
}