package com.martin.service;

import com.martin.dto.PayInfo;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ITBService
 * @Description:
 * @author ZXY
 * @date 2016/8/18 17:16
 */
public interface ITBService {

    /**
     * @Description: 获取退款信息
     * @param  flowIdList   收银台流水号
     * @return PayInfo
     * @throws
     */
    List<PayInfo> getRefundInfo(List<String> flowIdList) throws Exception;

    /**
     * @Description: 退款/批量退款的payType一定要一样
     * @return void
     * @throws
     * @param flowIdList
     * @param refundReason
     */
    Object doRefund(List<String> flowIdList, String refundReason) throws Exception;

    /**
     * @Description: 第三方回调
     * @return void
     * @throws
     */
    void doNotify(String notifyType, String payType, String ipAddress, Map<String, String> reqParam) throws Exception;
}
