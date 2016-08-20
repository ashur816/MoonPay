<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String context = request.getContextPath(); %>
<html>
<head>
    <title>退款</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <link rel="stylesheet" href="<%=context%>/static/css/mui.min.css"/>
    <link rel="stylesheet" href="<%=context%>/static/css/mui.moon.css"/>
</head>
<body>
<div class="mui-content">
    <ul class="mui-table-view">
        <li class="mui-table-view-cell">
            <label>订单号</label>
            <input id="flowIds" type="text" class="mui-input-clear mui-input " placeholder="支付流水号，逗号分隔">
            <button id="btnQry" type="button" class="mui-btn mui-btn-success span-right">查询</button>
        </li>
    </ul>

    <input type="hidden" readonly size="30" id="payType" value="${payInfo.payType}"/>
    <div id="payList"></div>

    <p></p>
    <ul id="btnReason" style="display: none" class="mui-table-view mui-table-view-striped mui-table-view-condensed">
        <li class="mui-table-view-cell">
            <label class="web-font">退款原因</label>
            <div class="div-center-tip">
                <input id="refundReason" type="text" class="mui-input-clear mui-input " placeholder="" value="测试退款"/>
            </div>
        </li>
        <button id="btnPay" type="button" class="mui-btn mui-btn-success mui-col-xs-12">退款</button>
    </ul>
</div>
</body>
</html>
<script src="<%=context%>/static/js/mui.min.js"></script>
<script src="<%=context%>/static/js/jquery-1.12.4.min.js"></script>
<script type="text/javascript">

    var context = "<%=context%>";

    $(function () {
        $("#btnPay").click(function () {
            var payType = $("#payType").val();
            if (payType == 2) {
                window.open(context + "/pay/doRefundPwd.htm?flowIds=" + $("#flowIds").val() + "&refundReason=" + $("#refundReason").val());
            }
            else {
                $.ajax({
                    type: "post",
                    url: context + "/pay/doRefund.htm",
                    dataType: "json",
                    data: {payType: payType, flowIds: $("#flowIds").val(), refundReason: $("#refundReason").val()},
                    success: function (data) {
                        alert(data);
                    },
                    error: function (data) {
                        alert(data.responseText);
                    }
                });
            }
        });

        $("#btnQry").click(function () {
            $.ajax({
                type: "post",
                url: context + "/pay/getRefund.htm",
                dataType: "json",
                data: {flowIds: $("#flowIds").val()},
                success: function (data) {
                    var arrList = eval(data);
                    var html = "";
                    for (var i = 0; i < arrList.length; i++) {
                        var payInfo = arrList[i];
                        html += '<p></p><ul class="mui-table-view mui-table-view-striped mui-table-view-condensed"><li class="mui-table-view-cell"><div class="mui-table"><div class="mui-table-cell mui-col-xs-10">'
                                + '<p>订单号：' + payInfo.bizId + '</p>'
                                + '<p>支付流水：' + payInfo.flowId + '</p>';
                        if (payInfo.payType == "1") {
                            html += '<p>支付渠道：微信</p>';
                        }
                        else if (payInfo.payType == "2") {
                            html += '<p>支付渠道：支付宝</p>';
                        }
                        else {
                            html += '<p>支付渠道：不详</p>';
                        }
                        html += '<p>退款金额：' + payInfo.payAmount + '</p>'
                                + '</div><div class="mui-table-cell mui-col-xs-2 mui-text-right"></div></div></li></ul>';
                    }
                    $("#payList").html(html);
                    $("#btnReason").css("display", "block");
                },
                error: function (data) {
                    alert(data.responseText);
                }
            });
        });
    });

</script>
