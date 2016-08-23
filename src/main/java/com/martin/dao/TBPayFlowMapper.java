package com.martin.dao;

import com.martin.bean.TBPayFlowBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName: PayFlowMapper
 * @Description: 支付流水
 * @author ZXY
 * @date 2016/7/1 10:13
 */
@Repository
public interface TBPayFlowMapper extends Mapper<TBPayFlowBean> {

    List<TBPayFlowBean> selectListById(@Param("flowIdList") List<String> flowIdList, @Param("payState") Integer payState);

    TBPayFlowBean selectByThdId(@Param("thdFlowId") String thdFlowId, @Param("payState") Integer payState);
}