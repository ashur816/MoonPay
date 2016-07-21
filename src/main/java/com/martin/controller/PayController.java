package com.martin.controller;

import com.martin.bean.PayInfo;
import com.martin.bean.PayResult;
import com.martin.constant.PayChannelEnum;
import com.martin.service.IPayCenter;
import com.martin.utils.IpUtils;
import com.martin.utils.JsonUtils;
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
 * @ClassName: PayController
 * @Description: 支付控制器
 * @author ZXY
 * @date 2016/6/16 14:10
 */
@Controller
@RequestMapping("/payCenter")
public class PayController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    //微信
    private final String TEN_PAY = PayChannelEnum.TEN_PAY.getPayType();
    //支付宝
    private final String ALI_PAY = PayChannelEnum.ALI_PAY.getPayType();
    private final static String CALLBACK_SUCCESS = "success";
    private final static String CALLBACK_FAIL = "fail";

    @Resource
    private IPayCenter payCenter;

    /**
     * @Description: 跳转网页支付
     * @param request 包含 订单id
     * @return
     * @throws
     */
    @RequestMapping(value = "/toPay")
    public ModelAndView toPay(HttpServletRequest request, String bizId) {
        ModelAndView modelAndView = new ModelAndView();

        if (StringUtils.isBlank(bizId)) {
            modelAndView.setViewName("common/error");
            modelAndView.addObject("error", "订单号不能为空");
        } else {
            logger.info("网页支付接收参数：bizId-{}", bizId);
            try {
                //获取订单信息
                PayInfo payInfo = payCenter.getPayInfo(bizId);
                modelAndView.setViewName("pay/common_pay");
                doError(payInfo, modelAndView);
            } catch (Exception e) {
                logger.error("doPayCenter异常-{}", e);
                modelAndView.addObject("error", e.getMessage());
                modelAndView.setViewName("common/error");
            }
        }
        return modelAndView;
    }

    /**
     * @Description: 跳转提现
     * @param request 包含 订单id
     * @return
     * @throws
     */
    @RequestMapping(value = "/toWithdraw")
    public ModelAndView toWithdraw(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pay/withdraw");
        return modelAndView;
    }

    /**
     * @Description: 跳转退款
     * @param request 包含 订单id
     * @return
     * @throws
     */
    @RequestMapping(value = "/toRefund")
    public ModelAndView toRefund(HttpServletRequest request, String flowIds) {
        ModelAndView modelAndView = new ModelAndView();

        if (StringUtils.isBlank(flowIds)) {
            modelAndView.setViewName("common/error");
            modelAndView.addObject("error", "支付流水不能为空");
        } else {
            logger.info("退款接收参数：flowIds-{}", flowIds);
            try {
                String[] str = flowIds.split(",");
                List<String> flowList = Arrays.asList(str);
                //获取支付信息
                List<PayInfo> payInfoList = payCenter.getRefundInfo(flowList);
                if (payInfoList != null) {
                    logger.info("支付返回成功");
                    modelAndView.setViewName("pay/refund");
                    modelAndView.addObject("payInfoList", payInfoList);
                    modelAndView.addObject("flowIds", flowIds);
                } else {
                    modelAndView.setViewName("common/error");
                    modelAndView.addObject("error", "未获取到支付信息");
                }
            } catch (Exception e) {
                logger.error("toRefund异常-{}", e);
                modelAndView.addObject("error", e.getMessage());
                modelAndView.setViewName("common/error");
            }
        }
        return modelAndView;
    }

    /**
     * @Description: 网页支付入口 用除微信/支付宝扫码的，直接跳到网页，网页选择支付方式，回传到此接口，再发起支付
     * @param request 包含 订单id
     * @return
     * @throws
     */
    @RequestMapping(value = "/doWebPay")
    public ModelAndView doWebPay(HttpServletRequest request, String bizId, String payType, String voucherId) {
        ModelAndView modelAndView = new ModelAndView();

        if (StringUtils.isBlank(bizId) || StringUtils.isBlank(payType)) {
            modelAndView.setViewName("common/error");
            modelAndView.addObject("error", "订单号和支付方式不能为空");
        } else {
            String ipAddress = IpUtils.getIpAddress(request);
            logger.info("开始网页支付,订单号-{},支付方式-{},代金券-{}", bizId, payType, voucherId);
            try {
                //生成订单支付信息
                PayInfo payInfo = payCenter.doPay(payType, bizId, ipAddress, "", voucherId);
                if (ALI_PAY.equals(payType)) {
                    modelAndView.setViewName("pay/ali_pay");
                    doError(payInfo, modelAndView);
                } else {
                    modelAndView.setViewName("common/error");
                    modelAndView.addObject("error", "暂不支持当前选择的支付方式");
                }
            } catch (Exception e) {
                logger.error("doWebPay异常-{}", e);
                modelAndView.addObject("error", e.getMessage());
                modelAndView.setViewName("common/error");
            }
        }
        return modelAndView;
    }

    /**
     * @Description: 扫码支付入口 微信需要授权，所以会重定向到/doAuthPay，支付宝不授权，直接返回支付form提交
     * @param request 包含 订单id
     * @return
     * @throws
     */
    @RequestMapping(value = "/doScanPay")
    public ModelAndView doScanPay(HttpServletRequest request, String bizId, String voucherId) {
        ModelAndView modelAndView = new ModelAndView();
        String userAgent = request.getHeader("User-Agent");

        if (StringUtils.isBlank(bizId)) {
            modelAndView.setViewName("common/error");
            modelAndView.addObject("error", "业务订单号不能为空");
        } else {
            String payType;
            String ipAddress = IpUtils.getIpAddress(request);
            try {
                if (userAgent.matches("(.*)MicroMessenger(.*)")) {//微信扫码

                    payType = TEN_PAY;
                    //查询授权地址
                    PayInfo retInfo = payCenter.doAuthorize(payType, bizId);
                    if (retInfo == null) {
                        modelAndView.setViewName("common/error");
                        modelAndView.addObject("error", "未获取到授权信息");
                    } else {
                        String url = retInfo.getDestUrl();
                        String param = retInfo.getDestParam();
                        modelAndView.setViewName("redirect:" + url + "?" + param);
                    }
                } else if (userAgent.matches("(.*)AlipayClient(.*)")) {//支付宝扫码

                    payType = ALI_PAY;
                    PayInfo payInfo = payCenter.doPay(payType, bizId, ipAddress, "", voucherId);
                    doError(payInfo, modelAndView);
                } else {//第三方扫码
                    modelAndView.setViewName("common/error");
                    modelAndView.addObject("error", "请用微信或支付宝扫码");
                }
            } catch (Exception e) {
                logger.error("doPayCenter异常-{}", e);
                modelAndView.addObject("error", e.getMessage());
                modelAndView.setViewName("common/error");
            }
        }
        return modelAndView;
    }

    /**
     * @Description: 鉴权后回调地址
     * @param  code-返回授权码 state-自定义参数 payFlowId_payType
     * @return
     * @throws
     */
    @RequestMapping(value = "/doAuthPay")
    public ModelAndView doAuthPay(HttpServletRequest request, String code, String state) {
        ModelAndView modelAndView = new ModelAndView();
        String ipAddress = IpUtils.getIpAddress(request);
        String error = "";
        if (StringUtils.isBlank(state)) {
            modelAndView.setViewName("common/error");
            error = "未获取到授权信息";
        } else {
            String payType = state.split("_")[0];
            String bizId = state.split("_")[1];

            logger.info("doAuthPay接收参数bizId={},payType={}", bizId, payType);
            if (TEN_PAY.equals(payType)) {
                modelAndView.setViewName("pay/ten_pay");
            } else {
                modelAndView.setViewName("common/error");
                modelAndView.addObject("error", "暂不支持当前支付方式");
            }

            try {
                //生成订单信息
                PayInfo payInfo = payCenter.doPay(payType, bizId, ipAddress, code, "");
                doError(payInfo, modelAndView);
            } catch (Exception e) {
                logger.error("PayController.doPay 异常：{}", e);
                modelAndView.setViewName("common/error");
                error = e.getMessage();
            }
        }
        modelAndView.addObject("error", error);
        return modelAndView;
    }

    /**
     * @Description: 支付第三方异步回调
     * @param
     * @return
     * @throws
     */
    @ResponseBody
    @RequestMapping(value = "/{notifyType}/{payType}", method = RequestMethod.POST)
    public Object doNotify(HttpServletRequest request, @PathVariable String notifyType, @PathVariable String payType) {
        Map reqMap = new LinkedHashMap<>();
        String returnCode = CALLBACK_SUCCESS;
        String ipAddress = IpUtils.getIpAddress(request);
        logger.info("第三方回调开始，类型-{},渠道-{},IP地址-{}", notifyType, payType, ipAddress);
        try {
            if (TEN_PAY.equals(payType)) {//微信返回的是xml
                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
                String line = null;
                StringBuilder sbXml = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sbXml.append(line);
                }
                br.close();
                String content = new String(sbXml.toString().getBytes("utf-8"), "utf-8");
                reqMap.put("content", content);
                logger.info("微信回调参数：{}-{}", notifyType, content);
            } else if (ALI_PAY.equals(payType)) {//支付宝返回
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
                    logger.info("支付宝回调参数：{}:{}", name, valueStr);
                }
            } else {
                //非法通知类型
                logger.error("PaymentController.doNotify：非法通知类型");
                returnCode = CALLBACK_FAIL;
                return returnCode;
            }
            payCenter.doNotify(notifyType, payType, ipAddress, reqMap);
        } catch (Exception e) {
            returnCode = CALLBACK_FAIL;
            logger.error("PaymentController.doNotify，异常-{}", e);
        }
        logger.info("第三方回调结束，返回状态：{}", returnCode);
        return returnCode;
    }

    /**
     * @Description: 订单后台无密退款 微信
     * @param request
     * @return
     * @throws
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
                PayResult payResult = (PayResult) payCenter.doRefund(flowIdList, tmpStr);
                if (payResult != null) {
                    logger.info("退款返回信息成功");
                    retMsg = JsonUtils.translateToJson(payResult);
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
     * @Description: 订单有密退款，支付宝
     * @param request
     * @return
     * @throws
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
                PayInfo payInfo = (PayInfo) payCenter.doRefund(flowIdList, tmpStr);

                String payType = payInfo.getPayType().toString();
                if (ALI_PAY.equals(payType)) {
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
     * @Description: 提现入口、企业付款
     * @param request
     * @return
     * @throws
     */
    @ResponseBody
    @RequestMapping(value = "/doWithdraw")
    public Object doWithdraw(HttpServletRequest request, String acctId, String payType, String drawAmount) {
        String retMsg = "";
        if (StringUtils.isBlank(payType) || StringUtils.isBlank(acctId) || StringUtils.isBlank(drawAmount)) {
            retMsg = "账号和支付方式不能为空";
        } else {
            String ipAddress = IpUtils.getIpAddress(request);
            logger.info("开始提现,提现渠道-{},金额-{},ip-{}", payType, drawAmount, ipAddress);
            try {
                //生成订单支付信息
                PayResult payResult = payCenter.doWithdraw(Long.parseLong(acctId), payType, Integer.parseInt(drawAmount), ipAddress);
                if (payResult != null) {
                    logger.info("提现成功");
                    retMsg = JsonUtils.translateToJson(payResult);
                } else {
                    retMsg = "未获取到支付信息";
                }
            } catch (Exception e) {
                logger.error("doWebPay异常-{}", e);
                retMsg = e.getMessage();
            }
        }
        return retMsg;
    }

    /**
     * @Description: 公用错误处理
     * @param
     * @return
     * @throws
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
