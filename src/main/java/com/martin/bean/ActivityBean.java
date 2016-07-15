package com.martin.bean;

import java.io.Serializable;
import java.util.Date;

/**
* @ClassName: ActivityBean
* @Description: 活动
* @author ZXY
* @date 2016-07-14 16:27:18
*/

public class ActivityBean implements Serializable {

    /**
    * 活动ID
    **/
    private Integer activityId;
    /**
    * 活动名称
    **/
    private String activityName;
    /**
    * 活动类型
    **/
    private Byte activityType;
    /**
    * 渠道
    **/
    private Byte channelId;
    /**
    * 省份
    **/
    private Integer provinceId;
    /**
    * 开始时间
    **/
    private Date startTime;
    /**
    * 结束时间
    **/
    private Date endTime;
    /**
    * 投入总金额
    **/
    private Long putAmount;

    public ActivityBean() {
        super();
    }

    public ActivityBean(Integer activityId, String activityName, Byte activityType, Byte channelId, Integer provinceId, Date startTime, Date endTime, Long putAmount) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.activityType = activityType;
        this.channelId = channelId;
        this.provinceId = provinceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.putAmount = putAmount;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName == null ? null : activityName.trim();
    }

    public Byte getActivityType() {
        return activityType;
    }

    public void setActivityType(Byte activityType) {
        this.activityType = activityType;
    }

    public Byte getChannelId() {
        return channelId;
    }

    public void setChannelId(Byte channelId) {
        this.channelId = channelId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getPutAmount() {
        return putAmount;
    }

    public void setPutAmount(Long putAmount) {
        this.putAmount = putAmount;
    }

}