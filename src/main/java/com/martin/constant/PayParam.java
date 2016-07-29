package com.martin.constant;

/**
 * @ClassName: PayParam
 * @Description: 支付参数
 * @author ZXY
 * @date 2016/7/29 17:06
 */
public class PayParam {
    //商品在第三方显示信息
    public static String body;

    //证书路径
    public static String certPath;

    public static String aliUrl;

    public static String aliVerifyUrl;

    //授权模式
    public static String aliAuthCode;

    //授权地址
    public static String aliAuthUrl;

    //授权回调地址
    public static String aliAuthRetUrl;

    //调用的接口名，支付
    public static String aliPayService;

    //调用的接口名，退款
    public static String aliRefundService;

    //调用应用id
    public static String aliAppId;

    // 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String aliPartner;

    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
    public static String aliSellerId;

    //商户的私钥,需要PKCS8格式，RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
    public static String aliPrivateKey;

    // 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String aliPublicKey;

    public static String aliMd5Key;

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String aliNotifyUrl;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String aliReturnUrl;

    // 退款服务器异步通知页面路径
    public static String aliRefundUrl;

    // 签名方式
    public static String aliSignType;

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String aliInputCharset;

    // 支付类型 ，无需修改
    public static String aliPaymentType;

    // 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
    public static String aliAntiPhishingKey;

    // 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
    public static String aliExterInvokeIp;

    // 设置未付款交易的超时时间，一旦超时，该笔交易就会自动被关闭
    public static String aliItBPay;


    public static String tenOrderUrl;
    public static String tenQueryUrl;
    public static String tenPayUrl;
    public static String tenRefundUrl;
    //授权模式
    public static String tenAuthCode;
    //授权地址
    public static String tenAuthUrl;
    //授权回调地址
    public static String tenAuthRetUrl;
    //微信分配的公众账号ID
    public static String tenAppId;
    //微信支付分配的商户号
    public static String tenMchId;
    //设备号 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
    public static String tenDeviceInfo;
    //密钥，微信公众号的 AppSecret
    public static String tenAppSecret;
    //密钥，在微信支付账号中配置，不是微信公众号的 AppSecret
    public static String tenPrivateKey;
    public static String tenNotifyUrl;
    public static String tenReturnUrl;
    //机密方式
    public static String tenSignType;
    //是否支持信用卡
    public static String tenLimitPay;
    //是否支持信用卡
    public static String tenTradeType;

    public void setBody(String body) {
        PayParam.body = body;
    }

    public void setCertPath(String certPath) {
        PayParam.certPath = certPath;
    }

    public void setAliUrl(String aliUrl) {
        PayParam.aliUrl = aliUrl;
    }

    public void setAliVerifyUrl(String aliVerifyUrl) {
        PayParam.aliVerifyUrl = aliVerifyUrl;
    }

    public void setAliAuthCode(String aliAuthCode) {
        PayParam.aliAuthCode = aliAuthCode;
    }

    public void setAliAuthUrl(String aliAuthUrl) {
        PayParam.aliAuthUrl = aliAuthUrl;
    }

    public void setAliAuthRetUrl(String aliAuthRetUrl) {
        PayParam.aliAuthRetUrl = aliAuthRetUrl;
    }

    public void setAliPayService(String aliPayService) {
        PayParam.aliPayService = aliPayService;
    }

    public void setAliRefundService(String aliRefundService) {
        PayParam.aliRefundService = aliRefundService;
    }

    public void setAliAppId(String aliAppId) {
        PayParam.aliAppId = aliAppId;
    }

    public void setAliPartner(String aliPartner) {
        PayParam.aliPartner = aliPartner;
    }

    public void setAliSellerId(String aliSellerId) {
        PayParam.aliSellerId = aliSellerId;
    }

    public void setAliPrivateKey(String aliPrivateKey) {
        PayParam.aliPrivateKey = aliPrivateKey;
    }

    public void setAliPublicKey(String aliPublicKey) {
        PayParam.aliPublicKey = aliPublicKey;
    }

    public void setAliMd5Key(String aliMd5Key) {
        PayParam.aliMd5Key = aliMd5Key;
    }

    public void setAliNotifyUrl(String aliNotifyUrl) {
        PayParam.aliNotifyUrl = aliNotifyUrl;
    }

    public void setAliReturnUrl(String aliReturnUrl) {
        PayParam.aliReturnUrl = aliReturnUrl;
    }

    public void setAliRefundUrl(String aliRefundUrl) {
        PayParam.aliRefundUrl = aliRefundUrl;
    }

    public void setAliSignType(String aliSignType) {
        PayParam.aliSignType = aliSignType;
    }

    public void setAliInputCharset(String aliInputCharset) {
        PayParam.aliInputCharset = aliInputCharset;
    }

    public void setAliPaymentType(String aliPaymentType) {
        PayParam.aliPaymentType = aliPaymentType;
    }

    public void setAliAntiPhishingKey(String aliAntiPhishingKey) {
        PayParam.aliAntiPhishingKey = aliAntiPhishingKey;
    }

    public void setAliExterInvokeIp(String aliExterInvokeIp) {
        PayParam.aliExterInvokeIp = aliExterInvokeIp;
    }

    public void setAliItBPay(String aliItBPay) {
        PayParam.aliItBPay = aliItBPay;
    }

    public void setTenOrderUrl(String tenOrderUrl) {
        PayParam.tenOrderUrl = tenOrderUrl;
    }

    public void setTenQueryUrl(String tenQueryUrl) {
        PayParam.tenQueryUrl = tenQueryUrl;
    }

    public void setTenPayUrl(String tenPayUrl) {
        PayParam.tenPayUrl = tenPayUrl;
    }

    public void setTenRefundUrl(String tenRefundUrl) {
        PayParam.tenRefundUrl = tenRefundUrl;
    }

    public void setTenAuthCode(String tenAuthCode) {
        PayParam.tenAuthCode = tenAuthCode;
    }

    public void setTenAuthUrl(String tenAuthUrl) {
        PayParam.tenAuthUrl = tenAuthUrl;
    }

    public void setTenAuthRetUrl(String tenAuthRetUrl) {
        PayParam.tenAuthRetUrl = tenAuthRetUrl;
    }

    public void setTenAppId(String tenAppId) {
        PayParam.tenAppId = tenAppId;
    }

    public void setTenMchId(String tenMchId) {
        PayParam.tenMchId = tenMchId;
    }

    public void setTenDeviceInfo(String tenDeviceInfo) {
        PayParam.tenDeviceInfo = tenDeviceInfo;
    }

    public void setTenAppSecret(String tenAppSecret) {
        PayParam.tenAppSecret = tenAppSecret;
    }

    public void setTenPrivateKey(String tenPrivateKey) {
        PayParam.tenPrivateKey = tenPrivateKey;
    }

    public void setTenNotifyUrl(String tenNotifyUrl) {
        PayParam.tenNotifyUrl = tenNotifyUrl;
    }

    public void setTenReturnUrl(String tenReturnUrl) {
        PayParam.tenReturnUrl = tenReturnUrl;
    }

    public void setTenSignType(String tenSignType) {
        PayParam.tenSignType = tenSignType;
    }

    public void setTenLimitPay(String tenLimitPay) {
        PayParam.tenLimitPay = tenLimitPay;
    }

    public void setTenTradeType(String tenTradeType) {
        PayParam.tenTradeType = tenTradeType;
    }
}
