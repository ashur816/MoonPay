package com.martin.service.alipay;

import com.martin.constant.PayParam;
import com.martin.exception.BusinessException;
import com.martin.utils.PayUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * 建立请求，以表单HTML形式构造（默认）
     */
    public static String buildReqForm(String payUrl, String privateKey, String signType, Map<String, String> sParaTemp) throws Exception {
        //除去数组中的空值和签名参数
        Map<String, String> paraMap = PayUtils.paramFilter(sParaTemp);
        //排序，组成待签名字符串，sign_type不参与加密
        paraMap.remove("sign_type");
        String needSignStr = PayUtils.buildConcatStr(paraMap);
        //生成签名结果
        String mySign = PayUtils.buildSign(signType, privateKey, needSignStr);

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
        String bizParam = PayUtils.buildConcatStr(paraMap);

        //生成签名结果
        String sign = PayUtils.buildSign("RSA", privateKey, bizParam);

        String tmpString = bizParam + "&sign=" + sign;

        StringBuffer sbHtml = new StringBuffer();
        sbHtml.append("<form id=\"payForm\" name=\"payForm\" action=\"" + payUrl + "?" + tmpString + "\" method=\"get\">");

        //submit按钮控件请不要含有name属性
        sbHtml.append("</form>");

        return sbHtml.toString();
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 回调验签
     */
    public static void returnValidate(String privateKey, Map<String, String> paraMap) throws Exception {
        if (paraMap == null || paraMap.size() < 1) {
            //参数不能为空
            throw new BusinessException("参数不能为空");
        }

        //判断responseTxt是否为true，isSign是否为true
        //responseTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        String responseTxt = "false";
        if (paraMap.get("notify_id") != null) {
            String notify_id = paraMap.get("notify_id");
            String verify_url = PayParam.aliVerifyUrl + "&partner=" + PayParam.aliPartnerId + "&notify_id=" + notify_id;
            responseTxt = AliPayUtils.checkUrl(verify_url);
        }
        if ("false".equalsIgnoreCase(responseTxt)) {
            //支付宝回调异常
            throw new BusinessException("支付宝回调异常");
        }

        String returnSign = paraMap.get("sign");
        Map<String, String> tmpMap = PayUtils.paramFilter(paraMap);

        String signType = paraMap.get("sign_type");
        //签名不带sign_type
        tmpMap.remove("sign_type");
        String needSignStr = PayUtils.buildConcatStr(tmpMap);

        String mySign = PayUtils.buildSign(signType, privateKey, needSignStr);
        if (!returnSign.equals(mySign)) {
            System.out.println("回调验签失败");
            //支付宝回调签名不匹配
//            throw new BusinessException("支付宝回调签名不匹配");
        }
    }
}
