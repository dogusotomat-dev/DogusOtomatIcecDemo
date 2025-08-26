package com.tcn.icecboard.control;

public class MsgTrade {
	private int m_iSlotNo = -1;
	private int m_iErrCode = -1;
	private String m_strTradeNo = null;
	private String m_strAmount = null;

	public MsgTrade(int slotNo,int errCode, String tradeNo, String amount) {
		m_iSlotNo = slotNo;
		m_iErrCode = errCode;
		m_strTradeNo = tradeNo;
		m_strAmount = amount;
	}

	public void setSlotNo(int SlotNo)
	{
		this.m_iSlotNo = SlotNo;
	}

	public int getSlotNo()
	{
		return this.m_iSlotNo;
	}


	public int getErrCode()
	{
		return this.m_iErrCode;
	}

	public void setTradeNo(String tradeNo)
	{
		this.m_strTradeNo = tradeNo;
	}

	public String getTradeNo()
	{
		return this.m_strTradeNo;
	}

	public void setAmount(String Amount)
	{
		this.m_strAmount = Amount;
	}

	public String getAmount()
	{
		return this.m_strAmount;
	}
}
