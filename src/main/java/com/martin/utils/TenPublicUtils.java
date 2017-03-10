package com.martin.utils;

import com.martin.constant.TenPublicParam;
import org.apache.commons.codec.Charsets;

import java.util.Map;

/**
 * @author ZXY
 * @ClassName: TenOpenUtils
 * @Description:
 * @date 2017/3/3 15:00
 */
public class TenPublicUtils {

    /**
     * @param 
     * @return 
     * @throws 
     * @Description: 获取微信公众号token
     */
    public static String getToken() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("&appid=").append(TenPublicParam.appId).append("&secret=").append(TenPublicParam.appSecret);
        String retJson = HttpUtils.sendPost(TenPublicParam.getTokenUrl, builder.toString(), TenPublicParam.charset);
        Map<String, String> retMap = (Map<String, String>) JsonUtils.readMap(retJson);
        String s = retMap.get("access_token");
        return s;
    }

    /**
     * 根据openId获取微信用户信息 必须要认证公众号才可以
     *
     * @param openId 微信用户ID
     * @return
     */
    public static String getUserInfo(String accessToken, String openId) throws Exception {
        StringBuilder sb = new StringBuilder();
        // 获取微信 access_token/openid
        sb.append("&access_token=").append(accessToken).append("&openid=").append(openId).append("&lang=").append(TenPublicParam.lang);
        String result = HttpUtils.sendPostXml(TenPublicParam.getUserInfoUrl, sb.toString(), Charsets.UTF_8.toString());
        return result;
    }

    /**
     * @param 
     * @return 
     * @throws
     * @Description: 根据错误消息code，获取对应文字异常信息
     */
    public static String transTenInfo(String code) throws Exception{

        return "";
    }
}
