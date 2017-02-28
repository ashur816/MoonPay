package com.martin.service;

import com.martin.bean.PayFlowBean;
import com.martin.dto.RefundResult;
import com.martin.dto.TransferResult;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PayService
 * @Description: 具体实现类接口
 * @author ZXY
 * @date 2016/6/17 11:36
 */
public interface IPayCommonService {
    /**
     * 批量付款，兼容单个
     * @param flowBeanList
     * @param extMap
     * @return
     */
    Object transferBatch(PayFlowBean flowBeanList, Map<String, String> extMap) throws Exception;

    /**
     * 单个账户转账
     * @param flowBean
     * @param extMap
     * @return
     */
    Object transferSingle(PayFlowBean flowBean, Map<String, String> extMap) throws Exception;

    /**
     * @Description: 企业付款返回信息
     * @param
     * @return
     * @throws
     */
    List<TransferResult> transferReturn(Map<String, String> paraMap) throws Exception;

    /**
     * 批量退款，兼容单个
     * @param flowBeanList
     * @param extMap
     * @return
     */
    Object refund(String clientSource, List<PayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception;

    /**
     * @Description: 退款返回信息
     * @param
     * @return
     * @throws
     */
    List<RefundResult> refundReturn(Map<String, String> paraMap) throws Exception;



}
