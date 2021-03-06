package com.martin.service.tenpublic;

import com.martin.bean.ThdUserBean;
import com.martin.constant.EventTypeEnum;
import com.martin.constant.UserConstant;
import com.martin.dto.TenMsgInfo;
import com.martin.exception.BusinessException;
import com.martin.service.ITenPublicService;
import com.martin.service.IThdUserService;
import com.martin.utils.ServiceContainer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Date;

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
    private IThdUserService thdUserService;

    /**
     * @param tenMsgInfo
     * @return
     * @throws
     * @Description: 微信消息处理
     */
    @Override
    public String doMsgDeal(TenMsgInfo tenMsgInfo) throws Exception {
        logger.info("事件处理");
        String enumName = tenMsgInfo.getEvent();
        String methodName = EventTypeEnum.valueOf(enumName.toUpperCase()).getMethod();
        Class[] argsClass = new Class[1];
        argsClass[0] = TenMsgInfo.class;
        ITenPublicService msgService = new ServiceContainer<ITenPublicService>().get("tenMsgEventService");
        if (StringUtils.isNotBlank(methodName)) {
            Method method = this.getClass().getMethod(methodName, argsClass);
            method.invoke(msgService, tenMsgInfo);
        } else {
            throw new BusinessException("未配置事件[" + enumName + "]相关枚举类");
        }

        return null;
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 订阅事件
     */
    public void subscribe(TenMsgInfo tenMsgInfo) throws Exception {
        logger.info("订阅事件");
        String userOpenId = tenMsgInfo.getFromUserName();
        logger.info("关注人openId-{}", userOpenId);
        //根据openId查询是否已经存在系统中
        ThdUserBean thdUserBean = thdUserService.getThdUserByThdId(userOpenId);
        if (thdUserBean == null) {
            thdUserBean = new ThdUserBean();
            //根据openId获取用户信息 认证号才可以
//        String result = TenPublicUtils.getUserInfo(TokenServer.accessToken, userOpenId);
//        logger.info("获取用户信息-{}", result);
//        String subscribe = JsonUtils.readValueByName(result, "subscribe");
//        if ("1".equals(subscribe)) {//已关注用户
//            userBean.setNickName(JsonUtils.readValueByName(result, "nickname"));
//            userBean.setSex(Integer.parseInt(JsonUtils.readValueByName(result, "sex")));
//            userBean.setCountry(JsonUtils.readValueByName(result, "country"));
//            userBean.setProvince(JsonUtils.readValueByName(result, "province"));
//            userBean.setCity(JsonUtils.readValueByName(result, "city"));
//            userBean.setHeadImg(JsonUtils.readValueByName(result, "headimgurl"));
//        }
            thdUserBean.setThdId(userOpenId);
            thdUserBean.setCreateTime(new Date());
            thdUserBean.setState(UserConstant.RELATE_STATE_1);
            thdUserService.addThdUser(thdUserBean);
        }
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 取消订阅事件
     */
    public void unSubscribe(TenMsgInfo tenMsgInfo) throws Exception {
        logger.info("取消订阅事件");
        String userOpenId = tenMsgInfo.getFromUserName();
        //根据openId查询用户信息
        ThdUserBean thdUserBean = thdUserService.getThdUserByThdId(userOpenId);
        if (thdUserBean != null) {
            thdUserBean.setState(UserConstant.STATE_0);
            thdUserBean.setState(UserConstant.RELATE_STATE_0);
            thdUserBean.setUpdateTime(new Date());
            thdUserService.updateThdUser(thdUserBean);
        }
        else {
            logger.info("未查询到用户信息");
        }
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 扫码事件
     */
    public void scan(TenMsgInfo tenMsgInfo) throws Exception {
        logger.info("扫码事件");

    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 上传地理位置事件
     */
    public void location(TenMsgInfo tenMsgInfo) throws Exception {
        logger.info("上传地理位置事件");

    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 菜单点击事件
     */
    public void click(TenMsgInfo tenMsgInfo) throws Exception {
        logger.info("菜单点击事件");

    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 点击链接跳转事件
     */
    public void view(TenMsgInfo tenMsgInfo) throws Exception {
        logger.info("点击链接跳转事件");

    }

}
