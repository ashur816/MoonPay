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

    private String userChannel;

    private String thdId;

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

    public String getUserChannel() {
        return userChannel;
    }

    public void setUserChannel(String userChannel) {
        this.userChannel = userChannel;
    }

    public String getThdId() {
        return thdId;
    }

    public void setThdId(String thdId) {
        this.thdId = thdId;
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
