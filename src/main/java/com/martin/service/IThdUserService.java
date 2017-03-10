package com.martin.service;

import com.martin.bean.ThdUserBean;

/**
 * @author ZXY
 * @ClassName: IThdUserService
 * @Description:
 * @date 2017/3/3 11:19
 */
public interface IThdUserService {

    /**
     * @param
     * @return
     * @throws
     * @Description: 新增用户
     */
    void addThdUser(ThdUserBean thdUserBean) throws Exception;

    /**
     * @param 
     * @return 
     * @throws 
     * @Description: 更新用户信息
     */
    void updateThdUser(ThdUserBean userBean) throws Exception;

    /**
     * @param
     * @return
     * @throws
     * @Description: 根据第三方信息获取用户信息
     */
    ThdUserBean getThdUserByThdId(String thdId) throws Exception;
}
