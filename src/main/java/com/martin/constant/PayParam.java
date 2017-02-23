package com.martin.constant;

/**
 * @ClassName: PayParam
 * @Description: 支付参数
 * @author ZXY
 * @date 2016/7/29 17:06
 */
public class PayParam {
    //商品在第三方显示信息
    public static String webBody;
    public static String appBody;

    //证书路径
    public static String certPath;
    //主页地址
    public static String homeUrl;

    public static String aliMapiUrl;
    public static String aliOpenUrl;
    public static String aliVerifyUrl;

    //授权地址
    public static String aliAuthUrl;
    //授权模式
    public static String aliAuthCode;
    //调用的接口名，查单
    public static String aliQueryService;
    //调用的接口名，老支付接口
    public static String aliOldPayService;
    //调用的接口名，支付
    public static String aliPayService;
    //调用的接口名，企业付款
    public static String aliTransferService;
    //调用的接口名，退款
    public static String aliRefundService;
    //调用的接口名，关单
    public static String aliCloseService;
    // WEB签名方式
    public static String aliWebSignType;
    // APP签名方式
    public static String aliAppSignType;
    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String aliInputCharset;
    // 支付类型 ，无需修改
    public static String aliPaymentType;
    //授权回调地址
    public static String aliAuthRetUrl;
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String aliReturnUrl;

    public static String aliRefundNotifyUrl;
    public static String aliTransferNotifyUrl;
    public static String aliWebNotifyUrl;
    public static String aliAppNotifyUrl;

    // 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String aliPartnerId;
    public static String aliAccountNo;
    public static String aliAccountName;
    // 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String aliAliPublicKey;
    // 合作伙伴MD5密钥
    public static String aliMD5Key;

    // WEB应用
    public static String aliWebAppId;
    public static String aliWebPrivateKey;

    // APP应用
    public static String aliAppAppId;
    public static String aliAppPrivateKey;


    public static String tenOrderUrl;
    public static String tenQueryUrl;
    public static String tenPayUrl;
    public static String tenRefundUrl;
    public static String tenTransferUrl;
    public static String tenCloseUrl;
    //授权地址
    public static String tenAuthUrl;
    //授权模式
    public static String tenAuthCode;
    //退款方式
    public static String refundAccount;
    //加密方式
    public static String tenSignType;
    //是否支持信用卡
    public static String tenLimitPay;
    public static String tenWebTradeType;
    public static String tenAppTradeType;

    public static String tenWebNotifyUrl;
    public static String tenAppNotifyUrl;

    //WEB
    public static String tenWebAppId;
    public static String tenWebMchId;
    public static String tenDeviceInfo;
    //密钥，在微信支付账号中配置，不是微信公众号的 AppSecret
    public static String tenWebPrivateKey;
    //密钥，微信公众号的 AppSecret
    public static String tenAppSecret;
    public static String tenWebAuthRetUrl;
    public static String tenWebReturnUrl;

    //APP
    public static String tenAppAppId;
    public static String tenAppMchId;
    public static String tenAppPrivateKey;

    public void setWebBody(String webBody) {
        this.webBody = webBody;
    }

    public void setAppBody(String appBody) {
        this.appBody = appBody;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public void setAliMapiUrl(String aliMapiUrl) {
        this.aliMapiUrl = aliMapiUrl;
    }

    public void setAliOpenUrl(String aliOpenUrl) {
        this.aliOpenUrl = aliOpenUrl;
    }

    public void setAliVerifyUrl(String aliVerifyUrl) {
        this.aliVerifyUrl = aliVerifyUrl;
    }

    public void setAliAuthUrl(String aliAuthUrl) {
        this.aliAuthUrl = aliAuthUrl;
    }

    public void setAliAuthCode(String aliAuthCode) {
        this.aliAuthCode = aliAuthCode;
    }

    public void setAliQueryService(String aliQueryService) {
        this.aliQueryService = aliQueryService;
    }

    public void setAliOldPayService(String aliOldPayService) {
        this.aliOldPayService = aliOldPayService;
    }

    public void setAliPayService(String aliPayService) {
        this.aliPayService = aliPayService;
    }

    public void setAliTransferService(String aliTransferService) {
        this.aliTransferService = aliTransferService;
    }

    public void setAliRefundService(String aliRefundService) {
        this.aliRefundService = aliRefundService;
    }

    public void setAliCloseService(String aliCloseService) {
        this.aliCloseService = aliCloseService;
    }

    public void setAliWebSignType(String aliWebSignType) {
        this.aliWebSignType = aliWebSignType;
    }

    public void setAliAppSignType(String aliAppSignType) {
        this.aliAppSignType = aliAppSignType;
    }

    public void setAliInputCharset(String aliInputCharset) {
        this.aliInputCharset = aliInputCharset;
    }

    public void setAliPaymentType(String aliPaymentType) {
        this.aliPaymentType = aliPaymentType;
    }

    public void setAliAuthRetUrl(String aliAuthRetUrl) {
        this.aliAuthRetUrl = aliAuthRetUrl;
    }

    public void setAliReturnUrl(String aliReturnUrl) {
        this.aliReturnUrl = aliReturnUrl;
    }

    public void setAliRefundNotifyUrl(String aliRefundNotifyUrl) {
        this.aliRefundNotifyUrl = aliRefundNotifyUrl;
    }

    public void setAliTransferNotifyUrl(String aliTransferNotifyUrl) {
        this.aliTransferNotifyUrl = aliTransferNotifyUrl;
    }

    public void setAliWebNotifyUrl(String aliWebNotifyUrl) {
        this.aliWebNotifyUrl = aliWebNotifyUrl;
    }

    public void setAliAppNotifyUrl(String aliAppNotifyUrl) {
        this.aliAppNotifyUrl = aliAppNotifyUrl;
    }

    public void setAliPartnerId(String aliPartnerId) {
        this.aliPartnerId = aliPartnerId;
    }

    public void setAliAccountNo(String aliAccountNo) {
        this.aliAccountNo = aliAccountNo;
    }

    public void setAliAccountName(String aliAccountName) {
        this.aliAccountName = aliAccountName;
    }

    public void setAliAliPublicKey(String aliAliPublicKey) {
        this.aliAliPublicKey = aliAliPublicKey;
    }

    public void setAliMD5Key(String aliMD5Key) {
        this.aliMD5Key = aliMD5Key;
    }

    public void setAliWebAppId(String aliWebAppId) {
        this.aliWebAppId = aliWebAppId;
    }

    public void setAliWebPrivateKey(String aliWebPrivateKey) {
        this.aliWebPrivateKey = aliWebPrivateKey;
    }

    public void setAliAppAppId(String aliAppAppId) {
        this.aliAppAppId = aliAppAppId;
    }

    public void setAliAppPrivateKey(String aliAppPrivateKey) {
        this.aliAppPrivateKey = aliAppPrivateKey;
    }

    public void setTenOrderUrl(String tenOrderUrl) {
        this.tenOrderUrl = tenOrderUrl;
    }

    public void setTenQueryUrl(String tenQueryUrl) {
        this.tenQueryUrl = tenQueryUrl;
    }

    public void setTenPayUrl(String tenPayUrl) {
        this.tenPayUrl = tenPayUrl;
    }

    public void setTenRefundUrl(String tenRefundUrl) {
        this.tenRefundUrl = tenRefundUrl;
    }

    public void setTenTransferUrl(String tenTransferUrl) {
        this.tenTransferUrl = tenTransferUrl;
    }

    public void setTenCloseUrl(String tenCloseUrl) {
        this.tenCloseUrl = tenCloseUrl;
    }

    public void setTenAuthUrl(String tenAuthUrl) {
        this.tenAuthUrl = tenAuthUrl;
    }

    public void setTenAuthCode(String tenAuthCode) {
        this.tenAuthCode = tenAuthCode;
    }

    public void setRefundAccount(String refundAccount) {
        this.refundAccount = refundAccount;
    }

    public void setTenSignType(String tenSignType) {
        this.tenSignType = tenSignType;
    }

    public void setTenLimitPay(String tenLimitPay) {
        this.tenLimitPay = tenLimitPay;
    }

    public void setTenWebTradeType(String tenWebTradeType) {
        this.tenWebTradeType = tenWebTradeType;
    }

    public void setTenAppTradeType(String tenAppTradeType) {
        this.tenAppTradeType = tenAppTradeType;
    }

    public void setTenWebNotifyUrl(String tenWebNotifyUrl) {
        this.tenWebNotifyUrl = tenWebNotifyUrl;
    }

    public void setTenAppNotifyUrl(String tenAppNotifyUrl) {
        this.tenAppNotifyUrl = tenAppNotifyUrl;
    }

    public void setTenWebAppId(String tenWebAppId) {
        this.tenWebAppId = tenWebAppId;
    }

    public void setTenWebMchId(String tenWebMchId) {
        this.tenWebMchId = tenWebMchId;
    }

    public void setTenDeviceInfo(String tenDeviceInfo) {
        this.tenDeviceInfo = tenDeviceInfo;
    }

    public void setTenWebPrivateKey(String tenWebPrivateKey) {
        this.tenWebPrivateKey = tenWebPrivateKey;
    }

    public void setTenAppSecret(String tenAppSecret) {
        this.tenAppSecret = tenAppSecret;
    }

    public void setTenWebAuthRetUrl(String tenWebAuthRetUrl) {
        this.tenWebAuthRetUrl = tenWebAuthRetUrl;
    }

    public void setTenWebReturnUrl(String tenWebReturnUrl) {
        this.tenWebReturnUrl = tenWebReturnUrl;
    }

    public void setTenAppAppId(String tenAppAppId) {
        this.tenAppAppId = tenAppAppId;
    }

    public void setTenAppMchId(String tenAppMchId) {
        this.tenAppMchId = tenAppMchId;
    }

    public void setTenAppPrivateKey(String tenAppPrivateKey) {
        this.tenAppPrivateKey = tenAppPrivateKey;
    }
}
