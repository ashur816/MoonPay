<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>支付宝支付</title>
</head>
<body>
<div style="display: none">${payInfo.retHtml}</div>

</body>
</html>
<script type="text/javascript">

    submit();

    function submit() {
        document.forms['payForm'].submit();
    }

</script>