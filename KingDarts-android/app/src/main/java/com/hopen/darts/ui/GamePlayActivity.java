package com.hopen.darts.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.BaseDialog;
import com.hopen.darts.base.C;
import com.hopen.darts.game.GameUtil;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.HitIJ;
import com.hopen.darts.game.base.Player;
import com.hopen.darts.game.base.Round;
import com.hopen.darts.game.base.interfaces.OnGameOverListener;
import com.hopen.darts.game.base.interfaces.OnHitOverListener;
import com.hopen.darts.game.base.interfaces.OnRetreatOverListener;
import com.hopen.darts.game.base.interfaces.OnRoundOverListener;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.netty.BaseNettyResponse;
import com.hopen.darts.networks.commons.netty.N;
import com.hopen.darts.networks.interfaces.OnNettyReceiveCallback;
import com.hopen.darts.networks.mapper.Step;
import com.hopen.darts.networks.netty.NettyUtil;
import com.hopen.darts.networks.request.HitRequest;
import com.hopen.darts.networks.response.CustomHitResponse;
import com.hopen.darts.networks.response.HitPushResponse;
import com.hopen.darts.ui.adpter.GamePlayRoundAdapter;
import com.hopen.darts.utils.AlertMessage.PushMessageDialog;
import com.hopen.darts.utils.Camera.CameraUtil;
import com.hopen.darts.utils.JumpIntent;
import com.hopen.darts.utils.PhoneUtil;
import com.hopen.darts.utils.SerialPort.KeyCode;
import com.hopen.darts.utils.StringFormatUtil;
import com.hopen.darts.utils.live.PushConfig;
import com.hopen.darts.views.CustomFontTextView;
import com.hopen.darts.views.DartsView;
import com.hopen.darts.views.GamePlayBottomView;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 玩游戏页面
 */
public class GamePlayActivity extends BaseActivity implements OnHitOverListener, OnRetreatOverListener, OnRoundOverListener, OnGameOverListener, OnNettyReceiveCallback {
    {
        isShowSpreadStarAnim = false;//不显示扩散星星动画
    }

    @BindView(R.id.custom_area_fl)
    FrameLayout customAreaFl;
    @BindView(R.id.top_logo_iv)
    ImageView topLogoIv;
    @BindView(R.id.player_head_iv)
    ImageView playerHeadIv;
    @BindView(R.id.player_name_tv)
    CustomFontTextView playerNameTv;
    @BindView(R.id.player_head_bg)
    View playerHeadBg;
    @BindView(R.id.player_info_item1_tv)
    CustomFontTextView playerInfoItem1Tv;
    @BindView(R.id.player_info_item2_tv)
    CustomFontTextView playerInfoItem2Tv;
    @BindView(R.id.player_info_item3_tv)
    CustomFontTextView playerInfoItem3Tv;
    @BindView(R.id.player_ll)
    LinearLayout playerLl;
    @BindView(R.id.round_num_tv)
    CustomFontTextView roundNumTv;
    @BindView(R.id.round_lv)
    ListView roundLv;
    @BindView(R.id.round_ll)
    LinearLayout roundLl;
    @BindView(R.id.live_fl)
    FrameLayout liveFl;
    @BindView(R.id.live_rl)
    RelativeLayout liveRl;
    @BindView(R.id.darts_dv)
    DartsView dartsDv;
    @BindView(R.id.players_bv)
    GamePlayBottomView playersBv;
    @BindView(R.id.online_over_player_head_iv)
    ImageView onlineOverPlayerHeadIv;
    @BindView(R.id.online_over_player_name_tv)
    CustomFontTextView onlineOverPlayerNameTv;
    @BindView(R.id.online_over_player_ll)
    LinearLayout onlineOverPlayerLl;
    @BindView(R.id.online_over_rl)
    RelativeLayout onlineOverRl;
    @BindView(R.id.page_rl)
    RelativeLayout pageRl;

    BaseGame mGame;//当前游戏
    Group currentGroup;//当前掷镖队伍
    Player currentPlayer;//当前掷镖玩家
    GamePlayRoundAdapter gamePlayRoundAdapter;//回合数据adapter
    PushMessageDialog switchPlayerDialog;//切换玩家提示窗
    PushMessageDialog waitSwitchDialog;//联机模式等待切换玩家提示窗
    Thread onlineSendThread, onlineReadThread;//联机模式的发送和接收线程
    LinkedList<HitRequest> waitSendHitRequest;//待发送的数据链表(掷镖和确认键操作)
    int sendPosition;//发送数据递增position，保证发送操作的顺序
    int readPosition;//接收数据递增position，保证接收操作的顺序
    SparseArray<Step> readStep;//已经接接收过的步骤，防止重复接收数据
    boolean rejectOperation = false;//页面是否拒绝操作
    boolean gameOver = false;//游戏是否结束
    boolean pageOver = false;//当前页面是否正在关闭，避免onGameOver回调多次造成的问题
    PushConfig pushConfig;//联机模式直播入口

    @Override
    protected void initView() {
        setContentView(R.layout.activity_game_play);
        ButterKnife.bind(this);
        if (GameUtil.getPlayingGame() == null) {
            finish();
            return;
        }
        mGame = GameUtil.getPlayingGame();
        initPage();
        initOnline();
    }

    @Override
    protected void initData() {
        setCurrentPlayer();
        PushMessageDialog.get()
                .text("回合一")
                .autoDismiss(true)
                .dismissListener(new BaseDialog.OnDismissWithAnimEndListener() {
                    @Override
                    public void onDismissWithAnimEnd() {
                        currentPlayerHint();
                    }
                })
                .show(this);
    }

    @Override
    protected void initControl() {
    }

    @Override
    protected void initListener() {
        mGame.addOnHitOverListener(this);
        mGame.addOnRetreatOverListener(this);
        mGame.addOnRoundOverListener(this);
        mGame.addOnGameOverListener(this);
    }

    /**
     * 初始化页面
     */
    private void initPage() {
        GameRes game_res = mGame.getGameRes();
        pageRl.setBackgroundResource(game_res.getPageBg());
        topLogoIv.setImageResource(game_res.getTopLogo());
        playerLl.setBackgroundResource(game_res.getPlayerBg());
        playerHeadBg.setBackgroundResource(game_res.getPlayerHeadBg());
        playerInfoItem1Tv.setBackgroundResource(game_res.getPlayerInfoItemBg());
        playerInfoItem2Tv.setBackgroundResource(game_res.getPlayerInfoItemBg());
        playerInfoItem3Tv.setBackgroundResource(game_res.getPlayerInfoItemBg());
        roundLl.setBackgroundResource(game_res.getRoundBg());
        gamePlayRoundAdapter = new GamePlayRoundAdapter(this, mGame);
        roundLv.setAdapter(gamePlayRoundAdapter);
        try {
            if (mGame.isOnline()) {
                pushConfig = new PushConfig(this);
                pushConfig.setPushUrl(PhoneUtil.readSNCode());
                CustomHitResponse.DataBean.PlayerInfoBean other = KDManager.getInstance().getOtherPlayer();
                pushConfig.setPlayUrl(other.getEquno(), other.isCamera(), other.getPlayIcon());
                pushConfig.startPlay(liveFl);
                pushConfig.startPush();
                mGame.getGameRule().live(pushConfig);
            } else {
                if (CameraUtil.checkCameraHardware()) {
                    CameraUtil.getInstance(this).startPreview(liveFl);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        liveRl.setBackgroundResource(game_res.getLiveBg());
        dartsDv.setBackgroundResource(game_res.getDartsBg());
        dartsDv.withGame(mGame);
        playersBv.setBackgroundResource(game_res.getBottomBg());
        game_res.initCustomArea(customAreaFl);
        playersBv.withGame(mGame);
    }

    /**
     * 设置当前玩家数据
     */
    private void setCurrentPlayer() {
        currentGroup = mGame.nextCurrentGroup();
        currentPlayer = currentGroup.getCurrentPlayer();
        currentPlayer.loadHead(playerHeadIv);
        playerNameTv.setText(currentPlayer.getName());
        playerInfoItem1Tv.setText(currentPlayer.getName());
        playersBv.showCurrentPlayerToTop(currentGroup);
        refreshData();
    }

    /**
     * 提示该那个玩家掷镖
     * 本方法会在{@link #switchPlayerDialog}关闭后调用，而在玩家打完本回合后，也有退镖的情况存在，
     * 在退镖后，{@link #switchPlayerDialog}也会消失，消失后会自动回调本方法，所以要在本方法中判断，
     * 当前正在掷镖的玩家的回合是否是空的，如果是空的，说明是切换玩家事件使{@link #switchPlayerDialog}关闭
     * 否则则是退镖事件使{@link #switchPlayerDialog}关闭，不提示“请投镖”
     */
    private void currentPlayerHint() {
        if (currentGroup.getCurrentRound().isEmpty()) {
            PushMessageDialog.get()
                    .text(currentPlayer.getName() + "请投镖")
                    .autoDismiss(true)
                    .show(this);
        }
    }

    /**
     * 刷新显示数据
     */
    private void refreshData() {
        if (currentGroup == null || currentPlayer == null) return;
        if (currentPlayer.getPPR() != -1 && currentPlayer.getPPD() != -1) {
            playerInfoItem2Tv.setText("PPR:" + StringFormatUtil.doubleRounding(currentPlayer.getPPR()));
            playerInfoItem3Tv.setText("PPD:" + StringFormatUtil.doubleRounding(currentPlayer.getPPD()));
        } else if (currentPlayer.getMPR() != -1) {
            playerInfoItem2Tv.setText("MPR:" + StringFormatUtil.doubleRounding(currentPlayer.getMPR()));
            playerInfoItem3Tv.setVisibility(View.GONE);
        }
        gamePlayRoundAdapter.refresh(currentGroup);
        //将回合列表滚动到指定位置，使列表中能够显示出当前回合
        final int current_round_position = currentGroup.getCurrentRoundPosition();
        if (current_round_position > 0) {//只有当前回合不是第一回合的时候才操作
            //取得position在ListView中对应的view
            View current_view = roundLv.getChildAt(current_round_position);
            //这个view为空(说明此view完全没有显示出来)，或者bottom大于ListView的高(说明没有显示完全)
            if (current_view == null || current_view.getBottom() > roundLv.getHeight()) {
                roundLv.post(new Runnable() {
                    @Override
                    public void run() {
                        //向上滑动：当前position - 能显示出的最后的position + 1 个位置
                        roundLv.setSelection(roundLv.getFirstVisiblePosition() +
                                current_round_position - roundLv.getLastVisiblePosition() + 1);
                    }
                });
            }
        }
        dartsDv.refreshDarts(currentGroup);
        playersBv.refreshScore();
    }

    /**
     * 执行一次掷镖操作
     * <p>特别注意：在联机模式中，添加到请求队列这步操作要在本机执行掷镖操作
     * （即：{@link Group#oneHit(HitIJ)}）之前进行！！！</p>
     * <p>否则在在执行{@link Group#oneHit(HitIJ)}方法的过程中，
     * 如果游戏结束，且没有可执行的游戏事件队列的话
     * 在其执行完毕前就会调用{@link #onGameOver()}，
     * 导致本次操作还没有添加到网络请求队列，就开始进行游戏结束逻辑，导致游戏异常结束</p>
     *
     * @param hit_ij 掷镖数据
     */
    private void currentGroupOneHit(HitIJ hit_ij) {
        //是本机在操作，且在联机模式下，将掷镖操作添加到请求队列中
        if (mGame.isLocalTurn() && mGame.isOnline()) {
            waitSendHitRequest.add(new HitRequest(sendPosition++, hit_ij.i(), hit_ij.j()));
        }
        currentGroup.oneHit(hit_ij);
    }

    /**
     * 掷镖操作结束时回调
     *
     * @param hit_ij    本镖投掷的数据
     * @param effective 本次数据是否有效
     */
    @Override
    public void onHitOver(HitIJ hit_ij, int hit_score, boolean effective) {
        if (effective) {
            refreshData();//如果有效刷新数据显示
        }
    }

    /**
     * 退镖操作结束时回调
     *
     * @param hit_ij    本镖退镖的数据
     * @param effective 本次操作是否有效
     */
    @Override
    public void onRetreatOver(HitIJ hit_ij, int hit_score, boolean effective) {
        if (effective) {
            refreshData();//如果有效刷新数据显示
            if (switchPlayerDialog != null) {
                switchPlayerDialog.dismiss();
                switchPlayerDialog = null;
            }
        }
    }

    /**
     * 回合结束时回调
     *
     * @param round     结束的回合数据
     * @param game_over 游戏是否已经结束
     */
    @Override
    public void onRoundOver(Round round, boolean game_over) {
        //游戏没有结束，仅仅是本会和结束，执行相关提示
        if (!game_over) {
            boolean local_turn = mGame.isLocalTurn();
            if (local_turn && switchPlayerDialog == null) {//轮到本机操作(非联机模式或者联机模式下轮到本机)
                switchPlayerDialog = PushMessageDialog.get();
                switchPlayerDialog.text("请取镖\n并按下 \"决定\" 键。")
                        .dismissListener(new BaseDialog.OnDismissWithAnimEndListener() {
                            @Override
                            public void onDismissWithAnimEnd() {
                                setCurrentPlayer();
                                currentPlayerHint();
                            }
                        })
                        .autoDismiss(false);
                switchPlayerDialog.show(this);
            } else if (waitSwitchDialog == null && !local_turn) {
                waitSwitchDialog = PushMessageDialog.get()
                        .text("等待对手取镖")
                        .dismissListener(new BaseDialog.OnDismissWithAnimEndListener() {
                            @Override
                            public void onDismissWithAnimEnd() {
                                setCurrentPlayer();
                                currentPlayerHint();
                            }
                        })
                        .autoDismiss(false);
                waitSwitchDialog.show(this);
            }
        }
    }

    /**
     * 游戏结束时回调
     */
    @Override
    public void onGameOver() {
        //游戏结束
        gameOver = true;
        rejectOperation = true;
        //联机模式，并且是本机在操作时，需要在所有数据发送成功后跳转结果页面
        //所以只有在非联机模式下，或者不是本机回合的情况下，或者所有数据都已发送完毕，才能直接在此跳转
        if (!mGame.isOnline() || !mGame.isLocalTurn() || waitSendHitRequest.size() == 0)
            gameOver();
    }

    /**
     * 靶盘接收到数据时回调
     *
     * @param data 靶盘采集到的数据
     */
    @Override
    public void onDartboardCallback(long data) {
        if (rejectOperation || gameOver) return;//游戏已经结束或者拒绝操作，不再响应任何操作
        //未轮到本机，不响应任何操作(非联机模式下，轮到本机一直是true)
        if (!mGame.isLocalTurn()) return;
        currentGroupOneHit(GameRule.analysisCollectedData(data));
    }

    /**
     * 按下决定键
     */
    @Override
    public void onKeyConfirm() {
        if (rejectOperation || gameOver) return;//游戏已经结束或者拒绝操作，不再响应任何操作
        //未轮到本机，不响应任何操作(非联机模式下，轮到本机一直是true)
        if (!mGame.isLocalTurn()) return;
        if (mGame.getGameRule().haveRunningEvent()) {
            //当游戏有正在运行的事件时，决定键是立即结束当前事件
            mGame.getGameRule().overCurrentEvent();
            if (mGame.isOnline())//联机模式下，将结束动画操作添加到请求队列中
                waitSendHitRequest.add(new HitRequest(sendPosition++, Step.Decision.OVER_EVENT));
        } else if (currentGroup.getCurrentRound().isOver()) {
            //当当前回合结束时，决定键是切换玩家
            if (switchPlayerDialog != null) {
                switchPlayerDialog.dismiss();
                switchPlayerDialog = null;
            }
            if (mGame.isOnline()) {//联机模式下，将切换玩家操作添加到请求队列中
                waitSendHitRequest.add(new HitRequest(sendPosition, Step.Decision.SWITCH_PLAYER));
                //本次操作应是此次队列中最后一个请求，将序列置为0
                sendPosition = 0;
            }
        } else {
            //当当前回合未结束时，决定键是执行一次投掷0分区操作
            onDartboardCallback(KeyCode.getScoreAreaCollectDataTable()[0][0]);
        }
    }

    /**
     * 按下取消键
     */
    @Override
    public void onKeyCancel() {
        if (rejectOperation || gameOver) return;//游戏已经结束或者拒绝操作，不再响应任何操作
        if (mGame.getGameRule().haveRunningEvent()) {//当游戏有正在运行的事件时，取消键是立即结束当前事件
            if (mGame.isLocalTurn()) {//仅在轮到本机时才能结束游戏事件
                //轮到本机(非联机模式下，轮到本机一直是true)
                mGame.getGameRule().overCurrentEvent();
                if (mGame.isOnline())//联机模式下，将结束动画操作添加到请求队列中
                    waitSendHitRequest.add(new HitRequest(sendPosition++, Step.Decision.OVER_EVENT));
            }//else do nothing
        } else {//没有游戏事件在执行时，取消键跳转设置页面，不区分是否轮到本机
            //跳转游戏设置页面(退镖操作和立即结束，联机模式下只有立即结束)
            JumpIntent.jump(this, GameSettingActivity.class,
                    mGame.isOnline() ? C.code.request.game_setting_online : C.code.request.game_setting_local);
        }
    }

    /**
     * 初始化网络对战
     */
    private void initOnline() {
        if (mGame.isOnline()) {
            sendPosition = 0;
            readPosition = 0;
            waitSendHitRequest = new LinkedList<>();
            readStep = new SparseArray<>();
            NettyUtil.addReceiveMsgCall(this);
            startSend();
            startRead();
        }
    }

    /**
     * 开始联机模式的发送线程
     */
    private void startSend() {
        onlineSendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (waitSendHitRequest.size() > 0) {
                            waitSendHitRequest.get(0).send();
                        }
                        Thread.sleep(100);
                        if (pageOver) break;//页面已经关闭，结束线程
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    startSend();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        onlineSendThread.start();
    }

    /**
     * 开始联机模式的接收线程
     */
    private void startRead() {
        onlineReadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        final Step step = readStep.get(readPosition);
                        if (step != null) {
                            if (step.isOverEvent()) {//结束游戏Event
                                readStep.remove(readPosition++);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mGame.getGameRule().haveRunningEvent())
                                            mGame.getGameRule().overCurrentEvent();
                                    }
                                });
                            } else if (step.isSwitchPlayer()) {//切换玩家
                                readStep.clear();
                                readPosition = 0;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (waitSwitchDialog != null) {
                                            waitSwitchDialog.dismiss();
                                            waitSwitchDialog = null;
                                        }
                                    }
                                });
                            } else if (step.isOverGame()) {//对手提前结束了游戏
                                readStep.clear();
                                readPosition = 0;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //当前本机正在操作，收到对手的立即结束请求
                                        if (mGame.isLocalTurn()) {
                                            activeOverOnlineGame();
                                        } else {
                                            onGameOver();
                                        }
                                    }
                                });
                            } else {//掷镖操作
                                readStep.remove(readPosition++);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        currentGroup.oneHit(step.toHitIJ());
                                    }
                                });
                            }
                        }
                        if (pageOver) break;//页面已经关闭，结束线程
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        onlineReadThread.start();
    }

    /**
     * 联机模式下，收到对手发来的信息时回调
     *
     * @param msg 对手发送的数据(json)
     */
    @Override
    public void receiveMsg(String msg) {
        BaseNettyResponse base_response = JSON.parseObject(msg, BaseNettyResponse.class);
        switch (base_response.getType()) {
            case N.hit:
                if (waitSendHitRequest.size() == 0) break;
                if (base_response.isSuccess() && TextUtils.equals(base_response.getRequestId(), waitSendHitRequest.get(0).getRequestId())) {
                    waitSendHitRequest.removeFirst();
                    if (waitSendHitRequest.size() == 0 && gameOver) //游戏已经结束，并且所有数据都已发送成功，跳转游戏结果页面
                        gameOver();
                } else {
                    waitSendHitRequest.get(0).sendAble();
                }
                break;
            case N.hitpush:
                if (base_response.isSuccess()) {
                    HitPushResponse response = JSON.parseObject(msg, HitPushResponse.class);
                    Step step = response.getData().getStep();
                    if (step == null) break;
                    readStep.put(step.getPosition(), step);
                }
                break;
        }
    }

    /**
     * 从设置页面返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case C.code.request.game_setting_local://单机模式
                switch (resultCode) {
                    case C.code.result.game_setting_retreat:
                        //退镖操作
                        currentGroup.retreatHit();
                        break;
                    case C.code.result.game_setting_over:
                        //立即结束操作，主动调用onGameOver回调，开始走正常结束游戏逻辑
                        onGameOver();
                        break;
                }
                break;
            case C.code.request.game_setting_online://联机模式
                if (resultCode == C.code.result.game_setting_over) {
                    activeOverOnlineGame();
                }
                break;
        }
    }

    /**
     * 一方选择主动结束联机游戏
     * <p>联机模式的立即结束是这样一个逻辑：</p>
     * <p>第一种情况：由正在操作的A方发起，A方的程序将页面设置为结束且不可操作状态，并紧接着将结束游戏操作添加到请求队列中，
     * A方的{@link #onlineSendThread}在不断的发送队列里的请求，待成功发送所有请求后，按照正常游戏结束的逻辑结束A方程序中的游戏。
     * B方不断的接收请求并将接受到的数据放入队列中，B方程序的{@link #onlineReadThread}在不断处理队列里收到的数据。
     * 处理顺序会按照A方发送的顺序来处理，一直处理到最后一步的结束游戏的操作。按照正常游戏结束的逻辑结束B方程序中的游戏。</p>
     * <p>第二种情况：由此时不在操作状态的B方发起，此时B方的程序仅将程序置为不可操作状态，并紧接着将结束游戏操作添加到请求队列中
     * B方此时是不在操作状态的，正常逻辑下，消息接收的回调每收到一个发送成功请求都会移除一个发送请求，
     * 并且判断如果请求队列为空且游戏结束的话，会进入结束游戏关闭页面的逻辑，但是此时操作权不在B方，所以B方不可直接结束游戏，
     * 所以B方仅将页面置为不可操作状态，并将结束请求通知给A方，等待A方处理，A方在收到B方发来的结束请求后，
     * 开始进行第一种情况的逻辑来结束游戏，所以在{@link #onlineReadThread}中收到结束操作时，需要判断此时是否是本机在操作
     * 如果是的话，将页面设置为结束且不可操作状态，再将结束请求添加到请求队列中，模拟第一种情况的操作，如果不是，则就是第一种情况。</p>
     */
    private synchronized void activeOverOnlineGame() {
        //联机模式下，将结束游戏操作添加到请求队列中
        waitSendHitRequest.add(new HitRequest(sendPosition, Step.Decision.OVER_GAME));
        //本次操作应是此次队列中最后一个请求，将序列置为0
        sendPosition = 0;
        if (mGame.isLocalTurn()) {
            //游戏结束
            onGameOver();
        } else {
            //将页面置为不可操作状态
            rejectOperation = true;
        }
        //防止联机数据传递超时或者失败导致的页面卡死
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long begin = System.currentTimeMillis();
                    while (!pageOver) {
                        Thread.sleep(100);
                        long now = System.currentTimeMillis();
                        if (now - begin > 20000) {//20秒后还没有结束则强制结束游戏
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //游戏结束
                                    gameOver = true;
                                    rejectOperation = true;
                                    gameOver();
                                }
                            });
                            break;
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 游戏结束，关闭页面，并且跳转游戏结果页
     */
    private synchronized void gameOver() {
        if (!pageOver) {//防止多次跳转
            pageOver = true;
            if (mGame.isOnline()) {
                //关闭切换玩家提示框
                if (switchPlayerDialog != null) {
                    switchPlayerDialog.setOnDismissWithAnimEndListener(null);
                    switchPlayerDialog.dismiss();
                }
                if (waitSwitchDialog != null) {
                    waitSwitchDialog.setOnDismissWithAnimEndListener(null);
                    waitSwitchDialog.dismiss();
                }
                if (mGame.getGameRule().isLocalWin()) {
                    onlineOverPlayerLl.setBackgroundResource(R.mipmap.icon_game_result_red);
                    onlineOverRl.setBackgroundResource(R.mipmap.game_play_online_winner);
                } else {
                    onlineOverPlayerLl.setBackgroundResource(R.mipmap.icon_game_result_grey);
                    onlineOverRl.setBackgroundResource(R.mipmap.game_play_online_loser);
                }
                Player player = mGame.getLocalGroup().getCurrentPlayer();
                player.loadHead(onlineOverPlayerHeadIv);
                onlineOverPlayerNameTv.setText(player.getName());
                onlineOverRl.setVisibility(View.VISIBLE);
                onlineOverRl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        JumpIntent.jump(GamePlayActivity.this, GameResultActivity.class, true);
                    }
                }, 3000);
            } else {
                JumpIntent.jump(this, GameResultActivity.class, true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (mGame.isOnline()) {
                if (pushConfig != null) pushConfig.release();
                NettyUtil.removeReceiveMsgCall(this);
            } else {
                CameraUtil.getInstance(this).releaseCamera();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
