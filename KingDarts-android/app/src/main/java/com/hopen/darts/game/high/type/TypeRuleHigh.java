package com.hopen.darts.game.high.type;

/**
 * 高分赛游戏规则枚举类
 */

public enum TypeRuleHigh {
    STANDARD(1, "标准"),
    ONLINE(2, "网络");

    private int type;
    private String name;

    TypeRuleHigh(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
