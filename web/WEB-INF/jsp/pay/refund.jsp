<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String context = request.getContextPath(); %>
<html>
<head>
    <title>退款</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <link rel="stylesheet" href="<%=context%>/static/css/mui.min.css"/>
</head>
<body>
<header class="mui-bar mui-bar-nav">
    <h1 class="mui-title">退款</h1>
</header>
<div class="mui-content">
    <h5 class="mui-content-padded">支付终端</h5>
    <ul id="appId" class="mui-table-view mui-table-view-radio">
        <li class="mui-table-view-cell mui-selected">
            <a class="mui-navigate-right" v="moon_web">WEB</a>
        </li>
        <li class="mui-table-view-cell">
            <a class="mui-navigate-right" v="moon_app">APP</a>
        </li>
    </ul>
    <h5 class="mui-content-padded">支付方式</h5>
    <ul id="payType" class="mui-table-view mui-table-view-radio">
        <li class="mui-table-view-cell mui-selected">
            <a class="mui-navigate-right" v="1">微信</a>
        </li>
        <li class="mui-table-view-cell">
            <a class="mui-navigate-right" v="2">支付宝</a>
        </li>
    </ul>
    <h5 class="mui-content-padded">支付流水号</h5>
    <input id="flowId" type="text" class="mui-input-clear" placeholder="请输入支付流水号">
    <h5 class="mui-content-padded">退款原因</h5>
    <input id="refundReason" type="text" class="mui-input-clear" placeholder="请输入退款原因">
    <div class="mui-button-row">
        <button id="btnQry" type="button" class="mui-btn mui-btn-primary">查询</button>
    </div>

    <div id="payList"></div>
    <div class="mui-button-row">
        <button id="btnRefund" type="button" class="mui-btn mui-btn-primary">退款</button>
    </div>
</div>

</body>
</html>
<script src="<%=context%>/static/js/jquery-1.12.4.min.js"></script>
<script src="<%=context%>/static/js/mui.min.js"></script>
<script type="text/javascript">
    $(function () {
        var appId = "moon_web";
        document.getElementById("appId").addEventListener('selected', function (e) {
            appId = e.detail.el.children[0].attributes["v"].value;
        });

        var payType = 1;
        document.getElementById("payType").addEventListener('selected', function (e) {
            payType = e.detail.el.children[0].attributes["v"].value;
        });

        $("#btnRefund").click(function () {
            var flowIds = "";
            $("input[name='selFlag']:checked").each(function (index, obj) {
                flowIds += obj.getAttribute("flowId") + ",";
            });
            if (flowIds == "") {
                mui.alert("请选择数据");
                return false;
            }
            if (payType == 2) {
                window.open("/moon/cashier/doRefundPwd.htm?flowIds=" + flowIds.substring(0, flowIds.length - 1) + "&refundReason=" + $("#refundReason").val());
            }
            else {
                $.ajax({
                    type: "post",
                    url: "/moon/cashier/doRefund.htm",
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
                url: "/moon/cashier/getRefund.htm",
                dataType: "json",
                data: {payType: payType, appId: appId, flowId: $("#flowId").val()},
                success: function (retObj) {
                    if (retObj.success == 1) {
                        var arrList = eval(retObj.data);
                        var html = "";
                        var payType;
                        var payState;
                        var len = arrList.length;
                        if (len == 0) {
                            mui.alert("未查询到有效数据");
                        }
                        else {
                            for (var i = 0; i < len; i++) {
                                var payInfo = arrList[i];
                                html += '<p></p><ul class="mui-table-view mui-table-view-striped mui-table-view-condensed"><li class="mui-table-view-cell mui-checkbox mui-left">'
                                    + '<input name="selFlag" type="checkbox" flowId=' + payInfo.flowId + ' checked/>'
                                    + '<div class="mui-table"><div class="mui-table-cell mui-col-xs-10">'
                                    + '<p>订单号：' + payInfo.bizId + '</p>'
                                    + '<p>支付流水：' + payInfo.flowId + '</p>';
                                payType = payInfo.payType;
                                if (payType == "1") {
                                    html += '<p>支付渠道：微信</p>';
                                }
                                else if (payType == "2") {
                                    html += '<p>支付渠道：支付宝</p>';
                                }
                                else {
                                    html += '<p>支付渠道：不详</p>';
                                }

                                payState = payInfo.payState;
                                if (payState == 1) {
                                    html += '<p>支付状态：已支付</p>';
                                }
                                else {
                                    html += '<p>支付状态：未支付</p>';
                                }

                                html += '<p>退款金额：' + payInfo.payAmount + '元</p>'
                                    + '</div><div class="mui-table-cell mui-col-xs-2 mui-text-right"></div></div></li></ul></div>';
                            }
                            $("#payList").html(html);
                            $("#payType").val(payType);
                            $("#showNoResult").css("display", "none");
                            $("#btnReason").css("display", "block");
                        }
                    }
                    else {
                        $("#payList").html(html);
                        alert(retObj.message);
                    }
                },
                error: function (data) {
                    alert(retObj.message);
                }
            });
        });
    });
</script>