package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.dto.TransferResult;
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
     * @return String
     * @throws
     * @Description: 第三方回调转发
     */
    @Override
    public void doNotify(String notifyType, int payType, String ipAddress, Map<String, String> reqParam) throws Exception {
        if (StringUtils.isEmpty(notifyType)) {
            //通知类型不能为空
            throw new BusinessException("09028");
        }

        IPayCommonService payCommonService = PayUtils.getCommonPayInstance(payType);
        if (PayConstant.NOTICE_WEB_PAY.equals(notifyType)) {//WEB支付
            payWebCenter.doPayNotify(payType, ipAddress, reqParam);
        } else if (PayConstant.NOTICE_APP_PAY.equals(notifyType)) {//APP支付
            payAppCenter.doPayNotify(payType, ipAddress, reqParam);
        } else if (PayConstant.NOTICE_WEB_REFUND.equals(notifyType)) {//WEB退款
            payWebCenter.doRefundNotify(payType, ipAddress, reqParam);
        } else if (PayConstant.NOTICE_APP_REFUND.equals(notifyType)) {//APP退款
            payAppCenter.doRefundNotify(payType, ipAddress, reqParam);
        } else if (PayConstant.NOTICE_TRANSFER.equals(notifyType)) {//企业付款
            transferNotify(payCommonService, reqParam);
        } else {
            //回调通知类型错误
            throw new BusinessException("09028");
        }
    }


    /*********************************** 提现 + 提现回调(企业付款接口提交) ******************************************/
    /**
     * @param flowIdList
     * @return
     * @throws
     * @Description: 企业付款（供运营平台使用）
     */
    @Override
    public Object doTransfer(Integer payType, List<Long> flowIdList, String ipAddress) throws Exception {
        if (flowIdList == null || 0 >= flowIdList.size()) {
            throw new BusinessException("111");
        }
        //根据流水号查未付款流水
        List<PayFlowBean> flowBeanList = null;//payFlow.getPayFlowById(flowIdList, PayConstant.PAY_NOT);
        if (flowBeanList == null || 0 >= flowBeanList.size()) {
            //未查询到支付流水信息
            throw new BusinessException("09026");
        }

        Map<String, String> extMap = new HashMap<>();
        extMap.put("transferReason", "爱学派提现");
        extMap.put("ipAddress", ipAddress);
        //同一批必须是同一个支付渠道的
        IPayCommonService payCommonService = PayUtils.getCommonPayInstance(payType);

        Object retObj;
        //微信是同步返回  单笔支付 是安全证书校验 不需要输密码
        if (PayConstant.PAY_TYPE_TEN == payType) {
            for (PayFlowBean flowBean : flowBeanList) {

                //查询用户openId
                extMap.put("openId", "");
                extMap.put("payeeName", "");

                ArrayList<PayFlowBean> tmpList = new ArrayList<>();
                tmpList.add(flowBean);

                //发起单笔支付
                PayResult payResult = (PayResult) payCommonService.transfer(tmpList, extMap);
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
                    throw new BusinessException("09096", payResult.getFailDesc());
                } else {
                    logger.error("微信企业付款失败，原因未知");
                    //操作失败
                    throw new BusinessException("09096", "原因未知");
                }
            }
            retObj = "操作成功";
        } else if (PayConstant.PAY_TYPE_ALI == payType) {//支付宝是异步返回 多笔批量 要在前台输密码
            String batchNo = RandomUtils.getPaymentNo();
            extMap.put("batchNo", batchNo);
            retObj = payCommonService.transfer(flowBeanList, extMap);
        } else {
            //第三方支付类型未定义
            throw new BusinessException("09506");
        }
        return retObj;
    }

    /**
     * @param flowIdList
     * @param refundReason
     * @return void
     * @throws
     * @Description:
     */
    @Override
    public PayInfo doRefund(List<String> flowIdList, String refundReason) throws Exception {
        return null;
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
                throw new BusinessException("09026");
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

}
