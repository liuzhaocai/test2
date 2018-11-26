package com.hopen.darts.ui;


import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.BaseDialog;
import com.hopen.darts.base.C;
import com.hopen.darts.game.GameUtil;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.Player;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.BaseResponse;
import com.hopen.darts.networks.commons.NetWork;
import com.hopen.darts.networks.commons.netty.BaseNettyResponse;
import com.hopen.darts.networks.commons.netty.N;
import com.hopen.darts.networks.interfaces.OnErrorCallback;
import com.hopen.darts.networks.interfaces.OnNettyReceiveCallback;
import com.hopen.darts.networks.interfaces.OnSuccessCallback;
import com.hopen.darts.networks.netty.NettyUtil;
import com.hopen.darts.networks.request.CustomHitRequest;
import com.hopen.darts.networks.request.FileEquUploadRequest;
import com.hopen.darts.networks.response.CustomHitResponse;
import com.hopen.darts.networks.response.FileEquUploadResponse;
import com.hopen.darts.utils.AlertMessage.PushMessageDialog;
import com.hopen.darts.utils.Camera.CameraUtil;
import com.hopen.darts.utils.FileUtils;
import com.hopen.darts.utils.JumpIntent;
import com.hopen.darts.utils.PhoneUtil;
import com.hopen.darts.utils.SharePreUtil;
import com.hopen.darts.views.CustomFontTextView;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 开始游戏紧跟先攻预览页面
 */
public class TakePhotoActivity extends BaseActivity implements OnNettyReceiveCallback {

    @BindView(R.id.fl_take_photos)
    FrameLayout flTakePhotos;


    @BindView(R.id.ll_take_photo)
    RelativeLayout rlTakePhoto;
    @BindView(R.id.ll_right_player)
    LinearLayout llRightPlayer;
    @BindView(R.id.ll_left_player)
    LinearLayout llLeftPlayer;
    @BindView(R.id.rl_player_vs)
    RelativeLayout rlPlayerVs;


    @BindView(R.id.iv_delay_time)
    ImageView ivDelayTime;
    @BindView(R.id.ll_bottom_player)
    LinearLayout llBottomPlayer;
    @BindView(R.id.iv_normal_delay_time)
    ImageView ivNormalDelayTime;
    @BindView(R.id.rl_normal_take_photo)
    RelativeLayout rlNormalTakePhoto;
    @BindView(R.id.rl_player_normal)
    RelativeLayout rlPlayerNormal;
    @BindView(R.id.fl_normal_take_photos)
    FrameLayout flNormalTakePhotos;

    private GameType gameType = new GameType();
    private int currentIndex = 0;

    private int[] delay_seconds = {R.mipmap.second_0, R.mipmap.second_1, R.mipmap.second_2, R.mipmap.second_3, R.mipmap.second_4, R.mipmap.second_5};
    private volatile int delay_time = delay_seconds.length;
    private HashMap<String, Object> map = new HashMap<>();
    private int flag = 0;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_take_photo);
        ButterKnife.bind(this);
        if (GameUtil.getPlayingGame() == null) {
            finish();
            return;
        }
        List<Group> groupList = GameUtil.getPlayingGame().getOrderGroup();
        if (groupList.size() == 0) {
            return;
        }
        if (groupList.get(0).getPlayerList().size() == 1) {//普通模式
            gameType.type = 2;
            gameType.bottomCount = groupList.size();
            if (GameUtil.getPlayingGame().isOnline()) {
                gameType.bottomCount = 1;
            }
        } else {
            gameType.type = 1;
            gameType.leftCount = groupList.get(0).getPlayerList().size();
            gameType.rightCount = groupList.get(0).getPlayerList().size();
        }
        if (gameType.type == 1) {
            initPlayerVSFrame();
        } else if (gameType.type == 2) {
            initPlayerNormalFrame();
        }
    }

    @Override
    protected void initData() {
        NettyUtil.addReceiveMsgCall(this);
        File baseFile = new File(C.player.BasePlayerImagePath);
        FileUtils.deleteDirectory(baseFile);
        baseFile.mkdirs();
        flTakePhotos.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!CameraUtil.checkCameraHardware()) {
                    takePhotoError();
                } else {
                    if (gameType.type == 1) {
                        if (CameraUtil.getInstance(TakePhotoActivity.this).startPreview(flTakePhotos))
                            startVSDelayTime();
                        else
                            takePhotoError();
                    } else if (gameType.type == 2) {
                        if (CameraUtil.getInstance(TakePhotoActivity.this).startPreview(flNormalTakePhotos))
                            startNormalDelayTime();
                        else
                            takePhotoError();
                    }
                }
            }
        }, 2000);
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (delay_time == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (delay_time == 0) {
                        if (gameType.type == 1) {
                            startVSDelayTime();
                        } else if (gameType.type == 2) {
                            startNormalDelayTime();
                        }
                    }
                }
            }, 1600);
        }
    }

    /**
     * 拍照发生错误，拍照失败
     */
    private void takePhotoError() {
        PushMessageDialog.get()
                .text("未检测到可用摄像头")
                .autoDismiss(true)
                .dismissListener(new BaseDialog.OnDismissWithAnimEndListener() {
                    @Override
                    public void onDismissWithAnimEnd() {
                        takePhotoEnd(false);
                    }
                })
                .show(this);
    }

    /**
     * 拍照页面交互结束
     */
    private void takePhotoEnd(boolean isTakePhoto) {
        if (GameUtil.getPlayingGame().isOnline()) {
            int index = getIntent().getExtras().getInt("index");
            if (GameUtil.getPlayingGame().getAllPlayer().size() < 2) {
                return;
            }
            String head = GameUtil.getPlayingGame().getAllPlayer().get(index).getHead();
            if (isTakePhoto) {
                uploadFile(head);
            } else {
                sendSelfDeviceInfo(C.player.default_head, isTakePhoto);
            }
        } else {
            jumpToStartGame();
        }
    }

    /**
     * 跳转开始游戏界面
     */
    private void jumpToStartGame() {
        CameraUtil.getInstance(this).releaseCamera();
        JumpIntent.jump(this, GamePlayActivity.class, true);
    }

    /**
     * 开始对战模式倒计时
     */
    private void startVSDelayTime() {
        rlTakePhoto.setVisibility(View.VISIBLE);
        if (currentIndex < gameType.leftCount) {
            View view = llLeftPlayer.getChildAt(currentIndex);
            view.setVisibility(View.INVISIBLE);
        } else if (currentIndex >= gameType.leftCount && currentIndex < (gameType.leftCount + gameType.rightCount)) {
            View view = llRightPlayer.getChildAt(currentIndex - gameType.leftCount);
            view.setVisibility(View.INVISIBLE);
        }
        if (delay_time != 0) {
            ivDelayTime.setImageResource(delay_seconds[delay_time - 1]);
        }
        ivDelayTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (delay_time == 0) {
                    CameraUtil.getInstance(TakePhotoActivity.this).captruePath(new CameraUtil.CameraCallData() {
                        @Override
                        public void takePhoto(byte[] data) {
                            String playPath = C.player.BasePlayerImagePath + currentIndex + UUID.randomUUID() + ".jpg";
                            FileUtils.writeData(data, playPath);
                            List<Player> players = GameUtil.getPlayingGame().getAllPlayer();
                            if (players != null && players.size() > currentIndex) {
                                Player player = players.get(currentIndex);
                                player.setHead(playPath);
                            }
                            TakePhotoActivity.this.setPhotoView(playPath);
                            if (currentIndex >= gameType.leftCount + gameType.rightCount) {
                                rlTakePhoto.setVisibility(View.INVISIBLE);
                                takePhotoEnd(true);
                                return;
                            }
                            delay_time = delay_seconds.length;
                            startVSDelayTime();
                        }
                    });
                } else {
                    delay_time -= 1;
                    startVSDelayTime();
                }
            }
        }, 1000);
    }

    /**
     * 开始普通模式倒计时
     */
    private void startNormalDelayTime() {
        rlNormalTakePhoto.setVisibility(View.VISIBLE);
        if (currentIndex < gameType.bottomCount) {
            View view = llBottomPlayer.getChildAt(currentIndex);
            view.setVisibility(View.INVISIBLE);
        }
        if (delay_time != 0) {
            ivNormalDelayTime.setImageResource(delay_seconds[delay_time - 1]);
        }
        ivNormalDelayTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (delay_time == 0) {
                    // TODO: 2018/7/30  java.lang.NullPointerException
                    CameraUtil.getInstance(TakePhotoActivity.this).captruePath(new CameraUtil.CameraCallData() {
                        @Override
                        public void takePhoto(byte[] data) {
                            String playPath = C.player.BasePlayerImagePath + currentIndex + UUID.randomUUID() + ".jpg";
                            FileUtils.writeData(data, playPath);
                            TakePhotoActivity.this.setPhotoView(playPath);
                            List<Player> players = GameUtil.getPlayingGame().getAllPlayer();
                            if (GameUtil.getPlayingGame().isOnline()) {
                                int index = getIntent().getExtras().getInt("index");
                                if (GameUtil.getPlayingGame().getAllPlayer().size() < 2) {
                                    return;
                                }
                                Player player = players.get(index);
                                player.setHead(playPath);
                            } else {
                                if (players != null && players.size() >= currentIndex) {
                                    Player player = players.get(currentIndex - 1);
                                    player.setHead(playPath);
                                }
                            }
                            if (currentIndex >= gameType.bottomCount) {
                                rlNormalTakePhoto.setVisibility(View.INVISIBLE);
                                takePhotoEnd(true);
                                return;
                            }
                            delay_time = delay_seconds.length;
                            startNormalDelayTime();
                        }
                    });
                } else {
                    delay_time -= 1;
                    startNormalDelayTime();
                }
            }
        }, 1000);
    }


    private void setPhotoView(String path) {
        ImageView imageView = null;
        if (gameType.type == 1) {
            if (currentIndex < gameType.leftCount) {
                View view = llLeftPlayer.getChildAt(currentIndex);
                view.setVisibility(View.VISIBLE);
                imageView = view.findViewById(R.id.iv_photo);
            } else if (currentIndex >= gameType.leftCount && currentIndex < (gameType.leftCount + gameType.rightCount)) {
                View view = llRightPlayer.getChildAt(currentIndex - gameType.leftCount);
                view.setVisibility(View.VISIBLE);
                imageView = view.findViewById(R.id.iv_photo);
            } else {
                return;
            }
        } else if (gameType.type == 2) {
            View view = llBottomPlayer.getChildAt(currentIndex);
            view.setVisibility(View.VISIBLE);
            imageView = view.findViewById(R.id.iv_photo);
        }

        currentIndex++;
        imageView.setImageURI(Uri.parse(path));
    }

    /**
     * 初始化对战模式布局
     */
    private void initPlayerVSFrame() {
        rlPlayerVs.setVisibility(View.VISIBLE);
        rlTakePhoto.setVisibility(View.INVISIBLE);
        rlPlayerNormal.setVisibility(View.GONE);
        for (int i = 0; i < gameType.leftCount; i++) {
            View view = getViewFromId(R.layout.item_take_photo_person, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
            CustomFontTextView tv_player_name = (CustomFontTextView) view.findViewById(R.id.tv_player_name);
            tv_player_name.setText(C.player.common_name + (i + 1));
            llLeftPlayer.addView(view);
        }
        for (int i = 0; i < gameType.rightCount; i++) {
            View view = getViewFromId(R.layout.item_take_photo_person, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
            CustomFontTextView tv_player_name = (CustomFontTextView) view.findViewById(R.id.tv_player_name);
            tv_player_name.setText(C.player.common_name + (gameType.leftCount + i + 1));
            llRightPlayer.addView(view);
        }
    }

    /**
     * 初始化普通模式布局
     */
    private void initPlayerNormalFrame() {
        rlPlayerNormal.setVisibility(View.VISIBLE);
        rlNormalTakePhoto.setVisibility(View.INVISIBLE);
        rlPlayerVs.setVisibility(View.GONE);
        for (int i = 0; i < gameType.bottomCount; i++) {
            View view = getViewFromId(R.layout.item_take_photo_person, null);
            CustomFontTextView tv_player_name = (CustomFontTextView) view.findViewById(R.id.tv_player_name);
            tv_player_name.setText(C.player.common_name + (i + 1));
            if (GameUtil.getPlayingGame().isOnline()) {
                int index = getIntent().getExtras().getInt("index");
                tv_player_name.setText(C.player.common_name + (index + 1));
            }
            llBottomPlayer.addView(view);
        }
    }

    /**
     * 头像上传
     *
     * @param path
     */
    private void uploadFile(String path) {
        FileEquUploadRequest request = new FileEquUploadRequest(path);
        NetWork.request(this, request, new OnSuccessCallback() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                Logger.e(JSON.toJSONString(baseResponse));
                FileEquUploadResponse response = (FileEquUploadResponse) baseResponse;
                String url = response.getData().getFiles().get(0).getUrl();
                sendSelfDeviceInfo(C.network.img_url + url, true);
            }
        }, new OnErrorCallback() {
            @Override
            public void onError(BaseResponse baseResponse) {
                sendSelfDeviceInfo(C.player.default_head, true);
            }
        }, false);
    }

    /**
     * 上传设备数据
     *
     * @param icon
     * @param isTakePhoto
     */
    private void sendSelfDeviceInfo(String icon, Boolean isTakePhoto) {
        map = new HashMap<>();
        map.put("playIcon", icon);
        map.put("equno", PhoneUtil.readSNCode());
        map.put("isCamera", isTakePhoto);
        CustomHitRequest hitRequest = new CustomHitRequest(map);
        NettyUtil.sendRequest(hitRequest);
        SharePreUtil.getInstance().putString(C.key.last_take_photo_hit_request_id, hitRequest.getRequestId());
    }

    class GameType {
        int type;
        int leftCount;
        int rightCount;
        int bottomCount;
    }

    @Override
    public void receiveMsg(String msg) {
        BaseNettyResponse response = JSON.parseObject(msg, BaseNettyResponse.class);
        switch (response.isSuccess(N.hit)) {
            case success:
                String last_take_photo_hit_request_id = SharePreUtil.getInstance().getString(C.key.last_take_photo_hit_request_id);
                if (TextUtils.equals(last_take_photo_hit_request_id, response.getRequestId())) {
                    flag++;
                    if (flag == 2) {
                        jumpToStartGame();
                    }
                }
                break;
            case fail:
                CustomHitRequest hitRequest = new CustomHitRequest(map);
                NettyUtil.sendRequest(hitRequest);
                break;
        }
        switch (response.isSuccess(N.hitpush)) {
            case success:
                flag++;
                CustomHitResponse customHitResponse = JSON.parseObject(msg, CustomHitResponse.class);
                KDManager.getInstance().setOtherPlayer(customHitResponse.getData().getPlayerInfo());
                int index = getIntent().getExtras().getInt("index");
                if (GameUtil.getPlayingGame().getAllPlayer().size() < 2) {
                    return;
                }
                int tempIndex = 1;
                if (index == 1) {
                    tempIndex = 0;
                }
                GameUtil.getPlayingGame().getAllPlayer().get(tempIndex).setHead(customHitResponse.getData().getPlayerInfo().getPlayIcon());
                if (flag == 2) {
                    jumpToStartGame();
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
    }

}
