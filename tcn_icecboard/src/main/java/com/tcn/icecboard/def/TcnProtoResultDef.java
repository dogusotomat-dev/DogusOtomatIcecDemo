package com.tcn.icecboard.def;

/**
 * Created by Administrator on 2017/6/7.
 */
public class TcnProtoResultDef {
	public static final int CMD_NO_DATA_RECIVE        = -10;

    public static final int SHIP_SHIPING             = 1; //出货中
    public static final int SHIP_SUCCESS             = 2; //出货成功
    public static final int SHIP_FAIL                = 3; //出货失败

    public static final int DO_NONE   = -1;
    public static final int DO_START   = 0;
    public static final int DO_END   = 1;

    public static final int SUCCESS		            = 1;
    public static final int FAIL		            = 0;

    public static final int DOOR_OPEN     = 0;
    public static final int DOOR_CLOSE    = 1;

	public static final int DOOR_TAKE_GOODS_INVALID   = -1;
	public static final int DOOR_TAKE_GOODS_OPEN   = 1;     //开门
	public static final int DOOR_TAKE_GOODS_CLOSE   = 2;     //关门

	public static final int CLAPBOARD_INVALID   = -1;
	public static final int CLAPBOARD_OPEN   = 0;     //开门
	public static final int CLAPBOARD_CLOSE   = 1;     //关门


    public static final int STATUS_INVALID		             = -1;
    public static final int STATUS_FREE		             = 1;
    public static final int STATUS_BUSY		             = 2;
    public static final int STATUS_WAIT_TAKE_GOODS		 = 3;
    public static final int STATUS_HEATING         		 = 4;
    public static final int STATUS_HEATING_START		     = 5;
    public static final int STATUS_HEATING_END 		     = 6;
	public static final int STATUS_SHIPING                 = 7;
	public static final int STATUS_CLEAN                   = 8;
	public static final int STATUS_FAULT                  = 9;

	public static final int CMD_DETECT_LIGHT_INVALID        = -1;
	public static final int CMD_DETECT_LIGHT_BLOCKED        = 0;  //检测升降机光检   表示挡住
	public static final int CMD_DETECT_LIGHT_NOT_BLOCKED   = 1; //检测升降机光检   表示没挡住

	public static final int CMD_DETECT_SHIP_INVALID   = -1;
	public static final int CMD_DETECT_SHIP_NO_GOODS      = 0;  //0 表示没有货物，1 表示有货物
	public static final int CMD_DETECT_SHIP_HAVE_GOODS    = 1;  //0 表示没有货物，1 表示有货物


	public static final int DETECT_SWITCH_INPUT_CONNECT    = 0;     //0 表示接通，1 表示断开
	public static final int DETECT_SWITCH_INPUT_DISCONNECT  = 1;    //0 表示接通，1 表示断开


	public static final int BOARD_SPRING               = 5;    //弹簧机
	public static final int BOARD_LATTICE              = 6;    //格子机
	public static final int BOARD_LIFT_HEFAN_ZP          = 22;  //转盘盒饭机

	public static final int BOARD_ICE               = 20;  //冰淇淋


}
