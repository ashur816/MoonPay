package com.martin.constant;

/**
 * @author ZXY
 * @ClassName: PayParam
 * @Description: 支付参数
 * @date 2016/7/29 17:06
 */
public class PayParam {
    /* 静态参数 */
    // 字符编码格式 目前支持 gbk 或 utf-8
    public static final String inputCharset = "UTF-8";

    /*支付宝固定参数*/
    public static final String aliMapiUrl = "https://mapi.alipay.com/gateway.do";
    public static final String aliOpenUrl = "https://openapi.alipay.com/gateway.do";
    public static final String aliVerifyUrl = "https://mapi.alipay.com/gateway.do?service=notify_verify";
    //授权地址
    public static final String aliAuthUrl = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm";
    //授权模式
    public static final String aliAuthCode = "auth_base";
    //调用的接口名，查单
    public static final String aliQueryService = "alipay.trade.query";
    //调用的接口名，老支付接口
    public static final String aliOldPayService = "alipay.wap.create.direct.pay.by.user";
    //调用的接口名，支付接口
    public static final String aliPayService = "alipay.trade.wap.pay";
    //调用的接口名，企业付款接口-批量
    public static final String aliBatchTransferService = "batch_trans_notify";
    //调用的接口名，企业付款接口-单个
    public static final String aliSingleTransferService = "alipay.fund.trans.toaccount.transfer";
    //调用的接口名，退款接口
    public static final String aliRefundService = "refund_fastpay_by_platform_pwd";
    //调用的接口名，关单接口
    public static final String aliCloseService = "alipay.trade.close";
    // 支付类型 ，无需修改
    public static final String aliPaymentType = "1";
    //签名方式
    public static final String aliSignTypeMD5 = "MD5";
    public static final String aliSignTypeRSA = "RSA";

    /*微信固定参数*/
    public static final String tenOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static final String tenQueryUrl = "https://api.mch.weixin.qq.com/pay/orderquery";
    public static final String tenRefundUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    public static final String tenTransferUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    public static final String tenCloseUrl = "https://api.mch.weixin.qq.com/pay/closeorder";
    //授权地址
    public static final String tenAuthUrl = "https://open.weixin.qq.com/connect/oauth2/authorize";
    //授权模式
    public static final String tenAuthCode = "snsapi_base";
    //退款方式
    public static final String refundAccount = "REFUND_SOURCE_RECHARGE_FUNDS";
    //加密方式
    public static final String tenSignType = "MD5";
    //是否支持信用卡
    public static final String tenLimitPay = "no_credit";
    public static final String tenWebTradeType = "JSAPI";
    public static final String tenAppTradeType = "APP";


    //商品在第三方显示信息
    public static String webBody;
    public static String appBody;

    //证书路径
    public static String certPath;
    //主页地址
    public static String homeUrl;

    /*支付宝动态参数*/
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

    /*微信动态参数*/
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
