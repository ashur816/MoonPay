package com.martin.bean;

import java.io.Serializable;

/**
 * @ClassName: PayResult
 * @Description: 支付结果
 * @author ZXY
 * @date 2016/5/24 19:06
 */
public class PayResult implements Serializable {

    private static final long serialVersionUID = -3486284039027446128L;
    /**
     * 支付流水id
     */
    private Long flowId;

    /**
     * 第三方返回的支付状态
     */
    private String tradeState;

    /**
     * 第三方支付返回id
     */
    private String thdFlowId;

    /**
     * 后台转义的状态
     */
    private Integer payState;

    /**
     * 支付失败编码
     */
    private String failCode;
    /**
     * 支付失败描述
     */
    private String failDesc;

    /**
     * 微信支付随机码
     */
    private Integer randomCode;

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getTradeState() {
        return tradeState;
    }

    public void setTradeState(String tradeState) {
        this.tradeState = tradeState;
    }

    public String getThdFlowId() {
        return thdFlowId;
    }

    public void setThdFlowId(String thdFlowId) {
        this.thdFlowId = thdFlowId;
    }

    public String getFailCode() {
        return failCode;
    }

    public void setFailCode(String failCode) {
        this.failCode = failCode;
    }

    public String getFailDesc() {
        return failDesc;
    }

    public void setFailDesc(String failDesc) {
        this.failDesc = failDesc;
    }

    public Integer getPayState() {
        return payState;
    }

    public void setPayState(Integer payState) {
        this.payState = payState;
    }

    public Integer getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(Integer randomCode) {
        this.randomCode = randomCode;
    }
}
