package com.martin.service.tenpublic;

import com.martin.bean.UserBean;
import com.martin.constant.EventTypeEnum;
import com.martin.dto.TenMsgInfo;
import com.martin.exception.BusinessException;
import com.martin.service.ITenPublicService;
import com.martin.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author ZXY
 * @ClassName: TenMsgEventService
 * @Description: 事件消息服务类
 * @date 2017/3/2 13:50
 */
@Service("tenMsgEventService")
public class TenMsgEventService implements ITenPublicService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IUserService userService;

    /**
     * @param tenMsgInfo
     * @return
     * @throws
     * @Description: 微信消息处理
     */
    @Override
    public void doMsgDeal(TenMsgInfo tenMsgInfo) throws Exception {
        logger.info("事件处理");
        String enumName = tenMsgInfo.getEvent();
        String methodName = EventTypeEnum.valueOf(enumName.toUpperCase()).getMethod();
        Class[] argsClass = new Class[1];
        argsClass[0] = TenMsgInfo.class;
        if (StringUtils.isNotBlank(methodName)) {
            Method method = this.getClass().getMethod(methodName, argsClass);
            method.invoke(this.getClass().newInstance(), tenMsgInfo);
        } else {
            throw new BusinessException("未配置事件[" + enumName + "]相关枚举类");
        }
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 订阅事件
     */
    public void subscribe(TenMsgInfo tenMsgInfo)throws Exception {
        logger.info("订阅事件");
        String userOpenId = tenMsgInfo.getFromUserName();
        //根据openId获取用户信息
        UserBean userBean = new UserBean();
        userBean.setThdId(userOpenId);
        userBean.setUserName(userOpenId);
        userService.addUser(userBean);
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 取消订阅事件
     */
    public void unSubscribe(TenMsgInfo tenMsgInfo)throws Exception {
        logger.info("取消订阅事件");

    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 扫码事件
     */
    public void scan(TenMsgInfo tenMsgInfo)throws Exception {
        logger.info("扫码事件");

    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 上传地理位置事件
     */
    public void location(TenMsgInfo tenMsgInfo)throws Exception {
        logger.info("上传地理位置事件");

    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 菜单点击事件
     */
    public void click(TenMsgInfo tenMsgInfo)throws Exception {
        logger.info("菜单点击事件");

    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 点击链接跳转事件
     */
    public void view(TenMsgInfo tenMsgInfo)throws Exception {
        logger.info("点击链接跳转事件");

    }

}
