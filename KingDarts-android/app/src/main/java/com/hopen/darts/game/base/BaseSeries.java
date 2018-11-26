package com.hopen.darts.game.base;

import com.hopen.darts.R;
import com.hopen.darts.game.GameUtil;

import java.util.List;

/**
 * 游戏系列基类
 */

public abstract class BaseSeries {

    /**
     * 获取游戏系列id
     *
     * @return 游戏系列id
     */
    public abstract String getSeriesId();

    /**
     * 获取游戏系列名称
     *
     * @return 游戏系列名称
     */
    public String getSeriesName() {
        return GameUtil.getSeriesName(getSeriesId());
    }

    /**
     * 获取游戏系列描述
     *
     * @return 游戏系列描述
     */
    public String getSeriesDescribe() {
        return GameUtil.getSeriesDescribe(getSeriesId());
    }

    /**
     * 获取游戏系列在首页的介绍图片
     *
     * @return 游戏系列在首页的介绍图片
     */
    public int getSeriesDescribeImg() {
        return R.mipmap.game_main_series_describe_img;
    }

    /**
     * 获取本系列包括的游戏列表
     *
     * @return 本系列包括的游戏列表
     */
    public abstract List<BaseGame> getGameList();
}
