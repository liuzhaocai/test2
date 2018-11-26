package com.hopen.darts.game.combat.type;

/**
 * 拳王格斗游戏规则枚举类
 */

public enum TypeRuleCombat {
    STANDARD(1, "标准"),
    ONLINE(2, "网络");

    private int type;
    private String name;

    TypeRuleCombat(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
