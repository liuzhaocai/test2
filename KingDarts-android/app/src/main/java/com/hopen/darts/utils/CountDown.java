package com.hopen.darts.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by thomas on 2017/10/25.
 * 按钮控件倒计时工具
 * 最简洁的调用模式如下：
 * {@link CountDown}.{@link CountDown#with(TextView)}.{@link CountDown#start()};
 * 会按照默认的透明度来设置不可点击时的状态样式
 */
public class CountDown {
    private static LinkedList<CountDown> countDowns;

    /**
     * 按钮倒计时入口方法，可以直接在{@link CountDown#with(TextView)}
     * 之后调用{@link CountDown#start()}方法，
     * 直接调用会按照默认的透明度来设置不可点击时的状态样式
     *
     * @param textView 需要显示倒计时的控件
     * @return 返回一个倒计时操作对象
     */
    public static CountDown with(TextView textView) {
        if (textView == null) {
            return null;
        }
        if (countDowns == null) countDowns = new LinkedList<>();
        for (CountDown item : countDowns) {
            if (item.textView.equals(textView)) {
                if (item.myCountDownTimer != null)
                    item.myCountDownTimer.cancel();
                return item;
            }
        }
        CountDown item = new CountDown(textView);
        countDowns.add(item);
        return item;
    }

    /**
     * 私有构造方法
     *
     * @param textView 需要显示倒计时的控件
     */
    private CountDown(TextView textView) {
        this.textView = textView;
        this.millisInFuture = 60000;
        this.countDownInterval = 1000;
        if (this.textView.getBackground() != null) {
            this.normalBackGroundAlpha = this.textView.getBackground().getAlpha();
            //android中从同一个资源文件中加载出来的drawable会共享状态，
            //如果你加载出来多个drawable，当改变了其中一个的状态时，其他drawable的状态也会相应改变。
            //如果把这个drawable变为mutate drawable后，这个drawable就不会与其他drawable共享状态。
            //特别注意，这个mutate操作是不可逆转的。
            this.normalBackGroundDrawable = this.textView.getBackground().mutate();
        }
        this.normalTextColor = this.textView.getTextColors();
        this.unableBackGroundDrawable = null;
        this.unableTextColor = this.textView.getTextColors().withAlpha(unableTextAlpha);
        this.unablePrefix = "";
        this.unablePostfix = "s";
        this.overText = this.textView.getText().toString();
        this.onCountDownTickHandler = null;
        this.onCountDownFinishHandler = null;
    }

    /**
     * 设置总时长
     *
     * @param millisInFuture 总时长
     * @return 链式调用, 返回自身
     */
    public CountDown millisLength(long millisInFuture) {
        this.millisInFuture = millisInFuture;
        return this;
    }

    /**
     * 设置间隔
     *
     * @param countDownInterval 间隔
     * @return 链式调用, 返回自身
     */
    public CountDown interval(long countDownInterval) {
        this.countDownInterval = countDownInterval;
        if (this.countDownInterval < 100) this.countDownInterval = 100;
        return this;
    }

    /**
     * 设置正常的控件背景透明度，如果没有特殊需求，不需要设置此属性
     * 此属性会在{@link CountDown#with(TextView)}中自动获取输入控件的初始背景透明度
     *
     * @param normalBackGroundAlpha 正常的控件背景透明度
     * @return 链式调用, 返回自身
     */
    public CountDown normalBackGroundAlpha(int normalBackGroundAlpha) {
        this.normalBackGroundAlpha = normalBackGroundAlpha;
        return this;
    }

    /**
     * 设置正常的控件背景，如果没有特殊需求，不需要设置此属性
     * 此属性会在{@link CountDown#with(TextView)}中自动获取输入控件的初始背景
     *
     * @param normalBackGroundResources 正常的控件背景资源id
     * @return 链式调用, 返回自身
     */
    public CountDown normalBackGround(int normalBackGroundResources) {
        this.normalBackGroundDrawable = ContextCompat.getDrawable(textView.getContext(),
                normalBackGroundResources);
        return this;
    }

    /**
     * 设置正常字体颜色，如果没有特殊需求，不需要设置此属性
     * 此属性会在{@link CountDown#with(TextView)}中自动获取输入控件的初始字体颜色
     *
     * @param normalTextColorResources 正常字体颜色资源id
     * @return 链式调用, 返回自身
     */
    public CountDown normalTextColorResources(int normalTextColorResources) {
        int temp_color = ContextCompat.getColor(textView.getContext(), normalTextColorResources);
        this.normalTextColor = ColorStateList.valueOf(temp_color);
        return this;
    }

    /**
     * 设置正常字体颜色，如果没有特殊需求，不需要设置此属性
     * 此属性会在{@link CountDown#with(TextView)}中自动获取输入控件的初始字体颜色
     *
     * @param normalTextColor 正常字体颜色16进制色值
     * @return 链式调用, 返回自身
     */
    public CountDown normalTextColor(int normalTextColor) {
        this.normalTextColor = ColorStateList.valueOf(normalTextColor);
        return this;
    }

    /**
     * 设置不可点击时具有透明效果的透明度，同时设置字体和背景
     * 设置此属性后可以不设置{@link CountDown#unableBackGroundDrawable}
     * 也可以不设置{@link CountDown#unableTextColor}
     * 当然也可以设置以上两属性，只是设置后透明度效果将不对该条属性生效
     * 该透明度有默认值，在不设置{@link CountDown#unableBackGroundDrawable}和
     * {@link CountDown#unableTextColor}时，将根据该默认值对控件的不可点击效果设置透明度
     *
     * @param unableAlpha 透明度(0-255)
     * @return 链式调用, 返回自身
     */
    public CountDown unableAlpha(@IntRange(from = 0, to = 255) int unableAlpha) {
        this.unableBackGroundAlpha = unableAlpha;
        this.unableTextAlpha = unableAlpha;
        this.unableBackGroundDrawable = null;
        this.unableTextColor = this.textView.getTextColors().withAlpha(this.unableTextAlpha);
        return this;
    }

    /**
     * 设置不可点击时具有透明效果的透明度，同时设置字体和背景
     * 设置此属性后可以不设置{@link CountDown#unableBackGroundDrawable}
     * 设置此属性后可以不设置{@link CountDown#unableTextColor}
     * 当然也可以设置以上两属性，只是设置后透明度效果将不对该条属性生效
     * 该透明度有默认值，在不设置{@link CountDown#unableBackGroundDrawable}和
     * {@link CountDown#unableTextColor}时，将根据该默认值对控件的不可点击效果设置透明度
     *
     * @param unableBackGroundAlpha 背景透明度(0-255)
     * @param unableTextAlpha       字体透明度(0-255)
     * @return 链式调用, 返回自身
     */
    public CountDown unableAlpha(@IntRange(from = 0, to = 255) int unableBackGroundAlpha,
                                 @IntRange(from = 0, to = 255) int unableTextAlpha) {
        this.unableBackGroundAlpha = unableBackGroundAlpha;
        this.unableTextAlpha = unableTextAlpha;
        this.unableBackGroundDrawable = null;
        this.unableTextColor = this.textView.getTextColors().withAlpha(this.unableTextAlpha);
        return this;
    }

    /**
     * 设置不可点击时背景的透明度
     * 设置此属性后可以不设置{@link CountDown#unableBackGroundDrawable}
     * 当然也可以设置以上属性，只是设置后透明度效果将不对该条属性生效
     * 该透明度有默认值，在不设置{@link CountDown#unableBackGroundDrawable}时
     * 将根据该默认值对控件的不可点击效果设置透明度
     *
     * @param unableBackGroundAlpha 透明度(0-255)
     * @return 链式调用, 返回自身
     */
    public CountDown unableBackGroundAlpha(@IntRange(from = 0, to = 255) int unableBackGroundAlpha) {
        this.unableBackGroundAlpha = unableBackGroundAlpha;
        this.unableBackGroundDrawable = null;
        return this;
    }

    /**
     * 设置不可点击时字体的透明度
     * 设置此属性后可以不设置{@link CountDown#unableTextColor}
     * 当然也可以设置以上属性，只是设置后透明度效果将不对该条属性生效
     * 该透明度有默认值，在不设置{@link CountDown#unableTextColor}
     * 将根据该默认值对控件的不可点击效果设置透明度
     *
     * @param unableTextAlpha 透明度(0-255)
     * @return 链式调用, 返回自身
     */
    public CountDown unableTextAlpha(@IntRange(from = 0, to = 255) int unableTextAlpha) {
        this.unableTextAlpha = unableTextAlpha;
        this.unableTextColor = this.textView.getTextColors().withAlpha(this.unableTextAlpha);
        return this;
    }

    /**
     * 设置不能点击时背景
     *
     * @param unableBackGroundResources 不能点击时背景
     * @return 链式调用, 返回自身
     */
    public CountDown unableBackGround(int unableBackGroundResources) {
        this.unableBackGroundDrawable = ContextCompat.getDrawable(textView.getContext(),
                unableBackGroundResources);
        return this;
    }

    /**
     * 设置不能点击时字体颜色
     *
     * @param unableTextColorResources 不能点击时字体颜色资源id
     * @return 链式调用, 返回自身
     */
    public CountDown unableTextColorResources(int unableTextColorResources) {
        int temp_color = ContextCompat.getColor(textView.getContext(), unableTextColorResources);
        this.unableTextColor = ColorStateList.valueOf(temp_color);
        return this;
    }

    /**
     * 设置不能点击时字体颜色
     *
     * @param unableTextColor 不能点击时字体颜色16进制色值
     * @return 链式调用, 返回自身
     */
    public CountDown unableTextColor(int unableTextColor) {
        this.unableTextColor = ColorStateList.valueOf(unableTextColor);
        return this;
    }

    /**
     * 设置倒计时后缀
     *
     * @param unablePrefix 倒计时前缀
     * @return 链式调用, 返回自身
     */
    public CountDown unablePrefix(String unablePrefix) {
        this.unablePrefix = unablePrefix;
        return this;
    }

    /**
     * 设置倒计时后缀
     *
     * @param unablePostfix 倒计时后缀
     * @return 链式调用, 返回自身
     */
    public CountDown unablePostfix(String unablePostfix) {
        this.unablePostfix = unablePostfix;
        return this;
    }

    /**
     * 设置倒计时结束后的文字
     *
     * @param overText 倒计时结束后的文字
     * @return 链式调用, 返回自身
     */
    public CountDown overText(String overText) {
        this.overText = overText;
        return this;
    }

    /**
     * 自定义倒计时onTick处理方法
     *
     * @param onCountDownTickHandler 自定义处理类
     * @return 链式调用, 返回自身
     */
    public CountDown countDownTickHandler(OnCountDownTickHandler onCountDownTickHandler) {
        this.onCountDownTickHandler = onCountDownTickHandler;
        return this;
    }

    /**
     * 自定义倒计时onFinish处理方法
     *
     * @param onCountDownFinishHandler 自定义处理类
     * @return 链式调用, 返回自身
     */
    public CountDown countDownFinishHandler(OnCountDownFinishHandler onCountDownFinishHandler) {
        this.onCountDownFinishHandler = onCountDownFinishHandler;
        return this;
    }

    /**
     * 不使用默认onTick处理方法
     *
     * @return 链式调用, 返回自身
     */
    public CountDown notDefaultCountDownTick() {
        this.onCountDownTickHandler = new OnCountDownTickHandler() {
            @Override
            public boolean onTick(long millisUntilFinished) {
                return true;
            }
        };
        return this;
    }

    /**
     * 不使用默认onFinish处理方法
     *
     * @return 链式调用, 返回自身
     */
    public CountDown notDefaultCountDownFinish() {
        this.onCountDownFinishHandler = new OnCountDownFinishHandler() {
            @Override
            public boolean onFinish() {
                return true;
            }
        };
        return this;
    }

    /**
     * 开始倒计时
     */
    public void start() {
        textView.setClickable(false);
        if (unableBackGroundDrawable == null) {
            if (normalBackGroundDrawable != null) {
                normalBackGroundDrawable.setAlpha(unableBackGroundAlpha);
                textView.setBackground(normalBackGroundDrawable);
            }
        } else
            textView.setBackground(unableBackGroundDrawable);
        textView.setTextColor(unableTextColor);
        myCountDownTimer = new MyCountDownTimer();
        myCountDownTimer.start();
    }

    /**
     * 开始倒计时
     */
    public void simpleStart() {
        myCountDownTimer = new MyCountDownTimer();
        myCountDownTimer.start();
    }

    /**
     * 控件(TextView类型)
     */
    private TextView textView;
    /**
     * 总时长
     */
    private long millisInFuture;
    /**
     * 间隔
     */
    private long countDownInterval;
    /**
     * 控件初始情况下背景的透明度
     */
    private int normalBackGroundAlpha;
    /**
     * 正常的控件背景
     */
    private Drawable normalBackGroundDrawable;
    /**
     * 正常字体颜色
     */
    private ColorStateList normalTextColor;
    /**
     * 不可点击时背景的透明度
     */
    private int unableBackGroundAlpha = 122;
    /**
     * 不可点击时字体的透明度
     */
    private int unableTextAlpha = 122;
    /**
     * 不能点击时背景
     */
    private Drawable unableBackGroundDrawable;
    /**
     * 不能点击时字体颜色
     */
    private ColorStateList unableTextColor;
    /**
     * 倒计时前缀
     */
    private String unablePrefix;
    /**
     * 倒计时后缀
     */
    private String unablePostfix;
    /**
     * 倒计时结束后的文字
     */
    private String overText;
    /**
     * 倒计时计时器
     */
    private MyCountDownTimer myCountDownTimer;
    /**
     * 自定义{@link CountDownTimer#onTick(long)}的操作
     */
    private OnCountDownTickHandler onCountDownTickHandler;
    /**
     * 自定义{@link CountDownTimer#onFinish()}的操作
     */
    private OnCountDownFinishHandler onCountDownFinishHandler;

    private class MyCountDownTimer extends CountDownTimer {

        MyCountDownTimer() {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long millisUntilFinished) {
            if (onCountDownTickHandler == null || !onCountDownTickHandler.onTick(millisUntilFinished))
                textView.setText(unablePrefix + (millisUntilFinished / 1000) + unablePostfix);
        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            if (onCountDownFinishHandler == null || !onCountDownFinishHandler.onFinish()) {
                if (unableBackGroundDrawable == null && normalBackGroundDrawable != null) {
                    normalBackGroundDrawable.setAlpha(normalBackGroundAlpha);
                    textView.setBackground(normalBackGroundDrawable);
                }
                textView.setTextColor(normalTextColor);
                textView.setText(overText);
                textView.setClickable(true);
            }
            countDowns.remove(CountDown.this);
        }
    }

    /**
     * {@link CountDownTimer#onTick(long)}处理回调
     */
    public interface OnCountDownTickHandler {
        /**
         * 处理{@link CountDownTimer#onTick(long)}事件
         *
         * @param millisUntilFinished 剩余时间
         * @return 是否消费此事件
         */
        boolean onTick(long millisUntilFinished);
    }

    /**
     * {@link CountDownTimer#onFinish()}处理回调
     */
    public interface OnCountDownFinishHandler {
        /**
         * 处理{@link CountDownTimer#onFinish()}事件
         *
         * @return 是否消费此事件
         */
        boolean onFinish();
    }
}
