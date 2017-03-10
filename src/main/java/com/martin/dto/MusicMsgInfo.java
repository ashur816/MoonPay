package com.martin.dto;

import java.io.Serializable;

/**
 * @author ZXY
 * @ClassName: MusicMsgInfo
 * @Description:
 * @date 2017/3/10 19:08
 */
public class MusicMsgInfo implements Serializable {

    private static final long serialVersionUID = 2634579730436874470L;

    private String Title;

    private String Description;

    private String MusicUrl;

    private String HQMusicUrl;

    private String ThumbMediaId;

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

    public String getMusicUrl() {
        return MusicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        MusicUrl = musicUrl;
    }

    public String getHQMusicUrl() {
        return HQMusicUrl;
    }

    public void setHQMusicUrl(String HQMusicUrl) {
        this.HQMusicUrl = HQMusicUrl;
    }

    public String getThumbMediaId() {
        return ThumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        ThumbMediaId = thumbMediaId;
    }
}
