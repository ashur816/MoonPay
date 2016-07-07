package com.martin.service.alipay;

import com.martin.bean.PayFlowBean;
import com.martin.bean.PayInfo;
import com.martin.bean.PayResult;
import com.martin.constant.PayConstant;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.exception.BusinessException;
import com.martin.service.IPayService;
import com.martin.utils.DateUtils;
import com.martin.utils.HttpUtils;
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
@Service("aliPayService")
public class AliPay implements IPayService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String charset = "UTF-8";

    /**
     * 生成支付信息
     * @param flowBean
     * @return
     */
    @Override
    public PayInfo buildPayInfo(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始支付宝支付");
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("service", PayConstant.ALIPAY_PAY_SERVICE);
        paraMap.put("partner", PayConstant.ALIPAY_PARTNER);
        paraMap.put("seller_id", PayConstant.ALIPAY_SELLER_ID);
        paraMap.put("_input_charset", PayConstant.ALIPAY_INPUT_CHARSET);
        paraMap.put("payment_type", PayConstant.ALIPAY_PAYMENT_TYPE);
        paraMap.put("notify_url", PayConstant.ALIPAY_NOTIFY_URL);
        paraMap.put("return_url", PayConstant.ALIPAY_RETURN_URL);
        paraMap.put("anti_phishing_key", PayConstant.ALIPAY_ANTI_PHISHING_KEY);
        paraMap.put("exter_invoke_ip", PayConstant.ALIPAY_EXTER_INVOKE_IP);
        //超时时间 支付宝默认1H
        paraMap.put("it_b_pay", PayConstant.ALIPAY_IT_B_PAY);
        // ZD流水号
        paraMap.put("out_trade_no", String.valueOf(flowBean.getFlowId()));
        //商品名称
        paraMap.put("subject", PayConstant.BODY);
        //支付总金额
        double payAmount = flowBean.getPayAmount() / 100.0;
        paraMap.put("total_fee", String.valueOf(payAmount));
        paraMap.put("body", PayConstant.BODY);

        String html = AliPayUtils.buildReqForm(PayConstant.ALIPAY_URL, PayConstant.ALIPAY_MD5_KEY, PayConstant.ALIPAY_SIGN_TYPE, paraMap);
        PayInfo payInfo = new PayInfo(PayConstant.BODY, payAmount, html);
        return payInfo;
    }

    /**
     * @Description: 回调参数校验 支付宝 TRADE_FINISHED 和 TRADE_SUCCESS 才会回调通知
     * @param paraMap
     * @return
     * @throws
     */
    @Override
    public PayResult returnValidate(String notifyType, Map<String, String> paraMap) throws Exception {
        PayResult payResult = new PayResult();
        if (paraMap == null || paraMap.size() < 1) {
            //参数不能为空
            throw new BusinessException("111");
        }

        //判断responseTxt是否为true，isSign是否为true
        //responseTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        String responseTxt = "false";
        if (paraMap.get("notify_id") != null) {
            String notify_id = paraMap.get("notify_id");
            String params = "&partner=" + PayConstant.ALIPAY_PARTNER + "&notify_id=" + notify_id;
            responseTxt = HttpUtils.sendPost(PayConstant.ALIPAY_VERIFY_URL, params, charset);
        }
        if ("false".equalsIgnoreCase(responseTxt)) {
            //支付宝回调异常
            throw new BusinessException("09023");
        }

        String returnSign = paraMap.get("sign");
        Map<String, String> tmpMap = AliPayUtils.paraFilter(paraMap);
        String mySign = AliPayUtils.buildRequestMySign(PayConstant.ALIPAY_MD5_KEY, PayConstant.ALIPAY_SIGN_TYPE, tmpMap);
        if (!returnSign.equals(mySign)) {
            //支付宝回调签名不匹配
            throw new BusinessException("09024");
        }

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
     * 退款
     * @param flowBean
     * @param extMap
     * @return
     */
    @Override
    public PayResult refund(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始支付宝退款");
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("service", PayConstant.ALIPAY_REFUND_SERVICE);
        paraMap.put("partner", PayConstant.ALIPAY_PARTNER);
        paraMap.put("seller_user_id", PayConstant.ALIPAY_SELLER_ID);
        paraMap.put("_input_charset", PayConstant.ALIPAY_INPUT_CHARSET);
        paraMap.put("sign_type", PayConstant.ALIPAY_SIGN_TYPE);
        paraMap.put("notify_url", PayConstant.ALIPAY_REFUND_URL);
        //退款时间 格式为：yyyy-MM-dd HH:mm:ss
        paraMap.put("refund_date", DateUtils.formatDateTime(new Date()));
        //退款批次号
        paraMap.put("batch_no", extMap.get("refundId"));
        //总笔数
        paraMap.put("batch_num", "1");
        //单笔数据集 原付款支付宝交易号^退款总金额^退款理由  第一笔交易退款数据集#第二笔交易退款数据集
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(flowBean.getThdFlowId()).append("^").append(flowBean.getPayAmount()).append("^").append(extMap.get("refundReason"));
        paraMap.put("detail_data", sBuilder.toString());

        String sendString = AliPayUtils.buildRequestInfo(PayConstant.ALIPAY_MD5_KEY, PayConstant.ALIPAY_SIGN_TYPE, paraMap);
        logger.info("发送退款信息{}", sendString);
        String reString = HttpUtils.sendPost(PayConstant.ALIPAY_URL, sendString, charset);
        logger.info("返回结果:" + reString);
        PayResult payResult = new PayResult();
        return payResult;
    }

    /**
     * 提现
     * @param flowBean
     * @return
     */
    @Override
    public PayResult withdraw(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        return null;
    }

    /**
     * 获取第三方支付信息
     * @param flowBean
     * @return
     */
    @Override
    public PayResult getPayInfo(PayFlowBean flowBean) throws Exception {
        return null;
    }

    /**
     * 预授权
     * @param bizId
     * @return
     */
    @Override
    public PayInfo authorize(String bizId) throws Exception {
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("app_id", PayConstant.ALIPAY_APP_ID);
        paraMap.put("scope", PayConstant.ALIPAY_AUTH_CODE);
        paraMap.put("redirect_uri", PayConstant.ALIPAY_AUTH_RET_URL);
        paraMap.put("state", bizId);

        String param = AliPayUtils.createLinkString(paraMap);

        PayInfo payInfo = new PayInfo();
        payInfo.setDestUrl(PayConstant.ALIPAY_AUTH_URL);
        payInfo.setDestParam(param);

        return payInfo;
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
}
