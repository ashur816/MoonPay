package com.martin.service;

import com.martin.dto.PayInfo;

import java.util.Map;

/**
 * @ClassName: IPayWebCenter
 * @Description:
 * @author ZXY
 * @date 2017/1/9 14:18
 */
public interface IPayWebCenter {

    /**
     * @Description: 授权信息
     * @param  payType  支付渠道 支付宝/微信等
     * @param  bizId    业务id ： 订单id等
     * @return PayInfo
     * @throws
     */
    PayInfo doAuthorize(int payType, String bizId, String bizType) throws Exception;

    /**
     * @Description: 支付入口
     * @param  payType  支付渠道 支付宝/微信等
     * @return String
     * @throws
     */
    PayInfo doPay(String appId, int payType, String bizId, int bizType, String ipAddress, String code) throws Exception;

    /**
     * @Description: 退款
     * @return void
     * @throws
     */
    Object doRefund(Long flowId) throws Exception;

    /**
     * @Description: 第三方回调--支付
     * @return void
     * @throws
     */
    void doPayNotify(int payType, String ipAddress, Map<String, String> reqParam) throws Exception;

    /**
     * @Description: 第三方回调--退款
     * @return void
     * @throws
     */
    void doRefundNotify(int payType, String ipAddress, Map<String, String> reqParam) throws Exception;
}
