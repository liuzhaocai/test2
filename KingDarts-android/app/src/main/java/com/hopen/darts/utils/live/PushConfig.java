package com.hopen.darts.utils.live;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import com.hopen.darts.base.C;

import java.util.LinkedList;

import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.filter.hardvideofilter.BaseHardVideoFilter;
import me.lake.librestreaming.filter.hardvideofilter.HardVideoGroupFilter;
import me.lake.librestreaming.ws.StreamAVOption;
import me.lake.librestreaming.ws.StreamLiveCameraView;
import me.lake.librestreaming.ws.filter.hardfilter.GPUImageBeautyFilter;
import me.lake.librestreaming.ws.filter.hardfilter.extra.GPUImageCompatibleFilter;

/**
 * Created by apple on 2018/7/9.
 */

public class PushConfig {
    public static final String TAG = PushConfig.class.getSimpleName();
    private static StreamLiveCameraView mLiveCameraView;
    private static StreamAVOption streamAVOption;
    private int reConnectTime = 0;
    private LiveSurfaceView mSurfaceView;
    private String pushUrlHead = "rtmp://video-center-bj.alivecdn.com/loveDarts/";
    private String pushUrlBottom = "?vhost=online.lovedarts.cn";
    private String playUrl = "rtmp://online.lovedarts.cn/loveDarts/";
    private boolean cameraCanUse;
    private boolean canUse;
    private boolean canPlay;
    private boolean canPush;
    private String headUrl;

    public PushConfig(Context context) {
        mSurfaceView = new LiveSurfaceView(context);
        cameraCanUse = CameraUtil.isCameraCanUse();
        mLiveCameraView = new StreamLiveCameraView(context);
        //参数配置 startPush
        streamAVOption = new StreamAVOption();
        //参数配置 endend
        try {
            mLiveCameraView.init(context, streamAVOption);
            mLiveCameraView.addStreamStateListener(resConnectionListener);
            LinkedList<BaseHardVideoFilter> files = new LinkedList<>();
            files.add(new GPUImageCompatibleFilter(new GPUImageBeautyFilter()));
            mLiveCameraView.setHardVideoFilter(new HardVideoGroupFilter(files));
        }catch (Exception e){
            cameraCanUse = false;
        }
    }

    /**
     * 设置推流地址
     *
     * @param rtmpUrl
     */
    public PushConfig setPushUrl(String rtmpUrl) {
        if (streamAVOption != null) {
            streamAVOption.streamUrl = pushUrlHead + rtmpUrl + pushUrlBottom;
            canPush = !C.app.no_sn.equals(rtmpUrl);
        }
        return this;
    }

    /**
     * 设置推流地址
     *
     * @param url
     */
    public PushConfig setPlayUrl(String url, boolean canUse, String headUrl) {
        if (streamAVOption != null) {
            this.canUse = canUse;
            this.headUrl = headUrl;
            streamAVOption.playUrl = playUrl + url;
            canPlay = !C.app.no_sn.equals(url);
        }
        return this;
    }

    /**
     * 开始推流
     */
    public void startPush() {
        if (!cameraCanUse) {
            Log.i(TAG, "摄像头不可用");
            return;
        }
        if (!canPush){
            return;
        }
        if (!mLiveCameraView.isStreaming() && mLiveCameraView != null && streamAVOption.streamUrl != null) {
            mLiveCameraView.startStreaming(streamAVOption.streamUrl);
        }
    }

    /**
     * 开始直播
     */

    public void startPlay(FrameLayout frameLayout) {
        if (mSurfaceView != null && streamAVOption.playUrl != null) {
            mSurfaceView.setPlayUrlToPyal(streamAVOption.playUrl, frameLayout, canUse, headUrl,canPlay);
        }
    }

    /**
     * 开始推流
     */

    public void stopPlay() {
        if (mSurfaceView != null) {
            mSurfaceView.stop();
        }
    }

    /**
     * 停止推流
     */
    public void stopPush() {
        if (mLiveCameraView != null && mLiveCameraView.isStreaming()) {
            mLiveCameraView.stopStreaming();
        }
    }

    /**
     * 转换摄像头
     */
    public void swapCamera() {
        if (mLiveCameraView != null) {
            mLiveCameraView.swapCamera();
        }
    }

    /**
     * 设置回调监听
     *
     * @param resConnectionListener
     */
    public void setResConnectionListener(RESConnectionListener resConnectionListener) {
        if (resConnectionListener != null && mLiveCameraView != null) {
            mLiveCameraView.addStreamStateListener(resConnectionListener);
        }
    }

    /**
     * 释放
     */
    public void release() {
        if (mLiveCameraView != null) {
            if (mLiveCameraView.isStreaming())
                stopPush();
            mLiveCameraView.destroy();
        }
        if (mSurfaceView!=null){
            mSurfaceView.destroy();
        }
    }

    /**
     * 断线重连
     */
    public void rePush() {
        stopPush();
        startPush();
    }

    RESConnectionListener resConnectionListener = new RESConnectionListener() {
        @Override
        public void onOpenConnectionResult(int result) {
            //result 0成功  1 失败
            Log.i(TAG, "打开推流连接 状态：" + result + " 推流地址：" + streamAVOption.streamUrl);
            if (result == 0) {
                reConnectTime = 0;
            }
        }

        @Override
        public void onWriteError(int errno) {
            Log.i(TAG, "推流出错,正在尝试重连");
            if (reConnectTime < 5) {
                reConnectTime++;
                rePush();
            }
        }

        @Override
        public void onCloseConnectionResult(int result) {
            //result 0成功  1 失败
            Log.i(TAG, "关闭推流连接 状态：" + result);
        }
    };
}
