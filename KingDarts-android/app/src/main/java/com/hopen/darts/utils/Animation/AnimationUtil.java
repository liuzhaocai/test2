package com.hopen.darts.utils.Animation;

import com.hopen.darts.utils.Animation.bean.AnimationConfig;

/**
 * 播放动画工具类
 * Created by zhangyanxue on 2018/7/17.
 */

public interface AnimationUtil {

    /**
     * 设置动画加载类型
     *
     * @param config
     */
    void setConfig(AnimationConfig config);

    /**
     * 显示图片
     *
     * @param i 显示第几帧
     */
    void show(int i);

    /**
     * 继续播放默认第0
     *
     */
    void play();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 开始播放
     *
     * @param i 第几帧
     */
    void play(int i);

    /**
     * 播放状态
     *
     * @return 暂停帧数
     */
    Boolean pauseStatus();

    /**
     * 停止播放彻底停止
     */
    void stop();

    /**
     * 设置动画监听
     *
     * @param animationListener
     */
    void onAnimationListener(AnimationListener animationListener);

    /**
     * 设置每帧时间
     *
     * @param duration
     */
    void setDuration(int duration);

    /**
     * 是否重复
     * @param tag
     */
    void setRepeat(boolean tag);

    /**
     * 停止播放彻底停止
     */
    void release();

}
