package com.martin.constant;

/**
 * @author ZXY
 * @ClassName: TenPublicParam
 * @Description:
 * @date 2017/3/1 14:32
 */
public class TenPublicParam {

    public static final String getUserInfoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?";

    public static final String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    public static final String lang = "zh_CN";

    public static final String charset = "UTF-8";

    public static String appId;

    public static String appSecret;

    public static String token;

    public static String aesKey;

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }
}
