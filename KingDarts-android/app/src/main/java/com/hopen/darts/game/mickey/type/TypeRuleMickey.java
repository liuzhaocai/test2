package com.hopen.darts.game.mickey.type;

/**
 * 米老鼠游戏规则枚举类
 */

public enum TypeRuleMickey {
    STANDARD(1, "标准"),
    BONUS(2, "加分"),
    ONLINE(3, "网络");

    private int type;
    private String name;

    TypeRuleMickey(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
