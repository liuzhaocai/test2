package com.hopen.darts.game.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by thomas on 2018/6/28.
 */

public abstract class GameCustomArea<E extends BaseGame> {
    /**
     * 上下文
     */
    protected Context mContext;
    /**
     * 自定义区域要显示的view
     */
    protected FrameLayout parentLayout;
    /**
     * 本自定义区域对应的游戏
     */
    protected E mGame;

    protected GameCustomArea(FrameLayout parent, E game) {
        mContext = parent.getContext();
        parentLayout = parent;
        mGame = game;
        initView();
        initData();
        initControl();
        initListener();
    }

    public void setContentView(@LayoutRes int layout_res_id) {
        View view = View.inflate(mContext, layout_res_id, null);
        setContentView(view);
    }

    public void setContentView(View content_view) {
        parentLayout.addView(content_view);
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return parentLayout.findViewById(id);
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initControl();

    protected abstract void initListener();

    protected abstract void onRefresh();
}
