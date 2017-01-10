package com.martin.service.impl;

import com.martin.service.IPayAppCenter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author ZXY
 * @ClassName: PayAppCenter
 * @Description:
 * @date 2017/1/10 14:03
 */
@Service("payAppCenter")
public class PayAppCenter implements IPayAppCenter {

    /**
     * @param tmpThdType
     * @param tmpBizId
     * @param tmpBizType
     * @param ipAddress
     * @return
     * @throws
     * @Description: 生成支付信息
     */
    @Override
    public Map<String, Object> buildPayInfo(String tmpThdType, String tmpBizId, String tmpBizType, String ipAddress) throws Exception {
        return null;
    }

    /**
     * @param flowId
     * @return void
     * @throws
     * @Description: 退款
     */
    @Override
    public Object doRefund(Long flowId) throws Exception {
        return null;
    }

    /**
     * @param payType
     * @param ipAddress
     * @param reqParam
     * @return void
     * @throws
     * @Description: 第三方回调--支付
     */
    @Override
    public void doPayNotify(int payType, String ipAddress, Map<String, String> reqParam) throws Exception {

    }

    /**
     * @param payType
     * @param ipAddress
     * @param reqParam
     * @return void
     * @throws
     * @Description: 第三方回调--退款
     */
    @Override
    public void doRefundNotify(int payType, String ipAddress, Map<String, String> reqParam) throws Exception {

    }
}
