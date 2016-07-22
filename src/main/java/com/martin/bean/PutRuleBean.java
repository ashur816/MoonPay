package com.martin.bean;

import javax.persistence.Table;
import java.io.Serializable;

/**
* @ClassName: PutRuleBean
* @Description: 发放规则
* @author ZXY
* @date 2016-07-14 16:29:03
*/
@Table(name = "m_put_rule")
public class PutRuleBean implements Serializable {

    /**
    * 规则id
    **/
    private Integer ruleId;
    /**
    * 策略id
    **/
    private Integer policyId;
    /**
    * 面值
    **/
    private Integer faceValue;
    /**
    * 总数量
    **/
    private Integer totalAmount;
    /**
    * 最初生成状态数量
    **/
    private Integer initialAmount;
    /**
    * 已领取状态数量
    **/
    private Integer receivedAmount;
    /**
    * 已使用状态数量
    **/
    private Integer usedAmount;
    /**
    * 已失效状态数量
    **/
    private Integer expiredAmount;

    public PutRuleBean() {
        super();
    }

    public PutRuleBean(Integer ruleId, Integer policyId, Integer faceValue, Integer totalAmount, Integer initialAmount, Integer receivedAmount, Integer usedAmount, Integer expiredAmount) {
        this.ruleId = ruleId;
        this.policyId = policyId;
        this.faceValue = faceValue;
        this.totalAmount = totalAmount;
        this.initialAmount = initialAmount;
        this.receivedAmount = receivedAmount;
        this.usedAmount = usedAmount;
        this.expiredAmount = expiredAmount;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public Integer getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(Integer faceValue) {
        this.faceValue = faceValue;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Integer initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Integer getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(Integer receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public Integer getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(Integer usedAmount) {
        this.usedAmount = usedAmount;
    }

    public Integer getExpiredAmount() {
        return expiredAmount;
    }

    public void setExpiredAmount(Integer expiredAmount) {
        this.expiredAmount = expiredAmount;
    }

}