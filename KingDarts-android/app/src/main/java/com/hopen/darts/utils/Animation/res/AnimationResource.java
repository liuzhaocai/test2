package com.hopen.darts.utils.Animation.res;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hopen.darts.base.BaseApplication;
import com.hopen.darts.base.C;
import com.hopen.darts.utils.Animation.IOUtils.IOUtils;
import com.hopen.darts.utils.Animation.bean.AnimationConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zhangyanxue on 2018/7/17.
 */

public class AnimationResource {

    /**
     * 从SD卡加载图片
     *
     * @param name
     * @return
     */
    public static Bitmap getImageFromLocal(AnimationConfig config, String name) {
        String imagePath = C.app.file_path_img + "/animation" + File.separator + config.getPath() + File.separator + name;
        File file = new File(imagePath);

        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            file.setLastModified(System.currentTimeMillis());
            return bitmap;
        }
        return null;
    }

    public static String getImagePathFromLocal(AnimationConfig config, String name) {
        String imagePath = C.app.file_path_img + "/animation" + File.separator + config.getPath() + File.separator + name;

        return imagePath;
    }

    public static Bitmap getBitmap(AnimationConfig config, String name){
        return getImageFromLocal(config,name);
    }



    public static AnimationConfig getConfig(String s) {
        String path = C.app.file_path_img + "/animation/"
                + s
                + "config.json";
        String io = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            io = IOUtils.toString(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.parseObject(io, AnimationConfig.class);
    }

    /**
     * 读取assets
     *
     * @param fileName
     * @return
     */
    public static String getJson(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = BaseApplication.getApplication().getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public static JSONObject getJson() {
        String config = getJson("config.json");
        return JSON.parseObject(config);
    }


}
