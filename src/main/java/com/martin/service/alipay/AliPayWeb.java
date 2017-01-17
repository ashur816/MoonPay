package com.martin.service.alipay;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.dto.RefundResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayWebService;
import com.martin.utils.DateUtils;
import com.martin.utils.JsonUtils;
import com.martin.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

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
     * 生成支付信息
     *
     * @param flowBean
     * @return
     */
    @Override
    public PayInfo buildPayInfo(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始支付宝web支付");
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("service", PayParam.aliPayService);
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
     * 批量退款，兼容单个
     *
     * @param flowBeanList
     * @param extMap
     * @return
     */
    @Override
    public PayInfo refund(List<PayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception {
        logger.info("开始支付宝退款-{}", extMap);
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("service", PayParam.aliRefundService);
        paraMap.put("partner", PayParam.aliPartnerId);
        paraMap.put("seller_user_id", PayParam.aliPartnerId);
        paraMap.put("_input_charset", PayParam.aliInputCharset);
        paraMap.put("sign_type", PayParam.aliWebSignType);
        paraMap.put("notify_url", PayParam.aliRefundNotifyUrl);
        //退款时间 格式为：yyyy-MM-dd HH:mm:ss
        paraMap.put("refund_date", DateUtils.formatDateTime(new Date()));
        //退款批次号
        paraMap.put("batch_no", extMap.get("batchNo"));

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

        String sendString = AliPayUtils.buildReqForm(PayParam.aliMapiUrl, PayParam.aliMD5Key, PayParam.aliWebSignType, paraMap);
        logger.info("发送退款信息{}", sendString);

        PayInfo payInfo = new PayInfo();
        payInfo.setPayType(PayConstant.PAY_TYPE_ALI);
        payInfo.setRetHtml(sendString);
        return payInfo;
    }

    /**
     * @param paraMap
     * @return
     * @throws
     * @Description: 退款回调参数校验
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
        RefundResult refundResult;
        List<RefundResult> refundList = new ArrayList<>();
        for (int i = 0; i < detailList.size(); i++) {
            resultList = Arrays.asList(detailList.get(i).split("\\^"));

            refundResult = new RefundResult();
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
}
