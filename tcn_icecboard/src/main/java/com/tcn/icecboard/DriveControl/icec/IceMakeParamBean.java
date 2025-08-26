package com.tcn.icecboard.DriveControl.icec;

public class IceMakeParamBean {
	private int GridVoltage = -1;   //电网电压
	private int RefriTemp = -1;   //冰箱冷藏温度
	private int RefriFreezTemp = -1;   //冰箱冷冻温度
	private int RefriCylinderTemp1 = -1;   //冷冻缸1温度
	private int RefriCylinderTemp2 = -1;   //冷冻缸2温度

	private int RefriCylinderPro1 = -1;   //冷冻缸1系统成型比例
	private int RefriCylinderPro2 = -1;   //冷冻缸2系统成型比例

	private int RefriFault1 = -1;   //制冰1系统故障信息  01-左电机堵转  02-左电机皮带打滑  03-左行程开关故障  04-左系统制冷超时  05-左系统缺料  06-左压缩机高压保护
	private int RefriFault2 = -1;   //制冰2系统故障信息  01-左电机堵转  02-左电机皮带打滑  03-左行程开关故障  04-左系统制冷超时  05-左系统缺料  06-左压缩机高压保护

	//整机系统故障信号：
	//1、左右压缩机高压保护。    2、电压过高。  3、电压过低。  4、中间行程开关故障
	//5、冰箱冷藏系统故障。 6、冰箱冷冻系统故障。7、冰箱冷藏传感器故障。8、冰箱冷冻传感器故障
	private int machineFault = -1;


	private int Refri1WorkMode = -1;        //制冰1返回实际工作模式  00停止  01解冻 02清洗  03补料  04保鲜  05制作冰激凌
	private int Refri2WorkMode = -1;        //制冰2返回实际工作模式  00停止  01解冻 02清洗  03补料  04保鲜  05制作冰激凌

	private int LoadOutputState1 = -1;          //负载输出状态1
	private int LoadOutputState2 = -1;          //负载输出状态2



	public IceMakeParamBean (int GridVoltage, int RefriTemp, int RefriFreezTemp, int RefriCylinderTemp1, int RefriCylinderTemp2, int RefriCylinderPro1, int RefriCylinderPro2
			, int RefriFault1, int RefriFault2, int machineFault, int Refri1WorkMode, int Refri2WorkMode, int LoadOutputState1, int LoadOutputState2) {
		this.GridVoltage = GridVoltage;
		this.RefriTemp = RefriTemp;
		this.RefriFreezTemp = RefriFreezTemp;
		this.RefriCylinderTemp1 = RefriCylinderTemp1;
		this.RefriCylinderTemp2 = RefriCylinderTemp2;
		this.RefriCylinderPro1 = RefriCylinderPro1;
		this.RefriCylinderPro2 = RefriCylinderPro2;
		this.RefriFault1 = RefriFault1;
		this.RefriFault2 = RefriFault2;
		this.machineFault = machineFault;
		this.Refri1WorkMode = Refri1WorkMode;
		this.Refri2WorkMode = Refri2WorkMode;
		this.LoadOutputState1 = LoadOutputState1;
		this.LoadOutputState2 = LoadOutputState2;
	}


	public int getGridVoltage() {
		return this.GridVoltage;
	}

	public int getRefriTemp() {
		return this.RefriTemp;
	}

	public int getRefriFreezTemp() {
		return this.RefriFreezTemp;
	}

	public int getRefriCylinderTemp1() {
		return this.RefriCylinderTemp1;
	}

	public int getRefriCylinderTemp2() {
		return this.RefriCylinderTemp2;
	}

	public int getRefriCylinderPro1() {
		return this.RefriCylinderPro1;
	}

	public int getRefriCylinderPro2() {
		return this.RefriCylinderPro2;
	}

	public int getRefriFault1() {
		return this.RefriFault1;
	}

	public int getRefriFault2() {
		return this.RefriFault2;
	}

	public int getMachineFault() {
		return this.machineFault;
	}

	public int getRefri1WorkMode() {
		return this.Refri1WorkMode;
	}

	public int getRefri2WorkMode() {
		return this.Refri2WorkMode;
	}

	public int getLoadOutputState1() {
		return this.LoadOutputState1;
	}

	public int getLoadOutputState2() {
		return this.LoadOutputState2;
	}

}
