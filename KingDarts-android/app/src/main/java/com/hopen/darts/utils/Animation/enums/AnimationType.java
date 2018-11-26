package com.hopen.darts.utils.Animation.enums;

public interface AnimationType {

    /**
     * 动画模块
     */
    enum Module{
        AddBlood,
        Boxing,
        ManBeaten,
        WomanBeaten,
    }

    enum Boxing{
        Start,
        End,
    }

    /**
     * 性别
     */
    enum Sex{
        Man,
        Woman
    }

    /**
     * 男的被打程度，以及女的出击状态（将Sex传入不同参数可以进行转换）
     */
    enum ManBeatenStatus{
        LevelOne,
        LevelTwo,
        LevelThree,
    }

    /**
     * 女的被打程度，以及男的出击状态（将Sex传入不同参数可以进行转换）
     */
    enum WoManBeatenStatus{
        LevelOne,
        LevelTwo,
        LevelThree,
        LevelFour,
    }

}
