package com.hopen.darts.utils.live;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.bumptech.glide.Glide;

/**
 * Created by apple on 2018/6/27.
 * 直播播放器
 */

public class LiveSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback,
        MediaPlayer.MediaPlayerStoppedListener, MediaPlayer.MediaPlayerPreparedListener,
        MediaPlayer.MediaPlayerFrameInfoListener, MediaPlayer.MediaPlayerErrorListener,
        MediaPlayer.MediaPlayerCompletedListener, MediaPlayer.MediaPlayerSeekCompleteListener {
    public static final String TAG = LiveSurfaceView.class.getSimpleName();
    AliVcMediaPlayer mPlayer;
    private MediaPlayer.VideoMirrorMode mirrorMode = MediaPlayer.VideoMirrorMode.VIDEO_MIRROR_MODE_NONE;
    private String mUrl;
    private FrameLayout frameLayout;
    private String headUrl;
    public LiveSurfaceView(Context context) {
        super(context);
        init();
    }

    public LiveSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPlayer = new AliVcMediaPlayer(getContext(), this);
        mPlayer.setPreparedListener(this);
        mPlayer.setFrameInfoListener(this);
        mPlayer.setErrorListener(this);
        mPlayer.setCompletedListener(this);
        mPlayer.setSeekCompleteListener(this);
        mPlayer.setStoppedListener(this);
        mPlayer.enableNativeLog();
        mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);//设置裁剪沾满全屏
        mPlayer.setRenderMirrorMode(MediaPlayer.VideoMirrorMode.VIDEO_MIRROR_MODE_NONE);
        mPlayer.setMaxBufferDuration(500);
        mPlayer.setMuteMode(true);//设置静音
        this.getHolder().addCallback(this);
    }

    public void setPlayUrlToPyal(String mUrl, FrameLayout frameLayout,boolean canUse,String headUrl,boolean canPlay) {
        this.mUrl = mUrl;
        this.frameLayout = frameLayout;
        this.headUrl = headUrl;
        ImageView headImageView = new ImageView(frameLayout.getContext());
        headImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(frameLayout.getContext()).load(headUrl).into(headImageView);
        if (this.frameLayout.getChildCount()!=0){
            this.frameLayout.removeAllViews();
        }
        this.frameLayout.addView(headImageView);
        if (canUse&&canPlay){
            replay();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfaceHolder.setKeepScreenOn(true);
        Log.d(TAG, "AlivcPlayer onSurfaceCreated." + mPlayer);
        // Important: surfaceView changed from background to front, we need reset surface to mediaplayer.
        // 对于从后台切换到前台,需要重设surface;部分手机锁屏也会做前后台切换的处理
        if (mPlayer != null) {
            mPlayer.setVideoSurface(getHolder().getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG, "onSurfaceChanged is valid ? " + surfaceHolder.getSurface().isValid());
        if (mPlayer != null) {
            mPlayer.setSurfaceChanged();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "onSurfaceDestroy.");
    }

    private void start() {
        if (mUrl == null) {
            Log.i(TAG, "请先指定播放的URL");
            return;
        }
        if (mPlayer != null) {
            mPlayer.prepareAndPlay(mUrl);
        }
    }

    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    public void stop() {
        if (mPlayer != null&&mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }

    public void resume() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.play();
        }
    }

    public void destroy() {
        if (mPlayer != null&&mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.destroy();
        }
    }

    public void replay() {
        stop();
        start();
    }

    @Override
    public void onStopped() {
        Log.i(TAG, "onStopped");
    }

    @Override
    public void onPrepared() {
        Log.i(TAG, "onPrepared+"+mUrl);
        this.frameLayout.removeAllViews();
        this.frameLayout.addView(this);
    }

    @Override
    public void onFrameInfoListener() {
        Log.i(TAG, "onFrameInfoListener");
    }

    @Override
    public void onError(int i, String s) {
        Log.i(TAG, "onError+code" + i + "msg+" + s);
        replay();
    }

    @Override
    public void onCompleted() {
        Log.i(TAG, "onCompleted");
    }

    @Override
    public void onSeekCompleted() {
        Log.i(TAG, "onSeekCompleted");
    }

}
