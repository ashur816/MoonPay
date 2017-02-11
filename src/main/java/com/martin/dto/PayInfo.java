package com.martin.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * @author ZXY
 * @ClassName: PayInfo
 * @Description: 支付信息
 * @date 2016/6/3 9:40
 */
public class PayInfo implements Serializable {

    private static final long serialVersionUID = -9213856796959528050L;

    /**
     * 支付流水号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public Long flowId;

    /**
     * 支付渠道
     */
    public Integer payType;

    /**
     * 商品名
     */
    public String goodName;

    /**
     * 总价
     */
    public Double totalAmount;

    /**
     * 支付总额
     */
    public Double payAmount;

    /**
     * 目标地址
     */
    public String destUrl;

    /**
     * 参数 &A=B 形式
     */
    public String destParam;

    /**
     * 返回的提交表单
     */
    public String retHtml;

    /**
     * 返回的授权码 微信 openId, 支付宝 authorCode
     */
    public String retCode;

    /**
     * 返回的 业务号
     */
    public String retState;

    /**
     * 业务订单号
     */
    public String bizId;

    /**
     * 支付状态
     */
    public Integer payState;

    public PayInfo() {
    }

    public PayInfo(String goodName, Double payAmount, String retHtml) {
        this.goodName = goodName;
        this.payAmount = payAmount;
        this.retHtml = retHtml;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }

    public String getDestUrl() {
        return destUrl;
    }

    public void setDestUrl(String destUrl) {
        this.destUrl = destUrl;
    }

    public String getDestParam() {
        return destParam;
    }

    public void setDestParam(String destParam) {
        this.destParam = destParam;
    }

    public String getRetHtml() {
        return retHtml;
    }

    public void setRetHtml(String retHtml) {
        this.retHtml = retHtml;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetState() {
        return retState;
    }

    public void setRetState(String retState) {
        this.retState = retState;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getPayState() {
        return payState;
    }

    public void setPayState(Integer payState) {
        this.payState = payState;
    }
}