package com.hopen.darts.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;

/**
 * Created by zhangyanxue on 2018/4/12.
 */

public class AnimationUtil {


    public static void rotateBtn(final View view, final float value) {
        view.post(new Runnable() {
            @Override
            public void run() {
                Animation anim = new RotateAnimation(0f, value, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setFillAfter(true); // 设置保持动画最后的状态
                anim.setDuration(300); // 设置动画时间
                anim.setInterpolator(new AccelerateInterpolator()); // 设置插入器
                view.startAnimation(anim);
            }
        });
    }

    public static void rotateRecoverBtn(final View view, final float value) {
        view.post(new Runnable() {
            @Override
            public void run() {
                Animation anim = new RotateAnimation(value, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setFillAfter(true); // 设置保持动画最后的状态
                anim.setDuration(300); // 设置动画时间
                anim.setInterpolator(new AccelerateInterpolator()); // 设置插入器
                view.startAnimation(anim);
            }
        });
    }

    public static void animationHViewHidden(View view, Context context) {
        ObjectAnimator animatorNav = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), -view.getHeight());
        animatorNav.setDuration(300);
        animatorNav.start();
        animatorNav.setInterpolator(AnimationUtils.loadInterpolator(context, android.R.interpolator.linear));
    }

    public static void animationHViewShow(View view, Context context) {
        ObjectAnimator animatorNav = ObjectAnimator.ofFloat(view, "translationY", 0);
        animatorNav.setDuration(300);
        animatorNav.start();
        animatorNav.setInterpolator(AnimationUtils.loadInterpolator(context, android.R.interpolator.linear));
    }

    public static void animationWViewHidden(View view, Context context) {
        ObjectAnimator animatorNav = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX(), -view.getWidth());
        animatorNav.setDuration(300);
        animatorNav.start();
        animatorNav.setInterpolator(AnimationUtils.loadInterpolator(context, android.R.interpolator.linear));
    }

    public static void animationWViewShow(View view, Context context) {
        ObjectAnimator animatorNav = ObjectAnimator.ofFloat(view, "translationX", 0);
        animatorNav.setDuration(300);
        animatorNav.start();
        animatorNav.setInterpolator(AnimationUtils.loadInterpolator(context, android.R.interpolator.linear));
    }

    public static void performHeightAnim(final View view, int from, int to){
        performHeightAnim(view,from,to,500,null);
    }

    public static void performHeightAnim(final View view, int from, int to, int time, final Call call){
        //属性动画对象
        ValueAnimator va ;
        va = ValueAnimator.ofInt(from,to);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                view.getLayoutParams().height = h;
                if (call!=null){
                    call.callBack(valueAnimator.getAnimatedFraction());
                }
                view.requestLayout();
            }
        });
        va.setDuration(time);
        //开始动画
        va.start();
    }

    public static void performwidthhAnim(final View view, int from, int to){
        performwidthhAnim(view,from,to,500, null);
    }

    public static void performwidthhAnim(final View view, int from, int to, int time, final Call call){
        //属性动画对象
        ValueAnimator va ;
        va = ValueAnimator.ofInt(from,to);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                view.getLayoutParams().width = h;
                if (call!=null){
                    call.callBack(valueAnimator.getAnimatedFraction());
                }
                view.requestLayout();
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (call!=null){
                    call.callBack(1.f);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.setDuration(time);
        //开始动画
        va.start();
    }

    public interface Call{
        void callBack(float valueAnimator);
    }

}
