package com.martin.service;

import com.martin.dto.TenMsgInfo;

/**
 * @author ZXY
 * @ClassName: ITenPublicService
 * @Description:
 * @date 2017/3/2 13:44
 */
public interface ITenPublicService {
    /**
     * @param
     * @return
     * @throws
     * @Description: 微信消息处理
     */
    void doMsgDeal(TenMsgInfo tenMsgInfo) throws Exception;
}
