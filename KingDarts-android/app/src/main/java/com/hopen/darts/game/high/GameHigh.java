package com.hopen.darts.game.high;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.high.type.TypeRuleHigh;

import java.util.ArrayList;
import java.util.List;

/**
 * 高分赛游戏类
 */

public class GameHigh extends BaseGame {
    TypeRuleHigh typeRuleHigh;

    GameHigh(TypeRuleHigh type_rule_high) {
        typeRuleHigh = type_rule_high;
    }

    @Override
    public String getSeriesId() {
        return G.HIGH.SERIES_HIGH;
    }

    @Override
    public String getGameId() {
        switch (typeRuleHigh) {
            case ONLINE:
                return G.HIGH.GAME_ONLINE_HIGH;
            case STANDARD:
            default:
                return G.HIGH.GAME_STANDARD_HIGH;
        }
    }

    @Override
    public boolean isOnline() {
        return typeRuleHigh == TypeRuleHigh.ONLINE;
    }

    @Override
    public List<GameMode> getModeList() {
        List<GameMode> list = new ArrayList<>();
        if (isOnline()) {
            list.add(GameMode.OL_TWO);
        } else {
            list.add(GameMode.ONE);
            list.add(GameMode.TWO);
            list.add(GameMode.THREE);
            list.add(GameMode.FOUR);
            list.add(GameMode._2V2);
            list.add(GameMode._3V3);
        }
        return list;
    }

    @Override
    public BaseGame newIns() {
        return new GameHigh(typeRuleHigh);
    }

    @Override
    protected GameRes createGameRes() {
        return new ResHigh(this);
    }

    @Override
    protected GameRule createGameRule() {
        return new RuleHigh(this);
    }
}
