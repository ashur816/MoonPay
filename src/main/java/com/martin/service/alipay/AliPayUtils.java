package com.martin.service.alipay;

import com.martin.utils.PayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author ZXY
 * @ClassName: AlipayCore
 * @Description: 支付宝工具类
 * @date 2016/5/24 19:27
 */
public class AliPayUtils {

    //编码格式
    private static String charset = "UTF-8";

    //连接超时时间，默认10秒
    private static int socketTimeout = 10000;

    //传输超时时间，默认30秒
    private static int connectTimeout = 30000;

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        String key = "";
        String value = "";
        for (Map.Entry<String, String> entry : sArray.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (StringUtils.isEmpty(value) || key.equalsIgnoreCase("sign") /*|| key.equalsIgnoreCase("sign_type")*/) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder preStr = new StringBuilder();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                preStr.append(key).append("=").append(value);
            } else {
                preStr.append(key).append("=").append(value).append("&");
            }
        }

        return preStr.toString();
    }

    /**
     * 生成签名结果
     *
     * @return 签名结果字符串
     */
    public static String buildRequestMySign(String privateKey, String signType, Map<String, String> sPara) throws Exception {
        String preStr = createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mySign = "";
        if (("MD5").equalsIgnoreCase(signType)) {
            mySign = MD5.sign(preStr, privateKey, charset);
        } else if (("RSA").equalsIgnoreCase(signType)) {
            mySign = RSA.sign(preStr, privateKey, charset);
        }
        return mySign;
    }

    /**
     * 建立请求，以表单HTML形式构造（默认）
     */
    public static String buildReqForm(String payUrl, String privateKey, String signType, Map<String, String> sParaTemp) throws Exception {
        //除去数组中的空值和签名参数
        Map<String, String> paraMap = paraFilter(sParaTemp);

        //生成签名结果
        String mySign = buildRequestMySign(privateKey, signType, paraMap);

        //签名结果与签名方式加入请求提交参数组中
        paraMap.put("sign", mySign);

        List<String> keys = new ArrayList<>(paraMap.keySet());

        StringBuffer sbHtml = new StringBuffer();
        sbHtml.append("<form id=\"payForm\" name=\"payForm\" action=\"" + payUrl + "\" method=\"get\">");

        for (int i = 0; i < keys.size(); i++) {
            String name = keys.get(i);
            String value = paraMap.get(name);
            sbHtml.append("<input type=\"\" name=\"" + name + "\" value=\"" + value + "\"/>");
        }

        //submit按钮控件请不要含有name属性
        sbHtml.append("</form>");

        return sbHtml.toString();
    }

    /**
     * 获取远程服务器ATN结果
     *
     * @return 服务器ATN结果
     * 验证结果集：
     * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     * true 返回正确信息
     * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    public static String checkUrl(String urlValue) {
        String inputLine = "";

        try {
            URL url = new URL(urlValue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), charset));
            inputLine = in.readLine();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            inputLine = "";
        }

        return inputLine;
    }


    /**
     * 建立请求，以表单HTML形式构造（默认）
     */
    public static String buildReqUrl(String payUrl, String privateKey, Map<String, String> paraMap) throws Exception {
        //拼装请求参数
        String bizParam = PayUtils.buildPayParam(paraMap);

        //生成签名结果
        String sign = RSA.sign(bizParam, privateKey, charset);

        String tmpString = bizParam + "&sign=" + sign;

        StringBuffer sbHtml = new StringBuffer();
        sbHtml.append("<form id=\"payForm\" name=\"payForm\" action=\"" + payUrl + "?" + tmpString + "\" method=\"get\">");

        //submit按钮控件请不要含有name属性
        sbHtml.append("</form>");

        return sbHtml.toString();
    }
}
