package com.hopen.darts.game.base;

/**
 * 游戏模式枚举
 */

public enum GameMode {

    /**
     * 单人游戏
     */
    ONE(1, "单人游戏", 1, 1),
    /**
     * 双人游戏
     */
    TWO(2, "双人游戏", 2, 2),
    /**
     * 三人游戏
     */
    THREE(3, "三人游戏", 3, 3),
    /**
     * 四人游戏
     */
    FOUR(4, "四人游戏", 4, 4),
    /**
     * 双人赛2V2
     */
    _2V2(5, "双人赛2V2", 2, 4),
    /**
     * 三人赛3V3
     */
    _3V3(6, "三人赛3V3", 2, 6),
    /**
     * 网络1V1
     */
    OL_TWO(7, "网络1V1", 2, 2);

    /**
     * 游戏模式标志
     */
    private int id;
    /**
     * 游戏模式名称
     */
    private String name;
    /**
     * 游戏模式的阵营(队伍)数量
     */
    private int groupNum;
    /**
     * 游戏模式总玩家数量
     */
    private int playerNum;

    GameMode(int id, String name, int group_num, int player_num) {
        this.id = id;
        this.name = name;
        this.groupNum = group_num;
        this.playerNum = player_num;
    }

    public String getName() {
        return name;
    }

    public int getGroupNum() {
        return groupNum;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public int getId() {
        return id;
    }

    /**
     * 获取一队中的玩家数量
     * 一队中的玩家数量 = 总玩家数量 / 阵营(队伍)数量
     *
     * @return 一队中的玩家数量
     */
    public int getPlayerNumInGroup() {
        return playerNum / groupNum;
    }
}
