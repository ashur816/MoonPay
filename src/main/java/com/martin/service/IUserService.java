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
    UserBean addUser(UserBean userBean) throws Exception;
}
