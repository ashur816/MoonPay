package com.martin.service;

import com.martin.bean.PayFlowBean;
import com.martin.bean.PayInfo;
import com.martin.bean.PayResult;
import com.martin.constant.PayConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: ComPay
 * @Description: 通用支付类
 * @author ZXY
 * @date 2016/6/22 15:35
 */
@Service("comPayService")
public class ComPay implements PayService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 生成支付信息
     * @param flowBean
     * @param extMap 额外参数
     * @return
     */
    @Override
    public PayInfo buildPayInfo(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始通用支付");
        //支付总金额
        double payAmount = flowBean.getPayAmount() / 100.0;
        PayInfo payInfo = new PayInfo(PayConstant.BODY, payAmount, "");
        return payInfo;
    }

    /**
     * @Description: 回调参数校验
     * @param paraMap
     * @return
     * @throws
     */
    @Override
    public PayResult returnValidate(Map<String, String> paraMap) throws Exception {
        return null;
    }

    /**
     * 提现
     * @param flowBean
     * @param extMap
     * @return
     */
    @Override
    public void withdraw(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {

    }

    /**
     * 获取第三方支付信息
     * @param flowBean
     * @return
     */
    @Override
    public PayResult getPayInfo(PayFlowBean flowBean) throws Exception {
        return null;
    }

    /**
     * 预授权
     * @param bizId 订单业务id
     * @return
     */
    @Override
    public PayInfo authorize(String bizId) throws Exception {
        return null;
    }
}
