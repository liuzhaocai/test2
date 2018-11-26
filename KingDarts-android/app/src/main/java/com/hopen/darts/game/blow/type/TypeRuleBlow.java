package com.hopen.darts.game.blow.type;

/**
 * 打气球游戏规则枚举类
 */

public enum TypeRuleBlow {
    STANDARD(1, "标准"),
    ONLINE(2, "网络");

    private int type;
    private String name;

    TypeRuleBlow(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
