package com.martin.bean;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: PayFlowBean
 * @Description: 支付流水
 * @author ZXY
 * @date 2016-05-24 13:43:02
 */
@Table(name = "tb_payflow")
public class TBPayFlowBean implements Serializable {

    private static final long serialVersionUID = 1627679510441577860L;
    /**
     * 收银台交易流水，根据 时间 + 随机数（4）
     **/
    @Id
    private Long flowId;
    /**
     * 流水类型
     **/
    private Integer flowType;
    /**
     * 业务订单流水
     **/
    private Long bizId;
    /**
     * 业务类型
     **/
    private Integer bizType;
    /**
     * 第三方交易流水
     **/
    private String thdFlowId;
    /**
     * 第三方交易类型
     **/
    private Integer thdType;
    /**
     * 通知地址
     **/
    private String notifyUrl;
    /**
     * 回调地址
     **/
    private String returnUrl;
    /**
     * 总交易金额
     **/
    private Integer totalAmount;
    /**
     * 实际支付金额
     **/
    private Integer payAmount;
    /**
     *
     **/
    private Date createTime;
    /**
     * 状态(0:失效,1:生效)
     **/
    private Integer state;

    /**
     * 支付状态
     **/
    private Integer payState;

    /**
     *支付失败编码
     **/
    private String failCode;

    /**
     *支付失败描述
     **/
    private String failDesc;

    /**
     *众包人userId
     **/
    private Long packetUserId;

    /**
     * 支付完成时间
     **/
    private Date payTime;

    /**
     *退款单号
     **/
    private Long refundId;
    /**
     *第三方退款单号
     **/
    private String thdRefundId;
    /**
     *退款时间
     **/
    private Date refundTime;
    /**
     * 微信支付随机码
     **/
    private Integer randomCode;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Integer getFlowType() {
        return flowType;
    }

    public void setFlowType(Integer flowType) {
        this.flowType = flowType;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getThdFlowId() {
        return thdFlowId;
    }

    public void setThdFlowId(String thdFlowId) {
        this.thdFlowId = thdFlowId;
    }

    public Integer getThdType() {
        return thdType;
    }

    public void setThdType(Integer thdType) {
        this.thdType = thdType;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Long getPacketUserId() {
        return packetUserId;
    }

    public void setPacketUserId(Long packetUserId) {
        this.packetUserId = packetUserId;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
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

    public Integer getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(Integer randomCode) {
        this.randomCode = randomCode;
    }
}