package com.tcn.icecboard.control;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;

import com.tcn.icecboard.DriveControl.VendProtoControl;
import com.tcn.icecboard.TcnUtility;
import com.tcn.icecboard.vend.TcnLog;
import com.tcn.icecboard.vend.VendControl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android_serialport_api.SerialPortController;
import android_serialport_api.SerialPortFinder;

/**
 * 描述：
 * 作者：Jiancheng,Song on 2016/5/28 16:36
 * 邮箱：m68013@qq.com
 */
public class TcnVendIF {
    private static final String TAG = "TcnVendIF";

    private volatile int m_iEventIDTemp = -1;
    private volatile int m_ilParam1 = -1;

    private volatile int m_orientation = Configuration.ORIENTATION_PORTRAIT;
    private static VendControl m_VendControl = null;
    private String m_newVersionName = "";
    //private String m_TextAd = null;
    private Context m_context = null;
    private List<String> m_strMountedPathList = null;


    private static class SingletonHolder {
        private static TcnVendIF instance = new TcnVendIF();

        private SingletonHolder() {
            //do nothing
        }

    }

    public static TcnVendIF getInstance() {
        return SingletonHolder.instance;
    }


    public void init(Context context) {
        if (null == context) {
            return;
        }
        m_context = context;
        m_orientation = context.getResources().getConfiguration().orientation;
        TcnLog.getInstance().initLog(context);
        TcnShareUseData.getInstance().init(context);
        TcnConstantLift.init(context);
        SerialPortController.getInstance().init(context);
        LoggerDebug(TAG, "init getVersionName: " + getVersionName() + " getVersionCode: " + getVersionCode() +
                " m_orientation: " + m_orientation);

    }

    public boolean isHasPermission() {
        boolean bRet = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {       //大于等于安卓6.0
            bRet = true;
            return bRet;
        }
        int permissionCheck1 = ContextCompat.checkSelfPermission(m_context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(m_context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if ((permissionCheck1 == PackageManager.PERMISSION_GRANTED) && (permissionCheck2 == PackageManager.PERMISSION_GRANTED)) {
            bRet = true;
        }
        return bRet;
    }

    public String getPermission() {
        StringBuffer mSb = new StringBuffer();
        mSb.append(Manifest.permission.READ_EXTERNAL_STORAGE);
        mSb.append("|");
        mSb.append(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return mSb.toString();
    }

    public String[] getPermission(String permissions) {
        String[] mPermissionArry = null;
        if ((null == permissions) || (permissions.length() < 1)) {
            return mPermissionArry;
        }
        if (permissions.contains("|")) {
            mPermissionArry = permissions.split("\\|");
        } else {
            mPermissionArry = new String[1];
            mPermissionArry[0] = permissions;
        }
        return mPermissionArry;
    }

    public void setConfig() {
        TcnShareUseData.getInstance().setBoardBaudRate("9600");
    }

    public boolean isServiceRunning() {
        if (null == m_context) {
            return false;
        }
        ActivityManager manager = (ActivityManager) m_context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(1000)) {
            if (("controller.VendService").equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前应用程序处于前台还是后台
     */
    public boolean isAppForeground() {
        boolean bRet = false;
        ActivityManager am = (ActivityManager) m_context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(m_context.getPackageName())) {
                bRet = true;
            }
        }
        return bRet;
    }

    public void startWorkThread() {
        LoggerDebug(TAG, "startWorkThread");

        setConfig();

        if (null != m_VendControl) {
            m_VendControl.quit();
            m_VendControl = null;
        }
        m_VendControl = new VendControl(m_context, "VendControl");
        m_VendControl.start();
    }

    public void stopWorkThread() {
        LoggerDebug(TAG, "stopWorkThread");

        if (m_VendControl != null) {
            m_VendControl.quit();
            m_VendControl = null;
        }
    }


    public List<GroupInfo> getGroupListAll () {
        return VendProtoControl.getInstance().getGroupListAll();
    }

//    public List<GroupInfo> getGroupListHefanZp () {
////        return VendProtoControl.getInstance().getGroupListHefanZp();
//    }

    public int getFitScreenSize(int defaultSize) {

        return defaultSize;
    }

    /**
     * 静默卸载apk到Data/app目录
     *
     * @param packageName
     * @return 卸载成功为true
     */
    public boolean uninstallDataAPPBySilent(String packageName) {
//        LoggerDebug(TAG, "-------uninstallDataAPPBySilent------");

        // 参数检测
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        LoggerDebug(TAG, "packageName: " + packageName);

        StringBuilder cmd = new StringBuilder();
        cmd.append("pm uninstall " + packageName).append("\n");
        // 部分手机Root之后Library path 丢失，导入library path可解决该问题
        // cmd.append("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
        if (execRootCmd(cmd.toString()) == 0) {
            LoggerError(TAG, "uninstall: " + packageName + "success");
            return true;
        }
        LoggerDebug(TAG, "uninstall: " + packageName + " failed");

        return false;
    }

    private boolean installSlientSuRyd() {
        boolean bRet = false;
        String apkPath = TcnUtility.getInstallApkPath();
        if (TextUtils.isEmpty(apkPath)) {
            return bRet;
        }
        String cmd = "pm install -r "+apkPath;
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {

            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            //获取返回结果
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
            bRet = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bRet;
    }

    private void installSlientSuYht() {
        if (null == m_context) {
            return;
        }
        String apkPath = TcnUtility.getInstallApkPath();
        Intent mIntent = new Intent("com.box.intent.OPENAPI_INSTALL_APP");
        mIntent.putExtra("path",apkPath);
        mIntent.putExtra("type",1);
        m_context.sendBroadcast(mIntent);
    }

    public void installAppSlient() {
        if (installSlientSuRyd()) {
            LoggerDebug(TAG,"installSlientSuRyd");
        } else {
            installSlientSuYht();
        }
        TcnUtility.deleteApk();
    }

    /**
     * root权限下执行命令
     *
     * @param cmd 多条命令需用换行分隔
     * @return 执行结果码 0代表成功
     */
    private int execRootCmd(String cmd) {
        LoggerDebug(TAG, "execRootCmd: " + cmd);

        Process process = null;
        DataOutputStream dos = null;
        try {
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            process.waitFor();
            LoggerError(TAG, "process.exitValue(): " + process.exitValue());

            return process.exitValue();
        } catch (Exception e) {
            LoggerError(TAG, "exception: " + e.getMessage());
            return -1;
        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateVersion() {
        if (null == m_context) {
            return;
        }

        int VersionCode = getVersionCode();

        if (VersionCode < 1) {
            return;
        }
        String mVersion = getVersionName();
        if ((null == mVersion) || (mVersion.length() < 6)) {
            return;
        }
    }

    public boolean isVaildSeriPort(String seriPort) {
        boolean bRet = false;
        if ((null == seriPort) || (seriPort.isEmpty())) {
            return bRet;
        }
        if (seriPort.contains("ttyS") || seriPort.contains("ttyO")
                || seriPort.contains("ttymxc") || seriPort.contains("ttyES")
                || seriPort.contains("ttysWK") || seriPort.contains("ttyCOM") || seriPort.contains("ttyHSL")
                || seriPort.contains("ttyXRUSB")) {
            bRet = true;
        }
        return bRet;
    }

    public String getFileName(boolean hasSuffix, String path) {
        String strName = null;
        if ((null == path) || (path.length() < 1)) {
            return strName;
        }
        int index = path.lastIndexOf("/");
        if ((index >= 0) && (path.length() > (index + 1))) {
            if (hasSuffix) {
                strName = path.substring(index + 1);
            } else {
                int indexP = path.indexOf(".", index + 1);
                strName = path.substring(index + 1, indexP);
            }

        }
        return strName;
    }

    public String getVersionName() {
        String strVerName = null;
        if (null == m_context) {
            return strVerName;
        }
        try {
            PackageInfo pInfo = m_context.getPackageManager().getPackageInfo(m_context.getPackageName(), 0);
            if (pInfo != null) {
                strVerName = pInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return strVerName;
    }

    public int getVersionCode() {
        int iVerCode = -1;
        if (null == m_context) {
            return iVerCode;
        }
        try {
            PackageInfo pInfo = m_context.getPackageManager().getPackageInfo(m_context.getPackageName(), 0);
            if (pInfo != null) {
                iVerCode = pInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return iVerCode;
    }

    public Context getContext() {
        return m_context;
    }

    public boolean isMachineLocked() {
        boolean bRet = false;
        int iFailCountLock = TcnShareUseData.getInstance().getShipFailCountLock();
        if ((iFailCountLock < 9) && ((TcnShareUseData.getInstance().getShipContinFailCount()) >= iFailCountLock)) {
            bRet = true;
        }
        return bRet;
    }

    public void rebootDevice() {
        if (m_context != null) {
            Intent intent = new Intent("com.android.action.REBOOT");
            m_context.sendBroadcast(intent);
        }
        rebootDeviceCmd();
    }

    private void rebootDeviceCmd() {
        try {
            Runtime.getRuntime().exec("su -c reboot");
        } catch (IOException e) {
        }
    }

    public int getScreenOrientation() {
        return m_orientation;
    }


    public List<String> getMountedDevicePathList() {
        return m_strMountedPathList;
    }

    public void addMountedDevicePath(String path) {
        if (null == m_strMountedPathList) {
            m_strMountedPathList = new ArrayList<String>();
        }
        if (!m_strMountedPathList.contains(path)) {
            m_strMountedPathList.add(path);
        }
    }

    public void hideSystemBar() {
        if (null == m_context) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction("elc_hide_systembar"); //不起作用
        m_context.sendBroadcast(intent);

        Intent intentBsd = new Intent();
        intentBsd.setAction("com.outform.hidebar");  //邦仕达
        m_context.sendBroadcast(intentBsd);

        Intent intentZb = new Intent();
        String action = "com.android.systemui.statusbar.phone.navigationbar.hide.or.show"; //致宝
        intentZb.putExtra("hideorshow", "hide"); //致宝
        intentZb.setAction(action);
        m_context.sendBroadcast(intentZb);

        Intent intentRyd = new Intent();
        intentRyd.setAction("com.android.action.hide_all_statusbar"); //锐益达
        m_context.sendBroadcast(intentRyd);

        Intent intentRyd39 = new Intent();
        intentRyd39.setAction("android.navigationbar.state");
        intentRyd39.putExtra("state", "off");
        m_context.sendBroadcast(intentRyd39);

        Intent intentTcn = new Intent();
        String actionTcn = "android.intent.action.NAVIGATIONBAR";
        intentTcn.putExtra("enable", "false");
        intentTcn.setAction(actionTcn);
        m_context.sendBroadcast(intentTcn);

        if ((TcnShareUseData.getInstance().getBoardSerPortFirst()).contains("ttymxc")) { //yht310
            hideBottomUIMenu();
        }
    }

    public void showSystembar() {
        if (null == m_context) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction("elc_unhide_systembar");//不起作用
        m_context.sendBroadcast(intent);

        Intent intentBsd = new Intent();
        intentBsd.setAction("com.outform.unhidebar");   //邦仕达
        m_context.sendBroadcast(intentBsd);

        Intent intentZb = new Intent();
        String action = "com.android.systemui.statusbar.phone.navigationbar.hide.or.show"; //致宝
        intentZb.putExtra("hideorshow", "show"); //致宝
        intentZb.setAction(action);
        m_context.sendBroadcast(intentZb);

        Intent intentRyd = new Intent();
        intentRyd.setAction("com.android.action.show_all_statusbar"); //锐益达
        m_context.sendBroadcast(intentRyd);

        Intent intentRyd39 = new Intent();
        intentRyd39.setAction("android.navigationbar.state");
        intentRyd39.putExtra("state", "on");
        m_context.sendBroadcast(intentRyd39);

        Intent intentTcn = new Intent();
        String actionTcn = "android.intent.action.NAVIGATIONBAR";
        intentTcn.putExtra("enable", "true");
        intentTcn.setAction(actionTcn);
        m_context.sendBroadcast(intentTcn);

        showBar();
    }

    private void hideBottomUIMenu() {
        try
        {
            if (Build.VERSION.SDK_INT <= 17) {
                String ProcID = "42";
                Process proc = Runtime.getRuntime().exec("su -c service call activity 42 s16 com.android.systemui");
                proc.waitFor();
            }
        }
        catch (Exception ex)
        {

        }

    }

    private void showBar() {
        try {
            execCmd("am startservice -n com.android.systemui/.SystemUIService");   //zq通达
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execCmd(String cmd){
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        } catch (Exception e) {
        }
    }

    public boolean createFoldersAndExist(String filePath) {
        boolean bRet = false;

        if ((filePath == null) || (filePath.length() < 1)) {
            return bRet;
        }
        try {
            String mStrRootPath = TcnUtility.getExternalStorageDirectory();
            String tmpPath = filePath;
            if ((!filePath.startsWith(mStrRootPath)) && (!filePath.startsWith("/mnt/"))) {
                tmpPath = mStrRootPath+"/"+filePath;
            }

            File dir = new File(tmpPath);

            dir.mkdirs();

            if((dir.exists()) && (dir.isDirectory())) {
                bRet = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bRet;
    }

    /**
     * 获取资源文件的路径
     *
     * @return
     */
    public String getResPath(int resId) {
        if (null == m_context) {
            return null;
        }
        Uri mUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + m_context.getResources().getResourcePackageName(resId)
                + "/" + m_context.getResources().getResourceTypeName(resId) + "/" + m_context.getResources().getResourceEntryName(resId));
        if (null == mUri) {
            return null;
        }
        return mUri.toString();
    }

    public void startGoBackShopTimer() {
        removeMsgToUI(TcnVendEventID.BACK_TO_SHOPPING);
        sendMsgToUIDelay(TcnVendEventID.BACK_TO_SHOPPING, 60000);
    }

    public void stopGoBackShopTimer() {
        removeMsgToUI(TcnVendEventID.BACK_TO_SHOPPING);
    }

//    public void reqSlotNoInfoOpenSerialPort() {
//        VendProtoControl.getInstance().reqSlotNoInfoOpenSerialPort();
//
//    }

    public SerialPortFinder getSerialPortFinder() {
        return SerialPortController.getInstance().getSerialPortFinder();
    }

    public String[] getAllDevicesPath() {
        String[] values = null;
        SerialPortFinder mSerialPortFinder = SerialPortController.getInstance().getSerialPortFinder();
        if (mSerialPortFinder != null) {
            values = mSerialPortFinder.getAllDevicesPath();
        }
        return values;
    }

//	public String getTextAd() {
//		return m_TextAd;
//	}

    //判断字符串是否包含中文
    public boolean isContainChinese(String str) {
        return TcnUtility.isContainChinese(str);
    }

    /**
     * 判断是否是含小数
     *
     * @param data
     * @return
     */
    public boolean isContainDeciPoint(String data) {
        boolean bRet = false;
        if ((null == data) || (data.length() < 1)) {
            return bRet;
        }
        try {
            // Pattern pattern = Pattern.compile("^[0-9]+\\.{0,1}[0-9]{0,2}$");
            int indexP = data.indexOf(".");
            if (indexP > 0) {
                int lastIndexOf = data.lastIndexOf(".");
                if (lastIndexOf > indexP) {
                    return bRet;
                }
                data = data.replace(".","");
                bRet = isDigital(data);
            }
        } catch (Exception e) {

        }

        return bRet;
    }

    /**
     * 判断是否全部由数字组成
     *
     * @param data
     * @return
     */
    public boolean isDigital(String data) {
        if ((null == data) || (data.length() < 1)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9]*$");
        return pattern.matcher(data).matches();
    }

    /**
     * 判断是否为数字(正负数都行)
     *
     * @param str 需要验证的字符串
     * @return
     */
    public boolean isNumeric(String str) {
        if ((null == str) || (str.length() < 1)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public void setNewVerionName(String ver) {
        m_newVersionName = ver;
    }

    public String getNewVerionName() {
        return m_newVersionName;
    }

    public void LoggerDebug(String tag, String msg) {
        TcnLog.getInstance().LoggerDebug(tag, msg);
    }

    public void logFileCheck() {
        TcnLog.getInstance().logFileCheck();
    }

    public void LoggerInfo(String tag, String msg) {
        TcnLog.getInstance().LoggerInfo(tag, msg);
    }

    public void LoggerInfoForce(String tag, String msg) {
        TcnLog.getInstance().LoggerInfoForce(tag, msg);
    }

    public void LoggerError(String tag, String msg) {
        TcnLog.getInstance().LoggerError(tag, msg);
    }

    public void LoggerErrorForce(String tag, String msg) {
        TcnLog.getInstance().LoggerErrorForce(tag, msg);
    }

    public boolean isValidAmount(String amount) {
        boolean bRet = false;
        if (TcnUtility.isDigital(amount) || TcnUtility.isContainDeciPoint(amount)) {
            bRet = true;
        }
        return bRet;
    }

    public String getTradeNoNew (int slotNo) {
        String sMID = TcnShareUseData.getInstance().getMachineID();
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(sMID)) {
            stringBuilder.append(sMID);
        }
        stringBuilder.append(slotNo);
        stringBuilder.append(TcnUtility.getTime("yyMMddHHmmss"));
        return stringBuilder.toString();
    }


    /************************** common end *******************************************/


    /************************** vend start *******************************************/


    public void reqShip(int slotNo, int zhuliao, int dingliao, int guojiang, int zhuQ, int dingQ, int guoQ, String shipMethod, String amount, String tradeNo) {
        if (null == m_VendControl) {
            return;
        }
        m_VendControl.reqShip(slotNo,zhuliao,dingliao,guojiang,zhuQ,dingQ,guoQ,shipMethod, amount, tradeNo);
    }

    //设置目标温度
    public void reqSetTemp(int grpId, int temp) {
        if (m_VendControl != null) {
            m_VendControl.reqSetTemp(grpId, temp);
        }
    }

    public void reqSetGlassHeatEnable(int grpId, boolean enable) {
        if (m_VendControl != null) {
            m_VendControl.reqSetGlassHeatEnable(grpId, enable);
        }
    }

    public void reqSetLedOpen(int grpId, boolean open) {
        if (m_VendControl != null) {
            m_VendControl.reqSetLedOpen(grpId, open);
        }
    }

    public void reqSetBuzzerOpen(int grpId, boolean open) {
        if (m_VendControl != null) {
            m_VendControl.reqSetBuzzerOpen(grpId, open);
        }
    }

        public int getTempControl () {
            if (null == m_VendControl) {
                return -1;
            }
            return m_VendControl.getTempControl();
        }

        public int getTempControlTemp () {
            if (null == m_VendControl) {
                return -1;
            }
            return m_VendControl.getTempControlTemp();
        }

        public int getTempControlStartTime () {
            if (null == m_VendControl) {
                return -1;
            }
            return m_VendControl.getTempControlStartTime();
        }

        public int getTempControlEndTime () {
            if (null == m_VendControl) {
                return -1;
            }
            return m_VendControl.getTempControlEndTime();
        }

        public void setTemperature (String temp){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.setTemperature(temp);
        }

        public String getTemp () {
            if (null == m_VendControl) {
                return null;
            }
            return m_VendControl.getTemp();
        }

        public boolean isShipping () {
            return VendProtoControl.getInstance().isShiping();
        }

        public boolean isCannotShipNext ( int slotNo){
            return VendProtoControl.getInstance().isCannotShipNext(slotNo);
        }

        public void reqSlotNoInfo () {
            VendProtoControl.getInstance().reqSlotNoInfo();
        }

//        public boolean haveDoorSwitch () {
//            return VendProtoControl.getInstance().haveDoorSwitch();
//        }

        // filePath存放图片的路径或者地址
        public boolean isImageOK (String filePath){
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options); //filePath代表图片路径
            if (options.mCancel || options.outWidth == -1
                    || options.outHeight == -1) {
                //表示图片已损毁
                return false;
            } else {
                return true;
            }
        }

        public void reqQuerySlotStatus ( int slotNo){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqQuerySlotStatus(slotNo);
        }

        public void reqSelfCheck ( int grpId){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqSelfCheck(grpId);
        }

        public void reqReset ( int grpId){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqReset(grpId);
        }

        //设置该货道为弹簧货道
        public void reqSetSpringSlot ( int slotNo){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqSetSpringSlot(slotNo);
        }

        //设置该货道为皮带货道
        public void reqSetBeltsSlot ( int slotNo){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqSetBeltsSlot(slotNo);
        }

        //设置所有货道为弹簧货道 grpId：如果没带副柜，就填-1或者0,如果带副柜，就填0(主柜),1(副柜1),2(副柜2)
        public void reqSpringAllSlot ( int grpId){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqSpringAllSlot(grpId);
        }

        //设置所有货道为皮带货道
        public void reqBeltsAllSlot ( int grpId){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqBeltsAllSlot(grpId);
        }

        //设置成单个货道
        public void reqSingleSlot ( int slotNo){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqSingleSlot(slotNo);
        }

        //设置改货道与下一个货道合并为一个货道
        public void reqDoubleSlot ( int slotNo){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqDoubleSlot(slotNo);
        }

        //设置所有货道为单货道
        public void reqSingleAllSlot ( int grpId){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqSingleAllSlot(grpId);
        }

        public void reqTestMode ( int grpId){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqTestMode(grpId);
        }

    public void reqShipTest(int zhuliao,int dingliao,int guojiang,int zhuQ,int dingQ,int guoQ){
        reqWriteDataShipTest(zhuliao,dingliao,guojiang,zhuQ,dingQ,guoQ);
    }

    private void reqWriteDataShipTest ( int start, int end,int heatSeconds){
        if (null == m_VendControl) {
            return;
        }
//        m_VendControl.reqTestSlotNo(start, end,heatSeconds);
    }
    private void reqWriteDataShipTest (int zhuliao,int dingliao,int guojiang,int zhuQ,int dingQ,int guoQ){
        if (null == m_VendControl) {
            return;
        }
        m_VendControl.reqTestSlotNo(zhuliao,dingliao,guojiang,zhuQ,dingQ,guoQ);
    }

        public void reqSelectSlotNo ( int slotNo){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.reqSelectSlotNo(slotNo);
        }

        public void reqEndEffectiveTime () {
            m_VendControl.reqEndEffectiveTime();
        }

        public void closeTrade () {
            closeTrade(true);
        }

        public void closeTrade ( boolean canRefund){
            if (null == m_VendControl) {
                return;
            }
            m_VendControl.closeTrade(canRefund);
        }

//    public String[] getCheckLightSelectData() {
//        return VendProtoControl.getInstance().getCheckLightSelectData();
//    }

    public String[] getFloorAllData() {

        return TcnConstantLift.LIFT_FLOOR_DATA;
    }

    public int getStartSlotNo(int grpId) {
        return VendProtoControl.getInstance().getStartSlotNo(grpId);
    }

    public String[] getBoardGroupNumberArr() {
        return VendProtoControl.getInstance().getBoardGroupNumberArr();
    }

    public String[] getBoardLatticeGroupNumberArr() {
        return VendProtoControl.getInstance().getBoardLatticeGroupNumberArr();
    }

    public void reqQueryStatus(int grpId) {
        if (m_VendControl != null) {
            m_VendControl.reqQueryStatus(grpId);
        }
    }

    public void reqTakeGoodsDoorControl(int grpId, boolean bOpen) {
        if (m_VendControl != null) {
            m_VendControl.reqTakeGoodsDoorControl(grpId, bOpen);
        }
    }

    public void reqLifterUp(int grpId, int floor) {
        if (m_VendControl != null) {
            m_VendControl.reqLifterUp(grpId, floor);
        }
    }

    public void reqBackHome(int grpId) {
        if (m_VendControl != null) {
            m_VendControl.reqBackHome(grpId);
        }
    }

    public void reqClapboardSwitch(int grpId, boolean bOpen) {
        if (m_VendControl != null) {
            m_VendControl.reqClapboardSwitch(grpId, bOpen);
        }
    }

    public void reqOpenCoolSpring(int grpId, int temp) {
        if (m_VendControl != null) {
            m_VendControl.reqOpenCoolSpring(grpId, temp);
        }
    }

    public void reqHeatSpring(int grpId, int temp) {
        if (m_VendControl != null) {
            m_VendControl.reqHeatSpring(grpId, temp);
        }
    }

    public void reqCloseCoolHeat(int grpId) {
        if (m_VendControl != null) {
            m_VendControl.reqCloseCoolHeat(grpId);
        }
    }

    public void reqCloseCoolHeatSpring(int grpId) {
        if (m_VendControl != null) {
            m_VendControl.reqCloseCoolHeatSpring(grpId);
        }
    }


    public void reqCleanDriveFaults(int grpId) {
        if (m_VendControl != null) {
            m_VendControl.reqCleanDriveFaults(grpId);
        }
    }

    public void reqQueryParameters(int grpId, int address) {
        if (m_VendControl != null) {
            m_VendControl.reqQueryParameters(grpId, address);
        }
    }

    public void reqSetId(int grpId, int id) {
        if (m_VendControl != null) {
            m_VendControl.reqSetId(grpId, id);
        }
    }

    public void reqSetSwitchOutPutStatus(int grpId,int number, int status) {
        if (m_VendControl != null) {
            m_VendControl.reqSetSwitchOutPutStatus(grpId,number, status);
        }
    }

    public void reqSetLightOutSteps(int grpId, int steps) {
        if (m_VendControl != null) {
            m_VendControl.reqSetLightOutSteps(grpId, steps);
        }
    }

    public void reqSwitchInPutDetect(int grpId,int number) {
        if (m_VendControl != null) {
            m_VendControl.reqSwitchInPutDetect(grpId, number);
        }
    }

    public void reqSetParameters(int grpId, int address, String value) {
        if (m_VendControl != null) {
            m_VendControl.reqSetParameters(grpId, address, value);
        }
    }

    public void reqFactoryReset(int grpId) {
        if (m_VendControl != null) {
            m_VendControl.reqFactoryReset(grpId);
        }
    }

    public void reqDetectLight(int grpId, String direction) {
        if (m_VendControl != null) {
            m_VendControl.reqDetectLight(grpId, direction);
        }
    }

    public void reqDetectShip(int grpId) {
        if (m_VendControl != null) {
            m_VendControl.reqDetectShip(grpId);
        }
    }

    /***********************  冰淇淋 start *******************************************/

    public void reqSetWorkMode(int workModeLeft, int workModeRight) {
        if (m_VendControl != null) {
            m_VendControl.reqSetWorkMode(workModeLeft, workModeRight);
        }
    }

    public void reqSetParamIceMake(int positionCoolLeft, int positionCoolRight, int coolTempLeft, int coolTempRight, int coolStorage, int coolFree) {
        if (m_VendControl != null) {
            m_VendControl.reqSetParamIceMake(positionCoolLeft, positionCoolRight, coolTempLeft, coolTempRight, coolStorage, coolFree);
        }
    }

    public void reqQueryParamIceMake() {
        if (m_VendControl != null) {
            m_VendControl.reqQueryParamIceMake();
        }
    }

    public void reqQueryParam(int operaItem, int operaPosition) {
        if (m_VendControl != null) {
            m_VendControl.reqQueryParam(operaItem, operaPosition);
        }
    }

    public void reqQueryParamAll(int operaItem, int operaPosition) {
        if (m_VendControl != null) {
            m_VendControl.reqQueryParamAll(operaItem, operaPosition);
        }
    }

    public void reqParamSet(int operaItem, int operaPosition, int data) {
        if (m_VendControl != null) {
            m_VendControl.reqParamSet(operaItem, operaPosition, data);
        }
    }

    public void reqIceLogout(int operaItem) {
        if (m_VendControl != null) {
//            m_VendControl.reqIceLogout(operaItem);
        }
    }

    public void reqMove(int operaPosition, int data) {
        if (m_VendControl != null) {
            m_VendControl.reqMove(operaPosition, data);
        }
    }

    public void reqQueryStatusAndJudge(int operaItem) {
        if (m_VendControl != null) {
            m_VendControl.reqQueryStatusAndJudge(operaItem);
        }
    }

    public void reqMachineSelfTest() {
        if (m_VendControl != null) {
            m_VendControl.reMachineSlefTest();
        }
    }

    public void reTestDischarge(int testProject, int testPosition) {
        if (m_VendControl != null) {
            m_VendControl.reTestDischarge(testProject, testPosition);
        }
    }
    /***********************  冰淇淋 end *******************************************/


        /************************** vend end *******************************************/

        private final Handler m_cEventHandlerForUI = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                handleMessageToUI(msg.what, (Bundle) msg.obj);
            }
        };

        public void handleMessageToUI ( int what, Bundle bundle){

            Bundle msgBundle = bundle;

            int iEventID = msgBundle.getInt("eID");
            int lParam1 = msgBundle.getInt("lP1");
            int lParam2 = msgBundle.getInt("lP2");
            long lParam3 = msgBundle.getLong("lP3");
            String lParam4 = msgBundle.getString("lP4");
            notifyUI(iEventID, lParam1, lParam2, lParam3, lParam4);
        }

        private final Handler m_cEventHandlerForUIObj = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                handleMessageToUIObj(msg.what, msg.arg1, msg.arg2, (Object) msg.obj);
            }
        };


        public void handleMessageToUIObj ( int iEventID, int lParam1, int lParam2, Object lParam5){
            notifyUI(iEventID, lParam1, lParam2, -1, null, lParam5);
        }

        public void sendMsgToUI ( int iEventID, int lParam1, int lParam2, long lParam3, String
        lParam4){

            if ((m_iEventIDTemp == iEventID) && (m_ilParam1 == lParam1)) {
                TcnUtility.removeMessages(m_cEventHandlerForUI, iEventID);
            }

            m_iEventIDTemp = iEventID;
            m_ilParam1 = lParam1;

            Bundle msgBundle = new Bundle();

            msgBundle.putInt("eID", iEventID);
            msgBundle.putInt("lP1", lParam1);
            msgBundle.putInt("lP2", lParam2);
            msgBundle.putLong("lP3", lParam3);
            msgBundle.putString("lP4", lParam4);
            TcnUtility.sendMsg(m_cEventHandlerForUI, iEventID, -1, -1, msgBundle);
        }

        public void sendMsgToUIDelay ( int iEventID, long delayMillis){
            Bundle msgBundle = new Bundle();

            msgBundle.putInt("eID", iEventID);
            msgBundle.putInt("lP1", -1);
            msgBundle.putInt("lP2", -1);
            msgBundle.putLong("lP3", -1);
            msgBundle.putString("lP4", null);
            TcnUtility.removeMessages(m_cEventHandlerForUI, iEventID);
            TcnUtility.sendMsgDelayed(m_cEventHandlerForUI, iEventID, -1, delayMillis, msgBundle);
        }

        public void sendMsgToUIDelay ( int iEventID, int lParam1, int lParam2, long lParam3,
        long delayMillis, String lParam4){
            Bundle msgBundle = new Bundle();

            msgBundle.putInt("eID", iEventID);
            msgBundle.putInt("lP1", lParam1);
            msgBundle.putInt("lP2", lParam2);
            msgBundle.putLong("lP3", lParam3);
            msgBundle.putString("lP4", lParam4);
            TcnUtility.removeMessages(m_cEventHandlerForUI, iEventID);
            TcnUtility.sendMsgDelayed(m_cEventHandlerForUI, iEventID, -1, delayMillis, msgBundle);
        }

        public void sendMsgToUI ( int iEventID, int lParam1, int lParam2, Object obj){
            TcnUtility.sendMsg(m_cEventHandlerForUIObj, iEventID, lParam1, lParam2, obj);
        }

        public void removeMsgToUI ( int iEventID){
            TcnUtility.removeMessages(m_cEventHandlerForUI, iEventID);
        }


        // VendEventListener interface
        public interface VendEventListener {
            public void VendEvent(VendEventInfo cEventInfo);
        }

        private final CopyOnWriteArrayList<VendEventListener> m_Callbacks = new CopyOnWriteArrayList<VendEventListener>();

        public void registerListener (VendEventListener callback){
            synchronized (m_Callbacks) {
                if (null == callback) {
                    return;
                }

                if (!(m_Callbacks.contains(callback))) {
                    m_Callbacks.add(callback);
                }
            }
        }

        public void unregisterListener (VendEventListener callback){
            synchronized (m_Callbacks) {
                if (null == callback) {
                    return;
                }
                if (m_Callbacks.contains(callback)) {
                    m_Callbacks.remove(callback);
                }

            }
        }

        private void sendNotifyToUI (VendEventInfo cEventInfo){
            synchronized (m_Callbacks) {
                for (VendEventListener c : m_Callbacks) {
                    c.VendEvent(cEventInfo);
                }
            }
        }
        private void notifyUI ( int iEventID, int lParam1, int lParam2, long lParam3, String lParam4)
        {
            VendEventInfo cEventInfo = new VendEventInfo();

            cEventInfo.SetEventID(iEventID);
            cEventInfo.SetlParam1(lParam1);
            cEventInfo.SetlParam2(lParam2);
            cEventInfo.SetlParam3(lParam3);
            cEventInfo.SetlParam4(lParam4);
            sendNotifyToUI(cEventInfo);
        }

        private void notifyUI ( int iEventID, int lParam1, int lParam2, long lParam3, String
        lParam4, Object lParam5){
            VendEventInfo cEventInfo = new VendEventInfo();

            cEventInfo.SetEventID(iEventID);
            cEventInfo.SetlParam1(lParam1);
            cEventInfo.SetlParam2(lParam2);
            cEventInfo.SetlParam3(lParam3);
            cEventInfo.SetlParam4(lParam4);
            cEventInfo.SetlParam5(lParam5);
            sendNotifyToUI(cEventInfo);
        }

        public void handleVendMessage (Message msg){
            if (m_VendListener != null) {
                m_VendListener.handleMessage(msg);
            }
        }

        public void handleCommunicationMessage (Message msg){
            if (m_CommunicationListener != null) {
                m_CommunicationListener.handleMessage(msg);
            }
        }

        public boolean handleShip ( int slotNo, String payMedthod, String tradeNo){
            boolean bRet = false;
            if (m_ShipListener != null) {
                bRet = true;
                m_ShipListener.onShip(slotNo, payMedthod, tradeNo);
            }

            return bRet;
        }

        public boolean handleShipTest ( int startSlotNo, int endSlotNo){
            boolean bRet = false;
            if (m_ShipTestListener != null) {
                bRet = true;
                m_ShipTestListener.onShipTest(startSlotNo, endSlotNo);
            }

            return bRet;
        }

        private VendListener m_VendListener = null;
        private boolean m_bVendNotUseDefault = false;
        public void setOnVendListener (VendListener listener,boolean notUseDefault){
            this.m_VendListener = listener;
            this.m_bVendNotUseDefault = notUseDefault;
        }

        private CommunicationListener m_CommunicationListener = null;
        public void setOnCommunicationListener (CommunicationListener listener)
        {
            this.m_CommunicationListener = listener;
        }

        private ShipListener m_ShipListener = null;
        public void setOnShipListener (ShipListener listener){
            m_ShipListener = listener;
        }

        private ShipTestListener m_ShipTestListener = null;
        public void setOnShipTestListener (ShipTestListener listener){
            m_ShipTestListener = listener;
        }

        public interface VendListener {
            public void handleMessage(Message msg);
        }

        public interface CommunicationListener {
            public void handleMessage(Message msg);
        }

        public interface ShipListener {
            public void onShip(int slotNo, String payMedthod, String tradeNo);
        }

        public interface ShipTestListener {
            public void onShipTest(int startSlotNo, int endSlotNo);
        }

    }
