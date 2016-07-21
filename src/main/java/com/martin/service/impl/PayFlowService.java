package com.martin.service.impl;

import com.martin.bean.PayFlowBean;
import com.martin.dao.PayFlowMapper;
import com.martin.service.IPayFlow;
import com.martin.utils.RandomUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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
        return payFlowMapper.selectByBiz(bizId);
    }

    /**
     * @Description: 根据支付状态查询流水
     * @param flowId
     * @param payState
     * @return
     * @throws
     */
    @Override
    public PayFlowBean getPayFlowById(Long flowId, Integer payState) {
        return payFlowMapper.selectById(flowId, payState);
    }

    /**
     * @Description: 根据第三方支付流水查询支付流水
     * @param thdFlowId
     * @param payState
     * @return
     * @throws
     */
    @Override
    public PayFlowBean getPayFlowByThdId(String thdFlowId, Integer payState) {
        return payFlowMapper.selectByThdId(thdFlowId, payState);
    }

    /**
     * @Description: 批量查询支付流水
     * @param flowIdList
     * @param payState
     * @return
     * @throws
     */
    @Override
    public List<PayFlowBean> getPayFlowByIdList(List<String> flowIdList, Integer payState) {
        return payFlowMapper.selectListById(flowIdList, payState);
    }
}
