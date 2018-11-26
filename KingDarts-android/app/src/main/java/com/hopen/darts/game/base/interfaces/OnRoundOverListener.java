package com.hopen.darts.game.base.interfaces;

import com.hopen.darts.game.base.Round;

public interface OnRoundOverListener {
    void onRoundOver(Round round, boolean game_over);
}
