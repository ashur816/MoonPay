package com.martin.service.tenpay;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.dto.PayResult;
import com.martin.dto.RefundResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayAppService;
import com.martin.service.IPayFlow;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ZXY
 * @ClassName: SdkTenPay
 * @Description: 微信APP支付类
 */
@Service("tenPayAppService")
public class TenPayApp implements IPayAppService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IPayFlow payFlow;

    /**
     * 生成预定单给微信支付网关，返回
     *
     * @param flowBean
     * @param extMap   中包含 appId、mchId、ipAddress、privateKey
     * @return
     */
    @Override
    public Map<String, String> buildPayInfo(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始APP微信支付");

        String appId = extMap.get("appId");
        String mchId = extMap.get("mchId");
        String privateKey = extMap.get("privateKey");
        //组装参数返回给前台
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", appId);
        paraMap.put("mch_id", mchId);
        //商品或支付单简要描述
        paraMap.put("body", PayParam.appBody);
        paraMap.put("limit_pay", PayParam.tenLimitPay);
        paraMap.put("notify_url", PayParam.tenAppNotifyUrl);

        paraMap.put("trade_type", PayParam.tenAppTradeType);
        paraMap.put("spbill_create_ip", extMap.get("ipAddress"));

        // ZD流水号
        Long flowId = flowBean.getFlowId();
        paraMap.put("out_trade_no", String.valueOf(flowId));

        //支付总金额
        paraMap.put("total_fee", String.valueOf(flowBean.getPayAmount()));
        //生成统一下单信息
        String xml = TenPayUtils.createRequestXml(privateKey, paraMap);
        logger.info("APP支付统一下单xml为:\n" + xml);

        //发送给微信支付生成预订单
        String returnXml = TenPayUtils.sendPostXml(PayParam.tenOrderUrl, xml);
        logger.info("APP支付返回结果:" + returnXml);

        //转换返回xml结果
        SortedMap<String, String> returnMap = TenPayUtils.getMapFromXML(returnXml);

        String returnCode = returnMap.get("return_code");
        String resultCode = returnMap.get("result_code");
        String returnMsg = returnMap.get("return_msg");
        String errCode = returnMap.get("err_code");
        String errDes = returnMap.get("err_code_des");

        //预订单号是否已经生成，且未失效  该值有效期为2小时
        String prepayId = "";

        LinkedHashMap<String, String> tmpMap = new LinkedHashMap<>();
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            prepayId = returnMap.get("prepay_id");
            //更新到流水表中
            payFlow.updateThdFlowId(flowId, prepayId);

            //插入顺序不能变
            tmpMap.put("appid", appId);
            tmpMap.put("noncestr", TenPayUtils.createNonceStr());
            tmpMap.put("package", "Sign=WXPay");
            tmpMap.put("partnerid", mchId);
            tmpMap.put("prepayid", prepayId);
            tmpMap.put("timestamp", Long.toString(new Date().getTime()));
            String sign = TenPayAppUtils.createSdkSign(privateKey, tmpMap);
            tmpMap.put("sign", sign);
        } else if (PayReturnCodeEnum.TENPAY_OUT_TRADE_NO_USED.getCode().equals(errCode)) {//订单号重复,直接返回原订单信息
            //查询原订单
            PayFlowBean oldBean = payFlow.getPayFlowById(flowId, PayConstant.ALL_PAY_STATE);
            prepayId = oldBean.getThdFlowId();

            //插入顺序不能变
            tmpMap.put("appid", appId);
            tmpMap.put("noncestr", TenPayUtils.createNonceStr());
            tmpMap.put("package", "Sign=WXPay");
            tmpMap.put("partnerid", mchId);
            tmpMap.put("prepayid", prepayId);
            tmpMap.put("timestamp", Long.toString(new Date().getTime()));
            String sign = TenPayAppUtils.createSdkSign(privateKey, tmpMap);
            tmpMap.put("sign", sign);
        } else if (PayReturnCodeEnum.TENPAY_ORDERPAID.getCode().equals(errCode)) {
            //订单已支付
            throw new BusinessException("订单已支付");
        } else {
            logger.info("APP支付微信预下单失败：{}", StringUtils.isNotBlank(returnMsg) ? returnMsg : errDes);
            //微信支付预下单失败
            throw new BusinessException("微信支付预下单失败");
        }

        //支付总金额
        return tmpMap;
    }

    /**
     * @param paraMap
     * @return
     * @throws
     * @Description: 支付回调先取出flowId
     */
    @Override
    public long getReturnFlowId(Map<String, String> paraMap) throws Exception {
        String tmpXml = paraMap.get("content");
        SortedMap<String, String> sortedMap = TenPayUtils.getMapFromXML(tmpXml);
        if (sortedMap == null || sortedMap.size() < 1) {
            //参数不能为空
            throw new BusinessException("参数不能为空");
        }
        long flowId = 0L;
        String outTradeNo = sortedMap.get("out_trade_no");
        if (!StringUtils.isEmpty(outTradeNo)) {
            flowId = Long.valueOf(outTradeNo);
        } else {
            //未查询到支付流水信息
            throw new BusinessException("未查询到支付流水信息");
        }
        return flowId;
    }

    /**
     * @param paraMap
     * @return
     * @throws
     * @Description: 支付回调参数校验
     */
    @Override
    public PayResult payReturn(String privateKey, Map<String, String> paraMap) throws Exception {
        logger.info("开始APP微信回调处理");
        SortedMap<String, String> sortedMap = returnValidate(paraMap);
        String resultCode = sortedMap.get("result_code");
        String returnCode = sortedMap.get("return_code");
        String tradeState = sortedMap.get("trade_state");

        PayResult payResult = new PayResult();
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            //支付结果
            payResult.setTradeState(tradeState);
            // 支付流水ID
            String tradeNo = sortedMap.get("out_trade_no");
            payResult.setFlowId(Long.valueOf(tradeNo));
            // 微信交易流水号
            payResult.setThdFlowId(sortedMap.get("transaction_id"));
            //错误代码
            payResult.setFailCode(sortedMap.get("err_code"));
            //错误代码描述
            payResult.setFailDesc(sortedMap.get("err_code_des"));

            if (StringUtils.isEmpty(tradeState)) {//支付成功时，微信不回传 trade_state，查询订单时会回传 trade_state
                tradeState = "SUCCESS";
            }
            int callbackState = transPayState(tradeState);
            payResult.setPayState(callbackState);
        }
        logger.info("APP微信回调处理成功");
        return payResult;
    }

    /**
     * @param flowId
     * @return
     * @throws
     * @Description: 查询第三方支付状态
     */
    @Override
    public PayResult getPayStatus(Long flowId, Map<String, String> extMap) throws Exception {
        logger.info("开始APP微信查单");
        String appId = extMap.get("appId");
        String mchId = extMap.get("mchId");
        String privateKey = extMap.get("privateKey");
        //组装参数返回给前台
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", appId);
        //商品或支付单简要描述
        paraMap.put("mch_id", mchId);

        // ZD流水号
        paraMap.put("out_trade_no", String.valueOf(flowId));

        //生成信息
        String xml = TenPayUtils.createRequestXml(privateKey, paraMap);
        logger.info("APP微信查单xml为:\n" + xml);

        //发送给微信支付
        String returnXml = TenPayUtils.sendPostXml(PayParam.tenQueryUrl, xml);
        logger.info("APP微信查单返回结果:" + returnXml);
        //转换返回xml结果
        SortedMap<String, String> returnMap = TenPayUtils.getMapFromXML(returnXml);

        String returnCode = returnMap.get("return_code");
        String resultCode = returnMap.get("result_code");

        PayResult payResult = new PayResult();
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            String tradeState = returnMap.get("trade_state");
            String thdFlowId = returnMap.get("transaction_id");
            payResult.setPayState(transPayState(tradeState));
            payResult.setThdFlowId(thdFlowId);
        } else {
            payResult.setPayState(PayConstant.PAY_NOT);
        }
        return payResult;
    }

    /**
     * @param flowId
     * @return
     * @throws
     * @Description: 关闭第三方支付订单
     */
    @Override
    public void closeThdPay(Long flowId, Map<String, String> extMap) throws Exception {
        logger.info("开始APP微信关单");
        //组装参数返回给前台
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", PayParam.tenAppAppId);
        //商品或支付单简要描述
        paraMap.put("mch_id", PayParam.tenAppMchId);

        // ZD流水号
        paraMap.put("out_trade_no", String.valueOf(flowId));

        //生成信息
        String xml = TenPayUtils.createRequestXml(PayParam.tenAppPrivateKey, paraMap);
        logger.info("APP微信关单xml为:\n" + xml);

        //发送给微信支付
        String returnXml = TenPayUtils.sendPostXml(PayParam.tenQueryUrl, xml);
        logger.info("APP微信关单返回结果:" + returnXml);
        //转换返回xml结果
        SortedMap<String, String> returnMap = TenPayUtils.getMapFromXML(returnXml);

        String returnCode = returnMap.get("return_code");
        String resultCode = returnMap.get("result_code");
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {

        } else {
            String errCode = returnMap.get("err_code");
            //关闭失败
            logger.info("APP微信关单失败-{}", errCode);
        }
    }

    /**
     * 批量退款，兼容单个
     *
     * @param flowBeanList
     * @param extMap
     * @return
     */
    @Override
    public Object refund(List<PayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception {
        logger.info("APP微信退款-{}", extMap);
        PayFlowBean flowBean = flowBeanList.get(0);
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", PayParam.tenWebAppId);
        paraMap.put("mch_id", PayParam.tenWebMchId);
        paraMap.put("op_user_id", PayParam.tenWebMchId);
        paraMap.put("refund_account", PayParam.refundAccount);
        //微信订单号
        paraMap.put("transaction_id", flowBean.getThdFlowId());
        //商户退款单号
        paraMap.put("out_refund_no", extMap.get("refundId"));

        String payAmount = String.valueOf(flowBean.getPayAmount());
        paraMap.put("total_fee", payAmount);
        paraMap.put("refund_fee", payAmount);

        //生成信息
        String xml = TenPayUtils.createRequestXml(PayParam.tenWebPrivateKey, paraMap);
        logger.info("退款发送xml为:\n" + xml);

        //发送给微信支付
        String returnXml = TenPayUtils.sendPostWithCert(PayParam.tenRefundUrl, xml, "UTF-8");
        logger.info("退款返回结果:" + returnXml);

        Map tmpMap = new HashMap();
        tmpMap.put("content", returnXml);
        List<RefundResult> refundResults = refundReturn(tmpMap);

        return refundResults.size() > 0 ? refundResults.get(0) : null;
    }

    /**
     * @Description: 退款回调参数校验
     * @param paraMap
     * @return
     * @throws
     */
    private List<RefundResult> refundReturn(Map<String, String> paraMap) throws Exception {
        logger.info("APP微信退款回调处理");
        SortedMap<String, String> sortedMap = returnValidate(paraMap);

        String resultCode = sortedMap.get("result_code");
        String returnCode = sortedMap.get("return_code");
        String tradeState = sortedMap.get("trade_state");

        List<RefundResult> refundResults = new ArrayList<>();
        RefundResult refundResult = new RefundResult();
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            //支付结果
            refundResult.setTradeState(tradeState);
            // 支付流水ID
            String tradeNo = sortedMap.get("out_trade_no");
            refundResult.setFlowId(Long.valueOf(tradeNo));
            // 原第三方支付流水
            refundResult.setThdFlowId(sortedMap.get("transaction_id"));
            // 微信退款流水号
            refundResult.setThdRefundId(sortedMap.get("refund_id"));
            //错误代码
            refundResult.setFailCode(sortedMap.get("err_code"));
            //错误代码描述
            refundResult.setFailDesc(sortedMap.get("err_code_des"));

            refundResult.setPayState(PayConstant.REFUND_SUCCESS);
        } else {
            //支付结果
            refundResult.setPayState(PayConstant.REFUND_FAIL);
            //错误代码
            refundResult.setFailCode(sortedMap.get("err_code"));
            //错误代码描述
            refundResult.setFailDesc(sortedMap.get("err_code_des"));
        }
        refundResults.add(refundResult);
        return refundResults;
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

    /**
     * 验签
     */
    private SortedMap<String, String> returnValidate(Map<String, String> paraMap) throws Exception {
        String tmpXml = paraMap.get("content");
        SortedMap<String, String> sortedMap = TenPayUtils.getMapFromXML(tmpXml);

        if (sortedMap == null || sortedMap.size() < 1) {
            //参数不能为空
            throw new BusinessException("参数不能为空");
        }

        String returnSign = sortedMap.get("sign");
        String mySign = TenPayUtils.createSign(PayParam.tenAppPrivateKey, sortedMap);
        if (!returnSign.equals(mySign)) {
            //回调签名不匹配
            throw new BusinessException("回调签名不匹配");
        }
        return sortedMap;
    }
}
