package com.tcn.icecboard.DriveControl.icec;

public class IceParamBean {
	private int operaItem = -1;   //操作项目
	private int operaPosition = -1;   //操作位置
	private int data = -1;   //数据



	public IceParamBean (int operaItem, int operaPosition, int data) {
		this.operaItem = operaItem;
		this.operaPosition = operaPosition;
		this.data = data;
	}

	public int getOperaItem() {
		return this.operaItem;
	}

	public int getOperaPosition() {
		return this.operaPosition;
	}

	public int getData() {
		return this.data;
	}
}
