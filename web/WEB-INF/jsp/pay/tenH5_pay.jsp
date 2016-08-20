<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>微信支付</title>
</head>
<body>
<div style="display: none">${payInfo.retHtml}</div>

</body>
</html>
<script>
    callPay();

    function getById(_id) {
        return document.getElementById(_id).value;
    }

    function callPay() {
        var s = "weixin：//wap/pay";
        var appId = getById('appId');
        var timeStamp = getById('timeStamp');
        var nonceStr = getById('nonceStr');
        var package = getById('package');
        var signType = getById('signType');
        var paySign = getById('paySign');

        s += "appid=" + appId + "&noncestr=" + nonceStr + "&package=" + package + "&sign=" + paySign + "&timestamp=" + timeStamp;

    }
</script>
