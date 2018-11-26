package com.hopen.darts.game.bump;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.bump.type.TypeRuleBump;

import java.util.ArrayList;
import java.util.List;

/**
 * 碰碰了游戏类
 */

public class GameBump extends BaseGame {
    TypeRuleBump typeRuleBump;

    GameBump(TypeRuleBump type_rule_bump) {
        typeRuleBump = type_rule_bump;
    }

    @Override
    public String getSeriesId() {
        return G.BUMP.SERIES_BUMP;
    }

    @Override
    public String getGameId() {
        switch (typeRuleBump) {
            case ONLINE:
                return G.BUMP.GAME_ONLINE_BUMP;
            case STANDARD:
            default:
                return G.BUMP.GAME_STANDARD_BUMP;
        }
    }

    @Override
    public boolean isOnline() {
        return typeRuleBump == TypeRuleBump.ONLINE;
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
        return new GameBump(typeRuleBump);
    }

    @Override
    protected GameRes createGameRes() {
        return new ResBump(this);
    }

    @Override
    protected GameRule createGameRule() {
        return new RuleBump(this);
    }
}