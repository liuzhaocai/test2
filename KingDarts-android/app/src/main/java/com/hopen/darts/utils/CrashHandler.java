package com.hopen.darts.utils;

import android.util.Log;

import com.hopen.darts.base.BaseApplication;
import com.hopen.darts.base.C;
import com.hopen.darts.networks.netty.NettyUtil;

/**
 * 程序崩溃信息拦截处理器
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler mInstance;

    private CrashHandler() {
    }

    /**
     * 拦截崩溃信息
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(throwable) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, throwable);
        }
    }

    /**
     * 处理崩溃信息
     *
     * @param ex 异常信息
     * @return 是否拦截处理
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        if (C.app.debug) {
            return false;
        } else {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
            try {
                NettyUtil.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            BaseApplication.restart();
            return true;
        }
    }

    /**
     * 初始化程序崩溃信息拦截处理器
     */
    public static void init() {
        if (mInstance == null)
            mInstance = new CrashHandler();
        mInstance.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mInstance);
    }
}
