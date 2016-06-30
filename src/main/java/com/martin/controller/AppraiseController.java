package com.martin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: AppraiseController
 * @Description: 评价控制器
 * @author ZXY
 * @date 2016/6/30 14:44
 */
@Controller
@RequestMapping("/appraise")
public class AppraiseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @Description: 到点评页面
     * @param
     * @return
     * @throws
     */
    @RequestMapping(value = "/toPraise")
    public ModelAndView toPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/common/appraise");
        return modelAndView;
    }

    /**
     * @Description: 到点评页面
     * @param
     * @return
     * @throws
     */
    @ResponseBody
    @RequestMapping(value = "/doPraise")
    public Object doPraise(HttpServletRequest request) {
        String pValue = request.getParameter("pValue");
        logger.info("开始评价{}星", pValue);
        return "{'msg':'评价成功'}";
    }
}
