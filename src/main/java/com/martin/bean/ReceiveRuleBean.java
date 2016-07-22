package com.martin.bean;

import javax.persistence.Table;
import java.io.Serializable;

/**
* @ClassName: ReceiveRuleBean
* @Description: 领取规则
* @author ZXY
* @date 2016-07-14 16:29:23
*/
@Table(name = "m_receive_rule")
public class ReceiveRuleBean implements Serializable {

    /**
    * 规则id
    **/
    private Integer ruleId;
    /**
    * 策略id
    **/
    private Integer policyId;
    /**
    * 指标id
    **/
    private Integer quotaId;
    /**
    * 指标字段
    **/
    private String quotaField;
    /**
    * 操作符
    **/
    private String operator;
    /**
    * 指标值
    **/
    private String quotaValue;

    public ReceiveRuleBean() {
        super();
    }

    public ReceiveRuleBean(Integer ruleId, Integer policyId, Integer quotaId, String quotaField, String operator, String quotaValue) {
        this.ruleId = ruleId;
        this.policyId = policyId;
        this.quotaId = quotaId;
        this.quotaField = quotaField;
        this.operator = operator;
        this.quotaValue = quotaValue;
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

    public Integer getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Integer quotaId) {
        this.quotaId = quotaId;
    }

    public String getQuotaField() {
        return quotaField;
    }

    public void setQuotaField(String quotaField) {
        this.quotaField = quotaField == null ? null : quotaField.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public String getQuotaValue() {
        return quotaValue;
    }

    public void setQuotaValue(String quotaValue) {
        this.quotaValue = quotaValue == null ? null : quotaValue.trim();
    }

}