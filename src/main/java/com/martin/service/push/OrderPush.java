package com.martin.service.push;

import com.martin.dto.PushMessage;
import com.martin.dto.PushResponse;

/**
 * @ClassName: OrderPush
 * @Description: 订单推送
 * @author ZXY
 * @date 2016/7/25 11:20
 */
public class OrderPush extends PushBase {

    /**
     * 初始化参数
     */
    @Override
    public void init() {

    }

    /**
     * 获取通道 Id
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
