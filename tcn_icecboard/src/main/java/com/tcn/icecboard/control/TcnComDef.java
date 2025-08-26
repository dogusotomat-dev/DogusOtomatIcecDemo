package com.tcn.icecboard.control;

public class TcnComDef {
	//串口
	public static final int SERIAL_PORT_RECEIVE_DATA                = 100;
	public static final int SERIAL_PORT_RECEIVE_DATA_OTHER         = 101;
	public static final int SERIAL_PORT_CONFIG_ERROR                = 102;
	public static final int SERIAL_PORT_SECURITY_ERROR              = 103;
	public static final int SERIAL_PORT_UNKNOWN_ERROR               = 104;


	public static final int COMMAND_TOSS_COINS                     = 110; //投硬币
	public static final int COMMAND_TOSS_PAPER_MONEY              = 111; //投纸币
	public static final int COMMAND_CHANGE_IN_COINS               = 112; //找硬币
	public static final int COMMAND_CHANGE_IN_PAPER_MONEY        = 113; //找纸币
	public static final int COMMAND_BALANCE                        = 114; //上报余额

	//出货
	public static final int COMMAND_SHIPMENT_CASHPAY             = 120; //现金购买
	public static final int COMMAND_SHIPMENT_WECHATPAY           = 121; //微信支付出货
	public static final int COMMAND_SHIPMENT_ALIPAY              = 122; //支付宝出货
	public static final int COMMAND_SHIPMENT_GIFTS               = 123; //赠送出货
	public static final int COMMAND_SHIPMENT_REMOTE              = 124; //远程App出货   此次支付金额|货道号|状态
	public static final int COMMAND_SHIPMENT_VERIFY              = 125; //提货码出货  此次支付金额|货道号|状态
	public static final int COMMAND_SHIPMENT_BANKCARD_ONE       = 126; //刷银行卡1支付出货 MDB卡支付
	public static final int COMMAND_SHIPMENT_BANKCARD_TWO       = 127; //刷银行卡2支付出货
	public static final int COMMAND_SHIPMENT_TCNCARD_OFFLINE   = 128; //刷中吉IC卡离线支付出货
	public static final int COMMAND_SHIPMENT_TCNCARD_ONLINE    = 129; //刷中吉IC卡在线支付出货
	public static final int COMMAND_SHIPMENT_OTHER_PAY          = 130; //其它支付出货  此次支付金额|货道号|状态|支付方式


	public static final int COMMAND_SLOTNO_INFO                  = 144;

	public static final int COMMAND_SLOTNO_INFO_SINGLE                  = 145;

	public static final int CMD_LOOP                                     = 146;

	public static final int CMD_READ_TEMP                               = 147;


	public static final int COMMAND_SELECT_SLOTNO                        = 150; //选择货道
	public static final int COMMAND_INVALID_SLOTNO                       = 151;
	public static final int COMMAND_FAULT_SLOTNO                        = 152;
	public static final int COMMAND_SELECT_FAIL                        = 153;

	public static final int COMMAND_BUSY                              = 180;
	public static final int REQ_CMD_TEST_SLOT                          = 181;
	public static final int CMD_TEST_SLOT                              = 182; //测试货道

	public static final int REQ_QUERY_SLOT_STATUS                   = 190;
	public static final int QUERY_SLOT_STATUS                       = 191;

	public static final int REQ_SELF_CHECK                   = 194;
	public static final int SELF_CHECK                        = 195;
	public static final int REQ_CMD_RESET                   = 196;
	public static final int CMD_RESET                       = 197;
	public static final int REQ_SET_SLOTNO_SPRING                   = 198;
	public static final int SET_SLOTNO_SPRING                       = 199;
	public static final int REQ_SET_SLOTNO_BELTS                   = 200;
	public static final int SET_SLOTNO_BELTS                       = 201;
	public static final int REQ_SET_SLOTNO_ALL_SPRING                   = 202;
	public static final int SET_SLOTNO_ALL_SPRING                       = 203;
	public static final int REQ_SET_SLOTNO_ALL_BELT                   = 204;
	public static final int SET_SLOTNO_ALL_BELT                       = 205;
	public static final int REQ_SET_SLOTNO_SINGLE                   = 206;
	public static final int SET_SLOTNO_SINGLE                       = 207;
	public static final int REQ_SET_SLOTNO_DOUBLE                   = 208;
	public static final int SET_SLOTNO_DOUBLE                       = 209;
	public static final int REQ_SET_SLOTNO_ALL_SINGLE                   = 210;
	public static final int SET_SLOTNO_ALL_SINGLE                       = 211;
	public static final int REQ_SET_TEST_MODE                             = 212;
	public static final int SET_TEST_MODE                       = 213;

	public static final int REQ_SET_TEMP_CONTROL_OR_NOT             = 215;
	public static final int SET_TEMP_CONTROL_OR_NOT                  = 216;
	public static final int REQ_CMD_SET_COOL                          = 217;
	public static final int CMD_SET_COOL                               = 218;
	public static final int REQ_CMD_SET_HEAT                          = 219;
	public static final int CMD_SET_HEAT                               = 220;
	public static final int REQ_CMD_SET_TEMP                          = 221;
	public static final int CMD_SET_TEMP                              = 222;
	public static final int REQ_CMD_SET_GLASS_HEAT_OPEN                  = 223;
	public static final int CMD_SET_GLASS_HEAT_OPEN                       = 224;
	public static final int REQ_CMD_SET_GLASS_HEAT_CLOSE                  = 225;
	public static final int CMD_SET_GLASS_HEAT_CLOSE                       = 226;
	public static final int REQ_CMD_READ_CURRENT_TEMP               = 227;
	public static final int CMD_READ_CURRENT_TEMP                    = 228;
	public static final int REQ_CMD_SET_LIGHT_OPEN                   = 229;
	public static final int CMD_SET_LIGHT_OPEN                        = 230;
	public static final int REQ_CMD_SET_LIGHT_CLOSE                  = 231;
	public static final int CMD_SET_LIGHT_CLOSE                       = 232;
	public static final int REQ_CMD_SET_BUZZER_OPEN                  = 233;
	public static final int CMD_SET_BUZZER_OPEN                       = 234;
	public static final int REQ_CMD_SET_BUZZER_CLOSE                 = 235;
	public static final int CMD_SET_BUZZER_CLOSE                      = 236;
	public static final int REQ_CMD_SET_COOL_HEAT_CLOSE                 = 237;
	public static final int CMD_SET_COOL_HEAT_CLOSE                      = 238;
	public static final int REQ_CMD_READ_DOOR_STATUS                 = 239;
	public static final int CMD_READ_DOOR_STATUS                      = 240;

	public static final int CMD_SHIP_SLOT_ERRCODE_UPDATE      = 292;

	public static final int CMD_CHECK_SERIPORT = 495;

	public static final int CMD_NO_DATA_RECIVE       = 500;
}
