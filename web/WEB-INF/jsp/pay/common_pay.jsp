<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String context = request.getContextPath(); %>
<html>
<head>
    <title>收银台</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <link rel="stylesheet" href="<%=context%>/static/css/mui.min.css"/>
    <link rel="stylesheet" href="<%=context%>/static/css/mui.moon.css"/>
</head>
<body>
<div class="mui-content">
    <form id="payForm" method="post" class="mui-input-group">
        <div class="mui-input-row">
            <label>业务单号</label>
            <input name="bizId" type="text" class="mui-input-clear" placeholder="请输入业务单号">
        </div>
        <div class="mui-input-row mui-radio mui-left">
            <label>业务类型</label>
            <input name="bizType" type="radio">
        </div>
        <div class="mui-input-row mui-radio mui-left">
            <label>业务类型</label>
            <input name="bizType" type="radio">
        </div>
        <div class="mui-button-row">
            <button id="btnPay" type="button" class="mui-btn mui-btn-primary">支付</button>
        </div>
    </form>
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