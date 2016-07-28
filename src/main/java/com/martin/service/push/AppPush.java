package com.martin.service.push;

import java.io.IOException;

/**
 * @ClassName: AppPush
 * @Description: 个推app
 * @author ZXY
 * @date 2016/7/25 10:01
 */
public class AppPush {

    public static void main(String[] args) throws IOException {

//        IGtPush push = new IGtPush(url, appKey, masterSecret);
//
//        // 定义"点击链接打开通知模板"，并设置标题、内容、链接
//        LinkTemplate template = new LinkTemplate();
//        template.setAppId(appId);
//        template.setAppkey(appKey);
//        template.setTitle("欢迎使用个推!");
//        template.setText("这是一条推送消息~");
//        template.setUrl("http://getui.com");
//
//        List<String> appIds = new ArrayList<>();
//        appIds.add(appId);
//
//        // 定义"AppMessage"类型消息对象，设置消息内容模板、发送的目标App列表、是否支持离线发送、以及离线消息有效期(单位毫秒)
//        AppMessage message = new AppMessage();
//        message.setData(template);
//        message.setAppIdList(appIds);
//        message.setOffline(true);
//        message.setOfflineExpireTime(1000 * 600);
//
//        IPushResult ret = push.pushMessageToApp(message);
//        System.out.println(ret.getResponse().toString());
    }
}
