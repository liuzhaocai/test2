package com.hopen.darts.ui;


import android.graphics.Bitmap;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.BaseDialog;
import com.hopen.darts.base.C;
import com.hopen.darts.game.GameUtil;
import com.hopen.darts.game.base.Player;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.netty.BaseNettyResponse;
import com.hopen.darts.networks.commons.netty.N;
import com.hopen.darts.networks.interfaces.OnNettyReceiveCallback;
import com.hopen.darts.networks.netty.NettyUtil;
import com.hopen.darts.networks.request.OrderStartRequest;
import com.hopen.darts.networks.response.PaySuccessResponse;
import com.hopen.darts.utils.AlertMessage.PushMessageDialog;
import com.hopen.darts.utils.JumpIntent;
import com.hopen.darts.utils.QRCodeUtil;
import com.hopen.darts.utils.SharePreUtil;
import com.hopen.darts.utils.UtilCopyData;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreparePayActivity extends BaseActivity implements OnNettyReceiveCallback{

    @BindView(R.id.iv_qr)
    ImageView ivQr;
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.iv_done)
    ImageView ivDone;
    private Bitmap bitmap;
    private boolean paySuccess = false;
    private String order_no;
    private PaySuccessResponse paySuccessResponse;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_prepare_pay);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        String url = getIntent().getExtras().getString("url");
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = QRCodeUtil.createQRBitmap(url, 260, 260, null);
        if (bitmap != null) {
            ivQr.setImageBitmap(bitmap);
        }
        NettyUtil.addReceiveMsgCall(this);
    }

    private void startGame() {
        if (GameUtil.getPlayingGame().isOnline()) {
            JumpIntent.jump(PreparePayActivity.this, VideoVSWaitActivity.class, true);
        } else {
            GameUtil.playGame(null);
            List<Player> players = GameUtil.getPlayingGame().getAllPlayer();
            for (int i = 0; i < players.size(); i++) {
                String path = C.app.file_path_img + "/icon_head_default.png";
                if (new File(path).exists() == false) {
                    try {
                        UtilCopyData.copyBigDataToSD(getApplicationContext(), path, "icon_head_default.png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Player player = players.get(i);
                player.setHead(path);
            }
            JumpIntent.jump(PreparePayActivity.this, TakePhotoActivity.class, true);
        }
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initListener() {

    }
    @Override
    public void receiveMsg(String msg) {
        BaseNettyResponse response = JSON.parseObject(msg, BaseNettyResponse.class);
        switch (response.isSuccess(N.OrderPay)) {
            case success:
                paySuccessResponse = JSON.parseObject(msg, PaySuccessResponse.class);
                order_no = paySuccessResponse.getData().getOrder_no();
                KDManager.getInstance().setPayerDetail(paySuccessResponse.getData());
                PushMessageDialog.get()
                        .text("订单支付成功！\n您可以开始游戏了")
                        .autoDismiss(true)
                        .dismissListener(new BaseDialog.OnDismissWithAnimEndListener() {
                            @Override
                            public void onDismissWithAnimEnd() {
                                OrderStartRequest orderStartRequest = new OrderStartRequest(order_no);
                                NettyUtil.sendRequest(orderStartRequest);
                                SharePreUtil.getInstance().putString(C.key.last_order_start_request_id, orderStartRequest.getRequestId());
                            }
                        })
                        .show(PreparePayActivity.this);
                paySuccess = true;
                break;
            case fail:
                showMsg(response.getMsg());
                break;
        }
        switch (response.isSuccess(N.OrderStart)) {
            case success:
                String last_order_start_request_id = SharePreUtil.getInstance().getString(C.key.last_order_start_request_id);
                if (last_order_start_request_id.equals(response.getRequestId())) {
                    startGame();
                } else {
                    showMsg("request不一致");
                }
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
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    @Override
    public void onKeyConfirm() {
        super.onKeyConfirm();
        if (!paySuccess) {
            showMsg("请扫码支付订单");
        }
    }

    @Override
    public void onKeyCancel() {
        super.onKeyCancel();
        onBackPressed();
    }
}
