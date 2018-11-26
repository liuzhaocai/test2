package com.hopen.darts.ui;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.C;
import com.hopen.darts.game.GameUtil;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.Player;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.BaseResponse;
import com.hopen.darts.networks.commons.NetWork;
import com.hopen.darts.networks.interfaces.OnErrorCallback;
import com.hopen.darts.networks.interfaces.OnSuccessCallback;
import com.hopen.darts.networks.request.OrderResultsRequest;
import com.hopen.darts.utils.AlertMessage.PushMessageDialog;
import com.hopen.darts.utils.JumpIntent;
import com.hopen.darts.utils.SerialPort.KeyEventUtil;
import com.hopen.darts.views.CustomFontTextView;
import com.hopen.darts.views.TextViewBorder;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@RequiresApi(api = Build.VERSION_CODES.M)
public class GameResultActivity extends BaseActivity {

    @BindView(R.id.ll_left_player)
    LinearLayout llLeftPlayer;
    @BindView(R.id.ll_right_player)
    LinearLayout llRightPlayer;
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.iv_done)
    ImageView ivDone;
    @BindView(R.id.tv_rest_money)
    CustomFontTextView tvRestMoney;
    @BindView(R.id.ll_center)
    LinearLayout llCenter;
    @BindView(R.id.fl_rank)
    FrameLayout flRank;
    @BindView(R.id.fl_vs)
    FrameLayout flVs;
    @BindView(R.id.ll_pp)
    LinearLayout llPp;
    @BindView(R.id.ll_mp)
    LinearLayout llMp;
    private ArrayList<Data> datas = new ArrayList<>();
    private static final String[] num_lower = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private PushMessageDialog pushMessage;
    private boolean isCharterFlight = false;
    private boolean isMpr = false;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_game_result);
        ButterKnife.bind(this);
        List<Group> groupList = GameUtil.getPlayingGame().getOrderGroup();
        if (groupList.size() == 0) {
            return;
        }
        if (groupList.get(0).getPlayerList().get(0).getMPR() != -1) {
            isMpr = true;
            llPp.setVisibility(View.GONE);
            llMp.setVisibility(View.VISIBLE);
        } else {
            isMpr = false;
            llMp.setVisibility(View.GONE);
            llPp.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {
        uploadGameResults();
        List<Group> groupList = GameUtil.getPlayingGame().getOrderGroup();
        if (groupList.size() == 0) {
            return;
        }
        if (groupList.get(0).getPlayerList().size() == 1) {//普通模式
            Data data = new Data();
            data.playGameResults = new ArrayList<>();
            for (int i = 0; i < groupList.size(); i++) {
                Player player = groupList.get(i).getPlayerList().get(0);
                PlayGameResult playGameResult = new PlayGameResult();
                playGameResult.personName = player.getName();
                playGameResult.number = player.getHitNum();
                playGameResult.ppd = player.getPPD();
                playGameResult.ppr = player.getPPR();
                playGameResult.mpr = player.getMPR();
                playGameResult.totalScore = player.getScore();
                playGameResult.headUrl = player.getHead();
                data.playGameResults.add(playGameResult);
            }
            data.result = 1;
            datas.add(data);
        } else {//对战模式
            int j = 1;
            for (Group group : groupList) {
                Data data = new Data();
                data.playGameResults = new ArrayList<>();
                for (int i = 0; i < group.getPlayerList().size(); i++) {
                    Player player = group.getPlayerList().get(i);
                    PlayGameResult playGameResult = new PlayGameResult();
                    playGameResult.personName = player.getName();
                    playGameResult.number = player.getHitNum();
                    playGameResult.ppd = player.getPPD();
                    playGameResult.ppr = player.getPPR();
                    playGameResult.mpr = player.getMPR();
                    playGameResult.totalScore = player.getScore();
                    playGameResult.headUrl = player.getHead();
                    data.playGameResults.add(playGameResult);
                }
                data.result = j;
                datas.add(data);
                j++;
            }
        }

        if (KDManager.getInstance().getOrderDetail().getOrder_type().equals("2")) {
            tvRestMoney.setVisibility(View.VISIBLE);
            tvRestMoney.setText("包机剩余游戏点：" + KDManager.getInstance().getOrderDetail().getBalance());
        } else {
            tvRestMoney.setVisibility(View.GONE);
        }
        if (datas.size() == 2) {//对战模式
            flVs.setVisibility(View.VISIBLE);
            for (Data data : datas) {
                for (PlayGameResult playGameResult : data.playGameResults) {
                    if (data.result == 1) {
                        View view = getGamePersonView(GameColor.Red, playGameResult);
                        llLeftPlayer.addView(view);
                        TextView userRank = (TextView) view.findViewById(R.id.tv_user_rank);
                        userRank.setText("胜利");
                    } else {
                        View view = getGamePersonView(GameColor.Grey, playGameResult);
                        llRightPlayer.addView(view);
                        TextView userRank = (TextView) view.findViewById(R.id.tv_user_rank);
                        userRank.setText("失败");
                    }
                }
            }
        } else {
            flRank.setVisibility(View.VISIBLE);//普通模式
            for (int i = 0; i < datas.get(0).playGameResults.size(); i++) {
                PlayGameResult playGameResult = datas.get(0).playGameResults.get(i);
                View view = getGamePersonView(GameColor.values()[i], playGameResult);
                ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                view.setLayoutParams(layoutParams);
                llCenter.addView(view);
                TextView userRank = (TextView) view.findViewById(R.id.tv_user_rank);
                userRank.setText("第" + num_lower[i] + "名");
            }
        }

        pushMessage = PushMessageDialog.get();
        pushMessage.text("再来一局")
                .autoDismiss(false)
                .keyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (C.app.debug) {
                            return KeyEventUtil.onKeyDown(GameResultActivity.this, i);
                        } else {
                            return false;
                        }
                    }
                });
        pushMessage.show(this);
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initListener() {

    }

    /**
     * 游戏数据上报
     */
    private void uploadGameResults() {
        final OrderResultsRequest resultsRequest = new OrderResultsRequest(KDManager.getInstance().getOrderDetail().getOrder_no());
        NetWork.request(this, resultsRequest, new OnSuccessCallback() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                if (baseResponse.isSuccess()) {
                    Logger.d("游戏数据上报成功", JSON.toJSONString(resultsRequest));
                } else {
                    showMsg(baseResponse.getMsg());
                }
            }
        }, new OnErrorCallback() {
            @Override
            public void onError(BaseResponse baseResponse) {
                showMsg(baseResponse.getMsg());
            }
        }, false);
    }

    private View getGamePersonView(GameColor gameColor, PlayGameResult playGameResult) {
        View view = getViewFromId(R.layout.view_game_rank, null);
        ImageView ivCrown = (ImageView) view.findViewById(R.id.iv_icon_crown);
        ImageView ivHead = (ImageView) view.findViewById(R.id.iv_game_result_icon);
        ImageView ivUserIcon = (ImageView) view.findViewById(R.id.iv_user_head);
        TextView tvNumber = (TextView) view.findViewById(R.id.tv_dart_number);
        TextViewBorder tvTotalPoints = (TextViewBorder) view.findViewById(R.id.tv_total_points);
        TextView userName = (TextView) view.findViewById(R.id.tv_user_name);
        userName.setText(playGameResult.personName);
        tvNumber.setText(playGameResult.number + "");
        LinearLayout llmp = (LinearLayout) view.findViewById(R.id.ll_mp);
        LinearLayout llpp = (LinearLayout) view.findViewById(R.id.ll_pp);
        if (isMpr) {
            llpp.setVisibility(View.GONE);
            llmp.setVisibility(View.VISIBLE);
            TextView tvMPR = (TextView) view.findViewById(R.id.tv_mpr);
            tvMPR.setText(String.format("%.1f", playGameResult.mpr));
            tvMPR.setTextColor(getGameColor(gameColor));
        } else {
            llmp.setVisibility(View.GONE);
            llpp.setVisibility(View.VISIBLE);
            TextView tvPPR = (TextView) view.findViewById(R.id.tv_ppr);
            TextView tvPPD = (TextView) view.findViewById(R.id.tv_ppd);
            tvPPD.setText(String.format("%.1f", playGameResult.ppd));
            tvPPR.setText(String.format("%.1f", playGameResult.ppr));
            tvPPR.setTextColor(getGameColor(gameColor));
            tvPPD.setTextColor(getGameColor(gameColor));
        }
        tvTotalPoints.setText(playGameResult.totalScore + "");
        tvNumber.setTextColor(getGameColor(gameColor));
        tvTotalPoints.setTextColor(getGameColor(gameColor));
        tvTotalPoints.setBorderColor(getGameColor(gameColor));
        if (playGameResult.headUrl == null) {
            ivUserIcon.setVisibility(View.INVISIBLE);
        } else {
            ivUserIcon.setVisibility(View.VISIBLE);
            if (playGameResult.headUrl.startsWith("http")) {
                Glide.with(this).load(playGameResult.headUrl).into(ivUserIcon);
            } else {
                ivUserIcon.setImageURI(Uri.parse(playGameResult.headUrl));
            }
        }
        if (gameColor == GameColor.Red) {
            ivCrown.setVisibility(View.VISIBLE);
        } else {
            ivCrown.setVisibility(View.INVISIBLE);
        }
        switch (gameColor) {
            case Red:
                ivHead.setImageResource(R.mipmap.icon_game_result_red);
                break;
            case Grey:
                ivHead.setImageResource(R.mipmap.icon_game_result_grey);
                break;
            case Green:
                ivHead.setImageResource(R.mipmap.icon_game_result_green);
                break;
            case Purple:
                ivHead.setImageResource(R.mipmap.icon_game_result_purple);
                break;
            case Yellow:
                ivHead.setImageResource(R.mipmap.icon_game_result_yellow);
                break;
        }
        return view;
    }

    private int getGameColor(GameColor gameColor) {
        switch (gameColor) {
            case Red:
                return getResColor(R.color.game_result_red);
            case Grey:
                return getResColor(R.color.game_result_grey);
            case Green:
                return getResColor(R.color.game_result_green);
            case Purple:
                return getResColor(R.color.game_result_purple);
            case Yellow:
                return getResColor(R.color.game_result_yellow);
        }
        return 0;
    }

    class Data {
        int result;
        ArrayList<PlayGameResult> playGameResults;
    }

    class PlayGameResult {

        String headUrl;
        String personName;
        int number;
        double ppr;
        double ppd;
        double mpr;
        int totalScore;

    }

    enum GameColor {
        Red,
        Yellow,
        Green,
        Purple,
        Grey,
    }

    @Override
    public void onKeyConfirm() {
        GameUtil.gameOver();
        Bundle bundle = new Bundle();
        if (pushMessage != null && pushMessage.getDialog() != null && pushMessage.getDialog().isShowing()) {//再来一局
            bundle.putInt(C.key.model, MainActivity.AGAIN_MODE);
        } else {//取消再来一局
            bundle.putInt(C.key.model, MainActivity.RESET_MODE);
        }
        JumpIntent.jump(this, MainActivity.class, bundle);
    }

    @Override
    public void onKeyCancel() {
        if (pushMessage == null) {
            onKeyConfirm();
        } else {
            pushMessage.dismiss();
            pushMessage = null;
        }
    }

    public int getResColor(int id) {
        return ContextCompat.getColor(this, id);
    }

}
