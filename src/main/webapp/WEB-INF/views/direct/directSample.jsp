<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>신규 프로젝트 샘플 페이지</title>
    <style>
        body {
            font-family: 'Malgun Gothic', sans-serif;
            margin: 40px;
            background-color: #f5f7fa;
            color: #333;
        }

        .container {
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            margin: 0 auto;
        }

        h1 {
            color: #004ea2; /* KT 브랜드 컬러 느낌의 블루 */
            border-bottom: 2px solid #004ea2;
            padding-bottom: 10px;
        }

        .info-box {
            background-color: #eef4fb;
            padding: 15px;
            border-left: 5px solid #004ea2;
            margin: 20px 0;
        }

        .badge {
            background-color: #28a745;
            color: white;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>✨ 신규 아키텍처 연결 성공</h1>

    <p>이 페이지는 레거시 <code>/web/</code> 폴더를 거치지 않고 <code>WebConfig</code>의 view controller 로 다이렉트 매핑된 화면입니다.</p>

    <div class="info-box">
        <strong>현재 요청 정보</strong>
        <ul>
            <li>컨트롤러 뷰네임: <code>pages/sample/helloSample</code></li>
            <li>물리적 파일 위치: <code>/WEB-INF/views/pages/sample/helloSample.jsp</code></li>
            <li>컨텍스트 패스: <code>${pageContext.request.contextPath}</code></li>
        </ul>
    </div>

    <p>
        <span class="badge">호환성 검증</span>
        기존 컨트롤러 코드 수정 없이 신규 폴더 확장이 완벽하게 가능합니다.
    </p>
</div>

</body>
</html>