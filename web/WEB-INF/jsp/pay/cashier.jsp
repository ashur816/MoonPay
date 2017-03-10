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
    <a class="mui-pull-right mui-icon mui-icon-home" href="/index.jsp"></a>
</header>
<div class="mui-content">
    <h5 class="mui-content-padded">业务单号</h5>
    <input id="bizId" type="text" class="mui-input-clear" placeholder="请输入业务单号">
    <h5 class="mui-content-padded">业务类型</h5>
    <ul class="mui-table-view mui-table-view-radio">
        <li class="mui-table-view-cell">
            <a class="mui-navigate-right" v="1">外卖</a>
        </li>
        <li class="mui-table-view-cell mui-selected">
            <a class="mui-navigate-right" v="2">跑腿</a>
        </li>
    </ul>
    <div class="mui-button-row">
        <button id="btnPay" type="button" style="width:100px" class="mui-btn mui-btn-primary">支付</button>
    </div>
</div>

<form id="payForm" name="payForm" method="get" action="<%=context%>/moon/cashier/toWebPay.htm">
    <input name="bizId" type="hidden"/>
    <input name="bizType" type="hidden"/>
</form>

</body>
</html>
<script src="<%=context%>/static/js/jquery-1.12.4.min.js"></script>
<script src="<%=context%>/static/js/mui.min.js"></script>
<script type="text/javascript">
    $(function () {
        var list = document.querySelector('.mui-table-view.mui-table-view-radio');
        var bizType = 1;
        list.addEventListener('selected', function (e) {
            bizType = e.detail.el.children[0].attributes["v"].value;
        });

        var bizId = Math.ceil(Math.random() * 10000000);
        $("#bizId").val(bizId);

        $("#btnPay").click(function () {
            $("#payForm input[name='bizId']").val($("#bizId").val());
            $("#payForm input[name='bizType']").val(bizType);
            $("#payForm").submit();
        });
    });
</script>