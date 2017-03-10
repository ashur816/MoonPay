package com.martin.controller;

import com.martin.service.ITenPublicCenter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author ZXY
 * @ClassName: TenUserController
 * @Description: 微信用户控制类
 * @date 2017/3/1 14:07
 */
@Controller
@RequestMapping("/moon/tenPublic")
public class TenPublicController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ITenPublicCenter tenPublicCenter;

    /**
     * 获取APP支付参数
     *
     * @param request
     * @return
     * @throws
     */
    @ResponseBody
    @RequestMapping(value = "/eventPush")
    public void eventPush(HttpServletRequest request, HttpServletResponse response) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echoStr = request.getParameter("echostr");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
            String line;
            StringBuilder sbXml = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sbXml.append(line);
            }
            br.close();
            String contentXml = new String(sbXml.toString().getBytes("utf-8"), "utf-8");

            String retMsg = tenPublicCenter.eventPush(signature, timestamp, nonce, contentXml);
            //返回给微信成功
            if(StringUtils.isNotBlank(echoStr)) {
                response.getWriter().write(echoStr);
            }
            else {
                response.getWriter().write(retMsg);
            }
        } catch (Exception e) {
            logger.error("微信消息接收异常，{}", e);
        }
    }
}
