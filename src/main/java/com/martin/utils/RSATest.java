package com.martin.utils;

import com.martin.service.alipay.RSA;

import java.util.*;

/**
 * @author ZXY
 * @ClassName: RSATest
 * @Description:
 * @date 2017/1/11 15:37
 */
public class RSATest {

    public static void main(String[] args) {
        String ali_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
        String sign = "f4Kjx33ndB+pMNRcOgoYXVwH0+rh0bpEaPe1Lfohs1M3q1pTNWOr5JTtAXP2lltT26EWZIlR4f3pQ0SAbDYnhcm9NkTpS08SRNpwMoDiyqvNLn6OK1f6GDBAO08j1iMIFoQ/fnHKrCqTF2Ih3wKOUvOy4uEkw8KZCNQJmfplCik=";
        String param = "app_id=2016120804019527&auth_app_id=2016120804019527&body=商品交易&buyer_id=2088312957212424&buyer_logon_id=187****2241&buyer_pay_amount=0.01&charset=utf-8&fund_bill_list=[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}]&gmt_create=2017-01-11+10%3A41%3A15&gmt_payment=2017-01-11 10:41:15&invoice_amount=0.01&notify_id=479b0210755ce0940831b1a797483f2j8q&notify_time=2017-01-11 10:41:16&notify_type=trade_status_sync&out_trade_no=2017011110410126369&point_amount=0.00&receipt_amount=0.01&seller_email=zd_sh_tech@shzhiduan.com&seller_id=2088421198129719&subject=商品交易&total_amount=0.01&trade_no=2017011121001004420234800090&trade_status=TRADE_SUCCESS&version=1.0";
        String param1 ="total_amount=0.01&buyer_id=2088312957212424&trade_no=2017011121001004420234800090&body=商品交易&notify_time=2017-01-11 10:41:16&subject=商品交易&buyer_logon_id=187****2241&auth_app_id=2016120804019527&charset=utf-8&notify_type=trade_status_sync&invoice_amount=0.01&out_trade_no=2017011110410126369&trade_status=TRADE_SUCCESS&gmt_payment=2017-01-11 10:41:16&version=1.0&point_amount=0.00&gmt_create=2017-01-11 10:41:15&buyer_pay_amount=0.01&receipt_amount=0.01&fund_bill_list=[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}]&app_id=2016120804019527&seller_id=2088421198129719&notify_id=479b0210755ce0940831b1a797483f2j8q&seller_email=zd_sh_tech@shzhiduan.com";

        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("total_amount", "0.01");
        paraMap.put("buyer_id", "2088312957212424");
        paraMap.put("trade_no", "2017011121001004420234800090");
        paraMap.put("body", "商品交易");
        paraMap.put("notify_time", "2017-01-11 10:41:16");
        paraMap.put("subject", "商品交易");
        paraMap.put("buyer_logon_id", "187****2241");
        paraMap.put("auth_app_id", "2016120804019527");
        paraMap.put("charset", "utf-8");
        paraMap.put("notify_type", "trade_status_sync");
        paraMap.put("invoice_amount", "0.01");
        paraMap.put("out_trade_no", "2017011110410126369");
        paraMap.put("trade_status", "TRADE_SUCCESS");
        paraMap.put("gmt_payment", "2017-01-11 10:41:16");
        paraMap.put("version", "1.0");
        paraMap.put("point_amount", "0.00");
//        paraMap.put("sign_type", "RSA");
//        paraMap.put("sign", "f4Kjx33ndB+pMNRcOgoYXVwH0+rh0bpEaPe1Lfohs1M3q1pTNWOr5JTtAXP2lltT26EWZIlR4f3pQ0SAbDYnhcm9NkTpS08SRNpwMoDiyqvNLn6OK1f6GDBAO08j1iMIFoQ/fnHKrCqTF2Ih3wKOUvOy4uEkw8KZCNQJmfplCik=");
        paraMap.put("gmt_create", "2017-01-11 10:41:15");
        paraMap.put("buyer_pay_amount", "0.01");
        paraMap.put("receipt_amount", "0.01");
        paraMap.put("fund_bill_list", "[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}]");
        paraMap.put("app_id", "2016120804019527");
        paraMap.put("seller_id", "2088421198129719");
        paraMap.put("notify_id", "479b0210755ce0940831b1a797483f2j8q");
        paraMap.put("seller_email", "zd_sh_tech@shzhiduan.com");

        List<String> keys = new ArrayList<>(paraMap.keySet());

        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = paraMap.get(key);
            authInfo.append(key).append("=").append(value);
            authInfo.append("&");
        }
        String s = authInfo.substring(0,authInfo.length()-1);

        boolean flag = RSA.verify(s, sign, ali_public_key, "utf-8");
        System.out.println(flag);

    }
}
