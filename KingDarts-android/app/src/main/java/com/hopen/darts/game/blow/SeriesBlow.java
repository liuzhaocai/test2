package com.hopen.darts.game.blow;

import com.hopen.darts.game.G;
import com.hopen.darts.game.blow.type.TypeRuleBlow;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * 打气球游戏系列类
 */

public class SeriesBlow extends BaseSeries {
    @Override
    public String getSeriesId() {
        return G.BLOW.SERIES_BLOW;
    }

    @Override
    public List<BaseGame> getGameList() {
        List<BaseGame> gameList = new ArrayList<>();
        gameList.add(new GameBlow(TypeRuleBlow.STANDARD));
        gameList.add(new GameBlow(TypeRuleBlow.ONLINE));
        return gameList;
    }
}
