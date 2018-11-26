package com.hopen.darts.game.blow;

import com.hopen.darts.game.G;
import com.hopen.darts.game.blow.type.TypeRuleBlow;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.GameRule;

import java.util.ArrayList;
import java.util.List;

/**
 * 打气球游戏类
 */

public class GameBlow extends BaseGame {
    TypeRuleBlow typeRuleBlow;

    GameBlow(TypeRuleBlow type_rule_blow) {
        typeRuleBlow = type_rule_blow;
    }

    @Override
    public String getSeriesId() {
        return G.BLOW.SERIES_BLOW;
    }

    @Override
    public String getGameId() {
        switch (typeRuleBlow) {
            case ONLINE:
                return G.BLOW.GAME_ONLINE_BLOW;
            case STANDARD:
            default:
                return G.BLOW.GAME_STANDARD_BLOW;
        }
    }

    @Override
    public boolean isOnline() {
        return typeRuleBlow == TypeRuleBlow.ONLINE;
    }

    @Override
    public List<GameMode> getModeList() {
        List<GameMode> list = new ArrayList<>();
        if (isOnline()) {
            list.add(GameMode.OL_TWO);
        } else {
            list.add(GameMode.TWO);
            list.add(GameMode.THREE);
            list.add(GameMode.FOUR);
        }
        return list;
    }

    @Override
    public BaseGame newIns() {
        return new GameBlow(typeRuleBlow);
    }

    @Override
    protected GameRes createGameRes() {
        return new ResBlow(this);
    }

    @Override
    protected GameRule createGameRule() {
        return new RuleBlow(this);
    }
}
