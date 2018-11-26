package com.hopen.darts.game.basecommon;

import android.widget.FrameLayout;

import com.hopen.darts.R;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.views.CustomFontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DefaultCustomArea<T extends BaseGame> extends GameCustomArea<T> {

    @BindView(R.id.score_tv)
    CustomFontTextView scoreTv;

    public DefaultCustomArea(FrameLayout parent, T game) {
        super(parent, game);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.view_game_play_custom_area_default);
        ButterKnife.bind(this, parentLayout);
    }

    @Override
    protected void initData() {
        scoreTv.setText(mGame.getCurrentGroup().getScore() + "");
    }

    @Override
    protected void initControl() {
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void onRefresh() {
        scoreTv.setText(mGame.getCurrentGroup().getScore() + "");
    }
}