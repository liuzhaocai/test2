package com.hopen.darts.game.combat;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.HitIJ;
import com.hopen.darts.game.base.Round;

import java.util.List;

/**
 * 拳王格斗赛游戏规则类
 */

public class RuleCombat extends GameRule<GameCombat> {

    protected RuleCombat(GameCombat game) {
        super(game);
    }

    @Override
    public int getOriginScore() {
        return 15;
    }

    @Override
    public int getRoundNum() {
        return 8;
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
        return group2.getScore() - group1.getScore();
    }

    @Override
    protected boolean oneHit(Group group, HitIJ hit_ij) {
        //如果血量溢出信息与当前玩家不符，则先清空
        if (mGame.overflowBloodList.size() > 0 && mGame.overflowBloodList.get(0).groupId != group.getId())
            mGame.overflowBloodList.clear();
        int group_score = group.getScore();
        Round round = group.getCurrentRound();
        if (round != null) {
            //本次掷镖对应分区和倍区
            //本次掷镖分数临时变量
            int hit_score = 0;
            //本次掷镖对应的玩家格斗信息
            Fighter fighter = findFighterByIJ(hit_ij);
            if (fighter != null) {
                //在格斗信息中找到了本次掷镖对应的分区，根据倍区计算分数
                switch (hit_ij.j()) {
                    case 0://单倍区
                    case 2:
                        hit_score = 1;
                        break;
                    case 1://三倍区
                        hit_score = 3;
                        break;
                    case 3://双倍区
                        hit_score = 2;
                        break;
                }
                //如果对应的格斗信息是自己，则给自己加血
                if (fighter.groupId == group.getId()) {
                    mGame.lastCurrentGroupHitState = hit_score;
                    mGame.lastBruiseGroup = null;
                    group_score += hit_score;
                    //血量最多15
                    if (group_score > getOriginScore()) {
                        //保存血量溢出信息
                        OverflowBlood temp = new OverflowBlood();
                        temp.groupId = group.getId();
                        temp.dartPosition = round.getHitNum();
                        temp.overflowNum = group_score - getOriginScore();
                        mGame.overflowBloodList.add(temp);
                        group_score = getOriginScore();
                    }
                } else {
                    mGame.lastCurrentGroupHitState = 0 - hit_score;
                    //否则找到格斗信息对应的阵营(队伍)
                    for (Group item_group : mGame.getGroupList()) {
                        if (item_group.getId() == fighter.groupId) {
                            //找到相应对象，带数据成功保存后，给其减血
                            mGame.lastBruiseGroup = item_group;
                            break;
                        }
                    }
                }
            } else {
                //本次投掷分区没有对应任何玩家的格斗信息(玩家在拳皇中的对应分区)
                mGame.lastCurrentGroupHitState = 0;
                mGame.lastBruiseGroup = null;
            }
            boolean effective = saveGroupScore(group, group_score, hit_ij, hit_score);
            if (effective) {//数据已有效保存
                if (mGame.lastBruiseGroup != null) {
                    //给相应的阵营(队伍)减血
                    int temp = mGame.lastBruiseGroup.getScore();
                    temp -= hit_score;
                    //无需存储减到0以下血量的情况的数据，因为一旦一方血量为0，游戏就结束了
                    if (temp < 0) temp = 0;
                    setGroupScore(mGame.lastBruiseGroup, temp);
                }
            } else {//数据保存无效
                //重置以下数据
                if (mGame.overflowBloodList.size() > 0)
                    mGame.overflowBloodList.remove(mGame.overflowBloodList.size() - 1);
                mGame.lastCurrentGroupHitState = 0;
                mGame.lastBruiseGroup = null;
            }
            return effective;
        }
        return false;
    }

    @Override
    protected boolean retreatHit(Group group) {
        //如果血量溢出信息与当前玩家不符，则先清空
        if (mGame.overflowBloodList.size() > 0 && mGame.overflowBloodList.get(0).groupId != group.getId())
            mGame.overflowBloodList.clear();
        int group_score = group.getScore();
        Round round = group.getCurrentRound();
        if (round != null) {
            //本次退镖对应的分数
            int hit_score = getRoundLastHitScore(round);
            if (hit_score == 0)//0分直接退镖
                return saveGroupScore(group, group_score, null, 0);
            //本次退镖对应分区和倍区
            HitIJ hit_ij = getRoundLastHitIJ(round);
            //本次退镖对应的玩家格斗信息
            Fighter fighter = findFighterByIJ(hit_ij);
            if (fighter != null) {
                //如果对应的格斗信息是自己，则给自己取消本次加血
                if (fighter.groupId == group.getId()) {
                    //找到相应血量溢出信息，如果有则补齐
                    for (OverflowBlood item : mGame.overflowBloodList) {
                        if (item.dartPosition == round.getHitNum() - 1) {
                            group_score += item.overflowNum;
                            mGame.overflowBloodList.remove(item);
                            break;
                        }
                    }
                    group_score -= hit_score;
                } else {
                    //否则找到格斗信息对应的阵营(队伍)，取消本次掉血
                    for (Group item_group : mGame.getGroupList()) {
                        if (item_group.getId() == fighter.groupId) {
                            int temp = item_group.getScore();
                            temp += hit_score;
                            setGroupScore(item_group, temp);
                            break;
                        }
                    }
                }
            }
            return saveGroupScore(group, group_score, null, 0);
        }
        return false;
    }

    @Override
    protected void onRetreatOver(HitIJ hit_ij, int hit_score, Round round, int round_position, boolean effective) {
        if (effective) {//退镖成功后，重置最近一次格斗信息数据
            mGame.lastCurrentGroupHitState = 0;
            mGame.lastBruiseGroup = null;
        }
    }

    @Override
    protected void onGameOver() {
        addGifEvent(R.drawable.combat_ko, 10);
    }

    @Override
    protected int calculateHitScore(HitIJ hit_ij) {
        return 0;
    }

    /**
     * 根据投掷的分区获取此分区对应的玩家格斗信息
     *
     * @param hit_ij 分区坐标
     * @return 对应格斗信息
     */
    private Fighter findFighterByIJ(HitIJ hit_ij) {
        //遍历所有玩家格斗信息
        for (Fighter fighter : mGame.fighters) {
            for (int score_area : fighter.scoreArea) {
                if (score_area == hit_ij.i()) return fighter;
            }
        }
        return null;
    }
}
