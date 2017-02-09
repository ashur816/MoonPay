package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.dto.RefundResult;
import com.martin.dto.ToPayInfo;
import com.martin.exception.BusinessException;
import com.martin.service.IPayCommonCenter;
import com.martin.service.IPayFlow;
import com.martin.service.IPayWebCenter;
import com.martin.service.IPayWebService;
import com.martin.utils.PayUtils;
import com.martin.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private IPayCommonCenter payCommonCenter;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

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
                            tmpBean.setThdFlowId(payResult.getThdFlowId());
                            tmpBean.setPayTime(new Date());
                            //业务回调处理
                            try {
                                payCommonCenter.doNotifyBusiness(bizId, bizType, tmpBean.getPayAmount());
                                //全部执行成功，即为支付成功
                                tmpBean.setPayState(PayConstant.PAY_SUCCESS);
                            } catch (Exception e) {
                                if (e instanceof BusinessException) {//业务异常
                                    //业务处理异常
                                    tmpBean.setPayState(PayConstant.PAY_ERROR_BIZ);
                                    BusinessException be = (BusinessException) e;
                                    String errorMsg = be.getMessage();
                                    logger.info("WEB支付回调异常:{}", errorMsg);
                                    //记录异常
                                    tmpBean.setFailDesc(errorMsg);
                                } else {
                                    //业务处理异常
                                    tmpBean.setPayState(PayConstant.PAY_ERROR_BIZ);
                                    tmpBean.setFailDesc("代码报错:" + e.getMessage());
                                }
                            }

                            //更新支付流水
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

        //获取要支付的信息
        ToPayInfo orderPayInfo = payCommonCenter.getToPayInfo(bizId, bizType);

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
        //解析返回
        logger.info("WEB解析支付回调");
        IPayWebService payWebService = PayUtils.getWebPayInstance(payType);
        PayResult payResult = payWebService.payReturn(reqParam);
        long flowId;
        if (payResult != null) {
            flowId = payResult.getFlowId();
        } else {
            throw new BusinessException("WEB回调解析失败");
        }

        //查支付流水 只查有效记录，无效记录不会出现回调
        PayFlowBean flowBean = payFlow.getPayFlowById(flowId, PayConstant.ALL_PAY_STATE);
        if (flowBean == null) {
            throw new BusinessException("未查询到支付流水信息");
        }

        //业务类型 抢单支付 1，提现 2，预订单 3
        int payState = flowBean.getPayState();
        int callbackState = payResult.getPayState();

        logger.info("flowId={},payState={},callbackState={}", flowId, payState, callbackState);

        if (PayConstant.PAY_UN_BACK == payState || PayConstant.PAY_NOT == payState || PayConstant.PAY_ERROR_BIZ == payState) {//已发支付，待回调 或者 未支付 或者 支付成功，业务处理失败的
            //防并发，保证回调业务不会并发
//            String existKey = getAndSet(RedisKeyEnum.PAY_KEY_NOTIFYING.getKey() + flowId, String.valueOf(flowId), 2L);
//            if (StringUtils.isEmpty(existKey)) {
            logger.info("进入WEB支付回调处理");

            if (PayConstant.PAY_SUCCESS == callbackState || PayConstant.PAY_UN_BACK == callbackState) {//第三方交易成功的
                String bizId = flowBean.getBizId();
                int bizType = flowBean.getBizType();

                //状态改成待业务处理
                flowBean.setPayState(PayConstant.PAY_UN_BIZ);
                //支付时间
                flowBean.setPayTime(new Date());
                flowBean.setThdFlowId(payResult.getThdFlowId());

                //业务回调处理
                try {
                    payCommonCenter.doNotifyBusiness(bizId, bizType, flowBean.getPayAmount());
                    //全部执行成功，即为支付成功
                    flowBean.setPayState(PayConstant.PAY_SUCCESS);
                } catch (Exception e) {
                    if (e instanceof BusinessException) {//业务异常
                        //业务处理异常
                        flowBean.setPayState(PayConstant.PAY_ERROR_BIZ);
                        BusinessException be = (BusinessException) e;
                        String errorMsg = be.getMessage();
                        logger.info("WEB支付回调异常:{}", errorMsg);
                        //记录异常
                        flowBean.setFailDesc(errorMsg);
                    } else {
                        //业务处理异常
                        flowBean.setPayState(PayConstant.PAY_ERROR_BIZ);
                        flowBean.setFailDesc("代码报错:" + e.getMessage());
                    }
                }
            } else if (PayConstant.PAY_FAIL == callbackState) {
                logger.info("回调WEB支付失败");
                flowBean.setFailCode(payResult.getFailCode());
                flowBean.setFailDesc(payResult.getFailDesc());
                flowBean.setPayState(PayConstant.PAY_FAIL);
                //支付失败，数据作废
                flowBean.setState(PayConstant.STATE_0);
            } else {
                //其余的直接退出
                return;
            }

            //更新交易流水
            payFlow.updPayFlow(flowBean);
        } else {
            logger.error("WEB支付重复回调被拦截 flowId-{}", flowId);
        }
//        } else {
//            //忽略，不处理
//        }
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
        //解析返回
        logger.info("WEB解析退款回调");
        IPayWebService payWebService = PayUtils.getWebPayInstance(payType);
        List<RefundResult> refundResults = payWebService.refundReturn(reqParam);
        RefundResult refundResult;
        int payState;
        int callbackState;
        long flowId;
        for (int i = 0; i < refundResults.size(); i++) {
            refundResult = refundResults.get(i);
            callbackState = refundResult.getPayState();
            if (callbackState == PayConstant.REFUND_FAIL) {//失败的 签名失败 参数格式校验错误等
                //不处理
                continue;
            } else {
                flowId = refundResult.getFlowId();
                PayFlowBean flowBean = payFlow.getPayFlowById(flowId, -1);
                if (flowBean != null) {
                    payState = flowBean.getPayState();
                    callbackState = refundResult.getPayState();
                    logger.info("退款回调参数 flowId-{},payState-{},callbackState-{}", flowId, payState, callbackState);
                    if (PayConstant.PAY_SUCCESS == payState || PayConstant.REFUND_ING == payState || PayConstant.REFUND_FAIL == payState) {//支付成功、退款中、退款失败的 才能继续退款
                        //支付状态
                        flowBean.setPayState(callbackState);
                        if (PayConstant.REFUND_SUCCESS == callbackState) {
                            Date now = new Date();
                            //退款单号
                            flowBean.setThdRefundId(refundResult.getThdRefundId());
                            //退款时间
                            flowBean.setRefundTime(now);
                            flowBean.setPayState(callbackState);

                            //业务处理
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
                    throw new BusinessException("未查询到支付流水信息");
                }
            }
        }
    }

    /**
     * redis 原子操作
     *
     * @param key
     * @param value
     * @return
     */
    private String getAndSet(final String key, final String value, final long expireSeconds) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] result = connection.getSet(redisTemplate.getStringSerializer().serialize(key), redisTemplate.getStringSerializer().serialize(value));
                redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
                if (result != null) {
                    try {
                        return new String(result, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error("getAndSet is error", e);
                        return "getAndSet";
                    }
                }
                return null;
            }
        });
    }
}
