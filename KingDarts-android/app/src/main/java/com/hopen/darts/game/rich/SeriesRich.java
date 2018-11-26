package com.hopen.darts.game.rich;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * 全民大富豪游戏系列类
 */

public class SeriesRich extends BaseSeries {
    @Override
    public String getSeriesId() {
        return G.RICH.SERIES_RICH;
    }

    @Override
    public List<BaseGame> getGameList() {
        List<BaseGame> gameList = new ArrayList<>();
        gameList.add(new GameRich());
        return gameList;
    }
}
