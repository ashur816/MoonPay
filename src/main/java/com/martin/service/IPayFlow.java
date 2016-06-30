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
    PayFlowBean addPayFlow();

     /**
      * @Description: 更新支付流水
      * @param
      * @return
      * @throws
      */
    PayFlowBean updPayFlow();


}
