package com.martin.bean;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author ZXY
 * @ClassName: ThdUserBean
 * @Description:
 * @date 2017/3/2 10:32
 */
@Table(name = "m_thd_user")
public class ThdUserBean implements Serializable {

    private static final long serialVersionUID = 1341102812856977817L;

    private int thdUserId;

    private String thdId;

    private int userId;

    private String userChannel;

    private int state;

    private int sex;

    private String country;

    private String province;

    private String city;

    private String headImg;

    private Date createTime;

    private Date updateTime;

    public int getThdUserId() {
        return thdUserId;
    }

    public void setThdUserId(int thdUserId) {
        this.thdUserId = thdUserId;
    }

    public String getThdId() {
        return thdId;
    }

    public void setThdId(String thdId) {
        this.thdId = thdId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserChannel() {
        return userChannel;
    }

    public void setUserChannel(String userChannel) {
        this.userChannel = userChannel;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
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
