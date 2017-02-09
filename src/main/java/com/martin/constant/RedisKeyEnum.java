package com.martin.constant;

/**
 * Created by Administrator on 2017/2/8.
 */
public enum RedisKeyEnum {

    PAY_KEY_PAYING("paying_", "判断是否正在支付中的key"),
    PAY_KEY_NOTIFYING("notifying_", "判断是否正在回调处理中的key"),
    REFUND_KEY("refund_","退款的key");

    private String key;
    private String desc;

    RedisKeyEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
