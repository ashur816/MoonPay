package com.martin.service.tenpay;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayChannelEnum;
import com.martin.constant.PayParam;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.dto.RefundResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayService;
import com.martin.utils.HttpUtils;
import com.martin.utils.JsonUtils;
import com.martin.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: WeiXinPay
 * @Description: 微信公众号支付类
 * @author ZXY
 * @date 2016/5/24 10:31
 */
@Service("tenPayService")
public class TenPay implements IPayService {

    private static final String charset = "UTF-8";
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 生成预定单给微信支付网关，返回
     * @param flowBean
     * @return
     */
    @Override
    public PayInfo buildPayInfo(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始微信支付");
        //组装参数返回给前台
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", PayParam.tenAppId);
        //商品或支付单简要描述
        paraMap.put("body", PayParam.body);
        paraMap.put("attach", "微信支付");
        paraMap.put("device_info", PayParam.tenDeviceInfo);
        paraMap.put("limit_pay", PayParam.tenLimitPay);
        paraMap.put("mch_id", PayParam.tenMchId);
        paraMap.put("notify_url", PayParam.tenNotifyUrl);
        paraMap.put("return_url", PayParam.tenReturnUrl);
        paraMap.put("spbill_create_ip", extMap.get("ipAddress"));

        String code = extMap.get("code");
        if(StringUtils.isBlank(code)){
            paraMap.put("trade_type", "MWEB");
//            paraMap.put("mweb_url", PayParam.tenReturnUrl);
        }
        else {
            //用户id
            String openId = getOpenId(code);
            logger.info("openId={}", openId);
            if (StringUtils.isBlank(openId)) {
                //用户必须关注指端微信号
                throw new BusinessException(null, "用户必须关注指端微信号");
            }
            paraMap.put("openid", openId);
            paraMap.put("trade_type", PayParam.tenTradeType);
        }

        // ZD流水号 + 随机数，防止流水号重复
        Long flowId = flowBean.getFlowId();
        StringBuilder sb = new StringBuilder();
        sb.append(flowId).append(RandomUtils.generateRandomNum(6));
        paraMap.put("out_trade_no", sb.toString());

        //支付总金额
        paraMap.put("total_fee", String.valueOf(flowBean.getPayAmount()));
        //生成统一下单信息
        String xml = TenPayUtils.createRequestXml(PayParam.tenPrivateKey, paraMap);
        logger.info("统一下单xml为:\n" + xml);

        //发送给微信支付生成预订单
        String returnXml = HttpUtils.sendPostXml(PayParam.tenOrderUrl, xml, charset);
        logger.info("统一下单返回结果:" + returnXml);

        //转换返回xml结果
        SortedMap<String, String> returnMap = TenPayUtils.getMapFromXML(returnXml);

        String returnCode = returnMap.get("return_code");
        String resultCode = returnMap.get("result_code");
        String returnMsg = returnMap.get("return_msg");
        String errCode = returnMap.get("err_code");
        String errDes = returnMap.get("err_code_des");

        //预订单号是否已经生成，且未失效  该值有效期为2小时
        String prepayId = "";

        SortedMap<String, String> tmpMap = new TreeMap<>();
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            prepayId = returnMap.get("prepay_id");
            tmpMap.put("appId", PayParam.tenAppId);
            tmpMap.put("timeStamp", Long.toString(new Date().getTime()));
            tmpMap.put("nonceStr", TenPayUtils.createNonceStr());
            tmpMap.put("package", String.format("prepay_id=%s", prepayId));
            tmpMap.put("signType", PayParam.tenSignType);
            String sign = TenPayUtils.createSign(PayParam.tenPrivateKey, tmpMap);
            tmpMap.put("paySign", sign);
        } else if (PayReturnCodeEnum.TENPAY_OUT_TRADE_NO_USED.getCode().equals(errCode)) {//订单号重复,直接返回原订单信息
            //查询原订单
            PayFlowBean oldBean = null;//payFlowService.getPayFlowById(flowId, PayConstant.ALL_PAY_STATE);
            prepayId = oldBean.getThdFlowId();
            tmpMap.put("appId", PayParam.tenAppId);
            tmpMap.put("timeStamp", Long.toString(new Date().getTime()));
            tmpMap.put("nonceStr", TenPayUtils.createNonceStr());
            tmpMap.put("package", String.format("prepay_id=%s", prepayId));
            tmpMap.put("signType", PayParam.tenSignType);
            String sign = TenPayUtils.createSign(PayParam.tenPrivateKey, tmpMap);
            tmpMap.put("paySign", sign);
        } else if (PayReturnCodeEnum.TENPAY_ORDERPAID.getCode().equals(errCode)) {
            //订单已支付
            throw new BusinessException(null, "订单已支付");
        } else {
            logger.info("微信预下单失败：{}", !StringUtils.isBlank(returnMsg) ? returnMsg : errDes);
            //微信支付预下单失败
            throw new BusinessException(null, "微信支付预下单失败");
        }
        String html = TenPayUtils.createPageRequest(tmpMap);
        //支付总金额
        Double amount = flowBean.getPayAmount() / 100.0;
        return new PayInfo(PayParam.body, amount, html);
    }

    /**
     * @Description: 支付回调参数校验
     * @param paraMap
     * @return
     * @throws
     */
    @Override
    public PayResult payReturn(Map<String, String> paraMap) throws Exception {
        String tmpXml = paraMap.get("content");
        SortedMap<String, String> sortedMap = TenPayUtils.getMapFromXML(tmpXml);

        PayResult payResult = new PayResult();
        if (sortedMap == null || sortedMap.size() < 1) {
            //参数不能为空
            throw new BusinessException(null, "参数不能为空");
        }

        String returnSign = sortedMap.get("sign");
        String mySign = TenPayUtils.createSign(PayParam.tenPrivateKey, sortedMap);
        if (!returnSign.equals(mySign)) {
            //回调签名不匹配
            throw new BusinessException(null, "回调签名不匹配");
        }
        String resultCode = sortedMap.get("result_code");
        String returnCode = sortedMap.get("return_code");
        String tradeState = sortedMap.get("trade_state");

        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            //支付结果
            payResult.setTradeState(tradeState);
            // 支付流水ID
            String tradeNo = sortedMap.get("out_trade_no");
            payResult.setFlowId(Long.valueOf(tradeNo.substring(0, tradeNo.length() - 6)));
            payResult.setRandomCode(Integer.parseInt(tradeNo.substring(tradeNo.length() - 6)));
            // 微信交易流水号
            payResult.setThdFlowId(sortedMap.get("transaction_id"));
            //错误代码
            payResult.setFailCode(sortedMap.get("err_code"));
            //错误代码描述
            payResult.setFailDesc(sortedMap.get("err_code_des"));

            if (StringUtils.isBlank(tradeState)) {//支付成功时，微信不回传 trade_state，查询订单时会回传 trade_state
                tradeState = "SUCCESS";
            }
            int callbackState = transPayState(tradeState);
            payResult.setPayState(callbackState);
        }
        return payResult;
    }

    /**
     * 单笔退款
     * @param flowBeanList
     * @param extMap
     * @return
     */
    @Override
    public RefundResult refund(List<PayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception {
        PayFlowBean flowBean = flowBeanList.get(0);
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", PayParam.tenAppId);
        paraMap.put("mch_id", PayParam.tenMchId);
        paraMap.put("op_user_id", PayParam.tenMchId);
        //微信订单号
        paraMap.put("transaction_id", flowBean.getThdFlowId());
        //商户退款单号
        paraMap.put("out_refund_no", extMap.get("refundId"));

        String payAmount = String.valueOf(flowBean.getPayAmount());
        paraMap.put("total_fee", payAmount);
        paraMap.put("refund_fee", payAmount);

        //生成信息
        String xml = TenPayUtils.createRequestXml(PayParam.tenPrivateKey, paraMap);
        logger.info("退款发送xml为:\n" + xml);

        //发送给微信支付
        String returnXml = HttpUtils.sendPostWithCert(PayParam.tenRefundUrl, xml, charset);
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
    @Override
    public List<RefundResult> refundReturn(Map<String, String> paraMap) throws Exception {
        logger.info("微信退款回调处理");
        String tmpXml = paraMap.get("content");
        SortedMap<String, String> sortedMap = TenPayUtils.getMapFromXML(tmpXml);

        RefundResult refundResult = new RefundResult();
        if (sortedMap == null || sortedMap.size() < 1) {
            //参数不能为空
            throw new BusinessException(null, "参数不能为空");
        }

        String returnSign = sortedMap.get("sign");
        String mySign = TenPayUtils.createSign(PayParam.tenPrivateKey, sortedMap);
        if (!returnSign.equals(mySign)) {
            //回调签名不匹配
            throw new BusinessException(null, "回调签名不匹配");
        }
        String resultCode = sortedMap.get("result_code");
        String returnCode = sortedMap.get("return_code");
        String tradeState = sortedMap.get("trade_state");

        List<RefundResult> refundResults = new ArrayList<>();
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            //支付结果
            refundResult.setTradeState(tradeState);
            // 支付流水ID
            String tradeNo = sortedMap.get("out_trade_no");
            refundResult.setFlowId(Long.valueOf(tradeNo.substring(0, tradeNo.length() - 6)));

            // 原第三方支付流水
            refundResult.setThdFlowId(sortedMap.get("transaction_id"));
            // 微信退款流水号
            refundResult.setThdRefundId(sortedMap.get("refund_id"));
            //错误代码
            refundResult.setFailCode(sortedMap.get("err_code"));
            //错误代码描述
            refundResult.setFailDesc(sortedMap.get("err_code_des"));

            if (StringUtils.isBlank(tradeState)) {//支付成功时，微信不回传 trade_state，查询订单时会回传 trade_state
                tradeState = "SUCCESS";
            }
            int callbackState = transPayState(tradeState);
            refundResult.setPayState(callbackState);
            refundResults.add(refundResult);
        }
        return refundResults;
    }

    /**
     * 提现
     * @param flowBean
     * @return
     */
    @Override
    public PayResult withdraw(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("mch_appid", PayParam.tenAppId);
        paraMap.put("mchid", PayParam.tenMchId);
        paraMap.put("nonce_str", TenPayUtils.createNonceStr());
        paraMap.put("partner_trade_no", String.valueOf(flowBean.getFlowId()));
        paraMap.put("user_type", "OPEN_ID");

        //NO_CHECK：不校验真实姓名
        //FORCE_CHECK：强校验真实姓名（未绑卡用户会校验失败，无法转账）
        //OPTION_CHECK：针对已绑卡的用户校验真实姓名（未绑卡用户不校验）
        paraMap.put("check_name", "NO_CHECK");
        paraMap.put("amount", String.valueOf(flowBean.getPayAmount()));
        paraMap.put("desc", "用户提现");
        paraMap.put("spbill_create_ip", extMap.get("ipAddress"));
        paraMap.put("openid", extMap.get("openId"));

        //生成信息
        String xml = TenPayUtils.createRequestXml(PayParam.tenPrivateKey, paraMap);
        logger.info("发送xml为:\n" + xml);

        //发送给微信支付
        String returnXml = HttpUtils.sendPostXml(PayParam.tenPayUrl, xml, charset);
        logger.info("返回结果:" + returnXml);

        Map tmpMap = new HashMap();
        tmpMap.put("content", returnXml);
        PayResult payResult = payReturn(tmpMap);

        return payResult;
    }

    /**
     * 获取第三方支付信息
     * @param flowBean
     * @return
     */
    @Override
    public PayResult getPayInfo(PayFlowBean flowBean) throws Exception {
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", PayParam.tenAppId);
        //商品或支付单简要描述
        paraMap.put("device_info", PayParam.tenDeviceInfo);
        paraMap.put("mch_id", PayParam.tenMchId);
        String thdFlowId = flowBean.getThdFlowId();
        if (StringUtils.isBlank(thdFlowId) || thdFlowId.contains("wx")) {
            paraMap.put("out_trade_no", String.valueOf(flowBean.getFlowId()));
        } else {
            paraMap.put("transaction_id", thdFlowId);
        }

        //生成信息
        String xml = TenPayUtils.createRequestXml(PayParam.tenPrivateKey, paraMap);
        logger.info("查询xml为:\n" + xml);

        //发送给微信支付生成预订单
        String returnXml = HttpUtils.sendPostXml(PayParam.tenQueryUrl, xml, charset);
        logger.info("返回结果:" + returnXml);

        Map tmpMap = new HashMap();
        tmpMap.put("content", returnXml);
        PayResult payResult = payReturn(tmpMap);
        return payResult;
    }

    /**
     * 预授权
     * @param bizId 订单业务id
     * @return
     */
    @Override
    public PayInfo authorize(String bizId) throws Exception {
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("appid", PayParam.tenAppId);
        paraMap.put("scope", PayParam.tenAuthCode);
        paraMap.put("redirect_uri", PayParam.tenAuthRetUrl);
        paraMap.put("response_type", "code");
        paraMap.put("state", PayChannelEnum.TEN_PAY.getPayType() + "_" + bizId);

        String param = TenPayUtils.createLinkString(paraMap);

        PayInfo payInfo = new PayInfo();
        payInfo.setDestUrl(PayParam.tenAuthUrl);
        payInfo.setDestParam(param + "#wechat_redirect");

        return payInfo;
    }

    /**
     * 微信获得openid
     * @param code 微信用户token
     * @return
     */
    public String getOpenId(String code) throws Exception {
        StringBuilder sb = new StringBuilder();
        // 获取微信 access_token/openid
        sb.append("&appid=" + PayParam.tenAppId + "&secret=" + PayParam.tenAppSecret + "&code=" + code + "&grant_type=" + "authorization_code");
        String result = HttpUtils.sendPostXml("https://api.weixin.qq.com/sns/oauth2/access_token", sb.toString(), charset);
        return JsonUtils.readValueByName(result, "openid");
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
