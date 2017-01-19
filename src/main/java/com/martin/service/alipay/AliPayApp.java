package com.martin.service.alipay;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.dto.PayResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayAppService;
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
 * @ClassName: AliPay
 * @Description: 支付宝
 * @author ZXY
 * @date 2016/5/24 10:22
 */
@Service("aliPayAppService")
public class AliPayApp implements IPayAppService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 生成支付信息
     * @param flowBean
     * @return
     */
    @Override
    public Map<String, String> buildPayInfo(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始App支付宝支付");
        String appId = extMap.get("appId");
        String privateKey = extMap.get("privateKey");
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("app_id", appId);
        paraMap.put("method", "alipay.trade.app.pay");
        paraMap.put("charset", "utf-8");
        paraMap.put("sign_type", PayParam.aliAppSignType);
        paraMap.put("timestamp", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        paraMap.put("version", "1.0");
        paraMap.put("notify_url", PayParam.aliAppNotifyUrl);

        // biz_content 业务请求参数的集合 subject out_trade_no total_amount product_code passback_params
        Map<String, String> bizMap = new HashMap<>();
        //商品名称
        bizMap.put("body", PayParam.appBody);
        bizMap.put("subject", PayParam.appBody);
        //支付流水号
        bizMap.put("out_trade_no", String.valueOf(flowBean.getFlowId()));
        bizMap.put("timeout_express", "30m");
        //支付总金额
        double payAmount = flowBean.getPayAmount() / 100.0;
        bizMap.put("total_amount", String.valueOf(payAmount));
        bizMap.put("product_code", "QUICK_MSECURITY_PAY");

        paraMap.put("biz_content", JsonUtils.translateToJson(bizMap));

        Map<String, String> tmpMap = AliPayAppUtils.createAliPayOrder(privateKey, paraMap);
        return tmpMap;
    }

    /**
     * @Description: 支付回调先取出flowId
     * @param paraMap

     * @return
     * @throws
     */
    @Override
    public long getReturnFlowId(Map<String, String> paraMap) throws Exception {
        long flowId;
        String outTradeNo = paraMap.get("out_trade_no");
        if (!StringUtils.isEmpty(outTradeNo)) {
            flowId = Long.valueOf(outTradeNo);
        } else {
            //未查询到支付流水信息
            throw new BusinessException("未查询到支付流水信息");
        }
        return flowId;
    }

    /**
     * @Description: 支付回调参数校验
     * @param paraMap
     * @return
     * @throws
     */
    @Override
    public PayResult payReturn(String privateKey, Map<String, String> paraMap) throws Exception {
        logger.info("开始APP支付宝回调处理");
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
        logger.info("APP支付宝回调处理成功");

        return payResult;
    }

    /**
     * @Description: 查询第三方支付状态
     * @param flowId
     * @return
     * @throws
     */
    @Override
    public PayResult getPayStatus(Long flowId, Map<String, String> extMap) throws Exception {
        logger.info("开始App支付宝查单");
        String appId = extMap.get("appId");
        String privateKey = extMap.get("privateKey");
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("app_id", appId);
        paraMap.put("method", PayParam.aliQueryService);
        paraMap.put("charset", "utf-8");
        paraMap.put("sign_type", PayParam.aliAppSignType);//只支持RSA
        paraMap.put("timestamp", DateUtils.format(new Date(), "yyyyMMddHHmmss"));
        paraMap.put("version", "1.0");

        // biz_content 业务请求参数的集合
        Map<String, String> bizMap = new HashMap<>();
        //支付流水号
        bizMap.put("out_trade_no", String.valueOf(flowId));

        paraMap.put("biz_content", JsonUtils.translateToJson(bizMap));

        String tmpString = AliPayAppUtils.sendPost(PayParam.aliOpenUrl, privateKey, paraMap);
        //解析返回
        Map tmpMap = JsonUtils.readMap(tmpString);
        Map returnMap = (Map) tmpMap.get("alipay_trade_query_response");
        Object subCode = returnMap.get("sub_code");
        Object tradeStatus = returnMap.get("trade_status");
        String code = "WAIT_BUYER_PAY";
        if (ObjectUtils.isNotEmpty(subCode)) {
            code = subCode.toString();
        } else if (ObjectUtils.isNotEmpty(tradeStatus)) {
            code = tradeStatus.toString();
        }
        logger.info("WEB支付宝查单结果-{}", code);
        PayResult payResult = new PayResult();
        payResult.setPayState(transPayState(code));
        if(PayConstant.PAY_SUCCESS == payResult.getPayState()){
            //支付成功的更新第三方交易流水号
            payResult.setThdFlowId(returnMap.get("trade_no").toString());
        }
        return payResult;
    }

    /**
     * @Description: 关闭第三方支付订单 只有等待买家付款状态下才能发起交易关闭
     * @param flowId
     * @return
     * @throws
     */
    @Override
    public void closeThdPay(Long flowId, Map<String, String> extMap) throws Exception {
        logger.info("开始APP支付宝关单");
        //组装参数
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("app_id", PayParam.aliAppAppId);
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

        String tmpString = AliPayAppUtils.sendPost(PayParam.aliOpenUrl, PayParam.aliAppPrivateKey, paraMap);
        //解析返回
        Map tmpMap = JsonUtils.readMap(tmpString);
        Map returnMap = (Map) tmpMap.get("alipay_trade_close_response");
        Object msg = returnMap.get("msg");
        Object subCode = returnMap.get("sub_code");
        if (ObjectUtils.isNotEmpty(msg) && "SUCCESS".equalsIgnoreCase(msg.toString())) {
            //关闭成功
            logger.info("APP支付宝关单成功-{}", msg);
        }
        else {
            //关闭失败
            logger.info("APP支付宝关单失败-{}", subCode);
        }
    }

    /**
     * @Description: 回调验签
     * @param
     * @return
     * @throws
     */
    private void returnValidate(Map<String, String> paraMap) throws Exception {
        logger.info("开始APP支付宝验签处理");
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

        String aliSign = paraMap.get("sign");
        paraMap.remove("sign");
        paraMap.remove("sign_type");
        paraMap.remove("content");

//        //验签字符串
//        String content = paraMap.get("content");
//        //去除 sign 和 sign_type
//        replaceParamReg(content, "sign");
//        replaceParamReg(content, "sign_type");

//        content = URLDecoder.decode(content,"UTF-8");
        Map<String, String> tmpMap = new HashMap<>();


        boolean signResult = AliPayAppUtils.checkBackSign(paraMap, PayParam.aliAliPublicKey, aliSign);
        if (!signResult) {
            //支付宝回调签名不匹配
            throw new BusinessException("支付宝回调签名不匹配");
        }
        logger.info("APP支付宝验签处理完成");
    }

    /**
     * 转换支付状态
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

    /**
     * 去除url中指定参数
     * @return
     */
    public static String replaceParamReg(String url, String name) {
        if (!StringUtils.isEmpty(url)) {
            String reg = "(" + name + "=[^&]*)";//单个参数
            String reg1 = "(&&)";//去除双&&
            url = url.replaceAll(reg, "");
            url = url.replaceAll(reg1, "&");
        }
        return url;
    }
}
