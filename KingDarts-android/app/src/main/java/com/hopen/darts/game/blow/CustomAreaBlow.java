package com.hopen.darts.game.blow;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.views.CustomFontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomAreaBlow extends GameCustomArea<GameBlow> {

    @BindView(R.id.burst_tv)
    CustomFontTextView burstTv;
    @BindView(R.id.balloon_iv)
    ImageView balloonIv;
    @BindView(R.id.now_total_score_tv)
    TextView nowTotalScoreTv;
    @BindView(R.id.burst_test_tv)
    TextView burstTestTv;

    RuleBlow mRule;
    int minBallSize = 380, maxBallSize = 700;

    protected CustomAreaBlow(FrameLayout parent, GameBlow game) {
        super(parent, game);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.view_game_play_custom_area_blow);
        ButterKnife.bind(this, parentLayout);
        mRule = (RuleBlow) mGame.getGameRule();
        burstTv.setText("爆炸范围：" + mRule.burstMin + "-" + mRule.burstMax);
        nowTotalScoreTv.setText("当前总分：" + mRule.totalScore);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initControl() {
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onRefresh() {
//        burstTestTv.setText("当前爆炸临界值：" + mRule.burstNow + "分");
        nowTotalScoreTv.setText("当前总分：" + mRule.totalScore);
        ViewGroup.LayoutParams params = balloonIv.getLayoutParams();
        int size = (maxBallSize - minBallSize) * mRule.totalScore / mRule.burstMax + minBallSize;
        params.height = size;
        params.width = size;
        balloonIv.setLayoutParams(params);
    }
}