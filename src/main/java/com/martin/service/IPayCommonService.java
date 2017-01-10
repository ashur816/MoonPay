package com.martin.service;

import com.martin.bean.PayFlowBean;
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
    Object transfer(List<PayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception;

    /**
     * @Description: 企业付款返回信息
     * @param
     * @return
     * @throws
     */
    List<TransferResult> transferReturn(Map<String, String> paraMap) throws Exception;

}
