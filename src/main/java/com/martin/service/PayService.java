package com.martin.service;

import com.martin.bean.PayFlowBean;
import com.martin.bean.PayInfo;
import com.martin.bean.PayResult;

import java.util.Map;

/**
 * @ClassName: PayService
 * @Description: 具体实现类接口
 * @author ZXY
 * @date 2016/6/17 11:36
 */
public interface PayService {

    /**
     * 生成支付信息
     * @param flowBean
     * @param extMap 额外参数
     * @return
     */
    PayInfo buildPayInfo(PayFlowBean flowBean, Map<String, String> extMap) throws Exception;

    /**
     * @Description: 回调参数校验
     * @param
     * @return
     * @throws
     */
    PayResult returnValidate(Map<String, String> paraMap) throws Exception;

    /**
     * 提现
     * @param flowBean
     * @return
     */
    void withdraw(PayFlowBean flowBean, Map<String, String> extMap) throws Exception;

    /**
     * 获取第三方支付信息
     * @param flowBean
     * @return
     */
    PayResult getPayInfo(PayFlowBean flowBean) throws Exception;

    /**
     * 预授权
     * @param bizId 订单业务id
     * @return
     */
    PayInfo authorize(String bizId) throws Exception;
}
