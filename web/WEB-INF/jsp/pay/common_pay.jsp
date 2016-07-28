<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../common/base.jsp"></jsp:include>
<html>
<head>
    <title>收银台</title>
</head>
<body>
<header class="moon-header">
    <h1>
        <span class="title-main" data-title="付款详情">付款详情</span><br/><span class="title-sub">${payInfo.bizId}</span>
    </h1>
</header>
<div class="moon-content">
    <form id="payForm" action="<%=request.getContextPath()%>/payCenter/doWebPay.htm" method="post">
        <input type="hidden" name="bizId" value="${payInfo.bizId}">
        <input type="hidden" name="thdType" value="2">
        <div class="moon-list">
            <div class="moon-list-item">
                <span class="moon-list-item-title">订单信息</span>
                <span class="moon-list-item-text moon-ft-ellipsis moon-ft-black">${payInfo.goodName}</span>
            </div>
            <div class="moon-list-item">
                <span class="moon-list-item-title">需支付</span>
                <span class="moon-list-item-text moon-ft-ellipsis moon-ft-black">${payInfo.payAmount}</span>
            </div>
            <div class="moon-list-item">
                <span class="moon-list-item-title">付款方式</span>
                <span class="moon-list-item-text moon-ft-ellipsis moon-ft-black"><input type="radio" id="payType1" name="payType" value="1"/>微信&nbsp;<input type="radio" id="payType2" name="payType"
                                                                                                                                                            value="2" checked/>支付宝</span>
            </div>
        </div>
        <div class="moon-section">
            <button type="submit" class="moon-button moon-button-red">确认付款</button>
        </div>
    </form>
</div>
<footer>
    <div class="moon-logo"></div>
</footer>
</body>
</html>
<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/cashier.js"></script>
<script type="text/javascript">

    $(function () {
        $("input[name='payType']").change(function () {
            var payType = $("input[name='payType']:checked").val();
            if (payType == 1) {
                $("#payForm").attr("action", context + "/payCenter/doScanPay.htm");
            }
            else {
                $("#payForm").attr("action", context + "/payCenter/doWebPay.htm");
            }
        });
    });

</script>