package com.martin.bean;

import java.io.Serializable;

/**
 * @ClassName: PayFlowDetailBean
 * @Description: 支付流水明细
 * @author ZXY
 * @date 2016-05-24 13:43:14
 */

public class PayFlowDetailBean implements Serializable {

    private static final long serialVersionUID = -236088554852035105L;
    /**
     * 明细id
     **/
    private Long detailId;
    /**
     * 收银台流水
     **/
    private Long flowId;
    /**
     * 账户id
     **/
    private Long acctId;
    /**
     * 账户类型（0：ZD账户1：用户账户)
     **/
    private Integer acctType;
    /**
     * 用户id
     **/
    private Long userId;
    /**
     * 账务操作编码
     **/
    private Integer paymentCode;
    /**
     * 操作金额
     **/
    private Integer optAmount;
    /**
     * 第三方账户id
     **/
    private Long thdId;
    /**
     * 账户名称
     **/
    private String acctName;
    /**
     * 账号
     **/
    private String acctNo;
    /**
     * 建立时间
     **/
    private String createTime;

    public PayFlowDetailBean() {
        super();
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
    }

    public Integer getAcctType() {
        return acctType;
    }

    public void setAcctType(Integer acctType) {
        this.acctType = acctType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(Integer paymentCode) {
        this.paymentCode = paymentCode;
    }

    public Integer getOptAmount() {
        return optAmount;
    }

    public void setOptAmount(Integer optAmount) {
        this.optAmount = optAmount;
    }

    public Long getThdId() {
        return thdId;
    }

    public void setThdId(Long thdId) {
        this.thdId = thdId;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName == null ? null : acctName.trim();
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo == null ? null : acctNo.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}