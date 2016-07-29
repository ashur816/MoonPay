package com.martin.service.alipay;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayChannelEnum;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.dto.RefundResult;
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
        paraMap.put("service", PayParam.aliPayService);
        paraMap.put("partner", PayParam.aliPartner);
        paraMap.put("seller_id", PayParam.aliSellerId);
        paraMap.put("_input_charset", PayParam.aliInputCharset);
        paraMap.put("payment_type", PayParam.aliPaymentType);
        paraMap.put("notify_url", PayParam.aliNotifyUrl);
        paraMap.put("return_url", PayParam.aliReturnUrl);
        paraMap.put("anti_phishing_key", PayParam.aliAntiPhishingKey);
        paraMap.put("exter_invoke_ip", PayParam.aliExterInvokeIp);
        //超时时间 支付宝默认1H
        paraMap.put("it_b_pay", PayParam.aliItBPay);
        // ZD流水号
        paraMap.put("out_trade_no", String.valueOf(flowBean.getFlowId()));
        //商品名称
        paraMap.put("subject", PayParam.body);
        //支付总金额
        double payAmount = flowBean.getPayAmount() / 100.0;
        paraMap.put("total_fee", String.valueOf(payAmount));
        paraMap.put("body", PayParam.body);

        String html = AliPayUtils.buildReqForm(PayParam.aliUrl, PayParam.aliMd5Key, PayParam.aliSignType, paraMap);
        PayInfo payInfo = new PayInfo(PayParam.body, payAmount, html);
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
        paraMap.put("service", PayParam.aliRefundService);
        paraMap.put("partner", PayParam.aliPartner);
        paraMap.put("seller_user_id", PayParam.aliSellerId);
        paraMap.put("_input_charset", PayParam.aliInputCharset);
        paraMap.put("sign_type", PayParam.aliSignType);
        paraMap.put("notify_url", PayParam.aliNotifyUrl);
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

        String sendString = AliPayUtils.buildReqForm(PayParam.aliUrl, PayParam.aliMd5Key, PayParam.aliSignType, paraMap);
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

        String batchNo = paraMap.get("batch_no");
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
        paraMap.put("app_id", PayParam.aliAppId);
        paraMap.put("scope", PayParam.aliAuthCode);
        paraMap.put("redirect_uri", PayParam.aliAuthRetUrl);
        paraMap.put("state", bizId);

        String param = AliPayUtils.createLinkString(paraMap);

        PayInfo payInfo = new PayInfo();
        payInfo.setDestUrl(PayParam.aliAuthUrl);
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
            String params = "&partner=" + PayParam.aliPartner + "&notify_id=" + notify_id;
            responseTxt = HttpUtils.sendPost(PayParam.aliVerifyUrl, params, charset);
        }
        if ("false".equalsIgnoreCase(responseTxt)) {
            //支付宝回调异常
            throw new BusinessException(null, "支付宝回调异常");
        }

        String returnSign = paraMap.get("sign");
        Map<String, String> tmpMap = AliPayUtils.paraFilter(paraMap);
        String mySign = AliPayUtils.buildRequestMySign(PayParam.aliMd5Key, PayParam.aliSignType, tmpMap);
        if (!returnSign.equals(mySign)) {
            //支付宝回调签名不匹配
            throw new BusinessException(null, "支付宝回调签名不匹配");
        }
    }
}
