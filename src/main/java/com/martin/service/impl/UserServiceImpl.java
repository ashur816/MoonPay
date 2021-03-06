package com.martin.service.impl;

import com.martin.bean.UserBean;
import com.martin.dao.IThdUserMapper;
import com.martin.dao.IUserMapper;
import com.martin.service.IUserService;
import com.martin.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author ZXY
 * @ClassName: UserServiceImpl
 * @Description: 用户类
 * @date 2017/3/3 11:20
 */
@Service("userService")
public class UserServiceImpl implements IUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IUserMapper userMapper;

    @Resource
    private IThdUserMapper thdUserMapper;

    /**
     * @param userBean
     * @return
     * @throws
     * @Description: 新增用户
     */
    @Override
    public void addUser(UserBean userBean) throws Exception {
        logger.info("新增用户-{}", JsonUtils.translateToJson(userBean));
        userMapper.addUser(userBean);
    }

    /**
     * @param userBean@return
     * @throws
     * @Description: 更新用户信息
     */
    @Override
    public void updateUser(UserBean userBean) throws Exception {
        logger.info("更新用户-{}", JsonUtils.translateToJson(userBean));
        userMapper.updateUser(userBean);
    }
}
