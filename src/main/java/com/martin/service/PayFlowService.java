package com.martin.service;

import com.martin.bean.PayFlowBean;
import org.springframework.stereotype.Service;

/**
 * @ClassName: PayFlowService
 * @Description: 支付流水
 * @author ZXY
 * @date 2016/6/30 17:40
 */
@Service("payFlowService")
public class PayFlowService implements IPayFlow {
    /**
     * @Description: 新增支付流水
     * @return
     * @throws
     */
    @Override
    public PayFlowBean addPayFlow() {
        return null;
    }

    /**
     * @Description: 更新支付流水
     * @return
     * @throws
     */
    @Override
    public PayFlowBean updPayFlow() {
        return null;
    }
}
