package com.hopen.darts.game.blow;

import android.widget.FrameLayout;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.game.base.GameRes;

/**
 * 打气球游戏资源类
 */

public class ResBlow extends GameRes<GameBlow> {
    protected ResBlow(GameBlow game) {
        super(game);
    }

    @Override
    public int getPageBg() {
        return R.mipmap.blow_page_bg;
    }

    @Override
    public int getTopLogo() {
        return R.mipmap.blow_top_logo;
    }

    @Override
    public int getPlayerBg() {
        return R.mipmap.blow_player_bg;
    }

    @Override
    public int getPlayerInfoItemBg() {
        return R.mipmap.blow_player_info_item_bg;
    }

    @Override
    public int getPlayerHeadBg() {
        return R.mipmap.blow_player_head_bg;
    }

    @Override
    public int getRoundBg() {
        return R.mipmap.blow_round_bg;
    }

    @Override
    public int getRoundItemBg() {
        return R.mipmap.blow_round_item_bg;
    }

    @Override
    public int getLiveBg() {
        return R.mipmap.blow_live_bg;
    }

    @Override
    public int getLiveItemBg() {
        return 0;
    }

    @Override
    public int getDartsBg() {
        return R.mipmap.blow_darts_bg;
    }

    @Override
    public int getDartsImg() {
        return R.drawable.darts_img_blow;
    }

    @Override
    public int getBottomBg() {
        return R.mipmap.blow_bottom_bg;
    }

    @Override
    public int getBottomItemGroupBg(int position) {
        switch (mGame.getGameMode().getPlayerNumInGroup()) {
            case 2:
                if (position == 0)
                    return R.mipmap.blow_bottom_item_group_2v2_left_bg;
                else
                    return R.mipmap.blow_bottom_item_group_2v2_right_bg;
            case 3:
            default:
                if (position == 0)
                    return R.mipmap.blow_bottom_item_group_3v3_left_bg;
                else
                    return R.mipmap.blow_bottom_item_group_3v3_right_bg;
        }
    }

    @Override
    protected GameCustomArea<GameBlow> createCustomArea(FrameLayout parent) {
        return new CustomAreaBlow(parent, mGame);
    }
}
