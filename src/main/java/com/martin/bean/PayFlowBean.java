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
@Table(name = "m_pay_flow")
public class PayFlowBean implements Serializable {

    private static final long serialVersionUID = 1627679510441577860L;
    /**
     * 收银台交易流水，根据 时间 + 随机数（4）
     **/
    @Id
    private Long flowId;
    /**
     * 业务订单流水
     **/
    private String bizId;
    /**
     * 业务类型
     **/
    private Integer bizType;
    /**
     * 第三方交易类型
     **/
    private Integer payType;
    /**
     * 第三方交易流水
     **/
    private String thdFlowId;
    /**
     * 发起支付的客户端来源
     **/
    private String clientSource;
    /**
     * 第三方支付的账户id
     **/
    private String paySource;
    /**
     * 总交易金额
     **/
    private Integer totalAmount;
    /**
     * 实际支付金额
     **/
    private Integer payAmount;
    /**
     * 创建时间
     **/
    private Date createTime;
    /**
     * 支付完成时间
     **/
    private Date payTime;
    /**
     * 状态(0:失效,1:生效)
     **/
    private Integer state;
    /**
     * 支付状态 0-未支付 2-支付成功，业务待处理 3-支付成功，业务处理失败 1-支付成功且业务处理成功 4-已退款 8-支付失败
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
     *退款单号
     **/
    private Long refundId;
    /**
     *退款原因
     **/
    private String refundReason;
    /**
     *第三方退款单号
     **/
    private String thdRefundId;
    /**
     *退款时间
     **/
    private Date refundTime;

    public PayFlowBean() {
        super();
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

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

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getThdFlowId() {
        return thdFlowId;
    }

    public void setThdFlowId(String thdFlowId) {
        this.thdFlowId = thdFlowId;
    }

    public String getClientSource() {
        return clientSource;
    }

    public void setClientSource(String clientSource) {
        this.clientSource = clientSource;
    }

    public String getPaySource() {
        return paySource;
    }

    public void setPaySource(String paySource) {
        this.paySource = paySource;
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

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
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
}