package com.hopen.darts.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.BaseDialog;
import com.hopen.darts.game.GameUtil;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.BaseResponse;
import com.hopen.darts.networks.commons.NetWork;
import com.hopen.darts.networks.commons.netty.BaseNettyResponse;
import com.hopen.darts.networks.commons.netty.N;
import com.hopen.darts.networks.interfaces.OnErrorCallback;
import com.hopen.darts.networks.interfaces.OnSuccessCallback;
import com.hopen.darts.networks.request.GameCancelRequest;
import com.hopen.darts.networks.response.GameCancelResponse;
import com.hopen.darts.networks.response.StartPushResponse;
import com.hopen.darts.utils.AlertMessage.PopGifDialog;
import com.hopen.darts.utils.AlertMessage.PushMessageDialog;
import com.hopen.darts.utils.JumpIntent;
import com.hopen.darts.networks.interfaces.OnNettyReceiveCallback;
import com.hopen.darts.networks.netty.NettyUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoVSWaitActivity extends BaseActivity implements OnNettyReceiveCallback {

    @BindView(R.id.iv_direction)
    ImageView ivDirection;
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.iv_done)
    ImageView ivDone;
    @BindView(R.id.ll_bottom_view)
    LinearLayout llBottomView;
    private StartPushResponse startPushResponse;
    private boolean isRequesting = false;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_video_vswait);
        ButterKnife.bind(this);
        ivCancel.setBackground(getResources().getDrawable(R.mipmap.icon_net_back));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initListener() {
        NettyUtil.addReceiveMsgCall(this);
    }


    private void playGif() {
        int res = 0;
        final Bundle bundle = new Bundle();
        if (TextUtils.equals(startPushResponse.getData().getPlayerName(), "player1")) {
            res = R.drawable.icon_player1;
            bundle.putInt("index", 0);
        } else {
            res = R.drawable.icon_player2;
            bundle.putInt("index", 1);
        }
        PopGifDialog.get()
                .res(res)
                .dismissListener(new BaseDialog.OnDismissWithAnimEndListener() {
                    @Override
                    public void onDismissWithAnimEnd() {
                        GameUtil.onlineGame(startPushResponse.getData().getPlayerName(), startPushResponse.getData().getTime());
                        JumpIntent.jump(VideoVSWaitActivity.this, TakePhotoActivity.class, true, bundle);
                    }
                })
                .show(this);
    }

    private void onGameCancel() {
        if (isRequesting) return;
        isRequesting = true;
        GameCancelRequest request = new GameCancelRequest(KDManager.getInstance().getOrderDetail().getOrder_no());
        NetWork.request(this, request, new OnSuccessCallback() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                isRequesting = false;
                GameCancelResponse resourceResponse = (GameCancelResponse) baseResponse;
                if (resourceResponse.isSuccess()) {
                    onBackPressed();
                } else {
                    showMsg(resourceResponse.getMsg());
                }
            }
        }, new OnErrorCallback() {
            @Override
            public void onError(BaseResponse baseResponse) {
                VideoVSWaitActivity.this.showMsg(baseResponse.getMsg());
            }
        }, false);
    }

    @Override
    public void onKeyConfirm() {
        super.onKeyConfirm();
    }

    @Override
    public void onKeyCancel() {
        super.onKeyCancel();
        onGameCancel();
    }

    @Override
    public void receiveMsg(String msg) {
        BaseNettyResponse response = JSON.parseObject(msg, BaseNettyResponse.class);
        switch (response.isSuccess(N.StartPush)) {
            case success:
                startPushResponse = JSON.parseObject(msg, StartPushResponse.class);
                PushMessageDialog.get()
                        .text("匹配成功，开始进入游戏")
                        .autoDismiss(true)
                        .dismissListener(new BaseDialog.OnDismissWithAnimEndListener() {
                            @Override
                            public void onDismissWithAnimEnd() {
                                playGif();
                            }
                        })
                        .show(VideoVSWaitActivity.this);
                break;
            case fail:
                showMsg(response.getMsg());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        NettyUtil.removeReceiveMsgCall(this);
        super.onDestroy();
    }
}
