package com.hopen.darts.game.rich;

import android.support.annotation.IntRange;
import android.util.SparseArray;

import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.HitIJ;
import com.hopen.darts.game.base.Round;
import com.hopen.darts.utils.AlertMessage.VideoEnum;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 全民大富豪游戏规则类
 */

public class RuleRich extends GameRule<GameRich> {
    /**
     * 全部幸运分值
     */
    private final int[] allLuckScore = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 24, 25, 26, 27, 28, 30, 32, 33, 34, 36, 38, 39, 40, 42, 45, 48, 50, 51, 54, 57, 60};

    protected RuleRich(GameRich game) {
        super(game);
        mGame.attach = new Attach();
        mGame.attach.init(this);
    }

    /**
     * 根据vip等级生成幸运分区数目
     *
     * @param vip_level vip等级
     * @return 幸运分区数目
     */
    private int getLuckNum(@IntRange(from = 1, to = 9) int vip_level) {
        switch (vip_level) {
            case 1:
                return 3;
            case 2:
                return 6;
            case 3:
                return 9;
            case 4:
                return 12;
            case 5:
                return 15;
            case 6:
                return 18;
            case 7:
                return 21;
            case 8:
                return 28;
            case 9:
                return 35;
        }
        return 0;
    }

    /**
     * 根据幸运分区数目生成幸运分区数组
     *
     * @param vip_level vip等级
     * @return 幸运分区数组
     */
    SparseArray<LuckScore> createLuckScores(@IntRange(from = 1, to = 9) int vip_level) {
        //幸运分区数目
        int luck_num = getLuckNum(vip_level);
        //构建所有幸运分区链表
        LinkedList<Integer> all_luck_score = new LinkedList<>();
        for (int item : allLuckScore)
            all_luck_score.add(item);
        //根据幸运分区数据随机筛选幸运分区，并构建幸运分区SparseArray
        SparseArray<LuckScore> luck_scores = new SparseArray<>();
        for (int i = 0; i < luck_num; i++) {
            int index = getGameRandom().nextInt(all_luck_score.size());
            int score = all_luck_score.get(index);
            luck_scores.put(score, new LuckScore(score));
            all_luck_score.remove(index);
        }
        return luck_scores;
    }

    @Override
    public int getOriginScore() {
        return 0;
    }

    @Override
    public int getRoundNum() {
        return 8;
    }

    @Override
    public boolean isOver() {
        for (Group group : mGame.getGroupList()) {
            if (!group.isAllRoundBeDone())
                return false;
        }
        return true;
    }

    @Override
    public int groupCompare(Group group1, Group group2) {
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
    protected void onHitOver(HitIJ hit_ij, int hit_score, boolean hit_effective, Round round, int round_position) {
        if (hit_effective) mGame.attach.oneHit(hit_score);
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
    protected void onRetreatOver(HitIJ hit_ij, int hit_score, Round round, int round_position, boolean effective) {
        if (effective) mGame.attach.retreatHit(hit_score);
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
    protected void onRoundOver(Round round, int round_position, boolean game_over) {
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
