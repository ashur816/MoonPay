package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.dto.ToPayInfo;
import com.martin.exception.BusinessException;
import com.martin.service.IPayFlow;
import com.martin.service.IPayWebCenter;
import com.martin.service.IPayWebService;
import com.martin.utils.PayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
            throw new BusinessException("111");
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
                            //订单已经支付，系统正在处理中，请勿重复支付
                            throw new BusinessException("09018");
                        }
                    } else {
                        //该订单已支付
                        throw new BusinessException("09031");
                    }
                }
            }
        }

        //获取订单支付信息
        ToPayInfo orderPayInfo = new ToPayInfo();
        orderPayInfo.setBizId("10000000");
        orderPayInfo.setBizType(1);
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

        logger.info("开始发起第三方支付");
        IPayWebService payService = PayUtils.getWebPayInstance(payType);
        PayInfo payInfo = payService.buildPayInfo(flowBean, extMap);
        payInfo.setBizId(bizId);
        return payInfo;
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
