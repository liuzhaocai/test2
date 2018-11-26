package com.hopen.darts.game.game01;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;
import com.hopen.darts.game.game01.type.TypeGame01;
import com.hopen.darts.game.game01.type.TypeRule01;

import java.util.ArrayList;
import java.util.List;

/**
 * 01系列游戏系列类
 */

public class Series01 extends BaseSeries {

    @Override
    public String getSeriesId() {
        return G._01.SERIES_01;
    }

    @Override
    public List<BaseGame> getGameList() {
        List<BaseGame> gameList = new ArrayList<>();
        gameList.add(new Game01(TypeRule01.DISPORT, TypeGame01._301));
        gameList.add(new Game01(TypeRule01.STANDARD, TypeGame01._301));
        gameList.add(new Game01(TypeRule01.ONLINE, TypeGame01._301));
        gameList.add(new Game01(TypeRule01.DISPORT, TypeGame01._501));
        gameList.add(new Game01(TypeRule01.STANDARD, TypeGame01._501));
        gameList.add(new Game01(TypeRule01.ONLINE, TypeGame01._501));
        gameList.add(new Game01(TypeRule01.DISPORT, TypeGame01._701));
        gameList.add(new Game01(TypeRule01.STANDARD, TypeGame01._701));
        gameList.add(new Game01(TypeRule01.ONLINE, TypeGame01._701));
        return gameList;
    }
}
