package com.hopen.darts.game.bump;

/**
 * 碰碰了专用回合信息类
 */

public class RoundBump {
    int[] flagArray;

    RoundBump(int dart_num_in_one_round) {
        flagArray = new int[dart_num_in_one_round];
    }

    /**
     * 判断本回合是否已经结束
     *
     * @return 本回合是否已经结束
     */
    public boolean isOver() {
        for (long hit : flagArray) {
            if (hit == 0)
                return false;
        }
        return true;
    }

    void clear() {
        for (int i = 0; i < flagArray.length; i++) {
            flagArray[i] = 0;
        }
    }

    boolean add(int times) {
        for (int i = 0; i < flagArray.length; i++) {
            if (flagArray[i] == 0) {
                flagArray[i] = times;
                return true;
            }
        }
        return false;
    }

    boolean remove() {
        for (int i = flagArray.length - 1; i >= 0; i--) {
            if (flagArray[i] != 0) {
                flagArray[i] = 0;
                return true;
            }
        }
        return false;
    }
}
