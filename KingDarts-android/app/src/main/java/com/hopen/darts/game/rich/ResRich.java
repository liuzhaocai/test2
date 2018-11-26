package com.hopen.darts.game.rich;

import android.widget.FrameLayout;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.game.base.GameRes;

/**
 * 全民大富豪游戏资源类
 */

public class ResRich extends GameRes<GameRich> {
    protected ResRich(GameRich game) {
        super(game);
    }

    @Override
    public int getPageBg() {
        return R.mipmap.rich_page_bg;
    }

    @Override
    public int getTopLogo() {
        return R.mipmap.rich_top_logo;
    }

    @Override
    public int getPlayerBg() {
        return R.mipmap.rich_player_bg;
    }

    @Override
    public int getPlayerInfoItemBg() {
        return R.mipmap.rich_player_info_item_bg;
    }

    @Override
    public int getPlayerHeadBg() {
        return R.mipmap.rich_player_head_bg;
    }

    @Override
    public int getRoundBg() {
        return R.mipmap.rich_round_bg;
    }

    @Override
    public int getRoundItemBg() {
        return R.mipmap.rich_round_item_bg;
    }

    @Override
    public int getLiveBg() {
        return R.mipmap.rich_live_bg;
    }

    @Override
    public int getLiveItemBg() {
        return 0;
    }

    @Override
    public int getDartsBg() {
        return R.mipmap.rich_darts_bg;
    }

    @Override
    public int getDartsImg() {
        return R.drawable.darts_img_rich;
    }

    @Override
    public int getBottomBg() {
        return R.mipmap.rich_bottom_bg;
    }

    @Override
    public int getBottomItemGroupBg(int position) {
        switch (mGame.getGameMode().getPlayerNumInGroup()) {
            case 2:
                if (position == 0)
                    return R.mipmap.rich_bottom_item_group_2v2_left_bg;
                else
                    return R.mipmap.rich_bottom_item_group_2v2_right_bg;
            case 3:
            default:
                if (position == 0)
                    return R.mipmap.rich_bottom_item_group_3v3_left_bg;
                else
                    return R.mipmap.rich_bottom_item_group_3v3_right_bg;
        }
    }

    @Override
    protected GameCustomArea<GameRich> createCustomArea(FrameLayout parent) {
        return new CustomAreaRich(parent, mGame);
    }
}
