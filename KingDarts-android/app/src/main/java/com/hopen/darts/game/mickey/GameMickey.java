package com.hopen.darts.game.mickey;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.Player;
import com.hopen.darts.game.mickey.type.TypeRuleMickey;

import java.util.ArrayList;
import java.util.List;

/**
 * 米老鼠游戏类
 */

public class GameMickey extends BaseGame {
    /**
     * 游戏规则类型
     */
    TypeRuleMickey typeRuleMickey;
    /**
     * 游戏分区占领情况
     */
    ArrayList<ScoreArea> occupyScoreArea;
    /**
     * 米老鼠游戏专用队伍信息
     */
    GroupMickey[] groupMickeys;

    GameMickey(TypeRuleMickey type_rule_mickey) {
        typeRuleMickey = type_rule_mickey;
    }

    /**
     * 根据队伍id获取米老鼠专用队伍信息
     *
     * @param group_id 队伍id
     * @return 米老鼠专用队伍信息
     */
    GroupMickey getGroupMickey(int group_id) {
        if (groupMickeys == null) return null;
        for (GroupMickey item : groupMickeys) {
            if (item.group.getId() == group_id) return item;
        }
        return null;
    }

    /**
     * 根据阵营(队伍)的攻击序列来获取指定指定阵营(队伍)
     * 米老鼠专用
     *
     * @param position 攻击序列
     * @return 指定阵营(队伍)
     */
    public GroupMickey getGroupMickeyByHitPosition(int position) {
        return groupMickeys[hitPositions[position]];
    }

    /**
     * 根据阵营(队伍)在数组中的序列来获取指定指定阵营(队伍)
     * 米老鼠专用
     *
     * @param position 在数组中的序列
     * @return 指定阵营(队伍)
     */
    public GroupMickey getGroupMickeyByPosition(int position) {
        return groupMickeys[position];
    }

    /**
     * 获取当前正在掷镖的米老鼠专用队伍信息
     *
     * @return 米老鼠专用队伍信息
     */
    GroupMickey getCurrentGroupMickey() {
        return groupMickeys[hitPositions[currentGroupHitPosition]];
    }

    @Override
    public void initGame(long start_time, Player first_player) {
        super.initGame(start_time, first_player);
        occupyScoreArea = new ArrayList<>();
        occupyScoreArea.add(new ScoreArea(20));
        occupyScoreArea.add(new ScoreArea(19));
        occupyScoreArea.add(new ScoreArea(18));
        occupyScoreArea.add(new ScoreArea(17));
        occupyScoreArea.add(new ScoreArea(16));
        occupyScoreArea.add(new ScoreArea(15));
        occupyScoreArea.add(new ScoreArea(25));
        groupMickeys = new GroupMickey[groups.length];
        for (int i = 0; i < groups.length; i++) {
            groupMickeys[i] = new GroupMickey(this, groups[i]);
        }
    }

    @Override
    public Group nextCurrentGroup() {
        Group group = super.nextCurrentGroup();
        GroupMickey groupMickey = getGroupMickey(group.getId());
        if (group.getCurrentRoundPosition() != groupMickey.getCurrentRoundPosition()
                || groupMickey.isEmpty())
            groupMickey.newRound();
        return group;
    }

    @Override
    public String getSeriesId() {
        return G.MICKEY.SERIES_MICKEY;
    }

    @Override
    public String getGameId() {
        switch (typeRuleMickey) {
            case BONUS:
                return G.MICKEY.GAME_BONUS_MICKEY;
            case ONLINE:
                return G.MICKEY.GAME_ONLINE_MICKEY;
            case STANDARD:
            default:
                return G.MICKEY.GAME_STANDARD_MICKEY;
        }
    }

    @Override
    public boolean isOnline() {
        return typeRuleMickey == TypeRuleMickey.ONLINE;
    }

    @Override
    public List<GameMode> getModeList() {
        List<GameMode> list = new ArrayList<>();
        if (isOnline()) {
            list.add(GameMode.OL_TWO);
        } else if (typeRuleMickey == TypeRuleMickey.STANDARD) {
            list.add(GameMode.TWO);
        } else {
            list.add(GameMode.THREE);
            list.add(GameMode.FOUR);
        }
        return list;
    }

    @Override
    public BaseGame newIns() {
        return new GameMickey(typeRuleMickey);
    }

    @Override
    protected GameRes createGameRes() {
        return new ResMickey(this);
    }

    @Override
    protected GameRule createGameRule() {
        return new RuleMickey(this);
    }
}
