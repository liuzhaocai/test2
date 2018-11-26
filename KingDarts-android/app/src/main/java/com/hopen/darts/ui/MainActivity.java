package com.hopen.darts.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.C;
import com.hopen.darts.game.GameUtil;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.Player;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.netty.BaseNettyResponse;
import com.hopen.darts.networks.commons.netty.N;
import com.hopen.darts.networks.interfaces.OnNettyReceiveCallback;
import com.hopen.darts.networks.netty.NettyUtil;
import com.hopen.darts.networks.request.OrderRequest;
import com.hopen.darts.networks.request.OrderStartRequest;
import com.hopen.darts.networks.response.OrderResponse;
import com.hopen.darts.networks.response.PaySuccessResponse;
import com.hopen.darts.ui.presenter.MainPresenter;
import com.hopen.darts.utils.FileUtils;
import com.hopen.darts.utils.JumpIntent;
import com.hopen.darts.utils.SharePreUtil;
import com.hopen.darts.utils.UtilCopyData;
import com.hopen.darts.views.CustomFontTextView;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hopen.darts.ui.MainActivity.CurrentFocus.GamesConfigs;
import static com.hopen.darts.ui.MainActivity.CurrentFocus.MainGames;
import static com.hopen.darts.ui.MainActivity.CurrentFocus.SonGames;

/**
 * 选择游戏界面
 */
public class MainActivity extends BaseActivity implements OnNettyReceiveCallback {
    {
        isFixedTimeNoOperationToShowAdvert = true;
    }

    public static final int AGAIN_MODE = 1;
    public static final int RESET_MODE = 2;

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.game_series_ryc)
    RecyclerView gameSeriesRyc;
    @BindView(R.id.ll_bottom_view)
    LinearLayout llBottomView;
    @BindView(R.id.tv_game_name_title)
    CustomFontTextView tvGameNameTitle;
    @BindView(R.id.tv_game_name_desc)
    CustomFontTextView tvGameNameDesc;
    @BindView(R.id.rcy_son_games)
    RecyclerView rcySonGames;
    @BindView(R.id.fl_left_son_games_view)
    public FrameLayout flLeftSonGamesView;
    @BindView(R.id.ll_main_desc_text_container)
    public LinearLayout llMainDescTextContainer;
    @BindView(R.id.fl_main_desc_view)
    public FrameLayout flMainDescView;
    @BindView(R.id.ll_model)
    LinearLayout llModel;

    public GameItemAdapter adapter;
    private List<BaseSeries> list = new ArrayList<>();
    private GameSonItemAdapter sonItemAdapter;
    private CurrentFocus currentFocus = MainGames;
    public int leftSonMenuWidth = 0;
    private List<BaseGame> baseGames = new ArrayList<>();
    private MainPresenter mainPresenter;
    private boolean isAllowClick = true;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainPresenter = new MainPresenter(this);
        mainPresenter.initModelSwitch(llModel);
        leftSonMenuWidth = flLeftSonGamesView.getLayoutParams().width;
        flLeftSonGamesView.getLayoutParams().width = 0;
        llMainDescTextContainer.getLayoutParams().width = 1093;
        adapter = new GameItemAdapter();
        gameSeriesRyc.setAdapter(adapter);
        gameSeriesRyc.setLayoutManager(new LinearLayoutManager(this));
        sonItemAdapter = new GameSonItemAdapter();
        rcySonGames.setAdapter(sonItemAdapter);
        rcySonGames.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initData() {
        adapter.notifyDataSetChanged(0);
        list = GameUtil.getGameSeriesList();
        list.add(new BaseSeries() {
            @Override
            public String getSeriesId() {
                return "-1";
            }

            @Override
            public String getSeriesName() {
                return getResources().getString(R.string.app_dart_title);
            }

            @Override
            public String getSeriesDescribe() {
                return getResources().getString(R.string.app_dart_desc);
            }

            @Override
            public List<BaseGame> getGameList() {
                return null;
            }
        });
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initListener() {
        NettyUtil.addReceiveMsgCall(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            switch (intent.getIntExtra(C.key.model, 0)) {
                case AGAIN_MODE:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            selectGameDone();
                        }
                    }, 160);
                    break;
                case RESET_MODE:
                    resetGameSetting();
                    break;
            }
        }
    }

    /**
     * 重置游戏菜单配置
     */
    public void resetGameSetting() {
        isAllowClick = true;
        currentFocus = MainGames;
        llModel.setVisibility(View.INVISIBLE);
        flMainDescView.setVisibility(View.VISIBLE);
        mainPresenter.hiddenSonItem();
        adapter.notifyDataSetChanged(0);
    }

    /**
     * 选择完游戏
     */
    public void selectGameDone() {
        if (adapter.selected != GameUtil.getGameSeriesList().size()) {
            BaseSeries series = GameUtil.getGameSeriesList().get(adapter.selected);
            BaseGame game = series.getGameList().get(sonItemAdapter.selected);
            startSettingGame(game, mainPresenter.list.get(mainPresenter.currentModel));
        }
    }

    /**
     * 设置游戏参数
     */
    public void startSettingGame(BaseGame game, GameMode gameMode) {
        GameUtil.chooseGame(game, gameMode);
        checkPay(GameUtil.getPlayingGame());
    }

    /**
     * 创建订单进行是否包机判断
     */
    private void checkPay(final BaseGame baseGame) {

        if (baseGame.getGameId() == null) {
            showMsg("未找到此游戏资源");
            return;
        }
        if (baseGame.getGameMode() == null) {
            showMsg("未找到此游戏模式");
            return;
        }
        isAllowClick = false;
        OrderRequest request = new OrderRequest(baseGame.getGameId(), baseGame.getGameMode().getId() + "");
        NettyUtil.sendRequest(request);
        SharePreUtil.getInstance().putString(C.key.last_order_request_id, request.getRequestId());
    }

    public class GameItemAdapter extends RecyclerView.Adapter<GameItemAdapter.Holder> {

        private int VisMaxIndex;
        private int selected = 0;

        public void notifyDataSetChanged(int index) {
            if (index == 0) {
                VisMaxIndex = list.size() < 7 ? list.size() : 7;
            }
            if (selected < index) {
                if (index >= VisMaxIndex) {
                    VisMaxIndex = index;
                    gameSeriesRyc.scrollToPosition(index);
                }
            } else if (selected > index) {
                if (index <= VisMaxIndex - 7) {
                    VisMaxIndex = index + 7;
                    gameSeriesRyc.scrollToPosition(VisMaxIndex - 1);
                }
            }
            selected = index;
            super.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(getViewFromId(R.layout.item_main_game_series, null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            BaseSeries baseSeries = list.get(position);
            if (selected == position) {
                holder.flBg.setSelected(true);
                holder.tvGameName.setTextColor(getResources().getColor(R.color.white));
                tvGameNameTitle.setText(baseSeries.getSeriesName());
                tvGameNameDesc.setText(baseSeries.getSeriesDescribe());
                if (position == list.size() - 1) {
                    ivLogo.setImageResource(R.mipmap.icon_center_dart);
                } else {
                    ivLogo.setImageResource(baseSeries.getSeriesDescribeImg());
                }
            } else {
                holder.flBg.setSelected(false);
                holder.tvGameName.setTextColor(getResources().getColor(R.color.text_white_80));
            }
            if (position == list.size() - 1) {
                holder.tvGameName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_small));
            } else {
                holder.tvGameName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_normal));
            }
            holder.tvGameName.setText(baseSeries.getSeriesName());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_game_name)
            TextView tvGameName;
            @BindView(R.id.fl_bg)
            FrameLayout flBg;

            public Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    class GameSonItemAdapter extends RecyclerView.Adapter<GameSonItemAdapter.Holder> {

        private int selected = 0;
        private int VisMaxIndex;

        public void notifyDataSetChanged(int index) {
            if (index == 0) {
                VisMaxIndex = baseGames.size() < 6 ? baseGames.size() : 6;
            }
            selected = index;
            if (index < VisMaxIndex && index > VisMaxIndex - 6) {
            } else if (index >= VisMaxIndex) {
                VisMaxIndex++;
                rcySonGames.scrollToPosition(index);
            } else {
                VisMaxIndex--;
                rcySonGames.scrollToPosition(index);
            }
            super.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(getViewFromId(R.layout.item_son_game_series, null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            BaseGame baseGame = baseGames.get(position);
            if (selected == position) {
                holder.flBg.setSelected(true);
                holder.tvGameName.setTextColor(getResources().getColor(R.color.white));
                ivLogo.setImageResource(baseGame.getGameDescribeImg());
                tvGameNameTitle.setText(baseGame.getGameName());
                tvGameNameDesc.setText(baseGame.getGameDescribe());
            } else {
                holder.flBg.setSelected(false);
                holder.tvGameName.setTextColor(getResources().getColor(R.color.text_white_80));
            }
            if (baseGame.getGameName().length() >= 6) {
                holder.tvGameName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_small));
            } else {
                holder.tvGameName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_normal));
            }
            holder.tvGameName.setText(baseGame.getGameName());
        }

        @Override
        public int getItemCount() {
            return baseGames.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_game_name)
            TextView tvGameName;
            @BindView(R.id.fl_bg)
            FrameLayout flBg;

            public Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


    public void onKeyLeft() {
        if (!isAllowClick) return;
        switch (currentFocus) {
            case MainGames:
                break;
            case SonGames:
                currentFocus = MainGames;
                mainPresenter.hiddenSonItem();
                break;
            case GamesConfigs:
                currentFocus = SonGames;
                llModel.setVisibility(View.INVISIBLE);
                flMainDescView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void onKeyRight() {
        if (!isAllowClick) return;
        switch (currentFocus) {
            case MainGames:
                if (list.get(adapter.selected) == null || list.get(adapter.selected).getGameList() == null || list.get(adapter.selected).getGameList().size() == 0) {
                    return;
                } else {
                    baseGames = list.get(adapter.selected).getGameList();
                    sonItemAdapter.notifyDataSetChanged(0);
                    currentFocus = SonGames;
                    mainPresenter.showSonItem();
                }
                break;
            case SonGames:
                currentFocus = GamesConfigs;
                llModel.setVisibility(View.VISIBLE);
                flMainDescView.setVisibility(View.INVISIBLE);
                mainPresenter.list = baseGames.get(sonItemAdapter.selected).getModeList();
                mainPresenter.currentModel = 2;
                if (mainPresenter.list.size() <= mainPresenter.currentModel) {
                    mainPresenter.currentModel = mainPresenter.list.size() - 1;
                }
                mainPresenter.showModelIndex(mainPresenter.list, mainPresenter.currentModel);
                break;
            case GamesConfigs:
                selectGameDone();
                break;
        }
    }

    public void onKeyTop() {
        if (!isAllowClick) return;
        switch (currentFocus) {
            case MainGames:
                if (adapter.selected > 0) {
                    adapter.notifyDataSetChanged(adapter.selected - 1);
                }
                break;
            case SonGames:
                if (sonItemAdapter.selected > 0) {
                    sonItemAdapter.notifyDataSetChanged(sonItemAdapter.selected - 1);
                }
                break;
            case GamesConfigs:
                if (mainPresenter.currentModel > 0) {
                    mainPresenter.currentModel--;
                    mainPresenter.showModelIndex(mainPresenter.list, mainPresenter.currentModel);
                }
                break;
        }
    }

    public void onKeyBottom() {
        if (!isAllowClick) return;
        switch (currentFocus) {
            case MainGames:
                if ((adapter.selected + 1) < adapter.getItemCount()) {
                    adapter.notifyDataSetChanged(adapter.selected + 1);
                }
                break;
            case SonGames:
                if ((sonItemAdapter.selected + 1) < sonItemAdapter.getItemCount()) {
                    sonItemAdapter.notifyDataSetChanged(sonItemAdapter.selected + 1);
                }
                break;
            case GamesConfigs:
                if ((mainPresenter.currentModel + 1) < mainPresenter.list.size()) {
                    mainPresenter.currentModel++;
                    mainPresenter.showModelIndex(mainPresenter.list, mainPresenter.currentModel);
                }
                break;
        }
    }

    public void onKeyCancel() {
        if (!isAllowClick) return;
        switch (currentFocus) {
            case MainGames:
                break;
            case SonGames:
                currentFocus = MainGames;
                mainPresenter.hiddenSonItem();
                break;
            case GamesConfigs:
                currentFocus = SonGames;
                llModel.setVisibility(View.INVISIBLE);
                flMainDescView.setVisibility(View.VISIBLE);
                break;
        }
    }


    public void onKeyConfirm() {
        if (!isAllowClick) return;
        switch (currentFocus) {
            case MainGames:
                break;
            case SonGames:
                break;
            case GamesConfigs:
                selectGameDone();
                return;
        }
        onKeyRight();
    }

    enum CurrentFocus {
        MainGames,
        SonGames,
        GamesConfigs,
    }

    @Override
    public void receiveMsg(String msg) {
        isAllowClick = true;
        BaseNettyResponse response = JSON.parseObject(msg, BaseNettyResponse.class);
        switch (response.isSuccess(N.Order)) {
            case success:
                OrderResponse orderResponse = JSON.parseObject(msg, OrderResponse.class);
                String last_order_request_id = SharePreUtil.getInstance().getString(C.key.last_order_request_id);
                Logger.d(last_order_request_id);
                if (TextUtils.equals(last_order_request_id, orderResponse.getRequestId())) {
                    File file = new File(C.app.file_path_img + "/Player");
                    FileUtils.deleteDirectory(file);
                    file.mkdirs();
                    KDManager.getInstance().setOrderDetail(orderResponse.getData());
                    if (Integer.valueOf(orderResponse.getData().getOrder_type()) == 1) {//单次游戏
                        Bundle bundle = new Bundle();
                        bundle.putString("url", orderResponse.getData().getUrl());
                        JumpIntent.jump(MainActivity.this, PreparePayActivity.class, bundle);
                    }
                } else {
                    showMsg("requestId不一致");
                }
                break;
            case fail:
                showMsg(response.getMsg());
                break;
        }
        switch (response.isSuccess(N.OrderPay)) {
            case success:
                PaySuccessResponse paySuccessResponse = JSON.parseObject(msg, PaySuccessResponse.class);
                if (Integer.valueOf(paySuccessResponse.getData().getOrder_type()) == 2) {//包机游戏
                    KDManager.getInstance().setPayerDetail(paySuccessResponse.getData());
                    OrderStartRequest orderStartRequest = new OrderStartRequest(paySuccessResponse.getData().getOrder_no());
                    NettyUtil.sendRequest(orderStartRequest);
                    BaseSeries series = GameUtil.getGameSeriesList().get(adapter.selected);
                    BaseGame baseGame = series.getGameList().get(sonItemAdapter.selected);
                    if (baseGame.isOnline()) {
                        JumpIntent.jump(MainActivity.this, VideoVSWaitActivity.class);
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
                        JumpIntent.jump(MainActivity.this, TakePhotoActivity.class);
                    }
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
