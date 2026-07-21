var g_target;
var g_pop_left;
var g_pop_top;
var g_cal_Day;
var g_cssAddr = "/common/css/calendar.css";
var g_oPopup;

function openCalendar(arg_obj) {

    g_oPopup = window.createPopup();
    g_oPopup.document.createStyleSheet(g_cssAddr);

    g_target = arg_obj;
    g_pop_top = document.body.clientTop + GetObjectTop(arg_obj) - document.body.scrollTop;
    g_pop_left = document.body.clientLeft + GetObjectLeft(arg_obj) - document.body.scrollLeft;

    var l_now = new Date();
    g_cal_Day = l_now.getFullYear() + "." + day2(l_now.getMonth() + 1) + "." + day2(l_now.getDate());
    Show_cal(l_now.getFullYear(), l_now.getMonth() + 1, l_now.getDate());

}


function checkNumber(arg_obj) {
    var l_str = arg_obj;
    if (l_str.length == 0)
        return false;

    for (var i = 0; i < l_str.length; i++) {
        if (!('0' <= l_str.charAt(i) && l_str.charAt(i) <= '9'))
            return false;
    }
    return true;
}

function Calendar_Click(arg_e) {
    g_cal_Day = arg_e.title;
    if (g_cal_Day.length == 10)
        g_target.value = g_cal_Day;

    g_oPopup.hide();
}

function day2(arg_d) {
    var l_str = new String();

    if (parseInt(arg_d) < 10)
        l_str = "0" + parseInt(arg_d);
    else
        l_str = "" + parseInt(arg_d);

    return l_str;
}


function fnChangeYearD(arg_sYear, arg_sMonth, arg_sDay) {
    Show_cal(arg_sYear, arg_sMonth, arg_sDay);
}

function delDate() {

    g_target.value = "";
    g_oPopup.hide();
}


function Show_cal(arg_sYear, arg_sMonth, arg_sDay) {
    var l_intaMonths_day = new Array(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
    var l_straMonth_Val = new Array("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");

    var l_intThisYear = new Number();
    var l_intThisMonth = new Number();
    var l_intThisDay = new Number();
    l_intThisYear = parseInt(arg_sYear, 10);
    l_intThisMonth = parseInt(arg_sMonth, 10);
    l_intThisDay = parseInt(arg_sDay, 10);

    var l_datToday = new Date();
    if (l_intThisYear == 0)
        l_intThisYear = l_datToday.getFullYear();
    if (l_intThisMonth == 0)
        l_intThisMonth = parseInt(l_datToday.getMonth(), 10) + 1;
    if (l_intThisDay == 0)
        l_intThisDay = l_datToday.getDate();

    switch (l_intThisMonth) {
        case 1:
            l_intPrevYear = l_intThisYear - 1;
            l_intPrevMonth = 12;
            l_intNextYear = l_intThisYear;
            l_intNextMonth = 2;
            break;
        case 12:
            l_intPrevYear = l_intThisYear;
            l_intPrevMonth = 11;
            l_intNextYear = l_intThisYear + 1;
            l_intNextMonth = 1;
            break;
        default:
            l_intPrevYear = l_intThisYear;
            l_intPrevMonth = parseInt(l_intThisMonth, 10) - 1;
            l_intNextYear = l_intThisYear;
            l_intNextMonth = parseInt(l_intThisMonth, 10) + 1;
            break;
    }//close switch

    l_datFirstDay = new Date(l_intThisYear, l_intThisMonth - 1, 1);
    l_intFirstWeekday = l_datFirstDay.getDay();

    if ((l_intThisYear % 4) == 0)
        if ((l_intThisYear % 100) == 0)
            if ((l_intThisYear % 400) == 0)
                l_intaMonths_day[2] = 29;
            else
                l_intaMonths_day[2] = 29;

    l_firstPrintDay = 1;
    l_intLastDay = l_intaMonths_day[l_intThisMonth];

    var l_strCal_HTML = "<html><body><form name='calendar'>";
    l_strCal_HTML += "<table id='Cal_Table' border='1' bordercolor='#cfcfcf' >";
    l_strCal_HTML += "<tr id='Cal_Header' bgcolor='#C6F2ED' ><td colspan=7 >";
    l_strCal_HTML += "<select name='selYear' id='Cal_Select' OnChange='parent.fnChangeYearD(calendar.selYear.value, calendar.selMonth.value, " + l_intThisDay + ")';>";
    for (var l_optYear = l_intThisYear - 10; l_optYear < (l_intThisYear + 10); l_optYear++) {
        l_strCal_HTML += "<option value='" + l_optYear + "' ";
        if (l_optYear == l_intThisYear)
            l_strCal_HTML += " selected>";
        else
            l_strCal_HTML += ">";
        l_strCal_HTML += l_optYear + "</option>";
    }
    l_strCal_HTML += "</select>";
    l_strCal_HTML += "&nbsp;&nbsp;&nbsp;<a style='cursor:hand;' OnClick='parent.Show_cal(" + l_intPrevYear + "," + l_intPrevMonth + "," + l_intThisDay + ");'><img src='/images/offadmin/calendar_ago.gif'></a> ";
    l_strCal_HTML += "&nbsp;";
    l_strCal_HTML += "<select name='selMonth' id='Cal_Select' OnChange='parent.fnChangeYearD(calendar.selYear.value, calendar.selMonth.value, " + l_intThisDay + ")';>";
    for (var i = 1; i < 13; i++) {
        l_strCal_HTML += "<option value='" + l_straMonth_Val[i - 1] + "' ";
        if (l_intThisMonth == parseInt(l_straMonth_Val[i - 1], 10))
            l_strCal_HTML += " selected>";
        else
            l_strCal_HTML += ">";
        l_strCal_HTML += l_straMonth_Val[i - 1] + "</option>";
    }
    l_strCal_HTML += "</select> ";
    l_strCal_HTML += "<a style='cursor:hand;' OnClick='parent.Show_cal(" + l_intNextYear + "," + l_intNextMonth + "," + l_intThisDay + ");'><img src='/images/offadmin/calendar_next.gif'></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a OnClick='parent.delDate()' style='text-decoration:none; cursor:hand;' ><span style='color: olive' ><b>삭제</b></span></a></td></tr>";
    l_strCal_HTML += "<tr id='Cal_Week'>";
    l_strCal_HTML += "	<td align='center' bgcolor='#eaeaea' class='bdr32 p5 ct01'>일</td>";
    l_strCal_HTML += "	<td align='center' bgcolor='#eaeaea' class='bdr32 p5 ct01'>월</td>";
    l_strCal_HTML += "	<td align='center' bgcolor='#eaeaea' class='bdr32 p5 ct01'>화</td>";
    l_strCal_HTML += "	<td align='center' bgcolor='#eaeaea' class='bdr32 p5 ct01'>수</td>";
    l_strCal_HTML += "	<td align='center' bgcolor='#eaeaea' class='bdr32 p5 ct01'>목</td>";
    l_strCal_HTML += "	<td align='center' bgcolor='#eaeaea' class='bdr32 p5 ct01'>금</td>";
    l_strCal_HTML += "	<td align='center' bgcolor='#eaeaea' class='bdr32 p5 ct01'>토</td>";
    l_strCal_HTML += "</tr>";

    for (l_intLoopWeek = 1; l_intLoopWeek <= 6; l_intLoopWeek++) {
        l_strCal_HTML += "<tr id='Cal_Day'>"
        for (l_intLoopDay = 1; l_intLoopDay <= 7; l_intLoopDay++) {
            if (l_intFirstWeekday > 0) {
                l_strCal_HTML += "<td class='Cal_EmptyDay'>&nbsp;";
                l_intFirstWeekday--;
            } else {
                if (l_firstPrintDay > l_intLastDay)
                    l_strCal_HTML += "<td class='Cal_Empty'>&nbsp;";
                else {
                    var l_strID = "";
                    var l_strClass = "";
                    if (l_intThisDay == l_firstPrintDay && l_intThisMonth == parseInt(g_cal_Day.split('.')[1], 10) && l_intThisYear == parseInt(g_cal_Day.split('.')[0], 10))
                        l_strID = "Cal_Today";

                    switch (l_intLoopDay) {
                        case 1:
                            l_strClass = "Cal_Sunday";
                            l_strCal_HTML += "<td id='" + l_strID + "' class='" + l_strClass + "' onClick='parent.Calendar_Click(this);' title=" + l_intThisYear + "." + day2(l_intThisMonth).toString() + "." + day2(l_firstPrintDay).toString() + " onmouseover=\"this.id='Cal_MouseOver'\" onmouseout=\"this.id='" + l_strID + "'\">" + l_firstPrintDay;
                            break;
                        case 7:
                            l_strClass = "Cal_Saturday";
                            l_strCal_HTML += "<td id='" + l_strID + "' class='" + l_strClass + "' onClick='parent.Calendar_Click(this);' title=" + l_intThisYear + "." + day2(l_intThisMonth).toString() + "." + day2(l_firstPrintDay).toString() + " onmouseover=\"this.id='Cal_MouseOver'\" onmouseout=\"this.id='" + l_strID + "'\">" + l_firstPrintDay;
                            break;
                        default:
                            l_strClass = "Cal_Weekday";
                            l_strCal_HTML += "<td id='" + l_strID + "' class='" + l_strClass + "' onClick='parent.Calendar_Click(this);' title=" + l_intThisYear + "." + day2(l_intThisMonth).toString() + "." + day2(l_firstPrintDay).toString() + " onmouseover=\"this.id='Cal_MouseOver'\" onmouseout=\"this.id='" + l_strID + "'\">" + l_firstPrintDay;
                    }
                }
                l_firstPrintDay++;
            }
            l_strCal_HTML += "</td>";
        }
        l_strCal_HTML += "</tr>";

        if (l_firstPrintDay > l_intLastDay)
            break;
    }
    l_strCal_HTML += "</table></form></body></html>";

    var l_oPopBody = g_oPopup.document.body;
    l_oPopBody.style.backgroundColor = "white";
    l_oPopBody.style.border = "solid #cfcfcf 1px";
    l_oPopBody.innerHTML = l_strCal_HTML;

    var l_calHeight;
    switch (l_intLoopWeek) {
        case 4:
            l_calHeight = 164;
            break;
        case 6:
            l_calHeight = 214;
            break;
        default:
            l_calHeight = 189;
    }

    g_oPopup.show(g_pop_left, (g_pop_top + g_target.offsetHeight), 220, l_calHeight, document.body);

}

function GetObjectTop(arg_obj) {
    var l_intTopSum = arg_obj.offsetTop;
    while (arg_obj.nodeName.indexOf('HTML') != 0 && arg_obj.nodeName.indexOf('BODY') != 0) {
        arg_obj = arg_obj.offsetParent;
        l_intTopSum += arg_obj.offsetTop;
    }
    return l_intTopSum;
}

function GetObjectLeft(arg_obj) {
    var l_intLeftSum = arg_obj.offsetLeft;
    while (arg_obj.nodeName.indexOf('HTML') != 0 && arg_obj.nodeName.indexOf('BODY') != 0) {
        arg_obj = arg_obj.offsetParent;
        l_intLeftSum += arg_obj.offsetLeft;
    }
    return l_intLeftSum;
}