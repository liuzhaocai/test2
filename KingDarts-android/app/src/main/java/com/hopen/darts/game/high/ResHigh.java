package com.hopen.darts.game.high;

import android.widget.FrameLayout;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.basecommon.DefaultCustomArea;

/**
 * 高分赛游戏资源类
 */

public class ResHigh extends GameRes<GameHigh> {
    protected ResHigh(GameHigh game) {
        super(game);
    }

    @Override
    public int getPageBg() {
        return R.mipmap.high_page_bg;
    }

    @Override
    public int getTopLogo() {
        return R.mipmap.high_top_logo;
    }

    @Override
    public int getPlayerBg() {
        return R.mipmap.high_player_bg;
    }

    @Override
    public int getPlayerInfoItemBg() {
        return R.mipmap.high_player_info_item_bg;
    }

    @Override
    public int getPlayerHeadBg() {
        return R.mipmap.high_player_head_bg;
    }

    @Override
    public int getRoundBg() {
        return R.mipmap.high_round_bg;
    }

    @Override
    public int getRoundItemBg() {
        return R.mipmap.high_round_item_bg;
    }

    @Override
    public int getLiveBg() {
        return R.mipmap.high_live_bg;
    }

    @Override
    public int getLiveItemBg() {
        return 0;
    }

    @Override
    public int getDartsBg() {
        return R.mipmap.high_darts_bg;
    }

    @Override
    public int getBottomBg() {
        return R.mipmap.high_bottom_bg;
    }

    @Override
    public int getBottomItemGroupBg(int position) {
        switch (mGame.getGameMode().getPlayerNumInGroup()) {
            case 2:
                if (position == 0)
                    return R.mipmap.high_bottom_item_group_2v2_left_bg;
                else
                    return R.mipmap.high_bottom_item_group_2v2_right_bg;
            case 3:
            default:
                if (position == 0)
                    return R.mipmap.high_bottom_item_group_3v3_left_bg;
                else
                    return R.mipmap.high_bottom_item_group_3v3_right_bg;
        }
    }

    @Override
    protected GameCustomArea<GameHigh> createCustomArea(FrameLayout parent) {
        return new DefaultCustomArea<>(parent, mGame);
    }
}
