package com.martin.service;

import com.martin.dto.PayInfo;

import java.util.Map;

/**
 * @author ZXY
 * @ClassName: IPayWebCenter
 * @Description:
 * @date 2017/1/9 14:18
 */
public interface IPayWebCenter {

    /**
     * @param payType 支付渠道 支付宝/微信等
     * @param bizId   业务id ： 订单id等
     * @return PayInfo
     * @throws
     * @Description: 授权信息
     */
    PayInfo doAuthorize(int payType, String bizId, String bizType) throws Exception;

    /**
     * @param payType 支付渠道 支付宝/微信等
     * @return String
     * @throws
     * @Description: 支付入口
     */
    PayInfo doPay(int payType, String bizId, int bizType, String ipAddress, String code) throws Exception;

    /**
     * @return void
     * @throws
     * @Description: 第三方回调--支付
     */
    void doPayNotify(int payType, String ipAddress, Map<String, String> reqParam) throws Exception;
}
