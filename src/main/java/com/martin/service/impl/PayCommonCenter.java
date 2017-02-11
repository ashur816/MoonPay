package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.dto.*;
import com.martin.exception.BusinessException;
import com.martin.service.*;
import com.martin.utils.PayUtils;
import com.martin.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ZXY
 * @ClassName: PayCenter
 * @Description: 通用支付中心
 * @date 2016/6/16 13:25
 */
@Service("payCommonCenter")
public class PayCommonCenter implements IPayCommonCenter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IPayFlow payFlow;

    @Resource
    private IPayWebCenter payWebCenter;

    @Resource
    private IPayAppCenter payAppCenter;

    /**
     * @param bizId
     * @param bizType
     * @return void
     * @throws
     * @Description: 获取业务订单信息，根据业务类型不同，访问不同的接口获取信息
     */
    @Override
    public ToPayInfo getToPayInfo(String bizId, int bizType) throws Exception {
        ToPayInfo toPayInfo = new ToPayInfo();
        toPayInfo.setGoodName("测试");
        toPayInfo.setPayAmount(1);
        return toPayInfo;
    }

    /**
     * @return String
     * @throws
     * @Description: 第三方回调转发
     */
    @Override
    public void doNotify(String notifyType, int payType, String ipAddress, Map<String, String> reqParam) throws Exception {
        if (StringUtils.isEmpty(notifyType)) {
            //通知类型不能为空
            throw new BusinessException("通知类型不能为空");
        }

        IPayCommonService payCommonService = PayUtils.getCommonPayInstance(payType);
        if (PayConstant.NOTICE_WEB_PAY.equals(notifyType)) {//WEB支付
            payWebCenter.doPayNotify(payType, ipAddress, reqParam);
        } else if (PayConstant.NOTICE_APP_PAY.equals(notifyType)) {//APP支付
            payAppCenter.doPayNotify(payType, ipAddress, reqParam);
        } else if (PayConstant.NOTICE_REFUND.equals(notifyType)) {//WEB退款
            refundNotify(payType, ipAddress, reqParam);
        } else if (PayConstant.NOTICE_TRANSFER.equals(notifyType)) {//企业付款
            transferNotify(payCommonService, reqParam);
        } else {
            //回调通知类型错误
            throw new BusinessException("回调通知类型错误");
        }
    }

    /**
     * @param bizId
     * @param bizType
     * @param payAmount
     * @return void
     * @throws
     * @Description: 支付回调分发业务处理
     */
    @Override
    public void doNotifyBusiness(String bizId, int bizType, int payAmount) throws Exception {
        String errorFlag = "订单侧";
        if (PayConstant.BIZ_TYPE_EXPRESS == bizType) {

        } else {
            throw new BusinessException(errorFlag + "报错：暂不支持此业务支付类型");
        }
        logger.info("WEB回调执行成功");
    }

    /**
     * @param appId
     * @param payType
     * @param tmpFlowId
     * @return void
     * @throws
     * @Description: 获取退款信息
     */
    @Override
    public List<PayInfo> getRefundInfo(String appId, int payType, String tmpFlowId) throws Exception {
        if (StringUtils.isBlank(tmpFlowId)) {
            tmpFlowId = "0";
        }
        //根据流水号查询
        List<PayFlowBean> flowBeanList = payFlow.getCanRefundList(Long.parseLong(tmpFlowId), PayConstant.PAY_SUCCESS, payType, appId);
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
     * @param flowIdList
     * @param refundReason
     * @return void
     * @throws
     * @Description: 执行退款操作
     */
    @Override
    public Object doRefund(List<String> flowIdList, String refundReason) throws Exception {
        //查询支付流水信息
        List<PayFlowBean> flowBeanList = payFlow.getPayFlowListByIdList(flowIdList, PayConstant.PAY_SUCCESS);
        Object retObj;
        if (flowBeanList != null && flowBeanList.size() > 0) {
            //同一批必须是同一个支付渠道的
            PayFlowBean bean0 = flowBeanList.get(0);
            String clientSource = bean0.getClientSource();
            int payType = bean0.getPayType();

            IPayCommonService payCommonService = PayUtils.getCommonPayInstance(payType);
            Map<String, String> extMap = new HashMap<>();
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
                    refundResult = (RefundResult) payCommonService.refund(clientSource, tmpList, extMap);
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
                retObj = payCommonService.refund(clientSource, flowBeanList, extMap);
            }

        } else {
            //未查询到可退款流水信息
            throw new BusinessException("未查询到可退款流水信息");
        }
        return retObj;
    }


    /*********************************** 提现 + 提现回调(企业付款接口提交) ******************************************/
    /**
     * @return
     * @throws
     * @Description: 企业付款（供运营平台使用）
     */
    @Override
    public Object doTransfer(String thdNo, String thdName, int drawAmount, int payType, String ipAddress) throws Exception {
        //先生成支付流水
        long flowId = Long.parseLong(RandomUtils.getPaymentNo());
        PayFlowBean flowBean = new PayFlowBean();
        flowBean.setFlowId(flowId);
        flowBean.setBizId(thdNo);
        flowBean.setPayType(payType);
        flowBean.setBizType(PayConstant.BIZ_TYPE_WITHDRAW);
        flowBean.setPayAmount(drawAmount);
        flowBean.setCreateTime(new Date());
        payFlow.addPayFlow(flowBean);

        Map<String, String> extMap = new HashMap<>();
        extMap.put("transferReason", "moon提现");
        extMap.put("ipAddress", ipAddress);
        //同一批必须是同一个支付渠道的
        IPayCommonService payCommonService = PayUtils.getCommonPayInstance(payType);

        Object retObj;
        //微信是同步返回  单笔支付 是安全证书校验 不需要输密码
        if (PayConstant.PAY_TYPE_TEN == payType) {

            //查询用户openId
            extMap.put("openId", thdNo);
            extMap.put("payeeName", "张向阳");

            //发起单笔支付
            PayResult payResult = (PayResult) payCommonService.transfer(flowBean, extMap);
            if (payResult != null && "SUCCESS".equalsIgnoreCase(payResult.getTradeState())) {
                //状态改成支付成功
                flowBean.setPayState(PayConstant.PAY_SUCCESS);
                //支付时间
                flowBean.setPayTime(new Date());
                flowBean.setThdFlowId(payResult.getThdFlowId());
                transferSuccessToBiz(flowBean);
            } else if (payResult != null && "FAIL".equalsIgnoreCase(payResult.getTradeState())) {
                flowBean.setFailCode(payResult.getFailCode());
                flowBean.setFailDesc(payResult.getFailDesc());
                //更新相关数据
                transferFailedUpdate(flowBean);
                //操作失败
                throw new BusinessException("操作失败{}", payResult.getFailDesc());
            } else {
                logger.error("微信企业付款失败，原因未知");
                //操作失败
                throw new BusinessException("操作失败{}", "原因未知");
            }
            retObj = "操作成功";
        } else if (PayConstant.PAY_TYPE_ALI == payType) {//支付宝是异步返回 多笔批量 要在前台输密码
            String batchNo = RandomUtils.getPaymentNo();
            extMap.put("thdNo", thdNo);
            extMap.put("thdName", thdName);
            extMap.put("batchNo", batchNo);
            retObj = payCommonService.transfer(flowBean, extMap);
        } else {
            //第三方支付类型未定义
            throw new BusinessException("第三方支付类型未定义");
        }
        return retObj;
    }

    /*********************************** 私有方法 ******************************************/
    /**
     * @return void
     * @throws
     * @Description: 企业付款回调
     */
    private void transferNotify(IPayCommonService payCommonService, Map<String, String> reqParam) throws Exception {
        //解析返回
        List<TransferResult> transferResults = payCommonService.transferReturn(reqParam);

        TransferResult transferResult;
        int payState;
        int callbackState;
        for (int i = 0; i < transferResults.size(); i++) {
            transferResult = transferResults.get(i);

            PayFlowBean flowBean = payFlow.getPayFlowById(transferResult.getFlowId(), -1);
            if (flowBean != null) {
                payState = flowBean.getPayState();
                callbackState = transferResult.getTransferState();
                logger.info("企业付款参数 flowId-{},payState-{},callbackState-{}", flowBean.getFlowId(), payState, callbackState);
                if (PayConstant.PAY_UN_BACK == payState || PayConstant.PAY_NOT == payState || PayConstant.PAY_ERROR_BIZ == payState) {//未支付、已支付未回调、业务处理失败 继续支付
                    //支付状态
                    flowBean.setPayState(callbackState);
                    if (PayConstant.PAY_SUCCESS == callbackState) {
                        Date now = new Date();
                        //转账单号
                        flowBean.setThdFlowId(transferResult.getThdFlowId());
                        //转账时间
                        flowBean.setPayTime(now);
                        //更新相关数据
                        transferSuccessToBiz(flowBean);
                    } else {
                        logger.info("企业付款回调支付失败");
                        flowBean.setFailCode("FAIL");
                        flowBean.setFailDesc(transferResult.getFailDesc());
                        //更新相关数据
                        transferFailedUpdate(flowBean);
                    }
                } else {
                    //其余的直接退出
                }
            } else {
                throw new BusinessException("未查询到支付流水");
            }
        }
    }

    /**
     * @return
     * @throws
     * @Description: 提现成功更新数据
     */
    private void transferSuccessToBiz(PayFlowBean flowBean) throws Exception {
        String flag = "支付侧";
        try {
            logger.info("1-更新账本数据");
            //更新账本数据
            flag = "账户侧";
//            writeBookByExchangeCashService.fileUserBook(flowBean, flowBean.getDetailList());

            logger.info("2-更新提现数据");
            //更新提现数据
            flag = "提现侧";
//            acctExchangeCashService.updateStateById(flowBean.getBizId(), flowBean.getThdFlowId(), 2);

            //全部执行成功，即为支付成功
            flowBean.setPayState(PayConstant.PAY_SUCCESS);
        } catch (Exception e) {
            if (e instanceof BusinessException) {//业务异常
                flowBean.setPayState(PayConstant.PAY_ERROR_BIZ);
                BusinessException be = (BusinessException) e;
                String errorCode = be.getMessageCode();
                logger.info("企业付款回调异常:{}报错{}", flag, errorCode);
                //记录异常
                flowBean.setFailDesc(flag + "业务报错，异常码：[" + errorCode + "]");
            } else {
                flowBean.setPayState(PayConstant.PAY_ERROR_BIZ);
                logger.info(flag + "异常:", e);
                flowBean.setFailDesc(flag + "代码报错");
            }
        }
        //更新交易流水
        payFlow.updPayFlow(flowBean);
    }

    /**
     * @return
     * @throws
     * @Description: 提现失败更新数据
     */
    private void transferFailedUpdate(PayFlowBean flowBean) throws Exception {
        //更新到流水表中
        PayFlowBean tmpBean = new PayFlowBean();
        tmpBean.setFlowId(flowBean.getFlowId());
        tmpBean.setFailCode(flowBean.getFailCode());
        tmpBean.setFailDesc(flowBean.getFailDesc());
        payFlow.updPayFlow(tmpBean);
    }

    /**
     * @return void
     * @throws
     * @Description: 退款回调
     */
    private void refundNotify(int payType, String ipAddress, Map<String, String> reqParam) throws Exception {
        //解析返回
        logger.info("解析退款回调");
        IPayCommonService payCommonService = PayUtils.getCommonPayInstance(payType);
        List<RefundResult> refundResults = payCommonService.refundReturn(reqParam);
        RefundResult refundResult;
        int payState;
        int callbackState;
        String thdFlowId;
        long flowId;
        for (int i = 0; i < refundResults.size(); i++) {
            refundResult = refundResults.get(i);
            callbackState = refundResult.getPayState();
            if (callbackState == PayConstant.REFUND_FAIL) {//失败的 签名失败 参数格式校验错误等
                //不处理
                continue;
            } else {
                thdFlowId = refundResult.getThdFlowId();
                //根据thdFlowId查询出支付流水
                PayFlowBean flowBean = payFlow.getPayFlowByThdFlowId(thdFlowId);
                if (flowBean != null) {
                    flowId = flowBean.getFlowId();
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

}
