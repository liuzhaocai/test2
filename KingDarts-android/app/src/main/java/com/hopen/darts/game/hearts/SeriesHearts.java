package com.hopen.darts.game.hearts;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;
import com.hopen.darts.game.hearts.type.TypeRuleHearts;

import java.util.ArrayList;
import java.util.List;

/**
 * 红心王游戏系列类
 */

public class SeriesHearts extends BaseSeries {

    @Override
    public String getSeriesId() {
        return G.HEARTS.SERIES_HEARTS;
    }

    @Override
    public List<BaseGame> getGameList() {
        List<BaseGame> gameList = new ArrayList<>();
        gameList.add(new GameHearts(TypeRuleHearts.STANDARD));
        gameList.add(new GameHearts(TypeRuleHearts.ONLINE));
        return gameList;
    }
}