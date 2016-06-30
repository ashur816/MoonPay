package com.martin.constant;

/**
 * @ClassName: PayConstant
 * @Description: 支付常量
 * @author ZXY
 * @date 2016/6/1 21:06
 */
public class PayConstant {
    /* 支付状态 */
    public static int ALL_PAY_STATE = -1;//所有状态

    public static int PAY_NOT = 0;//未支付

    public static int PAY_UN_BACK = 5;//已支付成功，但第三方未回调

    public static int PAY_UN_BIZ = 2;//已支付成功，但业务未处理

    public static int PAY_ERROR_BIZ = 3;//已支付成功，但业务处理失败

    public static int PAY_SUCCESS = 1;//支付成功，且业务处理成功

    public static int PAY_FAIL = 8;//支付失败

    /* 业务类型 */
    public static int BIZ_TYPE_GRAB = 1;
    public static int BIZ_TYPE_WITHDRAW = 2;

    /* 账户类型 */
    public static int ACCT_TYPE_ZD = 0;
    public static int ACCT_TYPE_USER = 1;

    /* 通知类型 */
    public static String NOTICE_RETURN = "return";//同步通知

    public static String NOTICE_NOTIFY = "notify";//异步通知

    public static int STATE_0 = 0;//数据状态--删除
    public static int STATE_1 = 1;//数据状态--正常

    //商品在第三方显示信息
    public static String BODY;

    public static String ALIPAY_URL;

    public static String ALIPAY_VERIFY_URL;

    //授权模式
    public static String ALIPAY_AUTH_CODE;

    //授权地址
    public static String ALIPAY_AUTH_URL;

    //授权回调地址
    public static String ALIPAY_AUTH_RET_URL;

    //调用的接口名，无需修改
    public static String ALIPAY_SERVICE;

    //调用应用id
    public static String ALIPAY_APP_ID;

    // 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String ALIPAY_PARTNER;

    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
    public static String ALIPAY_SELLER_ID;

    //商户的私钥,需要PKCS8格式，RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
    public static String ALIPAY_PRIVATE_KEY;

    // 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String ALIPAY_PUBLIC_KEY;

    public static String ALIPAY_MD5_KEY;

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String ALIPAY_NOTIFY_URL;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String ALIPAY_RETURN_URL;

    // 签名方式
    public static String ALIPAY_SIGN_TYPE;

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String ALIPAY_INPUT_CHARSET;

    // 支付类型 ，无需修改
    public static String ALIPAY_PAYMENT_TYPE;

    // 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
    public static String ALIPAY_ANTI_PHISHING_KEY;

    // 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
    public static String ALIPAY_EXTER_INVOKE_IP;

    // 设置未付款交易的超时时间，一旦超时，该笔交易就会自动被关闭
    public static String ALIPAY_IT_B_PAY;


    public static String TENPAY_URL;
    public static String TENPAY_QUERY_URL;
    //授权模式
    public static String TENPAY_AUTH_CODE;
    //授权地址
    public static String TENPAY_AUTH_URL;
    //授权回调地址
    public static String TENPAY_AUTH_RET_URL;
    //微信分配的公众账号ID
    public static String TENPAY_APP_ID;
    //微信支付分配的商户号
    public static String TENPAY_MCH_ID;
    //设备号 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
    public static String TENPAY_DEVICE_INFO;
    //密钥，微信公众号的 AppSecret
    public static String TENPAY_APP_SECRET;
    //密钥，在微信支付账号中配置，不是微信公众号的 AppSecret
    public static String TENPAY_PRIVATE_KEY;
    public static String TENPAY_NOTIFY_URL;
    public static String TENPAY_RETURN_URL;
    //机密方式
    public static String TENPAY_SIGN_TYPE;
    //是否支持信用卡
    public static String TENPAY_LIMIT_PAY;
    //是否支持信用卡
    public static String TENPAY_TRADE_TYPE;

    public void setALIPAY_URL(String alipayUrl) {
        ALIPAY_URL = alipayUrl;
    }

    public void setALIPAY_VERIFY_URL(String alipayVerifyUrl) {
        ALIPAY_VERIFY_URL = alipayVerifyUrl;
    }

    public void setALIPAY_AUTH_CODE(String alipayAuthCode) {
        ALIPAY_AUTH_CODE = alipayAuthCode;
    }

    public void setALIPAY_AUTH_URL(String alipayAuthUrl) {
        ALIPAY_AUTH_URL = alipayAuthUrl;
    }

    public void setALIPAY_AUTH_RET_URL(String alipayAuthRetUrl) {
        ALIPAY_AUTH_RET_URL = alipayAuthRetUrl;
    }

    public void setALIPAY_SERVICE(String alipayService) {
        ALIPAY_SERVICE = alipayService;
    }

    public void setALIPAY_APP_ID(String alipayAppId) {
        ALIPAY_APP_ID = alipayAppId;
    }

    public void setALIPAY_PARTNER(String alipayPartner) {
        ALIPAY_PARTNER = alipayPartner;
    }

    public void setALIPAY_SELLER_ID(String alipaySellerId) {
        ALIPAY_SELLER_ID = alipaySellerId;
    }

    public void setALIPAY_PRIVATE_KEY(String alipayPrivateKey) {
        ALIPAY_PRIVATE_KEY = alipayPrivateKey;
    }

    public void setALIPAY_PUBLIC_KEY(String alipayPublicKey) {
        ALIPAY_PUBLIC_KEY = alipayPublicKey;
    }

    public void setALIPAY_MD5_KEY(String alipayMd5Key) {
        ALIPAY_MD5_KEY = alipayMd5Key;
    }

    public void setALIPAY_NOTIFY_URL(String alipayNotifyUrl) {
        ALIPAY_NOTIFY_URL = alipayNotifyUrl;
    }

    public void setALIPAY_RETURN_URL(String alipayReturnUrl) {
        ALIPAY_RETURN_URL = alipayReturnUrl;
    }

    public void setALIPAY_SIGN_TYPE(String alipaySignType) {
        ALIPAY_SIGN_TYPE = alipaySignType;
    }

    public void setALIPAY_INPUT_CHARSET(String alipayInputCharset) {
        ALIPAY_INPUT_CHARSET = alipayInputCharset;
    }

    public void setALIPAY_PAYMENT_TYPE(String alipayPaymentType) {
        ALIPAY_PAYMENT_TYPE = alipayPaymentType;
    }

    public void setALIPAY_ANTI_PHISHING_KEY(String alipayAntiPhishingKey) {
        ALIPAY_ANTI_PHISHING_KEY = alipayAntiPhishingKey;
    }

    public void setALIPAY_EXTER_INVOKE_IP(String alipayExterInvokeIp) {
        ALIPAY_EXTER_INVOKE_IP = alipayExterInvokeIp;
    }

    public void setALIPAY_IT_B_PAY(String alipayItBPay) {
        ALIPAY_IT_B_PAY = alipayItBPay;
    }

    public void setTENPAY_URL(String tenpayUrl) {
        TENPAY_URL = tenpayUrl;
    }

    public void setTENPAY_QUERY_URL(String tenpayQueryUrl) {
        TENPAY_QUERY_URL = tenpayQueryUrl;
    }

    public void setTENPAY_AUTH_CODE(String tenpayAuthCode) {
        TENPAY_AUTH_CODE = tenpayAuthCode;
    }

    public void setTENPAY_AUTH_URL(String tenpayAuthUrl) {
        TENPAY_AUTH_URL = tenpayAuthUrl;
    }

    public void setTENPAY_AUTH_RET_URL(String tenpayAuthRetUrl) {
        TENPAY_AUTH_RET_URL = tenpayAuthRetUrl;
    }

    public void setTENPAY_APP_ID(String tenpayAppId) {
        TENPAY_APP_ID = tenpayAppId;
    }

    public void setTENPAY_MCH_ID(String tenpayMchId) {
        TENPAY_MCH_ID = tenpayMchId;
    }

    public void setTENPAY_DEVICE_INFO(String tenpayDeviceInfo) {
        TENPAY_DEVICE_INFO = tenpayDeviceInfo;
    }

    public void setTENPAY_APP_SECRET(String tenpayAppSecret) {
        TENPAY_APP_SECRET = tenpayAppSecret;
    }

    public void setTENPAY_PRIVATE_KEY(String tenpayPrivateKey) {
        TENPAY_PRIVATE_KEY = tenpayPrivateKey;
    }

    public void setTENPAY_NOTIFY_URL(String tenpayNotifyUrl) {
        TENPAY_NOTIFY_URL = tenpayNotifyUrl;
    }

    public void setTENPAY_RETURN_URL(String tenpayReturnUrl) {
        TENPAY_RETURN_URL = tenpayReturnUrl;
    }

    public void setTENPAY_SIGN_TYPE(String tenpaySignType) {
        TENPAY_SIGN_TYPE = tenpaySignType;
    }

    public void setTENPAY_LIMIT_PAY(String tenpayLimitPay) {
        TENPAY_LIMIT_PAY = tenpayLimitPay;
    }

    public void setTENPAY_TRADE_TYPE(String tenpayTradeType) {
        TENPAY_TRADE_TYPE = tenpayTradeType;
    }

    public void setBODY(String body) {
        BODY = body;
    }
}
