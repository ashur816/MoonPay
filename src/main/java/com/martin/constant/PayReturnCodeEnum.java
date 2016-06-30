package com.martin.constant;

/**
 * @ClassName: PayStatusEnum
 * @Description: 第三方支付状态枚举类
 * @author ZXY
 * @date 2016/5/27 11:06
 */
public enum PayReturnCodeEnum {
    ALIPAY_WAITPAY(PayConstant.PAY_FAIL, "WAIT_BUYER_PAY", "交易创建，等待买家付款"),
    ALIPAY_CLOSED(PayConstant.PAY_FAIL, "TRADE_CLOSED", "在指定时间段内未支付时关闭的交易；在交易完成全额退款成功时关闭的交易。"),
    ALIPAY_SUCCESS(PayConstant.PAY_UN_BACK, "TRADE_SUCCESS", "交易成功，且可对该交易做操作，如：多级分润、退款等。"),
    ALIPAY_PENDING(PayConstant.PAY_FAIL, "TRADE_PENDING", "等待卖家收款（买家付款后，如果卖家账号被冻结）。"),
    ALIPAY_FINISHED(PayConstant.PAY_SUCCESS, "TRADE_FINISHED", "交易成功且结束，即不可再做任何操作。"),

    TENPAY_NOTPAY(PayConstant.PAY_NOT, "NOTPAY", "订单未支付"),
    TENPAY_SUCCESS(PayConstant.PAY_UN_BACK, "SUCCESS", "支付成功，待回调"),
    TENPAY_NOAUTH(PayConstant.PAY_FAIL, "TRADE_PENDING", "商户未开通此接口权限"),
    TENPAY_NOTENOUGH(PayConstant.PAY_FAIL, "TRADE_PENDING", "用户帐号余额不足"),
    TENPAY_ORDERPAID(PayConstant.PAY_UN_BACK, "ORDERPAID", "商户订单已支付，无需重复操作"),
    TENPAY_ORDERCLOSED(PayConstant.PAY_FAIL, "ORDERCLOSED", "当前订单已关闭，无法支付"),
    TENPAY_SYSTEMERROR(PayConstant.PAY_FAIL, "SYSTEMERROR", "系统超时"),
    TENPAY_APPID_NOT_EXIST(PayConstant.PAY_FAIL, "APPID_NOT_EXIST", "参数中缺少APPID"),
    TENPAY_MCHID_NOT_EXIST(PayConstant.PAY_FAIL, "MCHID_NOT_EXIST", "参数中缺少MCHID"),
    TENPAY_APPID_MCHID_NOT_MATCH(PayConstant.PAY_FAIL, "APPID_MCHID_NOT_MATCH", "appid和mch_id不匹配"),
    TENPAY_LACK_PARAMS(PayConstant.PAY_FAIL, "LACK_PARAMS", "缺少必要的请求参数"),
    TENPAY_OUT_TRADE_NO_USED(PayConstant.PAY_FAIL, "OUT_TRADE_NO_USED", "同一笔交易不能多次提交"),
    TENPAY_SIGNERROR(PayConstant.PAY_FAIL, "SIGNERROR", "参数签名结果不正确"),
    TENPAY_XML_FORMAT_ERROR(PayConstant.PAY_FAIL, "XML_FORMAT_ERROR", "XML格式错误"),
    TENPAY_REQUIRE_POST_METHOD(PayConstant.PAY_FAIL, "REQUIRE_POST_METHOD", "未使用post传递参数 "),
    TENPAY_POST_DATA_EMPTY(PayConstant.PAY_FAIL, "POST_DATA_EMPTY", "post数据不能为空"),
    TENPAY_NOT_UTF8(PayConstant.PAY_FAIL, "NOT_UTF8", "未使用指定编码格式"),

    TEN_PAY_ORDER_NOT_EXIST(PayConstant.PAY_NOT, "ORDERNOTEXIST ", "查询系统中不存在此交易订单号");

    PayReturnCodeEnum(Integer payState, String code, String desc) {
        this.payState = payState;
        this.code = code;
        this.desc = desc;
    }

    /* 对应支付中心的支付状态 */
    private Integer payState;
    /* 第三方返回的交易状态 */
    private String code;
    private String desc;

    public Integer getPayState() {
        return payState;
    }

    public String getDesc() {
        return desc;
    }

    public String getCode() {
        return code;
    }
}
