<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1.0">
    <title>KT LMS Portal</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/resources/legacy/anymobi/css/style.css' />">
    <link rel="stylesheet" type="text/css" href="<c:url value='/resources/legacy/anymobi/css/main.css' />">
    <style>
        body.popup-main {
            margin: 0;
            background: #fff;
        }
        .popup-con {
            width: 427px;
            height: 680px;
            margin: 0 auto;
        }
        .popup-con img {
            display: block;
            max-width: 100%;
        }
        .chk-today {
            padding: 12px 0;
            color: #000;
            font-size: 13px;
            text-align: center;
        }
    </style>
</head>
<body class="popup-main">
<div class="popup-con">
    <a class="j-go-elearnning" href="<c:url value='/educontents/hrdIbox/hrdIboxDetail?num=294' />">
        <img src="<c:url value='/resources/legacy/anymobi/img/popup/popup_ai_20201026.png' />" alt="">
    </a>
    <div class="chk-today">
        <label><input type="checkbox" id="closeToday"> 오늘은 이 창을 다시 열지 않음</label>
    </div>
</div>
<script nonce="${cspNonce}">
    function setCookie(name, value, expiredays) {
        var todayDate = new Date();
        todayDate.setDate(todayDate.getDate() + expiredays);
        document.cookie = name + "=" + encodeURIComponent(value) + "; path=/; expires=" + todayDate.toUTCString() + ";";
    }
    document.getElementById("closeToday").addEventListener("change", function () {
        if (this.checked) {
            setCookie("popname", "done", 1);
            window.close();
        }
    });
</script>
</body>
</html>
