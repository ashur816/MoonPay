package com.martin.dto;

/**
 * @ClassName: ResultInfo
 * @Description: 用于封装页面返回json数据
 * @author ZXY
 * @date 2016/6/29 10:43
 */
public class ResultInfo {
    /**
     * 0 success,-1 fail
     */
    private int success;
    /**
     * 错误编码
     */
    private String code;
    /**
     * 消息提示
     */
    private String message;
    /**
     * 数据
     */
    private Object data;


    public ResultInfo() {
    }

    public ResultInfo(int success, String code) {
        super();
        this.success = success;
        this.code = code;
    }

    public ResultInfo(int success, String code, String message) {
        super();
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public ResultInfo(int success, String code, String message, Object data) {
        super();
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ResultInfo [success=" + success + ", code=" + code + ", message=" + message + ", data=" + data + "]";
    }
}
