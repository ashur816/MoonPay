package com.martin.dto;

import java.io.Serializable;

/**
 * @author ZXY
 * @ClassName: SendMsgInfo
 * @Description:
 * @date 2017/3/10 19:25
 */
public class SendMsgInfo implements Serializable {

    private static final long serialVersionUID = 8494228304514386882L;

    //开发者微信号
    private String ToUserName;

    //发送方帐号（一个OpenID）
    private String FromUserName;

    //消息创建时间 （整型）
    private String CreateTime;

    //消息类型，event
    private String MsgType;

    //文本消息内容
    private String Content;

    private String ArticleCount;

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(String articleCount) {
        ArticleCount = articleCount;
    }
}
