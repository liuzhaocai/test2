package com.hopen.darts.ui.presenter;

import android.view.View;
import android.widget.LinearLayout;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.ui.MainActivity;
import com.hopen.darts.utils.AnimationUtil;
import com.hopen.darts.views.CustomFontTextView;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter {

    public List<GameMode> list = new ArrayList<>();

    private static int ModelTag = 1000;
    private MainActivity mainActivity;
    private LinearLayout modelLll;
    public int currentModel = 2;

    public MainPresenter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /***********************************************  游戏模式相关  *************************************************/

    /**
     * 初始化选择模式视图
     *
     * @param linearLayout
     */
    public void initModelSwitch(LinearLayout linearLayout) {
        this.modelLll = linearLayout;
        for (int i = 0; i < 5; i++) {
            View view = null;
            switch (Math.abs(i - 2)) {
                case 0:
                    view = mainActivity.getViewFromId(R.layout.adapter_selected_model, null);
                    ;
                    break;
                case 1:
                    view = mainActivity.getViewFromId(R.layout.adapter_selected_model_two, null);
                    break;
                case 2:
                    view = mainActivity.getViewFromId(R.layout.adapter_selected_model_three, null);
                    break;
            }
            view.setVisibility(View.INVISIBLE);
            view.setTag(ModelTag + i);
            linearLayout.addView(view);
        }
    }

    /**
     * 设置当前焦点
     *
     * @param list
     * @param index
     */
    public void showModelIndex(List<GameMode> list, int index) {
        for (int i = 0; i < 5; i++) {
            if (index - 2 + i < 0) {
                showInfo(null, i);
            } else if (index - 2 + i >= list.size()) {
                showInfo(null, i);
            } else {
                showInfo(list.get(index - 2 + i), i);
            }
        }
    }

    /**
     * 展示某一个条目信息
     *
     * @param gameMode
     * @param index
     */
    private void showInfo(GameMode gameMode, int index) {
        View view = modelLll.findViewWithTag(ModelTag + index);
        if (gameMode == null) {
            view.setVisibility(View.INVISIBLE);
            return;
        } else {
            view.setVisibility(View.VISIBLE);
        }
        CustomFontTextView customFontTextView = (CustomFontTextView) view.findViewById(R.id.tv_game_model);
        customFontTextView.setText(gameMode.getName());
    }

    /***********************************************  二级菜单相关  *************************************************/

    /**
     * 隐藏二级菜单
     */
    public void hiddenSonItem() {
        AnimationUtil.performwidthhAnim(mainActivity.flLeftSonGamesView, mainActivity.leftSonMenuWidth, 0, 150, new AnimationUtil.Call() {
            @Override
            public void callBack(float valueAnimator) {
                mainActivity.llMainDescTextContainer.getLayoutParams().width = (int) (950.f + (1093.f - 950.f) * valueAnimator);
                if (valueAnimator == 1) {
                    mainActivity.adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 展示二级菜单
     */
    public void showSonItem() {
        AnimationUtil.performwidthhAnim(mainActivity.flLeftSonGamesView, 0, mainActivity.leftSonMenuWidth, 150, new AnimationUtil.Call() {
            @Override
            public void callBack(float valueAnimator) {
                mainActivity.llMainDescTextContainer.getLayoutParams().width = (int) (1093.f - (1093.f - 950.f) * valueAnimator);
            }
        });

    }


}
