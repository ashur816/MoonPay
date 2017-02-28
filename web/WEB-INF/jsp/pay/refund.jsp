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

<nav class="mui-bar mui-bar-tab">
    <input id="refundReason" type="text" class="mui-input-clear" style="float:left;" placeholder="请输入退款原因">
    <button id="btnRefund" type="button" class="mui-btn-nav mui-btn-primary" style="float:right;margin: 5px 10px;width: 80px">退款</button>
</nav>

<div id="pullrefresh" class="mui-content mui-scroll-wrapper">
    <div id="payList" class="mui-scroll">
        <!--数据列表-->
    </div>
</div>

</body>
</html>
<script src="<%=context%>/static/js/jquery-1.12.4.min.js"></script>
<script src="<%=context%>/static/js/mui.min.js"></script>
<script type="text/javascript">
    mui.init({
        pullRefresh: {
            container: '#pullrefresh',
            down: {
                callback: pullDownRefresh
            },
            up: {
                contentrefresh: '正在加载...',
                callback: pullUpRefresh
            }
        }
    });

    $("#refundReason").val("zxy测试");

    if (mui.os.plus) {
        mui.plusReady(function () {
            setTimeout(function () {
                mui('#pullrefresh').pullRefresh().pullupLoading();
            }, 1000);
        });
    } else {
        mui.ready(function () {
            mui('#pullrefresh').pullRefresh().pullupLoading();
        });
    }

    /**
     * 下拉刷新具体业务实现
     */
    function pullDownRefresh() {
        $("#payList").html("<p></p>");
        setTimeout(function () {
            getPayInfo();
            mui('#pullrefresh').pullRefresh().endPulldownToRefresh(); //refresh completed
        }, 1000);
    }

    var count = 0;
    /**
     * 上拉加载具体业务实现
     */
    function pullUpRefresh() {
        $("#payList").html("<p></p>");
        setTimeout(function () {
            getPayInfo();
            mui('#pullrefresh').pullRefresh().endPullupToRefresh((++count > 2)); //参数为true代表没有更多数据了。
        }, 1000);
    }

    //获取支付信息
    function getPayInfo() {
        $.ajax({
            type: "post",
            url: "/moon/cashier/getRefund.htm",
            dataType: "json",
            data: {payType: "", appId: ""},
            success: function (retObj) {
                if (retObj.success == 1) {
                    var arrList = eval(retObj.data);
                    var html = "";
                    var payType;
                    var payState;
                    var len = arrList.length;
                    if (len == 0) {
                        $("#payList").html('<p style="text-align: center;">未查询到有效数据</p>');
                    }
                    else {
                        for (var i = 0; i < arrList.length; i++) {
                            var payInfo = arrList[i];
                            payType = payInfo.payType;
                            html += '<p></p><ul class="mui-table-view mui-table-view-striped mui-table-view-condensed"><li class="mui-table-view-cell mui-checkbox mui-left">'
                                + '<input name="selFlag" type="checkbox" flowId=' + payInfo.flowId + ' payType=' + payType + ' />'
                                + '<div class="mui-table"><div class="mui-table-cell mui-col-xs-10">'
                                + '<p>订单号：' + payInfo.bizId + '</p>'
                                + '<p>支付流水：' + payInfo.flowId + '</p>';
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
                    }
                }
                else {
                    $("#payList").html(html);
                    mui.alert(retObj.message);
                }
            },
            error: function (retObj) {
                mui.alert(retObj.message);
            }
        });
    }

    //退款操作
    $("#btnRefund").click(function () {
        var flowIds = "";
        var payType = null;
        var sameTypeFlag = true;
        $("input[name='selFlag']:checked").each(function (index, obj) {
            flowIds += obj.getAttribute("flowId") + ",";
            if (payType != null && payType != obj.getAttribute("payType")) {
                sameTypeFlag = false;
            }
            else {
                payType = obj.getAttribute("payType");
            }
        });
        if (!sameTypeFlag) {
            mui.alert("请选择同一种支付方式");
            return false;
        }
        if (flowIds == "") {
            mui.alert("请选择数据");
            return false;
        }

        var refundReason = $("#refundReason").val();
        if (refundReason == "") {
            mui.alert("请输入退款原因");
            return false;
        }
        if (payType == 2) {
            window.open("/moon/cashier/doRefundPwd.htm?flowIds=" + flowIds.substring(0, flowIds.length - 1) + "&refundReason=" + refundReason);
        }
        else {
            $.ajax({
                type: "post",
                url: "/moon/cashier/doRefund.htm",
                dataType: "json",
                data: {payType: payType, flowIds: flowIds, refundReason: refundReason},
                success: function (retObj) {
                    mui.alert(retObj.message);
                },
                error: function (retObj) {
                    mui.alert(retObj.message);
                }
            });
        }
    });
</script>