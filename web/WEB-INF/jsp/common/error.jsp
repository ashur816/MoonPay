<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="base.jsp"></jsp:include>
<html>
<head>
    <title>错误</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"/>
</head>
<body>
<div class="moon-error">
    <p>${error}</p>
</div>
</body>
</html>