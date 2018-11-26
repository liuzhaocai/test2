package com.hopen.darts.game.game21;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;
import com.hopen.darts.game.game21.type.TypeRule21;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 2018/7/2.
 */

public class Series21 extends BaseSeries {
    @Override
    public String getSeriesId() {
        return G._21.SERIES_21;
    }

    @Override
    public List<BaseGame> getGameList() {
        List<BaseGame> gameList = new ArrayList<>();
        gameList.add(new Game21(TypeRule21.STANDARD));
        gameList.add(new Game21(TypeRule21.ONLINE));
        return gameList;
    }
}