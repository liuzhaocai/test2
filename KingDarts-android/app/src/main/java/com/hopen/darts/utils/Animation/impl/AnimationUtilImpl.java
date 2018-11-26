package com.hopen.darts.utils.Animation.impl;

import android.net.Uri;
import android.widget.ImageView;

import com.hopen.darts.utils.Animation.AnimationListener;
import com.hopen.darts.utils.Animation.AnimationUtil;
import com.hopen.darts.utils.Animation.bean.AnimationConfig;
import com.hopen.darts.utils.Animation.res.AnimationResource;

/**
 * Created by zhangyanxue on 2018/7/17.
 */

public class AnimationUtilImpl implements AnimationUtil {

    /**
     * 加载的控件
     */
    private final ImageView img;

    /**
     * 是否重复
     */
    private boolean isRepeat = true;

    /**
     * 当前配置文件
     */
    private AnimationConfig config;

    /**
     * 播放的帧下标
     */
    private int currentIndex = 0;

    /**
     * 是否暂停
     */
    private Boolean mPause = false;

    /**
     * 动画进度监听
     */
    private AnimationListener animationListener = null;

    /**
     * 默认图片间隔时间
     */
    private int duration = 20;

    /**
     * 是否正在播放
     */
    private boolean isPlay = false;

    public AnimationUtilImpl(ImageView image, boolean isRepeat) {
        this.img = image;
        this.isRepeat = isRepeat;
    }

    @Override
    public void show(int i) {
        setPause(true);
        if (getConfig() == null)return;
        AnimationConfig.ImagesBean imagesBean = getConfig().getImages().get(i);
        img.setImageURI(Uri.parse(AnimationResource.getImagePathFromLocal(getConfig(), imagesBean.getName())));
    }

    @Override
    public void play() {
        play(currentIndex);
    }

    @Override
    public void pause() {
        setPause(true);
    }

    @Override
    public synchronized void play(int i) {
        setPause(false);
        if (!isPlay) {
            startAnimation(i);
        } else {
            currentIndex = i;
        }
    }

    synchronized void startAnimation(int i) {
        isPlay = true;
        currentIndex = i;
        if (getConfig() == null)return;
        if (i >= getConfig().getImages().size()) {
            if (isRepeat) {
                if (animationListener != null) {
                    animationListener.onAnimationRepeat();
                }
                i = 0;
            } else {
                if (animationListener != null) {
                    animationListener.onAnimationEnd();
                }
                isPlay = false;
                return;
            }
        }
        if (0 == i) {
            if (animationListener != null) {
                animationListener.onAnimationStart();
            }
        }
        if (getConfig() == null)return;
        AnimationConfig.ImagesBean imagesBean = getConfig().getImages().get(i);
        img.setImageURI(Uri.parse(AnimationResource.getImagePathFromLocal(getConfig(), imagesBean.getName())));
        if (img == null) return;
        final int finalI = i;
        img.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {
                    return;
                }
                if (getConfig() == null)return;
                if (finalI == getConfig().getImages().size()) {
                    if (animationListener != null) {
                        animationListener.onAnimationRepeat();
                    }
                }
                if (animationListener != null) {
                    animationListener.onAnimationIndex(currentIndex);
                }
                startAnimation(finalI + 1);
            }
        }, imagesBean.getCount() * duration);
    }

    synchronized int setPause(Boolean tag) {
        this.mPause = tag;
        if (tag) {
            isPlay = false;
        }
        return currentIndex;
    }

    @Override
    public Boolean pauseStatus() {
        return mPause;
    }

    @Override
    public synchronized void stop() {
        setPause(true);
    }


    @Override
    public void setConfig(AnimationConfig config) {
        this.config = config;
        currentIndex = 0;
        isPlay = false;
    }

    private AnimationConfig getConfig() {
        return config;
    }

    @Override
    public void onAnimationListener(AnimationListener animationListener) {
        this.animationListener = animationListener;
    }

    @Override
    public synchronized void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public synchronized void setRepeat(boolean tag) {
        isRepeat = tag;
    }

    @Override
    public void release() {

    }
}
