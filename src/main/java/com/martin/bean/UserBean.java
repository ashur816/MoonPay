package com.martin.bean;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author ZXY
 * @ClassName: UserBean
 * @Description:
 * @date 2017/3/2 10:32
 */
@Table(name = "m_user")
public class UserBean implements Serializable {

    private static final long serialVersionUID = -1961925239059287102L;

    private int userId;

    private String userName;

    private String nickName;

    private String userChannel;

    private int state;

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
