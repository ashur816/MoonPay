package com.martin.dto;

import java.io.Serializable;

/**
 * @ClassName: ToPayInfo
 * @Description: 调用支付接口参数
 * @author ZXY
 * @date 2016/5/24 9:19
 */
public class ToPayInfo implements Serializable {

    private static final long serialVersionUID = 3911875069782744737L;
    /**
     * 业务id（订单id等）
     */
    private String bizId;

    /**
     * 业务类型
     */
    private Integer bizType;

    /**
     * 商品名
     */
    public String goodName;

    /**
     * 操作总额
     */
    private Integer totalAmount;

    /**
     * 实际支付总额
     */
    private Integer payAmount;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Integer payAmount) {
        this.payAmount = payAmount;
    }
}
