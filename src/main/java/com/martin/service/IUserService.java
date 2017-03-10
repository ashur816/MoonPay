package com.martin.service;

import com.martin.bean.UserBean;

/**
 * @author ZXY
 * @ClassName: IUserService
 * @Description:
 * @date 2017/3/3 11:19
 */
public interface IUserService {

    /**
     * @param
     * @return
     * @throws
     * @Description: 新增用户
     */
    void addUser(UserBean userBean) throws Exception;

    /**
     * @param 
     * @return 
     * @throws 
     * @Description: 更新用户信息
     */
    void updateUser(UserBean userBean) throws Exception;
}
