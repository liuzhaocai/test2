package com.hopen.darts.utils.AlertMessage;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseApplication;
import com.hopen.darts.base.BaseDialog;
import com.hopen.darts.base.C;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PopGifDialog extends BaseDialog {
    @BindView(R.id.gif_view)
    GifImageView gifView;
    Unbinder unbinder;

    private Uri uri = null;
    private boolean looping = false;
    private int gifWidth = ViewGroup.LayoutParams.MATCH_PARENT, gifHeight = ViewGroup.LayoutParams.MATCH_PARENT;
    private ImageView.ScaleType scaleType = null;
    private int[] mRules = new int[22];

    @Override
    protected View createDialogView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_t_gif, null, false);
        unbinder = ButterKnife.bind(this, view);
        initGif();
        return view;
    }

    /**
     * 初始化gif
     */
    private void initGif() {
        try {
            GifDrawable gif = new GifDrawable(getActivity().getContentResolver(), uri);
            gif.setLoopCount(looping ? 0 : 1);
            gif.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    if (!looping) dismiss();
                }
            });
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) gifView.getLayoutParams();
            boolean set_params = false;
            if (gifWidth != ViewGroup.LayoutParams.MATCH_PARENT || gifHeight != ViewGroup.LayoutParams.MATCH_PARENT) {
                params.width = gifWidth;
                params.height = gifHeight;
                set_params = true;
            }
            for (int i = 0; i < mRules.length; i++) {
                if (mRules[i] != 0) {
                    params.addRule(i, mRules[i]);
                    set_params = true;
                }
            }
            if (set_params) gifView.setLayoutParams(params);
            if (scaleType != null) gifView.setScaleType(scaleType);
            gifView.setImageDrawable(gif);
        } catch (IOException e) {
            e.printStackTrace();
            dismiss();
        }
    }

    /**
     * 设置播放资源
     *
     * @param path_enum 资源路径枚举
     * @return 链式调用，返回自身
     */
    public PopGifDialog path(VideoEnum path_enum) {
        uri = Uri.parse(C.app.file_path_audio + "/" + path_enum.name() + ".gif");
        return this;
    }

    /**
     * 设置播放资源
     *
     * @param gif_res 资源id
     * @return 链式调用，返回自身
     */
    public PopGifDialog res(int gif_res) {
        uri = Uri.parse("android.resource://" + BaseApplication.getApplication().getPackageName() + "/" + gif_res);
        return this;
    }

    /**
     * 设置gif宽度
     *
     * @param width 宽度的值
     * @return 链式调用，返回自身
     */
    public PopGifDialog width(int width) {
        gifWidth = width;
        return this;
    }

    /**
     * 设置gif高度
     *
     * @param height 高度的值
     * @return 链式调用，返回自身
     */
    public PopGifDialog height(int height) {
        gifHeight = height;
        return this;
    }

    /**
     * 设置将图像的边界缩放到该视图边界的选项。
     *
     * @param scale_type 将图像的边界缩放到该视图边界的选项。
     * @return 链式调用，返回自身
     */
    public PopGifDialog scaleType(ImageView.ScaleType scale_type) {
        this.scaleType = scale_type;
        return this;
    }

    /**
     * 添加由RelativeLayout解释的布局规则。
     * 同{@link android.widget.RelativeLayout.LayoutParams#addRule(int)}
     *
     * @param verb 布局动词, 例如 {@link RelativeLayout#ALIGN_PARENT_LEFT}
     * @return 链式调用，返回自身
     */
    public PopGifDialog addRule(int verb) {
        return addRule(verb, RelativeLayout.TRUE);
    }


    /**
     * 添加由RelativeLayout解释的布局规则。
     * 同{@link android.widget.RelativeLayout.LayoutParams#addRule(int, int)}
     *
     * @param verb    布局动词, 例如 {@link RelativeLayout#ALIGN_PARENT_LEFT}
     * @param subject 用作锚点的另一个控件的id，或者用{@link RelativeLayout#TRUE}来表示真假
     * @return 链式调用，返回自身
     */
    public PopGifDialog addRule(int verb, int subject) {
        mRules[verb] = subject;
        return this;
    }

    /**
     * 是否循环播放
     *
     * @param looping 是否循环播放
     * @return 链式调用，返回自身
     */
    public PopGifDialog looping(boolean looping) {
        this.looping = looping;
        return this;
    }

    /**
     * 弹窗关闭回调监听
     *
     * @param listener 弹窗关闭回调监听
     * @return 链式调用，返回自身
     */
    public PopGifDialog dismissListener(OnDismissWithAnimEndListener listener) {
        setOnDismissWithAnimEndListener(listener);
        return this;
    }

    /**
     * 展示此弹窗
     *
     * @param activity 所在activity
     */
    public void show(Activity activity) {
        show(activity.getFragmentManager());
    }

    /**
     * 入口方法
     *
     * @return 返回一个弹窗对象
     */
    public static PopGifDialog get() {
        return new PopGifDialog();
    }

}
