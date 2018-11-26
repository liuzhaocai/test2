package com.hopen.darts.game.bump;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameRes;

/**
 * 碰碰乐游戏资源类
 */

public class ResBump extends GameRes<GameBump> {
    protected ResBump(GameBump game) {
        super(game);
    }

    @Override
    public int getPageBg() {
        return R.mipmap.bump_page_bg;
    }

    @Override
    public int getTopLogo() {
        return R.mipmap.bump_top_logo;
    }

    @Override
    public int getPlayerBg() {
        return R.mipmap.bump_player_bg;
    }

    @Override
    public int getPlayerInfoItemBg() {
        return R.mipmap.bump_player_info_item_bg;
    }

    @Override
    public int getPlayerHeadBg() {
        return R.mipmap.bump_player_head_bg;
    }

    @Override
    public int getRoundBg() {
        return R.mipmap.bump_round_bg;
    }

    @Override
    public int getRoundItemBg() {
        return R.mipmap.bump_round_item_bg;
    }

    @Override
    public int getLiveBg() {
        return R.mipmap.bump_live_bg;
    }

    @Override
    public int getLiveItemBg() {
        return 0;
    }

    @Override
    public int getDartsBg() {
        return R.mipmap.bump_darts_bg;
    }

    @Override
    public int getDartsImg() {
        return R.drawable.darts_img_bump;
    }

    @Override
    public int getBottomBg() {
        return R.mipmap.bump_bottom_bg;
    }

    @Override
    public int getBottomItemGroupBg(int position) {
        switch (mGame.getGameMode().getPlayerNumInGroup()) {
            case 2:
                if (position == 0)
                    return R.mipmap.bump_bottom_item_group_2v2_left_bg;
                else
                    return R.mipmap.bump_bottom_item_group_2v2_right_bg;
            case 3:
            default:
                if (position == 0)
                    return R.mipmap.bump_bottom_item_group_3v3_left_bg;
                else
                    return R.mipmap.bump_bottom_item_group_3v3_right_bg;
        }
    }
}