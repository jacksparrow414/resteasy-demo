<%--
  Created by IntelliJ IDEA.
  User: jacksparrow414
  Date: 2023/6/17
  Time: 12:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--jstl3的uri已经改变了--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>helloMvc</title>
</head>
<body>
<div>
    这是返回给页面的信息：${message}<br>
    这是使用jstl标签库的信息：<c:out value="${message}"/><br>
<%--    https://jakarta.ee/specifications/mvc/2.1/jakarta-mvc-spec-2.1.html#view_engines--%>
    这是使用request.getAttribute()方法获取的信息：<%=request.getAttribute("message")%><br>
</div>
<div>
    <form action="${pageContext.request.contextPath}/mvc/test/deleteMvc" method="POST">
        <input type="hidden" name="_method" value="DELETE"/>
        <input type="text" name="message" value="${message}"/>
        <input type="submit" value="提交"/>
    </form>
</div>
<div>
    <form action="${pageContext.request.contextPath}/mvc/test/csrf" method="post">
<%--       https://jakarta.ee/specifications/mvc/2.1/jakarta-mvc-spec-2.1.html#mvc_context --%>
        <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
        <input type="submit" value="测试csrf"/>
    </form>
</div>
</body>
</html>
