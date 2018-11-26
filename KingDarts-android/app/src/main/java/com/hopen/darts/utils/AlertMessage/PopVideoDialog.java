package com.hopen.darts.utils.AlertMessage;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseDialog;
import com.hopen.darts.base.C;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.widget.PLVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 全局播放视频使用
 */
public class PopVideoDialog extends BaseDialog {
    @BindView(R.id.video_view)
    PLVideoView videoView;
    Unbinder unbinder;

    private boolean isPrepared = false;
    private Uri uri = null;
    private boolean looping = false;

    @Override
    protected View createDialogView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_t_video, null, false);
        unbinder = ButterKnife.bind(this, view);
        setShadowRes(R.color.shadow_bg);
        tAnimationDuration = 1000;
        //初始化视频播放控件
        initPlayer();
        return view;
    }

    private void initPlayer() {
        videoView.setLooping(looping);//是否循环
        //准备完成回调
        videoView.setOnPreparedListener(new PLOnPreparedListener() {
            @Override
            public void onPrepared(int i) {
                isPrepared = true;
                videoView.start();// 播放
            }
        });
        //播放完成回调
        videoView.setOnCompletionListener(new PLOnCompletionListener() {
            @Override
            public void onCompletion() {
                if (!looping) dismiss();
            }
        });
        //播放错误回调
        videoView.setOnErrorListener(new PLOnErrorListener() {
            @Override
            public boolean onError(int i) {
                Log.e("VideoView Error", "无法播放此视频。i = " + i);
                dismiss();
                return true;
            }
        });
        videoView.setVideoURI(uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPrepared && !videoView.isPlaying()) {
            videoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView.canPause()) {
            videoView.pause();
        }
    }

    @Override
    public void onDestroy() {
        try {
            videoView.stopPlayback();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * 设置播放资源
     *
     * @param path_enum 资源路径枚举
     * @return 链式调用，返回自身
     */
    public PopVideoDialog path(VideoEnum path_enum) {
        uri = Uri.parse(C.app.file_path_audio + "/" + path_enum.name() + ".mp4");
        return this;
    }

    /**
     * 是否循环播放
     *
     * @param looping 是否循环播放
     * @return 链式调用，返回自身
     */
    public PopVideoDialog looping(boolean looping) {
        this.looping = looping;
        return this;
    }

    /**
     * 弹窗关闭回调监听
     *
     * @param listener 弹窗关闭回调监听
     * @return 链式调用，返回自身
     */
    public PopVideoDialog dismissListener(OnDismissWithAnimEndListener listener) {
        setOnDismissWithAnimEndListener(listener);
        return this;
    }

    /**
     * 展示此弹窗
     *
     * @param activity 所在activity
     */
    public void show(Activity activity) {
        show(activity.getFragmentManager());
    }

    /**
     * 入口方法
     *
     * @return 返回一个弹窗对象
     */
    public static PopVideoDialog get() {
        return new PopVideoDialog();
    }

}
