package com.hopen.darts.ui.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.hopen.darts.base.C;
import com.hopen.darts.bean.FileVersion;
import com.hopen.darts.networks.commons.BaseResponse;
import com.hopen.darts.networks.commons.NetWork;
import com.hopen.darts.networks.interfaces.OnErrorCallback;
import com.hopen.darts.networks.interfaces.OnSuccessCallback;
import com.hopen.darts.networks.request.GetResourceRequest;
import com.hopen.darts.networks.response.GetResourceResponse;
import com.hopen.darts.utils.DBManager.DBManager;
import com.hopen.darts.utils.DefaultOkDownLoadListener;
import com.hopen.darts.utils.ZipUntil;
import com.liulishuo.okdownload.DownloadContext;
import com.liulishuo.okdownload.DownloadContextListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhangyanxue on 2018/7/23.
 */

public abstract class DownLoadResourcePresenter {

    private int startPort;
    private int length;
    private DBManager dbManager;
    private Activity activity;

    public DownLoadResourcePresenter(Activity activity, int startPort, int length) {
        this.activity = activity;
        this.startPort = startPort;
        this.length = length;
        dbManager = DBManager.getManager(activity, C.db.file_path_db + C.db.dbName);
    }

    /**
     * 从服务器端拉取全部资源文件更新数据
     */
    public void pullResourceFilesVersion() {
        upDataProgressInner(0, "正在检测资源版本");
        NetWork.request(activity, new GetResourceRequest(), new OnSuccessCallback() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                GetResourceResponse resourceRsponse = (GetResourceResponse) baseResponse;
                Logger.i("开始进行资源升级校验");
                checkResourceFilesVersion(resourceRsponse.getData());
            }
        }, new OnErrorCallback() {
            @Override
            public void onError(BaseResponse baseResponse) {
                upDataProgressInner(100, "获取最新程序资源失败");
            }
        }, false);

    }

    /**
     * 校验服务端数据与本地数据的版本
     *
     * @param upDataBeans 服务端的更新数据
     */
    private void checkResourceFilesVersion(List<GetResourceResponse.DataBean> upDataBeans) {
        //假如所有都需要下载
        List<GetResourceResponse.DataBean> tasks = new ArrayList<>(upDataBeans);
        List<FileVersion> fileVersions = dbManager.queryList(FileVersion.class);//从数据库读取本地文件版本信息
        for (GetResourceResponse.DataBean dataBean : upDataBeans) {
            for (FileVersion fileVersion : fileVersions) {
                if (TextUtils.equals(fileVersion.getResourceName(), dataBean.getResourceName())) {
                    if (dataBean.getResourceVersion() <= fileVersion.getResourceVersion()) {
                        tasks.remove(dataBean);//不需要下载的资源地址进行移除
                    }
                }
            }
        }
        if (tasks.size() == 0) {//没有需要更新的资源
            upDataProgressInner(100, "资源校验完毕");
        } else {//开始更新资源
            upDataProgressInner(10, "正在下载资源");
            Collections.sort(tasks, new Comparator<GetResourceResponse.DataBean>() {
                @Override
                public int compare(GetResourceResponse.DataBean o1, GetResourceResponse.DataBean o2) {
                    return o1.getResourceVersion() - o2.getResourceVersion();
                }
            });
            serialDownLoadResourceFiles(tasks);
        }
    }

    /**
     * 串行下载需要更新的文件
     *
     * @param upDataBeans 需要更新的文件数据对象列表
     */
    private void serialDownLoadResourceFiles(List<GetResourceResponse.DataBean> upDataBeans) {
        //构建下载队列
        DownloadContext.Builder builder = new DownloadContext.QueueSet().commit();
        //向下载队列中绑定下载任务
        for (GetResourceResponse.DataBean item : upDataBeans) {
            try {
                DownloadTask.Builder task_builder;
                if (item.getType() == 2) {
                    //先删除原有的旧文件
                    File old = new File(C.app.file_path_audio, item.getResourceName());
                    if (old.exists()) old.delete();
                    task_builder = new DownloadTask.Builder(C.network.file_url + item.getResourceUrl(),
                            C.app.file_path_audio, item.getResourceName());
                } else {
                    task_builder = new DownloadTask.Builder(C.network.file_url + item.getResourceUrl(),
                            C.app.file_path_temp, item.getResourceName());
                }
                builder.bind(task_builder).setTag(item);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        //下载结束监听
        builder.setListener(new DownloadContextListener() {
            @Override
            public void taskEnd(@NonNull DownloadContext context, @NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, int remainCount) {
                if (cause == EndCause.COMPLETED) {
                    downLoadSuccess((GetResourceResponse.DataBean) task.getTag(), task.getFile());
                } else {
                    Logger.i("文件：\"" + ((GetResourceResponse.DataBean) task.getTag()).getResourceName() + "\"下载失败");
                }
            }

            @Override
            public void queueEnd(@NonNull DownloadContext context) {
                upDataProgressInner(100, "资源全部更新完成");
            }
        });
        DownloadContext context = builder.build();
        context.start(new DefaultOkDownLoadListener() {

            @Override
            public void taskStart(@NonNull DownloadTask task) {
                GetResourceResponse.DataBean tag = (GetResourceResponse.DataBean) task.getTag();
                Logger.i("开始下载文件：" + tag.getResourceName() + "\n版本号为：" + tag.getResourceVersion());
            }
        }, true);
    }

    /**
     * 处理下载成功的文件
     */
    private void downLoadSuccess(final GetResourceResponse.DataBean dataBean, File file) {
        switch (dataBean.getType()) {
            case 1:
                unzipManager(dataBean, file);
                break;
            case 2:
                Logger.i("文件：\"" + dataBean.getResourceName() + "\"下载成功");
                dbManager.insert(new ArrayList<FileVersion>() {{
                    add(new FileVersion(dataBean));
                }}, "id");
                break;
            case 3:
                unzipManager(dataBean, file);
                break;
        }
    }

    /**
     * 处理压缩包
     */
    private void unzipManager(final GetResourceResponse.DataBean dataBean, File file) {
        Logger.i("文件：\"" + dataBean.getResourceName() + "\"下载成功\n开始解压资源文件" + file.getName());
        String outPutStr = null;
        if (dataBean.getType() == 1) {
            outPutStr = C.app.file_path_audio + "/";
        } else if (dataBean.getType() == 3) {
            outPutStr = C.app.file_path_img + "/animation/";
        }
        try {
            ZipUntil.UnZipFolderForZipPath(file.getAbsolutePath(), dataBean.getFileName() + "/", outPutStr);
            Logger.i("资源文件" + file.getName() + "解压成功\n开始删除临时文件");
            if (file.delete()) {
                Logger.i("删除临时文件成功");
            } else {
                Logger.i("删除临时文件失败");
            }
            Logger.i("文件：\"" + dataBean.getResourceName() + "\"更新成功");
            dbManager.insert(new ArrayList<FileVersion>() {{
                add(new FileVersion(dataBean));
            }}, "id");
        } catch (Exception e) {
            Logger.e("资源文件" + file.getName() + "解压失败", e);
            upDataError("资源文件" + file.getName() + "解压失败");
            e.printStackTrace();
        }
    }

    private void upDataProgressInner(int i, String msg) {
        upDataProgress(startPort + i * length / 100, msg);
    }

    public abstract void upDataProgress(int pro, String msg);

    public abstract void upDataError(String msg);
}
