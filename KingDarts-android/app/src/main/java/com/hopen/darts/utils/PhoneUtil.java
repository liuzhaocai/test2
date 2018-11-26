package com.hopen.darts.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.hopen.darts.base.C;
import com.hopen.darts.utils.Animation.IOUtils.IOUtils;

import java.io.File;
import java.io.FileInputStream;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by thomas on 2017/9/13.
 */

public class PhoneUtil {

    /**
     * 获取设备唯一编码
     *
     * @param context 上下文
     * @return 设备唯一编码
     */
    public static String getDeviceId(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        PackageManager pm = context.getPackageManager();
        if (TelephonyMgr != null && PackageManager.PERMISSION_GRANTED == pm.checkPermission(Manifest.permission.READ_PHONE_STATE, context.getPackageName())) {
            return TelephonyMgr.getDeviceId();
        } else {
            return "";
        }
    }

    /**
     * 获取程序软件版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static int getVersionCode(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return 版本名称
     */
    public static String getVersionName(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本名
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readSNCode() {
        File file = new File(C.app.file_path + "/code.txt");
        if (!file.exists()) {
            return C.app.no_sn;
        }
        try {
            FileInputStream localFileInputStream = new FileInputStream(C.app.file_path + "/code.txt");
            String code = IOUtils.toString(localFileInputStream, "UTF-8");
            String codeTrim = code.replaceAll("\r\n", "").replaceAll(" ","");
            if (TextUtils.isEmpty(codeTrim)){
                return C.app.no_sn;
            }else {
                return codeTrim;
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return C.app.no_sn;
    }

}
