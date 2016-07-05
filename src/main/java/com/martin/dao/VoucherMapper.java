package com.martin.dao;

import com.martin.bean.VoucherBean;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName: VoucharMapper
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author ZXY
 * @date 2016/7/4 15:50
 */
public interface VoucherMapper extends Mapper<VoucherBean> {

    List<VoucherBean> selectVoucherByUser(@Param("userId") Long userId, @Param("state") Integer state);
}
