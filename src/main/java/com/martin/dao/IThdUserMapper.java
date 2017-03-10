package com.martin.dao;

import com.martin.bean.ThdUserBean;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author ZXY
 * @ClassName: IUserMapper
 * @Description:
 * @date 2017/3/3 11:24
 */
@Repository
public interface IThdUserMapper extends Mapper<ThdUserBean> {

    int addThdUser(ThdUserBean thdUserBean);

    int updateThdUser(ThdUserBean thdUserBean);

    ThdUserBean getThdUserByThdId(String thdId);
}
