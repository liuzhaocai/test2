package com.hopen.darts.game.base;

import android.support.annotation.NonNull;

/**
 * Created by thomas on 2018/6/30.
 */

public abstract class GameEvent implements Comparable<GameEvent> {

    private Callback callback;
    private boolean isStart = false;
    private boolean isOver = false;
    private int level;

    public GameEvent(int level) {
        this.level = level;
    }

    public void start() {
        isStart = true;
        isOver = false;
        run();
    }

    protected abstract void run();

    public abstract void overNow();

    public void overCallback() {
        isOver = true;
        isStart = false;
        if (callback != null) callback.onOver();
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isOver() {
        return isOver;
    }

    public int getLevel() {
        return level;
    }

    void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public int compareTo(@NonNull GameEvent event) {
        return event.level - this.level;
    }

    public interface Callback {
        void onOver();
    }
}
