package com.hopen.darts.networks.commons;

/**
 * Created by thomas on 2018/1/10.
 */

public enum MethodType {

    POST(0, "POST"),
    GET(1, "GET"),
    DELETE(2, "DELETE");

    private int mValue;
    private String mName;

    MethodType(int value, String name) {
        this.mValue = value;
        this.mName = name;
    }

    public static MethodType getFromInt(int value) {
        for (MethodType direction : MethodType.values()) {
            if (direction.mValue == value) {
                return direction;
            }
        }
        return POST;
    }
}
