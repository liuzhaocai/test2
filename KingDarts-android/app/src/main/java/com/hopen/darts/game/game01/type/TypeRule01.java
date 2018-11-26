package com.hopen.darts.game.game01.type;

/**
 * 01系列游戏规则枚举类
 */

public enum TypeRule01 {
    DISPORT(1, "娱乐"),
    STANDARD(2, "标准"),
    ONLINE(3, "网络");

    private int type;
    private String name;

    TypeRule01(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
