package com.hopen.darts.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.alivc.player.AliVcMediaPlayer;
import com.hopen.darts.R;
import com.hopen.darts.networks.commons.NetWork;
import com.hopen.darts.networks.netty.NettyUtil;
import com.hopen.darts.ui.WaitLoadingActivity;
import com.hopen.darts.utils.CrashHandler;
import com.hopen.darts.utils.DaemonUtil;
import com.hopen.darts.utils.LoggerUtils.LogDiskFormatStrategy;
import com.hopen.darts.utils.SerialPort.SerialPortOperator;
import com.hopen.darts.utils.Sound.SoundPlayUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by thomas on 2017/1/17.
 */

public class BaseApplication extends Application {
    private static Context context;
    private static BaseApplication mApplication;
    private static BaseActivity baseActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mApplication = this;
        NetWork.init();
        NettyUtil.initClient();
        SoundPlayUtils.init(this);
        SerialPortOperator.open();
        initLogger();
        CrashHandler.init();
        CrashReport.initCrashReport(getApplicationContext(), "860cf11e70", C.app.debug);
        Logger.d(getResources().getString(R.string.app_name) + "程序启动");
        DaemonUtil.init();
        //初始化播放器（只需调用一次即可，建议在application中初始化）
        AliVcMediaPlayer.init(getApplicationContext());
    }

    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("KingDarts-Log")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        FormatStrategy logDiskFormatStrategy = LogDiskFormatStrategy.newBuilder()
                .tag("KingDarts-Log")
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(logDiskFormatStrategy));
    }

    //拿到应用的上下文Context
    public static Context getContext() {
        return context;
    }

    /**
     * 获取 Application 对象
     */
    public static BaseApplication getApplication() {
        return mApplication;
    }

    /**
     * 获取当前显示的activity
     *
     * @return 当前显示的activity
     */
    public static BaseActivity getBaseActivity() {
        return baseActivity;
    }

    /**
     * 设置当前显示的activity
     */
    public static void setBaseActivity(BaseActivity base_activity) {
        baseActivity = base_activity;
    }

    /**
     * 重启本程序
     */
    public static void restart(){
        //启动页
        Intent intent = new Intent(mApplication, WaitLoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mApplication.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
