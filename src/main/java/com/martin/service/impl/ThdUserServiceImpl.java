package com.martin.service.impl;

import com.martin.bean.ThdUserBean;
import com.martin.dao.IThdUserMapper;
import com.martin.service.IThdUserService;
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
@Service("thdUserService")
public class ThdUserServiceImpl implements IThdUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IThdUserMapper thdUserMapper;

    /**
     * @param thdUserBean
     * @return
     * @throws
     * @Description: 新增用户
     */
    @Override
    public void addThdUser(ThdUserBean thdUserBean) throws Exception {
        logger.info("新增用户-{}", JsonUtils.translateToJson(thdUserBean));
        thdUserMapper.addThdUser(thdUserBean);
    }

    /**
     * @param thdUserBean
     * @return
     * @throws
     * @Description: 更新用户信息
     */
    @Override
    public void updateThdUser(ThdUserBean thdUserBean) throws Exception {
        logger.info("更新用户-{}", JsonUtils.translateToJson(thdUserBean));
        thdUserMapper.updateThdUser(thdUserBean);
    }

    /**
     * @param thdId
     * @return
     * @throws
     * @Description: 根据第三方信息获取用户信息
     */
    @Override
    public ThdUserBean getThdUserByThdId(String thdId) throws Exception {
        logger.info("获取第三方用户信息-{}", thdId);
        return thdUserMapper.getThdUserByThdId(thdId);
    }
}
