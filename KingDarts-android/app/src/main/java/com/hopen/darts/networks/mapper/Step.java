package com.hopen.darts.networks.mapper;

import android.support.annotation.IntRange;

import com.alibaba.fastjson.annotation.JSONField;
import com.hopen.darts.game.base.HitIJ;

/**
 * 联机模式，每次操作数据
 */
public class Step {
    /**
     * 步骤序列
     */
    @JSONField(serialize = false)
    int position;
    /**
     * 分区，0-20对应0-20分区，21对应牛眼
     */
    @JSONField(serialize = false)
    int i;
    /**
     * 倍区，从0到3依次为：内围，三倍，外围，二倍(对应靶盘从内到外)
     */
    @JSONField(serialize = false)
    int j;
    /**
     * 按下决定建信息
     */
    @JSONField(serialize = false)
    Decision decision;

    public Step() {
        this.position = -1;
        this.i = -1;
        this.j = -1;
        this.decision = Decision.NONE;
    }

    public Step(@IntRange(from = 0) int position, int i, int j) {
        this.position = position;
        this.i = i;
        this.j = j;
        this.decision = Decision.NONE;
    }

    public Step(@IntRange(from = 0) int position, Decision decision) {
        this.position = position;
        this.i = -1;
        this.j = -1;
        this.decision = decision;
    }

    @JSONField(name = "position")
    public int getPosition() {
        return position;
    }

    @JSONField(name = "position")
    public void setPosition(int position) {
        this.position = position;
    }

    @JSONField(name = "score")
    public int getI() {
        return i;
    }

    @JSONField(name = "score")
    public void setI(int i) {
        this.i = i;
    }

    @JSONField(name = "area")
    public int getJ() {
        return j;
    }

    @JSONField(name = "area")
    public void setJ(int j) {
        this.j = j;
    }

    @JSONField(name = "decision")
    public int getDecision() {
        return decision.flag;
    }

    @JSONField(name = "decision")
    public void setDecision(int decision) {
        this.decision = Decision.flagOf(decision);
    }

    @JSONField(serialize = false)
    public boolean isOverEvent() {
        return decision == Decision.OVER_EVENT;
    }

    @JSONField(serialize = false)
    public boolean isSwitchPlayer() {
        return decision == Decision.SWITCH_PLAYER;
    }

    @JSONField(serialize = false)
    public boolean isOverGame() {
        return decision == Decision.OVER_GAME;
    }

    @JSONField(serialize = false)
    public HitIJ toHitIJ() {
        return new HitIJ(i, j);
    }

    public enum Decision {
        NONE(0),//无操作
        OVER_EVENT(1),//结束游戏Event
        SWITCH_PLAYER(2),//切换玩家
        OVER_GAME(3);//结束游戏
        /**
         * 游戏模式总玩家数量
         */
        private int flag;

        Decision(int flag) {
            this.flag = flag;
        }

        public static Decision flagOf(int flag) {
            for (Decision direction : Decision.values()) {
                if (direction.flag == flag) {
                    return direction;
                }
            }
            return NONE;
        }
    }
}
