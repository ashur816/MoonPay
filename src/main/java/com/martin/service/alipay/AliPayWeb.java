package com.martin.service.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayWebService;
import com.martin.utils.DateUtils;
import com.martin.utils.JsonUtils;
import com.martin.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZXY
 * @ClassName: AliPay
 * @Description: 支付宝--用来企业付款
 * @date 2016/5/24 10:22
 */
@Service("aliPayWebService")
public class AliPayWeb implements IPayWebService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 预授权
     *
     * @param bizId
     * @return
     */
    @Override
    public PayInfo authorize(String bizId, String bizType) throws Exception {
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("app_id", PayParam.aliWebAppId);
        paraMap.put("scope", PayParam.aliAuthCode);
        paraMap.put("redirect_uri", PayParam.aliAuthRetUrl);
        paraMap.put("state", PayConstant.PAY_TYPE_ALI + "|" + bizId + "|" + bizType);

        String param = AliPayUtils.createLinkString(paraMap);

        PayInfo payInfo = new PayInfo();
        payInfo.setDestUrl(PayParam.aliAuthUrl);
        payInfo.setDestParam(param);

        return payInfo;
    }

    /**
     * 新版生成支付信息
     *
     * @param flowBean
     * @return
     */
    @Override
    public PayInfo buildPayInfo(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        PayInfo p = build2(flowBean, extMap);
        return p;
    }

    /**
     * 老版生成支付信息
     *
     * @param flowBean
     * @return
     */
    private PayInfo build1(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始支付宝web支付");
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("service", PayParam.aliOldPayService);
        paraMap.put("partner", PayParam.aliPartnerId);
        paraMap.put("seller_id", PayParam.aliPartnerId);
        paraMap.put("_input_charset", PayParam.aliInputCharset);
        paraMap.put("payment_type", PayParam.aliPaymentType);
        paraMap.put("notify_url", PayParam.aliWebNotifyUrl);

        //统一跳订单详情
        String url = extMap.get("returnUrl");
        if (!StringUtils.isEmpty(url)) {
            paraMap.put("return_url", url);
        } else {//其余的跳首页
            paraMap.put("return_url", PayParam.homeUrl);
        }

        // ZD流水号
        paraMap.put("out_trade_no", String.valueOf(flowBean.getFlowId()));
        //商品名称
        paraMap.put("subject", PayParam.webBody);
        //支付总金额
        double needPayAmount = flowBean.getTotalAmount() / 100.0;
        paraMap.put("total_fee", String.valueOf(needPayAmount));
        paraMap.put("body", PayParam.webBody);

        String html = AliPayUtils.buildReqForm(PayParam.aliMapiUrl, PayParam.aliMD5Key, PayParam.aliWebSignType, paraMap);
        return new PayInfo(PayParam.webBody, needPayAmount, html);
    }

    private PayInfo build2(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始支付宝web支付");
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("app_id", PayParam.aliWebAppId);
        paraMap.put("method", PayParam.aliPayService);
        paraMap.put("format", "json");
//        paraMap.put("alipay_sdk", "alipay-sdk-java-dynamicVersionNo");

        //统一跳订单详情
        String url = extMap.get("returnUrl");
        if (!StringUtils.isEmpty(url)) {
            paraMap.put("return_url", url);
        } else {//其余的跳首页
            paraMap.put("return_url", PayParam.homeUrl);
        }

        paraMap.put("charset", "utf-8");
        paraMap.put("sign_type", "RSA");
        paraMap.put("timestamp", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        paraMap.put("version", "1.0");
        paraMap.put("notify_url", PayParam.aliWebNotifyUrl);

        // biz_content 业务请求参数的集合
        Map<String, String> bizMap = new HashMap<>();
        //商品名称
        bizMap.put("subject", PayParam.webBody);
        //支付流水号
        bizMap.put("out_trade_no", "2017021212044779888");
        //支付总金额
        double needPayAmount = flowBean.getTotalAmount() / 100.0;
        bizMap.put("total_amount", String.valueOf(needPayAmount));
        //销售产品码
        bizMap.put("product_code", "QUICK_WAP_PAY");

        paraMap.put("biz_content", JsonUtils.translateToJson(bizMap));
        String html = AliPayUtils.buildReqUrl(PayParam.aliOpenUrl, PayParam.aliWebPrivateKey, paraMap);
        return new PayInfo(PayParam.webBody, needPayAmount, html);
    }

    private PayInfo build3(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始支付宝web支付");
        //获得初始化的AlipayClient
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(PayParam.aliOpenUrl, PayParam.aliWebAppId, PayParam.aliWebPrivateKey, "json", "utf-8", PayParam.aliAliPublicKey, "RSA");
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        //统一跳订单详情
        String url = extMap.get("returnUrl");
        if (StringUtils.isEmpty(url)) {
            url = PayParam.homeUrl;
        }
        alipayRequest.setReturnUrl(url);
        alipayRequest.setNotifyUrl(PayParam.aliWebNotifyUrl);//在公共参数中设置回跳和通知地址

        // biz_content 业务请求参数的集合
        Map<String, String> bizMap = new HashMap<>();
        //商品名称
        bizMap.put("subject", PayParam.webBody);
        //支付流水号
        bizMap.put("out_trade_no", "2017021212044779888");
        //支付总金额
        double needPayAmount = flowBean.getTotalAmount() / 100.0;
        bizMap.put("total_amount", String.valueOf(needPayAmount));
        //销售产品码
        bizMap.put("product_code", "QUICK_WAP_PAY");

        alipayRequest.setBizContent(JsonUtils.translateToJson(bizMap));//填充业务参数
        String form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        return new PayInfo(PayParam.webBody, needPayAmount, form);
    }

    /**
     * @param paraMap
     * @return
     * @throws
     * @Description: 支付回调参数校验
     */
    @Override
    public PayResult payReturn(Map<String, String> paraMap) throws Exception {
        logger.info("web支付回调处理");
        //验签
        returnValidate(paraMap);

        PayResult payResult = new PayResult();
        // 支付流水ID
        payResult.setFlowId(Long.valueOf(paraMap.get("out_trade_no")));
        // 支付宝交易流水号
        payResult.setThdFlowId(paraMap.get("trade_no"));
        //交易状态
        String tradeState = paraMap.get("trade_status");
        payResult.setTradeState(tradeState);

        int callbackState = transPayState(tradeState);
        payResult.setPayState(callbackState);
        return payResult;
    }

    /**
     * @param flowId
     * @return
     * @throws
     * @Description: 查询第三方支付状态
     */
    @Override
    public PayResult getPayStatus(Long flowId) throws Exception {
        logger.info("开始WEB支付宝查单");
        //组装参数
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("app_id", PayParam.aliWebAppId);
        paraMap.put("method", PayParam.aliQueryService);
        paraMap.put("charset", "utf-8");
        paraMap.put("sign_type", "RSA");//只支持RSA
        paraMap.put("timestamp", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        paraMap.put("version", "1.0");

        // biz_content 业务请求参数的集合
        Map<String, String> bizMap = new HashMap<>();
        //支付流水号
        bizMap.put("out_trade_no", String.valueOf(flowId));

        paraMap.put("biz_content", JsonUtils.translateToJson(bizMap));

        String tmpString = AliPayAppUtils.sendPost(PayParam.aliOpenUrl, PayParam.aliWebPrivateKey, paraMap);
        //解析返回
        Map tmpMap = JsonUtils.readMap(tmpString);
        Map returnMap = (Map) tmpMap.get("alipay_trade_query_response");
        Object subCode = returnMap.get("sub_code");
        Object subMsg = returnMap.get("sub_msg");
        Object tradeStatus = returnMap.get("trade_status");
        String code = "WAIT_BUYER_PAY";
        if (ObjectUtils.isNotEmpty(subCode)) {
            code = subCode.toString();
        } else if (ObjectUtils.isNotEmpty(tradeStatus)) {
            code = tradeStatus.toString();
        }
        logger.info("WEB支付宝查单结果-{},-{}", code, subMsg);
        PayResult payResult = new PayResult();
        payResult.setPayState(transPayState(code));
        if (PayConstant.PAY_SUCCESS == payResult.getPayState()) {
            //支付成功的更新第三方交易流水号
            payResult.setThdFlowId(returnMap.get("trade_no").toString());
        }
        return payResult;
    }

    /**
     * @param flowId
     * @return
     * @throws
     * @Description: 关闭第三方支付订单 只有等待买家付款状态下才能发起交易关闭
     */
    @Override
    public void closeThdPay(Long flowId) throws Exception {
        logger.info("开始WEB支付宝关单");
        //组装参数
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("app_id", PayParam.aliWebAppId);
        paraMap.put("method", PayParam.aliCloseService);
        paraMap.put("charset", "utf-8");
        paraMap.put("sign_type", PayParam.aliAppSignType);//只支持RSA
        paraMap.put("timestamp", DateUtils.format(new Date(), "yyyyMMddHHmmss"));
        paraMap.put("version", "1.0");

        // biz_content 业务请求参数的集合
        Map<String, String> bizMap = new HashMap<>();
        //支付流水号
        bizMap.put("out_trade_no", String.valueOf(flowId));

        paraMap.put("biz_content", JsonUtils.translateToJson(bizMap));

        String tmpString = AliPayAppUtils.sendPost(PayParam.aliOpenUrl, PayParam.aliWebPrivateKey, paraMap);
        //解析返回
        Map tmpMap = JsonUtils.readMap(tmpString);
        Map returnMap = (Map) tmpMap.get("alipay_trade_close_response");
        Object msg = returnMap.get("msg");
        Object subCode = returnMap.get("sub_code");
        if (ObjectUtils.isNotEmpty(msg) && "SUCCESS".equalsIgnoreCase(msg.toString())) {
            //关闭成功
            logger.info("WEB支付宝关单成功-{}", msg);
        } else {
            //关闭失败
            logger.info("WEB支付宝关单失败-{}", subCode);
        }
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 回调验签
     */
    private void returnValidate(Map<String, String> paraMap) throws Exception {
        if (paraMap == null || paraMap.size() < 1) {
            //参数不能为空
            throw new BusinessException("参数不能为空");
        }

        //判断responseTxt是否为true，isSign是否为true
        //responseTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        String responseTxt = "false";
        if (paraMap.get("notify_id") != null) {
            String notify_id = paraMap.get("notify_id");
            String verify_url = PayParam.aliVerifyUrl + "&partner=" + PayParam.aliPartnerId + "&notify_id=" + notify_id;
            responseTxt = AliPayUtils.checkUrl(verify_url);
        }
        if ("false".equalsIgnoreCase(responseTxt)) {
            //支付宝回调异常
            throw new BusinessException("支付宝回调异常");
        }

        String returnSign = paraMap.get("sign");
        Map<String, String> tmpMap = AliPayUtils.paraFilter(paraMap);
        String mySign = AliPayUtils.buildRequestMySign(PayParam.aliMD5Key, PayParam.aliWebSignType, tmpMap);
        if (!returnSign.equals(mySign)) {
            //支付宝回调签名不匹配
            throw new BusinessException("支付宝回调签名不匹配");
        }
    }

    /**
     * 转换支付状态
     *
     * @return
     */
    private int transPayState(String tradeState) {
        int callbackState = -1;
        for (PayReturnCodeEnum anEnum : PayReturnCodeEnum.values()) {
            if (anEnum.getCode().equals(tradeState)) {
                callbackState = anEnum.getPayState();
                break;
            }
        }
        return callbackState;
    }


    public static void main(String[] args) throws Exception {
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("app_id", "2016120804019527");
        paraMap.put("method", "alipay.trade.wap.pay");
        paraMap.put("format", "json");
        paraMap.put("alipay_sdk", "alipay-sdk-java-dynamicVersionNo");

        paraMap.put("return_url", "http://local.axpapp.com");

        paraMap.put("charset", "utf-8");
        paraMap.put("sign_type", "RSA");
        paraMap.put("timestamp", "2017-02-12 14:54:37");
        paraMap.put("version", "1.0");
        paraMap.put("notify_url", "http://local.axpapp.com/moon/cashier/webpay/2.htm");

        // biz_content 业务请求参数的集合
        Map<String, String> bizMap = new HashMap<>();
        //商品名称
        bizMap.put("subject", "服务费");
        //支付流水号
        bizMap.put("out_trade_no", "2017021212044779888");
        //支付总金额
        bizMap.put("total_amount", "0.01");
        //销售产品码
        bizMap.put("product_code", "QUICK_WAP_PAY");

        paraMap.put("biz_content", JsonUtils.translateToJson(bizMap).replace("\"", "\\\""));
        String html = AliPayUtils.buildReqUrl("https://openapi.alipay.com/gateway.do", "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANWsWoldNrCLOd3S24OlfUsaC4LeZrlxT3jEu7M5L9TpNo3rPxaribDTaDZeNDQP1ZudSHdOaSo3x4KcYdi5qg4jH1r5ETV0GY1l3YUoO/NviewUxhMgzNVwZoqSUGtveL9y18M/Jny1aG/ucRKRus9IwJ1UFzQlHuldsx29JZ6HAgMBAAECgYEAvNyt3bKFb4BwMnB41KDG4UXxHMiFla3g58dEfQK0E4XbUY+4YMpYVvJVr5COpeHFFdnsvn+RFt7cusaM+eoJsvyAR4Z93QUq7puaxHP2s44gYtuw4tgT6hvioa41RIcChiPe7AkgK/cL7AIIaZp9q5pWg6iIK/1DUaF8WHzXVUkCQQD8+jmbY8hy0KA5TP7xRaqO96eCgz3ABHyzbC/0KWanfByBZUezhOkT9jYHO3OeskAXfSNATYeWh4gM/mk++yjzAkEA2Dno95WN+koLqSzqiifeF9XtoiibT/NJ5z7tNoKKT5BJV+jgprdqrzH/3AtXsAsrt5W/YsaS4zCPVJhS9hTZHQJBAOnR5bjoK3djuRP9RI6Ak7p80MjiwQpfm1rDHjeQpJ8dKcO3duRIbp3SrfFVU/JUUsTjFtfyUOYi8u7/nwtlXV0CQBWPmLpvcEvf7E+/Sdfi59OKonqEABC12s2zSaYg2Dfc1GNutlAJhBraKoA/pUvJoV9aEE6CLI14/yHZWpRtOcUCQB1/v5z7JPNrNc7ezuHCm4ejHmki101nEAayBEsEirx3ifbsupXmXegPu+vd0tFnLH0AZnnacjtQqtGPIn2gAp8=", paraMap);
        System.out.println(html);
    }
}
