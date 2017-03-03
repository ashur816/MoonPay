package com.martin.service.tenpublic;

import com.martin.dto.TenMsgInfo;
import com.martin.service.ITenPublicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ZXY
 * @ClassName: TenMsgVoiceService
 * @Description: 语音消息处理类
 * @date 2017/3/2 13:50
 */
@Service("tenMsgVoiceService")
public class TenMsgVoiceService implements ITenPublicService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param tenMsgInfo
     * @return
     * @throws
     * @Description: 微信消息处理
     */
    @Override
    public void doMsgDeal(TenMsgInfo tenMsgInfo) throws Exception {
        logger.info("语音消息处理");
    }
}
