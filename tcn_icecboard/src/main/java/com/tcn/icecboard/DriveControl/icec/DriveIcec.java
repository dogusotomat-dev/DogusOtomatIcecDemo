package com.tcn.icecboard.DriveControl.icec;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.tcn.icecboard.DriveControl.MyThread;
import com.tcn.icecboard.DriveControl.WriteThread;
import com.tcn.icecboard.DriveControl.MsgToSend;
import com.tcn.icecboard.TcnUtility;
import com.tcn.icecboard.vend.TcnLog;

public class DriveIcec {
	private static DriveIcec m_Instance = null;
	private static final String TAG = "DriveIcec";

	/// <summary>
	/// 包头
	/// </summary>
	private static final byte CMD_STX = 0x02;
	/// <summary>
	/// 包尾
	/// </summary>
	private static final byte CMD_ETX = 0x03;

	public static final int GROUP_SERIPORT_1 = 1;    //串口1
	public static final int GROUP_SERIPORT_2 = 2;    //串口2
	public static final int GROUP_SERIPORT_3 = 3;    //串口3
	public static final int GROUP_SERIPORT_4 = 4;    //串口4

	public static final int CMD_QUERY_STATUS = 1425;      //查询升降机状态及故障信息 0x01
	public static final int CMD_REQ_SHIP = 426;     //出货命令 0x02
	public static final int CMD_TAKE_GOODS_DOOR = 427;     //取货口门控制 0x03
	public static final int CMD_LIFTER_MOVE = 428;     //升降机上升 0x04
	public static final int CMD_LIFTER_BACK_HOME = 429;     //升降机回原点 0x05
	public static final int CMD_CLAPBOARD_SWITCH = 430;          //隔板开关 0x06
	public static final int CMD_OPEN_COOL = 431;          //其他控制命令 0x07
	public static final int CMD_OPEN_HEART = 432;          //其他控制命令 0x07
	public static final int CMD_CLOSE_COOL_HEAT = 433;          //其他控制命令 0x07
	public static final int CMD_QUERY_DRIVER_CMD = 436;         //查询驱动板回复命令 0x52
	public static final int CMD_SET_SWITCH_OUTPUT_STATUS = 437;         //设置开关量输出状态 0x53
	public static final int CMD_SET_ID = 438;         //设置升降机 ID 0x80


	public static final int CMD_INVALID = -1;
	public static final int CMD_SELECT_SLOTNO = 1800;   // 选择货道
	public static final int CMD_QUERY_STATUS_GOODS_TAKE = 1811;
	public static final int CMD_Wait_QUERY_STATUS = 1812;
	public static final int CMD_QUERY_STATUS_TO_SHIP = 1815;
	public static final int CMD_SHIP = 1816;
	public static final int CMD_SHIP_TEST = 1817;
	public static final int CMD_SELF_INSPECTION_STATUS = 1818;//自检状态
	public static final int CMD_SELF_INSPECTION_STATUS_LOOP = 1819;

	public static final int CMD_QUERY_STATUS_SHIP_RESULT = 1820;
	public static final int CMD_QUERY_STATUS_SHIP_RESULT_LOOP = 1821;

	public static final int CMD_QUERY_STATUS_SHIP_TEST_RESULT = 1825;
	public static final int CMD_QUERY_STATUS_SHIP_TEST_RESULT_LOOP = 1826;

	public static final int CMD_QUERY_STATUS_GOODS_TAKE_LOOP = 1827;

	public static final int CMD_SET_WORK_MODE = 1830;

	public static final int CMD_SELF_INSPECTION = 1834;//机器自检
	public static final int CMD_PARAM_ICE_MAKE_SET = 1835;       //制冰机参数设置
	public static final int CMD_PARAM_ICE_MAKE_QUERY = 1836;         //制冰机参数查询
	public static final int CMD_PARAM_ICE_MAKE_ALL_AUERY = 1837;       // 制冰机全部参数查询

	public static final int CMD_TEST_DISCHARGE = 1838;     // 测试出料


	public static final int CMD_PARAM_QUERY = 1840;
	public static final int CMD_PARAM_SET = 1841;
	public static final int CMD_POSITION_MOVE = 1842;

	public static final int CMD_QUERY_STATUS_AND_JUDGE = 1855;

	public static final int CMD_PARAM_ICE_MAKE_QUERY_LOOP = 1860;         //制冰机参数查询
	public static final int CMD_CLEAN_FAULTS = 1871;     // 清除故障
	public static final int CMD_SET_PARAMETERS = 1873;         //设置参数 0x82
	public static final int CMD_QUERY_PARAMETERS = 1872;          //查询参数 0x51


	public static final int CMD_BUSY = 1890;   // 系统忙


	public static final int SUCCESS = 0;
	public static final int FAIL = 1;


	public static final int STATUS_INVALID = -1;
	public static final int STATUS_FREE = 0; // 空闲
	public static final int STATUS_SHIPING = 1;// 出货中
	public static final int STATUS_WAIT_TAKE_GOODS = 2;// 等待取货中
	public static final int STATUS_CLEAN = 3; // 准备中
	public static final int STATUS_FAULT = 4;  // 故障中
	public static final int STATUS_SELEF_INSPECTION = 5;// 自检中

	public static final int SHIP_STATUS_INVALID = -1;
	public static final int SHIP_STATUS_SHIPING = 1;
	public static final int SHIP_STATUS_SUCCESS = 2;
	public static final int SHIP_STATUS_FAIL = 3;
	public static final int SHIP_STATUS_FAIL_WAIT_TAKE_GOODS = 4;
	public static final int SHIP_STATUS_SUCCESS_FREE = 5;

	private volatile int m_iCmdType = CMD_INVALID;
	private volatile int m_iQueryStatus = STATUS_INVALID;

	private volatile int m_bShipStatus = SHIP_STATUS_INVALID;
	private volatile int m_bShipTestStatus = SHIP_STATUS_INVALID;

	private volatile boolean m_isInited = false;

	private volatile StringBuilder m_read_sbuff = new StringBuilder();
	private volatile MsgToSend m_currentSendMsg = new MsgToSend();

	private Handler m_ReceiveHandler = null;

	private WriteThread m_WriteThread = null;
	private int m_iParmAddr;
	private int m_iNextCmd;


	public static synchronized DriveIcec getInstance() {
		if (null == m_Instance) {
			m_Instance = new DriveIcec();
		}
		return m_Instance;
	}

	public void init(Handler handlerReceive) {
		TcnLog.getInstance().LoggerDebug( TAG,  "init m_isInited: " + m_isInited);
		if (!m_isInited) {
			m_isInited = true;
			m_ReceiveHandler = handlerReceive;
			m_WriteThread = new WriteThread();
			m_WriteThread.setSendHandler(handlerReceive);
			m_WriteThread.startWriteThreads();
			m_ReceiveHandler.sendEmptyMessageDelayed(CMD_PARAM_ICE_MAKE_QUERY_LOOP, 10 * 1000);
		}
	}


	public void handleBusyMessage(MsgToSend msgToSend) {
		if ((null == m_ReceiveHandler) || (null == msgToSend)) {
			return;
		}

		TcnLog.getInstance().LoggerDebug(TAG, "init, handleBusyMessage cmdType: " + msgToSend.getCmdType() + " slotNo: " + msgToSend.getSlotNo() + " getAddrNum: " + msgToSend.getAddrNum());

		if (msgToSend.getCurrentCount() > msgToSend.getMaxCount()) {
			Message message = m_ReceiveHandler.obtainMessage();
			message.what = CMD_BUSY;
			message.arg1 = msgToSend.getCmdType();
			message.arg2 = -1;
			message.obj = msgToSend;
			m_ReceiveHandler.sendMessage(message);
			msgToSend.setErrCode(-1);
			if (CMD_QUERY_STATUS_TO_SHIP == msgToSend.getCmdType()) {
				Message msgShipFail = m_ReceiveHandler.obtainMessage();
				msgShipFail.what = CMD_QUERY_STATUS_SHIP_RESULT;
				msgShipFail.arg1 = msgToSend.getSlotNo();
				msgShipFail.arg2 = SHIP_STATUS_FAIL;
				msgShipFail.obj = msgToSend;
				m_ReceiveHandler.sendMessage(msgShipFail);
			} else if (CMD_SHIP_TEST == msgToSend.getCmdType()) {
				Message msgShipTestFail = m_ReceiveHandler.obtainMessage();
				msgShipTestFail.what = CMD_QUERY_STATUS_SHIP_TEST_RESULT;
				msgShipTestFail.arg1 = msgToSend.getSlotNo();
				msgShipTestFail.arg2 = SHIP_STATUS_FAIL;
				msgShipTestFail.obj = msgToSend;
				m_ReceiveHandler.sendMessage(msgShipTestFail);
			} else {

			}
			return;
		}
		long abTimeSpan = Math.abs(System.currentTimeMillis() - msgToSend.getCmdTime());
		if (msgToSend.getOverTimeSpan() < abTimeSpan) {
			Message message = m_ReceiveHandler.obtainMessage();
			message.what = CMD_BUSY;
			message.arg1 = msgToSend.getCmdType();
			message.arg2 = -1;
			message.obj = msgToSend;
			m_ReceiveHandler.sendMessage(message);

			msgToSend.setErrCode(-1);
			if (CMD_QUERY_STATUS_TO_SHIP == msgToSend.getCmdType()) {
				Message msgShipFail = m_ReceiveHandler.obtainMessage();
				msgShipFail.what = CMD_QUERY_STATUS_SHIP_RESULT;
				msgShipFail.arg1 = msgToSend.getSlotNo();
				msgShipFail.arg2 = SHIP_STATUS_FAIL;
				msgShipFail.obj = msgToSend;
				m_ReceiveHandler.sendMessage(msgShipFail);
			} else if (CMD_SHIP_TEST == msgToSend.getCmdType()) {
				Message msgShipTestFail = m_ReceiveHandler.obtainMessage();
				msgShipTestFail.what = CMD_QUERY_STATUS_SHIP_TEST_RESULT;
				msgShipTestFail.arg1 = msgToSend.getSlotNo();
				msgShipTestFail.arg2 = SHIP_STATUS_FAIL;
				msgShipTestFail.obj = msgToSend;
				m_ReceiveHandler.sendMessage(msgShipTestFail);
			} else {

			}
			return;
		}

		msgToSend.setCurrentCount(msgToSend.getCurrentCount() + 1);

		if (isBusy()) {
			Message message = m_ReceiveHandler.obtainMessage();
			message.what = CMD_BUSY;
			message.arg1 = msgToSend.getCmdType();
			message.arg2 = msgToSend.getSerialType();
			message.obj = msgToSend;
			m_ReceiveHandler.sendMessageDelayed(message, 50);
			return;
		}

		if (CMD_QUERY_STATUS == msgToSend.getCmdType()) {
			queryStatus(msgToSend);
		} else if (CMD_QUERY_STATUS_TO_SHIP == msgToSend.getCmdType()) {
			ship(msgToSend);
		} else if (CMD_SHIP_TEST == msgToSend.getCmdType()) {
			shipTest(msgToSend);
		} else if (CMD_SELECT_SLOTNO == msgToSend.getCmdType()) {
			selectSlotNo(msgToSend);

		} else {

		}

	}


	public boolean isCannotShipNext() {
		boolean bRet = false;
		if ((STATUS_SHIPING == m_iQueryStatus) || (STATUS_WAIT_TAKE_GOODS == m_iQueryStatus)
				|| (STATUS_CLEAN == m_iQueryStatus) || (STATUS_SELEF_INSPECTION == m_iQueryStatus)) {
			bRet = true;
		}
		return bRet;
	}

	public boolean isBusy() {
		boolean bRet = false;
		if (m_WriteThread != null) {
			bRet = m_WriteThread.isBusy();
		}
		return bRet;
	}



	private void sendBusyMessage(int serptGrp, int cmdType, int slotNo, int addrNum, int maxCount, byte grp, long cmdOverTimeSpan
			, String payMethod, String tradeNo, IceBean iceBean) {
		if (null == m_ReceiveHandler) {
			return;
		}
		long cTime = System.currentTimeMillis();
		MsgToSend msfSend = new MsgToSend(serptGrp, cmdType, slotNo, addrNum, 0, maxCount, -1, grp, cTime, cmdOverTimeSpan, payMethod, tradeNo, iceBean);
		Message message = m_ReceiveHandler.obtainMessage();
		message.what = CMD_BUSY;
		message.arg1 = cmdType;
		message.arg2 = serptGrp;
		message.obj = msfSend;
		m_ReceiveHandler.sendMessageDelayed(message, 50);
	}

	private MsgToSend getCurrentMessage(int serptGrp, int cmdType, int slotNo, int addrNum, int maxCount, byte grp, long cmdOverTimeSpan
			, String payMethod, String tradeNo, IceBean iceBean) {
		long cTime = System.currentTimeMillis();
		if (null == m_currentSendMsg) {
			m_currentSendMsg = new MsgToSend(serptGrp, cmdType, slotNo, addrNum, 0, maxCount, -1, grp, cTime, cmdOverTimeSpan, payMethod, tradeNo, iceBean);
		} else {
			m_currentSendMsg.setMsgToSend(serptGrp, cmdType, slotNo, addrNum, 0, maxCount, -1, grp, cTime, cmdOverTimeSpan, payMethod, tradeNo, iceBean);
		}
		return m_currentSendMsg;
	}

	private MsgToSend getCurrentMessage(int serptGrp, int cmdType, int slotNo, int addrNum, int maxCount, byte grp, long cmdOverTimeSpan
			, String payMethod, String tradeNo, IceBean iceBean, int param1, int param2) {
		long cTime = System.currentTimeMillis();
		if (null == m_currentSendMsg) {
			m_currentSendMsg = new MsgToSend(serptGrp, cmdType, slotNo, addrNum, 0, maxCount, -1, grp, cTime, cmdOverTimeSpan, payMethod, tradeNo, iceBean);
		} else {
			m_currentSendMsg.setMsgToSend(serptGrp, cmdType, slotNo, addrNum, 0, maxCount, -1, grp, cTime, cmdOverTimeSpan, payMethod, tradeNo, iceBean);
		}
		m_currentSendMsg.setPram1(param1);
		m_currentSendMsg.setPram2(param2);
		return m_currentSendMsg;
	}

	public void reqQueryStatus(int serptGrp, byte boardGrpNo) {
		if (isBusy()) {
			sendBusyMessage(serptGrp, CMD_QUERY_STATUS, m_currentSendMsg.getSlotNo(), m_currentSendMsg.getAddrNum(), 20, boardGrpNo, 2000, null, null, null);
			return;
		}

		m_currentSendMsg = getCurrentMessage(serptGrp, CMD_QUERY_STATUS, m_currentSendMsg.getSlotNo(), m_currentSendMsg.getAddrNum(), 20, boardGrpNo, 2000, null, null, null);

		m_iCmdType = CMD_QUERY_STATUS;

		sendCMD(m_iCmdType, (byte) 0x01, new byte[]{(byte) 0x01});

	}

	private void queryStatus(MsgToSend msgToSend) {
		if (isBusy()) {
			return;
		}
		m_currentSendMsg = msgToSend;

		m_iCmdType = CMD_QUERY_STATUS;

		sendCMD(m_iCmdType, (byte) 0x01, new byte[]{(byte) 0x01});
	}

	public void reqSelectSlotNo(int serptGrp, int slotNo, int addrSlotNo, byte boardGrpNo) {
		TcnLog.getInstance().LoggerInfo(TAG, "reqSelectSlotNo, slotNo: " + slotNo);

		if (isBusy()) {
			sendBusyMessage(serptGrp, CMD_SELECT_SLOTNO, slotNo, addrSlotNo, 30, boardGrpNo, 3000, null, null, null);
			return;
		}

		m_currentSendMsg = getCurrentMessage(serptGrp, CMD_SELECT_SLOTNO, slotNo, addrSlotNo, 30, boardGrpNo, 3000, null, null, null);

		m_iCmdType = CMD_SELECT_SLOTNO;

		sendCMD(m_iCmdType, (byte) 0x01, new byte[]{(byte) 0x01});
//		protocolAnalyse("021205010101020415FEFB0000000000000000170316");
	}

	private void selectSlotNo(MsgToSend msgToSend) {

		if (isBusy()) {
			sendBusyMessage(msgToSend.getSerialType(), CMD_SELECT_SLOTNO, msgToSend.getSlotNo(), msgToSend.getAddrNum(), 30, msgToSend.getBoardGrp(), 3000,
					msgToSend.getPayMethod(), msgToSend.getTradeNo(), msgToSend.getIceBean());
			return;
		}

		m_currentSendMsg = msgToSend;

		m_iCmdType = CMD_SELECT_SLOTNO;

		sendCMD(m_iCmdType, (byte) 0x01, new byte[]{(byte) 0x01});
	}


	private byte getCheckXorData(byte... datas) {
		byte bCheck = 0x00;
		if (null == datas) {
			return bCheck;
		}

		int iLength = datas.length;

		if (iLength < 1) {
			return bCheck;
		}

		byte temp = datas[0];

		for (int i = 1; i < datas.length; i++) {
			temp ^= datas[i];
		}

		return temp;
	}

	public void reqClearFaults(int serptGrp, byte boardGrpNo) {
		if (false) {
			Message message = m_ReceiveHandler.obtainMessage();
			message.what = CMD_CLEAN_FAULTS;
			m_iQueryStatus = Integer.parseInt("0", 16);
       /*         if ("00".equals(status)) {
                    m_iQueryStatus = STATUS_FREE;
                } else if ("01".equals(status)) {
                    m_iQueryStatus = STATUS_BUSY;
                } else if ("02".equals(status)) {
                    m_iQueryStatus = STATUS_WAIT_TAKE_GOODS;
                } else {
                    m_iQueryStatus = STATUS_INVALID;
                }*/
			message.arg1 = m_iQueryStatus;
			m_currentSendMsg.setErrCode(0
			);

			message.arg2 = 0;
			m_ReceiveHandler.sendMessage(message);
			return;
		}
		if (null == m_WriteThread) {
			return;
		}
		if (m_WriteThread.isBusy()) {
			return;
		}
		m_iCmdType = CMD_CLEAN_FAULTS;

		byte[] bCmdData = new byte[7];
		bCmdData[0] = (byte) 0x02;
		bCmdData[1] = (byte) 0x03;
		bCmdData[2] = (byte) 0x50;
		bCmdData[3] = boardGrpNo;
		bCmdData[4] = boardGrpNo;
		bCmdData[5] = (byte) 0x03;
		bCmdData[6] = getCheckXorData(bCmdData[0], bCmdData[1], bCmdData[2], bCmdData[3], bCmdData[4], bCmdData[5]);

		writeData(CMD_CLEAN_FAULTS, bCmdData);
	}


	/*
	 * zhuliao : 冰激凌主料口味  01-A口味  02-B口味  03-AB混合口味
	 *  guojiang: 果酱选择 00-不要果酱 01-1号果酱   02-2号果酱  03-3号果酱   04-4号果酱
	 *  dingliao: 顶料选择 00-不要顶料 01-1号顶料   02-2号顶料  03-3号顶料   04-4号顶料
	 *
	 *  zhuliaoQuantity  冰激凌主料量
	 *  guojiangQuantity  果酱选择
	 *  dingliaoQuantity  顶料选择
	 *
	 */
	public void reqShip(int serptGrp, int slotNo, int addrSlotNo, byte boardGrpNo, String payMethod, String tradeNo,
						int zhuliao, int guojiang, int dingliao, int zhuliaoQuantity, int guojiangQuantity, int dingliaoQuantity) {
		TcnLog.getInstance().LoggerInfo(TAG, "reqShip, slotNo: " + slotNo + " addrSlotNo: " + addrSlotNo + " zhuliao: " + zhuliao
				+ " guojiang: " + guojiang + " dingliao: " + dingliao + " zhuliaoQuantity: " + zhuliaoQuantity + " guojiangQuantity: " + guojiangQuantity + " dingliaoQuantity: " + dingliaoQuantity);

		IceBean mIceBean = new IceBean(zhuliao, guojiang, dingliao, zhuliaoQuantity, guojiangQuantity, dingliaoQuantity);
		if (false) {
			m_currentSendMsg = getCurrentMessage(serptGrp, CMD_QUERY_STATUS_TO_SHIP, slotNo, addrSlotNo, 100, boardGrpNo, 10000, payMethod, tradeNo, mIceBean);
			m_iCmdType = CMD_QUERY_STATUS_TO_SHIP;

			m_bShipStatus = SHIP_STATUS_SHIPING;
			sendMessage(CMD_QUERY_STATUS_SHIP_RESULT, slotNo, SHIP_STATUS_SHIPING, new MsgToSend(m_currentSendMsg));
			MyThread.getInstace().execute(new Runnable() {
				@Override
				public void run() {
					m_bShipStatus = SHIP_STATUS_FAIL;
					m_currentSendMsg.setErrCode(3);
					sendMessage(CMD_QUERY_STATUS_SHIP_RESULT, m_currentSendMsg.getSlotNo(), SHIP_STATUS_FAIL, new MsgToSend(m_currentSendMsg));
				}
			});

			return;
		}
		if (isBusy()) {
			sendBusyMessage(serptGrp, CMD_QUERY_STATUS_TO_SHIP, slotNo, addrSlotNo, 100, boardGrpNo, 10000, payMethod, tradeNo, mIceBean);
			return;
		}

		m_currentSendMsg = getCurrentMessage(serptGrp, CMD_QUERY_STATUS_TO_SHIP, slotNo, addrSlotNo, 100, boardGrpNo, 10000, payMethod, tradeNo, mIceBean);
		m_iCmdType = CMD_QUERY_STATUS_TO_SHIP;

		m_bShipStatus = SHIP_STATUS_SHIPING;
		sendMessage(CMD_QUERY_STATUS_SHIP_RESULT, slotNo, SHIP_STATUS_SHIPING, new MsgToSend(m_currentSendMsg));

		sendCMD(m_iCmdType, (byte) 0x01, new byte[]{(byte) 0x01});
	}

	private void ship(MsgToSend msgToSend) {

		if (isBusy()) {
			sendBusyMessage(msgToSend.getSerialType(), CMD_QUERY_STATUS_TO_SHIP, msgToSend.getSlotNo(), msgToSend.getAddrNum(), 100, msgToSend.getBoardGrp(), 10000,
					msgToSend.getPayMethod(), msgToSend.getTradeNo(), msgToSend.getIceBean());
			return;
		}
		m_currentSendMsg = msgToSend;
		m_iCmdType = CMD_QUERY_STATUS_TO_SHIP;

		m_bShipStatus = SHIP_STATUS_SHIPING;
		sendMessage(CMD_QUERY_STATUS_SHIP_RESULT, msgToSend.getSlotNo(), SHIP_STATUS_SHIPING, new MsgToSend(m_currentSendMsg));

		sendCMD(m_iCmdType, (byte) 0x01, new byte[]{(byte) 0x01});
	}


	/*
	 * zhuliao : 冰激凌主料口味  01-A口味  02-B口味  03-AB混合口味
	 *  guojiang: 果酱选择 00-不要果酱 01-1号果酱   02-2号果酱  03-3号果酱   04-4号果酱
	 *  dingliao: 顶料选择 00-不要顶料 01-1号顶料   02-2号顶料  03-3号顶料   04-4号顶料
	 *
	 *  zhuliaoQuantity  冰激凌主料量
	 *  guojiangQuantity  果酱选择
	 *  dingliaoQuantity  顶料选择
	 *
	 */
	public void reqShipTest(int serptGrp, int slotNo, int addrSlotNo, byte boardGrpNo,
							int zhuliao, int guojiang, int dingliao, int zhuliaoQuantity, int guojiangQuantity, int dingliaoQuantity) {
		TcnLog.getInstance().LoggerInfo(TAG, "reqShipTest, slotNo: " + slotNo + " addrSlotNo: " + addrSlotNo + " zhuliao: " + zhuliao
				+ " guojiang: " + guojiang + " dingliao: " + dingliao + " zhuliaoQuantity: " + zhuliaoQuantity + " guojiangQuantity: " + guojiangQuantity + " dingliaoQuantity: " + dingliaoQuantity);
		IceBean mIceBean = new IceBean(zhuliao, guojiang, dingliao, zhuliaoQuantity, guojiangQuantity, dingliaoQuantity);

		if (isBusy()) {
			sendBusyMessage(serptGrp, CMD_SHIP_TEST, slotNo, addrSlotNo, 100, boardGrpNo, 10000, m_currentSendMsg.getPayMethod(), m_currentSendMsg.getTradeNo(), mIceBean);
			return;
		}

		m_currentSendMsg = getCurrentMessage(serptGrp, CMD_SHIP_TEST, slotNo, addrSlotNo, 100, boardGrpNo, 10000,
				m_currentSendMsg.getPayMethod(), m_currentSendMsg.getTradeNo(), mIceBean);
		m_iCmdType = CMD_SHIP_TEST;

		m_bShipTestStatus = SHIP_STATUS_SHIPING;
		sendMessage(CMD_QUERY_STATUS_SHIP_TEST_RESULT, slotNo, SHIP_STATUS_SHIPING, new MsgToSend(m_currentSendMsg));

		writeData(m_iCmdType, getShipData(mIceBean.getZhuliao(), mIceBean.getGuojiang(), mIceBean.getDingliao(), mIceBean.getZhuliaoQuantity(), mIceBean.getGuojiangQuantity(), mIceBean.getDingliaoQuantity()));
	}

	private void shipTest(MsgToSend msgToSend) {

		if (isBusy()) {
			sendBusyMessage(msgToSend.getSerialType(), CMD_SHIP_TEST, msgToSend.getSlotNo(), msgToSend.getAddrNum(), 100, msgToSend.getBoardGrp(), 10000,
					msgToSend.getPayMethod(), msgToSend.getTradeNo(), msgToSend.getIceBean());
			return;
		}
		m_currentSendMsg = msgToSend;
		m_iCmdType = CMD_SHIP_TEST;

		m_bShipTestStatus = SHIP_STATUS_SHIPING;
		sendMessage(CMD_QUERY_STATUS_SHIP_TEST_RESULT, msgToSend.getSlotNo(), SHIP_STATUS_SHIPING, new MsgToSend(m_currentSendMsg));

		IceBean mIceBean = msgToSend.getIceBean();
		writeData(m_iCmdType, getShipData(mIceBean.getZhuliao(), mIceBean.getGuojiang(), mIceBean.getDingliao(), mIceBean.getZhuliaoQuantity(), mIceBean.getGuojiangQuantity(), mIceBean.getDingliaoQuantity()));
	}

	public void reqSetWorkMode(int workModeLeft, int workModeRight) {
		if (isBusy()) {
			return;
		}
		m_currentSendMsg = getCurrentMessage(m_currentSendMsg.getSerialType(), CMD_SET_WORK_MODE, m_currentSendMsg.getSlotNo(), m_currentSendMsg.getAddrNum(), 30, m_currentSendMsg.getBoardGrp(),
				5000, m_currentSendMsg.getPayMethod(), m_currentSendMsg.getTradeNo(), m_currentSendMsg.getIceBean(), workModeLeft, workModeRight);
		m_iCmdType = CMD_SET_WORK_MODE;
		writeData(m_iCmdType, getWorkModeData(workModeLeft, workModeRight));
	}

	public void reqSetParamIceMake(int positionCoolLeft, int positionCoolRight, int coolTempLeft, int coolTempRight, int coolStorage, int coolFree) {
		if (isBusy()) {
			return;
		}
		m_currentSendMsg = getCurrentMessage(m_currentSendMsg.getSerialType(), CMD_SET_WORK_MODE, m_currentSendMsg.getSlotNo(), m_currentSendMsg.getAddrNum(), 30, m_currentSendMsg.getBoardGrp(),
				5000, m_currentSendMsg.getPayMethod(), m_currentSendMsg.getTradeNo(), m_currentSendMsg.getIceBean(), positionCoolLeft, positionCoolRight);
		m_currentSendMsg.setValueInt(coolTempLeft);
		m_currentSendMsg.setDataInt(coolTempRight);
		m_currentSendMsg.setRow(coolStorage);
		m_currentSendMsg.setColumn(coolFree);
		m_iCmdType = CMD_PARAM_ICE_MAKE_SET;
		writeData(m_iCmdType, getIceMakeParamSetData(positionCoolLeft, positionCoolRight, coolTempLeft, coolTempRight, coolStorage, coolFree));
	}

	/**
	 * 测试出料
	 *
	 * @param testProject  测试项目
	 * @param testPosition 测试位置
	 */
	public void TestDischarge(int testProject, int testPosition) {
		Log.d(TAG, "TestDischarge");

		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_TEST_DISCHARGE;
		TcnLog.getInstance().LoggerDebug(TAG, "init m_iCmdType: " + m_iCmdType);
		writeData(m_iCmdType, getIceTestDischargeSetData(testProject, testPosition));
	}

	/*
	 * 制冰机参数查询
	 */
	public void reqQueryParamIceMake() {
//		TcnLog.getInstance().LoggerDebug("reqQueryParamIceMake",TAG,"init", "init m_iCmdType: " + m_iCmdType);
		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_PARAM_ICE_MAKE_QUERY;
		sendCMD(m_iCmdType, (byte) 0x05, new byte[]{(byte) 0x01});
	}

	/* 查询一类项目的全部参数
	 *
	 */
	public void reqDataAllQuery(int operaItem, int operaPosition) {

		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_PARAM_ICE_MAKE_ALL_AUERY;
		writeData(m_iCmdType, getParamSetData(operaItem, operaPosition, 0, 0));
	}

	/*
	 * 查询参数
	 */
	public void reqQueryParam(int operaItem, int operaPosition) {

		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_PARAM_QUERY;
		writeData(m_iCmdType, getParamSetData(operaItem, operaPosition, 0, 0));
	}

	/*
	 * 设置参数
	 */
	public void reqParamSet(int operaItem, int operaPosition, int data) {
		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_PARAM_SET;
		writeData(m_iCmdType, getParamSetData(operaItem, operaPosition, 1, data));

	}

	/*
	 * 移动
	 */
	public void reqMove(int operaPosition, int data) {
		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_POSITION_MOVE;
		writeData(m_iCmdType, getParamSetData(2, operaPosition, 2, data));
	}

	/*
	 * 状态查询与判断
	 */
	public void reqQueryStatusAndJudge() {
		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_QUERY_STATUS_AND_JUDGE;
		writeData(m_iCmdType, getQueryStatusAndJudge());
	}

	public void reqMachineSelf_test() {
		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_SELF_INSPECTION_STATUS;
		writeData(m_iCmdType, getMachineSelf_test());
	}

	private void ship(int zhuliao, int guojiang, int dingliao, int zhuliaoQuantity, int guojiangQuantity, int dingliaoQuantity) {
		m_iCmdType = CMD_SHIP;
		writeData(m_iCmdType, getShipData(zhuliao, guojiang, dingliao, zhuliaoQuantity, guojiangQuantity, dingliaoQuantity));
	}

	private byte[] getShipData(int zhuliao, int guojiang, int dingliao, int zhuliaoQuantity, int guojiangQuantity, int dingliaoQuantity) {
		byte[] mData = new byte[7];
		mData[0] = 0x01;   //01代表冰激凌机控制板
		mData[1] = Integer.valueOf(zhuliao).byteValue();   //冰激凌浆料类型   00-鲜奶 01-酸奶  02-0A：特殊类1-特殊8
		mData[2] = Integer.valueOf(guojiang).byteValue();   //果酱选择  00-不要果酱 01-1号果酱   02-2号果酱  03-3号果酱   04-4号果酱
		mData[3] = Integer.valueOf(dingliao).byteValue();   //顶料选择  00-不要顶料 01-1号顶料   02-2号顶料  03-3号顶料   04-4号顶料
		mData[4] = Integer.valueOf(zhuliaoQuantity).byteValue();   //冰激凌主料口味的量
		mData[5] = Integer.valueOf(guojiangQuantity).byteValue();   //果酱选择的量
		mData[6] = Integer.valueOf(dingliaoQuantity).byteValue();   //顶料选择的量
		byte[] cmdData = getCmdData((byte) 0x02, mData);

		return cmdData;
	}

	/*
	 * 工作模式设置
	 * workModeLeft 工作模式1： 00停止  01解冻 02清洗  03补料  04保鲜  05制作冰激凌
	 * workModeRight 工作模式2： 00停止  01解冻 02清洗  03补料  04保鲜  05制作冰激凌
	 */
	private byte[] getWorkModeData(int workModeLeft, int workModeRight) {
		if (workModeLeft < 0) {     //传无效值得时候，转换成FF
			workModeLeft = 255;
		}
		if (workModeRight < 0) {
			workModeRight = 255;
		}
		byte[] mData = new byte[4];
		mData[0] = 0x01;    //01代表冰激凌机控制板
		mData[1] = Integer.valueOf(workModeLeft).byteValue();    //左箱工作模式： 00停止  01解冻 02清洗  03补料  04保鲜  05制作冰激凌
		mData[2] = Integer.valueOf(workModeRight).byteValue();    //右箱工作模式： 00停止  01解冻 02清洗  03补料  04保鲜  05制作冰激凌
		mData[3] = 0x00;    //预留

		byte[] cmdData = getCmdData((byte) 0x03, mData);

		return cmdData;
	}

	/*
	 * 测试项目：00：无效 01：冰淇淋  02：果酱   03：顶料  04：杯子
	 * 测试位置： 00：无效  01：1号位置  02：二号位置  03：3号位置  04：4号位置
	 */
	private byte[] getIceTestDischargeSetData(int testproject, int testposition) {
		if (testposition < 0 && testposition > 4) {
			testposition = 0;
		}
		if (testproject < 0 && testproject > 4) {
			testproject = 0;
		}
		byte[] mData = new byte[3];
		mData[0] = 0x01;    //01代表冰激凌机控制板
		mData[1] = Integer.valueOf(testproject).byteValue();    //测试项目  0x01-0x0F
		mData[2] = Integer.valueOf(testposition).byteValue();    //测试位置  0x01-0x0F
		byte[] cmdData = getCmdData((byte) 0x04, mData);
		return cmdData;
	}

	/*
	 * 左制冰档位  0x01-0x0F
	 * 右制冰档位  0x01-0x0F
	 * 左冷冻缸保鲜温度 0-7度（实际-1-+6度）
	 * 右冷冻缸保鲜温度 0-7度（实际-1-+6度）
	 * 冰箱冷藏温度  3-6度
	 * 冰箱冷冻温度 coolFree -30-+10   -30 - +10度
	 */
	private byte[] getIceMakeParamSetData(int positionCoolLeft, int positionCoolRight, int coolTempLeft, int coolTempRight, int coolStorage, int coolFree) {
		if (positionCoolLeft < 0) {     //传无效值得时候，转换成FF
			positionCoolLeft = 255;
		}
		if (positionCoolRight < 0) {     //传无效值得时候，转换成FF
			positionCoolRight = 255;
		}
		if (coolTempLeft < 0) {     //传无效值得时候，转换成FF
			coolTempLeft = 255;
		}
		if (coolTempRight < 0) {     //传无效值得时候，转换成FF
			coolTempRight = 255;
		}
		if (coolStorage < 0) {     //传无效值得时候，转换成FF
			coolStorage = 255;
		}
		if (coolFree < -30) {     //传无效值得时候，转换成FF
			coolFree = 255;
		}
		byte[] mData = new byte[9];
		mData[0] = 0x01;    //01代表冰激凌机控制板
		mData[1] = Integer.valueOf(positionCoolLeft).byteValue();    //左制冰档位  0x01-0x0F
		mData[2] = Integer.valueOf(positionCoolRight).byteValue();    //右制冰档位  0x01-0x0F
		mData[3] = Integer.valueOf(coolTempLeft).byteValue();    //左冷冻缸保鲜温度 0-7度（实际-1-+6度）
		mData[4] = Integer.valueOf(coolTempRight).byteValue();    //右冷冻缸保鲜温度 0-7度（实际-1-+6度）
		mData[5] = Integer.valueOf(coolStorage).byteValue();    //冰箱冷藏温度  3-6度
		mData[6] = Integer.valueOf(coolFree).byteValue();    //冰箱冷冻温度 coolFree -30-+10   -30 - +10度
		mData[7] = 0x00;    //预留
		mData[8] = 0x00;    //预留

		byte[] cmdData = getCmdData((byte) 0x04, mData);

		return cmdData;
	}

	/*
	 * operaItem: 操作项目  0-无 1-电流设置  2-位置设置  3-时间设置
	 * operaPosition: 操作位置
	 * action: 动作 0-查询  1-存储   2-移动
	 * data: 设置数据
	 */
	private byte[] getParamSetData(int operaItem, int operaPosition, int action, int data) {
		if (operaItem < 0) {     //传无效值得时候，转换成FF
			operaItem = 255;
		}
		if (operaPosition < 0) {     //传无效值得时候，转换成FF
			operaPosition = 255;
		}
		if (action < 0) {     //传无效值得时候，转换成FF
			action = 255;
		}
		if (data < 0) {     //传无效值得时候，转换成FF
			data = 255;
		}
		byte[] mData = new byte[6];
		mData[0] = 0x01;    //01代表冰激凌机控制板
		mData[1] = Integer.valueOf(operaItem).byteValue();    //操作项目  0-无 1-电流设置  2-位置设置  3-时间设置  4-制冰机设置
		mData[2] = Integer.valueOf(operaPosition).byteValue();    //操作位置
		mData[3] = Integer.valueOf(action).byteValue();    //动作 0-查询  1-存储   2-移动
		mData[4] = Integer.valueOf(data / 256).byteValue();    //设置数据高8位
		mData[5] = Integer.valueOf(data % 256).byteValue();    //设置数据低8位

		byte[] cmdData = getCmdData((byte) 0x06, mData);

		return cmdData;
	}

	/*
	 *
	 *	状态显示
	 */
	private byte[] getQueryStatusAndJudge() {
		byte[] mData = new byte[1];
		mData[0] = 0x01;    //01代表冰激凌机控制板
		byte[] cmdData = getCmdData((byte) 0x07, mData);

		return cmdData;
	}

	/**
	 * 机器自检
	 */
	private byte[] getMachineSelf_test() {
		byte[] mData = new byte[1];
		mData[0] = 0x01;    //01代表冰激凌机控制板
		byte[] cmdData = getCmdData((byte) 0x08, mData);

		return cmdData;
	}

	private void sendCMD(int cmdType, byte CMD, byte[] data) {
		byte[] sendData = getCmdData(CMD, data);
		writeData(cmdType, sendData);
	}

	private void writeData(int cmdType, byte[] bytessMsg) {

		long cmdOverTimeSpan = 1000;
		boolean bNotShowLog = false;
		if ((CMD_SHIP == cmdType) || (CMD_SHIP_TEST == cmdType)) {
			cmdOverTimeSpan = 5000;
		} else {
			cmdOverTimeSpan = 3000;
		}
		m_read_sbuff.delete(0, m_read_sbuff.length());
		m_WriteThread.sendMsg(m_WriteThread.SERIAL_PORT_TYPE_1, cmdType, cmdOverTimeSpan, bytessMsg, bNotShowLog);
	}

	public void reqQueryParameters(int address, int serptGrp, byte boardGrpNo) {
		m_iParmAddr = address;
		if (address < 0) {
			return;
		}
		if (null == m_WriteThread) {
			return;
		}
		if (m_WriteThread.isBusy()) {
			return;
		}
		if (m_iParmAddr < 0) {
			return;
		}
		m_iCmdType = CMD_QUERY_PARAMETERS;

		byte[] bCmdData = new byte[8];
		bCmdData[0] = (byte) 0x02;
		bCmdData[1] = (byte) 0x04;
		bCmdData[2] = (byte) 0x51;
		bCmdData[3] = Integer.valueOf(serptGrp).byteValue();
		bCmdData[4] = Integer.valueOf(address).byteValue();
		bCmdData[5] = (byte) (bCmdData[3] + bCmdData[4]);
		bCmdData[6] = (byte) 0x03;
		bCmdData[7] = getCheckXorData(bCmdData[0], bCmdData[1], bCmdData[2], bCmdData[3], bCmdData[4], bCmdData[5], bCmdData[6]);
		Log.d("DriveIcec", "reqQueryParameters: "+ TcnUtility.bytesToHexString(bCmdData));
		writeData(CMD_QUERY_PARAMETERS, bCmdData);
	}

	public void reqSetParameters(int addr, int parameters, int serptGrp, byte boardGrpNo) {
		if (null == m_WriteThread) {
			return;
		}
		if (m_WriteThread.isBusy()) {
			return;
		}
		m_iCmdType = CMD_SET_PARAMETERS;
		int iValueData = parameters;
		if (iValueData < 0) {
			iValueData = 65536 - Math.abs(parameters);
		}
		String hexParameters2Bytes = TcnUtility.deciToHexData(iValueData);
		if (hexParameters2Bytes.length() < 1) {
			return;
		}
		if (hexParameters2Bytes.length() == 1) {
			hexParameters2Bytes = "000" + hexParameters2Bytes;
		} else if (hexParameters2Bytes.length() == 2) {
			hexParameters2Bytes = "00" + hexParameters2Bytes;
		} else if (hexParameters2Bytes.length() == 3) {
			hexParameters2Bytes = "0" + hexParameters2Bytes;
		} else {

		}
		byte[] bArrParam = TcnUtility.hexStringToBytes(hexParameters2Bytes);

		if ((null == bArrParam) || (bArrParam.length != 2)) {
			return;
		}

		byte[] bCmdData = new byte[10];
		bCmdData[0] = (byte) 0x02;
		bCmdData[1] = (byte) 0x06;
		bCmdData[2] = (byte) 0x82;
		bCmdData[3] = Integer.valueOf(serptGrp).byteValue();
		bCmdData[4] = Integer.valueOf(addr).byteValue();
		bCmdData[5] = bArrParam[0];
		bCmdData[6] = bArrParam[1];
		bCmdData[7] = (byte) (bCmdData[3] + bCmdData[4] + bCmdData[5] + bCmdData[6]);
		bCmdData[8] = (byte) 0x03;
		bCmdData[9] = getCheckXorData(bCmdData[0], bCmdData[1], bCmdData[2], bCmdData[3], bCmdData[4], bCmdData[5], bCmdData[6], bCmdData[7], bCmdData[8]);
		writeData(CMD_SET_PARAMETERS, bCmdData);
	}

	/*
	 * 信息通讯包格式：
	 * 命令头	通迅包长度	命令	数据包	EXT	BCC
	 * 0x02	1 byte	1 byte	n byte	0x03	1 byte
	 */
	private byte[] getCmdData(byte CMD, byte[] data) {
		byte[] CmdData = null;
		if (data != null) {
			CmdData = new byte[data.length + 6];
			System.arraycopy(data, 0, CmdData, 3, data.length);
			CmdData[CmdData.length - 3] = getSumVerify(data);
		} else {
			CmdData = new byte[5];
		}
		CmdData[0] = CMD_STX;
		CmdData[1] = (byte) (CmdData.length - 4);
		CmdData[2] = CMD;

		CmdData[CmdData.length - 2] = CMD_ETX;
		byte[] sum = new byte[CmdData.length - 1];


		System.arraycopy(CmdData, 0, sum, 0, sum.length);
		CmdData[CmdData.length - 1] = getBCC(sum);
		return CmdData;
	}

	private byte getBCC(byte[] data) {
		if (data == null) return 0;
		int length = data.length;
		if (length == 0) return 0;
		if (length == 1) return data[0];
		byte bcc = data[0];
		for (int i = 1; i < length; i++) {
			bcc ^= data[i];
		}
		return bcc;
	}

	public byte getSumVerify(byte[] data) {
		byte by = 0;
		for (int i = 0; i < data.length; i++) {
			by += data[i];
		}
		return (byte) (by);
	}

	private void sendMessage(int what, int arg1, int arg2, Object object) {
		if (null == m_ReceiveHandler) {
			return;
		}
		Message message = m_ReceiveHandler.obtainMessage();
		message.what = what;
		message.arg1 = arg1;
		message.arg2 = arg2;
		message.obj = object;
		m_ReceiveHandler.sendMessage(message);
	}

	// 自检开始SELEF_INSPECTION
	private void reqQuerySelefInspectionStatusWhenShip() {
//		TcnLog.getInstance().LoggerError(TAG, "reqQuerySelefInspectionStatusWhenShip", "reqQuerySelefInspectionStatusWhenShip() m_iQueryStatus=" + m_iQueryStatus);
		if (isBusy()) {
			return;
		}
//		TcnLog.getInstance().LoggerError(TAG, "reqQuerySelefInspectionStatusWhenShip", "reqQuerySelefInspectionStatusWhenShip() ！isBuys;m_iQueryStatus=" + m_iQueryStatus);
		m_iCmdType = CMD_SELF_INSPECTION_STATUS;
		sendCMD(m_iCmdType, (byte) 0x01, new byte[]{(byte) 0x01});
	}

	public void reqQuerySelefInspectionStatusDelay() {
//		TcnLog.getInstance().LoggerError(TAG, "reqQuerySelefInspectionStatusDelay", "reqQuerySelefInspectionStatusDelay() m_iQueryStatus=" + m_iQueryStatus);
		if ((STATUS_FREE == m_iQueryStatus) || (STATUS_FAULT == m_iQueryStatus)) {
			return;
		}
		reqQuerySelefInspectionStatusWhenShip();
		if (m_ReceiveHandler != null) {
			m_ReceiveHandler.removeMessages(CMD_SELF_INSPECTION_STATUS_LOOP);
			m_ReceiveHandler.sendEmptyMessageDelayed(CMD_SELF_INSPECTION_STATUS_LOOP, 500);
		}
	}

	public void removeQuerySelefInspectionStatus() {
		if (m_ReceiveHandler != null) {
			m_ReceiveHandler.removeMessages(CMD_SELF_INSPECTION_STATUS_LOOP);
		}
	}
	//自检结束

	private void reqQueryShipStatusWhenShip() {
		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_QUERY_STATUS_SHIP_RESULT;
		sendCMD(m_iCmdType, (byte) 0x01, new byte[]{(byte) 0x01});
	}

	public void reqQueryShipStatusDelay() {
		if ((STATUS_FREE == m_iQueryStatus) || (STATUS_FAULT == m_iQueryStatus)) {
			return;
		}
		reqQueryShipStatusWhenShip();
		if (m_ReceiveHandler != null) {
			m_ReceiveHandler.removeMessages(CMD_QUERY_STATUS_SHIP_RESULT_LOOP);
			m_ReceiveHandler.sendEmptyMessageDelayed(CMD_QUERY_STATUS_SHIP_RESULT_LOOP, 500);
		}
	}

	public void removeQueryShipStatus() {
		if (m_ReceiveHandler != null) {
			m_ReceiveHandler.removeMessages(CMD_QUERY_STATUS_SHIP_RESULT_LOOP);
		}
	}

	private void reqQueryShipStatusWhenShipTest() {
		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_QUERY_STATUS_SHIP_TEST_RESULT;
		sendCMD(m_iCmdType, (byte) 0x01, new byte[]{(byte) 0x01});
	}

	public void reqQueryShipTestStatusDelay() {
		if ((STATUS_FREE == m_iQueryStatus) || (STATUS_FAULT == m_iQueryStatus)) {
			return;
		}
		reqQueryShipStatusWhenShipTest();
		if (m_ReceiveHandler != null) {
			m_ReceiveHandler.removeMessages(CMD_QUERY_STATUS_SHIP_TEST_RESULT_LOOP);
			m_ReceiveHandler.sendEmptyMessageDelayed(CMD_QUERY_STATUS_SHIP_TEST_RESULT_LOOP, 500);
		}
	}

	public void removeQueryShipTestStatus() {
		if (m_ReceiveHandler != null) {
			m_ReceiveHandler.removeMessages(CMD_QUERY_STATUS_SHIP_TEST_RESULT_LOOP);
		}
	}

	private void reqQueryTakeGoodsStatus() {
		if (isBusy()) {
			return;
		}
		m_iCmdType = CMD_QUERY_STATUS_GOODS_TAKE;
		sendCMD(m_iCmdType, (byte) 0x01, new byte[]{(byte) 0x01});
	}

	public void reqQueryTakeGoodsStatusDelay() {
		if (STATUS_WAIT_TAKE_GOODS != m_iQueryStatus) {
			return;
		}
		reqQueryTakeGoodsStatus();
		if (m_ReceiveHandler != null) {
			m_ReceiveHandler.removeMessages(CMD_QUERY_STATUS_GOODS_TAKE_LOOP);
			m_ReceiveHandler.sendEmptyMessageDelayed(CMD_QUERY_STATUS_GOODS_TAKE_LOOP, 500);
		}
	}

	public void removeQueryTakeGoodsStatus() {
		if (m_ReceiveHandler != null) {
			m_ReceiveHandler.removeMessages(CMD_QUERY_STATUS_GOODS_TAKE_LOOP);
		}
	}


	public void reqQueryParamIceMakeStatusDelay() {
		if ((STATUS_SHIPING == m_iQueryStatus)) {

		} else {
			reqQueryParamIceMake();
		}

		if (m_ReceiveHandler != null) {
			m_ReceiveHandler.removeMessages(CMD_PARAM_ICE_MAKE_QUERY_LOOP);
			m_ReceiveHandler.sendEmptyMessageDelayed(CMD_PARAM_ICE_MAKE_QUERY_LOOP, 60 * 1000);
		}
	}

	public void protocolAnalyse(String strCmdData) {
		if ((null == strCmdData) || (strCmdData.length() <= 0)) {
			TcnLog.getInstance().LoggerError(TAG,  "protocolAnalyse() strCmdData: " + strCmdData + " m_read_sbuff: " + m_read_sbuff);
			return;
		}
		TcnLog.getInstance().LoggerError(TAG, "protocolAnalyse() strCmdData: " + strCmdData + " m_read_sbuff: " + m_read_sbuff);
		m_read_sbuff.append(strCmdData);

		try {
			while ((m_read_sbuff.length()) >= 16) {// 021205010101020415FEFB0000000000000000170316
				int indexS = m_read_sbuff.indexOf("02");//1
				int indexE = m_read_sbuff.indexOf("03");//41
				if (indexE >= 0) {
					if ((indexS > indexE) && ((indexS % 2) == 0) && ((indexE % 2) == 0)) {
						// 处理  031102
						m_read_sbuff.delete(0, indexS);
						TcnLog.getInstance().LoggerDebug(TAG,  "protocolAnalyse() Error indexS: " + indexS + " indexE: " + indexE + " m_read_sbuff: " + m_read_sbuff);
					} else {
						if (indexS < 0) {
							m_read_sbuff.delete(0, m_read_sbuff.length());
							continue;
						}
						if ((indexS % 2) != 0) {
							if (m_read_sbuff.length() > (indexS + 2)) {
								m_read_sbuff.delete(0, indexS + 2);
							} else {
								m_read_sbuff.delete(0, m_read_sbuff.length());
							}
							continue;
						}
						if (m_read_sbuff.length() < (indexS + 12)) {
							break;
						}
						String strLength = m_read_sbuff.substring(indexS + 2, indexS + 4);
						int iLength = Integer.valueOf(strLength, 16);
						if (iLength > 35) {  //超过了命令字节长度
							m_read_sbuff.delete(0, indexS + 2);
							continue;
						}
						String subStr = m_read_sbuff.substring(indexS + 4);
						if (subStr.length() >= (iLength * 2 + 4)) {
							subStr = subStr.substring(iLength * 2, iLength * 2 + 2);
							if (subStr.equals("03")) {
								String strCmd = m_read_sbuff.substring(indexS, indexS + iLength * 2 + 8);
								commondAnalyse(m_iCmdType, strCmd);
								if ((indexS + iLength * 2 + 8) < m_read_sbuff.length()) {
									m_read_sbuff.delete(0, indexS + iLength * 2 + 8);
								} else if ((indexS + iLength * 2 + 8) == m_read_sbuff.length()) {
									m_read_sbuff.delete(0, m_read_sbuff.length());
								} else {

								}
							} else {
								int indexS2 = m_read_sbuff.indexOf("02", indexS + 2);
								if (indexS2 > 0) {
									m_read_sbuff.delete(0, indexS2);
								} else {
									m_read_sbuff.delete(0, m_read_sbuff.length());
								}
							}
						} else {
							TcnLog.getInstance().LoggerDebug(TAG,  "protocolAnalyse() subStr break m_read_sbuff: " + m_read_sbuff);
							break;
						}
					}
				} else {
					TcnLog.getInstance().LoggerDebug(TAG,  "protocolAnalyse() break m_read_sbuff: " + m_read_sbuff);
					break;
				}
			}

		} catch (Exception e) {
			TcnLog.getInstance().LoggerError(TAG,  "protocolAnalyse Exception  e: " + e + " m_read_sbuff: " + m_read_sbuff);
			if (m_read_sbuff != null) {
				m_read_sbuff.delete(0, m_read_sbuff.length());
			}
		}
	}

	private void commondAnalyse(int cmdType, String cmdData) {

		if (null == m_ReceiveHandler) {
			TcnLog.getInstance().LoggerError(TAG, "commondAnalyse, m_ReceiveHandler is null");
			return;
		}

		TcnLog.getInstance().LoggerInfo(TAG, "commondAnalyse, cmdType: " + cmdType + " cmdData: " + cmdData);

		m_iCmdType = CMD_INVALID;
		m_WriteThread.setBusy(false);
//		String cmd = cmdData.substring(4, 6);    //命令号
		int cmd = Integer.parseInt(cmdData.substring(4, 6), 16);    //命令号
		String addr = cmdData.substring(6, 8);    //设备号：   01代表冰激凌机控制板
		String dataContent = cmdData.substring(8, cmdData.length() - 6);

		switch (cmd) {
			case 1:         //状态查询
				int cdmlength = Integer.parseInt(cmdData.substring(2, 4));
				String status = dataContent.substring(0, 2);     //00-空闲  01-出货中  02-等待取货中  03-准备中   04-故障中  05-自检中
				m_iQueryStatus = Integer.parseInt(status, 16);
				String errorCodes = "";
				int iErrCode = 0;
				switch (cdmlength) {
					case 5:
						iErrCode = Integer.parseInt(dataContent.substring(2, 4), 16); //故障码
						errorCodes = dataContent.substring(2, 4);
						break;
					case 6:
						iErrCode = Integer.parseInt(dataContent.substring(2, 6), 16); //故障码
						errorCodes = dataContent.substring(2, 6);
						break;
					case 7:
						iErrCode = Integer.parseInt(dataContent.substring(2, 8), 16); //故障码
						errorCodes = dataContent.substring(2, 8);
						break;
					case 8:
						iErrCode = Integer.parseInt(dataContent.substring(2, 10), 16); //故障码
						errorCodes = dataContent.substring(2, 10);
						break;
					case 9:
						iErrCode = Integer.parseInt(dataContent.substring(2, 12), 16); //故障码
						errorCodes = dataContent.substring(2, 12);
						break;
				}
//				iErrCode = Integer.parseInt(dataContent.substring(2,4),16); //故障码

				TcnLog.getInstance().LoggerInfo(TAG, "commondAnalyse, m_iQueryStatus: " + m_iQueryStatus + " getSlotNo: " + m_currentSendMsg.getSlotNo());

				if (CMD_SELECT_SLOTNO == cmdType) {
					m_currentSendMsg.setErrCode(iErrCode);
					sendMessage(CMD_SELECT_SLOTNO, m_currentSendMsg.getSlotNo(), m_iQueryStatus, new MsgToSend(m_currentSendMsg));
				} else if (CMD_QUERY_STATUS_TO_SHIP == cmdType) {
					IceBean mIceBean = m_currentSendMsg.getIceBean();
					ship(mIceBean.getZhuliao(), mIceBean.getGuojiang(), mIceBean.getDingliao(), mIceBean.getZhuliaoQuantity(), mIceBean.getGuojiangQuantity(), mIceBean.getDingliaoQuantity());
				} else if (CMD_QUERY_STATUS_SHIP_RESULT == cmdType) {
					if ((STATUS_FREE == m_iQueryStatus)) {
						removeQueryShipStatus();
						if (SHIP_STATUS_SUCCESS == m_bShipStatus) {
							//
						} else {
							// 购买完成后查询料盒状态
							m_bShipStatus = SHIP_STATUS_SUCCESS;
							sendMessage(CMD_QUERY_STATUS_SHIP_RESULT, m_currentSendMsg.getSlotNo(), SHIP_STATUS_SUCCESS, new MsgToSend(m_currentSendMsg));
							reqQueryParamIceMake();
						}
					} else if ((STATUS_FAULT == m_iQueryStatus)) {
						removeQueryShipStatus();
						if (SHIP_STATUS_FAIL == m_bShipStatus) {

						} else {
							m_bShipStatus = SHIP_STATUS_FAIL;
							m_currentSendMsg.setErrCode(iErrCode);
							TcnLog.getInstance().LoggerError(TAG, "commondAnalyse, isErrCode=" + iErrCode);
							sendMessage(CMD_QUERY_STATUS_SHIP_RESULT, m_currentSendMsg.getSlotNo(), SHIP_STATUS_FAIL, new MsgToSend(m_currentSendMsg));
						}

					} else if ((STATUS_WAIT_TAKE_GOODS == m_iQueryStatus)) {
						removeQueryShipStatus();
						if (SHIP_STATUS_SUCCESS != m_bShipStatus) {
							m_bShipStatus = SHIP_STATUS_SUCCESS;
							sendMessage(CMD_QUERY_STATUS_SHIP_RESULT, m_currentSendMsg.getSlotNo(), SHIP_STATUS_SUCCESS, new MsgToSend(m_currentSendMsg));
						}
					} else {

					}
				} else if (CMD_QUERY_STATUS_SHIP_TEST_RESULT == cmdType) {
					if ((STATUS_FREE == m_iQueryStatus)) {
						removeQueryShipTestStatus();
						if (SHIP_STATUS_SUCCESS == m_bShipTestStatus) {

						} else {
							m_bShipTestStatus = SHIP_STATUS_SUCCESS;
							sendMessage(CMD_QUERY_STATUS_SHIP_TEST_RESULT, m_currentSendMsg.getSlotNo(), SHIP_STATUS_SUCCESS, new MsgToSend(m_currentSendMsg));
						}
					} else if ((STATUS_FAULT == m_iQueryStatus)) {
						removeQueryShipTestStatus();
						if (SHIP_STATUS_FAIL == m_bShipTestStatus) {

						} else {
							m_currentSendMsg.setErrCode(iErrCode);
							m_bShipTestStatus = SHIP_STATUS_FAIL;
							sendMessage(CMD_QUERY_STATUS_SHIP_TEST_RESULT, m_currentSendMsg.getSlotNo(), SHIP_STATUS_FAIL, new MsgToSend(m_currentSendMsg));
						}

					} else if ((STATUS_WAIT_TAKE_GOODS == m_iQueryStatus)) {
						removeQueryShipTestStatus();
						if (SHIP_STATUS_SUCCESS != m_bShipTestStatus) {
							m_bShipTestStatus = SHIP_STATUS_SUCCESS;
							sendMessage(CMD_QUERY_STATUS_SHIP_TEST_RESULT, m_currentSendMsg.getSlotNo(), SHIP_STATUS_SUCCESS, new MsgToSend(m_currentSendMsg));
						}
					} else {

					}
				} else if (CMD_QUERY_STATUS == cmdType) {
					m_currentSendMsg.setErrCode(iErrCode);
					sendMessage(CMD_QUERY_STATUS, m_iQueryStatus, iErrCode, new MsgToSend(m_currentSendMsg));
				} else if (CMD_QUERY_STATUS_GOODS_TAKE == cmdType) {
					sendMessage(CMD_QUERY_STATUS_GOODS_TAKE, m_iQueryStatus, iErrCode, new MsgToSend(m_currentSendMsg));
				} else if (CMD_SELF_INSPECTION_STATUS == cmdType) {
					// 自检后查询
					m_currentSendMsg.setErrCode(iErrCode);
					sendMessage(CMD_SELF_INSPECTION_STATUS, m_iQueryStatus, -1, errorCodes);
					if (m_iQueryStatus == 0 || m_iQueryStatus == 4) {
						removeQuerySelefInspectionStatus();
					}
				} else {

				}

				if ((STATUS_WAIT_TAKE_GOODS == m_iQueryStatus)) {
					reqQueryTakeGoodsStatusDelay();
				}

				break;
			case 2:         //购买出货
				m_iQueryStatus = STATUS_INVALID;
				if (CMD_SHIP == cmdType) {
					reqQueryShipStatusDelay();
				} else if (CMD_SHIP_TEST == cmdType) {
					reqQueryShipTestStatusDelay();
				} else {

				}
				break;
			case 3:         //工作模式设置
				int recvStatus = Integer.parseInt(dataContent.substring(0, 2), 16);
				if (0 == recvStatus) {  //接收指令成功
					sendMessage(CMD_SET_WORK_MODE, SUCCESS, m_currentSendMsg.getPram1(), new MsgToSend(m_currentSendMsg));
				} else {
					sendMessage(CMD_SET_WORK_MODE, FAIL, m_currentSendMsg.getPram1(), new MsgToSend(m_currentSendMsg));
				}
				break;
			case 4:         //测试出料
				int recvMakeSetParamStatus = Integer.parseInt(dataContent.substring(0, 2), 16);
				if (0 == recvMakeSetParamStatus) {  //接收指令成功
					sendMessage(CMD_TEST_DISCHARGE, SUCCESS, m_currentSendMsg.getPram1(), new MsgToSend(m_currentSendMsg));
				} else {
					sendMessage(CMD_TEST_DISCHARGE, FAIL, m_currentSendMsg.getPram1(), new MsgToSend(m_currentSendMsg));
				}

				break;
			case 5:         //参数查询    （界面只供显示，不操作）
				int NewsletterState = Integer.parseInt(dataContent.substring(0, 2), 16);// 通讯状态
				int WorkMode = Integer.parseInt(dataContent.substring(2, 4), 16); // 工作模式
				int PuffingGear = Integer.parseInt(dataContent.substring(4, 6), 16); // 膨化挡位
				int RefrigerationGear = Integer.parseInt(dataContent.substring(6, 8), 16);// 制冷档位
				int TroughTemperature = TcnUtility.hex2StringToDecimal(dataContent.substring(8, 10)); // 料槽温度
				int TankTemperature = TcnUtility.hex2StringToDecimal(dataContent.substring(10, 12)); // 搅拌缸温度
				int SetPreservationTemperature = TcnUtility.hex2StringToDecimal(dataContent.substring(12, 14)); // 保鲜温度设定值
				int IceCreamMakingStatus = Integer.parseInt(dataContent.substring(14, 16), 16);// 雪糕制作状态
				int StatusCode = Integer.parseInt(dataContent.substring(16, 20), 16);// 状态代码
				int MachineCode = Integer.parseInt(dataContent.substring(20, 24), 16);//整机代码
				int MilkCupStockCondition = Integer.parseInt(dataContent.substring(24, 26), 16);//奶浆/杯子料况
				int JamMaterialConditions = Integer.parseInt(dataContent.substring(26, 28), 16);// 果酱料况
				int TopMaterialCondition = Integer.parseInt(dataContent.substring(28, 30), 16);// 顶料料况
//				IceMakeParamBean mIceMakeParamBean = new IceMakeParamBean(GridVoltage,RefriTemp,RefriFreezTemp,RefriCylinderTemp1,RefriCylinderTemp2,RefriCylinderPro1,RefriCylinderPro2,
//						RefriFault1,RefriFault2,machineFault,Refri1WorkMode,Refri2WorkMode,LoadOutputState1,LoadOutputState2);
				IcecParameter mIcecParameter = new IcecParameter(WorkMode, NewsletterState, PuffingGear, RefrigerationGear, TroughTemperature, TankTemperature, SetPreservationTemperature,
						IceCreamMakingStatus, StatusCode, MachineCode, MilkCupStockCondition, JamMaterialConditions, TopMaterialCondition);
//				m_currentSendMsg.setIceMakeParamBean(mIceMakeParamBean);
				Gson gson = new Gson();
				String strJson = gson.toJson(mIcecParameter);//把对象转为JSON格式的字符串
				TcnLog.getInstance().LoggerDebug( TAG, "commondAnalys, 参数返回：" + strJson);
				sendMessage(CMD_PARAM_ICE_MAKE_QUERY, m_currentSendMsg.getPram1(), m_currentSendMsg.getPram2(), strJson);
				break;
			case 6:         //参数设置       （进此界面需维护密码，不对客户开放）
				//0208060100020209ABB90315
				int recvSetParamStatus = Integer.parseInt(dataContent.substring(0, 2), 16);
				if (0 == recvSetParamStatus) {  //接收指令成功
					int operaItem = Integer.parseInt(dataContent.substring(2, 4), 16);
					int operaPosition = Integer.parseInt(dataContent.substring(4, 6), 16);
					int data = (int) TcnUtility.hexStringToDecimal(dataContent.substring(6, 10));
					IceParamBean mIceParamBean = new IceParamBean(operaItem, operaPosition, data);
					Gson gsonPatam = new Gson();
					String strJsonParam = gsonPatam.toJson(mIceParamBean);//把对象转为JSON格式的字符串
					if (CMD_PARAM_QUERY == cmdType) {
						sendMessage(CMD_PARAM_QUERY, SUCCESS, -1, strJsonParam);
					} else if (CMD_PARAM_SET == cmdType) {
						sendMessage(CMD_PARAM_SET, SUCCESS, -1, strJsonParam);
					} else if (CMD_POSITION_MOVE == cmdType) {
						sendMessage(CMD_POSITION_MOVE, SUCCESS, -1, strJsonParam);
					} else {

					}
					if (cmdType == CMD_PARAM_ICE_MAKE_ALL_AUERY) {
						TcnLog.getInstance().LoggerDebug( TAG, "commondAnalyse, 参数设置返回：" + strJsonParam);
						sendMessage(CMD_PARAM_QUERY, SUCCESS, -1, strJsonParam);
						switch (operaItem) {
							case 1:
								if (operaPosition + 1 < 13) {
									reqDataAllQuery(operaItem, operaPosition + 1);
								}
								break;
							case 2:
								if (operaPosition + 1 < 14) {
									final int operaps = operaPosition;
									new Handler().postDelayed(new Runnable() {

										@Override
										public void run() {
											//do something
											reqDataAllQuery(2, operaps + 1);
										}
									}, 500);

								}
								break;
							case 3:
								if (operaPosition + 1 < 14) {
									reqDataAllQuery(operaItem, operaPosition + 1);
								}
								break;
							case 4:
								if (operaPosition + 1 < 8) {
									reqDataAllQuery(operaItem, operaPosition + 1);
								}
								break;
							case 5:
								if (operaPosition + 1 < 9) {
									reqDataAllQuery(operaItem, operaPosition + 1);
								}
								break;
						}

					}
				} else {
					if (CMD_PARAM_QUERY == cmdType) {
						sendMessage(CMD_PARAM_QUERY, FAIL, -1, null);
					} else if (CMD_PARAM_SET == cmdType) {
						sendMessage(CMD_PARAM_SET, FAIL, -1, null);
					} else if (CMD_POSITION_MOVE == cmdType) {
						sendMessage(CMD_POSITION_MOVE, FAIL, -1, null);
					} else {

					}

				}

				break;
			case 7:         //状态查询与判断
				//
				int FallingCupLight = Integer.parseInt(dataContent.substring(0, 2), 16);// 落杯光检
				int AntiPinchLight = Integer.parseInt(dataContent.substring(2, 4), 16);// 防夹手光检;
				int JamLight = Integer.parseInt(dataContent.substring(4, 6), 16);// 果酱光检;
				int StartSwitchX = Integer.parseInt(dataContent.substring(6, 8), 16);// X轴起始开关;
				int StartSwitchY = Integer.parseInt(dataContent.substring(8, 10), 16);// Y轴起始开关;
				int IcecSwitch = Integer.parseInt(dataContent.substring(10, 12), 16);// 冰淇淋开关;
				int CupDropperSwitch = Integer.parseInt(dataContent.substring(12, 14), 16);// 落杯器开关;
				int MotorCoordinateX = Integer.parseInt(dataContent.substring(14, 16), 16);// X轴电机状态;
				int MotorCoordinateY = Integer.parseInt(dataContent.substring(16, 18), 16);// Y轴电机状态;
				int CupDropperMotor = Integer.parseInt(dataContent.substring(18, 20), 16);// 落杯电子状态;
				int JamMotor = Integer.parseInt(dataContent.substring(20, 22), 16);// 果酱电机状态;
				int JamControlMotor = Integer.parseInt(dataContent.substring(22, 24), 16);// 果酱控制电机;
				int ToppingMotor1 = Integer.parseInt(dataContent.substring(24, 26), 16);// 顶料电机1状态;
				int ToppingMotor2 = Integer.parseInt(dataContent.substring(26, 28), 16);// 顶料电机2状态;
				int ToppingMotor3 = Integer.parseInt(dataContent.substring(28, 30), 16);// 顶料电机3状态;
				int MotorX = Integer.parseInt(dataContent.substring(30, 34), 16);// X电机坐标值;
				int MotorY = Integer.parseInt(dataContent.substring(34, 38), 16);// Y电机坐标值;
				int CupWeight = Integer.parseInt(dataContent.substring(38, 42), 16);// 杯重量值;
				MachineStateBean machineStateBean = new MachineStateBean(FallingCupLight, AntiPinchLight, JamLight, StartSwitchX, StartSwitchY, IcecSwitch, CupDropperSwitch, MotorCoordinateX, MotorCoordinateY
						, CupDropperMotor, JamMotor, JamControlMotor, ToppingMotor1, ToppingMotor2, ToppingMotor3, MotorX, MotorY, CupWeight);
				Gson gson1 = new Gson();
				String strJson1 = gson1.toJson(machineStateBean);//把对象转为JSON格式的字符串
				TcnLog.getInstance().LoggerDebug(TAG, "commondAnalyse, 状态返回：" + strJson1);
				sendMessage(CMD_QUERY_STATUS_AND_JUDGE, m_currentSendMsg.getPram1(), m_currentSendMsg.getPram2(), strJson1);

				break;
			case 8:         //机器自检
				int recvSelfInspection = Integer.parseInt(dataContent.substring(0, 2), 16);
				m_iQueryStatus = STATUS_INVALID;
				if (0 == recvSelfInspection) {  //接收指令成功 还需修改
					m_iCmdType = CMD_SELF_INSPECTION_STATUS;
//					TcnLog.getInstance().LoggerError(TAG, "commondAnalyse", "commondAnalyse() m_iQueryStatus=" + m_iQueryStatus);
					reqQuerySelefInspectionStatusDelay();
					sendMessage(CMD_SELF_INSPECTION, SUCCESS, m_currentSendMsg.getPram1(), new MsgToSend(m_currentSendMsg));
				} else {
					sendMessage(CMD_SELF_INSPECTION, FAIL, m_currentSendMsg.getPram1(), new MsgToSend(m_currentSendMsg));
				}
				break;
			case 50://清除升降机故障
				Message message = m_ReceiveHandler.obtainMessage();
				message.what = CMD_CLEAN_FAULTS;
				status = dataContent.substring(0, 2);     //00-空闲  01-出货中  02-等待取货中  03-准备中   04-故障中  05-自检中
				m_iQueryStatus = Integer.parseInt(status, 16);
       /*         if ("00".equals(status)) {
                    m_iQueryStatus = STATUS_FREE;
                } else if ("01".equals(status)) {
                    m_iQueryStatus = STATUS_BUSY;
                } else if ("02".equals(status)) {
                    m_iQueryStatus = STATUS_WAIT_TAKE_GOODS;
                } else {
                    m_iQueryStatus = STATUS_INVALID;
                }*/
				message.arg1 = m_iQueryStatus;
				String errCode = cmdData.substring(10, 12);
				iErrCode = Integer.valueOf(errCode, 16);
				m_currentSendMsg.setErrCode(iErrCode);

				message.arg2 = iErrCode;
				m_ReceiveHandler.sendMessage(message);

				m_WriteThread.setBusy(false);

				TcnLog.getInstance().LoggerDebug(TAG,  "commondAnalyse() CMD_CLEAN_FAULTS iErrCode: " + iErrCode);
				break;
			case 81://查询参数
				message = m_ReceiveHandler.obtainMessage();
				message.what = cmdType;
				String address = cmdData.substring(8, 10);
				String strValue = cmdData.substring(10, 14);
				message.arg1 = Integer.parseInt(address, 16);
				message.arg2 = TcnUtility.hex4StringToDecimal(strValue);
				m_ReceiveHandler.sendMessage(message);
				m_WriteThread.setBusy(false);

				break;
			case 130://  82  设置参数
				message = m_ReceiveHandler.obtainMessage();
				message.what = CMD_SET_PARAMETERS;
				address = cmdData.substring(10, 12);
				message.arg1 = Integer.parseInt(address, 16);
				errCode = cmdData.substring(8, 10);
				iErrCode = Integer.valueOf(errCode, 16);
				m_currentSendMsg.setErrCode(iErrCode);

				message.arg2 = iErrCode;
				int iValue = TcnUtility.hex4StringToDecimal(cmdData.substring(12, 16));
				message.obj = iValue;
				m_ReceiveHandler.sendMessage(message);

				m_WriteThread.setBusy(false);
				break;
			default:
				break;
		}
	}
}

