package com.tcn.icecboard;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.tcn.icecboard.control.TcnVendIF;


/**
 * 描述：
 * 作者：Jiancheng,Song on 2016/5/29 22:24
 * 邮箱：m68013@qq.com
 */
public class TcnService extends Service {

	private static final String TAG = "TcnService";


	@Override
	public void onCreate() {
		super.onCreate();
		TcnVendIF.getInstance().LoggerDebug(TAG, "TcnService onCreate()");
		TcnVendIF.getInstance().startWorkThread();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		TcnVendIF.getInstance().LoggerDebug(TAG, "TcnService onStartCommand() flags: "+flags+" startId:"+startId);
		flags = Service.START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		TcnVendIF.getInstance().LoggerDebug(TAG, "TcnService onBind()");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		TcnVendIF.getInstance().LoggerDebug(TAG, "TcnService onUnbind()");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		TcnVendIF.getInstance().LoggerDebug(TAG, "TcnService onDestroy()");
		TcnVendIF.getInstance().stopWorkThread();
	}
}
