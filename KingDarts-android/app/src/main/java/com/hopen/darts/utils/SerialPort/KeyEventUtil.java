package com.hopen.darts.utils.SerialPort;

import android.view.KeyEvent;

import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.utils.Sound.SoundEnum;
import com.hopen.darts.utils.Sound.SoundPlayUtils;

/**
 * 键盘事件映射类
 */

public class KeyEventUtil {

    public static boolean onKeyDown(final BaseActivity activity, final int keyCode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SoundPlayUtils.play(SoundEnum.BTN_CLICK);
                switch (keyCode) {
                    case KeyEvent.KEYCODE_A:
                        activity.onKeyLeft();
                        break;
                    case KeyEvent.KEYCODE_W:
                        activity.onKeyTop();
                        break;
                    case KeyEvent.KEYCODE_D:
                        activity.onKeyRight();
                        break;
                    case KeyEvent.KEYCODE_S:
                        activity.onKeyBottom();
                        break;
                    case KeyEvent.KEYCODE_V:
                        activity.onKeyConfirm();
                        break;
                    case KeyEvent.KEYCODE_B:
                        activity.onKeyCancel();
                        break;
                    case KeyEvent.KEYCODE_P:
                        activity.onKeySetting();
                        break;
                }
            }
        });

        return true;
    }

}
