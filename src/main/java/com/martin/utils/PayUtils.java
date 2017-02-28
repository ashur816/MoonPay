package com.martin.utils;

import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.dto.PayResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayAppService;
import com.martin.service.IPayCommonService;
import com.martin.service.IPayWebService;
import com.martin.service.alipay.MD5;
import com.martin.service.alipay.RSA;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.*;

/**
 * @author ZXY
 * @ClassName: PayUtils
 * @Description:
 * @date 2017/1/4 11:25
 */
public class PayUtils {

    /**
     * @param appId 发起支付的客户端来源，就是客户端appId
     * @return
     * @throws
     * @Description: 获取支付来源
     */
    public static String getPayClientSource(String appId) {
        //根据支付客户端选择不同的sdk支付参数
        String prefixId;
        if (appId.indexOf(".") != -1) {
            prefixId = appId.substring(0, appId.indexOf("."));
        } else {
            //appId参数格式不正确
            throw new BusinessException("appId参数格式不正确");
        }

        return prefixId;
    }

    /**
     * @param clientSource 发起支付的客户端来源，就是客户端appId
     * @return
     * @throws
     * @Description: 动态选择收款账号来源
     */
    public static Map<String, String> getPaySource(int payType, String clientSource) {
        //根据支付客户端选择不同的sdk支付参数
        String prefixId;
        if (clientSource.indexOf(".") != -1) {
            prefixId = clientSource.substring(0, clientSource.indexOf("."));
        } else {
            //appId参数格式不正确
//            throw new BusinessException("appId参数格式不正确");
            prefixId = clientSource;
        }
        Map<String, String> extMap = new HashMap<>();
        if (payType == PayConstant.PAY_TYPE_TEN) {//微信
            if (PayConstant.APP_ID_WEB.equals(prefixId)) {//WEB版
                extMap.put("appId", PayParam.tenWebAppId);
                extMap.put("mchId", PayParam.tenWebMchId);
                extMap.put("privateKey", PayParam.tenWebPrivateKey);
            } else if (PayConstant.APP_ID_APP.equals(prefixId)) {//APP版
                extMap.put("appId", PayParam.tenAppAppId);
                extMap.put("mchId", PayParam.tenAppMchId);
                extMap.put("privateKey", PayParam.tenAppPrivateKey);
            } else {
                //appId参数格式不正确
                throw new BusinessException("appId参数格式不正确");
            }
        } else {//支付宝
            if (PayConstant.APP_ID_WEB.equals(prefixId)) {//WEB版
                extMap.put("appId", PayParam.aliWebAppId);
                extMap.put("privateKey", PayParam.aliWebPrivateKey);
            } else if (PayConstant.APP_ID_APP.equals(prefixId)) {//APP版
                extMap.put("appId", PayParam.aliAppAppId);
                extMap.put("privateKey", PayParam.aliAppPrivateKey);
            } else {
                //appId参数格式不正确
                throw new BusinessException("appId参数格式不正确");
            }
        }
        return extMap;
    }

    /**
     * @param payType 第三方类型
     * @return
     * @throws
     * @Description: 动态选择服务实例 -common
     */
    public static IPayCommonService getCommonPayInstance(int payType) {
        //根据渠道不同，调用不同实现类
        String commonPayService;
        if (payType == PayConstant.PAY_TYPE_TEN) {
            commonPayService = "tenPayCommonService";
        } else if (payType == PayConstant.PAY_TYPE_ALI) {
            commonPayService = "aliPayCommonService";
        } else {
            //暂不支持当前支付方式
            throw new BusinessException("暂不支持当前支付方式");
        }
        //返回服务实例
        return new ServiceContainer<IPayCommonService>().get(commonPayService);
    }

    /**
     * @param payType 第三方类型
     * @return
     * @throws
     * @Description: 动态选择服务实例-WEB
     */
    public static IPayWebService getWebPayInstance(int payType) {
        //根据渠道不同，调用不同实现类
        String webPayService;
        if (payType == PayConstant.PAY_TYPE_TEN) {
            webPayService = "tenPayWebService";
        } else if (payType == PayConstant.PAY_TYPE_ALI) {
            webPayService = "aliPayWebService";
        } else {
            //暂不支持当前支付方式
            throw new BusinessException("暂不支持当前支付方式");
        }
        //返回服务实例
        return new ServiceContainer<IPayWebService>().get(webPayService);
    }

    /**
     * @param payType 第三方类型
     * @return
     * @throws
     * @Description: 动态选择服务实例-SDK
     */
    public static IPayAppService getAppPayInstance(int payType) {
        //根据渠道不同，调用不同实现类
        String sdkPayService;
        if (payType == PayConstant.PAY_TYPE_TEN) {
            sdkPayService = "tenPayAppService";
        } else if (payType == PayConstant.PAY_TYPE_ALI) {
            sdkPayService = "aliPayAppService";
        } else {
            //暂不支持当前支付方式
            throw new BusinessException("暂不支持当前支付方式");
        }
        //返回服务实例
        return new ServiceContainer<IPayAppService>().get(sdkPayService);
    }

    /**
     * @Description: 查询web支付情况
     */
    public static PayResult getWebPayStatus(long flowId, int payType) throws Exception {
        IPayWebService payWebService = getWebPayInstance(payType);
        return payWebService.getPayStatus(flowId);
    }

    /**
     * @Description: 查询sdk支付情况
     */
    public static PayResult getSdkPayStatus(long flowId, int payType, Map<String, String> extMap) throws Exception {
        IPayAppService paySdkService = PayUtils.getAppPayInstance(payType);
        return paySdkService.getPayStatus(flowId, extMap);
    }

    /**
     * 转换支付状态
     *
     * @return
     */
    public static int transPayState(String tradeState) {
        int callbackState = -1;
        for (PayReturnCodeEnum anEnum : PayReturnCodeEnum.values()) {
            if (anEnum.getCode().equals(tradeState)) {
                callbackState = anEnum.getPayState();
                break;
            }
        }
        return callbackState;
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param paraMap 签名参数组
     * @return
     */
    public static Map paramFilter(Map<String, String> paraMap) {
        SortedMap<String, String> retMap = new TreeMap<>();
        String key, value;
        for (Map.Entry<String, String> entry : paraMap.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (StringUtils.isEmpty(value) || key.equalsIgnoreCase("sign")) {
                continue;
            } else {
                retMap.put(key, value);
            }
        }
        return retMap;
    }

    /**
     * 把数组所有元素排序，除去数组中的空值和签名参数，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     */
    public static String buildConcatStr(Map<String, String> map) {
        List<String> keys = new ArrayList<>(map.keySet());
        // key排序
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(key).append("=").append(value);
            if (i != keys.size() - 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * @param needSignStr 待加密信息
     * @return
     * @throws
     * @Description: 参数加密
     */
    public static String buildSign(String signType, String privateKey, String needSignStr) {
        String mySign = "";
        if (("MD5").equalsIgnoreCase(signType)) {
            mySign = MD5.sign(needSignStr, privateKey, PayParam.inputCharset);
        } else if (("RSA").equalsIgnoreCase(signType)) {
            mySign = RSA.sign(needSignStr, privateKey, PayParam.inputCharset);
        }
        return mySign;
    }

    /**
     * 对value值进行encode
     */
    public static String encodePayInfo(Map<String, String> map) throws Exception {
        List<String> keys = new ArrayList<>(map.keySet());
        // key排序
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        String sign = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key);
            String encodeValue = URLEncoder.encode(value, PayParam.inputCharset);
            if ("sign".equals(key)) {
                sign = encodeValue;
            } else {
                sb.append(key).append("=").append(encodeValue);
                if (i != keys.size() - 1) {
                    sb.append("&");
                }
            }
        }
        sb.append("&sign=").append(sign);

        return sb.toString();
    }
}
