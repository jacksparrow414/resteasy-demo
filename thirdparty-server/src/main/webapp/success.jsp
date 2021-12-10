<%@ page import="org.demo.resteasy.vo.TokenVO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Acquire Token Success</title>
</head>
<body>
<%
    TokenVO token = (TokenVO) session.getAttribute("token");

%>
<textarea>${token}</textarea>
</body>
</html>
