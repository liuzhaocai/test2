package com.hopen.darts.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.ui.adpter.GameModelAdapter;
import com.hopen.darts.utils.JumpIntent;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * app设置界面
 */
public class AppSettingActivity extends BaseActivity{

    @BindView(R.id.game_series_ryc)
    RecyclerView gameSeriesRyc;
    @BindView(R.id.ll_bottom_view)
    LinearLayout llBottomView;
    private GameModelAdapter adapter;
    private List<String> list = Arrays.asList(new String[]{"WIFI 设置","关于"});

    @Override
    protected void initView() {
        setContentView(R.layout.activity_app_setting);
        ButterKnife.bind(this);
        adapter = new GameModelAdapter(this);
        gameSeriesRyc.setAdapter(adapter);
        gameSeriesRyc.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged(list,0);
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


    public void onKeyLeft() {

    }

    public void onKeyRight() {

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
        onBackPressed();
    }

    public void onKeyConfirm() {
        switch (adapter.selected){
            case 0:
                Intent it = new Intent();
                ComponentName cn = new ComponentName("com.android.settings","com.android.settings.wifi.WifiSettings");
                it.setComponent(cn);
                startActivity(it);
                break;
            case 1:
                JumpIntent.jump(this, AboutActivity.class);
                break;
        }
    }

}
