package com.hopen.darts.game.hearts;

import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.HitIJ;
import com.hopen.darts.game.base.Round;
import com.hopen.darts.utils.AlertMessage.VideoEnum;

/**
 * 红心王游戏规则类
 */

public class RuleHearts extends GameRule<GameHearts> {
    protected RuleHearts(GameHearts game) {
        super(game);
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
                return 100;//D-BULL（内牛眼区）--100分
            else
                return 50;//S-BULL（外牛眼区）---50分
        } else {
            switch (hit_ij.j()) {
                case 0://内单倍区----------------30分
                    return 30;
                case 1://三倍区------------------20分
                    return 20;
                case 2://外单倍区---------------10分
                    return 10;
                case 3://双倍区------------------5分
                    return 5;
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
        for (int i = 0; i < hits_ij_array.length; i++) {
            HitIJ hit_ij = hits_ij_array[i];
            if (hit_ij.i() == 21) bull_num++;
            score += scores_array[i];
        }
        if (score < 50) {//鬼脸
            addVideoEvent(VideoEnum.grimace);
        } else if (1 <= bull_num && bull_num <= 2) {//牛眼
            addVideoEvent(VideoEnum.buphthalmos);
        } else if (bull_num >= 3) {//帽子戏法
            addVideoEvent(VideoEnum.hat_magic);
        }
    }
}