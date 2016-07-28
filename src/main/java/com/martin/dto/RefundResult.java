package com.martin.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: RefundResult
 * @Description: 退款结果
 * @author ZXY
 * @date 2016/5/24 19:06
 */
public class RefundResult implements Serializable {

    /**
     * 支付流水id
     */
    private Long flowId;

    /**
     * 退款批次号
     */
    private String thdRefundId;

    /**
     * 原第三方支付流水id
     */
    private String thdFlowId;

    /**
     * 第三方返回的状态
     */
    private String tradeState;

    /**
     * 后台转义的状态
     */
    private Integer payState;

    /**
     * 退款失败编码
     */
    private String failCode;

    /**
     * 退款失败描述
     */
    private String failDesc;

    private Date refundTime;

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getThdRefundId() {
        return thdRefundId;
    }

    public void setThdRefundId(String thdRefundId) {
        this.thdRefundId = thdRefundId;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public String getThdFlowId() {
        return thdFlowId;
    }

    public void setThdFlowId(String thdFlowId) {
        this.thdFlowId = thdFlowId;
    }

    public String getTradeState() {
        return tradeState;
    }

    public void setTradeState(String tradeState) {
        this.tradeState = tradeState;
    }

    public Integer getPayState() {
        return payState;
    }

    public void setPayState(Integer payState) {
        this.payState = payState;
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
}
