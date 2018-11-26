package com.hopen.darts.game.base;

import com.hopen.darts.base.BaseApplication;
import com.hopen.darts.base.BaseDialog;
import com.hopen.darts.game.base.interfaces.OnGameOverListener;
import com.hopen.darts.game.base.interfaces.OnHitOverListener;
import com.hopen.darts.game.base.interfaces.OnRetreatOverListener;
import com.hopen.darts.game.base.interfaces.OnRoundOverListener;
import com.hopen.darts.utils.AlertMessage.PopGifDialog;
import com.hopen.darts.utils.AlertMessage.PopVideoDialog;
import com.hopen.darts.utils.AlertMessage.VideoEnum;
import com.hopen.darts.utils.SerialPort.KeyCode;
import com.hopen.darts.utils.live.PushConfig;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 游戏规则基类
 */
public abstract class GameRule<E extends BaseGame> {

    /**
     * 解析把盘采集到的数据并返回
     *
     * @param collected_data 把盘采集到的数据
     * @return 返回单镖数据对象，score代表第几分区(0-21)，area代表倍区
     */
    public static HitIJ analysisCollectedData(long collected_data) {
        HitIJ hit_ij = new HitIJ();
        if (collected_data != 0) {
            for (int i = 0; i < KeyCode.getScoreAreaCollectDataTable().length; i++) {
                for (int j = 0; j < KeyCode.getScoreAreaCollectDataTable()[i].length; j++) {
                    if (collected_data == KeyCode.getScoreAreaCollectDataTable()[i][j]) {
                        hit_ij.i = i;
                        hit_ij.j = j;
                    }
                }
            }
        }
        return hit_ij;
    }

    /**
     * 本资源对应的游戏
     */
    protected E mGame;
    /**
     * 每镖投完后需要执行的事件队列，每个事件都有级别，默认为0。
     * 每次执行队列中的事件时，如果队列中的事件级别不统一，
     * 则会按照{@link GameRule#ifIgnoreLevel}设定的忽略级别来忽略低于(不含)此级别的事件。
     * 如果所有事件的级别都一样，则不会忽略任何一个事件，
     * 哪怕所有事件级别都小于{@link GameRule#ifIgnoreLevel}
     * <p>
     * ps：此项设定是为了方便后期变更时要忽略某个事件的情况存在
     * </p>
     */
    private PriorityBlockingQueue<GameEvent> eventList;
    /**
     * 当前正在执行的游戏事件
     */
    private volatile GameEvent currentGameEvent;
    /**
     * 如果需要忽略时，忽略事件的级别
     */
    private final int ifIgnoreLevel = 1;
    /**
     * 联机模式直播入口
     */
    protected PushConfig pushConfig;

    protected GameRule(E game) {
        mGame = game;
        eventList = new PriorityBlockingQueue<>();
    }

    /**
     * 设置直播入口
     *
     * @param push_config 直播入口
     */
    public void live(PushConfig push_config) {
        this.pushConfig = push_config;
    }

    /**
     * 获取本游戏起始分数
     *
     * @return 起始分数
     */
    public abstract int getOriginScore();

    /**
     * 获取回合数(不可为零)
     *
     * @return 回合数
     */
    public abstract int getRoundNum();

    /**
     * 判断本次游戏是否结束
     *
     * @return 本次游戏是否结束
     */
    public abstract boolean isOver();

    /**
     * 比较两个阵营(队伍)的分数(排名)
     *
     * @param group1 阵营(队伍)1
     * @param group2 阵营(队伍)2
     * @return <0（小于0）:group1分数高(排名靠前)，0（等于0）:两者分数相等，0<（大于0）:group2分数高(排名靠前)
     */
    public abstract int groupCompare(Group group1, Group group2);

    /**
     * 获取每回合镖数(默认都是3镖，如有特殊游戏镖数不符，则重写该方法，返回指定的值)
     *
     * @return 每回合镖数
     */
    public int getDartNumInOneRound() {
        return 3;
    }

    /**
     * 为指定阵营(队伍)执行一次掷镖计算
     *
     * @param group  要计算的阵营(队伍)
     * @param hit_ij 单镖数据(本次掷镖对应分区和倍区)
     * @return 本次数据是否被有效保存
     */
    protected abstract boolean oneHit(Group group, HitIJ hit_ij);

    /**
     * 为指定阵营(队伍)执行一次退镖计算
     *
     * @param group 要计算的阵营(队伍)
     * @return 本次数据是否被有效保存
     */
    protected abstract boolean retreatHit(Group group);

    /**
     * 计算单次掷镖的分数
     * 注：此方法只会把此次掷镖当做最新的一次掷镖来计算
     *
     * @param hit_ij 采集到的数据对应在{@link KeyCode#getScoreAreaCollectDataTable()}中的位置
     * @return 单次掷镖的分数
     */
    protected abstract int calculateHitScore(HitIJ hit_ij);

    /**
     * 计算ppr、ppd、mpr等信息
     *
     * @param player 要计算的玩家对象
     */
    protected void calculatePlayerInfo(Player player) {
        player.mpr = -1;//-1为无效值
        if (player.hitNum == 0) player.ppd = 0;
        else player.ppd = (float) player.score / player.hitNum;
        int dart_num_in_one_round = getDartNumInOneRound();
        int temp = player.hitNum % dart_num_in_one_round;
        int round_num = player.hitNum / dart_num_in_one_round + (temp == 0 ? 0 : 1);
        if (round_num == 0) player.ppr = 0;
        else player.ppr = (float) player.score / round_num;
    }

    /**
     * 当掷镖操作结束时回调
     *
     * @param hit_ij         本镖投掷的数据
     * @param hit_score      本镖投掷的分数
     * @param hit_effective  本次数据是否有效
     * @param round          当前的回合数据
     * @param round_position 回合序列
     */
    protected void onHitOver(HitIJ hit_ij, int hit_score, boolean hit_effective, Round round, int round_position) {

    }

    /**
     * 当掷镖操作结束时回调
     *
     * @param hit_ij         本镖投掷的数据
     * @param hit_score      本镖投掷的分数
     * @param hit_effective  本次数据是否有效
     * @param round          当前的回合数据
     * @param round_position 回合序列
     */
    final void onParentHitOver(HitIJ hit_ij, int hit_score, boolean hit_effective, Round round, int round_position) {
        //子类需要的操作
        onHitOver(hit_ij, hit_score, hit_effective, round, round_position);
        //通知游戏资源控制类刷新
        mGame.gameRes.onRefresh();
        //判断回合和游戏是否结束
        if (round != null && round.isOver())
            onParentRoundOver(hit_ij, hit_score, hit_effective, round, round_position);
        else if (isOver())
            onParentGameOver(hit_ij, hit_score, hit_effective, round, false);
        else
            runEventsAndCallback(hit_ij, hit_score, hit_effective, round, false, false);
    }

    /**
     * 当退镖操作结束时回调
     *
     * @param hit_ij         本镖退镖的数据
     * @param hit_score      本镖退镖的分数
     * @param round          当前的回合数据
     * @param round_position 回合序列
     * @param effective      本次操作是否有效
     */
    protected void onRetreatOver(HitIJ hit_ij, int hit_score, Round round, int round_position, boolean effective) {

    }

    /**
     * 当退镖操作结束时回调
     *
     * @param hit_ij         本镖退镖的数据
     * @param hit_score      本镖退镖的分数
     * @param round          当前的回合数据
     * @param round_position 回合序列
     * @param effective      本次操作是否有效
     */
    final void onParentRetreatOver(final HitIJ hit_ij, final int hit_score, final Round round, final int round_position, final boolean effective) {
        //子类需要的操作
        onRetreatOver(hit_ij, hit_score, round, round_position, effective);
        //通知游戏资源控制类刷新
        mGame.gameRes.onRefresh();
        //执行游戏中的事件队列
        runEvents(new GameEvent.Callback() {

            /**
             * 事件队列执行完毕回调
             */
            @Override
            public void onOver() {
                try {
                    for (OnRetreatOverListener item : mGame.onRetreatOverListenerList) {
                        item.onRetreatOver(hit_ij, hit_score, effective);
                    }
                    round.getPlayer().getGroup().isRetreating = false;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 当回合结束时回调
     *
     * @param round          结束的回合
     * @param round_position 回合序列
     * @param game_over      游戏是否已经结束
     */
    protected void onRoundOver(Round round, int round_position, boolean game_over) {

    }

    /**
     * 当回合结束时回调
     *
     * @param hit_ij         本镖投掷的数据
     * @param hit_score      本镖投掷的分数
     * @param hit_effective  本次数据是否有效
     * @param round          结束的回合
     * @param round_position 回合序列
     */
    private void onParentRoundOver(HitIJ hit_ij, int hit_score, boolean hit_effective, Round round, int round_position) {
        boolean game_over = isOver();
        onRoundOver(round, round_position, game_over);
        if (game_over)
            onParentGameOver(hit_ij, hit_score, hit_effective, round, true);
        else
            runEventsAndCallback(hit_ij, hit_score, hit_effective, round, true, false);
    }

    /**
     * 当游戏结束时回调
     */
    protected void onGameOver() {
        if (!mGame.isOnline()) {//默认情况，非联机模式下，游戏结束时播放胜利动画
            addVideoEvent(VideoEnum.win, 1);
        }
    }

    /**
     * 当游戏结束时回调
     *
     * @param hit_ij        本镖投掷的数据
     * @param hit_score     本镖投掷的分数
     * @param hit_effective 本次数据是否有效
     * @param round         当前的回合数据
     * @param round_over    回合是否已经结束
     */
    private void onParentGameOver(HitIJ hit_ij, int hit_score, boolean hit_effective, Round round, boolean round_over) {
        mGame.gameState = GameState.OVER;
        onGameOver();
        runEventsAndCallback(hit_ij, hit_score, hit_effective, round, round_over, true);
    }

    /**
     * @return 联机模式下，是否是本机方获得胜利；
     */
    public boolean isLocalWin() {
        return mGame.localGroupId == mGame.getOrderGroup().get(0).getId();
    }

    /**
     * 添加一个事件
     *
     * @param event 事件
     */
    public void addGameEvent(GameEvent event) {
        if (event == null) return;
        eventList.add(event);
    }

    /**
     * 移除一个事件
     *
     * @param event 事件
     */
    public void removeGameEvent(GameEvent event) {
        eventList.remove(event);
    }

    /**
     * 清空游戏事件队列
     */
    protected void clearGameEvent() {
        eventList.clear();
    }

    /**
     * 添加一个gif播放事件
     *
     * @param res gif资源id
     */
    protected void addGifEvent(final int res) {
        addGifEvent(res, 0);
    }

    /**
     * 添加一个gif播放事件
     *
     * @param res gif资源id
     */
    protected void addGifEvent(final int res, int level) {
        addGameEvent(new GameEvent(level) {
            PopGifDialog dialog;

            @Override
            public void run() {
                dialog = PopGifDialog.get()
                        .res(res)
                        .dismissListener(new BaseDialog.OnDismissWithAnimEndListener() {
                            @Override
                            public void onDismissWithAnimEnd() {
                                overCallback();
                            }
                        });
                dialog.show(BaseApplication.getBaseActivity());
            }

            @Override
            public void overNow() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 添加一个视频播放事件
     *
     * @param video 视频对应枚举
     */
    protected void addVideoEvent(final VideoEnum video) {
        addVideoEvent(video, 0);
    }

    /**
     * 添加一个视频播放事件
     *
     * @param video 视频对应枚举
     * @param level 本视频事件的级别
     */
    protected void addVideoEvent(final VideoEnum video, int level) {
        addGameEvent(new GameEvent(level) {
            PopVideoDialog dialog;

            @Override
            public void run() {
                dialog = PopVideoDialog.get()
                        .path(video)
                        .dismissListener(new BaseDialog.OnDismissWithAnimEndListener() {
                            @Override
                            public void onDismissWithAnimEnd() {
                                overCallback();
                            }
                        });
                dialog.show(BaseApplication.getBaseActivity());
            }

            @Override
            public void overNow() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 依次执行事件队列并在执行完毕后回调已注册的监听回调
     *
     * @param hit_ij        本镖投掷的数据
     * @param hit_score     本镖投掷的分数
     * @param hit_effective 本次数据是否有效
     * @param round         当前的回合数据
     * @param round_over    回合是否已经结束
     * @param game_over     游戏是否已经结束
     */
    private synchronized void runEventsAndCallback(final HitIJ hit_ij, final int hit_score, final boolean hit_effective, final Round round, final boolean round_over, final boolean game_over) {
        //执行游戏中的事件队列
        runEvents(new GameEvent.Callback() {

            /**
             * 事件队列执行完毕回调
             */
            @Override
            public void onOver() {
                try {
                    if (round != null && !game_over)
                        round.getPlayer().getGroup().isHitting = false;
                    for (OnHitOverListener item : mGame.onHitOverListenerList) {
                        item.onHitOver(hit_ij, hit_score, hit_effective);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (round_over) {
                    try {
                        for (OnRoundOverListener item : mGame.onRoundOverListenerList) {
                            item.onRoundOver(round, game_over);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                if (game_over) {
                    try {
                        for (OnGameOverListener item : mGame.onGameOverListeners) {
                            item.onGameOver();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 依次执行事件队列
     *
     * @param callback 指定完毕的回调
     */
    private synchronized void runEvents(final GameEvent.Callback callback) {
        if (eventList.size() == 0) {
            //事件队列为空时，回调传入的callback，并将当前事件置为null
            currentGameEvent = null;
            //如果直播入口存在，则在事件队列执行完毕时，开始直播
            try {
                if (pushConfig != null)
                    pushConfig.startPush();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (callback != null) callback.onOver();
        } else {
            if (currentGameEvent == null || currentGameEvent.isOver()) {
                //仅在当前事件为空或者当前事件已经执行完毕时，从队列中poll出下一个事件
                GameEvent temp = eventList.poll();
                //判断下一个事件的级别和当前事件的级别，如果下一个事件的级别小于当前事件的级别
                //并且下一个事件的级别小于要忽略的级别，则忽略当前事件之后所有的事件
                if (currentGameEvent != null && temp != null
                        && temp.getLevel() < currentGameEvent.getLevel()
                        && temp.getLevel() < ifIgnoreLevel) {
                    clearGameEvent();
                    runEvents(callback);
                    return;
                }
                currentGameEvent = temp;
            }
            if (currentGameEvent != null && !currentGameEvent.isStart()) {
                currentGameEvent.setCallback(new GameEvent.Callback() {
                    @Override
                    public void onOver() {
                        runEvents(callback);
                    }
                });
                //如果直播入口存在，则在开始执行事件队列时，暂停直播
                try {
                    if (pushConfig != null)
                        pushConfig.stopPush();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                currentGameEvent.start();
            }
        }
    }

    /**
     * 是否有正在执行的事件
     *
     * @return 是否有正在执行的事件
     */
    public synchronized boolean haveRunningEvent() {
        return currentGameEvent != null;
    }

    /**
     * 立即结束当前事件
     */
    public synchronized void overCurrentEvent() {
        if (currentGameEvent == null || currentGameEvent.isOver()) return;
        currentGameEvent.overNow();
    }

    /**
     * @return 本次游戏的随机数生成器
     */
    protected final Random getGameRandom() {
        return mGame.gameRandom;
    }

    /**
     * 保存指定阵营(队伍)的数据和分数
     * 当hit_ij为空且hit_score为0时，为退镖操作
     *
     * @param group       指定的阵营(队伍)
     * @param group_score 阵营(队伍)分数
     * @param hit_ij      采集数据在数据表中的位置
     * @param hit_score   本次掷镖分数
     * @return 本次数据是否被有效保存
     */
    protected final boolean saveGroupScore(Group group, int group_score, HitIJ hit_ij, int hit_score) {
        return saveGroupScore(group, group_score, hit_ij, hit_score, false);
    }

    /**
     * 保存指定阵营(队伍)的数据和分数
     * 当hit_ij为空且hit_score为0时，为退镖操作
     *
     * @param group       指定的阵营(队伍)
     * @param group_score 阵营(队伍)分数
     * @param hit_ij      采集数据在数据表中的位置
     * @param hit_score   本次掷镖分数
     * @param round_death 本次数据保存之后，回合的数据是否无法只能由框架内部操作，玩家无法继续进行本回合
     * @return 本次数据是否被有效保存
     */
    protected final boolean saveGroupScore(Group group, int group_score, HitIJ hit_ij, int hit_score, boolean round_death) {
        if (hit_ij == null && hit_score == 0)
            return group.saveRetreatHit(group_score);
        else
            return group.saveOneHit(group_score, hit_ij, hit_score, round_death);
    }

    /**
     * 保存指定阵营(队伍)的分数
     *
     * @param group 指定的阵营(队伍)
     * @param score 阵营(队伍)分数
     */
    protected final void setGroupScore(Group group, int score) {
        group.setScore(score);
    }

    /**
     * 直接将指定的回合置为完成状态
     *
     * @param round 指定的回合
     */
    protected final void directRoundOver(Round round) {
        round.directOver();
    }

    /**
     * 将指定的回合所有信息都置为0分区值
     *
     * @param round           指定的回合
     * @param new_group_score 所属阵营(队伍)在本回合被置为0分后的新的分数
     */
    protected final void makeRoundZero(Round round, int new_group_score) {
        round.makeZero(new_group_score);
    }

    /**
     * 获取指定回合所属的玩家
     *
     * @param round 指定的回合
     * @return 指定回合所属的玩家
     */
    protected final Player getRoundPlayer(Round round) {
        return round.getPlayer();
    }

    /**
     * 获取指定回合所属的阵营(队伍)
     *
     * @param round 指定的回合
     * @return 指定回合所属的阵营(队伍)
     */
    protected final Group getRoundGroup(Round round) {
        return round.getPlayer().getGroup();
    }

    /**
     * 获取指定回合的掷镖数据对应的位置数据
     *
     * @param round 指定的回合
     * @return 指定回合的掷镖数据对应的位置数据
     */
    protected final HitIJ[] getRoundHitsIJArray(Round round) {
        return round.getHitsIJArray();
    }

    /**
     * 获取指定回合的每镖分数数组
     *
     * @param round 指定的回合
     * @return 指定回合的每镖分数数组
     */
    protected final int[] getRoundScoresArray(Round round) {
        return round.getScoresArray();
    }

    /**
     * 获取指定回合最后一次掷镖数据对应表中的位置
     *
     * @param round 指定的回合
     * @return 指定回合最后一次掷镖对应表中的位置
     */
    protected final HitIJ getRoundLastHitIJ(Round round) {
        return round.getLastHitIJ();
    }

    /**
     * 获取指定回合最后一次掷镖的分数
     *
     * @param round 指定的回合
     * @return 指定回合最后一次掷镖的分数
     */
    protected final int getRoundLastHitScore(Round round) {
        return round.getLastHitScore();
    }

    /**
     * 设置指定玩家的ppr
     *
     * @param player 指定的玩家
     * @param ppr    ppr的值
     */
    protected final void setPlayerPPR(Player player, float ppr) {
        player.ppr = ppr;
    }

    /**
     * 设置指定玩家的ppd
     *
     * @param player 指定的玩家
     * @param ppd    ppd的值
     */
    protected final void setPlayerPPD(Player player, float ppd) {
        player.ppd = ppd;
    }

    /**
     * 设置指定玩家的mpr
     *
     * @param player 指定的玩家
     * @param mpr    mpr的值
     */
    protected final void setPlayerMPR(Player player, float mpr) {
        player.mpr = mpr;
    }
}
