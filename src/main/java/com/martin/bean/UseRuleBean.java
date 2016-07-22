package com.martin.bean;

import javax.persistence.Table;
import java.io.Serializable;

/**
* @ClassName: UseRuleBean
* @Description: 使用规则
* @author ZXY
* @date 2016-07-14 16:29:43
*/
@Table(name = "m_use_rule")
public class UseRuleBean implements Serializable {

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
    private String quotaFiled;
    /**
    * 操作符
    **/
    private String operator;
    /**
    * 指标值
    **/
    private String quotaValue;

    public UseRuleBean() {
        super();
    }

    public UseRuleBean(Integer ruleId, Integer policyId, Integer quotaId, String quotaFiled, String operator, String quotaValue) {
        this.ruleId = ruleId;
        this.policyId = policyId;
        this.quotaId = quotaId;
        this.quotaFiled = quotaFiled;
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

    public String getQuotaFiled() {
        return quotaFiled;
    }

    public void setQuotaFiled(String quotaFiled) {
        this.quotaFiled = quotaFiled == null ? null : quotaFiled.trim();
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