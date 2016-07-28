package com.martin.service.push;

import com.martin.dto.PushMessage;
import com.martin.dto.PushResponse;

/**
 * @ClassName: PayPush
 * @Description: 支付推送
 * @author ZXY
 * @date 2016/7/25 11:20
 */
public class PayPush extends PushBase {
    /**
     * 初始化参数
     */
    @Override
    public void init() {

    }

    /**
     * @Description: 获取通道 Id
     * @return
     */
    @Override
    public String getChannelId() {
        return null;
    }

    /**
     * 推送给 app
     * @param pushMessage
     */
    @Override
    public PushResponse pushToSingle(PushMessage pushMessage) {
        return null;
    }
}
