package com.martin.service;

import java.util.Map;

/**
 * @author ZXY
 * @ClassName: IPayWebCenter
 * @Description:
 * @date 2017/1/9 14:18
 */
public interface IPayAppCenter {
    /**
     * @return
     * @throws
     * @Description: 生成支付信息
     */
    Map<String, Object> buildPayInfo(String appId, String tmpPayType, String bizId, String tmpBizType, String ipAddress) throws Exception;

    /**
     * @return void
     * @throws
     * @Description: 第三方回调--支付
     */
    void doPayNotify(int payType, String ipAddress, Map<String, String> reqParam) throws Exception;
}
