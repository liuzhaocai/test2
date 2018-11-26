package com.hopen.darts.game.blow;

import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.HitIJ;
import com.hopen.darts.game.base.Round;
import com.hopen.darts.utils.AlertMessage.VideoEnum;

import java.util.HashSet;
import java.util.Set;

/**
 * 打气球游戏规则类
 */

public class RuleBlow extends GameRule<GameBlow> {

    /**
     * 使气球爆炸的阵营(队伍)id
     */
    int loseGroupId;
    /**
     * 所有玩家的总分
     */
    int totalScore;
    /**
     * 气球爆炸档位
     */
    int burstMin, burstMax, burstNow;

    protected RuleBlow(GameBlow game) {
        super(game);
        loseGroupId = -1;
        totalScore = 0;
        switch (mGame.getGameMode()) {
            case THREE:
                burstMin = 500;
                burstMax = 650;
                break;
            case FOUR:
                burstMin = 650;
                burstMax = 800;
                break;
            case TWO:
            case OL_TWO:
            default:
                burstMin = 350;
                burstMax = 500;
                break;
        }
    }

    @Override
    public int getOriginScore() {
        return 0;
    }

    @Override
    public int getRoundNum() {
        return 8;
    }

    /**
     * 打爆气球或者所有回合都已完成
     */
    @Override
    public boolean isOver() {
        if (loseGroupId != -1) return true;
        for (Group group : mGame.getGroupList()) {
            if (!group.isAllRoundBeDone())
                return false;
        }
        return true;
    }

    @Override
    public int groupCompare(Group group1, Group group2) {
        if (group1.getId() == loseGroupId) return 1;
        if (group2.getId() == loseGroupId) return -1;
        return group2.getScore() - group1.getScore();
    }

    @Override
    protected boolean oneHit(Group group, HitIJ hit_ij) {
        int group_score = group.getScore();
        Round round = group.getCurrentRound();
        if (round != null) {
            int hit_score = calculateHitScore(hit_ij);
            group_score += hit_score;
            return saveGroupScore(group, group_score, hit_ij, hit_score);
        }
        return false;
    }

    @Override
    protected boolean retreatHit(Group group) {
        int group_score = group.getScore();
        Round round = group.getCurrentRound();
        if (round != null) {
            int hit_score = getRoundLastHitScore(round);
            group_score -= hit_score;
            return saveGroupScore(group, group_score, null, 0);
        }
        return false;
    }

    @Override
    protected int calculateHitScore(HitIJ hit_ij) {
        if (hit_ij.i() == 0) {//零分区
            return 0;
        } else if (hit_ij.i() == 21) {//牛眼
            if (hit_ij.j() == 0)
                return 50;
            else
                return 25;
        } else {//i等于1-20，对应1-20分
            switch (hit_ij.j()) {
                case 0://单倍区
                case 2:
                    return hit_ij.i();
                case 1://三倍区
                    return hit_ij.i() * 3;
                case 3://双倍区
                    return hit_ij.i() * 2;
            }
        }
        return 0;
    }

    @Override
    protected void onHitOver(HitIJ hit_ij, int hit_score, boolean hit_effective, Round round, int round_position) {
        if (hit_effective) totalScore += hit_score;
        if (burstMax <= totalScore) {//爆炸
            loseGroupId = mGame.getCurrentGroup().getId();
        } else if (burstMin <= totalScore) {
            burstNow = getGameRandom().nextInt(burstMax - burstMin + 1) + burstMin;
            if (burstNow > burstMax) burstNow = burstMax;
            if (burstNow <= totalScore) {//爆炸
                loseGroupId = mGame.getCurrentGroup().getId();
            } else {
                burstMin = totalScore + 1;
            }
        }
    }

    @Override
    protected void onRetreatOver(HitIJ hit_ij, int hit_score, Round round, int round_position, boolean effective) {
        if (effective) totalScore -= hit_score;
    }

    @Override
    protected void onRoundOver(Round round, int round_position, boolean game_over) {
        if (loseGroupId != -1) return;//气球爆炸，不处理其他事件
        HitIJ[] hits_ij_array = getRoundHitsIJArray(round);
        int[] scores_array = getRoundScoresArray(round);
        int score = 0;//回合得分
        int bull_num = 0;//击中红心次数(不区分内外)
        Set<Integer> score_set = new HashSet<>();//击中的分区set集合
        Set<Integer> area_set = new HashSet<>();//击中的倍区set集合
        for (int i = 0; i < hits_ij_array.length; i++) {
            HitIJ hit_ij = hits_ij_array[i];
            score_set.add(hit_ij.i());
            area_set.add(hit_ij.j());
            if (hit_ij.i() == 21) bull_num++;
            score += scores_array[i];
        }
        if (bull_num == 3) {//帽子戏法
            addVideoEvent(VideoEnum.hat_magic);
        } else if (score == 180) {//得分180，(180必然是3in1)，所以放在前面
            addVideoEvent(VideoEnum.score_180);
        } else if (score_set.size() == 1 && area_set.size() == 1 && hits_ij_array[0].j() % 2 != 0) {//3in1
            //score_set.size() == 1说明都在一个分区，由于上一个判断，说明也肯定不是牛眼
            //area_set.size() == 1说明都在一个倍区
            //hits_ij_array[0].j()是第一个倍区，由于倍区都相等所以取第一个就行，对2取余不为0说明是奇数，即为2倍或3倍区
            addVideoEvent(VideoEnum.three_person);
        } else if (150 < score) {//WellDone
            addVideoEvent(VideoEnum.welldone);
        } else if (100 <= score && score <= 150) {//Good Throw
            addVideoEvent(VideoEnum.good_throw);
        } else if (score < 20) {//鬼脸
            addVideoEvent(VideoEnum.grimace);
        } else if (bull_num == 1) {//牛眼
            addVideoEvent(VideoEnum.buphthalmos);
        }
    }

    @Override
    protected void onGameOver() {
        if (loseGroupId != -1) {
            addVideoEvent(VideoEnum.balloon_boom, 10);
        }
    }
}
