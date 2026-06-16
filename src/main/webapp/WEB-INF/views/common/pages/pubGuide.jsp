<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>🎨 KTLMS Portal 퍼블리싱 가이드</title>
    <style>
        body {
            font-family: 'Malgun Gothic', sans-serif;
            padding: 20px;
            background-color: #f9f9f9;
        }

        h1 {
            color: #333;
            border-bottom: 2px solid #ff4500;
            padding-bottom: 10px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: #fff;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }

        th {
            background-color: #ff4500;
            color: white;
        }

        tr:hover {
            background-color: #f5f5f5;
        }

        a {
            color: #0066cc;
            text-decoration: none;
            font-weight: bold;
        }

        a:hover {
            text-decoration: underline;
        }

        .remarks-text {
            color: #666;
            font-size: 0.9em;
        }
    </style>
</head>
<body>

<h1>🎨 KTLMS Portal 퍼블리싱 파일 목록</h1>
<p>💡 /resources/pub 하위에 .html 파일 추가시 반영됩니다. <code>&lt;title&gt;</code> 내용은 비고란에 표기됩니다.</p>

<table>
    <thead>
    <tr>
        <th width="10%">번호</th>
        <th width="45%">파일 경로 (클릭 시 이동)</th>
        <th width="45%">비고</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="file" items="${fileList}" varStatus="status">
        <tr>
            <td>${status.count}</td>
            <td>
                <a href="${pageContext.request.contextPath}/resources/pub/${file.path}" target="_blank">
                    /resources/pub/${file.path}
                </a>
            </td>
            <td class="remarks-text">${file.remarks}</td>
        </tr>
    </c:forEach>
    <c:if test="${empty fileList}">
        <tr>
            <td colspan="3" style="text-align: center; color: #999;">검색된 퍼블리싱 파일이 없습니다.</td>
        </tr>
    </c:if>
    </tbody>
</table>

</body>
</html>