package com.martin.dto;

/**
 * @ClassName: PushResponse
 * @Description: 推送返回对象
 * @author ZXY
 * @date 2016/7/25 10:59
 */
public class PushResponse {

    /**
     * 请求返回ID
     */
    private String id;

    /**
     * 响应的状态码
     */
    private String code;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应的报文信息
     */
    private String original;

    /**
     * 响应的描述
     */
    private String desc;

    public PushResponse() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
