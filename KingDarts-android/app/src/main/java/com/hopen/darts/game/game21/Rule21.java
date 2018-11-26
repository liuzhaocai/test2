package com.hopen.darts.game.game21;

import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.HitIJ;
import com.hopen.darts.game.base.Round;
import com.hopen.darts.utils.AlertMessage.VideoEnum;

import java.util.List;

/**
 * Created by thomas on 2018/7/2.
 */

public class Rule21 extends GameRule<Game21> {
    protected Rule21(Game21 game) {
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

    /**
     * 完成了所有回合或者有一个人打到了21分
     */
    @Override
    public boolean isOver() {
        List<Group> group_list = mGame.getGroupList();
        boolean all_over = true;
        for (int i = 0; i < group_list.size(); i++) {
            Group group = group_list.get(i);
            if (group.getScore() == 21) return true;
            all_over = all_over && group.getNowRoundNum() == getRoundNum();
        }
        return all_over;
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
            boolean baobiao = false;
            if (group_score + hit_score > 21) {
                hit_score = 0;
                baobiao = true;
            }
            group_score += hit_score;
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
        if (round.isDeath()) {//爆镖
            addVideoEvent(VideoEnum.bust);
            makeRoundZero(round, getRoundGroup(round).getScore() - round.getScore());
        }
    }
}