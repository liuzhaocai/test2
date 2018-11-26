package com.hopen.darts.game.mickey;

/**
 * 米老鼠用回合类
 */

public class RoundMickey {
    int[] timesArray;

    RoundMickey(int dart_num_in_one_round) {
        timesArray = new int[dart_num_in_one_round];
    }

    /**
     * 添加一次掷镖数据
     *
     * @param times 几倍区
     * @return 此次添加是否有效
     */
    boolean add(int times) {
        for (int i = 0; i < timesArray.length; i++) {
            if (timesArray[i] == 0) {
                timesArray[i] = times;
                return true;
            }
        }
        return false;
    }

    /**
     * 进行一次退镖操作
     *
     * @return 此次退镖是否有效
     */
    boolean remove() {
        for (int i = timesArray.length - 1; i >= 0; i--) {
            if (timesArray[i] != 0) {
                timesArray[i] = 0;
                return true;
            }
        }
        return false;
    }

    /**
     * 获取本回合最后一次掷镖的数据(倍区)
     *
     * @return 本回合最后一次掷镖的数据(倍区)
     */
    int getLastHitTimes() {
        for (int i = timesArray.length - 1; i >= 0; i--) {
            if (timesArray[i] != 0) {
                return timesArray[i];
            }
        }
        return 0;
    }
}
