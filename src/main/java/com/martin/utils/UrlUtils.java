package com.martin.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ZXY
 * @ClassName: UrlUtils
 * @Description:
 * @date 2017/1/11 16:00
 */
public class UrlUtils {

    /**
     * 去除url中指定参数
     * @return
     */
    public static String replaceParamReg(String url, String name) {
        if (StringUtils.isNotBlank(url)) {
            String reg = "(" + name + "=[^&]*)";//单个参数
            String reg1 = "(&&)";//去除双&&
            url = url.replaceAll(reg, "");
            url = url.replaceAll(reg1, "&");
        }
        return url;
    }


    public static void main(String[] args) {
        String s = UrlUtils.replaceParamReg("https://www.baidu.com?a=1&b=2","b");
        System.out.println(s);
    }
}
