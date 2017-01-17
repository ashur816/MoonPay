package com.martin.service;

import com.martin.dto.PayInfo;

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
     * @Description: 第三方回调
     * @return void
     * @throws
     */
    void doNotify(String notifyType, int payType, String ipAddress, Map<String, String> reqParam) throws Exception;

    /**
     * @Description: 获取退款信息
     * @return void
     * @throws
     */
    List<PayInfo> getRefundInfo(String appId, int payType, String flowId) throws Exception;

    /**
     * @Description: 企业付款
     * @param
     * @return
     * @throws
     */
    Object doTransfer(int payType, List<Long> flowIdList, String ipAddress) throws Exception;

    /**
     * @Description:
     * @return void
     * @throws
     */
    PayInfo doRefund(List<String> flowIdList, String refundReason) throws Exception;
}
