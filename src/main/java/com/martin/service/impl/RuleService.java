package com.martin.service.impl;

import com.martin.bean.PutRuleBean;
import com.martin.bean.ReceiveRuleBean;
import com.martin.bean.UseRuleBean;
import com.martin.dao.PutRuleMapper;
import com.martin.dao.ReceiveRuleMapper;
import com.martin.dao.UseRuleMapper;
import com.martin.service.IRule;
import com.martin.service.IVoucher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: RuleService
 * @Description: 规则类
 * @author ZXY
 * @date 2016/7/14 16:41
 */
@Service("ruleService")
public class RuleService implements IRule {

    @Resource
    private PutRuleMapper putRuleMapper;

    @Resource
    private ReceiveRuleMapper receiveRuleMapper;

    @Resource
    private UseRuleMapper useRuleMapper;

    @Resource
    private IVoucher voucher;

    /**
     * @Description: 获取方法规则
     * @param policyId

     * @return
     * @throws
     */
    @Override
    public List<PutRuleBean> getPutRules(Integer policyId) throws Exception {
        return putRuleMapper.selectPutRuleByPolicy(policyId);
    }

    /**
     * @Description: 获取领取规则
     * @param policyId

     * @return
     * @throws
     */
    @Override
    public List<ReceiveRuleBean> getReceiveRules(Integer policyId) throws Exception {
        return null;
    }

    /**
     * @Description: 获取使用规则
     * @param policyId

     * @return
     * @throws
     */
    @Override
    public List<UseRuleBean> getUseRules(Integer policyId) throws Exception {
        return null;
    }
}
