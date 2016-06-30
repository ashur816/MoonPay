package com.martin.service.tenpay;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @ClassName: TenPayCore
 * @Description: 微信支付工具类
 * @author ZXY
 * @date 2016/5/27 16:54
 */
class TenPayUtils {

    //编码格式
    private static String charset = "UTF-8";

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
        SortedMap filterMap = paraFilter(parameters);

        StringBuilder sb = new StringBuilder();
        Set es = filterMap.entrySet();//所有参与传参的参数按照ASCII排序（升序）
        for (Object e : es) {
            Map.Entry entry = (Map.Entry) e;
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)) {
                sb.append(k).append("=").append(v).append("&");
            }
        }
        sb.append("key=").append(key);
        return MD5Encode(sb.toString());
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
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static SortedMap<String, String> paraFilter(Map<String, String> sArray) {

        SortedMap<String, String> result = new TreeMap<>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (StringUtils.isBlank(value) || key.equalsIgnoreCase("sign")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        String preStr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                preStr = preStr + key + "=" + value;
            } else {
                preStr = preStr + key + "=" + value + "&";
            }
        }

        return preStr;
    }

    /**
     * @Description: xml转map
     * @param
     * @return
     * @throws
     */
    public static SortedMap<String, String> getMapFromXML(String xmlString) throws Exception {

        //这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputStream is = getStringStream(xmlString);
        Document document = builder.parse(is);

        //获取到document里面的全部结点
        NodeList allNodes = document.getFirstChild().getChildNodes();
        Node node;
        SortedMap<String, String> map = new TreeMap<>();
        int i = 0;
        while (i < allNodes.getLength()) {
            node = allNodes.item(i);
            if (node instanceof Element) {
                map.put(node.getNodeName(), node.getTextContent());
            }
            i++;
        }
        return map;

    }

    private static String MD5Encode(String text) {
        return Hashing.md5().newHasher().putString(text, Charsets.UTF_8).hash().toString().toUpperCase();
    }

    private static InputStream getStringStream(String sInputString) throws Exception {
        ByteArrayInputStream tInputStringStream = null;
        if (sInputString != null && !sInputString.trim().equals("")) {
            tInputStringStream = new ByteArrayInputStream(sInputString.getBytes(charset));
        }
        return tInputStringStream;
    }
}
