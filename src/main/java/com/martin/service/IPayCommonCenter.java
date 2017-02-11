package com.martin.service;

import com.martin.dto.PayInfo;
import com.martin.dto.ToPayInfo;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IPayCenter
 * @Description: 支付中心接口
 * @author ZXY
 * @date 2016/6/16 13:25
 */
public interface IPayCommonCenter {

    /**
     * @Description: 获取业务订单信息
     * @return void
     * @throws
     */
    ToPayInfo getToPayInfo(String bizId, int bizType) throws Exception;

    /**
     * @Description: 第三方回调
     * @return void
     * @throws
     */
    void doNotify(String notifyType, int payType, String ipAddress, Map<String, String> reqParam) throws Exception;

    /**
     * @Description: 支付回调分发业务处理
     * @return void
     * @throws
     */
    void doNotifyBusiness(String bizId, int bizType, int payAmount) throws Exception;

    /**
     * @Description: 获取退款信息
     * @return void
     * @throws
     */
    List<PayInfo> getRefundInfo(String appId, int payType, String tmpFlowId) throws Exception;

    /**
     * @Description: 企业付款
     * @param
     * @return
     * @throws
     */
    Object doTransfer(int payType, List<Long> flowIdList, String ipAddress) throws Exception;

    /**
     * @Description: 退款
     * @return void
     * @throws
     */
    Object doRefund(List<String> flowIdList, String refundReason) throws Exception;
}
