package com.martin.service;

import java.util.Map;

/**
 * @ClassName: IPayWebCenter
 * @Description:
 * @author ZXY
 * @date 2017/1/9 14:18
 */
public interface IPayAppCenter {
    /**
     * @Description: 生成支付信息
     * @return
     * @throws
     */
    Map<String, Object> buildPayInfo(String tmpThdType, String tmpBizId, String tmpBizType, String ipAddress) throws Exception;

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
