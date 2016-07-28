package com.martin.service.push;

import com.martin.dto.PushMessage;
import com.martin.dto.PushResponse;

/**
 * @ClassName: PushBase
 * @Description: 推送基类
 * @author ZXY
 * @date 2016/7/25 10:49
 */
public abstract class PushBase {

    /**
     * 初始化参数
     */
    public void init() {
    }

    /**
     * @Description: 获取通道 Id
     * @return
     */
    public abstract String getChannelId();


    /**
     * 推送给 app
     */
    public PushResponse pushToSingle(PushMessage pushMessage) {
        return null;
    }
}
