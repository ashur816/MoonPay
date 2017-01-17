package com.martin.service.alipay;

import com.martin.bean.PayFlowBean;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.dto.PayInfo;
import com.martin.dto.TransferResult;
import com.martin.exception.BusinessException;
import com.martin.service.IPayCommonService;
import com.martin.service.IPayFlow;
import com.martin.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName: AliPay
 * @Description: 支付宝--用来企业付款
 * @author ZXY
 * @date 2016/5/24 10:22
 */
@Service("aliPayCommonService")
public class AliPayCommon implements IPayCommonService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IPayFlow payFlow;

    /**
     * 批量付款，兼容单个
     * @param flowBeanList
     * @param extMap
     * @return
     */
    @Override
    public Object transfer(List<PayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception {
        logger.info("开始支付宝企业付款-{}", extMap);
        //组装参数返回给前台
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("service", PayParam.aliTransferService);
        paraMap.put("partner", PayParam.aliPartnerId);
        paraMap.put("_input_charset", PayParam.aliInputCharset);
        paraMap.put("sign_type", PayParam.aliWebSignType);
        paraMap.put("notify_url", PayParam.aliTransferNotifyUrl);
        //付款方的支付宝账户名
        paraMap.put("account_name", PayParam.aliAccountName);
        //付款账号
        paraMap.put("email", PayParam.aliAccountNo);

        //单笔数据集 流水号^收款方账号^收款账号姓名^付款金额^备注说明  第一笔交易退款数据集|第二笔交易退款数据集
        StringBuilder sBuilder = new StringBuilder();
        double totalAmount = 0;
        double payAmount;
        int transferNum = 0;
        String transferReason = extMap.get("transferReason");
        for (PayFlowBean flowBean : flowBeanList) {
            payAmount = flowBean.getPayAmount() / 100.0;

            //根据服务查询提现记录
//            AcctExchangeCashInfo exchangeCashInfo = acctExchangeCashService.getExchangeCashThdparty(flowBean.getBizId());
//            if (exchangeCashInfo != null) {
                //查询收款方账号和名称
                String acctNo = "632663267@qq.com";
                String acctName = "zxy";
                sBuilder.append(flowBean.getFlowId()).append("^").append(acctNo).append("^").append(acctName).append("^").append(payAmount).append("^").append(transferReason).append("|");
                totalAmount += payAmount;
                transferNum++;
//            }
        }
        paraMap.put("detail_data", sBuilder.deleteCharAt(sBuilder.length() - 1).toString());
        //付款批次号
        paraMap.put("batch_no", extMap.get("batchNo"));
        //总笔数
        paraMap.put("batch_num", String.valueOf(transferNum));
        //付款总金额
        paraMap.put("batch_fee", String.valueOf(totalAmount));
        //付款时间 格式为：yyyy-MM-dd HH:mm:ss
        paraMap.put("pay_date", DateUtils.formatDate(new Date(), "yyyyMMdd"));

        String sendString = AliPayUtils.buildReqForm(PayParam.aliMapiUrl, PayParam.aliMD5Key, PayParam.aliWebSignType, paraMap);
        logger.info("发送企业付款信息{}", sendString);

        PayInfo payInfo = new PayInfo();
        payInfo.setPayType(PayConstant.PAY_TYPE_ALI);
        payInfo.setRetHtml(sendString);
        return payInfo;
    }

    /**
     * @Description: 企业付款返回信息
     * @param paraMap
     * @return
     * @throws
     */
    @Override
    public List<TransferResult> transferReturn(Map<String, String> paraMap) throws Exception {
        logger.info("支付宝企业付款回调处理");
        //验签
        returnValidate(paraMap);

        String batchNo = paraMap.get("batch_no");
        //返回格式 0315001^gonglei1@handsome.com.cn^龚本林^20.00^S^null^200810248427067^20081024143652|
        //格式为：流水号^收款方账号^收款账号姓名^付款金额^成功标识(S)^成功原因(null)^支付宝内部流水号^完成时间。
        //格式为：流水号^收款方账号^收款账号姓名^付款金额^失败标识(F)^失败原因^支付宝内部流水号^完成时间。
        String successDetails = paraMap.get("success_details");
        String failDetails = paraMap.get("fail_details");
        //合并处理
        List<String> combineList = new ArrayList<>();

        if(!StringUtils.isEmpty(successDetails)){
            combineList.addAll(Arrays.asList(successDetails.split("\\|")));
        }
        if(!StringUtils.isEmpty(failDetails)) {
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
            if("S".equals(resultFlag)){//成功的
                transferResult.setTransferState(PayConstant.PAY_SUCCESS);
                transferResult.setThdFlowId(resultList.get(6));
            }
            else {//失败的
                transferResult.setTransferState(PayConstant.PAY_FAIL);
                transferResult.setFailDesc(resultList.get(5));
            }
            transferList.add(transferResult);
        }

        return transferList;
    }

    /**
     * @Description: 回调验签
     * @param
     * @return
     * @throws
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
}
