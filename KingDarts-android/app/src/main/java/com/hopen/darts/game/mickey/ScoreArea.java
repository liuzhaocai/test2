package com.hopen.darts.game.mickey;

import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * 米老鼠用的分区类
 */

class ScoreArea {
    private static final int occupyNum = 3;//占领3次表示分区已占领
    int score;
    List<Occupy> occupyList;//占领情况列表

    ScoreArea(int score) {
        this.score = score;
        occupyList = new ArrayList<>();
    }

    /**
     * 指定的阵营(队伍)进行一次占领
     *
     * @param group 指定的阵营(队伍)
     * @param num   占领次数
     */
    void occupy(Group group, int num) {
        boolean contain = false;
        for (Occupy item : occupyList) {
            if (item.id == group.getId()) {
                contain = true;
                item.num += num;
                break;
            }
        }
        if (!contain) {
            occupyList.add(new Occupy(group.getId(), num));
        }
    }

    /**
     * 指定的阵营(队伍)取消一次占领
     *
     * @param group 指定的阵营(队伍)
     * @param num   占领次数
     */
    void unOccupy(Group group, int num) {
        for (Occupy item : occupyList) {
            if (item.id == group.getId()) {
                item.num -= num;
                break;
            }
        }
    }

    /**
     * 指定的阵营(队伍)是否已经占领了此分区
     *
     * @param group 指定的阵营(队伍)
     * @return 是否已经占领了此分区
     */
    boolean isOccupy(Group group) {
        for (Occupy item : occupyList) {
            if (item.id == group.getId()) return item.num >= occupyNum;
        }
        return false;
    }

    /**
     * 指定的阵营(队伍)距离占领分区剩余的占领次数
     *
     * @param group 指定的阵营(队伍)
     * @return 距离占领分区剩余的占领次数
     */
    int surplusOccupyNum(Group group) {
        for (Occupy item : occupyList) {
            if (item.id == group.getId()) return occupyNum - item.num;
        }
        return occupyNum;
    }

    /**
     * 此分区是否已经被封死
     *
     * @param game_mode 本次游戏的模式
     * @return 是否已经被封死
     */
    boolean isDeath(GameMode game_mode) {
        if (occupyList.size() < game_mode.getGroupNum()) return false;
        for (Occupy item : occupyList) {
            if (item.num < occupyNum) return false;
        }
        return true;
    }

    /**
     * 占领情况类
     */
    class Occupy {
        int id;//占领group的id
        int num;//占领次数

        Occupy(int id, int num) {
            this.id = id;
            this.num = num;
        }
    }
}
