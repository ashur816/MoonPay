package com.martin.service.impl;

import com.martin.bean.TBPayFlowBean;
import com.martin.constant.PayChannelEnum;
import com.martin.constant.PayConstant;
import com.martin.constant.PayParam;
import com.martin.dao.TBPayFlowMapper;
import com.martin.dto.PayInfo;
import com.martin.dto.RefundResult;
import com.martin.exception.BusinessException;
import com.martin.service.ITBService;
import com.martin.service.alipay.AliPayUtils;
import com.martin.service.tenpay.TenPay;
import com.martin.service.tenpay.TenPayUtils;
import com.martin.utils.DateUtils;
import com.martin.utils.HttpUtils;
import com.martin.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName: TBPayService
 * @Description:
 * @author ZXY
 * @date 2016/8/18 17:23
 */
@Service("tBPayService")
public class TBPayService implements ITBService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private TBPayFlowMapper tbPayFlowMapper;

    @Resource
    private TenPay tenPay;

    /**
     * @Description: 获取退款信息
     * @param  flowIdList   收银台流水号
     * @return PayInfo
     * @throws
     */
    @Override
    public List<PayInfo> getRefundInfo(List<String> flowIdList) throws Exception {
        //根据flowId查流水
        List<TBPayFlowBean> flowBeanList = tbPayFlowMapper.selectListById(flowIdList, PayConstant.PAY_SUCCESS);
        if (flowBeanList == null || flowBeanList.size() <= 0) {
            throw new BusinessException(null, "未查询到支付成功的订单");
        }
        PayInfo payInfo = null;
        List<PayInfo> infoList = new ArrayList<>();
        TBPayFlowBean flowBean = null;
        for (int i = 0; i < flowBeanList.size(); i++) {
            flowBean = flowBeanList.get(i);
            payInfo = new PayInfo();
            payInfo.setFlowId(flowBean.getFlowId());
            payInfo.setBizId(flowBean.getBizId().toString());
            payInfo.setPayType(flowBean.getThdType().toString());
            payInfo.setGoodName("XXXX");
            payInfo.setPayAmount(flowBean.getPayAmount() / 100.0);
            infoList.add(payInfo);
        }
        return infoList;
    }

    /**
     * @Description: 退款/批量退款的payType一定要一样
     * @return void
     * @throws
     * @param flowIdList
     * @param refundReason
     */
    @Override
    public Object doRefund(List<String> flowIdList, String refundReason) throws Exception {
        if (flowIdList == null || 0 >= flowIdList.size() || StringUtils.isBlank(refundReason)) {
            throw new BusinessException(null, "收银台流水和退款原因不能为空");
        }
        //根据流水号查流水
        List<TBPayFlowBean> flowBeanList = tbPayFlowMapper.selectListById(flowIdList, PayConstant.PAY_SUCCESS);
        if (flowBeanList == null) {
            throw new BusinessException(null, "未查询到支付信息");
        }

        Map<String, String> extMap = new HashMap<>();
        extMap.put("refundReason", refundReason);

        String payType = flowBeanList.get(0).getThdType().toString();

        Object retObj;
        //微信是同步返回  支付宝是异步返回
        if (PayChannelEnum.TEN_PAY.getPayType().equals(payType)) {
            //更新退款流水
            RefundResult refundResult;
            TBPayFlowBean flowBean;
            String refundId;
            for (int i = 0; i < flowBeanList.size(); i++) {
                refundId = RandomUtils.getPaymentNo();
                extMap.put("refundId", refundId);
                refundResult = (RefundResult)tenRefund(flowBeanList, extMap);
                if (refundResult != null) {
                    flowBean = flowBeanList.get(i);
                    flowBean.setRefundId(Long.parseLong(refundId));
                    flowBean.setFailDesc(refundReason);
                    flowBean.setPayState(PayConstant.REFUND_SUCCESS);
                    flowBean.setThdRefundId(refundResult.getThdFlowId());
                    flowBean.setRefundTime(new Date());
                    tbPayFlowMapper.updateByPrimaryKeySelective(flowBean);
                }
            }
            retObj = "退款成功";
        } else {
            extMap.put("refundId", RandomUtils.getPaymentNo());
            retObj = aliRefund(flowBeanList, extMap);
        }
        return retObj;
    }

    /**
     * 单笔/批量退款
     * @param flowBeanList
     * @param extMap
     * @return
     */
    public PayInfo aliRefund(List<TBPayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception {
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
        TBPayFlowBean flowBean;
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
     * 单笔退款
     * @param flowBeanList
     * @param extMap
     * @return
     */
    public RefundResult tenRefund(List<TBPayFlowBean> flowBeanList, Map<String, String> extMap) throws Exception {
        TBPayFlowBean flowBean = flowBeanList.get(0);
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
        String returnXml = HttpUtils.sendPostWithCert(PayParam.tenRefundUrl, xml, "utf-8");
        logger.info("退款返回结果:" + returnXml);

        Map tmpMap = new HashMap();
        tmpMap.put("content", returnXml);
        List<RefundResult> refundResults = tenPay.refundReturn(tmpMap);

        return refundResults.size() > 0 ? refundResults.get(0) : null;
    }
}
