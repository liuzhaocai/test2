package com.hopen.darts.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.hopen.darts.R;

/**
 * 拳皇格斗血条控件
 */

public class GameCombatHealthView extends View {
    /**
     * 百分比
     */
    private int currentHealth = 15, totalHealth = 15;
    /**
     * 字体大小，字体间距
     */
    private int textSize = 16, textHorizontalPadding = 12;
    /**
     * 血条中一个格子的宽高
     */
    private int healthOneWidth = 11, healthOneHeight = 26;
    /**
     * 血条中格子的左右边距
     */
    private int healthPaddingLeft = 24, healthPaddingRight = 5, healthVerticalPadding = 3;
    /**
     * 各色格子的数量
     */
    private int greenNum = 10, yellowNum = 10, redNum = 9, totalNum = greenNum + yellowNum + redNum;
    /**
     * 控件的大小
     */
    private int tWidth, tHeight;
    /**
     * 文字画笔
     */
    private TextPaint textPaint;
    /**
     * 血条资源
     */
    private Drawable green, yellow, red, background;

    public GameCombatHealthView(Context context) {
        super(context);
        init(context, null);
    }

    public GameCombatHealthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GameCombatHealthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GameCombatHealthView);
            if (a != null) {
                //总血量
                totalHealth = a.getInt(R.styleable.GameCombatHealthView_total_health, 15);
                //当前血量
                currentHealth = a.getInt(R.styleable.GameCombatHealthView_current_health, 15);
                a.recycle();
            }
        }
        //文字画笔
        textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        //初始化各个边距间距以及资源的值
        tWidth = healthOneWidth * totalNum + healthPaddingLeft + healthPaddingRight;
        tHeight = healthOneHeight + healthVerticalPadding * 2;
        int health_bottom = tHeight - healthVerticalPadding;
        green = ContextCompat.getDrawable(context, R.mipmap.game_health_green);
        if (green != null)
            green.setBounds(0, healthVerticalPadding, healthOneWidth, health_bottom);
        yellow = ContextCompat.getDrawable(context, R.mipmap.game_health_yellow);
        if (yellow != null)
            yellow.setBounds(0, healthVerticalPadding, healthOneWidth, health_bottom);
        red = ContextCompat.getDrawable(context, R.mipmap.game_health_red);
        if (red != null)
            red.setBounds(0, healthVerticalPadding, healthOneWidth, health_bottom);
        background = ContextCompat.getDrawable(context, R.mipmap.rich_health_bg);
        if (background != null)
            background.setBounds(0, 0, tWidth, tHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(tWidth, tHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int percent = (int) (currentHealth * 100f / totalHealth + 0.5f);
        if (background != null)
            background.draw(canvas);
        canvas.drawText(currentHealth + "", textHorizontalPadding, tHeight * 0.7f, textPaint);
        int show_num = (int) (percent * totalNum / 100f + 0.5f);
        if (show_num > totalNum) show_num = totalNum;
        if (show_num == 0 && percent != 0)
            show_num = 1;
        for (int i = 1; i <= show_num; i++) {
            Drawable item;
            if (i <= redNum) {
                item = red;
            } else if (redNum < i && i <= redNum + yellowNum) {
                item = yellow;
            } else {
                item = green;
            }
            if (item != null) {
                int left = healthPaddingLeft + healthOneWidth * (i - 1);
                int right = left + healthOneWidth;
                Rect temp = item.getBounds();
                item.setBounds(left, temp.top, right, temp.bottom);
                item.draw(canvas);
            }
        }
    }

    /**
     * 设置血量
     *
     * @param current 当前血量
     * @param total   总血量
     */
    public void setHealth(int current, int total) {
        this.currentHealth = current;
        this.totalHealth = total;
        invalidate();
    }
}

