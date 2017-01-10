package com.martin.service.alipay;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AlipayCore
 * @Description: 支付宝工具类
 * @author ZXY
 * @date 2016/5/24 19:27
 */
public class AliPayAppUtils {

    //编码格式
    private static String charset = "UTF-8";

    public static Map<String, String> createAliPayOrder(String privateKey, Map<String, String> params) throws Exception {
        String orderParam = buildOrderParam(params);
        String sign = getSign(params, privateKey);
        final String orderInfo = orderParam + "&sign=" + sign;
        params.put("sign", sign);
        params.put("payInfo", orderInfo);
        return params;
    }

    private static String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, 0));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, 0));

        return sb.toString();
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param codeFlag -1-不做操作  0-encode 1-decode
     * @return
     */
    private static String buildKeyValue(String key, String value, int codeFlag) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (codeFlag == 0) {
            try {
                sb.append(URLEncoder.encode(value, charset));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else if (codeFlag == 1) {
            try {
                sb.append(URLDecoder.decode(value, charset));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * 对支付参数信息进行签名
     * @param map 待签名授权信息
     * @return
     */
    public static String getSign(Map<String, String> map, String rsaKey) throws Exception {
        List<String> keys = new ArrayList<>(map.keySet());
        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            authInfo.append(buildKeyValue(key, value, -1));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, -1));

        String oriSign = RSA.sign(authInfo.toString(), rsaKey, charset);
        String encodedSign = URLEncoder.encode(oriSign, charset);
        return encodedSign;
    }

    /**
     * 对支付回调参数信息进行签名
     * @param map 待签名授权信息
     * @return
     */
    public static boolean checkBackSign(Map<String, String> map, String rsaKey, String aliSign) throws Exception {
        List<String> keys = new ArrayList<>(map.keySet());

        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            authInfo.append(buildKeyValue(key, value, 0));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, 0));

        return RSA.verify(authInfo.toString(), aliSign, rsaKey, charset);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param paraMap
     * @return 所代表远程资源的响应结果
     * @throws Exception
     */
    public static String sendPost(String url, String privateKey, Map<String, String> paraMap) throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setConnectTimeout(20000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));

            //拼装请求参数
            String sign = getSign(paraMap, privateKey);
            String tmpString = buildOrderParam(paraMap) + "&sign=" + sign;
            // 发送请求参数
            out.print(tmpString);

            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
            throw e;
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
