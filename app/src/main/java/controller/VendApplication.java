package controller;


import com.tcn.icecboard.control.TcnShareUseData;
import com.tcn.icecboard.control.TcnVendApplication;

/**
 * 描述：
 * 作者：Jiancheng,Song on 2016/5/31 15:53
 * 邮箱：m68013@qq.com
 */
public class VendApplication extends TcnVendApplication {
    @Override
    public void onCreate() {
        super.onCreate();
//        TcnShareUseData.getInstance().setBoardSerPortFirst("/dev/ttyS0");    //此处主板串口接安卓哪个串口，就填哪个串口
//        TcnShareUseData.getInstance().setBoardSerPortFirst("/dev/ttymxc1");


        /****************  如果接有副柜  则需要如下设置  **********************/
//        TcnShareUseData.getInstance().setSerPortGroupMapFirst("0");    //设置主柜组号，也可不设置，默认就是0
//        TcnShareUseData.getInstance().setSerPortGroupMapSecond("0");   //设置副柜组号为0,副柜需要接安卓另外一个串口
//        TcnShareUseData.getInstance().setBoardTypeSecond(TcnConstant.DEVICE_CONTROL_TYPE[1]);   //设置副柜类型为转盘盒饭机
//        TcnShareUseData.getInstance().setBoardSerPortSecond("/dev/ttyS1");    //设置副柜串口，副柜接安卓哪个串口，就填哪个串口

        //先运行程序之后，请将TcnKey目录的tcn_sdk_device_id.txt文件发给我们，授权才能使用，每台机器都必须先授权。

        /*******************************      故障代码表见 VendIF  这个文件 **************************************/

    }
}
