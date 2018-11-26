package com.hopen.darts.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.Round;

import pl.droidsonroids.gif.GifImageView;

/**
 * 游戏页面右侧旋转飞镖控件
 */

public class DartsView extends FrameLayout {
    BaseGame mGame;
    int dartPadding, widthPixels;
    ValueAnimator[] animatorArray;
    GifImageView[] dartImages;
    View[] scoreViews;

    public DartsView(Context context) {
        super(context);
        init(context, null);
    }

    public DartsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DartsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化控件操作数据
     */
    private void init(Context context, @Nullable AttributeSet attrs) {
        //允许超出范围显示
        setClipChildren(false);
        setClipToPadding(false);
        //默认padding和屏幕宽度
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        dartPadding = (int) (displayMetrics.density * 4f + 0.5f);
        widthPixels = displayMetrics.widthPixels;
    }

    /**
     * 创建一个属性动画
     *
     * @return 属性动画
     */
    private ValueAnimator createAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateInterpolator());
        return animator;
    }

    /**
     * 显示飞镖信息
     *
     * @param game 游戏信息对象
     */
    public void withGame(BaseGame game) {
        if (game == null) return;
        mGame = game;
        //清空之前的child
        removeAllViews();
        //分数父容器
        LinearLayout score_layout = new LinearLayout(getContext());
        score_layout.setOrientation(LinearLayout.VERTICAL);
        score_layout.setGravity(Gravity.CENTER);
        score_layout.setClipChildren(false);
        score_layout.setClipToPadding(false);
        LayoutParams score_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(score_layout, score_params);
        //飞镖父容器
        LinearLayout darts_layout = new LinearLayout(getContext());
        darts_layout.setOrientation(LinearLayout.VERTICAL);
        darts_layout.setGravity(Gravity.RIGHT);
        darts_layout.setClipChildren(false);
        darts_layout.setClipToPadding(false);
        LayoutParams darts_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(darts_layout, darts_params);
        //一共需要显示几镖
        int dart_total_num = mGame.getGameRule().getDartNumInOneRound();
        //动画数组
        animatorArray = new ValueAnimator[dart_total_num];
        //飞镖view数组
        dartImages = new GifImageView[dart_total_num];
        //分数view数组
        scoreViews = new View[dart_total_num];
        for (int i = 0; i < dart_total_num; i++) {
            //属性动画
            animatorArray[i] = createAnimator();
            //飞镖child
            dartImages[i] = new GifImageView(getContext());
            LinearLayout.LayoutParams params_dart = new LinearLayout.LayoutParams(284, 0);
            params_dart.weight = 1;
            dartImages[i].setPadding(dartPadding, dartPadding, dartPadding, dartPadding);
            int dart_res = mGame.getGameRes().getDartsImg();
            if (dart_res != 0) dartImages[i].setImageResource(dart_res);
            dartImages[i].setTag(new Integer(0));
            darts_layout.addView(dartImages[i], params_dart);
            //分数child
            scoreViews[i] = mGame.getGameRes().inflateHitDartView(getContext(), score_layout);
        }
    }

    /**
     * 设置指定view的右外边距
     *
     * @param item   指定view
     * @param margin 右外边距
     */
    private void setMarginRight(View item, int margin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) item.getLayoutParams();
        params.rightMargin = margin;
        item.setLayoutParams(params);
    }

    /**
     * 刷新飞镖显示数量
     */
    public void refreshDarts(Group group) {
        Round current_round = group.getCurrentRound();
        int hit_num = current_round.getHitNum();
        for (int i = 0; i < dartImages.length; i++) {
            mGame.getGameRes().setHitDartViewData(group, current_round, scoreViews[i], i);
            if (i >= hit_num) {
                animatorArray[i].cancel();
                animatorArray[i].removeAllUpdateListeners();
                setMarginRight(dartImages[i], 0);
                scoreViews[i].setAlpha(0f);
                dartImages[i].setTag(new Integer(0));
            } else if ((Integer) dartImages[i].getTag() != -1) {
                dartImages[i].setTag(new Integer(-1));
                animatorArray[i].cancel();
                animatorArray[i].removeAllUpdateListeners();
                setMarginRight(dartImages[i], 0);
                final View item_dart = dartImages[i];
                final View item_score = scoreViews[i];
                animatorArray[i].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float percent = (float) animation.getAnimatedValue();
                        setMarginRight(item_dart, (int) (widthPixels * percent + 0.5f));
                        item_score.setAlpha(percent);
                    }
                });
                animatorArray[i].start();
            }
        }
    }
}
