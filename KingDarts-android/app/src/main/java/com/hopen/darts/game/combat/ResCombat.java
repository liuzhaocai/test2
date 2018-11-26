package com.hopen.darts.game.combat;

import android.widget.FrameLayout;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.game.base.GameRes;

/**
 * 拳王格斗赛游戏资源类
 */

public class ResCombat extends GameRes<GameCombat> {
    protected ResCombat(GameCombat game) {
        super(game);
    }

    @Override
    public int getPageBg() {
        return R.mipmap.combat_page_bg;
    }

    @Override
    public int getTopLogo() {
        return R.mipmap.combat_top_logo;
    }

    @Override
    public int getPlayerBg() {
        return R.mipmap.combat_player_bg;
    }

    @Override
    public int getPlayerInfoItemBg() {
        return R.mipmap.combat_player_info_item_bg;
    }

    @Override
    public int getPlayerHeadBg() {
        return R.mipmap.combat_player_head_bg;
    }

    @Override
    public int getRoundBg() {
        return R.mipmap.combat_round_bg;
    }

    @Override
    public int getRoundItemBg() {
        return R.mipmap.combat_round_item_bg;
    }

    @Override
    public int getLiveBg() {
        return R.mipmap.combat_live_bg;
    }

    @Override
    public int getLiveItemBg() {
        return 0;
    }

    @Override
    public int getDartsBg() {
        return R.mipmap.combat_darts_bg;
    }

    @Override
    public int getBottomBg() {
        return R.mipmap.combat_bottom_bg;
    }

    @Override
    public int getBottomItemGroupBg(int position) {
        switch (mGame.getGameMode().getPlayerNumInGroup()) {
            case 2:
                if (position == 0)
                    return R.mipmap.combat_bottom_item_group_2v2_left_bg;
                else
                    return R.mipmap.combat_bottom_item_group_2v2_right_bg;
            case 3:
            default:
                if (position == 0)
                    return R.mipmap.combat_bottom_item_group_3v3_left_bg;
                else
                    return R.mipmap.combat_bottom_item_group_3v3_right_bg;
        }
    }

    @Override
    protected GameCustomArea<GameCombat> createCustomArea(FrameLayout parent) {
        return new CustomAreaCombat(parent, mGame);
    }
}
