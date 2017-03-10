package com.martin.dto;

import java.io.Serializable;

/**
 * @author ZXY
 * @ClassName: MusicMsgInfo
 * @Description:
 * @date 2017/3/10 19:08
 */
public class ArticleMsgInfo implements Serializable {

    private static final long serialVersionUID = -3079688054050274415L;

    private String Title;

    private String Description;

    private String PicUrl;

    private String Url;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
