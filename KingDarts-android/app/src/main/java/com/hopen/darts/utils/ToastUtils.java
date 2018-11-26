package com.hopen.darts.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hopen.darts.base.BaseApplication;


/**
 * Toast工具类
 */
public class ToastUtils {
    public static void toast(String content) {
        Toast.makeText(BaseApplication.getContext(), content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 是否打开Toast显示开关
     */
    private static boolean isShow = true;

    private static Toast sToast;

    public static void destroy(){
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
    }

    /**
     * 最常用的提示文本
     */
    public static void show(String message) {
        if (isShow) {
            if (sToast != null) {
                sToast.cancel();
                sToast = null;
            }
            sToast = Toast.makeText(BaseApplication.getApplication(), message, Toast.LENGTH_SHORT);
            sToast.show();
        }
    }


    /**
     * 直接显示文本
     *
     * @param messageId 需要显示的文字
     */
    public static void show(int messageId) {
        if (isShow) {
            if (sToast != null) {
                sToast.cancel();
                sToast = null;
            }
            sToast = Toast.makeText(BaseApplication.getApplication(), messageId, Toast.LENGTH_SHORT);
            sToast.show();
        }
    }


    /**
     * 直接显示文本
     *
     * @param messageId 需要显示的文字
     */
    public static void showShort(int messageId) {
        if (isShow) {
            if (sToast != null) {
                sToast.cancel();
                sToast = null;
            }
            sToast = Toast.makeText(BaseApplication.getApplication(), messageId, Toast.LENGTH_SHORT);
            sToast.show();
        }
    }

    /**
     * 直接显示文本
     *
     * @param message 需要显示的文字
     */
    public static void showShort(String message) {
        if (isShow) {
            if (sToast != null) {
                sToast.cancel();
                sToast = null;
            }
            sToast = Toast.makeText(BaseApplication.getApplication(), message, Toast.LENGTH_SHORT);
            sToast.show();
        }
    }

    /**
     * 直接显示文本
     *
     * @param messageId 需要显示的文字
     */
    public static void showLong(int messageId) {
        if (isShow) {
            if (sToast != null) {
                sToast.cancel();
                sToast = null;
            }
            sToast = Toast.makeText(BaseApplication.getApplication(), messageId, Toast.LENGTH_LONG);
            sToast.show();
        }
    }

    /**
     * 直接显示文本
     *
     * @param message 需要显示的文字
     */
    public static void showLong(String message) {
        if (isShow) {
            if (sToast != null) {
                sToast.cancel();
                sToast = null;
            }
            sToast = Toast.makeText(BaseApplication.getApplication(), message, Toast.LENGTH_LONG);
            sToast.show();
        }
    }

    /**
     * 直接显示文本
     *
     * @param messageId 需要显示的文字资源
     * @param duration  自定义显示时间
     */
    public static void show(int messageId, int duration) {
        if (isShow) {
            if (sToast != null) {
                sToast.cancel();
                sToast = null;
            }
            sToast = Toast.makeText(BaseApplication.getApplication(), messageId, duration);
            sToast.show();
        }
    }

    /**
     * 直接显示文本
     *
     * @param message  需要显示的文字
     * @param duration 自定义显示时间
     */
    public static void show(String message, int duration) {
        if (isShow) {
            if (sToast != null) {
                sToast.cancel();
                sToast = null;
            }
            sToast = Toast.makeText(BaseApplication.getApplication(), message, duration);
            sToast.show();
        }
    }

    /**
     * 带图片消息提示
     *
     * @param ImageResourceId 图片资源
     * @param messageId       文字资源
     */
    public static void showImageAndText(int ImageResourceId, int messageId) {
        Context context = BaseApplication.getApplication();
        showImageAndText(ImageResourceId, context.getResources().getString(messageId), Toast.LENGTH_SHORT, Gravity.CENTER);
    }

    /**
     * 带图片消息提示
     *
     * @param ImageResourceId 图片资源
     * @param message         文字
     */
    public static void showImageAndText(int ImageResourceId, CharSequence message) {
        showImageAndText(ImageResourceId, message, Toast.LENGTH_SHORT, Gravity.CENTER);
    }

    public static void showImageAndText(int ImageResourceId, CharSequence message, int duration, int gravity) {
        Toast toast = Toast.makeText(BaseApplication.getApplication(),
                message, duration);
        toast.setGravity(gravity, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(BaseApplication.getApplication());
        imageCodeProject.setImageResource(ImageResourceId);
        toastView.addView(imageCodeProject, 0);
        toast.show();
    }
}
