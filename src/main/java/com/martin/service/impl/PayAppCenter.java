package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.dto.PayInfo;
import com.martin.dto.ToPayInfo;
import com.martin.exception.BusinessException;
import com.martin.service.IPayAppCenter;
import com.martin.service.IPayAppService;
import com.martin.service.IPayFlow;
import com.martin.utils.PayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZXY
 * @ClassName: PayAppCenter
 * @Description:
 * @date 2017/1/10 14:03
 */
@Service("payAppCenter")
public class PayAppCenter implements IPayAppCenter {

    @Resource
    private IPayFlow payFlow;

    /**
     * @param tmpPayType
     * @param bizId
     * @param tmpBizType
     * @param ipAddress
     * @return
     * @throws
     * @Description: 生成支付信息
     */
    @Override
    public Map<String, Object> buildPayInfo(String appId, String tmpPayType, String bizId, String tmpBizType, String ipAddress) throws Exception {
        int payType = Integer.parseInt(tmpPayType);
        int bizType = Integer.parseInt(tmpBizType);

        if (bizType != PayConstant.BIZ_TYPE_GRAB) {
            //当前业务类型不支持APP支付
            throw new BusinessException("当前业务类型不支持APP支付");
        }
        if (PayConstant.PAY_TYPE_ALI != payType && PayConstant.PAY_TYPE_TEN != payType) {
            //暂不支持当前支付方式
            throw new BusinessException("暂不支持当前支付方式");
        }
        //获取支付参数
        Map<String, String> extMap = PayUtils.getPaySource(payType, appId);
        String thdAppId = extMap.get("appId");
        extMap.put("ipAddress", ipAddress);
        //获取订单支付信息
        ToPayInfo orderPayInfo = new ToPayInfo();
        orderPayInfo.setGoodName("测试");
        orderPayInfo.setPayAmount(1);

        //生成新的支付流水
        PayFlowBean flowBean = payFlow.buildPayFlow(PayConstant.APP_ID_APP, thdAppId, bizId, bizType, orderPayInfo.getPayAmount());
        flowBean.setPayType(payType);
        //保存流水信息
        payFlow.addPayFlow(flowBean);
        Map<String, String> thdPayMap = buildThdPayInfo(payType, flowBean, extMap);
        //支付组成
        Map<String, Object> payMap = new HashMap<>();
        if (PayConstant.PAY_TYPE_TEN == payType) {
            payMap.put("tenPay", thdPayMap);
        } else {
            payMap.put("aliPay", thdPayMap);
        }
        return payMap;
    }

    /**
     * @param tmpFlowId
     * @return void
     * @throws
     * @Description: 获取退款信息
     */
    @Override
    public List<PayInfo> getRefundInfo(int payType, String tmpFlowId) throws Exception {
        return null;
    }

    /**
     * @param flowBeanList
     * @return void
     * @throws
     * @Description: 退款
     */
    @Override
    public Object doRefund(List<PayFlowBean> flowBeanList, String refundReason) throws Exception {
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

    /**
     * @Description: 获取第三方支付信息
     **/
    private Map<String, String> buildThdPayInfo(int payType, PayFlowBean payFlowBean, Map<String, String> extMap) throws Exception {
        if (PayConstant.PAY_TYPE_ALI != payType && PayConstant.PAY_TYPE_TEN != payType) {
            //暂不支持当前支付方式
            throw new BusinessException("暂不支持当前支付方式");
        }
        //组装支付参数
        IPayAppService payAppService = PayUtils.getAppPayInstance(payType);
        return payAppService.buildPayInfo(payFlowBean, extMap);
    }
}
