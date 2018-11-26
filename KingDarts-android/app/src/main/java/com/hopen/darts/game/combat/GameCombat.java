package com.hopen.darts.game.combat;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.Player;
import com.hopen.darts.game.combat.type.TypeRuleCombat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 拳王格斗赛游戏类
 */

public class GameCombat extends BaseGame {
    /**
     * 游戏类型
     */
    TypeRuleCombat typeRuleCombat;
    /**
     * 每人对应分区数目
     */
    final int onePlayerAreaNum = 3;
    /**
     * 玩家对应格斗信息
     */
    Fighter[] fighters;
    /**
     * 血量溢出信息保存
     */
    List<OverflowBlood> overflowBloodList;
    /**
     * 最近一次当前阵营(队伍)掷镖的结果状态：
     * 大于0说明给自己加血；
     * 小于0说明给别人减血；
     * 等于0说明此次投掷了无效分区，没有血量变动。
     * 具体数值就是变动的血量，具体减血对象对应{@link #lastBruiseGroup}
     */
    int lastCurrentGroupHitState;
    /**
     * 最近一次掉血的队伍id
     */
    Group lastBruiseGroup;

    GameCombat(TypeRuleCombat type_rule_combat) {
        typeRuleCombat = type_rule_combat;
    }

    @Override
    public void initGame(long start_time, Player first_player) {
        super.initGame(start_time, first_player);
        //初始化玩家相关格斗信息数组
        fighters = new Fighter[getGameMode().getGroupNum()];
        for (int i = 0; i < fighters.length; i++) {
            fighters[i] = new Fighter(groups[hitPositions[i]].getId(), onePlayerAreaNum);
        }
        //一共20个分区(只取1-20分区，如下为把盘上的顺序，顺时针)
        int[] area_array = {20, 1, 18, 4, 13, 6, 10, 15, 2, 17, 3, 19, 7, 16, 8, 11, 14, 9, 12, 5};
        //转化成链表
        LinkedList<Integer> area_list = new LinkedList<>();
        for (int item : area_array)
            area_list.add(item);
        //一共要取出的分区数目
        int total_area_num = onePlayerAreaNum * fighters.length;
        for (int i = 0; i < total_area_num; i++) {
            //生成随机数下标
            int index = gameRandom.nextInt(area_list.size());
            //按照顺序分配对应分区
            fighters[i / onePlayerAreaNum].scoreArea[i % onePlayerAreaNum] = area_list.get(index);
            //移除此分区防止重复
            area_list.remove(index);
            if (fighters.length <= 2) {
                //移除左右分区防止产生相邻分区
                int left = (index - 1 + area_list.size()) % area_list.size();
                area_list.remove(left);
                int right = (index + area_list.size()) % area_list.size();
                area_list.remove(right);
            }
        }
        overflowBloodList = new ArrayList<>();
    }

    /**
     * 根据id战斗信息获取对应group
     *
     * @param fighter 战斗信息
     * @return 对应group
     */
    Group getFighterGroup(Fighter fighter) {
        for (Group group : groups) {
            if (fighter.groupId == group.getId()) return group;
        }
        return null;
    }

    @Override
    public String getSeriesId() {
        return G.COMBAT.SERIES_COMBAT;
    }

    @Override
    public String getGameId() {
        switch (typeRuleCombat) {
            case ONLINE:
                return G.COMBAT.GAME_ONLINE_COMBAT;
            case STANDARD:
            default:
                return G.COMBAT.GAME_STANDARD_COMBAT;
        }
    }

    @Override
    public boolean isOnline() {
        return typeRuleCombat == TypeRuleCombat.ONLINE;
    }

    @Override
    public List<GameMode> getModeList() {
        List<GameMode> list = new ArrayList<>();
        if (isOnline()) {
            list.add(GameMode.OL_TWO);
        } else {
            list.add(GameMode.TWO);
        }
        return list;
    }

    @Override
    public BaseGame newIns() {
        return new GameCombat(typeRuleCombat);
    }

    @Override
    protected GameRes createGameRes() {
        return new ResCombat(this);
    }

    @Override
    protected GameRule createGameRule() {
        return new RuleCombat(this);
    }
}
