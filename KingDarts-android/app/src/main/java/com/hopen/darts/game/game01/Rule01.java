package com.hopen.darts.game.game01;

import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.HitIJ;
import com.hopen.darts.game.base.Round;
import com.hopen.darts.game.game01.type.TypeGame01;
import com.hopen.darts.game.game01.type.TypeRule01;
import com.hopen.darts.utils.AlertMessage.VideoEnum;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 01系列游戏规则类
 */

public class Rule01 extends GameRule<Game01> {

    Rule01(Game01 game) {
        super(game);
    }

    @Override
    public int getOriginScore() {
        switch (mGame.typeGame01) {
            case _501:
                return 501;
            case _701:
                return 701;
            case _301:
            default:
                return 301;
        }
    }

    @Override
    public int getRoundNum() {
        switch (mGame.typeGame01) {
            case _501:
            case _701:
                return 15;
            case _301:
            default:
                return 8;
        }
    }

    /**
     * 完成了所有回合或者有一个人打到了0分
     */
    @Override
    public boolean isOver() {
        List<Group> group_list = mGame.getGroupList();
        boolean all_over = true;
        for (int i = 0; i < group_list.size(); i++) {
            Group group = group_list.get(i);
            if (group.getScore() == 0) return true;
            all_over = all_over && group.isAllRoundBeDone();
        }
        return all_over;
    }

    @Override
    public int groupCompare(Group group1, Group group2) {
        return group1.getScore() - group2.getScore();
    }

    @Override
    protected boolean oneHit(Group group, HitIJ hit_ij) {
        int group_score = group.getScore();
        Round round = group.getCurrentRound();
        if (round != null) {
            int hit_score = calculateHitScore(hit_ij);
            boolean baobiao = false;
            if (hit_score != 0) {
                int remainder = group_score - hit_score;
                if (remainder < 0) {//小于0分均为爆镖
                    hit_score = 0;
                    baobiao = true;
                } else if (mGame.typeRule01 != TypeRule01.DISPORT) {//标准和网络模式
                    if (remainder == 1) {//剩余1分，爆镖
                        hit_score = 0;
                        baobiao = true;
                    } else if (remainder == 0 && !(hit_ij.i() == 21 || hit_ij.j() == 3 || (hit_ij.j() == 1 && mGame.typeGame01 == TypeGame01._701))) {
                        //剩余0分，且最后一次不是双倍，牛眼，或者在701时不是三倍
                        hit_score = 0;
                        baobiao = true;
                    }
                }
            }
            group_score -= hit_score;
            return saveGroupScore(group, group_score, hit_ij, hit_score, baobiao);
        }
        return false;
    }

    @Override
    protected boolean retreatHit(Group group) {
        int group_score = group.getScore();
        Round round = group.getCurrentRound();
        if (round != null && !round.isDeath()) {
            int hit_score = getRoundLastHitScore(round);
            group_score += hit_score;
            return saveGroupScore(group, group_score, null, 0);
        }
        return false;
    }

    @Override
    protected int calculateHitScore(HitIJ hit_ij) {
        if (hit_ij.i() == 0) {//零分区
            return 0;
        } else if (hit_ij.i() == 21) {//牛眼
            return 50;
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
        if (round.isDeath()) {//爆镖
            addVideoEvent(VideoEnum.bust);
//            directRoundOver(round);//老版规则
            makeRoundZero(round, getRoundGroup(round).getScore() + round.getScore());//新版规则
        }
    }

    @Override
    protected void onRoundOver(Round round, int round_position, boolean game_over) {
        if (round.isDeath()) return;//爆镖
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
}
