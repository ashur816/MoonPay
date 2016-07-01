package com.martin.service;

import com.martin.bean.PayFlowBean;

/**
 * @ClassName: PayFlowService
 * @Description: 支付流水服务
 * @author ZXY
 * @date 2016/6/30 17:27
 */
public interface IPayFlow {

    /**
     * @Description: 新增支付流水
     * @param
     * @return
     * @throws
     */
    PayFlowBean addPayFlow(PayFlowBean payFlowBean);

    /**
     * @Description: 更新支付流水
     * @param
     * @return
     * @throws
     */
    PayFlowBean updPayFlow(PayFlowBean payFlowBean);

    /**
     * @Description: 根据业务查询支付流水
     * @param bizId 订单号
     * @return
     * @throws
     */
    PayFlowBean getPayFlowByBiz(String bizId);

}
