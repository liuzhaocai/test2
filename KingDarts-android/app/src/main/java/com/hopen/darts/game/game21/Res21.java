package com.hopen.darts.game.game21;

import android.widget.FrameLayout;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.basecommon.DefaultCustomArea;

/**
 * Created by thomas on 2018/7/2.
 */

public class Res21 extends GameRes<Game21> {
    protected Res21(Game21 game) {
        super(game);
    }

    @Override
    public int getPageBg() {
        return R.mipmap.g21_page_bg;
    }

    @Override
    public int getTopLogo() {
        return R.mipmap.g21_top_logo;
    }

    @Override
    public int getPlayerBg() {
        return R.mipmap.g21_player_bg;
    }

    @Override
    public int getPlayerInfoItemBg() {
        return R.mipmap.g21_player_info_item_bg;
    }

    @Override
    public int getPlayerHeadBg() {
        return R.mipmap.g21_player_head_bg;
    }

    @Override
    public int getRoundBg() {
        return R.mipmap.g21_round_bg;
    }

    @Override
    public int getRoundItemBg() {
        return R.mipmap.g21_round_item_bg;
    }

    @Override
    public int getLiveBg() {
        return R.mipmap.g21_live_bg;
    }

    @Override
    public int getLiveItemBg() {
        return 0;
    }

    @Override
    public int getDartsBg() {
        return R.mipmap.g21_darts_bg;
    }

    @Override
    public int getBottomBg() {
        return R.mipmap.g21_bottom_bg;
    }

    @Override
    public int getBottomItemGroupBg(int position) {
        switch (mGame.getGameMode().getPlayerNumInGroup()) {
            case 2:
                if (position == 0)
                    return R.mipmap.g21_bottom_item_group_2v2_left_bg;
                else
                    return R.mipmap.g21_bottom_item_group_2v2_right_bg;
            case 3:
            default:
                if (position == 0)
                    return R.mipmap.g21_bottom_item_group_3v3_left_bg;
                else
                    return R.mipmap.g21_bottom_item_group_3v3_right_bg;
        }
    }

    @Override
    protected GameCustomArea<Game21> createCustomArea(FrameLayout parent) {
        return new DefaultCustomArea<>(parent, mGame);
    }
}