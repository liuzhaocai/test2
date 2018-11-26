package com.hopen.darts.game.game21.type;

/**
 * 21点游戏规则枚举类
 */

public enum TypeRule21 {
    STANDARD(1, "标准"),
    ONLINE(2, "网络");

    private int type;
    private String name;

    TypeRule21(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
