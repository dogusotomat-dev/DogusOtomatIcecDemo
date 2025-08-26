package com.tcn.icecboard.DriveControl;

/**
 * Created by Administrator on 2018/4/16.
 */
public class MTShipInfo {
	private int m_do = -1;  //执行的动作
	private int m_MT_id = -1;  //出货的料道
	private int m_iQuantity = -1;  //出货的量
	private int m_iWater = -1;
	private int m_iData = -1;   //数据


	public int getMT_id() {
		return m_MT_id;
	}

	public void setMT_id(int MT_id) {
		m_MT_id = MT_id;
	}

	public int getDoType() {
		return m_do;
	}

	public void seDoType(int type) {
		m_do = type;
	}

	public int getQuantity() {
		return m_iQuantity;
	}

	public void setQuantity(int quantity) {
		m_iQuantity = quantity;
	}

	public int getWater() {
		return m_iWater;
	}

	public void setWater(int water) {
		m_iWater = water;
	}

	public int getData() {
		return m_iData;
	}

	public void setData(int data) {
		m_iData = data;
	}
}
