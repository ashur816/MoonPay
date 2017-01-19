package com.martin.service.tenpay;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.martin.constant.PayParam;
import com.martin.utils.PayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.*;

/**
 * @author ZXY
 * @ClassName: TenPayCore
 * @Description: 微信支付工具类
 * @date 2016/5/27 16:54
 */
class TenPayUtils {

    //编码格式
    private static String charset = "UTF-8";

    //连接超时时间，默认10秒
    private static int socketTimeout = 10000;

    //传输超时时间，默认30秒
    private static int connectTimeout = 30000;

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

        //拼装请求参数
        String bizParam = PayUtils.buildPayParam(filterMap);
        bizParam += "key=" + key;
        System.out.println("支付参数-" + bizParam);
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
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static SortedMap<String, String> paraFilter(Map<String, String> sArray) {

        SortedMap<String, String> result = new TreeMap<>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        String value = "";
        String key = "";
        Iterator iterator = sArray.keySet().iterator();
        while (iterator.hasNext()) {
            key = iterator.next().toString();
            value = sArray.get(key);
            if (StringUtils.isBlank(value) || "sign".equalsIgnoreCase(key)) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    /**
     * 通过Https往API post xml数据
     *
     * @param url    API地址
     * @param xmlObj 要提交的XML数据对象
     * @return API回包的实际数据
     * @throws Exception
     */
    static String sendPostXml(String url, String xmlObj) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        String result = null;
        HttpPost httpPost = new HttpPost(url);

        //得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
        StringEntity postEntity = new StringEntity(xmlObj, charset);
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(postEntity);

        //设置请求器的配置
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);

        try {
            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity, charset);
        } finally {
            httpPost.abort();
        }

        return result;
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 使用安全证书，发送请求
     */
    public static String sendPostWithCert(String url, String params, String charset) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream inStream = new FileInputStream(new File(PayParam.certPath));
        try {
            keyStore.load(inStream, PayParam.tenWebMchId.toCharArray());
        } finally {
            inStream.close();
        }

        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, PayParam.tenWebMchId.toCharArray()).build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslSf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslSf).build();
        HttpPost httpPost = new HttpPost(url);

        String result = null;
        //得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
        StringEntity postEntity = new StringEntity(params, charset);
        httpPost.addHeader("Content-Type", "application/xml");
        httpPost.setEntity(postEntity);

        //设置请求器的配置
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);

        try {
            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity, charset);
        } finally {
            httpPost.abort();
        }

        return result;
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: xml转map
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
