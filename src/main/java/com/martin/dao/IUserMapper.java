package com.martin.dao;

import com.martin.bean.UserBean;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author ZXY
 * @ClassName: IUserMapper
 * @Description:
 * @date 2017/3/3 11:24
 */
@Repository
public interface IUserMapper extends Mapper<UserBean> {

    int addUser(UserBean userBean);

    UserBean getUserByThdId(String thdId);
}
