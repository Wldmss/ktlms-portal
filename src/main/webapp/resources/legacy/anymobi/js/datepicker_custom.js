$(function () {
    // datepicker default setting (korea)
    $.datepicker.regional["ko"] = {
        showAnim: "slideDown",
        dateFormat: "yy.mm.dd",
        showButtonPanel: true,
        currentText: "오늘",
        closeText: "닫기",
        showOn: "both",
        buttonImage: "/anymobi/img/icon_calendar.png",
        buttonImageOnly: true,
        buttonText: "날짜 선택",
        showOtherMonths: true,
        showMonthAfterYear: true,
        prevText: "이전 달",
        nextText: "다음 달",
        yearSuffix: "년",
        firstDay: 0,
        monthNames: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        monthNamesShort: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        dayNames: ["일", "월", "화", "수", "목", "금", "토"],
        dayNamesShort: ["일", "월", "화", "수", "목", "금", "토"],
        dayNamesMin: ["일", "월", "화", "수", "목", "금", "토"],
    };
    $.datepicker.setDefaults($.datepicker.regional["ko"]);


    // single datepicker (default)
    $(".j-datepicker").datepicker();


    // single datepicker (current)


    // single datepicker (past)
    $(".j-datepicker-past").datepicker({maxDate: 0});


    // single datepicker (read only)
    $(".j-datepicker-readonly").datepicker({
        beforeShow: function () {
            $(this).readonlyDatepicker(true);
        }
    });
    $(".j-datepicker-readonly").next().css("cursor", "default");


    /**********************************************
     multi datepicker
     **********************************************/
    $(".j-datepicker-start-update").datepicker({
        onClose: function (selectedDate) {
            $(".j-datepicker-end-update").datepicker("option", "minDate", selectedDate);
        }
    });
    $(".j-datepicker-end-update").datepicker();

    /**********************************************
     multi datepicker (current) : 오늘 ~
     **********************************************/
    $(".j-datepicker-start").datepicker({
        minDate: 0,
        onClose: function (selectedDate) {
            $(".j-datepicker-end").datepicker("option", "minDate", selectedDate);
        }
    });
    $(".j-datepicker-end").datepicker({
        minDate: 0
    });

    /**********************************************
     multi datepicker (past) : ~ 오늘
     **********************************************/
    $(".j-datepicker-from").datepicker({
        maxDate: 0,
        onClose: function (selectedDate) {
            $(".j-datepicker-to").datepicker("option", "minDate", selectedDate);
        }
    });
    $(".j-datepicker-to").datepicker({
        maxDate: 0,
        onClose: function (selectedDate) {
            $(".j-datepicker-from").datepicker("option", "maxDate", selectedDate);
        }
    });


    /*******************************************************************
     multi datepicker (difference) :  시작 ~ 종료 (일수)
     *******************************************************************/
    function dateDifference() {
        var dayDiff = ($(".j-datepicker-diff-end").datepicker("getDate") - $(".j-datepicker-diff-start").datepicker("getDate")) / 1000 / 60 / 60 / 24;
        var daysVal = dayDiff + 1;
        $(".total-day").html("<em>총 " + daysVal + "일</em>");
    }

    $(".j-datepicker-diff-start").datepicker({
        minDate: 0,
        onSelect: function (dateText, inst) {
            var datePieces = dateText.split(".");
            var month = datePieces[0];
            var day = datePieces[1];
            var year = datePieces[2];
            $("select#arrmonth").val(month);
            $("select#arrday").val(day);
            $("select#arryear").val(year);
            if ($(".j-datepicker-diff-start").val() != "" && $(".j-datepicker-diff-end").val() != "") {
                dateDifference();
            }
        },
        onClose: function (selectedDate) {
            $(".j-datepicker-diff-end").datepicker("option", "minDate", selectedDate);
        }
    });
    $(".j-datepicker-diff-end").datepicker({
        minDate: 0,
        beforeShow: function (input, inst) {
            if ($(".j-datepicker-diff-start").val() == "") {
                alert("시작 날짜를 선택해주세요.");
                $(this).readonlyDatepicker(false);
            }
        },
        onSelect: function (dateText, inst) {
            var datePieces = dateText.split(".");
            var month = datePieces[0];
            var day = datePieces[1];
            var year = datePieces[2];
            $("select#depmonth").val(month);
            $("select#depday").val(day);
            $("select#depyear").val(year);
            dateDifference();
        },
        onClose: function (selectedDate) {
            $(".j-datepicker-diff-start").datepicker("option", "maxDate", selectedDate);
        }
    });

});