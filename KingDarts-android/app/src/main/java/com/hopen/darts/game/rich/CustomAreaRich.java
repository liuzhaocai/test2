package com.hopen.darts.game.rich;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.response.PaySuccessResponse;
import com.hopen.darts.utils.CountDown;
import com.hopen.darts.views.CustomFontTextView;
import com.hopen.darts.views.GameRichHealthView;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomas on 2018/7/2.
 */

public class CustomAreaRich extends GameCustomArea<GameRich> {

    @BindView(R.id.boss_iv)
    ImageView bossIv;
    @BindView(R.id.boss_head_iv)
    ImageView bossHeadIv;
    @BindView(R.id.health_hv)
    GameRichHealthView healthHv;
    @BindView(R.id.refresh_time_tv)
    CustomFontTextView refreshTimeTv;

    PaySuccessResponse.DataBean order;

    protected CustomAreaRich(FrameLayout parent, GameRich game) {
        super(parent, game);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.view_game_play_custom_area_rich);
        ButterKnife.bind(this, parentLayout);
    }

    @Override
    protected void initData() {
        order = KDManager.getInstance().getPayerDetail();
        int percent = (int) (order.getLifeRate() + 0.5f);
        if (percent <= 0) percent = 1;
        else if (100 < percent) percent = 100;
        ViewGroup.LayoutParams params = bossIv.getLayoutParams();
        params.height = (int) (830f * ((percent + 100f) / 200f));
        bossIv.setLayoutParams(params);
        healthHv.setPercent(percent);
    }

    @Override
    protected void initControl() {
        CountDown.with(refreshTimeTv).millisLength(order.getRemainTime() * 1000L).interval(1000)
                .notDefaultCountDownFinish()
                .countDownTickHandler(new CountDown.OnCountDownTickHandler() {
                    @Override
                    public boolean onTick(long millisUntilFinished) {
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));//设置时区，暂时不确定是否必须
                        String hms = formatter.format(millisUntilFinished);
                        refreshTimeTv.setText("剩余时间：" + hms);
                        return true;
                    }
                }).simpleStart();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onRefresh() {
    }
}
