package com.martin.bean;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
* @ClassName: VoucherBean
* @Description: 代金券
* @author ZXY
* @date 2016-07-14 17:01:03
*/
@Table(name = "m_voucher")
public class VoucherBean implements Serializable {

    /**
    * 券id
    **/
    private Long voucherId;
    /**
    * 策略id
    **/
    private Integer policyId;
    /**
    * 用户id
    **/
    private Long userId;
    /**
    * 面额
    **/
    private Integer faceValue;
    /**
    * 生效时间
    **/
    private Date effectTime;
    /**
    * 失效时间
    **/
    private Date expireTime;
    /**
    * 领取时间
    **/
    private Date receiveTime;
    /**
    * 使用时间
    **/
    private Date useTime;
    /**
    * 状态 0-已失效 1-已领取 2-已使用 3-已生成
    **/
    private Byte state;

    public VoucherBean() {
        super();
    }

    public VoucherBean(Long voucherId, Integer policyId, Long userId, Integer faceValue, Date effectTime, Date expireTime, Date receiveTime, Date useTime, Byte state) {
        this.voucherId = voucherId;
        this.policyId = policyId;
        this.userId = userId;
        this.faceValue = faceValue;
        this.effectTime = effectTime;
        this.expireTime = expireTime;
        this.receiveTime = receiveTime;
        this.useTime = useTime;
        this.state = state;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(Integer faceValue) {
        this.faceValue = faceValue;
    }

    public Date getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(Date effectTime) {
        this.effectTime = effectTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

}