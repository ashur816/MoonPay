package com.martin.service;

import com.martin.dto.PayInfo;
import com.martin.dto.ToPayInfo;

import java.util.List;
import java.util.Map;

/**
 * @author ZXY
 * @ClassName: IPayCenter
 * @Description: 支付中心接口
 * @date 2016/6/16 13:25
 */
public interface IPayCommonCenter {

    /**
     * @return void
     * @throws
     * @Description: 获取业务订单信息
     */
    ToPayInfo getToPayInfo(String bizId, int bizType) throws Exception;

    /**
     * @return void
     * @throws
     * @Description: 第三方回调
     */
    void doNotify(String notifyType, int payType, String ipAddress, Map<String, String> reqParam) throws Exception;

    /**
     * @return void
     * @throws
     * @Description: 支付回调分发业务处理
     */
    void doNotifyBusiness(String bizId, int bizType, int payAmount) throws Exception;

    /**
     * @return void
     * @throws
     * @Description: 获取退款信息
     */
    List<PayInfo> getRefundInfo(int payType, String appId) throws Exception;

    /**
     * @param
     * @return
     * @throws
     * @Description: 企业付款
     */
    Object doTransfer(String thdNo, String thdName, int drawAmount, int payType, String ipAddress) throws Exception;

    /**
     * @return void
     * @throws
     * @Description: 退款
     */
    Object doRefund(List<String> flowIdList, String refundReason) throws Exception;
}
