package com.martin.service.tenpay;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: TenPayCore
 * @Description: 微信支付工具类
 * @author ZXY
 * @date 2016/5/27 16:54
 */
class TenPaySdkUtils {

    //编码格式
    private static String charset = "UTF-8";

    /**
     * 获取签名
     */
    public static String createSdkSign(String privateKey, LinkedHashMap<String, String> paraMap) {

        StringBuilder sb = new StringBuilder();

        Set es = paraMap.entrySet();//所有参与传参的参数按照ASCII排序（升序）
        for (Object e : es) {
            Map.Entry entry = (Map.Entry) e;
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)) {
                sb.append(k).append("=").append(v).append("&");
            }
        }

        sb.append("key=");
        sb.append(privateKey); // 这里必须要用商户的KEY代码,我靠..

        System.err.println(sb.toString());
        String appSign = MD5(sb.toString());
        return appSign;
    }

    public static String MD5(String str) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(charset));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }
        return md5StrBuff.toString().toUpperCase();
    }
}
