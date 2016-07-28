package com.martin.dto;

/**
 * @ClassName: PushMessage
 * @Description: 推动数据
 * @author ZXY
 * @date 2016/7/25 11:14
 */
public class PushMessage {

    /**
     * 业务ID
     */
    private String bizId;
    /**
     * 业务类型
     */
    private String bizCode;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 展示类型 1为对话框展示 2为通知栏 3全部
     */
    private int showType;
    /**
     * 消息id
     */
    private Long msgId;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }
}
