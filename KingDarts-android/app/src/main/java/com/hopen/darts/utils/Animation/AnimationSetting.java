package com.hopen.darts.utils.Animation.impl;

import com.hopen.darts.utils.Animation.bean.AnimationConfig;
import com.hopen.darts.utils.Animation.enums.AnimationType;

import static com.hopen.darts.utils.Animation.res.AnimationResource.getConfig;
import static com.hopen.darts.utils.Animation.res.AnimationResource.getJson;


/**
 * 动画配置工具
 */
public class AnimationSetting {

    /**
     * 加载模块
     * @param type
     * @return
     */
    public static AnimationConfig setting(AnimationType.Module type) {
        return getConfig(getJson().getString(type.name()));
    }

    /**
     * Boxing使用
     * @param status
     * @return
     */
    public static AnimationConfig setting(AnimationType.Boxing status) {
        return getConfig(getJson().getString(AnimationType.Module.Boxing.name()) + getJson().getString(status.name()));
    }


    /**
     * 男的被打程度，以及女的出击状态（将Sex传入不同参数可以进行转换）
     * @param lever
     * @param sex
     * @return
     */
    public static AnimationConfig setting(AnimationType.ManBeatenStatus lever, AnimationType.Sex sex) {
        return getConfig(getJson().getString(AnimationType.Module.Boxing.ManBeaten.name()) + getJson().getString(sex.name()) + getJson().getString(lever.name()));
    }

    /**
     * 女的被打程度，以及男的出击状态（将Sex传入不同参数可以进行转换）
     * @param lever
     * @param sex
     * @return
     */
    public static AnimationConfig setting(AnimationType.WoManBeatenStatus lever, AnimationType.Sex sex) {
        return getConfig(getJson().getString(AnimationType.Module.Boxing.WomanBeaten.name()) + getJson().getString(sex.name()) + getJson().getString(lever.name()));
    }

}
