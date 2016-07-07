package com.martin.service.impl;

import com.martin.bean.*;
import com.martin.constant.PayChannelEnum;
import com.martin.constant.PayConstant;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //1、获取业务对象
        ToPayInfo orderPayInfo = getOrderInfo(bizId);//order service.getOrder
        //2、获取代金券信息
        List<VoucherBean> voucherList = voucher.getVoucherByUser(1001L, 1, 1);

        PayInfo payInfo = new PayInfo(orderPayInfo.getGoodName(), orderPayInfo.getPayAmount() / 100.0, "");
        payInfo.setBizId(bizId);
        payInfo.setVoucherList(voucherList);
        return payInfo;
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
    public PayInfo doScanPay(String payType, String bizId, String ipAddress, String code) throws Exception {
        if (StringUtils.isBlank(bizId) || StringUtils.isBlank(payType) || StringUtils.isBlank(ipAddress)) {
            throw new BusinessException("111");
        }
        logger.info("扫码支付bizId-{}", bizId);
        //1、获取业务对象
        ToPayInfo orderPayInfo = getOrderInfo(bizId);//order service.getOrder
        //2、判断支付状态

        //3、生成支付流水
        PayFlowBean flowBean = buildFlowInfo(payType, orderPayInfo, PayConstant.PAY_NOT);

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
            throw new BusinessException("111");
        }

        IPayService payService = getPayInstance(payType);
        return payService.authorize(bizId);
    }

    /**
     * @Description: web支付入口
     * @param  payType  支付渠道 支付宝/微信等
     * @param  bizId    业务id ： 订单id等
     * @param ipAddress

     *@param code @return String
     * @throws
     */
    @Override
    public PayInfo doWebPay(String payType, String bizId, String ipAddress, String code, String voucherId) throws Exception {
        if (StringUtils.isBlank(bizId) || StringUtils.isBlank(payType) || StringUtils.isBlank(ipAddress)) {
            throw new BusinessException("111");
        }

        logger.info("网页支付bizId-{}", bizId);
        //1、获取业务对象
        ToPayInfo orderPayInfo = getOrderInfo(bizId);//order service.getOrder
        //2、判断支付状态

        if (!StringUtils.isBlank(voucherId)) {
            //3、获取代金券
            VoucherBean voucherBean = voucher.selectVoucherById(Long.parseLong(voucherId), 1);
            if (voucherBean != null) {
                orderPayInfo.setPayAmount(orderPayInfo.getPayAmount() - voucherBean.getVoucherValue());
            }
        }

        //4、生成支付流水
        PayFlowBean flowBean = buildFlowInfo(payType, orderPayInfo, PayConstant.PAY_NOT);

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
            throw new BusinessException("通知类型不能为空");
        }

        if (StringUtils.isBlank(payType)) {
            throw new BusinessException("支付方式不能为空");
        }

        //校验返回参数
        IPayService payService = getPayInstance(payType);
        PayResult payResult = payService.returnValidate(notifyType, reqParam);

        //根据流水号查流水
        PayFlowBean flowBean = payFlow.getPayFlowById(payResult.getFlowId(), -1);
        if (flowBean != null) {
            if (PayConstant.NOTICE_REFUND.equals(notifyType)) {//退款
                flowBean.setThdRefundId(payResult.getThdFlowId());
            } else {
                int payState = flowBean.getPayState();
                long flowId = flowBean.getFlowId();
                int callbackState = payResult.getPayState();
                String tmpFlowId = payResult.getThdFlowId();
                String thdFlowId = flowBean.getThdFlowId();

                if (!tmpFlowId.equals(thdFlowId) && !StringUtils.isBlank(thdFlowId) && thdFlowId.contains("wx")) {//存在重复支付但是第三方流水号不一样的
                    logger.error("存在重复支付-flowId={}", flowId);
                }

                logger.info("flowId={},payState={},callbackState={}", flowId, payState, callbackState);
                if (PayConstant.PAY_UN_BACK == payState || PayConstant.PAY_NOT == payState) {//已发支付，待回调 或者 未支付
                    if (PayConstant.PAY_UN_BACK == callbackState) {//回调支付成功,等待业务处理
                        //状态改成待业务处理
                        flowBean.setPayState(PayConstant.PAY_UN_BIZ);
                        flowBean.setThdFlowId(thdFlowId);
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
        } else {//查不到记录
            throw new BusinessException("未查询到支付流水信息");
        }
    }

    /**
     * @Description: 退款
     * @return void
     * @throws
     * @param flowId
     * @param refundReason
     */
    @Override
    public PayResult doRefund(Long flowId, String refundReason) throws Exception {
        if (0 >= flowId || StringUtils.isBlank(refundReason)) {
            throw new BusinessException("收银台流水和退款原因不能为空");
        }
        //根据流水号查流水
        PayFlowBean flowBean = payFlow.getPayFlowById(flowId, PayConstant.PAY_SUCCESS);
        if (flowBean == null) {
            throw new BusinessException("未查询到支付信息");
        }

        String refundId = String.valueOf(RandomUtils.getPaymentNo());
        Map<String, String> extMap = new HashMap<>();
        extMap.put("refundId", refundId);
        extMap.put("refundReason", refundReason);

        IPayService payService = getPayInstance(flowBean.getPayType());
        PayResult payResult = payService.refund(flowBean, extMap);
        return payResult;
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
            throw new BusinessException("111");
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
     *@param bizId @return
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
            throw new BusinessException("09030");

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
        PayFlowBean payFlowBean = payFlow.getPayFlowByBiz(payInfo.getBizId());
        if (payFlowBean == null) {
            PayFlowBean tmpBean = new PayFlowBean();
            BeanUtils.copyProperties(payInfo, tmpBean);
            //支付状态
            tmpBean.setPayState(payState);
            tmpBean.setPayType(payType);
            //新增
            payFlowBean = payFlow.addPayFlow(tmpBean);
        } else {
            payFlowBean.setPayType(payType);
            payFlowBean.setPayAmount(payInfo.getPayAmount());
            //更新
            payFlowBean = payFlow.updPayFlow(payFlowBean);
        }
        return payFlowBean;
    }

    private ToPayInfo getOrderInfo(String bizId) throws Exception {
        String json = JsonUtils.readJsonByFile("D:\\space-private\\MoonPay\\src\\main\\resources\\OrderPayInfo.json");
        return JsonUtils.readValue(json, ToPayInfo.class);
    }
}
