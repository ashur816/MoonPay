<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../common/base.jsp"></jsp:include>
<html>
<head>
    <title>退款</title>
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"/>
</head>
<body>
<div align="center">
    <input type="hidden" readonly size="30" id="flowIds" value="${flowIds}"/>
    <table>
        <c:forEach items="${payInfoList}" var="payInfo">

            <input type="hidden" readonly size="30" id="payType" value="${payInfo.payType}"/>
            <tr>
                <td align="right">订单号:</td>
                <td><input type="text" readonly size="30" value="${payInfo.bizId}"/>
            </tr>
            <tr>
                <td align="right">商品名:</td>
                <td><input type="text" readonly size="30" value="${payInfo.goodName}"/>
            </tr>
            <tr>
                <td align="right">支付渠道:</td>
                <c:choose>
                <c:when test="${payInfo.payType eq 1}">
                <td><input type="text" readonly size="30" value="微信"/>
                    </c:when>
                    <c:when test="${payInfo.payType eq 2}">
                <td><input type="text" readonly size="30" value="支付宝"/>
                    </c:when>
                    <c:otherwise>
                <td><input type="text" readonly size="30" value="不详"/>
                    </c:otherwise>
                    </c:choose>
            </tr>
            <tr>
                <td align="right">退款金额:</td>
                <td><input type="text" readonly size="30" value="${payInfo.payAmount}"/>
            </tr>
        </c:forEach>
    </table>
    <table>
        <tr>
            <td align="right">退款原因:</td>
            <td><input type="text" size="30" id="refundReason" value=""/>
        </tr>
        <tr>
            <td></td>
            <td><input id="btnPay" type="button" style="width: 70%; height: 30px" value="退款"/></td>
        </tr>
    </table>
</div>
</body>
</html>
<script type="text/javascript">

    $(function () {
        $("#btnPay").click(function () {
            var voucherId = $("option:checked").val();
            var payType = $("#payType").val();
            if (payType == 2) {
                location.href = context + "/payCenter/doRefundPwd.htm?flowIds=" + $("#flowIds").val() + "&refundReason=" + $("#refundReason").val();
            }
            else {
                location.href = context + "/payCenter/doRefund.htm?flowIds=" + $("#flowIds").val() + "&refundReason=" + $("#refundReason").val();
            }

        });
    });

</script>
