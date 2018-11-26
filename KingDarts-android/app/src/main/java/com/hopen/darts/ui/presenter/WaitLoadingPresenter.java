package com.hopen.darts.ui.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.hopen.darts.base.C;
import com.hopen.darts.networks.commons.BaseResponse;
import com.hopen.darts.networks.commons.NetWork;
import com.hopen.darts.networks.interfaces.OnErrorCallback;
import com.hopen.darts.networks.interfaces.OnSuccessCallback;
import com.hopen.darts.networks.request.GetAppVersionRequest;
import com.hopen.darts.networks.response.GetAppVersionResponse;
import com.hopen.darts.ui.WaitLoadingActivity;
import com.hopen.darts.utils.DBManager.DBManager;
import com.hopen.darts.utils.DefaultOkDownLoadListener;
import com.hopen.darts.utils.FileUtils;
import com.hopen.darts.utils.PhoneUtil;
import com.hopen.darts.utils.UpdateApk.InstallApkUtils;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class WaitLoadingPresenter {

    private final WaitLoadingActivity waitLoadingActivity;
    private DBManager dbManager;
    private List<String> fileNames = new ArrayList<>();
    private int fileCount = 0;

    public WaitLoadingPresenter(WaitLoadingActivity waitLoadingActivity) {
        this.waitLoadingActivity = waitLoadingActivity;
    }

    /**
     * 读取仪器设备信息
     */
    public void readDeviceInfo() {
        upDataProgress(21, "正在读取设备信息");
        DisplayMetrics metric = new DisplayMetrics();
        waitLoadingActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        float density = metric.density;
        int densityDpi = metric.densityDpi;
        String info =
                "飞镖机型号: " + android.os.Build.MODEL
                        + ",\nSDK版本:" + android.os.Build.VERSION.SDK
                        + ",\n系统版本:" + android.os.Build.VERSION.RELEASE
                        + ",\nAppVersion:" + PhoneUtil.getVersionCode(waitLoadingActivity)
                        + "\n屏幕宽度（像素）: " + width
                        + "\n屏幕高度（像素）: " + height
                        + "\n屏幕密度:  " + density
                        + "\n屏幕密度DPI: " + densityDpi;
        Logger.d("System INFO\n" + info);
        upDataProgress(40, "读取仪器设备信息完成");
    }

    /**
     * 初始化文件夹路径
     */
    public void initFilePath() {
        upDataProgress(41, "正在初始化文件");
        String log = "";
        log = log + createDir(C.app.file_path_img);
        log = log + createDir(C.app.file_path_img + "/animation");
        log = log + createDir(C.app.file_path_audio);
        log = log + createDir(C.db.file_path_db);
        log = log + createDir(C.player.BasePlayerImagePath);
        File tempFile = new File(C.app.file_path_temp);
        if (tempFile.exists()) {
            FileUtils.deleteDirectory(tempFile);
            log = log + C.app.file_path_temp + "缓存文件夹清除成功";
        } else {
            log = log + C.app.file_path_temp + "未发现temp缓存文件夹";
        }
        createDir(C.app.file_path_temp);
        Logger.d("初始化文件路径\n" + log);
        upDataProgress(60, "初始化文件完成");
    }

    /**
     * 检测程序版本
     */
    public void checkAppVersion() {
        upDataProgress(61, "正在检测程序版本");
        GetAppVersionRequest request = new GetAppVersionRequest();
        NetWork.request(waitLoadingActivity, request, new OnSuccessCallback() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                GetAppVersionResponse appVersionResponse = (GetAppVersionResponse) baseResponse;
                Logger.d("开始进行程序升级校验");
                int versionCode = PhoneUtil.getVersionCode(waitLoadingActivity);
                if (versionCode >= appVersionResponse.getData().getAppVersion()) {
                    upDataProgress(80, "检测程序版本完成");
                } else {
                    downLoadApp(appVersionResponse.getData());
                }
            }
        }, new OnErrorCallback() {
            @Override
            public void onError(BaseResponse baseResponse) {
                upDataProgress(80, "检测程序版本失败");
            }
        }, false);

    }

    /**
     * 下载App
     *
     * @param dataBean
     */
    public void downLoadApp(GetAppVersionResponse.DataBean dataBean) {
        upDataProgress(65, "正在下载新版游戏");
        Logger.d("开始下载程序：" + dataBean.getAppUrl() + "\n版本号为：" + dataBean.getAppVersion());
        DownloadTask task = new DownloadTask.Builder(C.network.file_url + dataBean.getAppUrl(),
                C.app.file_path_temp, "darts_c" + dataBean.getAppVersion() + "_release.apk").build();
        task.enqueue(new DefaultOkDownLoadListener() {

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                if (cause == EndCause.COMPLETED && task.getFile() != null) {
                    upDataProgress(75, "程序下载完成");
                    upDataProgress(78, "开始更新程序");
                    InstallApkUtils.installApk(task.getFile().getAbsolutePath());
                } else {
                    Logger.e("程序下载失败", realCause);
                    upDataProgress(80, "程序下载失败!");
                }
            }
        });
    }

    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    private String createDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            return path + "\n";
        } else {
            return "存在" + path + "\n";
        }
    }

    public abstract void upDataProgress(int pro, String msg);

    public abstract void upDataError(String msg);

}
