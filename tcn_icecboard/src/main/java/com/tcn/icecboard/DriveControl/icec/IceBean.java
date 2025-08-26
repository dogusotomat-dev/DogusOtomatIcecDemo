package com.tcn.icecboard.DriveControl.icec;

public class IceBean {
	private int zhuliao = -1;
	private int guojiang = -1;
	private int dingliao = -1;
	private int zhuliaoQuantity = -1;
	private int guojiangQuantity = -1;
	private int dingliaoQuantity = -1;

	public IceBean (int zhuliao, int guojiang, int dingliao, int zhuliaoQuantity, int guojiangQuantity, int dingliaoQuantity) {
		this.zhuliao = zhuliao;
		this.guojiang = guojiang;
		this.dingliao = dingliao;
		this.zhuliaoQuantity = zhuliaoQuantity;
		this.guojiangQuantity = guojiangQuantity;
		this.dingliaoQuantity = dingliaoQuantity;
	}

	public int getZhuliao() {
		return this.zhuliao;
	}

	public int getGuojiang() {
		return this.guojiang;
	}

	public int getDingliao() {
		return this.dingliao;
	}

	public int getZhuliaoQuantity() {
		return this.zhuliaoQuantity;
	}

	public int getGuojiangQuantity() {
		return this.guojiangQuantity;
	}

	public int getDingliaoQuantity() {
		return this.dingliaoQuantity;
	}

}
