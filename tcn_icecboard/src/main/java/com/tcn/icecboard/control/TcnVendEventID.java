package com.tcn.icecboard.control;

/**
 * 作者：Jiancheng,Song on 2016/5/28 16:19
 * 邮箱：m68013@qq.com
 */
public class TcnVendEventID {

	public static final int ACTION_RECEIVE_DATA             = 8;
	public static final int COMMAND_INPUT_MONEY             = 14;
	public static final int COMMAND_SHIPPING                = 15;
	public static final int COMMAND_SHIPMENT_SUCCESS        = 16;
	public static final int COMMAND_SHIPMENT_FAILURE        = 17;
	public static final int COMMAND_SHIPMENT_FAULT          = 18;
	public static final int COMMAND_SELECT_GOODS            = 19;
	public static final int COMMAND_INVALID_SLOTNO          = 20;
	public static final int COMMAND_SOLD_OUT                = 21;
	public static final int COMMAND_COIN_REFUND_START       = 22;
	public static final int COMMAND_COIN_REFUND_END         = 23;

	public static final int ADJUST_TIME_REQ                 = 33;
	public static final int UPDATE_PAY_TIME                 = 34;
	public static final int COMMAND_DOOR_SWITCH             = 38;

	public static final int	SERIAL_PORT_CONFIG_ERROR			    = 40;
	public static final int	SERIAL_PORT_SECURITY_ERROR			    = 41;
	public static final int	SERIAL_PORT_UNKNOWN_ERROR			    = 42;

	public static final int BACK_TO_SHOPPING =        47;

	public static final int COMMAND_CANCEL_PAY   = 48;
	public static final int COMMAND_TOSS_PAPER_MONEY = 49;

	public static final int COMMAND_FAULT_SLOTNO      = 51;


	public static final int	 PROMPT_INFO                          = 190;
	public static final int TEMPERATURE_INFO                   = 191;

	public static final int	RESTART_MAIN_ACTIVITY			    = 250;

	public static final int	PLESE_CLOSE_FOOR               = 270;

	public static final int COMMAND_SYSTEM_BUSY          = 280;

	public static final int MDB_RECIVE_PAPER_MONEY = 310;
	public static final int MDB_RECIVE_COIN_MONEY = 311;
	public static final int MDB_BALANCE_CHANGE = 312;
	public static final int MDB_PAYOUT_PAPERMONEY = 313;
	public static final int MDB_PAYOUT_COINMONEY = 314;

	public static final int MDB_SHORT_CHANGE_PAPER = 318;
	public static final int MDB_SHORT_CHANGE_COIN = 319;
	public static final int MDB_SHORT_CHANGE = 320;
	public static final int CMD_COIN_NO_CHANGE = 321;

	public static final int COMMAND_SELECT_FAIL            = 337;
	public static final int CMD_TEST_SLOT            = 338;

	public static final int CMD_QUERY_SLOT_FAULTS            = 340;
	public static final int CMD_CLEAR_SLOT_FAULTS            = 341;
	public static final int CMD_QUERY_SLOT_STATUS            = 342;
	public static final int CMD_QUERY_ADDRESS                = 343;
	public static final int CMD_SELF_CHECK                    = 344;
	public static final int CMD_RESET                    = 345;
	public static final int CMD_QUERY_CABINET_STATUS     = 346;

	public static final int SET_SLOTNO_SPRING                    = 350;
	public static final int SET_SLOTNO_BELTS                    = 351;
	public static final int SET_SLOTNO_ALL_SPRING                    = 352;
	public static final int SET_SLOTNO_ALL_BELT                    = 353;
	public static final int SET_SLOTNO_SINGLE                    = 354;
	public static final int SET_SLOTNO_DOUBLE                    = 355;
	public static final int SET_SLOTNO_ALL_SINGLE                    = 356;

	public static final int SET_TEMP_CONTROL_OR_NOT                  = 360;
	public static final int CMD_SET_COOL                               = 361;
	public static final int CMD_SET_HEAT                               = 362;
	public static final int CMD_SET_TEMP                              = 363;
	public static final int CMD_SET_GLASS_HEAT_OPEN                  = 364;
	public static final int CMD_SET_GLASS_HEAT_CLOSE                  = 365;
	public static final int CMD_READ_CURRENT_TEMP                    = 366;
	public static final int CMD_SET_LIGHT_OPEN                        = 367;
	public static final int CMD_SET_LIGHT_CLOSE                       = 368;
	public static final int CMD_SET_BUZZER_OPEN                       = 369;
	public static final int CMD_SET_BUZZER_CLOSE                      = 370;
	public static final int CMD_READ_DOOR_STATUS                      = 371;
	public static final int CMD_SET_COOL_HEAT_CLOSE                  = 372;


	public static final int CMD_LIFTER_MOVE_START                   = 377;
	public static final int CMD_LIFTER_MOVE_END                   = 378;
	public static final int CMD_QUERY_STATUS_LIFTER               = 380;
	public static final int CMD_TAKE_GOODS_DOOR                   = 381;
	public static final int CMD_LIFTER_UP                        = 382;
	public static final int CMD_LIFTER_BACK_HOME               = 383;
	public static final int CMD_CLAPBOARD_SWITCH               = 384;
	public static final int CMD_OPEN_COOL                       = 385;
	public static final int CMD_OPEN_HEAT                      = 386;
	public static final int CMD_CLOSE_COOL_HEAT               = 387;
	public static final int CMD_CLEAN_FAULTS                   = 388;
	public static final int CMD_QUERY_PARAMETERS               = 389;
	public static final int CMD_QUERY_DRIVER_CMD               = 390;
	public static final int CMD_SET_SWITCH_OUTPUT_STATUS       = 391;
	public static final int CMD_SET_ID                             = 392;
	public static final int CMD_SET_LIGHT_OUT_STEP               = 393;
	public static final int CMD_SET_PARAMETERS                    = 394;
	public static final int CMD_FACTORY_RESET                    = 395;
	public static final int CMD_DETECT_LIGHT                     = 396;
	public static final int CMD_DETECT_SHIP                       = 397;
	public static final int CMD_DETECT_SWITCH_INPUT               = 398;

	public static final int COMMAND_QUERY_PARAMETERS           = 399;

	public static final int CMD_TAKE_GOODS_FIRST              = 400;
	public static final int CMD_SHIP_FAIL_TAKE_GOODS_FIRST              = 401;

	public static final int CMD_MICOVEN_HEAT_OPEN                      = 403;
	public static final int CMD_MICOVEN_HEAT_CLOSE                    = 404;

	public static final int CMD_MACHINE_LOCKED           = 451;

	public static final int CMD_REQ_PERMISSION           = 600;

	public static final int CMD_CHECK_SERIPORT                   = 753;

	public static final int CMD_QUERY_STATUS_ICEC         = 1000;
	public static final int CMD_QUERY_STATUS_GOODS_TAKE     = 1001;
	public static final int CMD_SET_WORK_MODE    = 1002;
	public static final int CMD_PARAM_ICE_MAKE_SET    = 1003;
	public static final int CMD_PARAM_ICE_MAKE_QUERY    = 1004;
	public static final int CMD_PARAM_QUERY           = 1005;
	public static final int CMD_PARAM_SET             = 1006;
	public static final int CMD_POSITION_MOVE    = 1007;
	public static final int CMD_QUERY_STATUS_AND_JUDGE    = 1008;
	public static final int CMD_SELF_INSPECTION           = 1009;
	public static final int CMD_SELF_INSPECTION_STATUS    = 1010;//自检状态
	public static final int CMD_UPDATE_ICESHOW = 1011;//更新冰淇淋的显示界面
}
