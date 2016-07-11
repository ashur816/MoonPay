<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../common/base.jsp"></jsp:include>
<html>
<head>
    <title>收银台</title>
</head>
<body>
<div align="center">
    <table>
        <tr>
            <td align="right">订单号:</td>
            <td><input type="text" readonly size="30" id="bizId" value="${payInfo.bizId}"/>
        </tr>
        <tr>
            <td align="right">商品名:</td>
            <td><input type="text" readonly size="30" id="orderId" value="${payInfo.goodName}"/>
        </tr>
        <tr>
            <td align="right">金额:</td>
            <td><input type="text" readonly size="30" id="payAmount" value="${payInfo.payAmount}"/>
        </tr>
        <tr>
            <td align="right">代金券:</td>
            <td>
                <select id="voucher" style="width: 213px">
                    <option ext="0">不使用代金券</option>
                    <c:forEach var="item" items="${payInfo.voucherList}">
                        <option value="${item.voucherId}" ext="${item.voucherValue}">
                            <fmt:formatNumber value="${item.voucherValue/100}" pattern="0.00" currencyCode="USD"/>元
                        </option>
                    </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><input type="radio" id="payType1" name="payType" value="1" checked/> <label for="payType1">微信</label></td>
        </tr>
        <tr>
            <td></td>
            <td><input type="radio" id="payType2" name="payType" value="2"/><label for="payType2">支付宝</label></td>
        </tr>
        <tr>
            <td></td>
            <td><input id="btnPay" type="button" style="width: 70%; height: 30px" value="支付"/></td>
        </tr>
    </table>
</div>
</body>
</html>
<script type="text/javascript">

    $(function () {
        var tmp1 = $("#payAmount").val() * 100;
        var tmp2 = tmp1 / 100;
        $("#btnPay").val("支付" + tmp2.toFixed(2) + "元");

        $("#btnPay").click(function () {
            var voucherId = $("option:checked").val();
            var payType = $("input[name='payType']:checked").val();
            if (payType == 1) {
                location.href = context + "/payCenter/doScanPay.htm?payType=" + payType + "&bizId=" + $("#bizId").val() + "&voucherId=" + voucherId;
            }
            else {
                location.href = context + "/payCenter/doWebPay.htm?payType=" + payType + "&bizId=" + $("#bizId").val() + "&voucherId=" + voucherId;
            }

        });

        $("#voucher").change(function () {
            var voucherValue = $("option:checked").attr("ext");
            var oldAmount = $("#payAmount").val() * 100;
            var payAmount = 0;
            if (oldAmount >= voucherValue) {
                payAmount = (oldAmount - voucherValue) / 100;
            }
            else {
                payAmount = 0;
            }
            $("#btnPay").val("支付" + payAmount.toFixed(2) + "元");
        });
    });

</script>