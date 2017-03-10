<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String context = request.getContextPath(); %>
<html>
<head>
    <title>企业付款</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <link rel="stylesheet" href="<%=context%>/static/css/mui.min.css"/>
</head>
<body>
<header class="mui-bar mui-bar-nav">
    <h1 class="mui-title">企业付款</h1>
    <a class="mui-pull-right mui-icon mui-icon-home" href="/index.jsp"></a>
</header>
<div class="mui-content">
    <h5 class="mui-content-padded">提现渠道</h5>
    <ul class="mui-table-view mui-table-view-radio">
        <li class="mui-table-view-cell mui-selected">
            <a class="mui-navigate-right" v="1">微信</a>
        </li>
        <li class="mui-table-view-cell">
            <a class="mui-navigate-right" v="2">支付宝</a>
        </li>
    </ul>
    <h5 class="mui-content-padded">提现账号</h5>
    <input id="thdNo" type="text" class="mui-input-clear" placeholder="请输入提现账号">
    <h5 class="mui-content-padded">真实姓名</h5>
    <input id="thdName" type="text" class="mui-input-clear" placeholder="请输入账号实名信息">
    <h5 class="mui-content-padded">提现金额</h5>
    <input id="drawAmount" type="text" class="mui-input-clear" placeholder="请输入提现金额">
    <div class="mui-button-row">
        <button id="btnDraw" type="button" class="mui-btn mui-btn-primary">转账</button>
    </div>
</div>

<div id="payHtml" style="display: none"></div>
</body>
</html>
<script src="<%=context%>/static/js/jquery-1.12.4.min.js"></script>
<script src="<%=context%>/static/js/mui.min.js"></script>
<script type="text/javascript">
    $(function () {
        var list = document.querySelector('.mui-table-view.mui-table-view-radio');
        var payType = 1;
        list.addEventListener('selected', function (e) {
            payType = e.detail.el.children[0].attributes["v"].value;
        });

//        $("#thdNo").val("632663267@qq.com");
        $("#thdNo").val("o6U0tuJA8ewtDsfhmR2rh4-7yDco");
        $("#thdName").val("张向阳");
        $("#drawAmount").val("0.01");


        $("#btnDraw").click(function () {
            var thdNo = $("#thdNo").val();
            var thdName = $("#thdName").val();
            var drawAmount = $("#drawAmount").val() * 100;
            $.ajax({
                type: "post",
                url: "/moon/cashier/doTransfer.htm",
                dataType: "json",
                data: {payType: payType, thdNo: thdNo, thdName: thdName, drawAmount: drawAmount},
                success: function (retInfo) {
                    if (retInfo.success == 1) {
                        var payInfo = retInfo.data["retHtml"];
                        if (payType == 2) {
                            $("#payHtml").html(payInfo);
                            document.forms['payForm'].submit();
                        }
                        else {
                            mui.alert(retInfo["message"]);
                        }
                    }
                    else {
                        mui.alert(retInfo["message"]);
                    }
                },
                error: function (data) {
                    mui.alert(data["message"]);
                }
            });
        });
    });
</script>