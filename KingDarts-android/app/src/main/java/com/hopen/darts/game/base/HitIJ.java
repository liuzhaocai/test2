package com.hopen.darts.game.base;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 单镖数据类，对应{@link com.hopen.darts.utils.SerialPort.KeyCode#getScoreAreaCollectDataTable()}中的位置
 */
public class HitIJ {
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

    public HitIJ() {
        i = 0;
        j = 0;
    }

    public HitIJ(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @JSONField(serialize = false)
    public int i() {
        return i;
    }

    @JSONField(serialize = false)
    public int j() {
        return j;
    }

    @JSONField(name = "score")
    public int getI() {
        return i;
    }

    @JSONField(name = "area")
    public int getJ() {
        return j;
    }

    public static HitIJ zero() {
        return new HitIJ();
    }
}
