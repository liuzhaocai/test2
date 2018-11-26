package com.hopen.darts.game.rich;

public class LuckScore {
    private int score;
    private int times;

    public LuckScore() {
        this.score = 0;
        this.times = 0;
    }

    public LuckScore(int score) {
        this.score = score;
        this.times = 0;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void oneHit() {
        times++;
    }

    public void retreatHit() {
        times--;
    }
}
