package com.martin.service.tenpay;

import com.martin.bean.PayFlowBean;
import com.martin.bean.PayInfo;
import com.martin.bean.PayResult;
import com.martin.constant.PayChannelEnum;
import com.martin.constant.PayConstant;
import com.martin.constant.PayReturnCodeEnum;
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

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String charset = "UTF-8";

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
        paraMap.put("appid", PayConstant.TENPAY_APP_ID);
        //商品或支付单简要描述
        paraMap.put("body", PayConstant.BODY);
        paraMap.put("attach", "微信支付");
        paraMap.put("device_info", PayConstant.TENPAY_DEVICE_INFO);
        paraMap.put("limit_pay", PayConstant.TENPAY_LIMIT_PAY);
        paraMap.put("mch_id", PayConstant.TENPAY_MCH_ID);
        paraMap.put("notify_url", PayConstant.TENPAY_NOTIFY_URL);
        paraMap.put("return_url", PayConstant.TENPAY_RETURN_URL);
        paraMap.put("trade_type", PayConstant.TENPAY_TRADE_TYPE);
        paraMap.put("spbill_create_ip", extMap.get("ipAddress"));

        //用户id
        String openId = getOpenId(extMap.get("code"));
        logger.info("openId={}", openId);
        if (StringUtils.isBlank(openId)) {
            //用户必须关注指端微信号
            throw new BusinessException("09033");
        }
        paraMap.put("openid", openId);
        // ZD流水号 + 随机数，防止流水号重复
        Long flowId = flowBean.getFlowId();
        StringBuilder sb = new StringBuilder();
        sb.append(flowId).append(RandomUtils.generateRandomNum(6));
        paraMap.put("out_trade_no", sb.toString());

        //支付总金额
        paraMap.put("total_fee", String.valueOf(flowBean.getPayAmount()));
        //生成统一下单信息
        String xml = TenPayUtils.createRequestXml(PayConstant.TENPAY_PRIVATE_KEY, paraMap);
        logger.info("统一下单xml为:\n" + xml);

        //发送给微信支付生成预订单
        String returnXml = HttpUtils.sendPost(PayConstant.TENPAY_ORDER_URL, xml, "utf-8");
        logger.info("返回结果:" + returnXml);

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
            tmpMap.put("appId", PayConstant.TENPAY_APP_ID);
            tmpMap.put("timeStamp", Long.toString(new Date().getTime()));
            tmpMap.put("nonceStr", TenPayUtils.createNonceStr());
            tmpMap.put("package", String.format("prepay_id=%s", prepayId));
            tmpMap.put("signType", PayConstant.TENPAY_SIGN_TYPE);
            String sign = TenPayUtils.createSign(PayConstant.TENPAY_PRIVATE_KEY, tmpMap);
            tmpMap.put("paySign", sign);
        } else if (PayReturnCodeEnum.TENPAY_OUT_TRADE_NO_USED.getCode().equals(errCode)) {//订单号重复,直接返回原订单信息
            //查询原订单
            PayFlowBean oldBean = null;//payFlowService.getPayFlowById(flowId, PayConstant.ALL_PAY_STATE);
            prepayId = oldBean.getThdFlowId();
            tmpMap.put("appId", PayConstant.TENPAY_APP_ID);
            tmpMap.put("timeStamp", Long.toString(new Date().getTime()));
            tmpMap.put("nonceStr", TenPayUtils.createNonceStr());
            tmpMap.put("package", String.format("prepay_id=%s", prepayId));
            tmpMap.put("signType", PayConstant.TENPAY_SIGN_TYPE);
            String sign = TenPayUtils.createSign(PayConstant.TENPAY_PRIVATE_KEY, tmpMap);
            tmpMap.put("paySign", sign);
        } else if (PayReturnCodeEnum.TENPAY_ORDERPAID.getCode().equals(errCode)) {
            //订单已支付
            throw new BusinessException("09031");
        } else {
            logger.info("微信预下单失败：{}", !StringUtils.isEmpty(returnMsg) ? returnMsg : errDes);
            //微信支付预下单失败
            throw new BusinessException("09020");
        }
        String html = TenPayUtils.createPageRequest(tmpMap);
        //支付总金额
        Double amount = flowBean.getPayAmount() / 100.0;
        return new PayInfo(PayConstant.BODY, amount, html);
    }

    /**
     * @Description: 回调参数校验
     * @param tmpMap
     * @return
     * @throws
     */
    @Override
    public PayResult returnValidate(Map<String, String> tmpMap) throws Exception {

        String tmpXml = tmpMap.get("content");
        SortedMap<String, String> paraMap = TenPayUtils.getMapFromXML(tmpXml);

        PayResult payResult = new PayResult();
        if (paraMap == null || paraMap.size() < 1) {
            //参数不能为空
            throw new BusinessException("111");
        }

        String returnSign = paraMap.get("sign");
        String mySign = TenPayUtils.createSign(PayConstant.TENPAY_PRIVATE_KEY, paraMap);
        if (!returnSign.equals(mySign)) {
            //回调签名不匹配
            throw new BusinessException("09025");
        }
        String resultCode = paraMap.get("result_code");
        String returnCode = paraMap.get("return_code");
        String tradeState = paraMap.get("trade_state");

        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            //支付结果
            payResult.setTradeState(tradeState);
            // 支付流水ID
            String tradeNo = paraMap.get("out_trade_no");
            payResult.setFlowId(Long.valueOf(tradeNo.substring(0, tradeNo.length() - 6)));
            // 微信交易流水号
            payResult.setThdFlowId(paraMap.get("transaction_id"));
            //错误代码
            payResult.setFailCode(paraMap.get("err_code"));
            //错误代码描述
            payResult.setFailDesc(paraMap.get("err_code_des"));

            if (StringUtils.isEmpty(tradeState)) {//支付成功时，微信不回传 trade_state，查询订单时会回传 trade_state
                tradeState = "SUCCESS";
            }
            int callbackState = transPayState(tradeState);
            payResult.setPayState(callbackState);
        }
        return payResult;
    }

    /**
     * 提现
     * @param flowBean
     * @return
     */
    @Override
    public PayResult withdraw(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("mch_appid", PayConstant.TENPAY_APP_ID);
        paraMap.put("mchid", PayConstant.TENPAY_MCH_ID);
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
        String xml = TenPayUtils.createRequestXml(PayConstant.TENPAY_PRIVATE_KEY, paraMap);
        logger.info("发送xml为:\n" + xml);

        //发送给微信支付
        String returnXml = HttpUtils.sendPost(PayConstant.TENPAY_PAY_URL, xml, charset);
        logger.info("返回结果:" + returnXml);

        Map tmpMap = new HashMap();
        tmpMap.put("content", returnXml);
        PayResult payResult = returnValidate(tmpMap);

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
        paraMap.put("appid", PayConstant.TENPAY_APP_ID);
        //商品或支付单简要描述
        paraMap.put("device_info", PayConstant.TENPAY_DEVICE_INFO);
        paraMap.put("mch_id", PayConstant.TENPAY_MCH_ID);
        String thdFlowId = flowBean.getThdFlowId();
        if (StringUtils.isEmpty(thdFlowId) || thdFlowId.contains("wx")) {
            paraMap.put("out_trade_no", String.valueOf(flowBean.getFlowId()));
        } else {
            paraMap.put("transaction_id", thdFlowId);
        }

        //生成信息
        String xml = TenPayUtils.createRequestXml(PayConstant.TENPAY_PRIVATE_KEY, paraMap);
        logger.info("查询xml为:\n" + xml);

        //发送给微信支付生成预订单
        String returnXml = HttpUtils.sendPost(PayConstant.TENPAY_QUERY_URL, xml, charset);
        logger.info("返回结果:" + returnXml);

        Map tmpMap = new HashMap();
        tmpMap.put("content", returnXml);
        PayResult payResult = returnValidate(tmpMap);
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
        paraMap.put("appid", PayConstant.TENPAY_APP_ID);
        paraMap.put("scope", PayConstant.TENPAY_AUTH_CODE);
        paraMap.put("redirect_uri", PayConstant.TENPAY_AUTH_RET_URL);
        paraMap.put("response_type", "code");
        paraMap.put("state", PayChannelEnum.TEN_PAY.getPayType() + "_" + bizId);

        String param = TenPayUtils.createLinkString(paraMap);

        PayInfo payInfo = new PayInfo();
        payInfo.setDestUrl(PayConstant.TENPAY_AUTH_URL);
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
        sb.append("&appid=" + PayConstant.TENPAY_APP_ID + "&secret=" + PayConstant.TENPAY_APP_SECRET + "&code=" + code + "&grant_type=" + "authorization_code");
        String result = HttpUtils.sendPost("https://api.weixin.qq.com/sns/oauth2/access_token", sb.toString(), charset);
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
