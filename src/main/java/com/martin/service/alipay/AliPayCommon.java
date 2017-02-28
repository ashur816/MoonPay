package com.martin.service.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.dto.PayInfo;
import com.martin.dto.PayResult;
import com.martin.dto.RefundResult;
import com.martin.dto.TransferResult;
import com.martin.service.IPayCommonService;
import com.martin.service.IPayFlow;
import com.martin.utils.DateUtils;
import com.martin.utils.JsonUtils;
import com.martin.utils.ObjectUtils;
import com.martin.utils.PayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ZXY
 * @ClassName: AliPay
 * @Description: 支付宝--用来企业付款
 * @date 2016/5/24 10:22
 */
@Service("aliPayCommonService")
public class AliPayCommon implements IPayCommonService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IPayFlow payFlow;

    /**
     * 单个企业付款
     *
     * @param flowBean
     * @param extMap
     * @return
     */
    @Override
    public Object transferBatch(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        logger.info("开始支付宝企业付款-{}", extMap);
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("service", PayParam.aliBatchTransferService);
        paraMap.put("partner", PayParam.aliPartnerId);
        paraMap.put("_input_charset", PayParam.inputCharset);
        paraMap.put("sign_type", PayParam.aliSignTypeMD5);
        paraMap.put("notify_url", PayParam.aliTransferNotifyUrl);
        //付款方的支付宝账户名
        paraMap.put("account_name", PayParam.aliAccountName);
        //付款账号
        paraMap.put("email", PayParam.aliAccountNo);

        //单笔数据集 流水号^收款方账号^收款账号姓名^付款金额^备注说明  第一笔交易退款数据集|第二笔交易退款数据集
        StringBuilder sBuilder = new StringBuilder();
        double payAmount;
        String transferReason = extMap.get("transferReason");
        payAmount = flowBean.getPayAmount() / 100.0;

        String thdNo = extMap.get("thdNo");
        String thdName = extMap.get("thdName");

        sBuilder.append(flowBean.getFlowId()).append("^").append(thdNo).append("^").append(thdName).append("^").append(payAmount).append("^").append(transferReason).append("|");

        paraMap.put("detail_data", sBuilder.deleteCharAt(sBuilder.length() - 1).toString());
        //付款批次号
        paraMap.put("batch_no", extMap.get("batchNo"));
        //总笔数
        paraMap.put("batch_num", "1");
        //付款总金额
        paraMap.put("batch_fee", String.valueOf(payAmount));
        //付款时间 格式为：yyyy-MM-dd HH:mm:ss
        paraMap.put("pay_date", DateUtils.formatDate(new Date(), "yyyyMMdd"));

        String sendString = AliPayUtils.buildReqForm(PayParam.aliMapiUrl, PayParam.aliMD5Key, PayParam.tenSignType, paraMap);
        logger.info("发送企业付款信息{}", sendString);
        PayInfo payInfo = new PayInfo();
        payInfo.setPayType(PayConstant.PAY_TYPE_ALI);
        payInfo.setRetHtml(sendString);
        return payInfo;
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
        return trans1(flowBean, extMap);
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 手工组装参数 RSA
     */
    private PayResult trans1(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("app_id", PayParam.aliWebAppId);
        paraMap.put("method", PayParam.aliSingleTransferService);
        paraMap.put("charset", PayParam.inputCharset);
        paraMap.put("sign_type", PayParam.aliSignTypeRSA);
        paraMap.put("timestamp", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        paraMap.put("version", "1.0");

        // biz_content 业务请求参数的集合
        Map<String, String> bizMap = new HashMap<>();
        //支付流水号
        bizMap.put("out_trade_no", String.valueOf(flowBean.getFlowId()));
        //收款方账户类型
        bizMap.put("payee_type", "ALIPAY_LOGONID");
        //收款方账户
        String thdNo = extMap.get("thdNo");
        bizMap.put("payee_account", thdNo);
        //转账金额 单位：元
        double payAmount = flowBean.getPayAmount() / 100.0;
        bizMap.put("amount", String.valueOf(payAmount));
        //收款方真实姓名
        String thdName = extMap.get("thdName");
        bizMap.put("payee_real_name", thdName);
        //转账备注
        bizMap.put("remark", extMap.get("transferReason"));

        paraMap.put("biz_content", JsonUtils.translateToJson(bizMap));

        String tmpString = AliPayAppUtils.sendPost(PayParam.aliOpenUrl, PayParam.aliWebPrivateKey, paraMap);
        System.err.println(tmpString);
        //解析返回
        Map tmpMap = JsonUtils.readMap(tmpString);
        Map returnMap = (Map) tmpMap.get("alipay_trade_query_response");
        Object subCode = returnMap.get("sub_code");
        Object subMsg = returnMap.get("sub_msg");
        Object tradeStatus = returnMap.get("trade_status");
        String code = "WAIT_BUYER_PAY";
        if (ObjectUtils.isNotEmpty(subCode)) {
            code = subCode.toString();
        } else if (ObjectUtils.isNotEmpty(tradeStatus)) {
            code = tradeStatus.toString();
        }
        logger.info("WEB支付宝查单结果-{},-{}", code, subMsg);
        PayResult payResult = new PayResult();
        payResult.setPayState(PayUtils.transPayState(code));
        if (PayConstant.PAY_SUCCESS == payResult.getPayState()) {
            //支付成功的更新第三方交易流水号
            payResult.setThdFlowId(returnMap.get("trade_no").toString());
        }
        return payResult;
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: SDK方式 RSA
     */
    private PayResult trans2(PayFlowBean flowBean, Map<String, String> extMap) throws Exception {
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", PayParam.aliWebAppId, PayParam.aliWebPrivateKey, "json", PayParam.inputCharset, PayParam.aliAliPublicKey, "RSA");
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();

        // biz_content 业务请求参数的集合
        Map<String, String> bizMap = new HashMap<>();
        //支付流水号
        bizMap.put("out_trade_no", String.valueOf(flowBean.getFlowId()));
        //收款方账户类型
        bizMap.put("payee_type", "ALIPAY_LOGONID");
        //收款方账户
        String thdNo = extMap.get("thdNo");
        bizMap.put("payee_account", thdNo);
        //转账金额 单位：元
        double payAmount = flowBean.getPayAmount() / 100.0;
        bizMap.put("amount", String.valueOf(payAmount));
        //收款方真实姓名
        String thdName = extMap.get("thdName");
        bizMap.put("payee_real_name", thdName);
        //转账备注
        bizMap.put("remark", extMap.get("transferReason"));
        request.setBizContent(JsonUtils.translateToJson(bizMap));
        AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);

        PayResult payResult = new PayResult();
        if (response.isSuccess()) {
            System.out.println("调用成功");
            payResult.setPayState(PayUtils.transPayState(response.getCode()));
            if (PayConstant.PAY_SUCCESS == payResult.getPayState()) {
                //支付成功的更新第三方交易流水号
                payResult.setThdFlowId(response.getOrderId());
            }
        } else {
            payResult.setPayState(PayConstant.PAY_FAIL);
            System.out.println("调用失败");
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
        logger.info("支付宝企业付款回调处理");
        //验签
        AliPayUtils.returnValidate(PayParam.aliWebPrivateKey, paraMap);

        String batchNo = paraMap.get("batch_no");
        //返回格式 0315001^gonglei1@handsome.com.cn^龚本林^20.00^S^null^200810248427067^20081024143652|
        //格式为：流水号^收款方账号^收款账号姓名^付款金额^成功标识(S)^成功原因(null)^支付宝内部流水号^完成时间。
        //格式为：流水号^收款方账号^收款账号姓名^付款金额^失败标识(F)^失败原因^支付宝内部流水号^完成时间。
        String successDetails = paraMap.get("success_details");
        String failDetails = paraMap.get("fail_details");
        //合并处理
        List<String> combineList = new ArrayList<>();

        if (!StringUtils.isEmpty(successDetails)) {
            combineList.addAll(Arrays.asList(successDetails.split("\\|")));
        }
        if (!StringUtils.isEmpty(failDetails)) {
            combineList.addAll(Arrays.asList(failDetails.split("\\|")));
        }

        List<String> resultList;
        TransferResult transferResult;
        List<TransferResult> transferList = new ArrayList<>();

        //遍历成功的
        String resultFlag;
        for (int i = 0; i < combineList.size(); i++) {
            resultList = Arrays.asList(combineList.get(i).split("\\^"));
            transferResult = new TransferResult();
            transferResult.setFlowId(Long.valueOf(resultList.get(0)));
            transferResult.setThdAcctNo(resultList.get(1));
            transferResult.setThdAcctName(resultList.get(2));
            transferResult.setPayAmount(Double.parseDouble(resultList.get(3)));
            resultFlag = resultList.get(4);
            if ("S".equals(resultFlag)) {//成功的
                transferResult.setTransferState(PayConstant.PAY_SUCCESS);
                transferResult.setThdFlowId(resultList.get(6));
            } else {//失败的
                transferResult.setTransferState(PayConstant.PAY_FAIL);
                transferResult.setFailDesc(resultList.get(5));
            }
            transferList.add(transferResult);
        }

        return transferList;
    }

    /**
     * 批量退款，兼容单个  MD5签名
     *
     * @param flowBeanList
     * @param extMap
     * @return
     */
    @Override
    public Object refund(String clientSource, List<PayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception {
        logger.info("开始支付宝退款-{}", extMap);
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("service", PayParam.aliRefundService);
        paraMap.put("partner", PayParam.aliPartnerId);
        paraMap.put("seller_user_id", PayParam.aliPartnerId);
        paraMap.put("_input_charset", PayParam.inputCharset);
        paraMap.put("sign_type", PayParam.aliSignTypeMD5);
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

        String sendString = AliPayUtils.buildReqForm(PayParam.aliMapiUrl, PayParam.aliMD5Key, PayParam.tenSignType, paraMap);
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
     * @Description: 退款返回信息
     */
    @Override
    public List<RefundResult> refundReturn(Map<String, String> paraMap) throws Exception {
        logger.info("支付宝退款回调处理");
        //验签
        AliPayUtils.returnValidate(PayParam.aliMD5Key, paraMap);

        String batchNo = paraMap.get("batch_no");
        //返回格式 支付宝支付时交易流水 2016072021001004610238752098^0.00^REFUND_TRADE_FEE_ERROR#2016072021001004610238752098^0.00^REFUND_TRADE_FEE_ERROR
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
}
