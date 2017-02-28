package com.martin.dao;

import com.martin.bean.PayFlowBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author ZXY
 * @ClassName: PayFlowMapper
 * @Description: 支付流水
 * @date 2016/7/1 10:13
 */
@Repository
public interface PayFlowMapper extends Mapper<PayFlowBean> {

    List<PayFlowBean> getPayFlowListByBiz(@Param("bizId") String bizId, @Param("bizType") int bizType);

    PayFlowBean getPayFlowById(@Param("flowId") long flowId, @Param("payState") int payState);

    List<PayFlowBean> getPayFlowList(@Param("flowId") long flowId, @Param("payState") int payState);

    List<PayFlowBean> selectListByIdList(@Param("flowIdList") List<String> flowIdList, @Param("payState") int payState);

    PayFlowBean selectByThdId(@Param("thdFlowId") String thdFlowId, @Param("payState") int payState);

    int updateThdFlowId(@Param("flowId") long flowId, @Param("thdFlowId") String thdFlowId);

    List<PayFlowBean> getCanRefundList(@Param("payType") int payType, @Param("clientSource") String clientSource);

    PayFlowBean getPayFlowByThdFlowId(@Param("thdFlowId") String thdFlowId);
}