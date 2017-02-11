package com.martin.service;

import com.martin.bean.PayFlowBean;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;

import java.util.Map;

/**
 * @ClassName: IPayWebService
 * @Description:
 * @author ZXY
 * @date 2017/1/9 14:19
 */
public interface IPayWebService {

    /**
     * 预授权
     * @param bizId 订单业务id
     * @return
     */
    PayInfo authorize(String bizId, String bizType) throws Exception;

    /**
     * 生成支付信息
     * @param flowBean
     * @param extMap 额外参数
     * @return
     */
    PayInfo buildPayInfo(PayFlowBean flowBean, Map<String, String> extMap) throws Exception;

    /**
     * @Description: 支付回调参数校验
     * @param
     * @return
     * @throws
     */
    PayResult payReturn(Map<String, String> paraMap) throws Exception;

    /**
     * @Description: 查询第三方支付状态
     * @param
     * @return
     * @throws
     */
    PayResult getPayStatus(Long flowId) throws Exception;

    /**
     * @Description: 关闭第三方支付订单
     * @param
     * @return
     * @throws
     */
    void closeThdPay(Long flowId) throws Exception;
}
