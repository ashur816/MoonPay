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

    function onBridgeReady() {
        var appId = getById('appId');
        var timeStamp = getById('timeStamp');
        var nonceStr = getById('nonceStr');
        var package = getById('package');
        var signType = getById('signType');
        var paySign = getById('paySign');
        WeixinJSBridge.invoke('getBrandWCPayRequest', {
            "appId": appId,//"wx2421b1c4370ec43b", //公众号名称，由商户传入
            "timeStamp": timeStamp,//"1395712654", //时间戳，自1970年以来的秒数
            "nonceStr": nonceStr,//"e61463f8efa94090b1f366cccfbbb444", //随机串
            "package": package,//"prepay_id=u802345jgfjsdfgsdg888",
            "signType": signType,//"MD5", //微信签名方式:
            "paySign": paySign//"70EA570631E4BB79628FBCA90534C63FF7FADD89" //微信签名
        }, function (res) { // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回  ok，但并不保证它绝对可靠。
            if (res.err_msg == "get_brand_wcpay_request:ok") {
                alert("支付成功");
            }
            if (res.err_msg == "get_brand_wcpay_request:cancel") {
                alert("交易取消");
            }
            if (res.err_msg == "get_brand_wcpay_request:fail") {
                alert("支付失败");
            }
//            WeixinJSBridge.call('closeWindow');
        });
    }

    function callPay() {
        if (typeof WeixinJSBridge == "undefined") {
            if (document.addEventListener) {
                document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
            } else if (document.attachEvent) {
                document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
            }
        } else {
            onBridgeReady();
        }
    }
</script>
