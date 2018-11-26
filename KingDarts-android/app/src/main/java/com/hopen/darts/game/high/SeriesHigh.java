package com.hopen.darts.game.high;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;
import com.hopen.darts.game.high.type.TypeRuleHigh;

import java.util.ArrayList;
import java.util.List;

/**
 * 高分赛游戏系列类
 */

public class SeriesHigh extends BaseSeries {
    @Override
    public String getSeriesId() {
        return G.HIGH.SERIES_HIGH;
    }

    @Override
    public List<BaseGame> getGameList() {
        List<BaseGame> gameList = new ArrayList<>();
        gameList.add(new GameHigh(TypeRuleHigh.STANDARD));
        gameList.add(new GameHigh(TypeRuleHigh.ONLINE));
        return gameList;
    }
}
