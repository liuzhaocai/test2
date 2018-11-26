package com.hopen.darts.game.combat;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;
import com.hopen.darts.game.combat.type.TypeRuleCombat;

import java.util.ArrayList;
import java.util.List;

/**
 * 拳王格斗赛游戏系列类
 */

public class SeriesCombat extends BaseSeries {
    @Override
    public String getSeriesId() {
        return G.COMBAT.SERIES_COMBAT;
    }

    @Override
    public List<BaseGame> getGameList() {
        List<BaseGame> gameList = new ArrayList<>();
        gameList.add(new GameCombat(TypeRuleCombat.STANDARD));
        gameList.add(new GameCombat(TypeRuleCombat.ONLINE));
        return gameList;
    }
}
