package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.dto.RefundResult;
import com.martin.dto.ToPayInfo;
import com.martin.exception.BusinessException;
import com.martin.service.IPayFlow;
import com.martin.service.IPayWebCenter;
import com.martin.service.IPayWebService;
import com.martin.utils.PayUtils;
import com.martin.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ZXY
 * @ClassName: PayWebCenter
 * @Description:
 * @date 2017/1/10 13:30
 */
@Service("payWebCenter")
public class PayWebCenter implements IPayWebCenter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IPayFlow payFlow;

    /**
     * @param payType 支付渠道 支付宝/微信等
     * @param bizId   业务id ： 订单id等
     * @param bizType @return PayInfo
     * @throws
     * @Description: 授权信息
     */
    @Override
    public PayInfo doAuthorize(int payType, String bizId, String bizType) throws Exception {
        IPayWebService payService = PayUtils.getWebPayInstance(payType);
        return payService.authorize(bizId, bizType);
    }

    /**
     * @param payType   支付渠道 支付宝/微信等
     * @param bizId     业务id ： 订单id等
     * @param bizType
     * @param ipAddress
     * @param code
     * @throws
     * @Description: 支付入口
     */
    @Override
    public PayInfo doPay(String appId, int payType, String bizId, int bizType, String ipAddress, String code) throws Exception {
        if (StringUtils.isEmpty(bizId) || StringUtils.isEmpty(ipAddress)) {
            throw new BusinessException("参数不能为空");
        }

        //防并发，保证一个订单只有一个人支付
        //根据订单号查询支付流水信息 state=1 有效记录，支付失败的会变无效
        logger.info("根据业务查询支付流水");
        //查有效记录 state=1
        List<PayFlowBean> flowBeanList = payFlow.getPayFlowListByBiz(bizId, bizType);
        if (!CollectionUtils.isEmpty(flowBeanList)) {
            //已存在支付记录，遍历，如有已支付成功的，不继续支付
            for (PayFlowBean tmpBean : flowBeanList) {
                //只有第三方支付的才需要查询支付状态
                int oldThdType = tmpBean.getPayType();
                if (oldThdType == PayConstant.PAY_TYPE_TEN || oldThdType == PayConstant.PAY_TYPE_ALI) {
                    if (PayConstant.PAY_NOT == tmpBean.getPayState()) {
                        //去第三方查询支付情况
                        PayResult payResult = PayUtils.getWebPayStatus(tmpBean.getFlowId(), oldThdType);
                        if (PayConstant.PAY_SUCCESS == payResult.getPayState()) {
                            //更新支付流水
                            tmpBean.setThdFlowId(payResult.getThdFlowId());
                            tmpBean.setPayState(PayConstant.PAY_SUCCESS);
                            tmpBean.setPayTime(new Date());
                            payFlow.updPayFlow(tmpBean);
                            //订单已经支付，系统正在处理中，请勿重复支付
                            throw new BusinessException("订单已经支付，系统正在处理中，请勿重复支付");
                        }
                    } else {
                        //该订单已支付
                        throw new BusinessException("该订单已支付");
                    }
                }
            }
        }

        //获取订单支付信息
        ToPayInfo orderPayInfo = new ToPayInfo();
        orderPayInfo.setGoodName("测试");
        orderPayInfo.setPayAmount(1);

        //获取支付参数
        Map<String, String> extMap = PayUtils.getPaySource(payType, appId);
        String thdAppId = extMap.get("appId");
        extMap.put("code", code);
        extMap.put("ipAddress", ipAddress);

        //生成新的支付流水
        PayFlowBean flowBean = payFlow.buildPayFlow(appId, thdAppId, bizId, bizType, orderPayInfo.getPayAmount());
        flowBean.setPayType(payType);
        //保存流水信息
        payFlow.addPayFlow(flowBean);

        StringBuilder returnUrl = new StringBuilder();
        returnUrl.append(PayParam.homeUrl);
        extMap.put("returnUrl", returnUrl.toString());

        logger.info("开始发起WEB第三方支付");
        IPayWebService payService = PayUtils.getWebPayInstance(payType);
        PayInfo payInfo = payService.buildPayInfo(flowBean, extMap);
        payInfo.setBizId(bizId);
        return payInfo;
    }

    /**
     * @param tmpFlowId
     * @return void
     * @throws
     * @Description: 获取退款信息
     */
    @Override
    public List<PayInfo> getRefundInfo(int payType, String tmpFlowId) throws Exception {
        long flowId = 0L;
        if (StringUtils.isNotBlank(tmpFlowId)) {
            flowId = Long.parseLong(tmpFlowId);
        }
        //根据流水号查询
        List<PayFlowBean> flowBeanList = payFlow.getPayFlowList(flowId, PayConstant.PAY_SUCCESS);
        List<PayInfo> payInfoList = new ArrayList<>();
        for (PayFlowBean tmpBean : flowBeanList) {
            PayInfo payInfo = new PayInfo();
            payInfo.setFlowId(tmpBean.getFlowId());
            payInfo.setPayType(tmpBean.getPayType());
            payInfo.setPayAmount(Double.parseDouble(tmpBean.getPayAmount().toString()));
            payInfoList.add(payInfo);
        }
        return payInfoList;
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
        IPayWebService payWebService = PayUtils.getWebPayInstance(payType);
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
                refundResult = (RefundResult) payWebService.refund(tmpList, extMap);
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
            retObj = payWebService.refund(flowBeanList, extMap);
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
}
