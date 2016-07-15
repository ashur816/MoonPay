package com.martin.service.impl;

import com.martin.bean.VoucherBean;
import com.martin.dao.VoucherMapper;
import com.martin.service.IVoucher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: VoucherService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author ZXY
 * @date 2016/7/4 9:36
 */
@Service("voucherService")
public class VoucherService implements IVoucher {

    @Resource
    private VoucherMapper voucherMapper;

    /**
     * @Description: 生成代金券
     * @param voucherBean
     * @return
     * @throws
     */
    @Override
    public void createVoucher(VoucherBean voucherBean) throws Exception {
        voucherMapper.insertSelective(voucherBean);
    }

    /**
     * @Description: 获取代金券
     * @param voucherId
     * @param state 1-可用 0-失效
     * @return
     * @throws
     */
    @Override
    public VoucherBean selectVoucherById(Long voucherId, Integer state) throws Exception {
        return voucherMapper.selectByPrimaryKey(voucherId);
    }

    /**
     * @Description: 获取用户的代金券
     * @param userId
     * @param orderType
     * @param state 1-可用 0-失效
     * @return
     * @throws
     */
    @Override
    public List<VoucherBean> getVoucherByUser(Long userId, Integer orderType, Integer state) throws Exception {
        return voucherMapper.selectVoucherByUser(userId, state);
    }
}
