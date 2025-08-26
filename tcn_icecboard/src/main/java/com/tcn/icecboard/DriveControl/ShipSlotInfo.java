package com.tcn.icecboard.DriveControl;

public class ShipSlotInfo {
	private int m_iSlotNo = -1;
	private long m_lNeedShipTime = -1;
	private long m_lTimeOut = -1;
	private int m_iIndex = -1;
	private String m_payMedthod = null;
	private String m_TradeNo = null;
	private String m_Amount = null;

	public ShipSlotInfo(int slotNo, long needShipTime, int timeOut) {
		m_iSlotNo = slotNo;
		m_lNeedShipTime = needShipTime;
		m_lTimeOut = timeOut;
	}

	public ShipSlotInfo(int slotNo, int index,long needShipTime, String payMedthod,String tradeNo,String amount) {
		m_iSlotNo = slotNo;
		m_iIndex = index;
		m_lNeedShipTime = needShipTime;
		m_payMedthod = payMedthod;
		m_TradeNo = tradeNo;
		m_Amount = amount;
	}

	public ShipSlotInfo(int slotNo, int index,long needShipTime, String payMedthod,String tradeNo) {
		m_iSlotNo = slotNo;
		m_iIndex = index;
		m_lNeedShipTime = needShipTime;
		m_payMedthod = payMedthod;
		m_TradeNo = tradeNo;
	}

	public ShipSlotInfo(int slotNo, int index,long needShipTime) {
		m_iSlotNo = slotNo;
		m_iIndex = index;
		m_lNeedShipTime = needShipTime;
	}

	public int getShipSlotNo() {
		return m_iSlotNo;
	}

	public void setShipSlotNo(int slotNo) {
		m_iSlotNo = slotNo;
	}

	public long getNeedShipTime() {
		return m_lNeedShipTime;
	}

	public void setNeedShipTime(long time) {
		m_lNeedShipTime = time;
	}

	public long getTimeOut() {
		return m_lTimeOut;
	}

	public void setTimeOut(long timeOutValue) {
		m_lTimeOut = timeOutValue;
	}

	public String getPayMedthod() {
		return m_payMedthod;
	}

	public void setPayMedthod(String payMedthod) {
		m_payMedthod = payMedthod;
	}


	public String getTradeNo() {
		return m_TradeNo;
	}

	public void setTradeNo(String tradeNo) {
		m_TradeNo = tradeNo;
	}

	public String getAmount() {
		return m_Amount;
	}

	public void setAmount(String amount) {
		m_Amount = amount;
	}

	public boolean isShipOutTime() {
		boolean bRet = false;
		if (-1 == m_lTimeOut) {
			m_lTimeOut = 60000;
		}
		if (Math.abs(System.currentTimeMillis() - m_lNeedShipTime) > m_lTimeOut) {
			bRet = true;
		}
		return bRet;
	}
}
