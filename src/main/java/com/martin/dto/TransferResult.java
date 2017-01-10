package com.martin.dto;

import java.io.Serializable;

/**
 * @ClassName: TransferResult
 * @Description: 企业付款结果
 * @author ZXY
 */
public class TransferResult implements Serializable {

    private static final long serialVersionUID = -820439131047424788L;
    /**
     * 支付流水id
     */
    private Long flowId;

    /**
     * 收款方账号
     */
    private String thdAcctNo;

    /**
     * 收款账号姓名
     */
    private String thdAcctName;

    /**
     * 付款金额
     */
    private Double payAmount;

    /**
     * 处理结果
     */
    private Integer transferState;

    /**
     * 转账失败描述
     */
    private String failDesc;

    /**
     * 支付宝内部流水号
     */
    private String thdFlowId;

    /**
     * 完成时间
     */
    private String finishTime;

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getThdAcctNo() {
        return thdAcctNo;
    }

    public void setThdAcctNo(String thdAcctNo) {
        this.thdAcctNo = thdAcctNo;
    }

    public String getThdAcctName() {
        return thdAcctName;
    }

    public void setThdAcctName(String thdAcctName) {
        this.thdAcctName = thdAcctName;
    }

    public Double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }

    public String getFailDesc() {
        return failDesc;
    }

    public void setFailDesc(String failDesc) {
        this.failDesc = failDesc;
    }

    public String getThdFlowId() {
        return thdFlowId;
    }

    public void setThdFlowId(String thdFlowId) {
        this.thdFlowId = thdFlowId;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getTransferState() {
        return transferState;
    }

    public void setTransferState(Integer transferState) {
        this.transferState = transferState;
    }
}
