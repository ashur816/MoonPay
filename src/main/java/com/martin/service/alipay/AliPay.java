package com.martin.service.alipay;

import com.martin.bean.PayFlowBean;
import com.martin.bean.PayInfo;
import com.martin.bean.PayResult;
import com.martin.bean.RefundResult;
import com.martin.constant.PayChannelEnum;
import com.martin.constant.PayConstant;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.exception.BusinessException;
import com.martin.service.IPayService;
import com.martin.utils.DateUtils;
import com.martin.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: AliPay
 * @Description: 支付宝
 * @author ZXY
 * @date 2016/5/24 10:22
 */
@Service("aliPayService")
public class AliPay implements IPayService {

    private static final String charset = "UTF-8";
    private Logger logger = LoggerFactory.getLogger(getClass());

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
     * @Description: 支付回调参数校验
     * @param paraMap
     * @return
     * @throws
     */
    @Override
    public PayResult payReturn(Map<String, String> paraMap) throws Exception {
        logger.info("支付回调处理");
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
     * 单笔/批量退款
     * @param flowBeanList
     * @param extMap
     * @return
     */
    @Override
    public PayInfo refund(List<PayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception {
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

        int refundNum = flowBeanList.size();
        //总笔数
        paraMap.put("batch_num", String.valueOf(refundNum));
        //单笔数据集 原付款支付宝交易号^退款总金额^退款理由  第一笔交易退款数据集#第二笔交易退款数据集
        StringBuilder sBuilder = new StringBuilder();
        PayFlowBean flowBean;
        double payAmount;
        for (int i = 0; i < refundNum; i++) {
            flowBean = flowBeanList.get(i);
            payAmount = flowBean.getPayAmount() / 100.0;
            sBuilder.append(flowBean.getThdFlowId()).append("^").append(payAmount).append("^").append(extMap.get("refundReason")).append("#");
        }
        paraMap.put("detail_data", sBuilder.deleteCharAt(sBuilder.length() - 1).toString());

        String sendString = AliPayUtils.buildReqForm(PayConstant.ALIPAY_URL, PayConstant.ALIPAY_MD5_KEY, PayConstant.ALIPAY_SIGN_TYPE, paraMap);
        logger.info("发送退款信息{}", sendString);

        PayInfo payInfo = new PayInfo();
        payInfo.setPayType(PayChannelEnum.ALI_PAY.getPayType());
        payInfo.setRetHtml(sendString);
        return payInfo;
    }

    /**
     * @Description: 退款回调参数校验
     * @param paraMap
     * @return
     * @throws
     */
    @Override
    public List<RefundResult> refundReturn(Map<String, String> paraMap) throws Exception {
        logger.info("支付宝退款回调处理");
        //验签
        returnValidate(paraMap);

        Long batchNo = Long.parseLong(paraMap.get("batch_no"));
        //返回格式 2016072021001004610238752098^0.00^REFUND_TRADE_FEE_ERROR#2016072021001004610238752098^0.00^REFUND_TRADE_FEE_ERROR
        String resultDetails = paraMap.get("result_details");
        List<String> detailList = Arrays.asList(resultDetails.split("#"));
        List<String> resultList;
        String resultMsg;
        RefundResult refundResult = new RefundResult();
        List<RefundResult> refundList = new ArrayList<>();
        for (int i = 0; i < detailList.size(); i++) {
            resultList = Arrays.asList(detailList.get(i).split("\\^"));
            refundResult.setThdFlowId(resultList.get(0));
            refundResult.setThdRefundId(batchNo);
            resultMsg = resultList.get(2);
            if ("SUCCESS".equals(resultMsg)) {
                //退款成功
                refundResult.setPayState(PayConstant.REFUND_SUCCESS);
            } else {
                //退款失败
                refundResult.setPayState(PayConstant.REFUND_FAIL);
                refundResult.setFailCode(resultList.get(2));
                refundResult.setFailDesc("退款失败");
            }
            refundList.add(refundResult);
        }
        return refundList;
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

    /**
     * 回调校验
     */
    private void returnValidate(Map<String, String> paraMap) throws Exception {
        if (paraMap == null || paraMap.size() < 1) {
            //参数不能为空
            throw new BusinessException(null, "参数不能为空");
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
            throw new BusinessException(null, "支付宝回调异常");
        }

        String returnSign = paraMap.get("sign");
        Map<String, String> tmpMap = AliPayUtils.paraFilter(paraMap);
        String mySign = AliPayUtils.buildRequestMySign(PayConstant.ALIPAY_MD5_KEY, PayConstant.ALIPAY_SIGN_TYPE, tmpMap);
        if (!returnSign.equals(mySign)) {
            //支付宝回调签名不匹配
            throw new BusinessException(null, "支付宝回调签名不匹配");
        }
    }
}
