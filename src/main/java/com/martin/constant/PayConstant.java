package com.martin.constant;

/**
 * @ClassName: PayConstant
 * @Description: 支付常量
 * @author ZXY
 * @date 2016/6/1 21:06
 */
public class PayConstant {
    /* 支付状态 */
    public static int ALL_PAY_STATE = -1;//所有状态

    public static int PAY_NOT = 0;//未支付

    public static int PAY_UN_BACK = 5;//已支付成功，但第三方未回调

    public static int PAY_UN_BIZ = 2;//已支付成功，但业务未处理

    public static int PAY_ERROR_BIZ = 3;//已支付成功，但业务处理失败

    public static int PAY_SUCCESS = 1;//支付成功，且业务处理成功

    public static int REFUND_ING = 6;//退款中

    public static int REFUND_SUCCESS = 8;//退款成功

    public static int REFUND_FAIL = 7;//退款失败

    public static int PAY_FAIL = 4;//支付失败

    /* 业务类型 */
    public static int BIZ_TYPE_GRAB = 1;
    public static int BIZ_TYPE_WITHDRAW = 2;

    /* 账户类型 */
    public static int ACCT_TYPE_ZD = 0;
    public static int ACCT_TYPE_USER = 1;

    /* 通知类型 */
    public static String NOTICE_RETURN = "return";//同步通知

    public static String NOTICE_NOTIFY = "pay";//异步通知

    public static String NOTICE_REFUND = "refund";//退款通知

    public static int STATE_0 = 0;//数据状态--删除
    public static int STATE_1 = 1;//数据状态--正常
}
