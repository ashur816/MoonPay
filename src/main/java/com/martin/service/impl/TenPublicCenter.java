package com.martin.service.impl;

import com.martin.constant.MsgTypeEnum;
import com.martin.constant.PayParam;
import com.martin.dto.TenMsgInfo;
import com.martin.exception.BusinessException;
import com.martin.service.ITenPublicCenter;
import com.martin.service.ITenPublicService;
import com.martin.utils.BeanUtils;
import com.martin.utils.JsonUtils;
import com.martin.utils.PayUtils;
import com.martin.utils.ServiceContainer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.SortedMap;

/**
 * @author ZXY
 * @ClassName: TenUserCenter
 * @Description:
 * @date 2017/3/1 14:15
 */
@Service("tenPublicCenter")
public class TenPublicCenter implements ITenPublicCenter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     * @throws
     * @Description: 微信消息验签
     */
    @Override
    public boolean checkSign(String signature, String timestamp, String nonce) throws Exception {
//        String mySign = SHA1.getSHA1(TenPublicParam.token, timestamp, nonce);
//        if (!mySign.equalsIgnoreCase(signature)) {
//            return false;
//        }
        return true;
    }

    /**
     * @param signature
     * @param timestamp
     * @param nonce
     * @param contentXml @return
     * @throws
     * @Description: 微信消息推送
     */
    @Override
    public void eventPush(String signature, String timestamp, String nonce, String contentXml) throws Exception {
        boolean flag = checkSign(signature, timestamp, nonce);
        if (!flag) {
            throw new BusinessException("微信消息验签失败");
        } else {
            if (StringUtils.isNotBlank(contentXml)) {
                SortedMap<String, String> map = PayUtils.getMapFromXML(contentXml, PayParam.inputCharset);
                logger.info("微信消息信息-{}", JsonUtils.translateToJson(map));
                TenMsgInfo tenMsgInfo = (TenMsgInfo) BeanUtils.convertMap(TenMsgInfo.class, map);
                logger.error(JsonUtils.translateToJson(tenMsgInfo));
                String msgType = tenMsgInfo.getMsgType();
                MsgTypeEnum msgTypeEnum = MsgTypeEnum.valueOf(msgType.toUpperCase());
                String serviceName = msgTypeEnum.getService();
                ITenPublicService tenPublicService = new ServiceContainer<ITenPublicService>().get(serviceName);
                tenPublicService.doMsgDeal(tenMsgInfo);
            }
        }
    }
}
