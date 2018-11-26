package com.hopen.darts.game.base.interfaces;

import com.hopen.darts.game.base.HitIJ;

public interface OnHitOverListener {
    void onHitOver(HitIJ hit_ij, int hit_score, boolean effective);
}
