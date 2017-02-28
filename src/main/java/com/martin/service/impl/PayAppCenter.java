package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.dto.PayResult;
import com.martin.dto.ToPayInfo;
import com.martin.exception.BusinessException;
import com.martin.service.IPayAppCenter;
import com.martin.service.IPayAppService;
import com.martin.service.IPayCommonCenter;
import com.martin.service.IPayFlow;
import com.martin.utils.PayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZXY
 * @ClassName: PayAppCenter
 * @Description:
 * @date 2017/1/10 14:03
 */
@Service("payAppCenter")
public class PayAppCenter implements IPayAppCenter {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
     * @param payType
     * @param ipAddress
     * @param reqParam
     * @return void
     * @throws
     * @Description: 第三方回调--支付
     */
    @Override
    public void doPayNotify(int payType, String ipAddress, Map<String, String> reqParam) throws Exception {
        //校验返回参数
        IPayAppService payAppService = PayUtils.getAppPayInstance(payType);
        logger.info("APP支付解析参数");
        //先取出flowId确定是哪个sdk支付的
        long flowId = payAppService.getReturnFlowId(reqParam);

        //查支付流水 状态是有效的 =1
        PayFlowBean flowBean = payFlow.getPayFlowById(flowId, PayConstant.ALL_PAY_STATE);
        if (flowBean == null) {
            //未查询到支付流水信息
            throw new BusinessException("09026");
        }

        //获取支付参数
        int thdType = flowBean.getPayType();
        Map<String, String> extMap = PayUtils.getPaySource(thdType, flowBean.getClientSource());

        //解析返回 + 验签
        PayResult payResult = payAppService.payReturn(extMap.get("privateKey"), reqParam);

        //业务类型 抢单支付 1，提现 2
        int payState = flowBean.getPayState();
        int callbackState = payResult.getPayState();

        logger.info("APP支付flowId={},payState={},callbackState={}", flowId, payState, callbackState);
        if (PayConstant.PAY_UN_BACK == payState || PayConstant.PAY_NOT == payState || PayConstant.PAY_ERROR_BIZ == payState) {//已发支付，待回调 或者 未支付 或 支付成功，业务处理失败的
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
                    logger.info("APP支付回调异常:{}", errorMsg);
                    //记录异常
                    flowBean.setFailDesc(errorMsg);
                } else {
                    //业务处理异常
                    flowBean.setPayState(PayConstant.PAY_ERROR_BIZ);
                    flowBean.setFailDesc("代码报错:" + e.getMessage());
                }
            }
        } else if (PayConstant.PAY_FAIL == callbackState) {
            logger.info("回调APP支付失败");
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
