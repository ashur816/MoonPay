package com.martin.utils;

import com.martin.constant.TenPublicParam;
import org.apache.commons.codec.Charsets;

/**
 * @author ZXY
 * @ClassName: TenOpenUtils
 * @Description:
 * @date 2017/3/3 15:00
 */
public class TenPublicUtils {

    /**
     * 根据openId获取微信用户信息
     *
     * @param openId 微信用户ID
     * @return
     */
    public static String getUserInfo(String openId) throws Exception {
        StringBuilder sb = new StringBuilder();
        // 获取微信 access_token/openid
        sb.append("&access_token=").append(TenPublicParam.token).append("&openid=").append(openId).append("&lang=").append(TenPublicParam.lang);
        return HttpUtils.sendPostXml(TenPublicParam.getUserInfoUrl, sb.toString(), Charsets.UTF_8.toString());
    }
}
