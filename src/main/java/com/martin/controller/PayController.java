package com.martin.controller;

import com.martin.constant.PayConstant;
import com.martin.dto.PayInfo;
import com.martin.dto.ResultInfo;
import com.martin.exception.BusinessException;
import com.martin.service.IPayAppCenter;
import com.martin.service.IPayCommonCenter;
import com.martin.service.IPayWebCenter;
import com.martin.utils.IpUtils;
import com.martin.utils.JsonUtils;
import com.martin.utils.PayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author ZXY
 * @ClassName: PayController
 * @Description: 支付控制器
 * @date 2016/6/16 14:10
 */
@Controller
@RequestMapping("/moon/cashier")
public class PayController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IPayCommonCenter payCommonCenter;

    @Resource
    private IPayWebCenter payWebCenter;

    @Resource
    private IPayAppCenter payAppCenter;

    /**
     * 收银台界面
     *
     * @param request
     * @return
     * @throws
     */
    @RequestMapping(value = "/toPay")
    public ModelAndView toCashier(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pay/cashier");
        return modelAndView;
    }

    /**
     * @param request 包含 订单id
     * @return
     * @throws
     * @Description: 跳转退款
     */
    @RequestMapping(value = "/toRefund")
    public ModelAndView toRefund(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pay/refund");
        return modelAndView;
    }

    /**
     * 企业付款界面
     *
     * @param request
     * @return
     * @throws
     */
    @RequestMapping(value = "/toTransfer")
    public ModelAndView toTransfer(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pay/transfer");
        return modelAndView;
    }

    /*********************************************** WEB支付 ******************************************************/
    /**
     * 跳转网页支付 根据请求头，判断支付途径
     * 在微信里面打开的，肯定是微信支付
     * 在其他浏览器中打开的，肯定是支付宝支付
     *
     * @param request
     * @return
     * @throws
     */
    @RequestMapping(value = "/toWebPay")
    public ModelAndView toWebPay(HttpServletRequest request, String bizId, String bizType, String code) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("common/error");
        if (StringUtils.isBlank(bizId)) {
            modelAndView.addObject("error", "业务单号不能为空");
        } else if (StringUtils.isBlank(bizType)) {
            modelAndView.addObject("error", "业务类型不能为空");
        } else {
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = IpUtils.getIpAddress(request);
            int payType;
            try {
                if (userAgent.matches("(.*)MicroMessenger(.*)")) {//微信
                    payType = PayConstant.PAY_TYPE_TEN;
                    //查询授权地址
                    PayInfo retInfo = payWebCenter.doAuthorize(payType, bizId, bizType);
                    if (retInfo == null) {
                        modelAndView.setViewName("common/error");
                        modelAndView.addObject("error", "未获取到微信授权信息");
                    } else {
                        String url = retInfo.getDestUrl();
                        String param = retInfo.getDestParam();
                        modelAndView.setViewName("redirect:" + url + "?" + param);
                    }
                } else {
                    payType = PayConstant.PAY_TYPE_ALI;
                    PayInfo payInfo = payWebCenter.doPay("moon_web.ios", payType, bizId, Integer.parseInt(bizType), ipAddress, "");
                    doError(payInfo, modelAndView);
                    modelAndView.setViewName("pay/ali_pay");
                }
            } catch (Exception e) {
                logger.error("PayController.doWebPay异常-{}", e);
                modelAndView.addObject("error", e.getMessage());
                modelAndView.setViewName("common/error");
            }
        }
        return modelAndView;
    }

    /**
     * @param code-返回授权码 state-自定义参数 payFlowId_payType
     * @return
     * @throws
     * @Description: 微信鉴权后回调地址，然后发起支付
     */
    @RequestMapping(value = "/doAuthPay")
    public ModelAndView doAuthPay(HttpServletRequest request, String appId, String code, String state) {
        ModelAndView modelAndView = new ModelAndView();
        String ipAddress = IpUtils.getIpAddress(request);
        String error = "";
        if (StringUtils.isBlank(state)) {
            modelAndView.setViewName("common/error");
            error = "未获取到授权信息";
        } else {
            String[] str = state.split("\\|");
            int payType = Integer.parseInt(str[0]);
            String bizId = str[1];
            String bizType = str[2];

            logger.info("doAuthPay接收参数payType={},bizId={},bizType={}", payType, bizId, bizType);
            if (PayConstant.PAY_TYPE_ALI == payType) {
                modelAndView.setViewName("pay/ten_pay");
            } else if (PayConstant.PAY_TYPE_TEN == payType) {
                modelAndView.setViewName("pay/ali_pay");
            } else {
                modelAndView.setViewName("common/error");
                modelAndView.addObject("error", "暂不支持当前支付方式");
            }
            try {
                //组装支付信息
                PayInfo payInfo = payWebCenter.doPay(appId, payType, bizId, Integer.parseInt(bizType), ipAddress, code);
                doError(payInfo, modelAndView);
            } catch (Exception e) {
                logger.error("PayController.doAuthPay 异常：{}", e);
                modelAndView.setViewName("common/error");
                error = e.getMessage();
            }
        }
        modelAndView.addObject("error", error);
        return modelAndView;
    }

    /*********************************************** APP支付 ******************************************************/
    /**
     * 获取APP支付参数
     *
     * @param request 包含 订单id
     * @return
     * @throws
     */
    @ResponseBody
    @RequestMapping(value = "/toAppPay")
    public ResultInfo toAppPay(HttpServletRequest request, String appId, String bizId, String bizType, String payType) {
        ResultInfo resultInfo;
        if (StringUtils.isBlank(bizId)) {
            resultInfo = new ResultInfo(-1, "", "业务单号不能为空");
        } else if (StringUtils.isBlank(bizType)) {
            resultInfo = new ResultInfo(-1, "", "业务类型不能为空");
        } else {
            String ipAddress = IpUtils.getIpAddress(request);
            try {
                Map payMap = payAppCenter.buildPayInfo(appId, payType, bizId, bizType, ipAddress);
                resultInfo = new ResultInfo(1, "", "", payMap);
            } catch (Exception e) {
                logger.error("PayController.toAppPay异常-{}", e);
                if (e instanceof BusinessException) {
                    resultInfo = new ResultInfo(-1, "", e.getMessage());
                } else {
                    resultInfo = new ResultInfo(-1, "", "获取支付参数异常");
                }
            }
        }
        return resultInfo;
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 支付第三方异步回调
     */
    @ResponseBody
    @RequestMapping(value = "/{notifyType}/{tmpPayType}", method = RequestMethod.POST)
    public Object doNotify(HttpServletRequest request, @PathVariable String notifyType, @PathVariable String tmpPayType) {
        Map reqMap = new LinkedHashMap<>();
        String returnCode = PayConstant.CALLBACK_SUCCESS;
        String ipAddress = IpUtils.getIpAddress(request);
        int payType = Integer.parseInt(tmpPayType);
        logger.info("第三方回调开始，类型-{},渠道-{},IP地址-{}", notifyType, payType, ipAddress);
        try {
            if (PayConstant.PAY_TYPE_TEN == payType) {//微信返回的是xml
                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
                String line;
                StringBuilder sbXml = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sbXml.append(line);
                }
                br.close();
                String content = new String(sbXml.toString().getBytes("utf-8"), "utf-8");
                reqMap.put("content", content);
                logger.info("微信回调参数：{}-{}", notifyType, content);
            } else if (PayConstant.PAY_TYPE_ALI == payType) {//支付宝返回
                //获取支付宝POST过来反馈信息，lockMap不能修改
                Map<String, String[]> tmpMap = request.getParameterMap();
                Iterator it = tmpMap.entrySet().iterator();

                for (; it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String name = entry.getKey().toString();
                    String[] values = (String[]) entry.getValue();
                    StringBuilder valueStr = new StringBuilder();
                    for (int i = 0; i < values.length; i++) {
                        if (i == values.length - 1) {
                            valueStr.append(values[i]);
                        } else {
                            valueStr.append(values[i]).append(",");
                        }
                    }
                    //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                    //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                    reqMap.put(name, valueStr.toString());
                }
            } else {
                //非法通知类型
                logger.error("PaymentController.doNotify：非法支付类型");
                returnCode = PayConstant.CALLBACK_FAIL;
                return returnCode;
            }
            logger.info("支付宝回调全部参数：{}", PayUtils.buildConcatStr(reqMap));
            payCommonCenter.doNotify(notifyType, payType, ipAddress, reqMap);
        } catch (Exception e) {
            returnCode = PayConstant.CALLBACK_FAIL;
            logger.error("PaymentController.doNotify，异常-{}", e);
        }
        logger.info("第三方回调结束，返回状态：{}", returnCode);
        return returnCode;
    }

    /*********************************************** 退款 ******************************************************/
    /**
     * @param request
     * @return
     * @throws
     * @Description: 获取退款信息
     */
    @ResponseBody
    @RequestMapping(value = "/getRefund")
    public ResultInfo getRefundInfo(HttpServletRequest request, String payType, String appId) {
        ResultInfo resultInfo;
        if (StringUtils.isBlank(payType)) {
            payType = "0";
        }
        if (StringUtils.isNotBlank(appId)) {
            appId += "%";
        }
        try {
            List<PayInfo> payInfoList = payCommonCenter.getRefundInfo(Integer.parseInt(payType), appId);
            if (payInfoList != null) {
                logger.info("退款返回信息成功");
                resultInfo = new ResultInfo(1, "", "", JsonUtils.translateToJson(payInfoList));
            } else {
                resultInfo = new ResultInfo(-1, "", "未获取到付款流水");
            }
        } catch (Exception e) {
            logger.error("getRefundInfo异常-{}", e);
            resultInfo = new ResultInfo(-1, "", e.getMessage());
        }
        return resultInfo;
    }

    /**
     * @param request
     * @return
     * @throws
     * @Description: 无密退款 微信
     */
    @ResponseBody
    @RequestMapping(value = "/doRefund")
    public Object doRefund(HttpServletRequest request, String flowIds, String refundReason) {
        String retMsg = "";
        if (StringUtils.isBlank(flowIds)) {
            retMsg = "传入信息不能为空";
        } else {
            try {
                String tmpStr = new String(refundReason.getBytes("ISO-8859-1"), "UTF-8");
                logger.info("无密退款接收flowIds-{},refundReason-{}", flowIds, tmpStr);

                String[] str = flowIds.split(",");
                List<String> flowIdList = Arrays.asList(str);
                Object refundResult = payCommonCenter.doRefund(flowIdList, tmpStr);
                if (refundResult != null) {
                    logger.info("退款返回信息成功");
                    retMsg = refundResult.toString();
                } else {
                    retMsg = "未获取到退款信息";
                }
            } catch (Exception e) {
                logger.error("doRefund异常-{}", e);
                retMsg = e.getMessage();
            }
        }
        return retMsg;
    }

    /**
     * @param request
     * @return
     * @throws
     * @Description: 有密退款，支付宝
     */
    @RequestMapping(value = "/doRefundPwd")
    public ModelAndView doRefundPwd(HttpServletRequest request, String flowIds, String refundReason) {
        ModelAndView modelAndView = new ModelAndView();
        if (StringUtils.isBlank(flowIds)) {
            modelAndView.setViewName("common/error");
            modelAndView.addObject("error", "传入信息不能为空");
        } else {
            try {
                String tmpStr = new String(refundReason.getBytes("ISO-8859-1"), "UTF-8");
                logger.info("有密退款接收flowIds-{},refundReason-{}", flowIds, tmpStr);

                String[] str = flowIds.split(",");
                List<String> flowIdList = Arrays.asList(str);
                PayInfo payInfo = (PayInfo) payCommonCenter.doRefund(flowIdList, tmpStr);

                int payType = payInfo.getPayType();
                if (PayConstant.PAY_TYPE_ALI == payType) {
                    modelAndView.setViewName("pay/ali_pay");
                    doError(payInfo, modelAndView);
                } else {
                    modelAndView.setViewName("common/error");
                    modelAndView.addObject("error", "暂不支持当前退款方式");
                }
            } catch (Exception e) {
                logger.error("doRefundPwd异常-{}", e);
            }
        }
        return modelAndView;
    }

    /**
     * 获取APP支付参数
     *
     * @param request 包含 订单id
     * @return
     * @throws
     */
    @ResponseBody
    @RequestMapping(value = "/doTransfer")
    public ResultInfo doTransfer(HttpServletRequest request, String thdNo, String thdName, String drawAmount, String payType) {
        ResultInfo resultInfo;
        try {
            if (StringUtils.isBlank(thdNo)) {
                resultInfo = new ResultInfo(-1, "", "提现账号不能为空");
            } else if (StringUtils.isBlank(thdName)) {
                resultInfo = new ResultInfo(-1, "", "账户名称不能为空");
            } else if (StringUtils.isBlank(drawAmount)) {
                resultInfo = new ResultInfo(-1, "", "提现金额不能为空");
            } else if (StringUtils.isBlank(payType)) {
                resultInfo = new ResultInfo(-1, "", "提现渠道不能为空");
            } else {
                String ipAddress = IpUtils.getIpAddress(request);
                Object retInfo = payCommonCenter.doTransfer(thdNo, thdName, Integer.parseInt(drawAmount), Integer.parseInt(payType), ipAddress);
                resultInfo = new ResultInfo(1, "", "", retInfo);
            }
        } catch (Exception e) {
            logger.error("PayController.toAppPay异常-{}", e);
            if (e instanceof BusinessException) {
                resultInfo = new ResultInfo(-1, "", e.getMessage());
            } else {
                resultInfo = new ResultInfo(-1, "", "获取支付参数异常");
            }
        }
        return resultInfo;
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 公用错误处理
     */
    private void doError(PayInfo payInfo, ModelAndView modelAndView) {
        if (payInfo != null) {
            logger.info("支付返回成功");
            modelAndView.addObject("payInfo", payInfo);
        } else {
            modelAndView.setViewName("common/error");
            modelAndView.addObject("error", "未获取到支付信息");
        }
    }
}
