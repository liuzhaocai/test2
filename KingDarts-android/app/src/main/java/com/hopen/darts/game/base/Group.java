package com.hopen.darts.game.base;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;
import com.hopen.darts.utils.Sound.SoundEnum;
import com.hopen.darts.utils.Sound.SoundPlayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 阵营(队伍)
 */

public class Group implements Comparable<Group> {
    /**
     * 本次比赛的游戏
     */
    @JSONField(serialize = false)
    BaseGame mGame;
    /**
     * 本次比赛中，本阵营(队伍)的唯一标识
     */
    @JSONField(serialize = false)
    int id;
    /**
     * 本次比赛中，本阵营(队伍)的玩家数组
     */
    @JSONField(serialize = false)
    Player[] players;
    /**
     * 本次比赛中，本阵营(队伍)的玩家攻击顺序(存储玩家在{@link #players}中的position)
     */
    @JSONField(serialize = false)
    int[] hitPositions;
    /**
     * 本次比赛中，本阵营(队伍)的回合数组
     */
    @JSONField(serialize = false)
    Round[] rounds;
    /**
     * 本次比赛中，本阵营(队伍)已计算过的掷镖数据数量
     */
    @JSONField(serialize = false)
    int calculateHitNum;
    /**
     * 本次比赛中，本阵营(队伍)的得分
     */
    @JSONField(serialize = false)
    int score;
    /**
     * 是否正在掷镖
     */
    @JSONField(serialize = false)
    volatile boolean isHitting = false;
    /**
     * 是否正在退镖
     */
    @JSONField(serialize = false)
    volatile boolean isRetreating = false;
    /**
     * 上次掷镖时间
     */
    @JSONField(serialize = false)
    volatile long lastHitTime = 0;

    /**
     * 阵营(队伍)构造方法
     *
     * @param game           一队中的玩家数量
     * @param group_position 阵营(队伍)序列(从1开始)
     */
    Group(BaseGame game, @IntRange(from = 1) int group_position) {
        mGame = game;
        id = group_position;
        //一队中的玩家数量 = 总玩家数量 / 阵营(队伍)数量
        int player_num_in_group = mGame.gameMode.getPlayerNumInGroup();
        players = new Player[player_num_in_group];
        //本队中末尾玩家的position = 每队中的玩家数量 * 阵营(队伍)的position
        //例：3v3的情况，每队3人，第二队的最后一个玩家position就是：3 * 2 = 6
        int end_position = group_position * player_num_in_group;
        for (int i = 0; i < player_num_in_group; i++) {
            //实例化本队中的每个玩家，从第一个开始
            //首位玩家position = 末尾玩家position - 一队中的玩家数量 + 1
            players[i] = new Player(this, end_position - player_num_in_group + i + 1);
        }
    }

    /**
     * 初始化阵营(队伍)的基本数据
     *
     * @param player 本次游戏的先攻玩家
     */
    boolean initGroup(Player player) {
        boolean first = false;
        hitPositions = new int[players.length];
        for (int i = 0; i < players.length; i++) {
            players[i].calculatePPRPPD();
            hitPositions[i] = i;
        }
        if (player != null) {
            for (int i = 0; i < players.length; i++) {
                Player item = players[i];
                if (item.getId() == player.getId()) {
                    int temp = hitPositions[0];
                    hitPositions[0] = hitPositions[i];
                    hitPositions[i] = temp;
                    first = true;
                }
            }
        }
        rounds = new Round[mGame.getGameRule().getRoundNum()];
        score = mGame.getGameRule().getOriginScore();
        calculateHitNum = 0;
        return first;
    }

    /**
     * 开始一个新的回合
     */
    void newRound() {
        for (int i = 0; i < rounds.length; i++) {
            int temp = i % players.length;
            if (rounds[i] == null) {
                rounds[i] = new Round(players[hitPositions[temp]], mGame.getGameRule().getDartNumInOneRound());
                return;
            }
        }
    }

    /**
     * 设置本阵营(队伍)的分数
     *
     * @param score 本阵营(队伍)的分数
     */
    void setScore(int score) {
        this.score = score;
    }

    /**
     * 获取阵营(队伍)当前回合数
     *
     * @return 阵营(队伍)当前回合数
     */
    @JSONField(serialize = false)
    public int getNowRoundNum() {
        for (int i = 0; i < rounds.length; i++) {
            if (rounds[i] == null) return i;
        }
        return rounds.length;
    }

    /**
     * 获取阵营(队伍)当前的回合对应的position
     *
     * @return 阵营(队伍)当前的回合对应的position
     */
    @JSONField(serialize = false)
    public int getCurrentRoundPosition() {
        //从后向前遍历，找到第一个不为空的回合对象
        for (int i = rounds.length - 1; i >= 0; i--) {
            if (rounds[i] != null) return i;
        }
        return 0;
    }

    /**
     * 获取阵营(队伍)当前的回合对象
     *
     * @return 阵营(队伍)当前的回合对象
     */
    @JSONField(serialize = false)
    public Round getCurrentRound() {
        return rounds[getCurrentRoundPosition()];
    }

    /**
     * 获取本阵营(队伍)的回合列表
     *
     * @return 本阵营(队伍)的回合列表
     */
    @JSONField(serialize = false)
    public List<Round> getAllRound() {
        return Arrays.asList(rounds);
    }

    /**
     * 获取阵营(队伍)当前的玩家对象对应的攻击position
     *
     * @return 阵营(队伍)当前的玩家对象对应的攻击position
     */
    @JSONField(serialize = false)
    public int getCurrentPlayerHitPosition() {
        Player current_player = getCurrentPlayer();
        for (int i = 0; i < players.length; i++) {
            if (players[hitPositions[i]].getId() == current_player.getId())
                return i;
        }
        return 0;
    }

    /**
     * 获取阵营(队伍)当前的玩家对象
     *
     * @return 阵营(队伍)当前的玩家对象
     */
    @JSONField(serialize = false)
    public Player getCurrentPlayer() {
        Round current_round = getCurrentRound();
        if (current_round == null || current_round.getPlayer() == null)
            return players[0];
        else
            return current_round.getPlayer();
    }

    /**
     * 获取本次队伍玩家列表，按照攻击顺序排列
     *
     * @return 本次队伍玩家列表
     */
    @JSONField(serialize = false)
    public List<Player> getPlayerListOrderByHit() {
        List<Player> player_list = new ArrayList<>();
        for (int i = 0; i < players.length; i++) {
            Player player = players[hitPositions[i]];
            player_list.add(player);
        }
        return player_list;
    }

    /**
     * 所有回合是否均已完成
     *
     * @return 所有回合是否均已完成
     */
    @JSONField(serialize = false)
    public boolean isAllRoundBeDone() {
        return rounds[rounds.length - 1] != null && rounds[rounds.length - 1].isOver();
    }

    /**
     * 执行一次掷镖操作
     *
     * @param collected_data 采集到的数据
     */
    public synchronized void oneHit(long collected_data) {
        oneHit(GameRule.analysisCollectedData(collected_data));
    }

    /**
     * 执行一次掷镖操作
     *
     * @param hit_ij 单镖数据(本次掷镖对应分区和倍区)
     */
    public synchronized void oneHit(HitIJ hit_ij) {
        if (!isHitting && !isRetreating && !mGame.getGameRule().haveRunningEvent()) {
            isHitting = true;
            int current_position = getCurrentRoundPosition();
            Round current = rounds[current_position];
            //每镖投掷的间隔最小允许为800毫秒
            long now = System.currentTimeMillis();
            if (hit_ij == null || current == null || current.isOver() || current.isDeath() || now - lastHitTime < 800) {
                isHitting = false;
                return;
            }
            lastHitTime = now;
            if (hit_ij.i() == 21) {//牛眼
                SoundPlayUtils.play(SoundEnum.SHOOT_CENTER_SCORE);
            } else if (hit_ij.i() != 0) {//i等于1-20，对应1-20分区
                switch (hit_ij.j()) {
                    case 0://单倍区
                    case 2:
                        SoundPlayUtils.play(SoundEnum.SHOOT_ONE_SCORE);
                        break;
                    case 1://三倍区
                        SoundPlayUtils.play(SoundEnum.SHOOT_TER_SCORE);
                        break;
                    case 3://双倍区
                        SoundPlayUtils.play(SoundEnum.SHOOT_DOUBLE_SCORE);
                        break;
                }
            }
            boolean effective = mGame.getGameRule().oneHit(this, hit_ij);
            mGame.getGameRule().onParentHitOver(hit_ij, effective ? current.getLastHitScore() : 0,
                    effective, current, current_position);
        }
    }

    /**
     * 保存一次掷镖数据和分数
     *
     * @param group_score 阵营(队伍)分数
     * @param hit_ij      采集数据在数据表中的位置
     * @param hit_score   本次掷镖分数
     * @param round_death 本次数据保存之后，回合的数据是否无法继续由玩家操作
     * @return 本次数据是否被有效保存
     */
    boolean saveOneHit(int group_score, HitIJ hit_ij, int hit_score, boolean round_death) {
        int current_position = getCurrentRoundPosition();
        Round current = rounds[current_position];
        if (current != null) {
            boolean effective = current.oneHit(hit_ij, hit_score);
            if (effective) {
                score = group_score;
                getCurrentPlayer().oneHit(hit_score);
            }
            current.death = round_death;
            return effective;
        }
        return false;
    }

    /**
     * 执行一次退镖操作
     *
     * @return 本次操作是否有效
     */
    public synchronized void retreatHit() {
        if (!isHitting && !isRetreating && !mGame.getGameRule().haveRunningEvent()) {
            isRetreating = true;
            int current_position = getCurrentRoundPosition();
            Round current = rounds[current_position];
            if (current == null || current.isDeath() || current.isEmpty()) {
                isRetreating = false;
                return;
            }
            HitIJ last_hit_ij = current.getLastHitIJ();
            int last_hit_score = current.getLastHitScore();
            boolean effective = mGame.getGameRule().retreatHit(this);
            mGame.getGameRule().onParentRetreatOver(effective ? last_hit_ij : null,
                    effective ? last_hit_score : 0,
                    current, current_position, effective);
        }
    }

    /**
     * 保存一次退镖操作，将最后一次采集到的数据置为零
     *
     * @param group_score 阵营(队伍)分数
     * @return 本次数据是否被有效保存
     */
    boolean saveRetreatHit(int group_score) {
        int current_position = getCurrentRoundPosition();
        Round current = rounds[current_position];
        if (current != null) {
            int hit_score = current.getLastHitScore();
            boolean effective = current.retreatHit();
            if (effective) {
                score = group_score;
                getCurrentPlayer().retreatHit(hit_score);
            }
            return effective;
        }
        return false;
    }

    /**
     * 获取指定position的回合
     *
     * @param position 指定position
     * @return 回合
     */
    public Round getRound(int position) {
        if (position < 0 || rounds.length <= position)
            return null;
        return rounds[position];
    }

    /**
     * 获取阵营(队伍)id
     *
     * @return 阵营(队伍)id
     */
    @JSONField(name = "groupId")
    public int getId() {
        return id;
    }

    /**
     * 获取阵营(队伍)当前分数
     *
     * @return 阵营(队伍)当前分数
     */
    @JSONField(name = "groupScore")
    public int getScore() {
        return score;
    }

    /**
     * 获取本阵营(队伍)的玩家列表
     *
     * @return 本阵营(队伍)的玩家列表
     */
    @JSONField(name = "playerList")
    public List<Player> getPlayerList() {
        return Arrays.asList(players);
    }

    /**
     * 获取本阵营(队伍)的回合列表，不包含null
     *
     * @return 本阵营(队伍)的回合列表，不包含null
     */
    @JSONField(name = "roundList")
    public List<Round> getRoundListButNull() {
        ArrayList<Round> list = new ArrayList<>();
        for (Round round : rounds) {
            if (round != null && !round.isEmpty())
                list.add(round);
        }
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Group) {
            Group obj_group = (Group) obj;
            return this == obj_group || obj_group.id == this.id;
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull Group group) {
        if (mGame == null || mGame.getGameRule() == null) return 0;
        return mGame.getGameRule().groupCompare(this, group);
    }
}
