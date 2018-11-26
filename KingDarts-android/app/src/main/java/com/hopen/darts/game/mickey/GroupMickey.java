package com.hopen.darts.game.mickey;

import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.mickey.type.TypeRuleMickey;

/**
 * 米老鼠用阵营(队伍)类
 */

public class GroupMickey {
    Group group;//游戏数据对应的阵营(队伍)对象
    GameMickey mGame;//游戏对象
    RoundMickey[] rounds;//米老鼠专用回合数据
    int roundNum;//回合数量
    int totalTimes;//击中倍区值总和
    float mpr;//米老鼠专用，每回合击中的倍区平均值
    private int crazyFlag;//狂杀状态标识，默认为零，每打一镖如果在狂杀状态便自增1

    GroupMickey(GameMickey game, Group group) {
        this.group = group;
        this.mGame = game;
        rounds = new RoundMickey[mGame.getGameRule().getRoundNum()];
        roundNum = 0;
        totalTimes = 0;
        mpr = 0;
        crazyFlag = 0;
    }

    /**
     * 开始一个新的回合
     */
    void newRound() {
        for (int i = 0; i < rounds.length; i++) {
            if (rounds[i] == null) {
                rounds[i] = new RoundMickey(mGame.getGameRule().getDartNumInOneRound());
                roundNum++;
                return;
            }
        }
    }

    /**
     * 回合数据是否为空
     *
     * @return 是否为空
     */
    boolean isEmpty() {
        return rounds[0] == null;
    }

    /**
     * 获取阵营(队伍)当前的回合对应的position
     *
     * @return 阵营(队伍)当前的回合对应的position
     */
    int getCurrentRoundPosition() {
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
    RoundMickey getCurrentRound() {
        return rounds[getCurrentRoundPosition()];
    }

    /**
     * 添加一次掷镖数据
     *
     * @param times 几倍区
     * @return 此次添加是否有效
     */
    boolean oneHit(int times) {
        RoundMickey last = getCurrentRound();
        boolean effective = last != null && last.add(times);
        if (effective && times > 0) {//只有在有效保存且分数大于0时才计算，节省资源
            totalTimes += times;
            mpr = (float) totalTimes / roundNum + 0.5f;
        }
        //判断狂杀状态
        if (mGame.typeRuleMickey != TypeRuleMickey.BONUS) {
            boolean is_crazy = false;
            for (Group item : mGame.getGroupList()) {
                if (group.getScore() - item.getScore() >= 200) {
                    is_crazy = true;
                    crazyFlag++;
                    break;
                }
            }
            if (!is_crazy) crazyFlag = 0;
        }
        return effective;
    }

    /**
     * 进行一次退镖操作
     *
     * @return 此次退镖是否有效
     */
    boolean retreatHit() {
        RoundMickey last = getCurrentRound();
        int last_times = 0;
        boolean effective = false;
        if (last != null) {
            last_times = last.getLastHitTimes();
            effective = last.remove();
        }
        if (effective && last_times > 0) {//只有在有效保存且分数大于0时才计算，节省资源
            totalTimes -= last_times;
            mpr = (float) totalTimes / roundNum + 0.5f;
        }
        //判断狂杀状态
        if (mGame.typeRuleMickey != TypeRuleMickey.BONUS) {
            boolean is_crazy = false;
            for (Group item : mGame.getGroupList()) {
                if (group.getScore() - item.getScore() >= 200) {
                    is_crazy = true;
                    crazyFlag--;
                    break;
                }
            }
            if (!is_crazy) crazyFlag = 0;
        }
        return effective;
    }

    float getMPR() {
        return mpr;
    }

    /**
     * 判断本方是否在狂杀模式中
     *
     * @return 是否在狂杀模式中
     */
    public boolean isCrazy() {
        return crazyFlag > 0;
    }

    /**
     * 判断本方是否进入狂杀模式
     * 注：只有在使本方进入狂杀模式下那一镖打完且未打下一镖时，才返回真
     *
     * @return 是否进入狂杀模式
     */
    public boolean isEnterCrazy() {
        return crazyFlag == 1;
    }
}
