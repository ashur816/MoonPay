package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.bean.PayInfo;
import com.martin.bean.PayResult;
import com.martin.bean.ToPayInfo;
import com.martin.constant.PayChannelEnum;
import com.martin.constant.PayConstant;
import com.martin.exception.BusinessException;
import com.martin.service.IPayCenter;
import com.martin.service.IPayFlow;
import com.martin.service.IPayService;
import com.martin.utils.JsonUtils;
import com.martin.utils.ServiceContainer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
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

        PayInfo payInfo = new PayInfo(orderPayInfo.getGoodName(), orderPayInfo.getPayAmount() / 100.0, "");
        payInfo.setBizId(bizId);
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
        PayFlowBean flowBean = createFlowInfo(payType, orderPayInfo, PayConstant.PAY_NOT);

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
    public PayInfo doWebPay(String payType, String bizId, String ipAddress, String code) throws Exception {
        if (StringUtils.isBlank(bizId) || StringUtils.isBlank(payType) || StringUtils.isBlank(ipAddress)) {
            throw new BusinessException("111");
        }

        logger.info("网页支付bizId-{}", bizId);
        //1、获取业务对象
        ToPayInfo orderPayInfo = getOrderInfo(bizId);//order service.getOrder
        //2、判断支付状态

        //3、生成支付流水
        PayFlowBean flowBean = createFlowInfo(payType, orderPayInfo, PayConstant.PAY_NOT);

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

        if (StringUtils.isEmpty(payService)) {
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
    private PayFlowBean createFlowInfo(String payType, ToPayInfo payInfo, int payState) throws Exception {
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
