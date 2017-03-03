package com.martin.service;

/**
 * @author ZXY
 * @ClassName: ITenUserCenter
 * @Description: 微信公众号
 * @date 2017/3/1 14:14
 */
public interface ITenPublicCenter {

    /**
     * @param
     * @return
     * @throws
     * @Description: 微信消息验签
     */
    boolean checkSign(String signature, String timestamp, String nonce) throws Exception;

    /**
     * @param
     * @return
     * @throws
     * @Description: 微信消息推送
     */
    void eventPush(String signature, String timestamp, String nonce, String contentXml) throws Exception;
}
