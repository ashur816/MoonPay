package com.martin.constant;

/**
 * Created by Administrator on 2017/3/2.
 */
public enum EventTypeEnum {

    SUBSCRIBE("subscribe", "关注", "subscribe"),
    UNSUBSCRIBE("unsubscribe", "取消关注", "unSubscribe"),
    SCAN("scan", "扫描带参数二维码", "scan"),
    LOCATION("location", "上报地理位置", "location"),
    CLICK("click", "点击菜单", "click"),
    VIEW("view", "点击菜单跳转链接", "view");

    EventTypeEnum(String type, String desc, String method) {
        this.type = type;
        this.desc = desc;
        this.method = method;
    }

    private String type;
    private String desc;
    private String method;

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public String getMethod() {
        return method;
    }
}
