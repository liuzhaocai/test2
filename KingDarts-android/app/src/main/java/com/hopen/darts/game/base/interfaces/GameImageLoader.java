package com.hopen.darts.game.base.interfaces;

import android.widget.ImageView;

/**
 * 游戏图片加载器
 */
public interface GameImageLoader {
    /**
     * 加载图片
     *
     * @param image_view 要加载到哪个ImageView
     * @param path       图片路径
     */
    void load(ImageView image_view, String path);
}
