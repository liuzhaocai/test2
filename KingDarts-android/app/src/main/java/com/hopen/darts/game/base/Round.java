package com.hopen.darts.game.base;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/**
 * 回合信息类
 * 注：关于回合类的设计上有一个问题，很纠结。问题如下：
 * 每镖投掷的数据信息(HitIJ hit_ij)和每镖对应计算出的分数(int score)要不要合并到一个类中
 * 觉得分数score应该合并到HitIJ类中，这样在掷镖、退镖的回调等操作中可以减少传参的数量，降低代码量，提高代码整洁性、易读性；
 * 但是，不合适的地方又在于，玩家掷镖操作的入口方法{@link Group#oneHit(HitIJ)}中，入参可以是一个HitIJ对象。
 * 先来解释一下{@link Group#oneHit(HitIJ)}方法，此方法代表将玩家一次掷镖的信息传入，根据靶盘数据约定和游戏规则来计算玩家本次掷镖的分数。
 * 那么问题就来了，就是说进入这个入口方法的数据应该只是玩家掷镖对应的靶盘信息，只有此方法执行完了之后才会生产出分数来。
 * 如果把分数score合并进HitIJ中，就有种玩家掷镖数据带着分数一起进入了分数计算入口方法中，有种别扭的感觉。
 * 这个问题的出现主要在于框架设计之初，并没有设计HitIJ这个类，后期为了方便给后台上报数据才有了HitIJ。。。。。
 */
public class Round {
    /**
     * 本回合对应的玩家
     */
    @JSONField(serialize = false)
    Player player;
    /**
     * 一回合内玩家的所有掷镖数据
     * 注：对应在{@link com.hopen.darts.utils.SerialPort.KeyCode#getScoreAreaCollectDataTable()}中的位置
     */
    @JSONField(serialize = false)
    HitIJ[] hitsIJArray;
    /**
     * 本回合玩家每镖的分数
     */
    @JSONField(serialize = false)
    int[] scoresArray;
    /**
     * 回合是否已经无效(无法修改)
     */
    @JSONField(serialize = false)
    boolean death;

    /**
     * 根据每回合镖数来构建一个回合对象
     *
     * @param dart_num_in_one_round 每回合镖数
     */
    Round(@NonNull Player player, int dart_num_in_one_round) {
        this.player = player;
        hitsIJArray = new HitIJ[dart_num_in_one_round];
        scoresArray = new int[dart_num_in_one_round];
        death = false;
    }

    /**
     * 获取本回合的掷镖数据对应的位置数据
     *
     * @return 本回合的掷镖数据对应的位置数据
     */
    @JSONField(serialize = false)
    HitIJ[] getHitsIJArray() {
        return hitsIJArray;
    }

    /**
     * 获取本回合的每镖分数数组
     *
     * @return 本回合的每镖分数数组
     */
    @JSONField(serialize = false)
    int[] getScoresArray() {
        return scoresArray;
    }

    /**
     * 获取本回合对应的玩家
     *
     * @return 本回合对应的玩家
     */
    @JSONField(serialize = false)
    Player getPlayer() {
        return player;
    }

    /**
     * 获取本回合最后一次掷镖数据对应表中的位置
     *
     * @return 本回合最后一次掷镖对应表中的位置
     */
    @JSONField(serialize = false)
    public HitIJ getLastHitIJ() {
        for (int i = hitsIJArray.length - 1; i >= 0; i--) {
            if (hitsIJArray[i] != null) {
                return hitsIJArray[i];
            }
        }
        return HitIJ.zero();
    }

    /**
     * 获取本回合最后一次掷镖的分数
     *
     * @return 本回合最后一次掷镖的分数
     */
    @JSONField(serialize = false)
    public int getLastHitScore() {
        for (int i = hitsIJArray.length - 1; i >= 0; i--) {
            if (hitsIJArray[i] != null) {
                return scoresArray[i];
            }
        }
        return 0;
    }

    /**
     * 保存一次掷镖采集到的数据
     *
     * @param hit_ij 采集数据在数据表中的位置
     * @param score  本次掷镖分数
     * @return 本次数据是否被有效保存
     */
    boolean oneHit(HitIJ hit_ij, int score) {
        if (isDeath()) return false;
        for (int i = 0; i < hitsIJArray.length; i++) {
            if (hitsIJArray[i] == null) {
                hitsIJArray[i] = hit_ij;
                scoresArray[i] = score;
                return true;
            }
        }
        return false;
    }

    /**
     * 执行一次退镖操作，将最后一次采集到的数据置为零
     *
     * @return 本次操作是否有效
     */
    boolean retreatHit() {
        if (isDeath()) return false;
        for (int i = hitsIJArray.length - 1; i >= 0; i--) {
            if (hitsIJArray[i] != null) {
                hitsIJArray[i] = null;
                scoresArray[i] = 0;
                return true;
            }
        }
        return false;
    }

    /**
     * 直接将本回合置为完成状态
     */
    void directOver() {
        death = true;
        for (int i = 0; i < hitsIJArray.length; i++) {
            if (hitsIJArray[i] == null) {
                player.oneHit(0);
                hitsIJArray[i] = HitIJ.zero();
                scoresArray[i] = 0;
            }
        }
    }

    /**
     * 将本回合置为0分且已完成的状态
     * 需要将本回合已经击打的每镖的分数置为0分
     * 将本回合未击打的所有镖的数据置为0分区
     * 剔除player中以保留的数据，并补齐未击打的数据
     * 重新设置本回合所属阵营(队伍)的分数，由于不同游戏有不同的计算阵营(队伍)的方法，所以需要将新的分数作为入参传入
     *
     * @param new_group_score 所属阵营(队伍)在本回合被置为0分后的新的分数
     */
    void makeZero(int new_group_score) {
        death = true;
        player.group.setScore(new_group_score);
        for (int i = 0; i < hitsIJArray.length; i++) {
            if (hitsIJArray[i] == null) {
                hitsIJArray[i] = HitIJ.zero();
            } else {
                //先将打过的镖从player的score和ppr等数据中剔除
                player.retreatHit(scoresArray[i]);
            }
            //为其添加一个0分
            player.oneHit(0);
            scoresArray[i] = 0;
        }
    }

    /**
     * 判断本回合是否已经结束
     *
     * @return 本回合是否已经结束
     */
    @JSONField(serialize = false)
    public boolean isOver() {
        for (HitIJ hit : hitsIJArray) {
            if (hit == null)
                return false;
        }
        return true;
    }

    /**
     * 回合是否已经无效(无法修改)
     *
     * @return 回合是否已经无效(无法修改)
     */
    @JSONField(serialize = false)
    public boolean isDeath() {
        return death;
    }

    /**
     * 本回合数据是否为空
     *
     * @return 本回合数据是否为空
     */
    @JSONField(serialize = false)
    public boolean isEmpty() {
        return hitsIJArray[0] == null;
    }

    /**
     * 获取本回合对应的玩家id
     *
     * @return 获取本回合对应的玩家id
     */
    @JSONField(name = "playerId")
    public int getPlayerId() {
        return player.getId();
    }

    /**
     * 获取本回合掷镖次数
     *
     * @return 本回合掷镖次数
     */
    @JSONField(name = "hitNum")
    public int getHitNum() {
        int num = 0;
        for (HitIJ hit : hitsIJArray) {
            if (hit != null)
                num++;
        }
        return num;
    }

    /**
     * 获取本回合的分数
     *
     * @return 本回合的分数
     */
    @JSONField(name = "roundScore")
    public int getScore() {
        int score = 0;
        for (int i = 0; i < hitsIJArray.length; i++) {
            if (hitsIJArray[i] != null) score += scoresArray[i];
            else break;
        }
        return score;
    }

    /**
     * 获取掷镖数据
     *
     * @return 掷镖数据
     */
    @JSONField(name = "hitData")
    public List<HitIJ> getHitDataButNull() {
        ArrayList<HitIJ> list = new ArrayList<>();
        for (HitIJ hit_ij : hitsIJArray) {
            if (hit_ij != null) list.add(hit_ij);
        }
        return list;
    }

    /**
     * 获取某一镖的分数
     *
     * @param position 第几镖(从0开始)
     * @return 分数
     */
    public int getHitScore(@IntRange(from = 0) int position) {
        if (position < 0 || scoresArray.length <= position) return 0;
        return scoresArray[position];
    }

    /**
     * 获取某一镖的分数
     *
     * @param position 第几镖(从0开始)
     * @return 分数
     */
    public HitIJ getHitIJ(@IntRange(from = 0) int position) {
        if (position < 0 || scoresArray.length <= position) return null;
        return hitsIJArray[position];
    }
}
