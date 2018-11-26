package com.hopen.darts.game.base;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hopen.darts.R;
import com.hopen.darts.base.BaseApplication;
import com.hopen.darts.game.GameUtil;
import com.hopen.darts.game.base.interfaces.GameImageLoader;
import com.hopen.darts.game.base.interfaces.OnGameOverListener;
import com.hopen.darts.game.base.interfaces.OnHitOverListener;
import com.hopen.darts.game.base.interfaces.OnRetreatOverListener;
import com.hopen.darts.game.base.interfaces.OnRoundOverListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 游戏基类
 */

public abstract class BaseGame {

    /**
     * 获取游戏系列id
     *
     * @return 游戏系列id
     */
    public abstract String getSeriesId();

    /**
     * 获取游戏id
     *
     * @return 游戏id
     */
    public abstract String getGameId();

    /**
     * 获取游戏名称
     *
     * @return 游戏名称
     */
    public String getGameName() {
        return GameUtil.getGameName(getSeriesId(), getGameId());
    }

    /**
     * 获取游戏描述
     *
     * @return 游戏描述
     */
    public String getGameDescribe() {
        return GameUtil.getGameDescribe(getSeriesId(), getGameId());
    }

    /**
     * 获取游戏在首页的介绍图片
     *
     * @return 游戏在首页的介绍图片
     */
    public int getGameDescribeImg() {
        return R.mipmap.game_main_game_describe_img;
    }

    /**
     * 是否为网络模式
     *
     * @return 是否为网络模式
     */
    public abstract boolean isOnline();

    /**
     * 获取游戏模式列表
     *
     * @return 游戏模式列表
     */
    public abstract List<GameMode> getModeList();

    /**
     * 游戏的实时状态
     */
    protected GameState gameState = GameState.NONE;
    /**
     * 本次游戏的随机数生成器，根据指定seed生成随机数，保证联机的两端具有相同的随机数生成逻辑
     */
    protected Random gameRandom;
    /**
     * 游戏页面资源文件配置类
     */
    protected GameRes gameRes;
    /**
     * 游戏规则类
     */
    protected GameRule gameRule;
    /**
     * 本次游戏的游戏模式
     */
    protected GameMode gameMode;
    /**
     * 在网络对战中本机阵营(队伍)的id
     */
    protected int localGroupId;
    /**
     * 本次游戏的阵营(队伍)数组
     */
    protected Group[] groups;
    /**
     * 当前正在掷镖阵营(队伍)的攻击顺序position
     */
    protected int currentGroupHitPosition;
    /**
     * 本次比赛中，阵营(队伍)的攻击顺序(存储队伍在{@link #groups}中的position)
     */
    protected int[] hitPositions;
    /**
     * 默认玩家头像图片加载器
     */
    GameImageLoader defaultHeadLoader;
    /**
     * 掷镖结束回调监听
     */
    protected LinkedList<OnHitOverListener> onHitOverListenerList;
    /**
     * 退镖结束回调监听
     */
    protected LinkedList<OnRetreatOverListener> onRetreatOverListenerList;
    /**
     * 回合结束回调监听
     */
    protected LinkedList<OnRoundOverListener> onRoundOverListenerList;
    /**
     * 游戏结束回调监听
     */
    protected LinkedList<OnGameOverListener> onGameOverListeners;

    /**
     * 创建一个与自身数据相同的对象
     *
     * @return 一个与自身数据相同的对象
     */
    public abstract BaseGame newIns();

    /**
     * 创建游戏页面资源文件配置对象
     *
     * @return 游戏页面资源文件配置对象
     */
    protected abstract GameRes createGameRes();

    /**
     * 创建游戏规则对象
     *
     * @return 游戏规则对象
     */
    protected abstract GameRule createGameRule();

    /**
     * 获取游戏实时状态
     *
     * @return 游戏实时状态
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * 获取本游戏的游戏模式
     *
     * @return 游戏模式
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * 获取游戏页面资源文件配置对象
     *
     * @return 游戏页面资源文件配置对象
     */
    public GameRes getGameRes() {
        return gameRes;
    }

    /**
     * 获取游戏规则建模对象
     *
     * @return 游戏规则建模对象
     */
    public GameRule getGameRule() {
        return gameRule;
    }

    /**
     * 创建阵营(队伍)数组
     * 游戏加载过程中应调用的第一个方法(入口方法)
     *
     * @param game_mode 游戏模式
     */
    public void initGroups(GameMode game_mode) {
        this.gameState = GameState.BEGINNING;
        this.gameMode = game_mode;
        this.localGroupId = 0;
        defaultHeadLoader = new GameImageLoader() {
            @Override
            public void load(ImageView image_view, String path) {
                image_view.setImageURI(Uri.parse(path));
            }
        };
        if (isOnline())
            gameMode = GameMode.OL_TWO;
        groups = new Group[gameMode.getGroupNum()];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = new Group(this, i + 1);
        }
    }

    /**
     * 游戏开始进行初始化
     * 在这个方法中初始化{@link #gameRes}和{@link #gameRule}
     *
     * @param start_time   游戏开始时间的long型时间戳
     * @param first_player 先攻玩家(可以传空，以player1先攻)
     */
    public void initGame(long start_time, Player first_player) {
        //初始化游戏随机数生成器
        gameRandom = new Random(start_time);
        //初始化掷镖结束回调监听列表
        onHitOverListenerList = new LinkedList<>();
        //初始化退镖结束回调监听列表
        onRetreatOverListenerList = new LinkedList<>();
        //初始化回合结束回调监听列表
        onRoundOverListenerList = new LinkedList<>();
        //初始化游戏结束回调监听列表
        onGameOverListeners = new LinkedList<>();
        //初始化res和rule
        gameRes = createGameRes();
        gameRule = createGameRule();
        //根据先攻玩家初始化各队伍信息
        hitPositions = new int[groups.length];
        for (int i = 0; i < groups.length; i++) {
            Group group = groups[i];
            boolean first = group.initGroup(first_player);
            hitPositions[i] = i;
            if (first) {
                int temp = hitPositions[0];
                hitPositions[0] = hitPositions[i];
                hitPositions[i] = temp;
            }
        }
        //初始化本游戏的第一个回合
        currentGroupHitPosition = 0;
        groups[hitPositions[currentGroupHitPosition]].newRound();
        this.gameState = GameState.PLAYING;
    }

    /**
     * 初始化联机模式游戏专用方法
     *
     * @param start_time     游戏开始时间的long型时间戳
     * @param local_group_id 在网络对战中本机阵营(队伍)的id
     */
    public void initOnlineGame(long start_time, int local_group_id) {
        setLocalGroupId(local_group_id);
        initGame(start_time, null);
    }

    /**
     * 设置在网络对战中本机阵营(队伍)的id
     * 并且为非本机阵营(队伍)设置glide加载方式的头像loader
     *
     * @param local_group_id 在网络对战中本机阵营(队伍)的id
     */
    public void setLocalGroupId(int local_group_id) {
        if (this.localGroupId != 0) return;
        this.localGroupId = local_group_id;
        GameImageLoader glide_loader = new GameImageLoader() {
            @Override
            public void load(final ImageView image_view, String path) {
                Glide.with(BaseApplication.getApplication()).load(path).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        image_view.setImageResource(R.mipmap.icon_head_default);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        image_view.setImageDrawable(resource);
                        return true;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.NONE).into(image_view);
            }
        };
        for (Group group : groups) {
            if (group.getId() != localGroupId) {
                for (Player player : group.players) {
                    player.headLoader = glide_loader;
                }
            }
        }
    }

    /**
     * 添加掷镖结束回调监听
     *
     * @param listener 掷镖结束回调监听
     */
    public void addOnHitOverListener(OnHitOverListener listener) {
        if (onHitOverListenerList == null)
            onHitOverListenerList = new LinkedList<>();
        onHitOverListenerList.add(listener);
    }

    /**
     * 移除掷镖结束回调监听
     *
     * @param listener 掷镖结束回调监听
     */
    public void removeOnHitOverListener(OnHitOverListener listener) {
        if (onHitOverListenerList == null) return;
        onHitOverListenerList.remove(listener);
    }

    /**
     * 添加退镖结束回调监听
     *
     * @param listener 退镖结束回调监听
     */
    public void addOnRetreatOverListener(OnRetreatOverListener listener) {
        if (onRetreatOverListenerList == null)
            onRetreatOverListenerList = new LinkedList<>();
        onRetreatOverListenerList.add(listener);
    }

    /**
     * 移除退镖结束回调监听
     *
     * @param listener 退镖结束回调监听
     */
    public void removeOnRetreatOverListener(OnRetreatOverListener listener) {
        if (onRetreatOverListenerList == null) return;
        onRetreatOverListenerList.remove(listener);
    }

    /**
     * 添加回合结束回调监听
     *
     * @param listener 回合结束回调监听
     */
    public void addOnRoundOverListener(OnRoundOverListener listener) {
        if (onRoundOverListenerList == null)
            onRoundOverListenerList = new LinkedList<>();
        onRoundOverListenerList.add(listener);
    }

    /**
     * 移除回合结束回调监听
     *
     * @param listener 回合结束回调监听
     */
    public void removeOnRoundOverListener(OnRoundOverListener listener) {
        if (onRoundOverListenerList == null) return;
        onRoundOverListenerList.remove(listener);
    }

    /**
     * 添加游戏结束回调监听
     *
     * @param listener 游戏结束回调监听
     */
    public void addOnGameOverListener(OnGameOverListener listener) {
        if (onGameOverListeners == null)
            onGameOverListeners = new LinkedList<>();
        onGameOverListeners.add(listener);
    }

    /**
     * 移除游戏结束回调监听
     *
     * @param listener 游戏结束回调监听
     */
    public void removeOnGameOverListener(OnGameOverListener listener) {
        if (onGameOverListeners == null) return;
        onGameOverListeners.remove(listener);
    }

    /**
     * 获取本次游戏队伍列表
     *
     * @return 本次游戏队伍列表
     */
    public List<Group> getGroupList() {
        return Arrays.asList(groups);
    }

    /**
     * 获取本次游戏队伍列表，按照攻击顺序排列
     *
     * @return 本次游戏队伍列表
     */
    public List<Group> getGroupListOrderByHit() {
        List<Group> group_list = new ArrayList<>();
        for (int i = 0; i < groups.length; i++) {
            Group group = groups[hitPositions[i]];
            group_list.add(group);
        }
        return group_list;
    }

    /**
     * 获取本次游戏全部玩家列表
     *
     * @return 全部玩家列表
     */
    public List<Player> getAllPlayer() {
        List<Player> player_list = new ArrayList<>();
        for (Group group : groups) {
            player_list.addAll(group.getPlayerList());
        }
        return player_list;
    }

    /**
     * 获取按照分数高低(排名先后)排好序的阵营(队伍)列表
     *
     * @return 排好序的阵营(队伍)列表
     */
    public List<Group> getOrderGroup() {
        List<Group> group_list = getGroupList();
        Collections.sort(group_list);
        return group_list;
    }

    /**
     * 获取游戏上报数据时的附加数据，默认返回null
     *
     * @return 游戏上报数据时的附加数据
     */
    public Object getAttach() {
        return null;
    }

    /**
     * @return 网络对战中，是否轮到本机掷镖，非联机模式情况一直返回true(即自动忽略此方法)
     */
    public boolean isLocalTurn() {
        if (!isOnline()) return true;
        if (groups == null || hitPositions == null) return false;
        return getCurrentGroup().getId() == localGroupId;
    }

    /**
     * @return 获取本机对应的阵营(队伍)id
     */
    public Group getLocalGroup() {
        for (Group item : groups) {
            if (item.getId() == localGroupId) return item;
        }
        return groups[0];
    }

    /**
     * @return 本机是否赢得本次游戏(单机游戏默认返回true)
     */
    public boolean isLocalWin() {
        return !isOnline() || localGroupId == getOrderGroup().get(0).getId();
    }

    /**
     * @return 是否已经进入游戏（正在准备状态或者进心中的状态都返回true）
     */
    public boolean isEnter() {
        return isBeginning() || isPlaying();
    }

    /**
     * @return 本次游戏是否正在准备
     */
    public boolean isBeginning() {
        return gameState == GameState.BEGINNING;
    }

    /**
     * @return 本次游戏是否正在进行
     */
    public boolean isPlaying() {
        return gameState == GameState.PLAYING;
    }

    /**
     * @return 本次游戏是否已经结束
     */
    public boolean isOver() {
        return gameState == GameState.OVER;
    }

    /**
     * 根据阵营(队伍)的攻击序列来获取指定指定阵营(队伍)
     *
     * @param position 攻击序列
     * @return 指定阵营(队伍)
     */
    public Group getGroupByHitPosition(int position) {
        return groups[hitPositions[position]];
    }

    /**
     * 根据阵营(队伍)在数组中的序列来获取指定指定阵营(队伍)
     *
     * @param position 在数组中的序列
     * @return 指定阵营(队伍)
     */
    public Group getGroupByPosition(int position) {
        return groups[position];
    }

    /**
     * 获取当前正在掷镖阵营(队伍)
     *
     * @return 当前正在掷镖阵营(队伍)
     */
    public Group getCurrentGroup() {
        return groups[hitPositions[currentGroupHitPosition]];
    }

    /**
     * 切换到下一个要开始掷镖阵营(队伍)
     * 本方法会为该阵营(队伍)创建一个新的回合
     * 如果当前正在掷镖的阵营(队伍)回合还没有结束，则不会切换，会返回正在正在掷镖的阵营(队伍)
     *
     * @return 下一个要开始掷镖阵营(队伍)
     */
    public Group nextCurrentGroup() {
        Group group = groups[hitPositions[currentGroupHitPosition]];
        if (gameRule.isOver()) return group;
        if (groups.length == 1) {
            //单人模式，判断回合是否结束，结束了创建一个新回合，并直接返回
            if (group.getCurrentRound().isOver())
                group.newRound();
            return group;
        } else {
            //如果当前正在掷镖的阵营(队伍)回合还没有结束，则不会切换，会返回正在正在掷镖的阵营(队伍)
            if (!group.getCurrentRound().isOver())
                return group;
            //否则切换到下一攻击顺位的阵营(队伍)position
            currentGroupHitPosition = (currentGroupHitPosition + 1) % hitPositions.length;
            group = groups[hitPositions[currentGroupHitPosition]];
            group.newRound();//创建新回合
            gameRes.onRefresh();
            return group;
        }
    }
}
