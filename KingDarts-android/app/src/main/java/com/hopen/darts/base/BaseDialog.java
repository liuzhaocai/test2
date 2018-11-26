package com.hopen.darts.base;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.hopen.darts.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.animation.AnimationUtils.loadAnimation;


/**
 * Created by SEELE on 2017/6/10.
 */

public abstract class BaseDialog extends DialogFragment {
    protected View tDialogShadow;
    protected boolean clickOutToDismiss = true;
    protected boolean clickBackToDismiss = true;
    protected Animation tDialogShadowInAnim;
    protected Animation tDialogShadowOutAnim;
    protected View tDialogContent;
    protected List<View> tDialogChild = new ArrayList<>();
    protected List<Animation> tDialogChildInAnim = new ArrayList<>();
    protected List<Animation> tDialogChildOutAnim = new ArrayList<>();
    protected View tDialogCurtain;
    protected boolean autoUnifyAnimationDuration = true;
    protected boolean autoSetFillAfterToTrue = true;
    protected long tAnimationDuration;
    protected int tShadowBackgroundRes = 0;
    protected OnShowWithAnimStartListener onShowWithAnimStartListener;
    protected OnShowWithAnimEndListener onShowWithAnimEndListener;
    protected OnDismissWithAnimStartListener onDismissWithAnimStartListener;
    protected OnDismissWithAnimEndListener onDismissWithAnimEndListener;
    private boolean isBeforeCreateDialogView = true;
    private boolean isDismissing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.ThomasDialogTheme);
        tAnimationDuration = 250;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            if (clickBackToDismiss) dismiss();
                            break;
                        case KeyEvent.ACTION_DOWN:
                            break;
                    }
                    return true;
                }
                return false;
            }
        });
        tDialogContent = createDialogView(inflater, container, savedInstanceState);
        isBeforeCreateDialogView = false;
        tDialogShadow = new View(getActivity());
        if (tShadowBackgroundRes == 0)
            tShadowBackgroundRes = R.color.shadow_bg;
        tDialogShadow.setBackgroundResource(tShadowBackgroundRes);
        tDialogShadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickOutToDismiss)
                    dismiss();
            }
        });
        tDialogCurtain = new View(getActivity());
        tDialogCurtain.setBackgroundColor(0x00000000);
        tDialogCurtain.setVisibility(View.GONE);
        tDialogCurtain.setClickable(true);
        FrameLayout parent = new FrameLayout(getActivity());
        parent.addView(tDialogShadow);
        parent.addView(tDialogContent);
        parent.addView(tDialogCurtain);
        //检查是否设置了动画，没有的话则初始化为默认动画
        initDefaultAnim();
        baseInitAnimListener();
        //同步阴影和内容的动画执行时间
        unifyAnimationDuration();
        //开始执行动画
        tDialogShadow.startAnimation(tDialogShadowInAnim);
        for (int i = 0; i < tDialogChild.size(); i++) {
            if (tDialogChildInAnim.get(i) != null)
                tDialogChild.get(i).startAnimation(tDialogChildInAnim.get(i));
        }
        return parent;
    }

    private void baseInitAnimListener() {
        tDialogShadowInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                try {
                    if (onShowWithAnimStartListener != null)
                        onShowWithAnimStartListener.OnShowWithAnimStart();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    if (onShowWithAnimEndListener != null)
                        onShowWithAnimEndListener.OnShowWithAnimEnd();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tDialogShadowOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                try {
                    if (onDismissWithAnimStartListener != null)
                        onDismissWithAnimStartListener.DismissWithAnimStart();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                superDismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    protected void superDismiss() {
        try {
            if (onDismissWithAnimEndListener != null)
                onDismissWithAnimEndListener.onDismissWithAnimEnd();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    BaseDialog.super.dismiss();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }, 30);
    }

    @Override
    public void dismiss() {
        if (isDismissing) {
            return;
        }
        if (tDialogCurtain == null || tDialogShadow == null) {
            superDismiss();
        } else {
            tDialogCurtain.setVisibility(View.VISIBLE);
            tDialogCurtain.setClickable(true);
            isDismissing = true;
            tDialogShadow.startAnimation(tDialogShadowOutAnim);
            for (int i = 0; i < tDialogChild.size(); i++) {
                if (tDialogChildOutAnim.get(i) != null)
                    tDialogChild.get(i).startAnimation(tDialogChildOutAnim.get(i));
            }
        }
    }

    protected abstract View createDialogView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 检查是否设置了动画，没有的话则初始化为默认动画
     */
    private void initDefaultAnim() {
        //dialog阴影默认动画初始化
        if (tDialogShadowInAnim == null) {
            tDialogShadowInAnim = new AlphaAnimation(0, 1);
            tDialogShadowInAnim.setFillAfter(true);
            tDialogShadowInAnim.setDuration(tAnimationDuration);
        }
        if (tDialogShadowOutAnim == null) {
            tDialogShadowOutAnim = new AlphaAnimation(1, 0);
            tDialogShadowOutAnim.setFillAfter(true);
            tDialogShadowOutAnim.setDuration(tAnimationDuration);
        }
        //dialog内容默认动画
        if (tDialogChild.size() == 0 || tDialogChildInAnim.size() == 0 || tDialogChildOutAnim.size() == 0) {
            tDialogChild.clear();
            tDialogChild.add(tDialogContent);
            tDialogChildInAnim.clear();
            tDialogChildInAnim.add(tDialogShadowInAnim);
            tDialogChildOutAnim.clear();
            tDialogChildOutAnim.add(tDialogShadowOutAnim);
        }
    }

    /**
     * 同步阴影和内容的动画执行时间
     */
    protected void unifyAnimationDuration() {
        if (autoUnifyAnimationDuration) {
            if (tDialogShadowInAnim != null) {
                for (Animation item : tDialogChildInAnim) {
                    if (item != null && item.getDuration() != tDialogShadowInAnim.getDuration())
                        item.setDuration(tDialogShadowInAnim.getDuration());
                }
            }
            if (tDialogShadowOutAnim != null) {
                for (Animation item : tDialogChildOutAnim) {
                    if (item != null && item.getDuration() != tDialogShadowOutAnim.getDuration())
                        item.setDuration(tDialogShadowOutAnim.getDuration());
                }
            }
        }
    }

    /**
     * 检查添加动画方法的执行位置
     */
    private void check() {
        if (!isBeforeCreateDialogView) {
            throw new RuntimeException("必须在createDialogView方法中才能使用");
        }
    }

    /**
     * 添加一组动画
     *
     * @param view     要执行动画的view
     * @param in_anim  开始动画
     * @param out_anim 结束动画
     */
    public void addAnimation(View view, @NonNull int in_anim, int out_anim) {
        addAnimation(view, AnimationUtils.loadAnimation(getActivity(), in_anim),
                AnimationUtils.loadAnimation(getActivity(), out_anim));
    }

    /**
     * 添加一组动画
     *
     * @param view     要执行动画的view
     * @param in_anim  开始动画
     * @param out_anim 结束动画
     */
    public void addAnimation(View view, Animation in_anim, Animation out_anim) {
        if (autoUnifyAnimationDuration) {
            if (tDialogShadowInAnim != null && in_anim != null
                    && in_anim.getDuration() != tDialogShadowInAnim.getDuration())
                in_anim.setDuration(tDialogShadowInAnim.getDuration());
            if (tDialogShadowInAnim != null && out_anim != null
                    && out_anim.getDuration() != tDialogShadowOutAnim.getDuration())
                out_anim.setDuration(tDialogShadowOutAnim.getDuration());
        }
        if (autoSetFillAfterToTrue) {
            if (in_anim != null)
                in_anim.setFillAfter(true);
            if (out_anim != null)
                out_anim.setFillAfter(true);
        }
        if (tDialogChild.contains(view)) {
            int index = tDialogChild.indexOf(view);
            tDialogChildInAnim.set(index, in_anim);
            tDialogChildOutAnim.set(index, out_anim);
        } else {
            tDialogChild.add(view);
            tDialogChildInAnim.add(in_anim);
            tDialogChildOutAnim.add(out_anim);
        }
    }

    private void checkRepeatAnim() {

    }

    /**
     * 设置阴影动画
     *
     * @param in_anim  开始动画
     * @param out_anim 结束动画
     */
    public void setAnimationShadow(int in_anim, int out_anim) {
        check();
        tDialogShadowInAnim = loadAnimation(getActivity(), in_anim);
        tDialogShadowOutAnim = loadAnimation(getActivity(), out_anim);
    }

    /**
     * 设置阴影动画
     *
     * @param in_anim  开始动画
     * @param out_anim 结束动画
     */
    public void setAnimationShadow(Animation in_anim, Animation out_anim) {
        check();
        tDialogShadowInAnim = in_anim;
        tDialogShadowOutAnim = out_anim;
        if (autoSetFillAfterToTrue) {
            if (tDialogShadowInAnim != null)
                in_anim.setFillAfter(true);
            if (tDialogShadowOutAnim != null)
                out_anim.setFillAfter(true);
        }
    }

    /**
     * 设置背景
     *
     * @param res xml背景资源
     */
    public void setShadowRes(int res) {
        tShadowBackgroundRes = res;
        if (tDialogShadow != null)
            tDialogShadow.setBackgroundResource(tShadowBackgroundRes);
    }

    /**
     * 设置在显示时，并且在动画开始时的监听
     *
     * @param onShowWithAnimStartListener 监听回调
     */
    public void setOnShowWithAnimStartListener(OnShowWithAnimStartListener onShowWithAnimStartListener) {
        this.onShowWithAnimStartListener = onShowWithAnimStartListener;
    }

    /**
     * 设置在显示时，并且在动画结束时的监听
     *
     * @param onShowWithAnimEndListener 监听回调
     */
    public void setOnShowWithAnimEndListener(OnShowWithAnimEndListener onShowWithAnimEndListener) {
        this.onShowWithAnimEndListener = onShowWithAnimEndListener;
    }

    /**
     * 设置在dismiss时，并且动画开始时的监听
     *
     * @param onDismissWithAnimStartListener 监听回调
     */
    public void setOnDismissWithAnimStartListener(OnDismissWithAnimStartListener onDismissWithAnimStartListener) {
        this.onDismissWithAnimStartListener = onDismissWithAnimStartListener;
    }

    /**
     * 设置在dismiss时，并且动画结束时的监听
     *
     * @param onDismissWithAnimEndListener 监听回调
     */
    public void setOnDismissWithAnimEndListener(OnDismissWithAnimEndListener onDismissWithAnimEndListener) {
        this.onDismissWithAnimEndListener = onDismissWithAnimEndListener;
    }

    /**
     * 是否自动同步动画时间
     *
     * @return 是否自动同步动画时间
     */
    public boolean isAutoUnifyAnimationDuration() {
        return autoUnifyAnimationDuration;
    }

    /**
     * 设置是否自动同步动画时间，默认为是
     *
     * @param autoUnifyAnimationDuration 是否自动同步动画时间
     */
    public void setAutoUnifyAnimationDuration(boolean autoUnifyAnimationDuration) {
        this.autoUnifyAnimationDuration = autoUnifyAnimationDuration;
    }

    /**
     * 设置点击外部是否消失
     *
     * @param clickOutToDismiss 点击外部是否消失
     */
    public void setClickOutToDismiss(boolean clickOutToDismiss) {
        this.clickOutToDismiss = clickOutToDismiss;
    }

    /**
     * 设置点击返回键是否消失
     *
     * @param clickBackToDismiss 点击返回键是否消失
     */
    public void setClickBackToDismiss(boolean clickBackToDismiss) {
        this.clickBackToDismiss = clickBackToDismiss;
    }

    public int show(FragmentTransaction transaction) {
        return super.show(transaction, getClass().getSimpleName());
    }

    public void show(FragmentManager manager) {
        super.show(manager, getClass().getSimpleName());
    }

    public interface OnShowWithAnimStartListener {
        void OnShowWithAnimStart();
    }

    public interface OnShowWithAnimEndListener {
        void OnShowWithAnimEnd();
    }

    public interface OnDismissWithAnimStartListener {
        void DismissWithAnimStart();
    }

    public interface OnDismissWithAnimEndListener {
        void onDismissWithAnimEnd();
    }
}
