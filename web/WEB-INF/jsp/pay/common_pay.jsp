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
<input type="text" value="订单号:"/><input id="bizId" value="${payInfo.bizId}"/><br/>
<input type="text" value="商品名:"/><input value="${payInfo.goodName}"/><br/>
<input type="text" value="金额:"/><input value="${payInfo.payAmount}"/><br/>

<input type="radio" id="payType2" name="payType" value="2" checked/><label for="payType2"> 支付宝</label><br/>
<input type="radio" id="payType3" name="payType" value="3"/> <label for="payType3"> 招商银行</label><br/>
<input id="btnPay" type="button" value="支付"/>

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