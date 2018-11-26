package com.hopen.darts.game.base;

public enum GameState {

    /**
     * 游戏未载入
     */
    NONE(0, "游戏未载入"),
    /**
     * 游戏准备中
     */
    BEGINNING(1, "游戏准备中"),
    /**
     * 游戏进行中
     */
    PLAYING(2, "游戏进行中"),
    /**
     * 游戏已结束
     */
    OVER(3, "游戏已结束");

    /**
     * 游戏状态标志
     */
    private int state;
    /**
     * 游戏状态名称
     */
    private String name;

    GameState(int state, String name) {
        this.state = state;
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public String getName() {
        return name;
    }
}
