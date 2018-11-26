package com.hopen.darts.game.game01;

import com.hopen.darts.game.G;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.GameRule;
import com.hopen.darts.game.game01.type.TypeGame01;
import com.hopen.darts.game.game01.type.TypeRule01;

import java.util.ArrayList;
import java.util.List;

/**
 * 01系列游戏类
 */

public class Game01 extends BaseGame {
    TypeRule01 typeRule01;
    TypeGame01 typeGame01;

    Game01(TypeRule01 type_rule_01, TypeGame01 type_game_01) {
        typeRule01 = type_rule_01;
        typeGame01 = type_game_01;
    }

    @Override
    public String getSeriesId() {
        return G._01.SERIES_01;
    }

    @Override
    public String getGameId() {
        switch (typeGame01) {
            case _501:
                switch (typeRule01) {
                    case DISPORT:
                        return G._01.GAME_DISPORT_501;
                    case ONLINE:
                        return G._01.GAME_ONLINE_501;
                    case STANDARD:
                    default:
                        return G._01.GAME_STANDARD_501;
                }
            case _701:
                switch (typeRule01) {
                    case DISPORT:
                        return G._01.GAME_DISPORT_701;
                    case ONLINE:
                        return G._01.GAME_ONLINE_701;
                    case STANDARD:
                    default:
                        return G._01.GAME_STANDARD_701;
                }
            case _301:
            default:
                switch (typeRule01) {
                    case DISPORT:
                        return G._01.GAME_DISPORT_301;
                    case ONLINE:
                        return G._01.GAME_ONLINE_301;
                    case STANDARD:
                    default:
                        return G._01.GAME_STANDARD_301;
                }
        }
    }

    @Override
    public boolean isOnline() {
        return typeRule01 == TypeRule01.ONLINE;
    }

    @Override
    public List<GameMode> getModeList() {
        List<GameMode> list = new ArrayList<>();
        if (isOnline()) {
            list.add(GameMode.OL_TWO);
        } else {
            list.add(GameMode.ONE);
            list.add(GameMode.TWO);
            list.add(GameMode.THREE);
            list.add(GameMode.FOUR);
            list.add(GameMode._2V2);
            list.add(GameMode._3V3);
        }
        return list;
    }

    @Override
    public BaseGame newIns() {
        return new Game01(typeRule01, typeGame01);
    }

    @Override
    protected GameRes createGameRes() {
        return new Res01(this);
    }

    @Override
    protected GameRule createGameRule() {
        return new Rule01(this);
    }
}
