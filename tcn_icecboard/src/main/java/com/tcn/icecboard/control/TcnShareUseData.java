package com.tcn.icecboard.control;

import android.content.Context;
import android.content.SharedPreferences;

import com.tcn.icecboard.TcnConstant;


/**
 * 作者：Jiancheng,Song on 2016/5/28 15:18
 * 邮箱：m68013@qq.com
 */
public class TcnShareUseData {
    private static TcnShareUseData m_Instance = null;
    public Context m_context = null;


    public static synchronized TcnShareUseData getInstance() {
        if (null == m_Instance) {
            m_Instance = new TcnShareUseData();
        }
        return m_Instance;
    }

    public void init(Context context) {
        m_context = context;
    }

    /**
     *@desc 获取机器id号
     *@author Jiancheng,Song
     *@time 2016/5/27 22:22
     */
    public String getMachineID() {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        String id = sp.getString("machin_id", "");
        return id;
    }

    /**
     *@desc 设置机器id号
     *@author Jiancheng,Song
     *@time 2016/5/27 22:22
     */
    public void setMachineID(String id) {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("machin_id", id);
        editor.commit();
    }

    /**
     * @desc 获取支付有效时间(s)
     * @author Jiancheng, Song
     * @time 2016/5/27 22:18
     */
    public int getPayTime() {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        int paytime = sp.getInt("PayTime", 90);
        return paytime;
    }

    /**
     * @desc 设置支付有效时间(s)
     * @author Jiancheng, Song
     * @time 2016/5/27 22:17
     */
    public void setPayTime(int time) {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("PayTime", time);
        editor.commit();
    }

    /**
     * @desc 设置软件版本号
     * @author Jiancheng, Song
     * @time 2016/6/3 21:03
     */
    public void setSoftVersion(String ver) {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Version", ver);
        editor.commit();
    }

    /**
     * @desc 获取软件版本号
     * @author Jiancheng, Song
     * @time 2016/6/3 21:03
     */
    public String getSoftVersion() {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        String brand = sp.getString("Version", "0");
        return brand;
    }

    /**
     * @desc 获取主板波特率
     * @author Jiancheng, Song
     * @time 2016/5/27 22:18
     */
    public String getBoardBaudRate() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String remoteAdSysType = sp.getString("MAINBAUDRATE", "9600");
        return remoteAdSysType;
    }

    /**
     * @desc 设置主板波特率
     * @author Jiancheng, Song
     * @time 2016/5/27 22:17
     */
    public void setBoardBaudRate(String baudRate) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("MAINBAUDRATE", baudRate);
        editor.commit();
    }

    /**
     * @desc 获取串口
     * @author Jiancheng, Song
     * @time 2016/5/27 22:18
     */
    public String getBoardSerPortFirst() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String remoteAdSysType = sp.getString("MAINDEVICE", "/dev/ttyS0");
        return remoteAdSysType;
    }

    /**
     * @desc 设置串口
     * @author Jiancheng, Song
     * @time 2016/5/27 22:17
     */
    public void setBoardSerPortFirst(String device) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("MAINDEVICE", device);
        editor.commit();
    }

    /**
     *@desc 获取串口
     *@author Jiancheng,Song
     *@time 2016/5/27 22:18
     */
    public String getBoardSerPortSecond() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String remoteAdSysType = sp.getString("SERVERDEVICE", "");
        return remoteAdSysType;
    }

    /**
     *@desc 设置串口
     *@author Jiancheng,Song
     *@time 2016/5/27 22:17
     */
    public void setBoardSerPortSecond(String device) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("SERVERDEVICE", device);
        editor.commit();
    }

    /**
     * @desc 设置设备号
     * @author Jiancheng, Song
     * @time 2016/6/3 21:03
     */
    public void setDeviceID(String ver) {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("DeviceID", ver);
        editor.commit();
    }

    /**
     * @desc 获取设备号
     * @author Jiancheng, Song
     * @time 2016/6/3 21:03
     */
    public String getDeviceID() {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        String DeviceID = sp.getString("DeviceID", "");
        return DeviceID;
    }

    /**
     *@desc 获取掉货检测开关
     *@author Jiancheng,Song
     *@time 2016/5/27 22:22
     */
    public boolean isDropSensorCheck() {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        boolean bCheck = sp.getBoolean("DropSensor", true);
        return bCheck;
    }


    /**
     *@desc 获取掉货检测开关
     *@author Jiancheng,Song
     *@time 2016/5/27 22:22
     */
    public void setDropSensorCheck(boolean bDropSensor) {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (null != editor) {
            editor.putBoolean("DropSensor", bDropSensor);
            editor.commit();
        }
    }

    /**
     *@desc出货失败次数
     *@author Jiancheng,Song
     *@time 2016/5/27 22:22
     */
    public void setShipContinFailCount(int count) {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("ShipContinFailCount", count);
        editor.commit();
    }

    /**
     *@desc 出货失败次数
     *@author Jiancheng,Song
     *@time 2016/5/27 22:22
     */
    public int getShipContinFailCount() {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        int count = sp.getInt("ShipContinFailCount", 0);
        return count;
    }

    /**
     *@desc 联系多少次出货失败锁机
     *@author Jiancheng,Song
     *@time 2016/5/27 22:22
     */
    public void setShipFailCountLock(int count) {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("ShipFailCountLock", count);
        editor.commit();
    }

    /**
     *@desc 联系多少次出货失败锁机
     *@author Jiancheng,Song
     *@time 2016/5/27 22:22
     */
    public int getShipFailCountLock() {
        SharedPreferences sp = m_context.getSharedPreferences("info_config", Context.MODE_PRIVATE);
        int count = sp.getInt("ShipFailCountLock", 5);
        return count;
    }

    /**
     *@desc 获取主板类型
     *@author Jiancheng,Song
     *@time 2016/5/27 22:20
     */
    public String getBoardType() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String type = sp.getString("BoardType", TcnConstant.DEVICE_CONTROL_TYPE[1]);
        return type;
    }

    /**
     *@desc 设置主板类型
     *@author Jiancheng,Song
     *@time 2016/5/27 22:19
     */
    public void setBoardType(String type) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("BoardType", type);
        editor.commit();
    }

    /**
     *@desc 获取主板类型
     *@author Jiancheng,Song
     *@time 2016/5/27 22:20
     */
    public String getBoardTypeSecond() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String type = sp.getString("BoardTypeSecond", TcnConstant.DEVICE_CONTROL_TYPE[0]);
        return type;
    }

    /**
     *@desc 设置主板类型
     *@author Jiancheng,Song
     *@time 2016/5/27 22:19
     */
    public void setBoardTypeSecond(String type) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("BoardTypeSecond", type);
        editor.commit();
    }

    /**
     *@desc 获取主板类型
     *@author Jiancheng,Song
     *@time 2016/5/27 22:20
     */
    public String getBoardTypeThird() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String type = sp.getString("BoardTypeThird", TcnConstant.DEVICE_CONTROL_TYPE[0]);
        return type;
    }

    /**
     *@desc 设置主板类型
     *@author Jiancheng,Song
     *@time 2016/5/27 22:19
     */
    public void setBoardTypeThird(String type) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("BoardTypeThird", type);
        editor.commit();
    }

    /**
     *@desc 获取主板类型
     *@author Jiancheng,Song
     *@time 2016/5/27 22:20
     */
    public String getBoardTypeFourth() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String type = sp.getString("BoardTypeFourth", TcnConstant.DEVICE_CONTROL_TYPE[0]);
        return type;
    }

    /**
     *@desc 设置主板类型
     *@author Jiancheng,Song
     *@time 2016/5/27 22:19
     */
    public void setBoardTypeFourth(String type) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("BoardTypeFourth", type);
        editor.commit();
    }

    /**
     *@desc 获取组号和串口对应
     *@author Jiancheng,Song
     *@time 2016/5/27 22:18
     */
    public String getSerPortGroupMapFirst() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String remoteAdSysType = sp.getString("SerPtGrpMapFirst", TcnConstant.SRIPORT_GRP_MAP[1]);
        return remoteAdSysType;
    }

    /**
     *@desc 设置组号和串口对应
     *@author Jiancheng,Song
     *@time 2016/5/27 22:17
     */
    public void setSerPortGroupMapFirst(String mapData) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("SerPtGrpMapFirst", mapData);
        editor.commit();
    }

    /**
     *@desc 获取组号和串口对应
     *@author Jiancheng,Song
     *@time 2016/5/27 22:18
     */
    public String getSerPortGroupMapSecond() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String remoteAdSysType = sp.getString("SerPtGrpMapSecond", TcnConstant.SRIPORT_GRP_MAP[0]);
        return remoteAdSysType;
    }

    /**
     *@desc 设置组号和串口对应
     *@author Jiancheng,Song
     *@time 2016/5/27 22:17
     */
    public void setSerPortGroupMapSecond(String mapData) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("SerPtGrpMapSecond", mapData);
        editor.commit();
    }

    /**
     *@desc 获取组号和串口对应
     *@author Jiancheng,Song
     *@time 2016/5/27 22:18
     */
    public String getSerPortGroupMapThird() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String remoteAdSysType = sp.getString("SerPtGrpMapThird", TcnConstant.SRIPORT_GRP_MAP[0]);
        return remoteAdSysType;
    }

    /**
     *@desc 设置组号和串口对应
     *@author Jiancheng,Song
     *@time 2016/5/27 22:17
     */
    public void setSerPortGroupMapThird(String mapData) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("SerPtGrpMapThird", mapData);
        editor.commit();
    }

    /**
     *@desc 获取组号和串口对应
     *@author Jiancheng,Song
     *@time 2016/5/27 22:18
     */
    public String getSerPortGroupMapFourth() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String remoteAdSysType = sp.getString("SerPtGrpMapFourth", TcnConstant.SRIPORT_GRP_MAP[0]);
        return remoteAdSysType;
    }

    /**
     *@desc 设置组号和串口对应
     *@author Jiancheng,Song
     *@time 2016/5/27 22:17
     */
    public void setSerPortGroupMapFourth(String mapData) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("SerPtGrpMapFourth", mapData);
        editor.commit();
    }


    /**
     * @desc 钱币预存
     * @author Jiancheng, Song
     * @time 2016/5/27 22:22
     */
    public String getCoinPreStorage() {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        String money = sp.getString("CoinPreStorage", String.valueOf(0));
        return money;
    }

    /**
     * @desc钱币预存
     * @author Jiancheng, Song
     * @time 2016/5/27 22:22
     */
    public void setCoinPreStorage(String money) {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("CoinPreStorage", money);
        editor.commit();
    }

    /**
     * @desc 钱币预存
     * @author Jiancheng, Song
     * @time 2016/5/27 22:22
     */
    public String getPaperPreStorage() {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        String money = sp.getString("PaperPreStorage", String.valueOf(0));
        return money;
    }

    /**
     * @desc钱币预存
     * @author Jiancheng, Song
     * @time 2016/5/27 22:22
     */
    public void setPaperPreStorage(String money) {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("PaperPreStorage", money);
        editor.commit();
    }


    /**
     * @desc 获取设置纸币的参数
     * @author Jiancheng, Song
     * @time 2016/5/27 22:18
     */
    public int getPaperOpenSet() {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        int paytime = sp.getInt("PaperOpenSet", 65535);
        return paytime;
    }

    /**
     * @desc 设置纸币的参数
     * @author Jiancheng, Song
     * @time 2016/5/27 22:17
     */
    public void setPaperOpenSet(int data) {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("PaperOpenSet", data);
        editor.commit();
    }

    /**
     * @desc 获取设置硬币的参数
     * @author Jiancheng, Song
     * @time 2016/5/27 22:18
     */
    public int getCoinOpenSet() {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        int paytime = sp.getInt("CoinOpenSet", 65535);
        return paytime;
    }

    /**
     * @desc 设置硬币的参数
     * @author Jiancheng, Song
     * @time 2016/5/27 22:17
     */
    public void setCoinOpenSet(int data) {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("CoinOpenSet", data);
        editor.commit();
    }

    /**
     *@desc 设置是否支持现金支付
     *@author Jiancheng,Song
     *@time 2016/5/27 22:25
     */
    public void setCashPayOpen(boolean open) {
        SharedPreferences sp = m_context.getSharedPreferences("pay_system", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (null != editor) {
            editor.putBoolean("isopencash", open);
        }
        editor.commit();
    }

    /**
     *@desc 是否支持现金支付
     *@author Jiancheng,Song
     *@time 2016/5/27 22:37
     */
    public boolean isCashPayOpen() {
        SharedPreferences sp = m_context.getSharedPreferences("pay_system", Context.MODE_PRIVATE);
        boolean bAliPayOpen = false;
        if (null != sp) {
            bAliPayOpen = sp.getBoolean("isopencash", false);
        }

        return bAliPayOpen;
    }

    /**
     *@desc 设置是否支持纸币找零
     *@author Jiancheng,Song
     *@time 2016/5/27 22:25
     */
    public void setPaperChange(boolean open) {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (null != editor) {
            editor.putBoolean("PaperChange", open);
        }
        editor.commit();
    }

    /**
     *@desc 是否支持纸币找零
     *@author Jiancheng,Song
     *@time 2016/5/27 22:37
     */
    public boolean isPaperChangeOpen() {
        SharedPreferences sp = m_context.getSharedPreferences("menu_set_config", Context.MODE_PRIVATE);
        boolean PaperChange = false;
        if (null != sp) {
            PaperChange = sp.getBoolean("PaperChange", false);
        }

        return PaperChange;
    }

    /**
     *@desc 获取串口
     *@author Jiancheng,Song
     *@time 2016/5/27 22:18
     */
    public String getBoardSerPortMDB() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String deviceMdb = sp.getString("DEVICEMDB", "");
        return deviceMdb;
    }

    /**
     *@desc 设置串口
     *@author Jiancheng,Song
     *@time 2016/5/27 22:17
     */
    public void setBoardSerPortMDB(String device) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("DEVICEMDB", device);
        editor.commit();
    }

    /**
     *@desc 获取主板波特率
     *@author Jiancheng,Song
     *@time 2016/5/27 22:18
     */
    public String getMDBBaudRate() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String remoteAdSysType = sp.getString("MDBBAUDRATE", "9600");
        return remoteAdSysType;
    }

    /**
     *@desc 设置主板波特率
     *@author Jiancheng,Song
     *@time 2016/5/27 22:17
     */
    public void setMDBBaudRate(String baudRate) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("MDBBAUDRATE", baudRate);
        editor.commit();
    }

    /**
     *@desc 客户类型
     *@author Jiancheng,Song
     *@time 2016/5/27 22:18
     */
    public String getCustomType() {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String CustomType = sp.getString("CustomType", "dft");
        return CustomType;
    }

    /**
     *@desc 客户类型
     *@author Jiancheng,Song
     *@time 2016/5/27 22:17
     */
    public void setCustomType(String customType) {
        SharedPreferences sp = m_context.getSharedPreferences(m_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("CustomType", customType);
        editor.commit();
    }
}
