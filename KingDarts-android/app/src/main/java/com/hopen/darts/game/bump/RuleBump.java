package com.hopen.darts.game.bump;

import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.HitIJ;
import com.hopen.darts.game.base.Round;

/**
 * 碰碰乐游戏规则类
 */

public class RuleBump extends GameRule<GameBump> {
    private final int red = -1;
    private final int green = -2;
    private final int black = -3;
    private final int white = -4;
    private final int bull = -5;
    private RoundBump currentRoundBump;

    protected RuleBump(GameBump game) {
        super(game);
        currentRoundBump = new RoundBump(getDartNumInOneRound());
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
        if (round != null && !round.isOver()) {
            int hit_score = calculateHitScore(hit_ij);
            currentRoundBump.add(hit_score);
            if (currentRoundBump.isOver())
                hit_score = calculateRoundBumpScore();
            else hit_score = 0;
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
            return bull;
        } else {//i等于1-20，对应1-20分
            switch (hit_ij.i()) {
                case 20://黑、红
                case 18:
                case 13:
                case 10:
                case 2:
                case 3:
                case 7:
                case 8:
                case 14:
                case 12:
                    switch (hit_ij.j()) {
                        case 0://单倍区
                        case 2:
                            return black;
                        case 1://三倍区
                        case 3://双倍区
                            return red;
                    }
                    break;
                case 1://绿、白
                case 4:
                case 6:
                case 15:
                case 17:
                case 19:
                case 16:
                case 11:
                case 9:
                case 5:
                    switch (hit_ij.j()) {
                        case 0://单倍区
                        case 2:
                            return white;
                        case 1://三倍区
                        case 3://双倍区
                            return green;
                    }
                    break;
            }
        }
        return 0;
    }

    private int calculateRoundBumpScore() {
        int red_num = 0;
        int green_num = 0;
        int black_num = 0;
        int white_num = 0;
        int bull_num = 0;
        for (int item : currentRoundBump.flagArray) {
            switch (item) {
                case red:
                    red_num++;
                    break;
                case green:
                    green_num++;
                    break;
                case black:
                    black_num++;
                    break;
                case white:
                    white_num++;
                    break;
                case bull:
                    bull_num++;
                    break;
            }
        }
        if (red_num >= 3 || green_num >= 3 || bull_num >= 3)
            return 100;
        else if (black_num >= 3 || white_num >= 3)
            return 50;
        else if (red_num == 2 || green_num == 2)
            return 50;
        else if (black_num == 2 || white_num == 2)
            return 25;
        else
            return bull_num * 25;
    }
}