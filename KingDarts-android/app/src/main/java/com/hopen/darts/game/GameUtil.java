package com.hopen.darts.game;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hopen.darts.base.BaseApplication;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.BaseSeries;
import com.hopen.darts.game.base.GameMode;
import com.hopen.darts.game.base.Player;
import com.hopen.darts.game.blow.SeriesBlow;
import com.hopen.darts.game.combat.SeriesCombat;
import com.hopen.darts.game.game01.Series01;
import com.hopen.darts.game.game21.Series21;
import com.hopen.darts.game.hearts.SeriesHearts;
import com.hopen.darts.game.high.SeriesHigh;
import com.hopen.darts.game.mickey.SeriesMickey;
import com.hopen.darts.game.rich.SeriesRich;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 2018/5/25.
 */

public class GameUtil {
    private static BaseGame playingGame;
    private static JSONArray assetsGame;

    /**
     * 读取assets
     */
    private static void readAssetsGame() {
        if (assetsGame == null || assetsGame.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                AssetManager assetManager = BaseApplication.getApplication().getAssets();
                BufferedReader bf = new BufferedReader(new InputStreamReader(
                        assetManager.open("games.json")));
                String line;
                while ((line = bf.readLine()) != null) {
                    stringBuilder.append(line);
                }
                assetsGame = JSON.parseArray(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
                assetsGame = new JSONArray();
            }
        }
    }

    /**
     * 获取游戏系列名称
     *
     * @param series_id 游戏系列id
     * @return 游戏系列名称
     */
    public static String getSeriesName(String series_id) {
        readAssetsGame();
        for (int i = 0; i < assetsGame.size(); i++) {
            JSONObject series = assetsGame.getJSONObject(i);
            if (TextUtils.equals(series.getString("id"), series_id)) {
                return series.getString("name");
            }
        }
        return "";
    }

    /**
     * 获取游戏系列描述
     *
     * @param series_id 游戏系列id
     * @return 游戏系列描述
     */
    public static String getSeriesDescribe(String series_id) {
        readAssetsGame();
        for (int i = 0; i < assetsGame.size(); i++) {
            JSONObject series = assetsGame.getJSONObject(i);
            if (TextUtils.equals(series.getString("id"), series_id)) {
                return series.getString("desc");
            }
        }
        return "";
    }

    /**
     * 获取游戏系列名称
     *
     * @param series_id 游戏系列id
     * @param game_id   游戏id
     * @return 游戏名称
     */
    public static String getGameName(String series_id, String game_id) {
        readAssetsGame();
        for (int i = 0; i < assetsGame.size(); i++) {
            JSONObject series = assetsGame.getJSONObject(i);
            if (TextUtils.equals(series.getString("id"), series_id)) {
                JSONArray games = series.getJSONArray("games");
                for (int j = 0; j < games.size(); j++) {
                    JSONObject game = games.getJSONObject(j);
                    if (TextUtils.equals(game.getString("id"), game_id)) {
                        return game.getString("name");
                    }
                }
            }
        }
        return "";
    }

    /**
     * 获取游戏系列描述
     *
     * @param series_id 游戏系列id
     * @param game_id   游戏id
     * @return 游戏描述
     */
    public static String getGameDescribe(String series_id, String game_id) {
        readAssetsGame();
        for (int i = 0; i < assetsGame.size(); i++) {
            JSONObject series = assetsGame.getJSONObject(i);
            if (TextUtils.equals(series.getString("id"), series_id)) {
                JSONArray games = series.getJSONArray("games");
                for (int j = 0; j < games.size(); j++) {
                    JSONObject game = games.getJSONObject(j);
                    if (TextUtils.equals(game.getString("id"), game_id)) {
                        return game.getString("desc");
                    }
                }
            }
        }
        return "";
    }

    /**
     * 获取游戏系列列表
     *
     * @return 游戏系列列表
     */
    public static List<BaseSeries> getGameSeriesList() {
        ArrayList<BaseSeries> list = new ArrayList<>();
        list.add(new Series01());
        list.add(new SeriesMickey());
        list.add(new SeriesHigh());
        list.add(new SeriesHearts());
        list.add(new SeriesCombat());
        list.add(new SeriesRich());
        list.add(new Series21());
        list.add(new SeriesBlow());
        return list;
    }

    /**
     * 获取当前正在玩的游戏
     *
     * @return 当前正在玩的游戏
     */
    public static BaseGame getPlayingGame() {
        return playingGame;
    }

    /**
     * @return 是否已经进入游戏（正在准备状态或者进行中的状态都返回true）
     */
    public static boolean isEnter() {
        return isBeginning() || isPlaying();
    }

    /**
     * @return 本次游戏是否正在准备
     */
    public static boolean isBeginning() {
        return playingGame != null && playingGame.isBeginning();
    }

    /**
     * @return 本次游戏是否正在进行
     */
    public static boolean isPlaying() {
        return playingGame != null && playingGame.isPlaying();
    }

    /**
     * @return 本次游戏是否已经结束
     */
    public static boolean isOver() {
        return playingGame == null || playingGame.isOver();
    }

    /**
     * 选择一个要开始玩的游戏
     *
     * @param game 选择的游戏
     * @param mode 游戏人数模式
     */
    public static void chooseGame(BaseGame game, GameMode mode) {
        playingGame = game.newIns();
        playingGame.initGroups(mode);
    }

    /**
     * 开始玩一个游戏
     *
     * @param first 先攻玩家
     */
    public static void playGame(Player first) {
        if (playingGame.isOnline()) {
            throw new RuntimeException("这是一个网络对战游戏，请调用：GameUtil.onlineGame方法开始游戏");
        }
        playingGame.initGame(System.currentTimeMillis(), first);
    }

    /**
     * 开始玩一个网络对战
     *
     * @param local_flag 本机玩家在网络对战中的标识
     * @param start_time 联机游戏开始时间的时间戳(匹配成功的时间，由服务器返回)
     */
    public static void onlineGame(String local_flag, long start_time) {
        if (TextUtils.equals("player1", local_flag))
            playingGame.initOnlineGame(start_time, 1);
        else
            playingGame.initOnlineGame(start_time, 2);
    }

    /**
     * 游戏结束，清除游戏相关缓存
     */
    public static void gameOver() {
        playingGame = null;
    }
}
