package com.hopen.darts.utils.AlertMessage;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseDialog;
import com.hopen.darts.views.CustomFontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PushMessageDialog extends BaseDialog {

    @BindView(R.id.content_tv)
    CustomFontTextView contentTv;
    Unbinder unbinder;

    String content;
    boolean autoDismiss = true;
    View.OnKeyListener customOnKeyListener;
    int dismissTime = 1500;
    boolean isDismissed = false;

    @Override
    protected View createDialogView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_start_game, null, false);
        unbinder = ButterKnife.bind(this, view);
        setShadowRes(R.color.transparent);
        contentTv.setText(content);
        if (autoDismiss) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long show_time = System.currentTimeMillis();
                        while (!isDismissed) {
                            if (System.currentTimeMillis() - show_time >= dismissTime && !isDismissed) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            dismiss();
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                return;
                            }
                            Thread.sleep(10);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        if (customOnKeyListener != null) {
            view.setFocusableInTouchMode(true);
            view.setOnKeyListener(customOnKeyListener);
        } else {
            setClickOutToDismiss(false);
            setClickBackToDismiss(false);
        }
        return view;
    }

    @Override
    public void dismiss() {
        isDismissed = true;
        super.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 刷新弹窗展示的信息
     *
     * @param content 要展示的信息
     */
    public void refresh(String content) {
        if (contentTv == null || TextUtils.isEmpty(content) || TextUtils.equals(this.content, content))
            return;
        this.content = content;
        contentTv.setText(this.content);

    }

    /**
     * 弹窗提示内容
     *
     * @param content 弹窗提示内容
     * @return 链式调用，返回自身
     */
    public PushMessageDialog text(String content) {
        this.content = content;
        return this;
    }

    /**
     * 弹窗是否自动关闭
     *
     * @param auto_dismiss 弹窗是否自动关闭
     * @return 链式调用，返回自身
     */
    public PushMessageDialog autoDismiss(Boolean auto_dismiss) {
        this.autoDismiss = auto_dismiss;
        return this;
    }

    /**
     * 弹窗自动关闭时的持续时间
     *
     * @param dismiss_time 弹窗自动关闭时的持续时间
     * @return 链式调用，返回自身
     */
    public PushMessageDialog dismissTime(int dismiss_time) {
        this.dismissTime = dismiss_time;
        return this;
    }

    /**
     * 弹窗按键回调监听
     *
     * @param listener 弹窗按键回调监听
     * @return 链式调用，返回自身
     */
    public PushMessageDialog keyListener(View.OnKeyListener listener) {
        this.customOnKeyListener = listener;
        return this;
    }

    /**
     * 弹窗关闭回调监听
     *
     * @param listener 弹窗关闭回调监听
     * @return 链式调用，返回自身
     */
    public PushMessageDialog dismissListener(OnDismissWithAnimEndListener listener) {
        this.setOnDismissWithAnimEndListener(listener);
        return this;
    }

    /**
     * 展示此弹窗
     *
     * @param activity 所在activity
     */
    public void show(Activity activity) {
        try {
            if (!activity.isFinishing())
                show(activity.getFragmentManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 入口方法
     *
     * @return 一个dialog对象
     */
    public static PushMessageDialog get() {
        return new PushMessageDialog();
    }
}
