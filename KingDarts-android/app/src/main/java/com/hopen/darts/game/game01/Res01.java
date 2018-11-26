package com.hopen.darts.game.game01;

import android.widget.FrameLayout;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.basecommon.DefaultCustomArea;

/**
 * 01系列游戏资源类
 */

public class Res01 extends GameRes<Game01> {

    Res01(Game01 game) {
        super(game);
    }

    @Override
    public int getPageBg() {
        return R.mipmap.g01_page_bg;
    }

    @Override
    public int getTopLogo() {
        return R.mipmap.g01_top_logo;
    }

    @Override
    public int getPlayerBg() {
        return R.mipmap.g01_player_bg;
    }

    @Override
    public int getPlayerInfoItemBg() {
        return R.mipmap.g01_player_info_item_bg;
    }

    @Override
    public int getPlayerHeadBg() {
        return R.mipmap.g01_player_head_bg;
    }

    @Override
    public int getRoundBg() {
        return R.mipmap.g01_round_bg;
    }

    @Override
    public int getRoundItemBg() {
        return R.mipmap.g01_round_item_bg;
    }

    @Override
    public int getLiveBg() {
        switch (mGame.typeGame01) {
            case _501:
                return R.mipmap.g01_live_501_bg;
            case _701:
                return R.mipmap.g01_live_701_bg;
            case _301:
            default:
                return R.mipmap.g01_live_301_bg;
        }
    }

    @Override
    public int getLiveItemBg() {
        return 0;
    }

    @Override
    public int getDartsBg() {
        return R.mipmap.g01_darts_bg;
    }

    @Override
    public int getBottomBg() {
        return R.mipmap.g01_bottom_bg;
    }

    @Override
    public int getBottomItemGroupBg(int position) {
        switch (mGame.getGameMode().getPlayerNumInGroup()) {
            case 2:
                if (position == 0)
                    return R.mipmap.g01_bottom_item_group_2v2_left_bg;
                else
                    return R.mipmap.g01_bottom_item_group_2v2_right_bg;
            case 3:
            default:
                if (position == 0)
                    return R.mipmap.g01_bottom_item_group_3v3_left_bg;
                else
                    return R.mipmap.g01_bottom_item_group_3v3_right_bg;
        }
    }

    @Override
    protected GameCustomArea<Game01> createCustomArea(FrameLayout parent) {
        return new DefaultCustomArea<>(parent, mGame);
    }
}
