package com.martin.service;

import com.martin.bean.PayFlowBean;

import java.util.List;

/**
 * @author ZXY
 * @ClassName: PayFlowService
 * @Description: 支付流水服务
 * @date 2016/6/30 17:27
 */
public interface IPayFlow {

    /**
     * @param
     * @return
     * @throws
     * @Description: 查询支付流水
     */
    PayFlowBean getPayFlowById(long flowId, int payState);

    /**
     * @param
     * @return
     * @throws
     * @Description: 查询支付流水
     */
    List<PayFlowBean> getPayFlowList(long flowId, int payState);

    /**
     * @param bizId
     * @param bizType
     * @return PayFlowBean
     * @throws
     * @Description: 根据 biz_id + biz_type 查是否已有流水
     */
    List<PayFlowBean> getPayFlowListByBiz(String bizId, int bizType) throws Exception;

    /**
     * @param flowIdList
     * @return
     * @throws
     * @Description: 批量获取支付流水
     */
    List<PayFlowBean> getPayFlowListByIdList(List<String> flowIdList, int payState) throws Exception;

    /**
     * @param
     * @return
     * @throws
     * @Description: 新增支付流水
     */
    void addPayFlow(PayFlowBean payFlowBean);

    /**
     * @param
     * @return
     * @throws
     * @Description: 更新支付流水
     */
    void updPayFlow(PayFlowBean payFlowBean);

    /**
     * @param
     * @return
     * @throws
     * @Description: 组装支付流水
     */
    PayFlowBean buildPayFlow(String clientSource, String paySource, String bizId, int bizType, int payAmount);

    /**
     * @param flowId
     * @param thdFlowId
     * @return Boolean
     * @throws
     * @Description: 更新预付单号
     */
    Boolean updateThdFlowId(long flowId, String thdFlowId) throws Exception;

    /**
     * @param
     * @return
     * @throws
     * @Description: 查询可以退款的支付流水
     */
    List<PayFlowBean> getCanRefundList(int payType, String preClientSource);

    /**
     * @param
     * @return
     * @throws
     * @Description: 根据第三方支付流水，查询支付流水
     */
    PayFlowBean getPayFlowByThdFlowId(String thdFlowId);
}
