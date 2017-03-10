package com.martin.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZXY
 * @ClassName: UserBean
 * @Description:
 * @date 2017/3/2 10:32
 */
public class UserBean implements Serializable {

    private static final long serialVersionUID = -1961925239059287102L;

    private int userId;

    private String userName;

    private String nickName;

    private String userChannel;

    private int state;

    private String thdId;

    private int relateState;

    private int sex;

    private String country;

    private String province;

    private String city;

    private String headImg;

    private Date createTime;

    private Date updateTime;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public String getThdId() {
        return thdId;
    }

    public void setThdId(String thdId) {
        this.thdId = thdId;
    }

    public int getRelateState() {
        return relateState;
    }

    public void setRelateState(int relateState) {
        this.relateState = relateState;
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
