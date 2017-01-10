package com.martin.service.tenpay;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayParam;
import com.martin.constant.PayReturnCodeEnum;
import com.martin.dto.PayResult;
import com.martin.dto.TransferResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayCommonService;
import com.martin.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @ClassName: WeiXinPay
 * @Description: 微信公众号支付类
 * @author ZXY
 * @date 2016/5/24 10:31
 */
@Service("tenPayCommonService")
public class TenPayCommon implements IPayCommonService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 批量付款，兼容单个
     * @param flowBeanList
     * @param extMap
     * @return
     */
    @Override
    public Object transfer(List<PayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception {
        //仅有一笔，绝对存在
        PayFlowBean flowBean = flowBeanList.get(0);

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
        String returnXml = TenPayUtils.sendPostWithCert(PayParam.tenTransferUrl, xml, "UTF-8");
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
            String err = !StringUtils.isEmpty(returnMsg) ? returnMsg : errDes;
            payResult.setFailDesc(err);
            payResult.setFailCode(errCode);
            payResult.setTradeState("FAIL");
            logger.info("微信企业付款失败：{}", err);
        }

        return payResult;
    }

    /**
     * @Description: 企业付款返回信息
     * @param paraMap
     * @return
     * @throws
     */
    @Override
    public List<TransferResult> transferReturn(Map<String, String> paraMap) throws Exception {
        return null;
    }

    /**
     * 微信获得openid
     * @param code 微信用户token
     * @return
     */
    public String getOpenId(String code) throws Exception {
        StringBuilder sb = new StringBuilder();
        // 获取微信 access_token/openid
        sb.append("&appid=" + PayParam.tenWebAppId + "&secret=" + PayParam.tenAppSecret + "&code=" + code + "&grant_type=" + "authorization_code");
        String result = TenPayUtils.sendPostXml("https://api.weixin.qq.com/sns/oauth2/access_token", sb.toString());
        if (StringUtils.isEmpty(result)) {
            throw new BusinessException("09033");
        }
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

    /**
     * 验签
     */
    private SortedMap<String, String> returnValidate(Map<String, String> paraMap) throws Exception {
        String tmpXml = paraMap.get("content");
        SortedMap<String, String> sortedMap = TenPayUtils.getMapFromXML(tmpXml);

        if (sortedMap == null || sortedMap.size() < 1) {
            //参数不能为空
            throw new BusinessException(null, "参数不能为空");
        }

        String returnSign = sortedMap.get("sign");
        String mySign = TenPayUtils.createSign(PayParam.tenWebPrivateKey, sortedMap);
        if (!returnSign.equals(mySign)) {
            //回调签名不匹配
            throw new BusinessException(null, "回调签名不匹配");
        }
        return sortedMap;
    }
}
