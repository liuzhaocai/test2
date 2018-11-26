package com.hopen.darts.utils.Animation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hopen.darts.utils.Animation.bean.AnimationConfig;
import com.hopen.darts.utils.Animation.impl.AnimationUtilImpl;

/**
 * Created by zhangyanxue on 2018/7/17.
 */

@SuppressLint("AppCompatCustomView")
public class AnimationView extends ImageView implements AnimationUtil {

    private AnimationUtilImpl animationUtil;

    public AnimationView(Context context) {
        super(context);
        initUtil();
    }

    public AnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUtil();
    }

    public AnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUtil();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUtil();
    }

    void initUtil(){
        animationUtil = new AnimationUtilImpl(this,false);
    }

    @Override
    public void setConfig(AnimationConfig config) {
        animationUtil.setConfig(config);
    }

    @Override
    public void show(int i) {
        animationUtil.show(i);
    }

    @Override
    public void play() {
        animationUtil.play();
    }

    @Override
    public void pause() {
        animationUtil.pause();
    }

    @Override
    public void play(int i) {
        animationUtil.play(i);
    }

    @Override
    public Boolean pauseStatus() {
        return animationUtil.pauseStatus();
    }

    @Override
    public void stop() {
        animationUtil.stop();
    }

    @Override
    public void onAnimationListener(AnimationListener animationListener) {
        animationUtil.onAnimationListener(animationListener);
    }

    @Override
    public void setDuration(int duration) {
        animationUtil.setDuration(duration);
    }

    @Override
    public void setRepeat(boolean tag) {
        animationUtil.setRepeat(tag);
    }

    @Override
    public void release() {
        animationUtil.release();
    }
}
