package com.hopen.darts.game.combat;

/**
 * 玩家相关格斗信息类
 */

public class Fighter {
    int groupId;//对应阵营(队伍)的id
    public int[] scoreArea;//自身对应的分区

    /**
     * 构造方法
     *
     * @param id                  阵营(队伍)id
     * @param one_player_area_num 每人对应分区数目
     */
    Fighter(int id, int one_player_area_num) {
        groupId = id;
        scoreArea = new int[one_player_area_num];
    }
}
