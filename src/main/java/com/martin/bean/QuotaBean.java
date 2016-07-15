package com.martin.bean;

import java.io.Serializable;

/**
* @ClassName: QuotaBean
* @Description: 指标
* @author ZXY
* @date 2016-07-14 16:26:06
*/

public class QuotaBean implements Serializable {

    /**
    * 指标id
    **/
    private Integer quotaId;
    /**
    * 指标名称
    **/
    private String quotaName;
    /**
    * 对应字段
    **/
    private String fieldName;
    /**
    * 状态 0-废弃 1-在用
    **/
    private Boolean state;

    public QuotaBean() {
        super();
    }

    public QuotaBean(Integer quotaId, String quotaName, String fieldName, Boolean state) {
        this.quotaId = quotaId;
        this.quotaName = quotaName;
        this.fieldName = fieldName;
        this.state = state;
    }

    public Integer getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Integer quotaId) {
        this.quotaId = quotaId;
    }

    public String getQuotaName() {
        return quotaName;
    }

    public void setQuotaName(String quotaName) {
        this.quotaName = quotaName == null ? null : quotaName.trim();
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName == null ? null : fieldName.trim();
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

}