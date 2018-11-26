package com.hopen.darts.game.base;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hopen.darts.R;
import com.hopen.darts.views.CustomFontTextView;

/**
 * 游戏页面资源基类
 */

public abstract class GameRes<E extends BaseGame> {
    /**
     * 本资源对应的游戏
     */
    protected E mGame;
    /**
     * 游戏页面的自定义区域显示控制类
     */
    protected GameCustomArea<E> gameCustomArea;

    protected GameRes(E game) {
        mGame = game;
    }

    /**
     * 获取页面整体背景
     *
     * @return 页面整体背景
     */
    public abstract int getPageBg();

    /**
     * 获取顶部logo图片
     *
     * @return 顶部logo图片
     */
    public abstract int getTopLogo();

    /**
     * 获取玩家信息框背景
     *
     * @return 玩家信息框背景
     */
    public abstract int getPlayerBg();

    /**
     * 获取玩家信息部分一个条目的背景
     *
     * @return 玩家信息部分一个条目的背景
     */
    public abstract int getPlayerInfoItemBg();

    /**
     * 获取玩家头像部分背景
     *
     * @return 玩家头像部分背景
     */
    public abstract int getPlayerHeadBg();

    /**
     * 获取玩家姓名框背景
     *
     * @return 玩家姓名框背景
     */
    public int getPlayerNameBg() {
        return 0;
    }

    /**
     * 获取回合信息框背景
     *
     * @return 回合信息框背景
     */
    public abstract int getRoundBg();

    /**
     * 获取回合部分一个条目的背景
     *
     * @return 回合部分一个条目的背景
     */
    public abstract int getRoundItemBg();

    /**
     * 获取回合部分一个条目的自定义view
     * 默认返回null，需要返回自定义view的游戏需要重写此方法
     *
     * @param context        上下文
     * @param group          阵营(队伍)信息
     * @param round          回合信息
     * @param round_position 回合序列
     * @param convertView    当前条目的view
     * @param parent         当前条目的父view
     * @return 回合部分一个条目的自定义view
     */
    public View getRoundCustomItemView(Context context, Group group, Round round, int round_position, View convertView, ViewGroup parent) {
        return null;
    }

    /**
     * 获取摄像头实况部分框体背景
     *
     * @return 摄像头实况部分框体背景
     */
    public abstract int getLiveBg();

    /**
     * 获取摄像头实况部分一个条目的背景
     *
     * @return 摄像头实况部分一个条目的背景
     */
    public abstract int getLiveItemBg();

    /**
     * 获取飞镖部分框体背景
     *
     * @return 飞镖部分框体背景
     */
    public abstract int getDartsBg();

    /**
     * 获取飞镖部分飞镖的图片
     *
     * @return 飞镖部分飞镖的图片
     */
    public int getDartsImg() {
        return R.drawable.darts_img_default;
    }

    /**
     * 获取已经投掷飞镖后显示的view
     *
     * @param context 上下文
     * @return 要显示的view
     */
    public View inflateHitDartView(Context context, LinearLayout parent) {
        CustomFontTextView view = new CustomFontTextView(context);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(Color.WHITE);
        view.setTextSize(30);
        view.setAlpha(0f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        params.weight = 1;
        parent.addView(view, params);
        return view;
    }

    /**
     * 设置已经投掷飞镖后view显示的信息
     *
     * @param view     对应的view
     * @param position 第几镖
     */
    public void setHitDartViewData(Group group, Round current_round, View view, int position) {
        CustomFontTextView text_view = (CustomFontTextView) view;
        HitIJ hit_ij = current_round.getHitIJ(position);
        String text = "";
        if (hit_ij != null) {
            if (hit_ij.i() == 0) {//零分区
                text = "Miss";
            } else if (hit_ij.i() == 21) {//牛眼
                if (hit_ij.j() == 0)//内红心
                    text = "D-BULL";
                else//外红心
                    text = "S-BULL";
            } else {//i等于1-20，对应1-20分
                switch (hit_ij.j()) {
                    case 0://单倍区
                    case 2:
                        text = "SINGLE " + hit_ij.i();
                        break;
                    case 1://三倍区
                        text = "TRIPLE " + hit_ij.i();
                        break;
                    case 3://双倍区
                        text = "DOUBLE " + hit_ij.i();
                        break;
                }
            }
        }
        text_view.setText(text, TextView.BufferType.NORMAL);
        //text_view.setText(current_round.getHitScore(position) + "", TextView.BufferType.NORMAL);
    }

    /**
     * 获取底部玩家栏目框体背景
     *
     * @return 底部玩家栏目框体背景
     */
    public abstract int getBottomBg();

    /**
     * @return 单人队伍模式下，单人背景框是否动态修改
     */
    public boolean isBottomItemSingleDynamicBg() {
        return false;
    }

    /**
     * 根据position获取底部玩家栏目指定位置的玩家队伍框体背景（单人队伍模式），默认返回固定值，有需要修改的游戏重写此方法
     *
     * @param position 指定位置
     * @return 指定位置的玩家队伍框体背景（单人队伍模式）
     */
    public int getBottomItemSingleBg(int position) {
        return R.mipmap.game_play_bottom_player_single_bg;
    }

    /**
     * 根据position获取底部玩家栏目，指定位置的玩家队伍框体背景
     *
     * @param position 指定位置
     * @return 指定位置的玩家队伍框体背景
     */
    public abstract int getBottomItemGroupBg(int position);

    /**
     * 创建游戏页面的自定义区域显示控制类
     * 默认没有自定义区域显示，显示自定义区域需要重写此方法，创建并返回一个实现好的对象
     *
     * @return 游戏页面的自定义区域显示控制类
     */
    protected GameCustomArea<E> createCustomArea(FrameLayout parent) {
        return null;
    }

    /**
     * 刷新显示
     */
    protected void onRefresh() {
        if (gameCustomArea != null) gameCustomArea.onRefresh();
    }

    /**
     * 初始化游戏页面的自定义区域显示
     */
    public void initCustomArea(FrameLayout parent) {
        gameCustomArea = createCustomArea(parent);
    }
}
