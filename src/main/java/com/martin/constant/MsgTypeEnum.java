package com.martin.constant;

/**
 * Created by Administrator on 2017/3/2.
 */
public enum MsgTypeEnum {

    TEXT("text", "文本消息", "tenMsgTextService"),
    IMAGE("image", "图片消息", "tenMsgImageService"),
    VOICE("voice", "语音消息", "tenMsgVoiceService"),
    SHORTVIDEO("shortvideo", "小视频消息", "tenMsgShortVideoService"),
    LOCATION("location", "地理位置消息", "tenMsgLocationService"),
    LINK("link", "链接消息", "tenMsgLinkService"),
    EVENT("event", "事件消息", "tenMsgEventService");

    MsgTypeEnum(String code, String desc, String service) {
        this.code = code;
        this.desc = desc;
        this.service = service;
    }

    private String code;
    private String desc;
    private String service;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getService() {
        return service;
    }
}
