package com.hopen.darts.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.hopen.darts.R;

/**
 * 全民大富豪血条控件
 */

public class GameRichHealthView extends View {
    /**
     * 百分比
     */
    private int percent;
    /**
     * 字体大小，字体间距
     */
    private int textSize = 20, verticalSpacing = 4, textHorizontalPadding = 8;
    /**
     * 血条中一个格子的宽高
     */
    private int healthOneWidth = 11, healthOneHeight = 26;
    /**
     * 血条中格子的左右边距
     */
    private int healthHorizontalPadding = 6, healthVerticalPadding = 3;
    /**
     * 各色格子的数量
     */
    private int greenNum = 14, yellowNum = 12, redNum = 15, totalNum = greenNum + yellowNum + redNum;
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

    public GameRichHealthView(Context context) {
        super(context);
        init(context, null);
    }

    public GameRichHealthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GameRichHealthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //初始化百分比
        percent = 4;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GameRichHealthView);
            if (a != null) {
                percent = a.getInt(R.styleable.GameRichHealthView_percent, 3);
                a.recycle();
            }
        }
        //文字画笔
        textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
        //初始化各个边距间距以及资源的值
        tWidth = healthOneWidth * totalNum + healthHorizontalPadding * 2;
        tHeight = textSize + verticalSpacing + healthOneHeight + healthVerticalPadding * 2;
        int health_bg_top = textSize + verticalSpacing;
        int health_top = health_bg_top + healthVerticalPadding;
        int health_bottom = tHeight - healthVerticalPadding;
        green = ContextCompat.getDrawable(context, R.mipmap.game_health_green);
        if (green != null)
            green.setBounds(0, health_top, healthOneWidth, health_bottom);
        yellow = ContextCompat.getDrawable(context, R.mipmap.game_health_yellow);
        if (yellow != null)
            yellow.setBounds(0, health_top, healthOneWidth, health_bottom);
        red = ContextCompat.getDrawable(context, R.mipmap.game_health_red);
        if (red != null)
            red.setBounds(0, health_top, healthOneWidth, health_bottom);
        background = ContextCompat.getDrawable(context, R.mipmap.rich_health_bg);
        if (background != null)
            background.setBounds(0, health_bg_top, tWidth, tHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(tWidth, tHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(getPercentText(), textHorizontalPadding, textSize, textPaint);
        if (background != null)
            background.draw(canvas);
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
                int left = healthHorizontalPadding + healthOneWidth * (i - 1);
                int right = left + healthOneWidth;
                Rect temp = item.getBounds();
                item.setBounds(left, temp.top, right, temp.bottom);
                item.draw(canvas);
            }
        }
    }

    private String getPercentText() {
        return percent + "%";
    }

    /**
     * 设置百分比
     *
     * @param percent 百分比
     */
    public void setPercent(@IntRange(from = 0, to = 100) int percent) {
        this.percent = percent;
        invalidate();
    }
}
