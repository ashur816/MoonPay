package com.martin.bean;

import javax.persistence.Table;
import java.io.Serializable;

/**
* @ClassName: PolicyBean
* @Description: 策略
* @author ZXY
* @date 2016-07-14 16:28:23
*/
@Table(name = "m_policy")
public class PolicyBean implements Serializable {

    /**
    * 策略id
    **/
    private Integer policyId;
    /**
    * 活动id
    **/
    private Integer activityId;
    /**
    * 策略名称，前台展示用 eg:限上海，限上午
    **/
    private String policyName;
    /**
    * 策略类型 1-代金券 2-红包 3-补贴
    **/
    private Byte policyType;
    /**
    * 优先级 越大优先级越高1-99
    **/
    private Byte policyPriority;
    /**
    * 有效时长天数
    **/
    private Integer effectDays;

    public PolicyBean() {
        super();
    }

    public PolicyBean(Integer policyId, Integer activityId, String policyName, Byte policyType, Byte policyPriority, Integer effectDays) {
        this.policyId = policyId;
        this.activityId = activityId;
        this.policyName = policyName;
        this.policyType = policyType;
        this.policyPriority = policyPriority;
        this.effectDays = effectDays;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName == null ? null : policyName.trim();
    }

    public Byte getPolicyType() {
        return policyType;
    }

    public void setPolicyType(Byte policyType) {
        this.policyType = policyType;
    }

    public Byte getPolicyPriority() {
        return policyPriority;
    }

    public void setPolicyPriority(Byte policyPriority) {
        this.policyPriority = policyPriority;
    }

    public Integer getEffectDays() {
        return effectDays;
    }

    public void setEffectDays(Integer effectDays) {
        this.effectDays = effectDays;
    }

}