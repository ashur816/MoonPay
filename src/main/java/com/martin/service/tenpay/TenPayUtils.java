package com.martin.service.tenpay;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.martin.exception.BusinessException;
import com.martin.utils.PayUtils;

import java.util.*;

/**
 * @author ZXY
 * @ClassName: TenPayCore
 * @Description: 微信支付工具类
 * @date 2016/5/27 16:54
 */
class TenPayUtils {

    static String createRequestXml(String privateKey, SortedMap<String, String> parameters) {
        //随机码
        parameters.put("nonce_str", createNonceStr());

        String mySign = createSign(privateKey, parameters);
        //MD5加密
        parameters.put("sign", mySign);

        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        for (Object e : es) {
            Map.Entry entry = (Map.Entry) e;
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
//            if ("total_fee".equalsIgnoreCase(k)) {
            sb.append("<").append(k).append(">").append(v).append("</").append(k).append(">");
//            } else {
//            sb.append("<").append(k).append(">").append("<![CDATA[").append(v).append("]]></").append(k).append(">");
//            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    @SuppressWarnings("rawtypes")
    static String createSign(String key, SortedMap<String, String> parameters) {
        //过滤空值和sign
        Map filterMap = PayUtils.paramFilter(parameters);

        //拼装请求参数
        String bizParam = PayUtils.buildConcatStr(filterMap);
        bizParam += "&key=" + key;
        System.out.println("签名参数-" + bizParam);
        return MD5Encode(bizParam);
    }


    static String createNonceStr() {
        Random random = new Random();
        return MD5Encode(String.valueOf(random.nextInt(10000)));
    }

    static String createPageRequest(Map<String, String> paraMap) {
        List<String> keys = new ArrayList<>(paraMap.keySet());

        StringBuffer sbHtml = new StringBuffer();

        for (int i = 0; i < keys.size(); i++) {
            String id = keys.get(i);
            String value = paraMap.get(id);
            sbHtml.append("<input type=\"\" id=\"").append(id).append("\" value=\"").append(value).append("\"/>");
        }
        return sbHtml.toString();
    }

    /**
     * 验签
     */
    public static SortedMap<String, String> returnValidate(String privateKey, Map<String, String> paraMap) throws Exception {
        String tmpXml = paraMap.get("content");
        SortedMap<String, String> sortedMap = PayUtils.getMapFromXML(tmpXml, Charsets.UTF_8.toString());

        if (sortedMap == null || sortedMap.size() < 1) {
            //参数不能为空
            throw new BusinessException("参数不能为空");
        }

        String returnSign = sortedMap.get("sign");
        String mySign = createSign(privateKey, sortedMap);
        if (!returnSign.equals(mySign)) {
            //回调签名不匹配
            throw new BusinessException("回调签名不匹配");
        }
        return sortedMap;
    }

    private static String MD5Encode(String text) {
        return Hashing.md5().newHasher().putString(text, Charsets.UTF_8).hash().toString().toUpperCase();
    }
}
