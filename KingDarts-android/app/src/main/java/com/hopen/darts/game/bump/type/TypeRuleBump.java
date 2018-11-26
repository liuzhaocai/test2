package com.hopen.darts.game.bump.type;

/**
 * 碰碰乐游戏规则枚举类
 */

public enum TypeRuleBump {
    STANDARD(1, "标准"),
    ONLINE(2, "网络");

    private int type;
    private String name;

    TypeRuleBump(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
