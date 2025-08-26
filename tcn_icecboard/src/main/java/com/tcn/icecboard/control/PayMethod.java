package com.tcn.icecboard.control;

/**
 * Created by Administrator on 2017/5/31.
 */
public class PayMethod {
    
    public static final String PAYMETHED_NONE             = "-1";
    public static final String PAYMETHED_CASH             = "0";   //现金支付
    public static final String PAYMETHED_THMQH        = "1";  //后台下发
    public static final String PAYMETHED_MDB_CARD        = "2";    //银联卡
    //3:会员卡
    public static final String PAYMETHED_TCNICCARD      = "4";    //中吉IC卡支付  会员卡
    public static final String PAYMETHED_BANKPOSCARD     = "5";  //银行卡POS机支付    银行卡
    public static final String PAYMETHED_WECHAT        = "11";      //微信
    public static final String PAYMETHED_ALI           = "13";          //支付宝
    public static final String PAYMETHED_GIFTS        = "15";
    public static final String PAYMETHED_REMOUT       = "16";    //远程出货（App一键出货）
    public static final String PAYMETHED_VERIFY       = "17";    //验证码出货(提货码出货)
    public static final String PAYMETHED_OTHER_A        = "A";    //其它支付  微信
    public static final String PAYMETHED_OTHER_B        = "B";    //其它支付  支付宝
    public static final String PAYMETHED_OTHER_C        = "C";    //其它支付
    public static final String PAYMETHED_OTHER_D        = "D";    //微信游戏 68
    public static final String PAYMETHED_OTHER_E        = "E";    //翼支付 69
    public static final String PAYMETHED_OTHER_F        = "F";    //工行扫码 70
    public static final String PAYMETHED_OTHER_G        = "G";    //微信会员卡 71
    public static final String PAYMETHED_OTHER_H        = "H";    //QQ钱包 72
    public static final String PAYMETHED_OTHER_I        = "I";    //银联商务 73
    public static final String PAYMETHED_OTHER_J        = "J";    //平安E支付 74
    public static final String PAYMETHED_OTHER_K        = "K";    //其它支付
    public static final String PAYMETHED_OTHER_L        = "L";    //其它支付
}
