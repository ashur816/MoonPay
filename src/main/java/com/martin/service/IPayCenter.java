package com.martin.service;

import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IPayCenter
 * @Description: 支付中心接口
 * @author ZXY
 * @date 2016/6/16 13:25
 */
public interface IPayCenter {

    /**
     * @Description: 获取基础订单信息
     * @param  bizId    业务id ： 订单id等
     * @return PayInfo
     * @throws
     */
    PayInfo getPayInfo(String bizId) throws Exception;


    /**
     * @Description: 支付入口
     * @param  payType  支付渠道 支付宝/微信等
     * @param  bizId    业务id ： 订单id等
     * @return String
     * @throws
     */
    PayInfo doPay(String payType, String bizId, String ipAddress, String code, String voucherId) throws Exception;

    /**
     * @Description: 授权信息
     * @param  payType  支付类型 支付宝/微信等
     * @param  bizId    业务id ： 订单id等
     * @return PayInfo
     * @throws
     */
    PayInfo doAuthorize(String payType, String bizId) throws Exception;

    /**
     * @Description: 第三方回调
     * @return void
     * @throws
     */
    void doNotify(String notifyType, String payType, String ipAddress, Map<String, String> reqParam) throws Exception;


    /**
     * @Description: 获取退款信息
     * @param  flowIdList   收银台流水号
     * @return PayInfo
     * @throws
     */
    List<PayInfo> getRefundInfo(List<String> flowIdList) throws Exception;

    /**
     * @Description: 退款
     * @return void
     * @throws
     */
    Object doRefund(List<String> flowIdList, String refundReason) throws Exception;

    /**
     * @Description: 企业付款
     * @return void
     * @throws
     */
    PayResult doWithdraw(Long acctId, String payType, Integer drawAmount, String ipAddress) throws Exception;

    /**
     * @Description: 查询第三方支付结果
     * @param
     * @return
     * @throws
     */
    PayResult doQueryState(String payType, String bizId) throws Exception;
}
