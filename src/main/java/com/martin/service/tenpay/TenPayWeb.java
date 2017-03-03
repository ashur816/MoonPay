package com.martin.service.tenpay;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayFlow;
import com.martin.service.IPayWebService;
import com.martin.utils.HttpUtils;
import com.martin.utils.JsonUtils;
import com.martin.utils.PayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ZXY
 * @ClassName: WeiXinPay
 * @Description: 微信公众号支付类
 * @date 2016/5/24 10:31
 */
@Service("tenPayWebService")
public class TenPayWeb implements IPayWebService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IPayFlow payFlow;

    /**
     * 预授权
     *
     * @param bizId 订单业务id
     * @return
     */
    @Override
    public PayInfo authorize(String bizId, String bizType) throws Exception {
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("appid", PayParam.tenWebAppId);
        paraMap.put("scope", PayParam.tenAuthCode);
        paraMap.put("redirect_uri", PayParam.tenWebAuthRetUrl);
        paraMap.put("response_type", "code");
        paraMap.put("state", PayConstant.PAY_TYPE_TEN + "|" + bizId + "|" + bizType);

        String param = PayUtils.buildConcatStr(paraMap);

        PayInfo payInfo = new PayInfo();
        payInfo.setDestUrl(PayParam.tenAuthUrl);
        payInfo.setDestParam(param + "#wechat_redirect");

        return payInfo;
    }

    /**
     * 生成预定单给微信支付网关，返回
     *
     * @param flowBean
     * @return
     */
    @Override
    public PayInfo buildPayInfo(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始微信支付");
        //组装参数返回给前台
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", PayParam.tenWebAppId);
        //商品或支付单简要描述
        paraMap.put("body", PayParam.webBody);
        paraMap.put("attach", "微信支付");
        paraMap.put("device_info", PayParam.tenDeviceInfo);
        paraMap.put("limit_pay", PayParam.tenLimitPay);
        paraMap.put("mch_id", PayParam.tenWebMchId);
        paraMap.put("notify_url", PayParam.tenWebNotifyUrl);

        //统一跳订单详情
        String url = extMap.get("returnUrl");
        if (StringUtils.isNotBlank(url)) {
            paraMap.put("return_url", url);
        } else {//其余的跳首页
            paraMap.put("return_url", PayParam.homeUrl);
        }

        paraMap.put("trade_type", PayParam.tenWebTradeType);
        paraMap.put("spbill_create_ip", extMap.get("ipAddress"));

        //用户id
        String openId = getOpenId(extMap.get("code"));
        if (StringUtils.isEmpty(openId)) {
            //用户必须关注指端微信号
            throw new BusinessException("用户必须关注指端微信号");
        }
        paraMap.put("openid", openId);
        // ZD流水号
        Long flowId = flowBean.getFlowId();
        paraMap.put("out_trade_no", String.valueOf(flowId));

        //支付总金额
        paraMap.put("total_fee", String.valueOf(flowBean.getPayAmount()));
        //生成统一下单信息
        String xml = TenPayUtils.createRequestXml(PayParam.tenWebPrivateKey, paraMap);
        logger.info("统一下单xml为:\n" + xml);

        //发送给微信支付生成预订单
        String returnXml = HttpUtils.sendPostXml(PayParam.tenOrderUrl, xml, PayParam.inputCharset);
        logger.info("下单返回结果:" + returnXml);

        //转换返回xml结果
        SortedMap<String, String> returnMap = PayUtils.getMapFromXML(returnXml, PayParam.inputCharset);

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
            //更新到流水表中
            payFlow.updateThdFlowId(flowId, prepayId);
            tmpMap.put("appId", PayParam.tenWebAppId);
            tmpMap.put("timeStamp", Long.toString(new Date().getTime()));
            tmpMap.put("nonceStr", TenPayUtils.createNonceStr());
            tmpMap.put("package", String.format("prepay_id=%s", prepayId));
            tmpMap.put("signType", PayParam.tenSignType);
            String sign = TenPayUtils.createSign(PayParam.tenWebPrivateKey, tmpMap);
            tmpMap.put("paySign", sign);
        } else if (PayReturnCodeEnum.TENPAY_OUT_TRADE_NO_USED.getCode().equals(errCode)) {//订单号重复,直接返回原订单信息
            //查询原订单
            PayFlowBean oldBean = payFlow.getPayFlowById(flowId, PayConstant.ALL_PAY_STATE);
            prepayId = oldBean.getThdFlowId();
            tmpMap.put("appId", PayParam.tenWebAppId);
            tmpMap.put("timeStamp", Long.toString(new Date().getTime()));
            tmpMap.put("nonceStr", TenPayUtils.createNonceStr());
            tmpMap.put("package", String.format("prepay_id=%s", prepayId));
            tmpMap.put("signType", PayParam.tenSignType);
            String sign = TenPayUtils.createSign(PayParam.tenWebPrivateKey, tmpMap);
            tmpMap.put("paySign", sign);
        } else if (PayReturnCodeEnum.TENPAY_ORDERPAID.getCode().equals(errCode)) {
            //订单已支付
            throw new BusinessException("订单已支付");
        } else {
            logger.info("微信预下单失败：{}", !StringUtils.isEmpty(returnMsg) ? returnMsg : errDes);
            //微信支付预下单失败
            throw new BusinessException("微信支付预下单失败");
        }
        String html = TenPayUtils.createPageRequest(tmpMap);
        //支付总金额
        Double amount = flowBean.getPayAmount() / 100.0;
        return new PayInfo(PayParam.webBody, amount, html);
    }

    /**
     * @param paraMap
     * @return
     * @throws
     * @Description: 支付回调参数校验
     */
    @Override
    public PayResult payReturn(Map<String, String> paraMap) throws Exception {
        logger.info("WEB微信支付回调处理");
        SortedMap<String, String> sortedMap = TenPayUtils.returnValidate(PayParam.tenWebPrivateKey, paraMap);
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
            int callbackState = PayUtils.transPayState(tradeState);
            payResult.setPayState(callbackState);
        }
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
        logger.info("开始WEB微信查单");
        //组装参数返回给前台
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", PayParam.tenWebAppId);
        //商品或支付单简要描述
        paraMap.put("mch_id", PayParam.tenWebMchId);

        // ZD流水号
        paraMap.put("out_trade_no", String.valueOf(flowId));

        //生成信息
        String xml = TenPayUtils.createRequestXml(PayParam.tenWebPrivateKey, paraMap);
        logger.info("WEB微信查单xml为:\n" + xml);

        //发送给微信支付
        String returnXml = HttpUtils.sendPostXml(PayParam.tenQueryUrl, xml, PayParam.inputCharset);
        logger.info("WEB微信查单返回结果:" + returnXml);
        //转换返回xml结果
        SortedMap<String, String> returnMap = PayUtils.getMapFromXML(returnXml, PayParam.inputCharset);

        String returnCode = returnMap.get("return_code");
        String resultCode = returnMap.get("result_code");

        PayResult payResult = new PayResult();
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            String tradeState = returnMap.get("trade_state");
            String thdFlowId = returnMap.get("transaction_id");
            payResult.setPayState(PayUtils.transPayState(tradeState));
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
    public void closeThdPay(Long flowId) throws Exception {
        logger.info("开始WEB微信关单");
        //组装参数返回给前台
        SortedMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("appid", PayParam.tenWebAppId);
        //商品或支付单简要描述
        paraMap.put("mch_id", PayParam.tenWebMchId);

        // ZD流水号
        paraMap.put("out_trade_no", String.valueOf(flowId));

        //生成信息
        String xml = TenPayUtils.createRequestXml(PayParam.tenWebPrivateKey, paraMap);
        logger.info("WEB微信关单xml为:\n" + xml);

        //发送给微信支付
        String returnXml = HttpUtils.sendPostXml(PayParam.tenQueryUrl, xml, PayParam.inputCharset);
        logger.info("WEB微信关单返回结果:" + returnXml);
        //转换返回xml结果
        SortedMap<String, String> returnMap = PayUtils.getMapFromXML(returnXml, PayParam.inputCharset);

        String returnCode = returnMap.get("return_code");
        String resultCode = returnMap.get("result_code");
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {

        } else {
            String errCode = returnMap.get("err_code");
            //关闭失败
            logger.info("WEB微信关单失败-{}", errCode);
        }
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
        String result = HttpUtils.sendPostXml("https://api.weixin.qq.com/sns/oauth2/access_token", sb.toString(), PayParam.inputCharset);
        if (StringUtils.isEmpty(result)) {
            throw new BusinessException("用户必须关注指端微信号");
        }
        return JsonUtils.readValueByName(result, "openid");
    }
}
