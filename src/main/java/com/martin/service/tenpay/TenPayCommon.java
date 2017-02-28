package com.martin.service.tenpay;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.dto.PayResult;
import com.martin.dto.RefundResult;
import com.martin.dto.TransferResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayCommonService;
import com.martin.utils.JsonUtils;
import com.martin.utils.PayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ZXY
 * @ClassName: WeiXinPay
 * @Description: 微信公众号支付类
 * @date 2016/5/24 10:31
 */
@Service("tenPayCommonService")
public class TenPayCommon implements IPayCommonService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 单个企业付款
     *
     * @param flowBean
     * @param extMap
     * @return
     */
    @Override
    public Object transferBatch(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        return transferSingle(flowBean, extMap);
    }

    /**
     * 单个账户转账
     *
     * @param flowBean
     * @param extMap
     * @return
     */
    @Override
    public Object transferSingle(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("mch_appid", PayParam.tenWebAppId);
        paraMap.put("mchid", PayParam.tenWebMchId);
        paraMap.put("nonce_str", TenPayUtils.createNonceStr());
        paraMap.put("partner_trade_no", String.valueOf(flowBean.getFlowId()));
        paraMap.put("openid", extMap.get("openId"));

        //NO_CHECK：不校验真实姓名
        //FORCE_CHECK：强校验真实姓名（未绑卡用户会校验失败，无法转账）
        //OPTION_CHECK：针对已绑卡的用户校验真实姓名（未绑卡用户不校验）
        paraMap.put("check_name", "OPTION_CHECK");
        paraMap.put("re_user_name", extMap.get("payeeName"));
        paraMap.put("amount", String.valueOf(flowBean.getPayAmount()));
        paraMap.put("desc", extMap.get("transferReason"));
        paraMap.put("spbill_create_ip", extMap.get("ipAddress"));

        //生成信息
        String xml = TenPayUtils.createRequestXml(PayParam.tenWebPrivateKey, paraMap);
        logger.info("发送xml为:\n" + xml);

        //发送给微信支付
        String returnXml = TenPayUtils.sendPostWithCert(PayParam.tenTransferUrl, xml, PayParam.inputCharset);
        logger.info("返回结果:" + returnXml);

        //转换返回xml结果
        SortedMap<String, String> returnMap = TenPayUtils.getMapFromXML(returnXml);

        String returnCode = returnMap.get("return_code");
        String resultCode = returnMap.get("result_code");
        String returnMsg = returnMap.get("return_msg");
        String errCode = returnMap.get("err_code");
        String errDes = returnMap.get("err_code_des");

        PayResult payResult = new PayResult();
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            //微信支付完后返回的微信订单号
            payResult.setTradeState("SUCCESS");
            payResult.setThdFlowId(returnMap.get("payment_no"));
        } else {
            String err = !StringUtils.isEmpty(errDes) ? errDes : returnMsg;
            payResult.setFailDesc(err);
            payResult.setFailCode(errCode);
            payResult.setTradeState("FAIL");
            logger.info("微信企业付款失败：{}", err);
        }

        return payResult;
    }

    /**
     * @param paraMap
     * @return
     * @throws
     * @Description: 企业付款返回信息
     */
    @Override
    public List<TransferResult> transferReturn(Map<String, String> paraMap) throws Exception {
        return null;
    }

    /**
     * 批量退款，兼容单个
     *
     * @param flowBeanList
     * @param extMap
     * @return
     */
    @Override
    public Object refund(String clientSource, List<PayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception {
        Map<String, String> paySourceMap = PayUtils.getPaySource(PayConstant.PAY_TYPE_TEN, clientSource);
        String appId = paySourceMap.get("appId");
        String privateKey = paySourceMap.get("privateKey");
        String mchId = paySourceMap.get("mchId");

        logger.info("开始微信退款-{}", extMap);
        PayFlowBean flowBean = flowBeanList.get(0);
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", appId);
        paraMap.put("mch_id", mchId);
        paraMap.put("op_user_id", mchId);
        paraMap.put("refund_account", PayParam.refundAccount);
        //微信订单号
        paraMap.put("transaction_id", flowBean.getThdFlowId());
        //商户退款单号
        paraMap.put("out_refund_no", extMap.get("refundId"));

        String payAmount = String.valueOf(flowBean.getPayAmount());
        paraMap.put("total_fee", payAmount);
        paraMap.put("refund_fee", payAmount);

        //生成信息
        String xml = TenPayUtils.createRequestXml(privateKey, paraMap);
        logger.info("退款发送xml为:\n" + xml);

        //发送给微信支付
        String returnXml = TenPayUtils.sendPostWithCert(PayParam.tenRefundUrl, xml, PayParam.inputCharset);
        logger.info("退款返回结果:" + returnXml);

        Map tmpMap = new HashMap();
        tmpMap.put("content", returnXml);
        List<RefundResult> refundResults = refundReturn(tmpMap);

        return refundResults.size() > 0 ? refundResults.get(0) : null;
    }

    /**
     * @param paraMap
     * @return
     * @throws
     * @Description: 退款返回信息
     */
    @Override
    public List<RefundResult> refundReturn(Map<String, String> paraMap) throws Exception {
        logger.info("WEB微信退款回调处理");
        SortedMap<String, String> sortedMap = TenPayUtils.returnValidate(PayParam.tenWebPrivateKey, paraMap);

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
     * 微信获得openid
     *
     * @param code 微信用户token
     * @return
     */
    public String getOpenId(String code) throws Exception {
        StringBuilder sb = new StringBuilder();
        // 获取微信 access_token/openid
        sb.append("&appid=" + PayParam.tenWebAppId + "&secret=" + PayParam.tenAppSecret + "&code=" + code + "&grant_type=" + "authorization_code");
        String result = TenPayUtils.sendPostXml("https://api.weixin.qq.com/sns/oauth2/access_token", sb.toString());
        if (StringUtils.isEmpty(result)) {
            throw new BusinessException("未关注微信公众号");
        }
        return JsonUtils.readValueByName(result, "openid");
    }
}
