<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String context = request.getContextPath(); %>
<html>
<head>
    <title>收银台</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <link rel="stylesheet" href="<%=context%>/static/css/mui.min.css"/>
</head>
<body>
<header class="mui-bar mui-bar-nav">
    <h1 class="mui-title">收银台</h1>
</header>
<div class="mui-content">
    <h5 class="mui-content-padded">业务单号</h5>
    <input name="bizId" type="text" class="mui-input-clear" placeholder="请输入业务单号">
    <h5 class="mui-content-padded">业务类型</h5>
    <ul class="mui-table-view mui-table-view-radio">
        <li class="mui-table-view-cell">
            <a class="mui-navigate-right">外卖</a>
        </li>
        <li class="mui-table-view-cell mui-selected">
            <a class="mui-navigate-right">跑腿</a>
        </li>
    </ul>
    <div class="mui-button-row">
        <button id="btnPay" type="button" class="mui-btn mui-btn-primary">支付</button>
    </div>
</div>
</body>
</html>
<script src="<%=context%>/static/js/mui.min.js"></script>
<script src="<%=context%>/static/js/jquery-1.12.4.min.js"></script>
<script type="text/javascript">

    var context = "<%=context%>";

    $(function () {
        $("#btnPay").click(function () {
            var url = context + "/moon/cashier/toWebPay.htm";
            $("#payForm").attr("action", url);
            $("#payForm").submit();
        });
    });
</script>