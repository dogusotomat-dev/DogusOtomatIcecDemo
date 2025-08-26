package com.tcn.icecboard.DriveControl;

import com.tcn.icecboard.vend.TcnLog;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/6/8.
 */

public class MyThread extends ThreadPoolExecutor {
    static MyThread myThread;

    public static synchronized MyThread getInstace() {
        if (myThread == null) {
            myThread = new MyThread();
        }
        TcnLog.getInstance().LoggerDebug("MyThread", "getInstace: "+myThread.getCorePoolSize());
        return myThread;
    }

    public MyThread() {
        super(0, Integer.MAX_VALUE, 6L, TimeUnit. SECONDS, new SynchronousQueue<Runnable>());;

    }
}
