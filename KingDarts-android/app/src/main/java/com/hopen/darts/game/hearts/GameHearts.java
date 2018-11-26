package com.hopen.darts.game.hearts;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.hearts.type.TypeRuleHearts;

import java.util.ArrayList;
import java.util.List;

/**
 * 红心王游戏类
 */

public class GameHearts extends BaseGame {
    TypeRuleHearts typeRuleHearts;

    GameHearts(TypeRuleHearts type_rule_hearts) {
        typeRuleHearts = type_rule_hearts;
    }

    @Override
    public String getSeriesId() {
        return G.HEARTS.SERIES_HEARTS;
    }

    @Override
    public String getGameId() {
        switch (typeRuleHearts) {
            case ONLINE:
                return G.HEARTS.GAME_ONLINE_HEARTS;
            case STANDARD:
            default:
                return G.HEARTS.GAME_STANDARD_HEARTS;
        }
    }

    @Override
    public boolean isOnline() {
        return typeRuleHearts == TypeRuleHearts.ONLINE;
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
        }
        return list;
    }

    @Override
    public BaseGame newIns() {
        return new GameHearts(typeRuleHearts);
    }

    @Override
    protected GameRes createGameRes() {
        return new ResHearts(this);
    }

    @Override
    protected GameRule createGameRule() {
        return new RuleHearts(this);
    }
}