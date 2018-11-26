package com.hopen.darts.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hopen.darts.R;
import com.hopen.darts.base.interfaces.OnActivityResultListener;
import com.hopen.darts.base.interfaces.OnActivityResumeListener;
import com.hopen.darts.networks.netty.NettyUtil;
import com.hopen.darts.ui.AppSettingActivity;
import com.hopen.darts.ui.WaitLoadingActivity;
import com.hopen.darts.utils.AlertMessage.PushMessageDialog;
import com.hopen.darts.utils.DaemonUtil;
import com.hopen.darts.utils.JumpIntent;
import com.hopen.darts.utils.SerialPort.KeyCode;
import com.hopen.darts.utils.SerialPort.KeyEventUtil;
import com.hopen.darts.utils.Sound.SoundEnum;
import com.hopen.darts.utils.Sound.SoundPlayUtils;
import com.hopen.darts.utils.ZZTestFileUtil;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by bjlx on 2017/7/6.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected boolean isVisible = false;//本页面是否在显示
    private OnActivityResumeListener onActivityResumeListener;//resume回调
    private OnActivityResultListener onActivityResultListener;//onActivityResult回调

    protected boolean tFinishing = false;//本页面是否调用了finish方法
    protected boolean wifiSignalAble = true;//是否显示wifi信号
    protected boolean isShowSpreadStarAnim = true;//是否显示扩散星星动画
    public boolean isFixedTimeNoOperationToShowAdvert = false;//是否在一定时间无操作后显示广告页面
    private RelativeLayout baseParentContent;//BaseActivity增加的显示内容的父容器view
    protected ImageView wifiSignal;//wifi信号显示控件
    protected GifImageView spreadStar;//扩散星星显示控件

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏和底部虚拟按键
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(flag);
        }
        if (this instanceof WaitLoadingActivity) {
            final PushMessageDialog temp = PushMessageDialog.get();
            temp.text("正在拷贝资源文件").autoDismiss(false);
            temp.show(this);
            if (!tFinishing)
                initView();
            ZZTestFileUtil.check(new ZZTestFileUtil.Callback() {
                @Override
                public void onOver(final boolean isEligible) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            temp.dismiss();
                            if (isEligible) {
                                if (!tFinishing)
                                    initData();
                                if (!tFinishing)
                                    initControl();
                                if (!tFinishing)
                                    initListener();
                            } else {
                                PushMessageDialog.get()
                                        .text(C.app.no_sn + "，请检查仪器是否正常")
                                        .autoDismiss(false)
                                        .show(BaseActivity.this);
                            }
                        }
                    });
                }
            });
        } else {
            if (!tFinishing)
                initView();
            if (!tFinishing)
                initData();
            if (!tFinishing)
                initControl();
            if (!tFinishing)
                initListener();
        }
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initControl();

    protected abstract void initListener();

    @Override
    public void setContentView(int layoutResID) {
        if (wifiSignalAble || isShowSpreadStarAnim) {
            baseParentContent = new RelativeLayout(this);
            View content = View.inflate(this, layoutResID, null);
            RelativeLayout.LayoutParams content_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            baseParentContent.addView(content, content_params);
            if (wifiSignalAble) {
                ImageView wifi_bg = new ImageView(this);
                wifi_bg.setImageResource(R.mipmap.icon_wifi_bg);
                RelativeLayout.LayoutParams wifi_bg_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                wifi_bg_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                baseParentContent.addView(wifi_bg, wifi_bg_params);
                wifiSignal = new ImageView(this);
                setWifiSignalLevel(3);
                RelativeLayout.LayoutParams wifi_signal_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                wifi_signal_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                baseParentContent.addView(wifiSignal, wifi_signal_params);
            }
            if (isShowSpreadStarAnim) {
                baseParentContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            spreadStar = new GifImageView(BaseActivity.this);
                            spreadStar.setImageResource(R.drawable.star_bg);
                            ((GifDrawable) spreadStar.getDrawable()).setSpeed(1.5f);
                            RelativeLayout.LayoutParams star_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            baseParentContent.addView(spreadStar, star_params);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }, 100);
            }
            super.setContentView(baseParentContent);
        } else super.setContentView(layoutResID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (isShowSpreadStarAnim && spreadStar != null)
                ((GifDrawable) spreadStar.getDrawable()).start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        isVisible = true;
        BaseApplication.setBaseActivity(this);
        if (onActivityResumeListener != null)
            onActivityResumeListener.onActivityResume();
    }

    @Override
    protected void onPause() {
        try {
            if (isShowSpreadStarAnim && spreadStar != null)
                ((GifDrawable) spreadStar.getDrawable()).stop();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (onActivityResultListener != null)
            onActivityResultListener.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        tFinishing = true;
        super.finish();
    }

    /**
     * 获取BaseActivity增加的显示内容的父容器view
     *
     * @return BaseActivity增加的显示内容的父容器view
     */
    protected RelativeLayout getBaseParentContent() {
        return baseParentContent;
    }

    /**
     * 判断mainactivity是否处于栈顶
     *
     * @return true在栈顶false不在栈顶
     */
    public synchronized boolean isVisible() {
        return isVisible;
    }

    /**
     * 设置resume回调
     *
     * @param onActivityResumeListener resume回调
     */
    public void setOnActivityResumeListener(OnActivityResumeListener onActivityResumeListener) {
        this.onActivityResumeListener = onActivityResumeListener;
    }

    /**
     * 设置onActivityResult回调
     *
     * @param onActivityResultListener onActivityResult回调
     */
    public void setOnActivityResultListener(OnActivityResultListener onActivityResultListener) {
        this.onActivityResultListener = onActivityResultListener;
    }

    /**
     * 加载xml布局
     *
     * @param layout
     * @param parent
     * @return
     */
    public View getViewFromId(@LayoutRes int layout, ViewGroup parent) {
        return LayoutInflater.from(this).inflate(layout, parent, false);
    }

    /**
     * 设置无线信号强度
     *
     * @param level 强度级别
     */
    public void setWifiSignalLevel(@IntRange(from = 0, to = 3) int level) {
        if (wifiSignal == null) return;
        switch (level) {
            case 0:
                wifiSignal.setImageResource(R.mipmap.icon_wifi_bg);
                break;
            case 1:
                wifiSignal.setImageResource(R.mipmap.icon_wifi_c);
                break;
            case 2:
                wifiSignal.setImageResource(R.mipmap.icon_wifi_b);
                break;
            case 3:
            default:
                wifiSignal.setImageResource(R.mipmap.icon_wifi_a);
                break;
        }
    }

    /**
     * 提示信息
     *
     * @param msg
     */
    public void showMsg(String msg) {
        PushMessageDialog.get()
                .text(msg)
                .autoDismiss(true)
                .show(this);
    }

    /* 按钮事件 begin */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        onKeyOperate(-1);
        if (C.app.debug) {
            return KeyEventUtil.onKeyDown(this, keyCode);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 按键、靶盘操作回调，只要有操作就会回调此方法
     *
     * @param instruction 操作指令标识
     */
    public void onKeyOperate(long instruction) {
        DaemonUtil.resetLastOperationTime();
        if (instruction == KeyCode.KEY_SETTING_DOWN) {
            SoundPlayUtils.play(SoundEnum.BTN_CLICK);
            onKeySetting();
            return;
        }
        if (!NettyUtil.isNettyLogin()) return;
        if (instruction == KeyCode.KEY_LEFT_DOWN) {
            SoundPlayUtils.play(SoundEnum.BTN_CLICK);
            onKeyLeft();
        } else if (instruction == KeyCode.KEY_TOP_DOWN) {
            SoundPlayUtils.play(SoundEnum.BTN_CLICK);
            onKeyTop();
        } else if (instruction == KeyCode.KEY_RIGHT_DOWN) {
            SoundPlayUtils.play(SoundEnum.BTN_CLICK);
            onKeyRight();
        } else if (instruction == KeyCode.KEY_BOTTOM_DOWN) {
            SoundPlayUtils.play(SoundEnum.BTN_CLICK);
            onKeyBottom();
        } else if (instruction == KeyCode.KEY_CANCEL_DOWN) {
            SoundPlayUtils.play(SoundEnum.BTN_CLICK);
            onKeyCancel();
        } else if (instruction == KeyCode.KEY_CONFIRM_DOWN) {
            SoundPlayUtils.play(SoundEnum.BTN_CLICK);
            onKeyConfirm();
        } else if (KeyCode.isScoreAreaCollectData(instruction)) {
            onDartboardCallback(instruction);
        }
    }

    public void onKeyLeft() {
    }

    public void onKeyTop() {
    }

    public void onKeyRight() {
    }

    public void onKeyBottom() {
    }

    public void onKeyCancel() {
    }

    public void onKeyConfirm() {
    }

    public void onKeySetting() {
        JumpIntent.jump(this, AppSettingActivity.class);
    }

    public void onDartboardCallback(long data) {

    }
    /* 按钮事件 end */

}
