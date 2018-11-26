package com.hopen.darts.utils.UpdateApk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hopen.darts.ui.WaitLoadingActivity;
import com.orhanobut.logger.Logger;

public class BCRUpgradeApk extends BroadcastReceiver{

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Logger.e("BCRUpgradeApk" + "BOOT_COMPLETED 启动了");
            Intent intent1 = new Intent(context, WaitLoadingActivity.class);  // 要启动的Activity
            //1.如果自启动APP，参数为需要自动启动的应用包名
            //Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            //下面这句话必须加上才能开机自动运行app的界面
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //2.如果自启动Activity
            //context.startActivity(intent);
            //3.如果自启动服务
            context.startService(intent1);
        }
//        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){
//            Logger.e("BCRUpgradeApk" + "升级了一个安装包");
//            Intent intent2 = new Intent(context, WaitLoadingActivity.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent2);
//        }

    }

}
