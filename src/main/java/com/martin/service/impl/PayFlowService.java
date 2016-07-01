package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.dao.PayFlowMapper;
import com.martin.service.IPayFlow;
import com.martin.utils.RandomUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @ClassName: PayFlowService
 * @Description: 支付流水
 * @author ZXY
 * @date 2016/6/30 17:40
 */
@Service("payFlowService")
public class PayFlowService implements IPayFlow {

    @Resource
    private PayFlowMapper payFlowMapper;

    /**
     * @Description: 新增支付流水
     * @return
     * @throws
     */
    @Override
    public PayFlowBean addPayFlow(PayFlowBean payFlowBean) {
        //生成流水号
        long flowId = Long.valueOf(RandomUtils.getPaymentNo());
        payFlowBean.setFlowId(flowId);
        payFlowBean.setCreateTime(new Date());
        payFlowMapper.insertSelective(payFlowBean);
        return payFlowBean;
    }

    /**
     * @Description: 更新支付流水
     * @return
     * @throws
     */
    @Override
    public PayFlowBean updPayFlow(PayFlowBean payFlowBean) {
        payFlowMapper.updateByPrimaryKeySelective(payFlowBean);
        return payFlowBean;
    }

    /**
     * @Description: 根据业务查询支付流水
     * @param bizId 订单号
     * @return
     * @throws
     */
    @Override
    public PayFlowBean getPayFlowByBiz(String bizId) {
        return payFlowMapper.selectPayFlowByBiz(bizId);
    }


}
