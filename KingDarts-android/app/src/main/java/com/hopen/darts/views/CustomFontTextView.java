package com.hopen.darts.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hopen.darts.R;

/**
 * 可设置字体、描边的自定义TextView
 */

public class CustomFontTextView extends AppCompatTextView {

    private TextView strokeView;
    private int strokeColor;
    private float strokeWidth;

    public CustomFontTextView(Context context) {
        super(context);
        strokeView = new TextView(context);
        init(context, null);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        strokeView = new TextView(context, attrs);
        init(context, attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        strokeView = new TextView(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * 根据xml值自动设定字体
     *
     * @param context 上下文
     * @param attrs   xml集合
     */
    private void init(Context context, AttributeSet attrs) {
        strokeColor = Color.BLACK;
        strokeWidth = 1;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
            if (a != null) {
                strokeColor = a.getColor(R.styleable.CustomFontTextView_stroke_color, 0);
                strokeWidth = a.getDimension(R.styleable.CustomFontTextView_stroke_width, 0);
                a.recycle();
            }
        }
        setDefaultPadding();
        Typeface customFont = Typeface.DEFAULT_BOLD;
        setTypeface(customFont);
        strokeView.setTypeface(customFont);
        strokeView.setIncludeFontPadding(false);
        setIncludeFontPadding(false);
    }

    /**
     * 设置默认边距
     */
    private void setDefaultPadding() {
        int padding_left = getPaddingLeft(), padding_top = getPaddingTop(),
                padding_right = getPaddingRight(), padding_bottom = getPaddingBottom();
        if (padding_left < strokeWidth) padding_left = (int) (strokeWidth + 0.5f);
        if (padding_top < strokeWidth) padding_top = (int) (strokeWidth + 0.5f);
        if (padding_right < strokeWidth) padding_right = (int) (strokeWidth + 0.5f);
        if (padding_bottom < strokeWidth) padding_bottom = (int) (strokeWidth + 0.5f);
        setPadding(padding_left, padding_top, padding_right, padding_bottom);
        strokeView.setPadding(padding_left, padding_top, padding_right, padding_bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = strokeView.getText();

        //两个TextView上的文字必须一致
        if (tt == null || !tt.equals(this.getText())) {
            strokeView.setText(getText());
            this.postInvalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        strokeView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        strokeView.layout(left, top, right, bottom);
    }

    /**
     * 重写实现自定义描边
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // lazy load
        if (strokeColor != 0 && strokeWidth != 0) {
            TextPaint tp1 = strokeView.getPaint();
            tp1.set(getPaint());
            tp1.setStrokeWidth(strokeWidth * 1.5f);           //设置描边宽度
            tp1.setStyle(Paint.Style.STROKE);                 //对文字只描边
            tp1.setColor(strokeColor);
            strokeView.setTextColor(strokeColor);             //设置描边颜色
            strokeView.setGravity(getGravity());
            strokeView.draw(canvas);
        }
        super.onDraw(canvas);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (strokeView != null) {
            strokeView.setText(text, type);
        }
        super.setText(text, type);
    }

    @Override
    public void setTextSize(float size) {
        if (strokeView != null) {
            strokeView.setTextSize(size);
        }
        super.setTextSize(size);
    }

    @Override
    public void setTextSize(int unit, float size) {
        if (strokeView != null) {
            strokeView.setTextSize(unit, size);
        }
        super.setTextSize(unit, size);
    }

    /**
     * 设置描边颜色
     *
     * @param strokeColor 描边颜色
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    /**
     * 设置描边宽度
     *
     * @param strokeWidth 描边颜色
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        setDefaultPadding();
        invalidate();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        strokeView.setLayoutParams(params);
    }
}