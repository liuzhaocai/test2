package com.hopen.darts.game.rich;

import android.util.SparseArray;

import com.alibaba.fastjson.annotation.JSONField;
import com.hopen.darts.manager.KDManager;

import java.util.ArrayList;

public class Attach {
    @JSONField(serialize = false)
    SparseArray<LuckScore> luckScores;
    @JSONField(serialize = false)
    int totalScore;

    public void init(RuleRich rule) {
        luckScores = rule.createLuckScores(KDManager.getInstance().getPayerDetail().getVipLevel());
        totalScore = 0;
    }

    public void oneHit(int hit_score) {
        LuckScore luck_score = luckScores.get(hit_score);
        if (luck_score != null) {
            luck_score.oneHit();
            totalScore += hit_score;
        }
    }

    public void retreatHit(int hit_score) {
        LuckScore luck_score = luckScores.get(hit_score);
        if (luck_score != null) {
            luck_score.retreatHit();
            totalScore -= hit_score;
        }
    }

    @JSONField(name = "luck_scores")
    public ArrayList<LuckScore> getLuckScores() {
        ArrayList<LuckScore> list = new ArrayList<>();
        for (int i = 0; i < luckScores.size(); i++) {
            list.add(luckScores.valueAt(i));
        }
        return list;
    }

    @JSONField(name = "total_score")
    public int getTotalScore() {
        return totalScore;
    }
}
