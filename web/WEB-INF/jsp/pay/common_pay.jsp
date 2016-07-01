<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../common/base.jsp"></jsp:include>
<%
    String path = request.getContextPath();
%>
<html>
<head>
    <title>收银台</title>
</head>
<body>
<div align="center">
    <table>
        <tr>
            <td align="right">订单号:</td>
            <td><input type="text" size="30" name="merchantId" value="${payInfo.bizId}"/>
        </tr>
        <tr>
            <td align="right">商品名:</td>
            <td><input type="text" size="30" name="orderId" value="${payInfo.goodName}"/>
        </tr>
        <tr>
            <td align="right">金额:</td>
            <td><input type="text" size="30" name="orderId" value="${payInfo.payAmount}"/>
        </tr>
        <tr>
            <td></td>
            <td><input type="radio" id="payType2" name="payType" value="2" checked/><label for="payType2"> 支付宝</label></td>
        </tr>
        <tr>
            <td></td>
            <td><input type="radio" id="payType3" name="payType" value="3"/> <label for="payType3"> 招商银行</label></td>
        </tr>
        <tr>
            <td></td>
            <td><input id="btnPay" type="button" style="width: 70%; height: 30px" value="支&nbsp;付"/></td>
        </tr>
    </table>
</div>
</body>
</html>
<script type="text/javascript">

    $(function () {
        $("#btnPay").click(function () {
            var payType = $("input[name='payType']:checked").val();
            location.href = "<%=path%>/payCenter/doWebPay.htm?payType=" + payType + "&bizId=" + $("#bizId").val();
        });
    });

</script>