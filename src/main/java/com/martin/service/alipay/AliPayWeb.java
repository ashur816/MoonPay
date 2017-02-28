package com.martin.service.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.service.IPayWebService;
import com.martin.utils.DateUtils;
import com.martin.utils.JsonUtils;
import com.martin.utils.ObjectUtils;
import com.martin.utils.PayUtils;
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

        String param = PayUtils.buildConcatStr(paraMap);

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
        PayInfo p = build1(flowBean, extMap);
        return p;
    }

    /**
     * 老版生成支付信息 MD5加密
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
        paraMap.put("_input_charset", PayParam.inputCharset);
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

        String html = AliPayUtils.buildReqForm(PayParam.aliMapiUrl, PayParam.aliMD5Key, PayParam.aliSignTypeMD5, paraMap);
        return new PayInfo(PayParam.webBody, needPayAmount, html);
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 新版web支付，自己组装参数 RSA加密
     */
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
        bizMap.put("out_trade_no", String.valueOf(flowBean.getFlowId()));
        //支付总金额
        double needPayAmount = flowBean.getTotalAmount() / 100.0;
        bizMap.put("total_amount", String.valueOf(needPayAmount));
        //销售产品码
        bizMap.put("product_code", "QUICK_WAP_PAY");

        paraMap.put("biz_content", JsonUtils.translateToJson(bizMap));
        String html = AliPayUtils.buildReqUrl(PayParam.aliOpenUrl, PayParam.aliWebPrivateKey, paraMap);
        return new PayInfo(PayParam.webBody, needPayAmount, html);
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 新版web支付，SDK方式 RSA加密
     */
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
        bizMap.put("out_trade_no", String.valueOf(flowBean.getFlowId()));
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
        String singType = paraMap.get("sign_type");
        if(PayParam.aliSignTypeMD5.equalsIgnoreCase(singType)){
            AliPayUtils.returnValidate(PayParam.aliMD5Key, paraMap);
        }
        else {
            AliPayUtils.returnValidate(PayParam.aliWebPrivateKey, paraMap);
        }

        PayResult payResult = new PayResult();
        // 支付流水ID
        payResult.setFlowId(Long.valueOf(paraMap.get("out_trade_no")));
        // 支付宝交易流水号
        payResult.setThdFlowId(paraMap.get("trade_no"));
        //交易状态
        String tradeState = paraMap.get("trade_status");
        payResult.setTradeState(tradeState);

        int callbackState = PayUtils.transPayState(tradeState);
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
        payResult.setPayState(PayUtils.transPayState(code));
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
        paraMap.put("sign_type", PayParam.aliSignTypeRSA);//只支持RSA
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
}
