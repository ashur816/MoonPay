package com.martin.service.impl;

import com.martin.bean.*;
import com.martin.constant.PayChannelEnum;
import com.martin.constant.PayConstant;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.dto.RefundResult;
import com.martin.dto.ToPayInfo;
import com.martin.exception.BusinessException;
import com.martin.service.IPayCenter;
import com.martin.service.IPayFlow;
import com.martin.service.IPayService;
import com.martin.service.IVoucher;
import com.martin.utils.JsonUtils;
import com.martin.utils.RandomUtils;
import com.martin.utils.ServiceContainer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName: PayCenter
 * @Description: 支付中心实现类
 * @author ZXY
 * @date 2016/6/16 13:25
 */
@Service("payCenter")
public class PayCenter implements IPayCenter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IPayFlow payFlow;

    @Resource
    private IVoucher voucher;

    /**
     * @Description: 获取基础订单信息
     * @param  bizId    业务id ： 订单id等
     * @return PayInfo
     * @throws
     */
    @Override
    public PayInfo getPayInfo(String bizId) throws Exception {
        //根据bizId查流水
        PayFlowBean flowBean = payFlow.getPayFlowByBiz(bizId);
        if (flowBean != null) {
            //判断支付状态
            int payState = flowBean.getPayState();
            if (PayConstant.PAY_SUCCESS == payState) {//除了未支付，其他都是已支付
                throw new BusinessException(null, "该订单已经支付");
            }
        }
        ToPayInfo orderPayInfo = getOrderInfo(bizId);//order service.getOrder
        //获取代金券信息
        List<VoucherBean> voucherList = voucher.getVoucherByUser(1001L, 1, 1);

        PayInfo payInfo = new PayInfo(orderPayInfo.getGoodName(), orderPayInfo.getPayAmount() / 100.0, "");
        payInfo.setBizId(bizId);
        payInfo.setVoucherList(voucherList);
        return payInfo;
    }

    /**
     * @Description: 获取退款信息
     * @param  flowIdList   收银台流水号
     * @return PayInfo
     * @throws
     */
    @Override
    public List<PayInfo> getRefundInfo(List<String> flowIdList) throws Exception {
        //根据flowId查流水
        List<PayFlowBean> flowBeanList = payFlow.getPayFlowByIdList(flowIdList, PayConstant.PAY_SUCCESS);
        if (flowBeanList == null || flowBeanList.size() <= 0) {
            throw new BusinessException(null, "未查询到支付成功的订单");
        }
        PayInfo payInfo = null;
        List<PayInfo> infoList = new ArrayList<>();
        PayFlowBean flowBean = null;
        for (int i = 0; i < flowBeanList.size(); i++) {
            flowBean = flowBeanList.get(i);
            payInfo = new PayInfo();
            payInfo.setFlowId(flowBean.getFlowId());
            payInfo.setBizId(flowBean.getBizId());
            payInfo.setPayType(flowBean.getPayType());
            payInfo.setGoodName("XXXX");
            payInfo.setPayAmount(flowBean.getPayAmount() / 100.0);
            infoList.add(payInfo);
        }
        return infoList;
    }

    /**
     * @Description: 支付入口  支付时，为防止多人扫码支付，要锁定记录，可以采用每一次被扫码，就记redis信息，取消付款或失败，删除redis信息
     * @param  payType  支付渠道 支付宝/微信等
     * @param  bizId    业务id ： 订单id等
     * @param ipAddress
     * @param code
     * @return String
     * @throws
     */
    @Override
    public PayInfo doPay(String payType, String bizId, String ipAddress, String code, String voucherId) throws Exception {
        if (StringUtils.isBlank(bizId) || StringUtils.isBlank(payType) || StringUtils.isBlank(ipAddress)) {
            throw new BusinessException(null, "参数不能为空");
        }
        logger.info("扫码支付bizId-{}", bizId);
        //根据bizId查流水
        PayFlowBean flowBean = payFlow.getPayFlowByBiz(bizId);
        if (flowBean == null) {//未查到流水，说明还未支付
            ToPayInfo orderPayInfo = getOrderInfo(bizId);//order service.getOrder
            flowBean = buildFlowInfo(payType, orderPayInfo, PayConstant.PAY_NOT);
        } else {
            //判断支付状态
            int payState = flowBean.getPayState();
            if (PayConstant.PAY_SUCCESS == payState) {//已支付
                throw new BusinessException(null, "该订单已经支付");
            }
        }

        Map<String, String> extMap = new HashMap<>();
        extMap.put("code", code);
        extMap.put("ipAddress", ipAddress);

        logger.info("开始发起第三方支付");
        IPayService payService = getPayInstance(payType);
        PayInfo payInfo = payService.buildPayInfo(flowBean, extMap);
        payInfo.setFlowId(flowBean.getFlowId());
        payInfo.setBizId(flowBean.getBizId());
        return payInfo;
    }

    /**
     * @Description: 授权信息
     * @param  payType  支付渠道 支付宝/微信等
     * @param  bizId    业务id ： 订单id等
     * @return PayInfo
     * @throws
     */
    @Override
    public PayInfo doAuthorize(String payType, String bizId) throws Exception {
        if (StringUtils.isBlank(bizId) || StringUtils.isBlank(payType)) {
            throw new BusinessException(null, "参数不能为空");
        }

        IPayService payService = getPayInstance(payType);
        return payService.authorize(bizId);
    }

    /**
     * @Description: 第三方回调
     * @return void
     * @throws
     * @param notifyType
     * @param payType
     * @param ipAddress
     * @param reqParam
     */
    @Override
    public void doNotify(String notifyType, String payType, String ipAddress, Map<String, String> reqParam) throws Exception {
        if (StringUtils.isBlank(notifyType)) {
            throw new BusinessException(null, "通知类型不能为空");
        }

        if (StringUtils.isBlank(payType)) {
            throw new BusinessException(null, "支付方式不能为空");
        }

        //校验返回参数
        IPayService payService = getPayInstance(payType);
        if (PayConstant.NOTICE_NOTIFY.equals(notifyType)) {//支付异步回调
            payNotify(payService, reqParam);
        } else if (PayConstant.NOTICE_RETURN.equals(notifyType)) {//支付同步回调
            returnNotify(payService, reqParam);
        } else if (PayConstant.NOTICE_REFUND.equals(notifyType)) {//退款回调
            refundNotify(payService, reqParam);
        } else {

        }
    }

    /**
     * @Description: 退款/批量退款的payType一定要一样
     * @return void
     * @throws
     * @param flowIdList
     * @param refundReason
     */
    @Override
    public Object doRefund(List<String> flowIdList, String refundReason) throws Exception {
        if (flowIdList == null || 0 >= flowIdList.size() || StringUtils.isBlank(refundReason)) {
            throw new BusinessException(null, "收银台流水和退款原因不能为空");
        }
        //根据流水号查流水
        List<PayFlowBean> flowBeanList = payFlow.getPayFlowByIdList(flowIdList, PayConstant.PAY_SUCCESS);
        if (flowBeanList == null) {
            throw new BusinessException(null, "未查询到支付信息");
        }

        Map<String, String> extMap = new HashMap<>();
        extMap.put("refundReason", refundReason);

        String payType = flowBeanList.get(0).getPayType();
        IPayService payService = getPayInstance(payType);

        Object retObj;
        //微信是同步返回  支付宝是异步返回
        if (PayChannelEnum.TEN_PAY.getPayType().equals(payType)) {
            //更新退款流水
            RefundResult refundResult;
            PayFlowBean flowBean;
            String refundId;
            for (int i = 0; i < flowBeanList.size(); i++) {
                refundId = RandomUtils.getPaymentNo();
                extMap.put("refundId", refundId);
                refundResult = (RefundResult) payService.refund(flowBeanList, extMap);
                if(refundResult != null){
                    flowBean = flowBeanList.get(i);
                    flowBean.setRefundId(Long.parseLong(refundId));
                    flowBean.setRefundReason(refundReason);
                    flowBean.setPayState(PayConstant.REFUND_SUCCESS);
                    flowBean.setThdRefundId(refundResult.getThdFlowId());
                    flowBean.setRefundTime(new Date());
                    payFlow.updPayFlow(flowBean);
                }
            }
            retObj = "退款成功";
        } else {
            extMap.put("refundId", RandomUtils.getPaymentNo());
            retObj = payService.refund(flowBeanList, extMap);
        }
        return retObj;
    }

    /**
     * @Description: 企业付款
     * @return void
     * @throws
     * @param payType
     * @param ipAddress
     */
    @Override
    public PayResult doWithdraw(Long acctId, String payType, Integer drawAmount, String ipAddress) throws Exception {
        if (0 >= acctId || StringUtils.isBlank(payType) || 0 >= drawAmount || StringUtils.isBlank(ipAddress)) {
            throw new BusinessException(null, "参数不能为空");
        }

        logger.info("用户-{},支付类型-{},ip地址-{}", acctId, payType, ipAddress);
        ToPayInfo toPayInfo = new ToPayInfo();
        toPayInfo.setPayAmount(drawAmount);
        toPayInfo.setTotalAmount(drawAmount);
        toPayInfo.setBizType(PayConstant.BIZ_TYPE_WITHDRAW);

        //生成支付流水
        PayFlowBean flowBean = buildFlowInfo(payType, toPayInfo, PayConstant.PAY_NOT);

        //根据acctId查绑定的openId
        String openId = "o6U0tuJA8ewtDsfhmR2rh4-7yDco";

        Map<String, String> extMap = new HashMap<>();
        extMap.put("openId", openId);
        extMap.put("ipAddress", ipAddress);

        logger.info("开始发起企业付款");
        IPayService payService = getPayInstance(payType);
        PayResult payResult = payService.withdraw(flowBean, extMap);
        payResult.setFlowId(flowBean.getFlowId());
        return payResult;
    }

    /**
     * @Description: 查询第三方支付结果
     * @param payType
     * @param bizId
     * @return
     * @throws
     */
    @Override
    public PayResult doQueryState(String payType, String bizId) throws Exception {
        return null;
    }

    /**
     * @Description: 动态选择服务实例
     * @param payType 第三方类型
     * @return
     * @throws
     */
    private IPayService getPayInstance(String payType) {
        //根据渠道不同，调用不同实现类
        PayChannelEnum payChannel = PayChannelEnum.getPayChannel(payType);
        String payService = payChannel.getPayService();

        if (StringUtils.isBlank(payService)) {
            //暂不支持当前支付方式
            throw new BusinessException(null, "暂不支持当前支付方式");

        }
        //返回服务实例
        return new ServiceContainer<IPayService>().get(payService);
    }

    /**
     * @Description: 生成支付流水
     * @param payInfo
     * @return PayFlowInfo
     * @throws
     */
    private PayFlowBean buildFlowInfo(String payType, ToPayInfo payInfo, int payState) throws Exception {
        PayFlowBean tmpBean = new PayFlowBean();
        BeanUtils.copyProperties(payInfo, tmpBean);
        //支付状态
        tmpBean.setPayState(payState);
        tmpBean.setPayType(payType);
        //新增
        PayFlowBean payFlowBean = payFlow.addPayFlow(tmpBean);
        return payFlowBean;
    }

    private ToPayInfo getOrderInfo(String bizId) throws Exception {
        String json = JsonUtils.readJsonByFile("D:\\space-private\\MoonPay\\src\\main\\resources\\OrderPayInfo.json");
        ToPayInfo toPayInfo = JsonUtils.readValue(json, ToPayInfo.class);
        toPayInfo.setBizId(bizId);
        return toPayInfo;
    }

    /**
     * @Description: 支付回调处理
     * @param
     * @return
     * @throws
     */
    private void payNotify(IPayService payService, Map<String, String> reqParam) throws Exception {
        //解析返回
        PayResult payResult = payService.payReturn(reqParam);
        //根据流水号查流水
        PayFlowBean flowBean = payFlow.getPayFlowById(payResult.getFlowId(), PayConstant.ALL_PAY_STATE);
        if (flowBean == null) {
            throw new BusinessException("", "未查询到支付流水");
        }

        int payState = flowBean.getPayState();
        long flowId = flowBean.getFlowId();
        int callbackState = payResult.getPayState();
        String retThdFlowId = payResult.getThdFlowId();
        String thdFlowId = flowBean.getThdFlowId();

        if (!retThdFlowId.equals(thdFlowId) && !StringUtils.isBlank(thdFlowId) && thdFlowId.contains("wx")) {//存在重复支付但是第三方流水号不一样的
            logger.error("存在重复支付-flowId={}", flowId);
        }

        logger.info("flowId={},payState={},callbackState={}", flowId, payState, callbackState);
        if (PayConstant.PAY_UN_BACK == payState || PayConstant.PAY_NOT == payState) {//已发支付，待回调 或者 未支付
            if (PayConstant.PAY_UN_BACK == callbackState) {//回调支付成功,等待业务处理
                //状态改成待业务处理
                flowBean.setPayState(PayConstant.PAY_SUCCESS);
                //支付时间
                flowBean.setPayTime(new Date());
                flowBean.setThdFlowId(retThdFlowId);
                flowBean.setRandomCode(payResult.getRandomCode());
            } else if (PayConstant.PAY_FAIL == callbackState) {//回调支付失败的
                logger.info("回调支付失败");
                flowBean.setFailCode(payResult.getFailCode());
                flowBean.setFailDesc(payResult.getFailDesc());
                flowBean.setPayState(PayConstant.PAY_FAIL);
                //支付失败，数据作废
                flowBean.setState(PayConstant.STATE_0);
            } else if (PayConstant.PAY_SUCCESS == callbackState) {//订单已经被支付 其实在支付时，就已经被相应的渠道拦截了
                logger.info("订单已经被支付");
                //不处理
            }
            //更新交易流水
            payFlow.updPayFlow(flowBean);
        } else if (PayConstant.PAY_SUCCESS == payState) {//已经支付成功，防止重复回调
            //不处理直接返回
        } else if (PayConstant.PAY_FAIL == payState) {//已经支付失败，防止重复回调
            //不处理直接返回
        }
    }

    /**
     * @Description: 退款回调处理
     * @param
     * @return
     * @throws
     */
    private void refundNotify(IPayService payService, Map<String, String> reqParam) throws Exception {
        //解析返回
        List<RefundResult> refundResults = payService.refundReturn(reqParam);
        RefundResult refundResult;
        int payState;
        int callbackState;
        for (int i = 0; i < refundResults.size(); i++) {
            refundResult = refundResults.get(i);

            PayFlowBean flowBean = payFlow.getPayFlowByThdId(refundResult.getThdFlowId(), PayConstant.ALL_PAY_STATE);
            if (flowBean != null) {
                payState = flowBean.getPayState();
                callbackState = refundResult.getPayState();
                if (PayConstant.PAY_SUCCESS == payState || PayConstant.REFUND_ING == payState || PayConstant.REFUND_FAIL == payState) {//支付成功、退款中、退款失败的 才能继续退款
                    //支付状态
                    flowBean.setPayState(callbackState);
                    if (PayConstant.REFUND_SUCCESS == callbackState) {
                        //退款单号
                        flowBean.setThdRefundId(refundResult.getThdRefundId());
                        //退款时间
                        flowBean.setRefundTime(new Date());
                    } else {
                        flowBean.setFailCode(refundResult.getFailCode());
                        flowBean.setFailDesc(refundResult.getFailDesc());
                    }
                    //更新交易流水
                    payFlow.updPayFlow(flowBean);
                } else {
                    //跳过不管
                }
            } else {
                throw new BusinessException(null, "未查询到支付流水");
            }
        }
    }

    /**
     * @Description: 同步回调处理
     * @param
     * @return
     * @throws
     */
    private void returnNotify(IPayService payService, Map<String, String> reqParam) throws Exception {
        //解析返回
        PayResult payResult = payService.payReturn(reqParam);

        PayFlowBean tmpBean = new PayFlowBean();
        tmpBean.setFlowId(payResult.getFlowId());
        tmpBean.setPayState(PayConstant.PAY_UN_BACK);
        //更新支付流水为支付中
        payFlow.updPayFlow(tmpBean);
    }
}
