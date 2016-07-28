package com.martin.constant;

/**
 * @ClassName: PushConstant
 * @Description: 推送常量
 * @author ZXY
 * @date 2016/7/25 10:37
 */
public class PushConstant {

    public static String url;

    public static String appId;

    public static String appKey;

    public static String masterSecret;

    public void setUrl(String url) {
        PushConstant.url = url;
    }

    public void setAppId(String appId) {
        PushConstant.appId = appId;
    }

    public void setAppKey(String appKey) {
        PushConstant.appKey = appKey;
    }

    public void setMasterSecret(String masterSecret) {
        PushConstant.masterSecret = masterSecret;
    }
}
