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
 * @author ZXY
 * @ClassName: PayFlowService
 * @Description: 支付流水
 * @date 2016/6/30 17:40
 */
@Service("payFlowService")
public class PayFlowService implements IPayFlow {

    @Resource
    private PayFlowMapper payFlowMapper;

    /**
     * @param bizId
     * @param bizType
     * @return PayFlowBean
     * @throws
     * @Description: 根据 biz_id + biz_type 查是否已有流水
     */
    @Override
    public List<PayFlowBean> getPayFlowListByBiz(String bizId, int bizType) throws Exception {
        return payFlowMapper.getPayFlowListByBiz(bizId, bizType);
    }

    /**
     * @param flowIdList
     * @return
     * @throws
     * @Description: 批量获取支付流水
     */
    @Override
    public List<PayFlowBean> getPayFlowListByIdList(List<String> flowIdList, int payState) throws Exception {
        return payFlowMapper.selectListByIdList(flowIdList, payState);
    }

    /**
     * @param payFlowBean
     * @return
     * @throws
     * @Description: 新增支付流水
     */
    @Override
    public void addPayFlow(PayFlowBean payFlowBean) {
        payFlowMapper.insertSelective(payFlowBean);
    }

    /**
     * @return
     * @throws
     * @Description: 更新支付流水
     */
    @Override
    public void updPayFlow(PayFlowBean payFlowBean) {
        payFlowMapper.updateByPrimaryKeySelective(payFlowBean);
    }

    /**
     * @param flowId
     * @param payState
     * @return
     * @throws
     * @Description: 根据支付状态查询流水
     */
    @Override
    public PayFlowBean getPayFlowById(long flowId, int payState) {
        return payFlowMapper.getPayFlowById(flowId, payState);
    }

    /**
     * @param flowId
     * @param payState
     * @return
     * @throws
     * @Description: 查询支付流水
     */
    @Override
    public List<PayFlowBean> getPayFlowList(long flowId, int payState) {
        return payFlowMapper.getPayFlowList(flowId, payState);
    }

    /**
     * @param clientSource
     * @param paySource
     * @param bizId
     * @param bizType
     * @return
     * @throws
     * @Description: 组装支付流水
     */
    @Override
    public PayFlowBean buildPayFlow(String clientSource, String paySource, String bizId, int bizType, int payAmount) {
        PayFlowBean flowBean = new PayFlowBean();
        flowBean.setFlowId(Long.parseLong(RandomUtils.getPaymentNo()));
        flowBean.setTotalAmount(payAmount);
        flowBean.setPayAmount(payAmount);
        flowBean.setBizId(bizId);
        flowBean.setBizType(bizType);
        flowBean.setClientSource(clientSource);
        flowBean.setPaySource(paySource);
        flowBean.setCreateTime(new Date());
        return flowBean;
    }

    /**
     * @param flowId
     * @param thdFlowId
     * @return Boolean
     * @throws
     * @Description: 更新预付单号
     */
    @Override
    public Boolean updateThdFlowId(long flowId, String thdFlowId) throws Exception {
        int num = payFlowMapper.updateThdFlowId(flowId, thdFlowId);
        if (1 == num) {
            return true;
        }
        return false;
    }

}
