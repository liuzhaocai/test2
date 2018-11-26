package com.hopen.darts.game.hearts;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameRes;

/**
 * 红心王游戏资源类
 */

public class ResHearts extends GameRes<GameHearts> {
    protected ResHearts(GameHearts game) {
        super(game);
    }

    @Override
    public int getPageBg() {
        return R.mipmap.hearts_page_bg;
    }

    @Override
    public int getTopLogo() {
        return R.mipmap.hearts_top_logo;
    }

    @Override
    public int getPlayerBg() {
        return R.mipmap.hearts_player_bg;
    }

    @Override
    public int getPlayerInfoItemBg() {
        return R.mipmap.hearts_player_info_item_bg;
    }

    @Override
    public int getPlayerHeadBg() {
        return R.mipmap.hearts_player_head_bg;
    }

    @Override
    public int getRoundBg() {
        return R.mipmap.hearts_round_bg;
    }

    @Override
    public int getRoundItemBg() {
        return R.mipmap.hearts_round_item_bg;
    }

    @Override
    public int getLiveBg() {
        return R.mipmap.hearts_live_bg;
    }

    @Override
    public int getLiveItemBg() {
        return 0;
    }

    @Override
    public int getDartsBg() {
        return R.mipmap.hearts_darts_bg;
    }

    @Override
    public int getDartsImg() {
        return R.drawable.darts_img_hearts;
    }

    @Override
    public int getBottomBg() {
        return R.mipmap.hearts_bottom_bg;
    }

    @Override
    public int getBottomItemGroupBg(int position) {
        switch (mGame.getGameMode().getPlayerNumInGroup()) {
            case 2:
                if (position == 0)
                    return R.mipmap.hearts_bottom_item_group_2v2_left_bg;
                else
                    return R.mipmap.hearts_bottom_item_group_2v2_right_bg;
            case 3:
            default:
                if (position == 0)
                    return R.mipmap.hearts_bottom_item_group_3v3_left_bg;
                else
                    return R.mipmap.hearts_bottom_item_group_3v3_right_bg;
        }
    }
}
