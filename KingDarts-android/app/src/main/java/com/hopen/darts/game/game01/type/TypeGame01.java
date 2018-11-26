package com.hopen.darts.game.game01.type;

/**
 * 01系列游戏类别枚举类
 */

public enum TypeGame01 {

    _301(1, "301"),
    _501(2, "501"),
    _701(3, "701");

    private int type;
    private String name;

    TypeGame01(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
