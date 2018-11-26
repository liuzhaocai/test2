package com.hopen.darts.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;

public class AliVideoView extends SurfaceView implements
        SurfaceHolder.Callback,
        MediaPlayer.MediaPlayerStoppedListener, MediaPlayer.MediaPlayerPreparedListener,
        MediaPlayer.MediaPlayerFrameInfoListener, MediaPlayer.MediaPlayerErrorListener,
        MediaPlayer.MediaPlayerCompletedListener, MediaPlayer.MediaPlayerSeekCompleteListener {
    public static final String TAG = AliVideoView.class.getSimpleName();

    AliVcMediaPlayer mPlayer;
    String mUrl;
    boolean isPrepared;
    MediaPlayer.MediaPlayerStoppedListener onStoppedListener;
    MediaPlayer.MediaPlayerPreparedListener onPreparedListener;
    MediaPlayer.MediaPlayerFrameInfoListener onFrameInfoListener;
    MediaPlayer.MediaPlayerErrorListener onErrorListener;
    MediaPlayer.MediaPlayerCompletedListener onCompletedListener;
    MediaPlayer.MediaPlayerSeekCompleteListener onSeekCompleteListener;

    public AliVideoView(Context context) {
        super(context);
        init();
    }

    public AliVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AliVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mPlayer.setMediaType(MediaPlayer.MediaType.Vod);
        mPlayer.setDefaultDecoder(0);
        this.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfaceHolder.setKeepScreenOn(true);
        Log.d(TAG, "AlivcPlayer onSurfaceCreated." + mPlayer);
        mPlayer.setVideoSurface(getHolder().getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG, "onSurfaceChanged is valid ? " + surfaceHolder.getSurface().isValid());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "onSurfaceDestroy.");
    }

    public void setLooping(boolean looping) {
        mPlayer.setCirclePlay(looping);
    }

    public void setOnStoppedListener(MediaPlayer.MediaPlayerStoppedListener listener) {
        onStoppedListener = listener;
    }

    public void setOnPreparedListener(MediaPlayer.MediaPlayerPreparedListener listener) {
        onPreparedListener = listener;
    }

    public void setOnFrameInfoListener(MediaPlayer.MediaPlayerFrameInfoListener listener) {
        onFrameInfoListener = listener;
    }

    public void setOnErrorListener(MediaPlayer.MediaPlayerErrorListener listener) {
        onErrorListener = listener;
    }

    public void setOnCompletedListener(MediaPlayer.MediaPlayerCompletedListener listener) {
        onCompletedListener = listener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.MediaPlayerSeekCompleteListener listener) {
        onSeekCompleteListener = listener;
    }

    public void setVideoPath(String url) {
        isPrepared = false;
        mUrl = url;
        mPlayer.prepareToPlay(mUrl);
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public boolean canPause() {
        return true;
    }

    public void start() {
        if (TextUtils.isEmpty(mUrl)) {
            Log.i(TAG, "未指定播放的URL");
            return;
        }
        if (isPrepared) {
            mPlayer.play();
        }
    }

    public void pause() {
        mPlayer.pause();
    }

    public void stop() {
        mPlayer.stop();
    }

    public void stopPlayback() {
        mPlayer.stop();
        mPlayer.destroy();
        isPrepared = false;
    }

    @Override
    public void onStopped() {
        Log.i(TAG, "onStopped");
        if (onStoppedListener != null) {
            try {
                onStoppedListener.onStopped();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPrepared() {
        Log.i(TAG, "onPrepared+" + mUrl);
        isPrepared = true;
        if (onPreparedListener != null) {
            try {
                onPreparedListener.onPrepared();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFrameInfoListener() {
        Log.i(TAG, "onFrameInfoListener");
        if (onFrameInfoListener != null) {
            try {
                onFrameInfoListener.onFrameInfoListener();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(int i, String s) {
        Log.i(TAG, "onError+code" + i + "msg+" + s);
        if (onErrorListener != null) {
            try {
                onErrorListener.onError(i, s);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompleted() {
        Log.i(TAG, "onCompleted");
        if (onCompletedListener != null) {
            try {
                onCompletedListener.onCompleted();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSeekCompleted() {
        Log.i(TAG, "onSeekCompleted");
        if (onSeekCompleteListener != null) {
            try {
                onSeekCompleteListener.onSeekCompleted();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}