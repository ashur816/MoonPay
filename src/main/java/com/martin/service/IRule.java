package com.martin.service;

import com.martin.bean.PutRuleBean;
import com.martin.bean.ReceiveRuleBean;
import com.martin.bean.UseRuleBean;

import java.util.List;

/**
 * @ClassName: IRule
 * @Description: 规则接口
 * @author ZXY
 * @date 2016/7/14 16:40
 */
public interface IRule {

     /**
      * @Description: 获取方法规则
      * @param
      * @return
      * @throws
      */
    List<PutRuleBean> getPutRules(Integer policyId) throws Exception;

    /**
     * @Description: 获取领取规则
     * @param
     * @return
     * @throws
     */
    List<ReceiveRuleBean> getReceiveRules(Integer policyId) throws Exception;

    /**
     * @Description: 获取使用规则
     * @param
     * @return
     * @throws
     */
    List<UseRuleBean> getUseRules(Integer policyId) throws Exception;

}
