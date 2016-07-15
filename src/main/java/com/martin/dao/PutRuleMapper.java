package com.martin.dao;

import com.martin.bean.PutRuleBean;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName: RuleMapper
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author ZXY
 * @date 2016/7/14 16:32
 */
public interface PutRuleMapper extends Mapper<PutRuleBean> {

    List<PutRuleBean> selectPutRuleByPolicy(@Param("policyId") Integer policyId);
}
