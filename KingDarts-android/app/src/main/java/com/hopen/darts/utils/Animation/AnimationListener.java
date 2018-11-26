package com.hopen.darts.utils.Animation;

/**
 * Created by zhangyanxue on 2018/7/17.
 */

public interface AnimationListener {

    /**
     * <p>Notifies the start of the animation.</p>
     */
    void onAnimationStart();

    /**
     * <p>Notifies the end of the animation. This callback is not invoked
     * for animations with repeat count set to INFINITE.</p>
     */
    void onAnimationEnd();

    /**
     * <p>Notifies the repetition of the animation.</p>
     */
    void onAnimationRepeat();

    /**
     * <p>Notifies the currentIndex of the animation.</p>
     */
    void onAnimationIndex(int currentIndex);

}
