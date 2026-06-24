<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<%
    Object customCode = request.getAttribute("errorCode");
    Object customMsg = request.getAttribute("errorMessage");

    // 서블릿 컨테이너가 찔러준 에러 코드 숫자 추출
    Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    if (customCode != null) {
        statusCode = Integer.parseInt(customCode.toString());
    } else if (statusCode == null) {
        statusCode = 500; // 최종 방어 기본값
    }

    String errorTitle = "알 수 없는 오류가 발생했습니다.";
    String errorMsg = "시스템 관리자에게 문의해 주세요.";

    if (customMsg != null) {
        errorTitle = "오류가 발생했습니다. (" + statusCode + ")";
        errorMsg = customMsg.toString();
    } else {
        // 기존 동적 분기 로직 (이하 동일)
        if (statusCode == 400) {
            errorTitle = "잘못된 요청입니다. (400)";
            errorMsg = "요청 파라미터나 형식이 올바르지 않습니다.<br>입력값을 확인해 주세요.";
        } else if (statusCode == 403) {
            errorTitle = "접근 권한이 없습니다. (403)";
            errorMsg = "이 페이지를 열어볼 수 있는 권한이 없습니다.<br>로그인 상태나 계정 등급을 확인해 주세요.";
        } else if (statusCode == 404) {
            errorTitle = "페이지를 찾을 수 없습니다. (404)";
            errorMsg = "방문하시려는 주소가 잘못 입력되었거나,<br>페이지가 삭제되어 더 이상 존재하지 않습니다.";
        } else if (statusCode == 405) { // 🎯 405 분기도 하나 추가해두면 기막힙니다!
            errorTitle = "허용되지 않은 접근 방식입니다. (405)";
            errorMsg = "요청 형식이 올바르지 않거나 허용되지 않은 HTTP 메서드입니다.";
        } else if (statusCode == 500) {
            errorTitle = "서버 내부 오류가 발생했습니다. (500)";
            errorMsg = "서버가 요청을 처리하는 과정에서 문제가 생겼습니다.<br>잠시 후 다시 시도해 주세요.";
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= errorTitle %> - KT LMS</title>
    <style>
        body {
            text-align: center;
            padding: 120px 20px;
            font-family: 'Malgun Gothic', sans-serif;
            background: #f8f9fa;
            margin: 0;
        }

        .error-container {
            max-width: 600px;
            margin: 0 auto;
            background: #fff;
            padding: 50px 40px;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
        }

        .code {
            font-size: 90px;
            font-weight: 900;
            color: #ff4d4f;
            margin: 0 0 10px 0;
            line-height: 1;
        }

        .code.server-err {
            color: #52c41a;
        }

        h2 {
            font-size: 24px;
            color: #222;
            margin-top: 0;
            margin-bottom: 15px;
        }

        p {
            color: #666;
            font-size: 16px;
            line-height: 1.6;
            margin-bottom: 30px;
        }

        .btn-group a {
            display: inline-block;
            padding: 12px 35px;
            border-radius: 6px;
            font-weight: bold;
            text-decoration: none;
            font-size: 15px;
            transition: all 0.2s;
        }

        .btn-home {
            background: #007bff;
            color: #fff;
            border: 1px solid #007bff;
        }

        .btn-home:hover {
            background: #0056b3;
        }

        .btn-back {
            background: #fff;
            color: #444;
            border: 1px solid #ccc;
            margin-right: 10px;
        }

        .btn-back:hover {
            background: #f1f1f1;
        }
    </style>
</head>
<body>

<div class="error-container">
    <div class="code <%= statusCode != 404 ? "server-err" : "" %>"><%= statusCode %>
    </div>
    <h2><%= errorTitle %>
    </h2>
    <p><%= errorMsg %>
    </p>

    <div class="btn-group">
        <a href="javascript:history.back();" class="btn-back">이전 화면</a>
        <a href="${pageContext.request.contextPath}/" class="btn-home">메인으로</a>
    </div>
</div>

</body>
</html>