package com.martin.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: PayFlowBean
 * @Description: 支付流水
 * @author ZXY
 * @date 2016-05-24 13:43:02
 */

public class PayFlowBean implements Serializable {

    private static final long serialVersionUID = 1627679510441577860L;
    /**
     * 收银台交易流水，根据 时间 + 随机数（4）
     **/
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
    private String payType;
    /**
     * 第三方交易流水
     **/
    private String thdFlowId;
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
     * 明细
     */
    private List<PayFlowDetailBean> detailList;

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

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
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

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
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

    public List<PayFlowDetailBean> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<PayFlowDetailBean> detailList) {
        this.detailList = detailList;
    }
}