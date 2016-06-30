package com.martin.utils;

import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * @ClassName: RandomUtils
 * @Description: 随机码工具
 * @author ZXY
 * @date 2016/6/17 13:48
 */
public class RandomUtils {

    public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String numberChar = "123456789";

    /**
     * @Description: 随机数
     * @param digit 位数
     * @return
     * @throws
     */
    public static String generateRandomNum(int digit) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < digit; i++) {
            sb.append(numberChar.charAt(random.nextInt(9)));
        }
        return sb.toString();
    }

    /**
     * @Description: 字母+数字随机数
     * @param digit 位数
     * @return
     * @throws
     */
    public static String generateMixString(int digit) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < digit; i++) {
            sb.append(allChar.charAt(random.nextInt(letterChar.length())));
        }
        return sb.toString();
    }

    /**
     * 支付单号
     * 20160531091530592368 19位
     * @return
     */
    public static String getPaymentNo() {
        Long currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(currentTime) + generateRandomNum(5);
    }

    public static void main(String[] args) {
        System.out.println(generateMixString(8));
    }
}
