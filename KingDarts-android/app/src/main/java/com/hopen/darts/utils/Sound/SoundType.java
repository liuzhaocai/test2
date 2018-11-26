package com.hopen.darts.utils.Sound;

public class SoundType {

    /**
     * 点击音
     */
    public int btn_click;

    /**
     * 获胜
     */
    public int win;

    /**
     * 击中20的三倍区
     */
    public int shoot_twenty_double_score;

    /**
     * 击中单倍区
     */
    public int shoot_one_score;

    /**
     * 击中单倍区
     */
    public int shoot_center_score;

    /**
     * 击中三倍区
     */
    public int shoot_ter_score;

    /**
     * 击中双倍区
     */
    public int shoot_double_score;

    /**
     * 类别和人数选择时左右滑动
     */
    public int scroll_menu;

    /**
     * 提示框
     */
    public int message;

    /**
     * bust
     */
    public int bust;

    /**
     * miss
     */
    public int miss;

    /**
     * 根据枚举获取id
     * @param soundEnum
     * @return
     */
    public int getSoundId(SoundEnum soundEnum){
        switch (soundEnum){
            case BTN_CLICK:
                return btn_click;
            case WIN:
                return win;
            case SHOOT_TWENTY_DOUBLE_SCORE:
                return shoot_twenty_double_score;
            case SHOOT_ONE_SCORE:
                return shoot_one_score;
            case SHOOT_CENTER_SCORE:
                return shoot_center_score;
            case SHOOT_TER_SCORE:
                return shoot_ter_score;
            case SHOOT_DOUBLE_SCORE:
                return shoot_double_score;
            case SCROLL_MENU:
                return scroll_menu;
            case MESSAGE:
                return message;
            case BUST:
                return bust;
            case MISS:
                return miss;
        }
        return 0;
    }

}
