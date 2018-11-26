package com.hopen.darts.ui;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.C;
import com.hopen.darts.game.GameUtil;
import com.hopen.darts.ui.adpter.GameModelAdapter;
import com.hopen.darts.utils.JumpIntent;
import com.hopen.darts.views.CustomFontTextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameSettingActivity extends BaseActivity {

    @BindView(R.id.game_series_ryc)
    RecyclerView gameSeriesRyc;
    @BindView(R.id.tv_game_info_desc)
    CustomFontTextView tvGameInfoDesc;
    @BindView(R.id.iv_direction)
    ImageView ivDirection;
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.iv_done)
    ImageView ivDone;
    @BindView(R.id.ll_bottom_view)
    LinearLayout llBottomView;

    private int requestCode;
    private List<String> list;
    private GameModelAdapter adapter;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_game_setting);
        ButterKnife.bind(this);
        requestCode = getIntent().getIntExtra(JumpIntent.REQUEST_CODE, -1);
        if (requestCode == C.code.request.game_setting_online) {
            list = Arrays.asList("立即结束");
        } else {
            list = Arrays.asList("退镖操作", "立即结束");
        }
        adapter = new GameModelAdapter(this, R.layout.item_main_game_series);
        gameSeriesRyc.setAdapter(adapter);
        gameSeriesRyc.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged(list, 0);
    }

    @Override
    protected void initData() {
        tvGameInfoDesc.setText(GameUtil.getPlayingGame().getGameDescribe());
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initListener() {
    }


    public void onKeyLeft() {
        onBackPressed();
    }

    public void onKeyRight() {
        switch (adapter.selected) {
            case 0:
                switch (requestCode) {
                    case C.code.request.game_setting_local:
                        setResult(C.code.result.game_setting_retreat);
                        break;
                    case C.code.request.game_setting_online:
                        setResult(C.code.result.game_setting_over);
                        break;
                }
                finish();
                break;
            case 1:
                setResult(C.code.result.game_setting_over);
                finish();
                break;
        }
    }

    public void onKeyTop() {
        if (adapter.selected > 0) {
            adapter.notifyDataSetChanged(adapter.selected - 1);
        }
    }

    public void onKeyBottom() {
        if ((adapter.selected + 1) < adapter.getItemCount()) {
            adapter.notifyDataSetChanged(adapter.selected + 1);
        }
    }

    public void onKeyCancel() {
        onKeyLeft();
    }

    public void onKeyConfirm() {
        onKeyRight();
    }
}
