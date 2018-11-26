package com.hopen.darts.game.game21;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.game21.type.TypeRule21;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 2018/7/2.
 */

public class Game21 extends BaseGame {
    TypeRule21 typeRule21;

    Game21(TypeRule21 type_rule_21) {
        typeRule21 = type_rule_21;
    }

    @Override
    public String getSeriesId() {
        return G._21.SERIES_21;
    }

    @Override
    public String getGameId() {
        switch (typeRule21) {
            case ONLINE:
                return G._21.GAME_ONLINE_21;
            case STANDARD:
            default:
                return G._21.GAME_STANDARD_21;
        }
    }

    @Override
    public boolean isOnline() {
        return typeRule21 == TypeRule21.ONLINE;
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
        return new Game21(typeRule21);
    }

    @Override
    protected GameRes createGameRes() {
        return new Res21(this);
    }

    @Override
    protected GameRule createGameRule() {
        return new Rule21(this);
    }
}
