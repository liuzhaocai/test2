package com.hopen.darts.ui;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.utils.IPUtil;
import com.hopen.darts.utils.PhoneUtil;
import com.hopen.darts.views.CustomFontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_system_info)
    CustomFontTextView tvSystemInfo;
    @BindView(R.id.iv_direction)
    ImageView ivDirection;
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.iv_done)
    ImageView ivDone;
    @BindView(R.id.ll_bottom_view)
    LinearLayout llBottomView;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        ivDirection.setVisibility(View.INVISIBLE);
        ivDone.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void initData() {
        String version_code = PhoneUtil.getVersionCode(this) + "";
        String sn = PhoneUtil.readSNCode();
        String ip = IPUtil.getIPAddress(this);
        String info = "版本号：" + version_code + "\n"
                + "机器码：" + sn + "\n"
                + "IP：" + ip;
        tvSystemInfo.setText(info);
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onKeyCancel() {
        super.onKeyCancel();
        onBackPressed();
    }

}
