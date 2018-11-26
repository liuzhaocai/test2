package com.hopen.darts.game.mickey;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;
import com.hopen.darts.game.mickey.type.TypeRuleMickey;

import java.util.ArrayList;
import java.util.List;

/**
 * 米老鼠游戏系列类
 */

public class SeriesMickey extends BaseSeries {
    @Override
    public String getSeriesId() {
        return G.MICKEY.SERIES_MICKEY;
    }

    @Override
    public List<BaseGame> getGameList() {
        List<BaseGame> gameList = new ArrayList<>();
        gameList.add(new GameMickey(TypeRuleMickey.STANDARD));
        gameList.add(new GameMickey(TypeRuleMickey.BONUS));
        gameList.add(new GameMickey(TypeRuleMickey.ONLINE));
        return gameList;
    }
}
