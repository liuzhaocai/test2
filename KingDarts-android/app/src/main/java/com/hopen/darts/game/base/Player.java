package com.hopen.darts.game.base;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.alibaba.fastjson.annotation.JSONField;
import com.hopen.darts.game.base.interfaces.GameImageLoader;

/**
 * 玩家
 */

public class Player implements Comparable<Player> {
    @JSONField(serialize = false)
    Group group;
    @JSONField(serialize = false)
    int id;
    @JSONField(serialize = false)
    String name;
    @JSONField(serialize = false)
    String head;
    @JSONField(serialize = false)
    int hitNum;
    @JSONField(serialize = false)
    int score;
    @JSONField(serialize = false)
    float ppr;
    @JSONField(serialize = false)
    float ppd;
    @JSONField(serialize = false)
    float mpr;
    @JSONField(serialize = false)
    GameImageLoader headLoader;

    Player(Group group, @IntRange(from = 1) int player_id) {
        this.group = group;
        id = player_id;
        name = "player" + id;
        head = "";
        hitNum = 0;
        score = 0;
        headLoader = this.group.mGame.defaultHeadLoader;
    }

    /**
     * 获取玩家所在阵营(队伍)
     *
     * @return 玩家所在阵营(队伍)
     */
    @JSONField(serialize = false)
    public Group getGroup() {
        return group;
    }

    /**
     * 获取玩家id
     *
     * @return 玩家id
     */
    @JSONField(name = "playerId")
    public int getId() {
        return id;
    }

    /**
     * 获取玩家名
     *
     * @return 玩家名
     */
    @JSONField(name = "playerName")
    public String getName() {
        return name;
    }

    /**
     * 获取玩家头像
     *
     * @return 玩家头像
     */
    @JSONField(serialize = false)
    public String getHead() {
        return head;
    }

    /**
     * 获取玩家投掷镖数
     *
     * @return 玩家投掷镖数
     */
    @JSONField(name = "hitNum")
    public int getHitNum() {
        return hitNum;
    }

    /**
     * 获取玩家平均每回合分数
     *
     * @return 玩家平均每回合分数
     */
    @JSONField(name = "PPR")
    public float getPPR() {
        return ppr;
    }

    /**
     * 获取玩家平均每镖分数
     *
     * @return 玩家平均每镖分数
     */
    @JSONField(name = "PPD")
    public float getPPD() {
        return ppd;
    }

    /**
     * 获取玩家MPR(米老鼠专用)
     *
     * @return 玩家MPR
     */
    @JSONField(name = "MPR")
    public float getMPR() {
        return mpr;
    }

    /**
     * 获取玩家总分数
     *
     * @return 玩家总分数
     */
    @JSONField(name = "playerScore")
    public int getScore() {
        return score;
    }

    /**
     * @return 获取头像加载器
     */
    @JSONField(serialize = false)
    public GameImageLoader getHeadLoader() {
        return headLoader;
    }

    /**
     * 设置玩家名
     *
     * @param name 玩家名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置玩家头像
     *
     * @param head 玩家头像
     */
    public void setHead(String head) {
        this.head = head;
    }

    /**
     * 加载玩家头像
     *
     * @param image_view 要加载到的ImageView
     */
    public void loadHead(ImageView image_view) {
        headLoader.load(image_view, head);
    }

    /**
     * 对player进行一次掷镖操作
     *
     * @param hit_score 此镖的分数
     */
    void oneHit(int hit_score) {
        hitNum++;
        score += hit_score;
        calculatePPRPPD();
    }

    /**
     * 对player进行一次退镖操作
     *
     * @param hit_score 此镖的分数
     */
    void retreatHit(int hit_score) {
        hitNum--;
        score -= hit_score;
        calculatePPRPPD();
    }

    /**
     * 计算ppr和ppd
     */
    void calculatePPRPPD() {
        group.mGame.getGameRule().calculatePlayerInfo(this);
    }

    /**
     * 比较两个玩家的分数(排名)
     *
     * @param player 玩家
     * @return <0:player2分数高(排名靠前)，0:两者分数相等，0<:player1分数高(排名靠前)
     */
    @Override
    public int compareTo(@NonNull Player player) {
        return 0;
    }
}
