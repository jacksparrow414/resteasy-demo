<%--
  Created by IntelliJ IDEA.
  User: jacksparrow414
  Date: 2023/6/17
  Time: 13:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>deleteMvcResult</title>
</head>
<body>
<c:if test="${not empty errors}">
    删除失败：<br>
    <ul>
    <c:forEach items="${errors}" var="error">
        <li>${error}</li>
    </c:forEach>
    </ul>
    <br>
</c:if>
<c:if test="${not empty message}">
    删除 ${message} 成功
</c:if>

</body>
</html>
