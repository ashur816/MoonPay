package com.martin.service.alipay;

import com.martin.constant.PayParam;
import com.martin.utils.PayUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * @author ZXY
 * @ClassName: AlipayCore
 * @Description: 支付宝工具类
 * @date 2016/5/24 19:27
 */
public class AliPayAppUtils {

    public static Map<String, String> createAliPayOrder(String privateKey, Map<String, String> params) throws Exception {
        //1. 请求参数按照key=value&key=value方式拼接的未签名原始字符串
        String bizParam = PayUtils.buildConcatStr(params);
        //2. 再对原始字符串进行签名
        String sign = PayUtils.buildSign(PayParam.aliSignTypeRSA, bizParam, privateKey);
        //3. 最后对请求字符串的所有一级value（biz_content作为一个value）进行encode
        params.put("sign", sign);
        String payInfo = PayUtils.encodePayInfo(params);
        params.put("sign", sign);
        params.put("payInfo", payInfo);
        return params;
    }

    /**
     * 对支付回调参数信息进行签名
     *
     * @param map 待签名授权信息
     * @return
     */
    public static boolean checkBackSign(Map<String, String> map, String aliPublicKey, String returnSign) throws Exception {
        String authInfo = PayUtils.buildConcatStr(map);
        return RSA.verify(authInfo, returnSign, aliPublicKey, PayParam.inputCharset);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url     发送请求的 URL
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

            //去除不必要参数
            Map tmpMap = PayUtils.paramFilter(paraMap);
            //拼装请求参数
            String bizParam = PayUtils.buildConcatStr(tmpMap);
            String sign = PayUtils.buildSign(PayParam.aliSignTypeRSA, privateKey, bizParam);
            String tmpString = bizParam + "&sign=" + sign;
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
