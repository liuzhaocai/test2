package com.hopen.darts.game.hearts.type;

/**
 * 红心王游戏规则枚举类
 */

public enum TypeRuleHearts {
    STANDARD(1, "标准"),
    ONLINE(2, "网络");

    private int type;
    private String name;

    TypeRuleHearts(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
