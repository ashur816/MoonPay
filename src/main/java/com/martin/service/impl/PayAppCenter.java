package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.dto.RefundResult;
import com.martin.dto.ToPayInfo;
import com.martin.exception.BusinessException;
import com.martin.service.*;
import com.martin.utils.PayUtils;
import com.martin.utils.RandomUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private IPayCommonCenter payCommonCenter;

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

        if (bizType != PayConstant.BIZ_TYPE_EXPRESS) {
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
        //获取要支付的信息
        ToPayInfo orderPayInfo = payCommonCenter.getToPayInfo(bizId, bizType);

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
     * @param flowBeanList
     * @return void
     * @throws
     * @Description: 退款
     */
    @Override
    public Object doRefund(List<PayFlowBean> flowBeanList, String refundReason) throws Exception {
        Map<String, String> extMap = new HashMap<>();
        int payType = flowBeanList.get(0).getPayType();
        //同一批必须是同一个支付渠道的
        IPayAppService payAppService = PayUtils.getAppPayInstance(payType);
        Object retObj = "";
        //微信是同步返回  单笔退
        if (PayConstant.PAY_TYPE_TEN == payType) {
            //更新退款流水
            RefundResult refundResult;
            String thdRefundId;
            for (PayFlowBean flowBean : flowBeanList) {
                //已经退款成功的
                if (flowBean.getPayState() == PayConstant.REFUND_SUCCESS) {
                    continue;
                }
                extMap.put("refundId", RandomUtils.getPaymentNo());
                List<PayFlowBean> tmpList = new ArrayList<>();
                tmpList.add(flowBean);
                //发送退款
                refundResult = (RefundResult) payAppService.refund(tmpList, extMap);
                if (refundResult != null) {
                    if (PayConstant.REFUND_SUCCESS == refundResult.getPayState()) {
                        thdRefundId = refundResult.getThdFlowId();

                        //更新支付流水
                        flowBean.setPayState(PayConstant.REFUND_SUCCESS);
                        flowBean.setThdRefundId(thdRefundId);
                        flowBean.setRefundTime(new Date());
                        payFlow.updPayFlow(flowBean);
                    } else {
                        //退款失败
                        flowBean.setPayState(PayConstant.REFUND_FAIL);
                        flowBean.setRefundTime(new Date());
                        payFlow.updPayFlow(flowBean);
                    }
                }
            }
            retObj = "操作成功";
        } else {
            //支付宝是异步返回 多笔退
            String batchNo = RandomUtils.getPaymentNo();
            extMap.put("batchNo", batchNo);
            extMap.put("refundReason", refundReason);
            retObj = payAppService.refund(flowBeanList, extMap);
        }
        return retObj;
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
