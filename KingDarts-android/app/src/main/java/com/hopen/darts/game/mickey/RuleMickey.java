package com.hopen.darts.game.mickey;

import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.HitIJ;
import com.hopen.darts.game.base.Player;
import com.hopen.darts.game.base.Round;
import com.hopen.darts.game.mickey.type.TypeRuleMickey;
import com.hopen.darts.utils.AlertMessage.VideoEnum;
import com.orhanobut.logger.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * 米老鼠游戏规则类
 */

public class RuleMickey extends GameRule<GameMickey> {

    protected RuleMickey(GameMickey game) {
        super(game);
    }

    private ScoreArea getArea(int score) {
        for (ScoreArea item : mGame.occupyScoreArea) {
            if (item.score == score)
                return item;
        }
        return null;
    }

    @Override
    public int getOriginScore() {
        return 0;
    }

    @Override
    public int getRoundNum() {
        return 15;
    }

    /**
     * 所有分区均被封死或者已打完所有回合
     *
     * @return 游戏是否结束
     */
    @Override
    public boolean isOver() {
        boolean all_area_death = true;
        for (ScoreArea area : mGame.occupyScoreArea) {
            if (!area.isDeath(mGame.getGameMode())) {
                all_area_death = false;
                break;
            }
        }
        if (all_area_death) return true;
        for (Group group : mGame.getGroupList()) {
            if (!group.isAllRoundBeDone())
                return false;
        }
        return true;
    }

    @Override
    public int groupCompare(Group group1, Group group2) {
        if (mGame.typeRuleMickey == TypeRuleMickey.BONUS)
            return group1.getScore() - group2.getScore();
        else
            return group2.getScore() - group1.getScore();
    }

    @Override
    protected boolean oneHit(Group group, HitIJ hit_ij) {
        if (mGame.typeRuleMickey == TypeRuleMickey.BONUS) {
            return bonusOneHit(group, hit_ij);
        } else {
            return standardOneHit(group, hit_ij);
        }
    }

    @Override
    protected boolean retreatHit(Group group) {
        if (mGame.typeRuleMickey == TypeRuleMickey.BONUS) {
            return bonusRetreatHit(group);
        } else {
            return standardRetreatHit(group);
        }
    }

    @Override
    protected int calculateHitScore(HitIJ hit_ij) {
        int[] hit_st = conversionHitIJToSt(hit_ij);
        return hit_st[0] * hit_st[1];
    }

    @Override
    protected void calculatePlayerInfo(Player player) {
        setPlayerPPR(player, -1);
        setPlayerPPD(player, -1);
        if (mGame.groupMickeys == null) {
            setPlayerMPR(player, 0);
        } else {
            GroupMickey group_mickey = mGame.getGroupMickey(player.getGroup().getId());
            setPlayerMPR(player, group_mickey == null ? 0 : group_mickey.getMPR());
        }
    }

    @Override
    protected void onHitOver(HitIJ hit_ij, int hit_score, boolean hit_effective, Round round, int round_position) {
        if (isEnterCrazy(mGame.getCurrentGroup())){//进入狂杀
            addVideoEvent(VideoEnum.insane_kill);
        }
    }

    @Override
    protected void onRoundOver(Round round, int round_position, boolean game_over) {
        Group group = mGame.getCurrentGroup();
        if (isEnterCrazy(mGame.getCurrentGroup())){//进入狂杀
            return;
        }
        RoundMickey round_mickey = mGame.getGroupMickey(group.getId()).rounds[round_position];
        int invalid_times = 0, one_times = 0, two_times = 0, three_time = 0;
        Set<Integer> score_set = new HashSet<>();//击中的分区set集合
        for (int i = 0; i < round_mickey.timesArray.length; i++) {
            score_set.add(getRoundHitsIJArray(round)[i].i());
            switch (round_mickey.timesArray[i]) {
                case 1:
                    one_times++;
                    break;
                case 2:
                    two_times++;
                    break;
                case 3:
                    three_time++;
                    break;
                default:
                    invalid_times++;
                    break;
            }
        }
        if (three_time == 3) {
            if (score_set.size() == getDartNumInOneRound()) {//白马
                addVideoEvent(VideoEnum.white_horse);
            } else {//⊕⊕⊕
                addVideoEvent(VideoEnum.three_treble);
            }
        } else if (three_time == 2) {
            if (invalid_times == 1) {//⊕⊕-
                addVideoEvent(VideoEnum.two_treble_one_miss);
            } else if (one_times == 1) {//⊕⊕/
                addVideoEvent(VideoEnum.two_treble_one_one);
            } else if (two_times == 1) {//⊕⊕×
                addVideoEvent(VideoEnum.two_treble_one_double);
            }
        } else if (three_time == 1) {
            if (two_times == 2) {//⊕××
                addVideoEvent(VideoEnum.one_treble_two_double);
            } else if (two_times == 1 && one_times == 1) {//⊕×/
                addVideoEvent(VideoEnum.one_treble_one_double_one_one);
            } else if (one_times == 2) {//⊕//
                addVideoEvent(VideoEnum.one_treble_two_one);
            } else if (two_times == 1 && invalid_times == 1) {//⊕×-
                addVideoEvent(VideoEnum.one_treble_one_double_one_miss);
            }
        } else if (two_times == 3) {//×××
            addVideoEvent(VideoEnum.three_double);
        } else if (invalid_times == 3) {//白板
            addVideoEvent(VideoEnum.white_board);
        }
    }

    /**
     * 加分模式一次掷镖操作
     *
     * @param group  阵营(队伍)
     * @param hit_ij 单镖数据(本次掷镖对应分区和倍区)
     * @return 本次数据是否被有效保存
     */
    private boolean bonusOneHit(Group group, HitIJ hit_ij) {
        Round round = group.getCurrentRound();
        if (round != null) {
            int[] hit_st = conversionHitIJToSt(hit_ij);
            ScoreArea score_area = getArea(hit_st[0]);
            GroupMickey group_mickey = mGame.getGroupMickey(group.getId());
            int hit_score = 0;
            //分别是游戏通用数据保存是否有效，米老鼠专用数据保存是否有效，击打分区是否有效
            boolean common_effective, mickey_effective, area_effective;
            if (score_area != null && !score_area.isDeath(mGame.getGameMode())) {//有效分区
                area_effective = true;
                if (score_area.isOccupy(group)) {//已经占领了此分区
                    score_area.occupy(group, hit_st[1]);//保存占领分区的信息
                    //分数全部算数
                    hit_score = hit_st[0] * hit_st[1];
                } else {//还未占领此分区
                    //获取此时还差多少占领此分区
                    int surplus_occupy_num = score_area.surplusOccupyNum(group);
                    //再占领分区
                    score_area.occupy(group, hit_st[1]);
                    //判断是否已经占领了此分区，且占领之后此分区是否有效
                    if (score_area.isOccupy(group) && !score_area.isDeath(mGame.getGameMode())) {
                        //占领且有效：去掉用来占领分区的那部分，剩余的是分数
                        hit_score = surplus_occupy_num >= hit_st[1] ? 0 : hit_st[0] * (hit_st[1] - surplus_occupy_num);
                    }//else 还没有占领分区或者占领之后此分区被封死，都不算分
                }
            } else {//无效分区
                area_effective = false;
                if (score_area != null)//只有在不为空(说明是15-20分区和牛眼)才保存占领分区的信息
                    score_area.occupy(group, hit_st[1]);
            }
            //保存游戏通用数据
            common_effective = saveGroupScore(group, group.getScore(), hit_ij, hit_score);
            if (common_effective) {//游戏通用数据被有效保存
                if (area_effective)
                    mickey_effective = group_mickey.oneHit(hit_st[1]);//计算并保存米老鼠专用游戏数据信息
                else
                    mickey_effective = group_mickey.oneHit(-1);//计算并保存米老鼠专用游戏数据信息
                if (mickey_effective) {//游戏数据保存成功
                    if (hit_score > 0) {//本镖分数大于零
                        //为其他没有占领此分区的阵营(队伍)加分
                        for (Group item : mGame.getGroupList()) {
                            if (item.getId() != group.getId() && !score_area.isOccupy(item)) {
                                int temp = item.getScore();
                                temp += hit_score;
                                setGroupScore(item, temp);
                            }
                        }
                    }
                } else {//游戏数据保存出现未知错误
                    Logger.e("游戏数据保存出现未知错误");
                }
            } else {//保存无效的话，要移除占领分区信息
                if (score_area != null)
                    score_area.unOccupy(group, hit_st[1]);
                mickey_effective = false;
            }
            return common_effective && mickey_effective;
        }
        return false;
    }

    /**
     * 标准模式一次掷镖操作
     *
     * @param group  阵营(队伍)
     * @param hit_ij 单镖数据(本次掷镖对应分区和倍区)
     * @return 本次数据是否被有效保存
     */
    private boolean standardOneHit(Group group, HitIJ hit_ij) {
        Round round = group.getCurrentRound();
        if (round != null) {
            int group_score = group.getScore();
            int[] hit_st = conversionHitIJToSt(hit_ij);
            ScoreArea score_area = getArea(hit_st[0]);
            GroupMickey group_mickey = mGame.getGroupMickey(group.getId());
            int hit_score = 0;
            //分别是游戏通用数据保存是否有效，米老鼠专用数据保存是否有效，击打分区是否有效
            boolean common_effective, mickey_effective, area_effective;
            if (score_area != null && !score_area.isDeath(mGame.getGameMode())) {//有效分区
                area_effective = true;
                if (score_area.isOccupy(group)) {//已经占领了此分区
                    score_area.occupy(group, hit_st[1]);//保存占领分区的信息
                    if (group_mickey.isCrazy())//此阵营(队伍)已经进入狂杀模式，不得分
                        hit_score = 0;
                    else//分数全部算数
                        hit_score = hit_st[0] * hit_st[1];
                    group_score += hit_score;
                } else {//还未占领此分区
                    //获取此时还差多少占领此分区
                    int surplus_occupy_num = score_area.surplusOccupyNum(group);
                    //再占领分区
                    score_area.occupy(group, hit_st[1]);
                    //判断是否已经占领了此分区，且占领之后此分区是否被封死
                    if (score_area.isOccupy(group) && !score_area.isDeath(mGame.getGameMode())) {
                        if (group_mickey.isCrazy())//此阵营(队伍)已经进入狂杀模式，不得分
                            hit_score = 0;
                        else//占领且有效：去掉用来占领分区的那部分，剩余的是分数
                            hit_score = surplus_occupy_num >= hit_st[1] ? 0 : hit_st[0] * (hit_st[1] - surplus_occupy_num);
                        group_score += hit_score;
                    }//else 还没有占领分区或者占领之后此分区被封死，都不算分
                }
            } else {//无效分区
                area_effective = false;
                if (score_area != null)//只有在不为空(说明是15-20分区和牛眼)才保存占领分区的信息
                    score_area.occupy(group, hit_st[1]);
            }
            //保存游戏通用数据
            common_effective = saveGroupScore(group, group_score, hit_ij, hit_score);
            if (common_effective) {//游戏通用数据被有效保存
                if (area_effective)
                    mickey_effective = group_mickey.oneHit(hit_st[1]);//计算并保存米老鼠专用游戏数据信息
                else
                    mickey_effective = group_mickey.oneHit(-1);//计算并保存米老鼠专用游戏数据信息
                if (!mickey_effective) {//游戏数据保存出现未知错误
                    Logger.e("游戏数据保存出现未知错误");
                }
            } else {//保存无效的话，要移除占领分区信息
                if (score_area != null)
                    score_area.unOccupy(group, hit_st[1]);
                mickey_effective = false;
            }
            return common_effective && mickey_effective;
        }
        return false;
    }

    /**
     * 判断一方是否进入狂杀模式
     *
     * @param group 要判断的阵营(队伍)
     * @return 是否进入狂杀模式
     */
    public boolean isCrazy(Group group) {
        return mGame.getGroupMickey(group.getId()).isCrazy();
    }

    /**
     * 判断本方是否进入狂杀模式
     * 注：只有在使本方进入狂杀模式下那一镖打完且未打下一镖时，才返回真
     *
     * @return 是否进入狂杀模式
     */
    public boolean isEnterCrazy(Group group) {
        return mGame.getGroupMickey(group.getId()).isEnterCrazy();
    }

    /**
     * 加分模式一次退镖操作
     *
     * @param group 阵营(队伍)
     * @return 本次数据是否被有效保存
     */
    private boolean bonusRetreatHit(Group group) {
        Round round = group.getCurrentRound();
        if (round != null) {
            int group_score = group.getScore();
            HitIJ hit_ij = getRoundLastHitIJ(round);
            int[] hit_st = conversionHitIJToSt(hit_ij);
            ScoreArea score_area = getArea(hit_st[0]);
            GroupMickey group_mickey = mGame.getGroupMickey(group.getId());
            int hit_score = getRoundLastHitScore(round);
            if (hit_score != 0) {
                //为其他没有占领此分区的阵营(队伍)减分
                for (Group item : mGame.getGroupList()) {
                    if (item.getId() != group.getId() && !score_area.isOccupy(item)) {
                        int temp = item.getScore();
                        temp -= hit_score;
                        setGroupScore(item, temp);
                    }
                }
            }
            if (score_area != null)
                score_area.unOccupy(group, hit_st[1]);
            return group_mickey.retreatHit() && saveGroupScore(group, group_score, null, 0);
        }
        return false;
    }

    /**
     * 标准模式一次退镖操作
     *
     * @param group 阵营(队伍)
     * @return 本次数据是否被有效保存
     */
    private boolean standardRetreatHit(Group group) {
        Round round = group.getCurrentRound();
        if (round != null) {
            int group_score = group.getScore();
            HitIJ hit_ij = getRoundLastHitIJ(round);
            int[] hit_st = conversionHitIJToSt(hit_ij);
            ScoreArea score_area = getArea(hit_st[0]);
            GroupMickey group_mickey = mGame.getGroupMickey(group.getId());
            int hit_score = getRoundLastHitScore(round);
            group_score -= hit_score;
            if (score_area != null)
                score_area.unOccupy(group, hit_st[1]);
            return group_mickey.retreatHit() && saveGroupScore(group, group_score, null, 0);
        }
        return false;
    }

    /**
     * 将掷镖数据对应数据表中的位置转化为分数和倍数
     *
     * @param hit_ij 掷镖数据对应数据表中的位置
     * @return 分数和倍数 hit_st[0]:分数，hit_st[1]:倍数(倍区)
     */
    private int[] conversionHitIJToSt(HitIJ hit_ij) {
        int[] hit_st = new int[2];
        if (hit_ij.i() < 15) {//0-14分区
            hit_st[0] = 0;
            hit_st[1] = 0;
        } else if (hit_ij.i() == 21) {//牛眼
            hit_st[0] = 25;
            if (hit_ij.j() == 0)
                hit_st[1] = 2;
            else
                hit_st[1] = 1;
        } else {//i等于15-20，对应15-20分
            hit_st[0] = hit_ij.i();
            hit_st[1] = 0;
            switch (hit_ij.j()) {
                case 0://单倍区
                case 2:
                    hit_st[1] = 1;
                    break;
                case 1://三倍区
                    hit_st[1] = 3;
                    break;
                case 3://双倍区
                    hit_st[1] = 2;
                    break;
            }
        }
        return hit_st;
    }
}
