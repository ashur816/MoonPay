<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../common/base.jsp"></jsp:include>
<html>
<head>
    <title>企业付款</title>
</head>
<body>
<div align="center">
    <table>
        <tr>
            <td align="right">提现账号:</td>
            <td><input type="text" size="30" id="acctId" value=""/>
        </tr>
        <tr>
            <td align="right">提现金额:</td>
            <td><input type="text" size="30" id="drawAmount" value=""/>元
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
            <td><input id="btnDraw" type="button" style="width: 70%; height: 30px" value="提现"/></td>
        </tr>
    </table>
</div>
</body>
</html>
<script type="text/javascript">
    $(function () {
        $("#btnDraw").click(function () {
            var acctId = $("#acctId").val();
            var drawAmount = $("#drawAmount").val() * 100;
            var payType = $("input[name='payType']:checked").val();
            $.ajax({
                type: "post",
                url: context + "/pay/doWithdraw.htm",
                dataType: "json",
                data: {payType: payType, acctId: acctId, drawAmount: drawAmount},
                success: function (data) {
                    alert(data);
                },
                error: function (data) {
                    alert(data.responseText);
                }
            });
        });
    });
</script>