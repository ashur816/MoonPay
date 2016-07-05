package com.martin.service;

import com.martin.bean.VoucherBean;

import java.util.List;

/**
 * @ClassName: IVoucher
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author ZXY
 * @date 2016/7/4 9:35
 */
public interface IVoucher {

    /**
     * @Description: 获取代金券
     * @param voucherId
     * @param state 1-可用 0-失效
     * @return
     * @throws
     */
    VoucherBean selectVoucherById(Long voucherId, Integer state) throws Exception;

    /**
     * @Description: 获取用户的代金券
     * @param userId
     * @param orderType
     * @param state 1-可用 0-失效
     * @return
     * @throws
     */
    List<VoucherBean> getVoucherByUser(Long userId, Integer orderType, Integer state) throws Exception;
}
