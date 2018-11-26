package com.hopen.darts.game.bump;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;
import com.hopen.darts.game.bump.type.TypeRuleBump;

import java.util.ArrayList;
import java.util.List;

/**
 * 碰碰乐系列(暂时废弃)
 */

public class SeriesBump extends BaseSeries {
    @Override
    public String getSeriesId() {
        return G.BUMP.SERIES_BUMP;
    }

    @Override
    public List<BaseGame> getGameList() {
        List<BaseGame> gameList = new ArrayList<>();
        gameList.add(new GameBump(TypeRuleBump.STANDARD));
        gameList.add(new GameBump(TypeRuleBump.ONLINE));
        return gameList;
    }
}