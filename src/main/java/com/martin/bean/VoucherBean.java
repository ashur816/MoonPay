package com.martin.bean;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: VoucherBean
 * @Description: 代金券实体
 * @author ZXY
 * @date 2016/7/4 9:26
 */
@Table(name = "voucher")
public class VoucherBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voucherId;

    private Long userId;

    private Integer voucherType;

    private Integer voucherValue;

    private String source;

    private Integer useRange;

    private Integer state;

    private Date createTime;

    private Date updateTime;

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(Integer voucherType) {
        this.voucherType = voucherType;
    }

    public Integer getVoucherValue() {
        return voucherValue;
    }

    public void setVoucherValue(Integer voucherValue) {
        this.voucherValue = voucherValue;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setUseRange(Integer useRange) {
        this.useRange = useRange;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
