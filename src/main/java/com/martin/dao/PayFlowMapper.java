package com.martin.dao;

import com.martin.bean.PayFlowBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName: PayFlowMapper
 * @Description: 支付流水
 * @author ZXY
 * @date 2016/7/1 10:13
 */
@Repository
public interface PayFlowMapper extends Mapper<PayFlowBean> {

    PayFlowBean selectByBiz(String bizId);

    PayFlowBean selectByPayState(@Param("flowId") Long flowId, @Param("payState") Integer payState);
}