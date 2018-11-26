package com.hopen.darts.ui;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.C;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.netty.N;
import com.hopen.darts.networks.interfaces.OnNettyReceiveCallback;
import com.hopen.darts.networks.netty.NettyUtil;
import com.hopen.darts.networks.response.LoginResponse;
import com.hopen.darts.ui.presenter.DownLoadResourcePresenter;
import com.hopen.darts.ui.presenter.WaitLoadingPresenter;
import com.hopen.darts.utils.AlertMessage.PushMessageDialog;
import com.hopen.darts.utils.IPUtil;
import com.hopen.darts.utils.JumpIntent;
import com.hopen.darts.utils.PhoneUtil;
import com.hopen.darts.views.CustomFontTextView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * loading页面，一般供首次进入使用进行下载更新同步资源需要完成的任务
 * <p>
 * 1.打开设备信息记录           20%
 * 2.文件路径初始化与数据库拷贝   20%
 * 3.长连接登陆                20%
 * 4.资源版本校验              20%
 * 5.资源下载与更新            20%
 */
public class WaitLoadingActivity extends BaseActivity implements OnNettyReceiveCallback {

    @BindView(R.id.pb_loading)
    public ProgressBar pbLoading;
    @BindView(R.id.tv_loading)
    CustomFontTextView tvLoading;
    @BindView(R.id.tv_loading_done)
    CustomFontTextView tvLoadingDone;
    @BindView(R.id.tv_err_msg)
    CustomFontTextView tvErrorView;

    public int pro = 0;
    private String[] dot = new String[]{"   ", ".  ", ".. ", "..."};
    private String loading = "正在加载";
    private int dotNum = 0;
    private int LoadingTime = 1000;
    private WaitLoadingPresenter presenter;
    private final MyHandler mHandler = new MyHandler(this);
    private DownLoadResourcePresenter downLoadResourcePresenter;
    private PushMessageDialog pushMessageDialog;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_wait_loading);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        KDManager manager = KDManager.getInstance();
        if (TextUtils.equals(PhoneUtil.readSNCode(), C.app.no_sn)) {
            pushMessageDialog = PushMessageDialog.get();
            pushMessageDialog.text(C.app.no_sn + "，请检查仪器是否正常")
                    .autoDismiss(false)
                    .show(this);
            return;
        }
        manager.setNetFlag(IPUtil.isNetworkAvailable(this));
        if (!manager.isNetFlag()) {
            pushMessageDialog = PushMessageDialog.get();
            pushMessageDialog.text(C.netty.ConnectErrorMsg)
                    .autoDismiss(false).show(this);
            setWifiSignalLevel(0);
            return;
        }
        setLoading();
        downLoadResourcePresenter = new DownLoadResourcePresenter(this, 81, 19) {
            @Override
            public void upDataProgress(int pro, String msg) {
                WaitLoadingActivity.this.upDataProgress(pro, msg);
                loading = msg;
            }

            @Override
            public void upDataError(String msg) {
                WaitLoadingActivity.this.upDataError(msg);
            }
        };
        presenter = new WaitLoadingPresenter(this) {
            @Override
            public void upDataProgress(int pro, String msg) {
                WaitLoadingActivity.this.upDataProgress(pro, msg);
            }

            @Override
            public void upDataError(String msg) {
                WaitLoadingActivity.this.upDataError(msg);
            }
        };
        //监听是否登陆成功
        upDataProgress(5, "正在登录");
        NettyUtil.addReceiveMsgCall(this);
        NettyUtil.open();
    }

    public void upDataProgress(int pro, String msg) {
        WaitLoadingActivity.this.upDataProgress(pro);
        loading = msg;
        switch (pro) {
            case 20:
                presenter.readDeviceInfo();
                break;
            case 40:
                presenter.initFilePath();
                break;
            case 60:
                presenter.checkAppVersion();
                break;
            case 80:
                downLoadResourcePresenter.pullResourceFilesVersion();
                break;
            default:
                break;
        }
    }

    private void upDataError(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvErrorView.setVisibility(View.VISIBLE);
                tvErrorView.setText(msg);
                tvLoading.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initListener() {

    }

    private void updateProgress() {
        pbLoading.setProgress(pro);
        if (pro == pbLoading.getMax()) {
            tvLoading.setVisibility(View.INVISIBLE);
            dotNum = 4;
            tvLoading.removeCallbacks(runnable);
            tvLoadingDone.setVisibility(View.VISIBLE);
            tvLoadingDone.postDelayed(new Runnable() {
                @Override
                public void run() {
                    JumpIntent.jump(WaitLoadingActivity.this, MainActivity.class, true);
                }

            }, 2000);
        }
    }

    private void upDataProgress(final int currentTime) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //子线程循环间隔消息
                    while (pro < currentTime) {
                        pro += 1;
                        Message msg = new Message();
                        mHandler.sendMessage(msg);
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setLoading() {
        switch (dotNum) {
            case 0:
                dotNum = 1;
                break;
            case 1:
                dotNum = 2;
                break;
            case 2:
                dotNum = 3;
                break;
            case 3:
                dotNum = 0;
                break;
            default:
                dotNum = 4;
                return;
        }
        tvLoading.setText(loading + dot[dotNum]);
        tvLoading.postDelayed(runnable, LoadingTime);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setLoading();
        }
    };

    private static class MyHandler extends Handler {
        private final WeakReference<WaitLoadingActivity> mActivity;

        public MyHandler(WaitLoadingActivity activity) {
            mActivity = new WeakReference<WaitLoadingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WaitLoadingActivity activity = mActivity.get();
            if (activity != null) {
                activity.updateProgress();
            }
        }
    }

    @Override
    public void receiveMsg(final String response) {
        if (response.contains("\"type\":\"" + N.Login + "\"")) {//登录接口回文
            LoginResponse loginResponse = JSON.parseObject(response, LoginResponse.class);
            switch (loginResponse.getCode()) {
                case C.netty.code_success:
                    upDataProgress(20, "登录成功");
                    break;
                case "401"://登录失败
                    upDataError(loginResponse.getMsg());
                    break;
                case "403"://服务器重启中
                    upDataProgress(10, loginResponse.getMsg());
                    break;
                case "402"://设备已登录
                default://其他未知情况
                    upDataProgress(10, (TextUtils.isEmpty(loginResponse.getMsg()) ?
                            ("登录失败(code" + loginResponse.getCode() + ")")
                            : loginResponse.getMsg()) + "，正在重试");
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        NettyUtil.removeReceiveMsgCall(this);
        super.onDestroy();
        tvLoading.removeCallbacks(runnable);
    }
}
