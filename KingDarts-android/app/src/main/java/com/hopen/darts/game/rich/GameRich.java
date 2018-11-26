package com.hopen.darts.game.rich;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.GameRule;

import java.util.ArrayList;
import java.util.List;

/**
 * 全民大富豪游戏类
 */

public class GameRich extends BaseGame {
    Attach attach;

    @Override
    public String getSeriesId() {
        return G.RICH.SERIES_RICH;
    }

    @Override
    public String getGameId() {
        return G.RICH.GAME_RICH;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public List<GameMode> getModeList() {
        List<GameMode> list = new ArrayList<>();
        list.add(GameMode.ONE);
        list.add(GameMode.TWO);
        list.add(GameMode.THREE);
        list.add(GameMode.FOUR);
        return list;
    }

    @Override
    public BaseGame newIns() {
        return new GameRich();
    }

    @Override
    protected GameRes createGameRes() {
        return new ResRich(this);
    }

    @Override
    protected GameRule createGameRule() {
        return new RuleRich(this);
    }

    @Override
    public Attach getAttach() {
        return attach;
    }
}
