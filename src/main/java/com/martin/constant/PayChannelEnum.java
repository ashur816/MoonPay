package com.martin.constant;

/**
 * @ClassName: PayEnum
 * @Description: 支付渠道枚举类
 * @author ZXY
 * @date 2016/5/26 21:37
 */
public enum PayChannelEnum {

    TEN_PAY("1", "tenPayService", "微信支付", "pay/ten_pay"),
    ALI_PAY("2", "aliPayService", "支付宝", "pay/ali_pay"),
    CMB_PAY("3", "cmbPayService", "招行支付", "pay/cmb_pay");

    private String payType;
    private String payService;
    private String payDesc;
    private String webPage;

    PayChannelEnum(String payType, String payService, String payDesc, String webPage) {
        this.payType = payType;
        this.payService = payService;
        this.payDesc = payDesc;
        this.webPage = webPage;
    }

    public static PayChannelEnum getPayChannel(String payType) {
        for (PayChannelEnum anEnum : PayChannelEnum.values()) {
            if (payType.equals(anEnum.getPayType())) {
                return anEnum;
            }
        }
        return null;
    }

    public String getPayType() {
        return payType;
    }

    public String getPayService() {
        return payService;
    }

    public String getPayDesc() {
        return payDesc;
    }

    public String getWebPage() {
        return webPage;
    }
}
