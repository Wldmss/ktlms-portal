/****************************************************
 * 자기개발관리 관련 스크립트
 *
 * 작성자: written by bkLove(최병국)
 * 작성일: 2016-01-25
 ****************************************************/

/*
 * 자기개발관리
 * 
 * 작성자: written by bkLove(최병국)
 * 작성일: 2016-01-25
 */
var ADM_SELF = {

    fn_tabView: function (selector) {

        try {

            if (selector && selector.length > 0) {
                var tabForm = jQuery("#tabForm");
                var action = "";

                switch (selector.attr("id")) {

                    /* 사용자 > 자기개발계획 수립 탭 */
                    case    "usrTab1":
                        action = "/selfDev/plan/usrSelfDevPlanList.do";
                        break;

                    /* 사용자 > 자기개발 실적조회 탭 */
                    case    "usrTab2":
                        action = "/selfDev/actual/usrSelfDevActualList.do";
                        break;

                    /* 사용자 > 개인학습 인정신청 탭 */
                    case    "usrTab3":
                        action = "/selfDev/personal/usrSelfDevPersonalList.do";
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

    , fn_numCheck: function (selector) {

        try {

            if (!selector
                || selector.length == 0
            ) {
                throw "인자값을 확인해 주세요";
            }

            if ("intOnly" == selector.attr("numtype")) {

                if (selector && selector.val()) {
                    if (!jQuery.isNumeric(selector.val())) {
                        alert("숫자 형식이 아닙니다. 다시 입력해 주세요");
                        selector.val("");
                        selector.focus();

                        return false;
                    }

                    if (selector.val().indexOf(".") > -1) {
                        alert("소수점을 사용할수 없습니다.");
                        selector.val("");
                        selector.focus();

                        return false;
                    }
                }

            } else if ("double" == selector.attr("numtype")) {

                var pos = 0;
                if (selector.attr("pos")) {
                    pos = selector.attr("pos");
                }

                if (!ADM_SELF.fn_sosuCheck(selector, pos)) {
                    return false;
                }
            }

            return true;

        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * maxLength 까지 입력값을 받도록 제한한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-02-15
     */
    , fn_maxInput: function (selector, showSelector) {

        try {
            var selector = jQuery(selector);
            var chk = true;

            if (selector
                && selector.length > 0) {

                var limit = selector.attr("maxlength");
                if (!limit || limit.length == 0) {
                    limit = selector.attr("data-maxLength");
                }

                if (limit && limit.length > 0) {

                    var txtLen = selector.val().length;

                    if (txtLen > parseInt(limit)) {
                        alert("입력하신 내용은 " + limit + "자 까지만 입력가능합니다.");
                        selector.val(selector.val().substr(0, limit));
                        selector.focus();

                        chk = false;
                    }

                    if (showSelector && showSelector.length > 0) {
                        showSelector.html(selector.val().length);
                    }
                }
            }

            return chk;

        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * minLength 와 maxLength 입력값을 체크한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-02-15
     */
    , fn_lenCheck: function (selector) {

        try {
            var chk = true;

            if (ADM_SELF.fn_maxInput(selector)) {

                var minLen = selector.attr("minlength");

                if (!minLen || minLen.length == 0) {
                    minLen = selector.attr("data-minLength");
                }

                if (!minLen || minLen.length == 0) {
                    minLen = 0;
                }

                if (selector && selector.length > 0) {
                    var txtLen = selector.val().length;
                    if (txtLen < parseInt(minLen)) {
                        alert("입력하신 내용은 " + minLen + "자 이상 입력하셔야 합니다.");
                        selector.focus();

                        chk = false;
                    }
                }
            }

            return chk;
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * 소수점을 체크한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-02-15
     */
    , fn_sosuCheck: function (selector, pos) {

        try {
            var selector = jQuery(selector);

            if (!selector
                || selector.length == 0) {
                throw "[입력값]을 확인해 주세요";
            }

            if (!pos
                || pos.length == 0) {
                throw "[자리수]를 확인해 주세요";
            }

            if (!jQuery.isNumeric(pos)) {
                throw "[자리수]가 숫자형이 아닙니다.";
            }

            if (selector && selector.val()) {
                if (!jQuery.isNumeric(selector.val())) {
                    alert("숫자 형식이 아닙니다. 다시 입력해 주세요");
                    selector.val("");
                    selector.focus();

                    return false;
                }

                if (selector.val().indexOf(".") > -1) {
                    var sosuInx = selector.val().indexOf(".") + 1;
                    var sosuLen = selector.val().substr(sosuInx).length;

                    if (sosuLen > pos) {
                        alert("소수점 " + pos + "자리까지만 입력하셔야 합니다.");
                        selector.val(selector.val().substr(0, sosuInx + pos));
                        selector.focus();

                        return false;
                    }
                }
            }
        } catch (e) {
            console.log(e);
            throw e;
        }

        return true;
    }

    /*
     * 산식을 계산하여 target에 출력한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-02-15
     */
    , fn_calc: function (jsonData) {
        try {

            if (!jsonData
                || !jsonData.selector) {
                throw "selector 인자를 확인해 주세요.";
            }

            if (!jsonData.target) {
                throw "target 인자를 확인해 주세요";
            }

            if (!jsonData.calc) {
                throw "calc 인자를 확인해 주세요";
            }

            if (!jQuery.isNumeric(jsonData.calc)) {
                throw "calc 가 숫자형인지 확인해 주세요";
            }

            var selector = jQuery(jsonData.selector);
            var target = jQuery(jsonData.target);
            var calc = parseFloat(jsonData.calc);
            var pos = !jsonData.pos ? 1 : jsonData.pos;

            if (!jQuery.isNumeric(pos)) {
                throw "pos 가 숫자형인지 확인해 주세요";
            }

            target.html(((!selector.val() ? 0 : parseFloat(selector.val()).toFixed(pos)) * calc).toFixed(pos));

        } catch (e) {
            console.log(e);
            throw e;
        }
    }


    /*
     * val 값에서 html 태그 정보를 치환한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_replaceEscape: function (val) {

        try {
            val = (val ? val : "");

            if (typeof val == "string") {
                val = val.replace(/>/gi, "&gt;");
                val = val.replace(/</gi, "&lt;");
                val = val.replace(/\"/gi, "&quot;");
            }

            return val;
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * selector 값이 null 인 경우 alterValue 로 대체하여 리턴한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_nvl: function (selector, alterValue) {

        try {
            if (selector == undefined
                || jQuery(selector).val() == "undefined"
                || jQuery.type(selector) == "undefined"
                || jQuery(selector).val() == null
                || jQuery(selector).length == 0
            ) {
                return alterValue;
            } else {
                return jQuery(selector).val();
            }
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * selector 로 검색된 대상에서 input 및 select 박스 정보만을 추출하여 json 으로 반환한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_getJsonData: function (selector) {
        try {
            return this.fn_toJson(jQuery("input[type=text], input[type=hidden], input[type=checkbox]:checked, input[type=radio]:checked, select"
                    , selector
                ).serializeArray()
            );
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * json 데이터를 dest 대상에 복사한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_jsonCopyTo: function (selector, data) {

        try {
            jQuery.each(data, function (key, value) {
                jQuery('[name=' + key + ']', selector).val(value);
            });
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * selector 대상 데이터를 json으로 반환한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_toJson: function (selector, setData) {
        var o = {};

        try {
            jQuery.each(selector, function (inx, data) {

                if (data.name) {
                    var value = "";

                    if (o[data.name]) {

                        if (!jQuery.isArray(o[data.name])) {

                            var prevValue = o[data.name];
                            o[data.name] = [];

                            o[data.name].push(prevValue);
                        }

                        if (setData
                            && "Y" == setData.CONVERT_YN) {
                            o[data.name].push(encodeURIComponent(data.value));
                        } else {
                            o[data.name].push(data.value);
                        }
                    } else {
                        if (setData
                            && "Y" == setData.CONVERT_YN) {
                            o[data.name] = encodeURIComponent(data.value);
                        } else {
                            o[data.name] = data.value;
                        }
                    }
                }
            });
        } catch (e) {
            console.log(e);
            throw e;
        }

        return o;
    }

    /*
     * json에서 key에 일치하는 값을 추출한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_findByJson: function (json, findKey, subKey) {
        var o = "";

        try {

            jQuery.each(json, function (key, value) {

                if (key == findKey) {

                    if (subKey) {
                        jQuery.each(value, function (key1, value1) {
                            if (key1 == subKey) {
                                o = value1;
                                return false;
                            }
                        });
                    } else {
                        o = value;
                        return false;
                    }
                }
            });
        } catch (e) {
            console.log(e);
            throw e;
        }

        return o;
    }

    /*
     * selector 에서 엔터키시 eventHandler 를 bind 한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-02-05
     */
    , fn_enterEvent: function (setData) {

        try {

            if (!setData
                || !setData.button
                || !setData.eventHandler) {
                throw "인자값을 확인해 주세요.";
            }


            jQuery.each(setData.button, function (key, data) {
                jQuery(data).bind("keydown", setData.eventHandler);
            });
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * selector 에서 A, SELECT 태그에 대해 eventHandler 에 정의된 이벤트를 bind 한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-02-05
     */
    , fn_eventBind: function (selector, eventHandler) {

        try {
            jQuery.each(selector, function (key, data) {

                jQuery(data).attr("disabled", false);

                if ("A" == data.tagName
                    || "IMG" == data.tagName
                    || "SELECT" == data.tagName
                    || "EM" == data.tagName) {

                    var eventId = (jQuery(data).attr("id") ? jQuery(data).attr("id") : jQuery(data).attr("name"));

                    jQuery.each(eventHandler, function (eKey, eData) {

                        if (eKey == eventId) {
                            if (eventHandler[eKey].click) {
                                jQuery(data).bind("click", eventHandler[eKey].click);
                            }

                            if (eventHandler[eKey].change) {
                                jQuery(data).bind("change", eventHandler[eKey].change);
                            }
                        }
                    });
                }
            });
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * 선택된 TR 에서 체크된 데이터를 JSON 으로 변환하여 반환한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_checkedTR2json: function (selector, setData) {

        try {
            var jsonArray = [];

            if (!selector
                || selector.length == 0
                || !setData.TITLE_ROW) {
                return false;
            }

            jQuery.each(selector, function (index, data) {
                var jsonData = {};
                jQuery.each(jQuery("input, select, input:checked", jQuery(data)), function (subInx, subData) {

                    if (jQuery(subData).attr("name") == "ROW_INDEX[]") {
                        jQuery(subData).val(jQuery(data).index() - setData.TITLE_ROW);
                    }

                    jsonData[jQuery(subData).attr("name").replace("[]", "")] = ADM_SELF.fn_replaceEscape(jQuery(subData).val());
                });
                jsonArray.push(jsonData);
            });

            return jsonArray;
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * 비동기 ajax 를 호출한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     *
     * 수정 이력
     * 수정자 : 한준호
     * 수정일 : 2020-08-14
     * 내용 : callback 을 기존 string 만 받던 로직에서 function 도 받을수 있도록 추가
     */
    , fn_ajax: function (form, setData) {

        // 2020.08.14 한준호 추가
        var processCallback = function (options, data) {
            if (typeof options.callBack === 'string') {
                //eval( options.callBack + "(" + JSON.stringify( data ) + ")" );
                (new Function("return " + options.callBack + "(" + JSON.stringify(data) + ")"))();
            } else if (typeof options.callBack === 'function') {
                options.callBack(data);
            }
        };

        try {
            if (jQuery.type(form) == "undefined"
                || jQuery.type(setData) == "undefined"
                || !setData
                || !setData.url
            ) {
                throw "인자값을 확인해 주세요";
            }

            if (setData.nextCheck) {
//				var eval = (new Function ("return "+ setData.nextCheck+"(" + ( setData.param ? JSON.stringify( setData.param ) : "" ) + ")"))();
                var eval = setData.nextCheck(setData.param ? JSON.stringify(setData.param) : "");
                if (!eval) {
                    return false;
                }
            }

            var dataParams = "";
            if (setData.dataParams) {
                dataParams = setData.dataParams;
            } else {

                if (!setData.SEND_DATA_YN
                    || "" == setData.SEND_DATA_YN
                    || "Y" == setData.SEND_DATA_YN
                ) {

                    dataParams = this.fn_toJson(jQuery(form).serializeArray());
                }
            }

            if (jQuery(form).find("input[type=file]")
                && jQuery(form).find("input[type=file]").length != 0
            ) {
                jQuery(form).ajaxForm({

                    dataType: "json"
                    , contentType: "multipart/form-data; charset=UTF-8"
                    , beforeSubmit: function (data, formData, option) {

                        if (setData.beforeCheck) {
                            //var temp = eval( setData.beforeCheck + "()" );
                            var eval = (new Function("return " + setData.beforeCheck + "()"))();
                            if (!eval) {
                                return false;
                            }
                        }

                        if (setData.loadingParam) {
                            setData.loadingParam.button.unbind("click");
                            setData.loadingParam.button.unbind("change");
                            setData.loadingParam.loadingBar.show();
                        }

                        return true;
                    }

                    , uploadProgress: function (event, position, total, percentComplete) {
                    }

                    , success: function (data, status) {

                        if (setData.loadingParam) {
                            setData.loadingParam.loadingBar.hide();

                            if (setData.loadingParam.button
                                && setData.loadingParam.eventHandler) {

                                ADM_SELF.fn_eventBind(setData.loadingParam.button, setData.loadingParam.eventHandler);
                            }
                        }

                        if (setData.callBack) {
                            processCallback(setData, data);
                        }
                    }

                    , error: function (data, status, error) {

                        if (setData.loadingParam) {
                            setData.loadingParam.loadingBar.hide();

                            if (setData.loadingParam.button
                                && setData.loadingParam.eventHandler) {

                                ADM_SELF.fn_eventBind(setData.loadingParam.button, setData.loadingParam.eventHandler);
                            }
                        }

                        if (setData.callBack) {
                            processCallback(setData, data);
                        }
                    }
                });

            } else {

                if (setData.loadingParam) {
                    setData.loadingParam.button.unbind("click");
                    setData.loadingParam.button.unbind("change");
                    setData.loadingParam.loadingBar.show();
                }

                jQuery.ajax({
                    type: "POST"
                    , async: true
                    , url: setData.url
                    , data: dataParams
                    , dataType: "json"
                    , success: function (e) {

                        if (setData.loadingParam) {
                            setData.loadingParam.loadingBar.hide();

                            if (setData.loadingParam.button
                                && setData.loadingParam.eventHandler) {

                                ADM_SELF.fn_eventBind(setData.loadingParam.button, setData.loadingParam.eventHandler);
                            }
                        }

                        if (setData.callBack) {
                            processCallback(setData, e);
                        }
                    }
                    , error: function (e) {

                        if (setData.loadingParam) {
                            setData.loadingParam.loadingBar.hide();

                            if (setData.loadingParam.button
                                && setData.loadingParam.eventHandler) {

                                ADM_SELF.fn_eventBind(setData.loadingParam.button, setData.loadingParam.eventHandler);
                            }
                        }

                        if (setData.callBack) {
                            processCallback(setData, e);
                        }
                    }
                });
            }
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * json 데이터 정보를 fomData에 append 한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_appendFomData: function (dataParams, formData) {

        try {
            if (jQuery.type(dataParams) == "undefined"
                || jQuery.type(formData) == "undefined"
            ) {
                throw "인자값을 확인해 주세요";
            }

            jQuery.each(dataParams, function (key, data) {

                if (jQuery.isArray(data)) {
                    jQuery.each(data, function (inx, subData) {
                        formData.append(key, subData);
                    });
                } else {
                    formData.append(key, data);
                }
            });

        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * 팝업화면을 보여준다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_showPopup: function (setData, pData) {

        try {
            if (jQuery.type(setData) == "undefined"
                || !setData
                || !setData.url
            ) {
                throw "인자값을 확인해 주세요";
            }

            var winName = (setData.winName ? setData.winName : "openWinView");
            var getParams = "";

            if (pData) {
                pData.winName = setData.winName;
            } else {
                pData = {};
                pData.winName = setData.winName;
            }

            getParams = jQuery.param(pData);
            var url = setData.url;
            if (!setData.form
                && getParams) {
                url += "?" + getParams;
            }

            /*
                        setData.height		= ( setData && setData.height			?	setData.height	:	"0"		);
                        setData.width		= ( setData && setData.width 			? 	setData.width 	: 	"100"	);

                        if( screen.availHeight ) {
                            setData.top		= ( screen.availHeight - setData.height ) / 2 ;
                        }

                        if( screen.availWidth ) {
                            setData.left	= ( screen.availWidth - setData.width ) / 2;
                        }
            */
            var popUrl = "";
            if (!setData.form)
                popUrl = url;

            var popup = window.open(popUrl
                , winName
                , "  	  left=" + (setData && setData.left ? "0" : "0")
                + "	, top=" + (setData && setData.top ? "0" : "0")
                + "	, width=" + (setData && setData.width ? setData.width : "0")
                + "	, height=" + (setData && setData.height ? setData.height : "0")
                + "	, toolbar=" + (setData && setData.toolbar ? "yes" : "no")
                + "	, menubar=" + (setData && setData.menubar ? "yes" : "no")
                + "	, status=" + (setData && setData.statusbar ? "yes" : "no")
                + "	, scrollbars=" + (setData && setData.scrollbars ? "yes" : "no")
                + "	, resizable=" + (setData && setData.resizable ? "yes" : "no")
            );

            if (setData.form) {

                setData.form.attr("action", url);
                setData.form.attr("target", winName);
                setData.form.attr("method", "POST");
                setData.form.submit();

                if (!setData.clearYn
                    || "Y" == setData.clearYn
                ) {
                    jQuery("input:hidden", setData.form).val("");
                }
            }

            return popup;
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * 팝업창을 종료한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_popupClose: function (p_opener, p_self, resultData) {

        if (!resultData
            || !resultData.closeYn
            || "Y" == resultData.closeYn
        ) {
            p_self.opener = p_self;
        }

        if ("fn_popupReturn" in p_opener) {
            p_opener.fn_popupReturn(resultData);
        }

        if (!resultData
            || !resultData.closeYn
            || "Y" == resultData.closeYn
        ) {
            p_self.close();
        }
    }

    /*
     * dateParam 에 달력 정보를 설정한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_setDatePicker: function (dateParam) {

        try {
            if (!dateParam
                || !dateParam.id) {
                return false;
            }

            jQuery(dateParam.id).datepicker({
                showAnim: "slideDown"
                , dateFormat: "yy-mm-dd"
                , showButtonPanel: true
                , currentText: "오늘"
                , closeText: "닫기"
                , showOn: "both"
                , buttonImage: "/anymobi/adm/images/ico_cal.gif"
                , buttonImageOnly: true
                , buttonText: "날짜 선택"
                , showOtherMonths: true
                , showMonthAfterYear: true
                , prevText: "이전 달"
                , nextText: "다음 달"
                , yearSuffix: "년"
                , monthNames: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"]
                , monthNamesShort: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"]
                , dayNames: ["일", "월", "화", "수", "목", "금", "토"]
                , dayNamesShort: ["일", "월", "화", "수", "목", "금", "토"]
                , dayNamesMin: ["일", "월", "화", "수", "목", "금", "토"]
                , onClose: function (selectedDate, picker) {
                    if (dateParam.callBack) {
                        var returnData = {
                            "name": this.name
                            , "index": jQuery("input[name='" + this.name + "'].input-date").index(this)
                            , "selectedDate": selectedDate
                        };

                        if (typeof dateParam.callBack === 'function') {
                            dateParam.callBack(returnData);
                        } else {
                            //eval( dateParam.callBack + "( (" + JSON.stringify(returnData) + ") )" );
                            (new Function("return " + dateParam.callBack + "( (" + JSON.stringify(returnData) + ") )"))();
                        }
                    }
                }
            });

            jQuery("img.ui-datepicker-trigger").attr("style", "margin-left:2px; vertical-align:middle; cursor: Pointer;");
        } catch (e) {
            console.log(e);
            throw e;
        }
    }

    /*
     * form 정보를  checkDest 를 기준으로 검증한다.
     *
     * 작성자: written by bkLove(최병국)
     * 작성일: 2016-01-25
     */
    , fn_validate: function (form, checkDest, setData) {

        try {
            jQuery.validator.setDefaults({
                onkeyup: false
                , onclick: false
                , onfocusout: false
                , showErrors: function (errorMap, errorList) {
                    if (this.numberOfInvalids()) {
                        alert(errorList[0].message);
                    }
                    /*
                                                                if(errorList.length < 1)
                                                                    return false;
                                                                alert(errorList[0].message);
                    */
                }
                , submitHandler: function () {
                    if (!setData.ajaxYn
                        || "" == setData.ajaxYn
                        || "Y" == setData.ajaxYn) {

                        ADM_SELF.fn_ajax(form, setData);
                    } else {
                        jQuery(form).attr({action: setData.url});

                        return true;
                    }
                }
            });

            jQuery(form).validate(checkDest);

        } catch (e) {
            console.log(e);
            throw e;
        }
    }
    /**
     * 기존 함수에 textarea ( 일괄검색같은 textarea) 도 추가
     * @param selector
     * @returns {*}
     */
    , fn_getJsonDataNew: function (selector) {
        try {
            return this.fn_toJson(jQuery("input[type=text], input[type=hidden], input[type=checkbox]:checked, input[type=radio]:checked, select, textarea"
                    , selector
                ).serializeArray()
            );
        } catch (e) {
            console.log(e);
            throw e;
        }
    }
}