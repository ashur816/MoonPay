package com.martin.service.tenpublic;

import com.martin.constant.MsgTypeEnum;
import com.martin.constant.TenPublicParam;
import com.martin.dto.ArticleMsgInfo;
import com.martin.dto.SendMsgInfo;
import com.martin.dto.TenMsgInfo;
import com.martin.service.ITenPublicService;
import com.martin.utils.BeanUtils;
import com.martin.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZXY
 * @ClassName: TenMsgTextService
 * @Description: 文本消息处理类
 * @date 2017/3/2 13:50
 */
@Service("tenMsgTextService")
public class TenMsgTextService implements ITenPublicService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param tenMsgInfo
     * @return
     * @throws
     * @Description: 微信消息处理
     */
    @Override
    public String doMsgDeal(TenMsgInfo tenMsgInfo) throws Exception {
        logger.info("文本消息处理");
        String content = tenMsgInfo.getContent();
        Map map = new HashMap();
        if ("段子".equals(content)) {
            SendMsgInfo rootMsg = new SendMsgInfo();
            rootMsg.setFromUserName(TenPublicParam.originalId);
            rootMsg.setToUserName(tenMsgInfo.getFromUserName());
            rootMsg.setCreateTime(DateUtils.getTime());
            rootMsg.setMsgType(MsgTypeEnum.NEWS.getCode());
            rootMsg.setArticleCount("1");
            map.put("root", rootMsg);

            ArticleMsgInfo articleMsgInfo = new ArticleMsgInfo();
            articleMsgInfo.setTitle("给我一个套路，我能吃垮一生幸福");
            articleMsgInfo.setDescription("不要为了吃饭，而放弃你的人生");
            articleMsgInfo.setPicUrl("http://i.snssdk.com/neihan/video/playback/?video_id=ead9f4e6ff224048af9a70f19e12bb24&quality=480p&line=0&is_gif=0.mp4");
            articleMsgInfo.setUrl("http://neihanshequ.com/p56848615331/");
            map.put("Articles", articleMsgInfo);

        } else if ("网址".equals(content)) {
            TenMsgInfo retMsgInfo = new TenMsgInfo();
            retMsgInfo.setFromUserName(TenPublicParam.originalId);
            retMsgInfo.setToUserName(tenMsgInfo.getFromUserName());
            retMsgInfo.setCreateTime(DateUtils.getTime());
            retMsgInfo.setMsgType(MsgTypeEnum.TEXT.getCode());
            retMsgInfo.setContent("www.baidu.com");
        } else {
            TenMsgInfo retMsgInfo = new TenMsgInfo();
            retMsgInfo.setFromUserName(TenPublicParam.originalId);
            retMsgInfo.setToUserName(tenMsgInfo.getFromUserName());
            retMsgInfo.setCreateTime(DateUtils.getTime());
            retMsgInfo.setMsgType(MsgTypeEnum.TEXT.getCode());
            retMsgInfo.setContent("你好！很高兴在这里遇见你，你可以回复段子，为你揭开一个逗比世界！");
            map.put("root", retMsgInfo);
        }
        String retXml = BeanUtils.convertXml(map);
        return retXml;
    }
}
