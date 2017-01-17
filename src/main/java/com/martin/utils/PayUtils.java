package com.martin.utils;

import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.dto.PayResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayAppService;
import com.martin.service.IPayCommonService;
import com.martin.service.IPayWebService;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: PayUtils
 * @Description:
 * @author ZXY
 * @date 2017/1/4 11:25
 */
public class PayUtils {

    /**
     * @Description: 动态选择收款账号来源
     * @param clientSource 发起支付的客户端来源，就是客户端appId
     * @return
     * @throws
     */
    public static Map<String, String> getPaySource(int payType, String clientSource) {
        //根据支付客户端选择不同的sdk支付参数
        String prefixId;
        if (clientSource.indexOf(".") != -1) {
            prefixId = clientSource.substring(0, clientSource.indexOf("."));
        } else {
            //appId参数格式不正确
            throw new BusinessException("appId参数格式不正确");
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
     * @Description: 动态选择服务实例 -common
     * @param payType 第三方类型
     * @return
     * @throws
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
     * @Description: 动态选择服务实例-WEB
     * @param payType 第三方类型
     * @return
     * @throws
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
     * @Description: 动态选择服务实例-SDK
     * @param payType 第三方类型
     * @return
     * @throws
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
}
