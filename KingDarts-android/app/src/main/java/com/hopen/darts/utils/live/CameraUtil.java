package com.hopen.darts.utils.live;

import android.hardware.Camera;

/**
 * Created by apple on 2018/7/17.
 */

public class CameraUtil {
    /**
     * 判断摄像头是否可用
     * @return
     */
    public static boolean isCameraCanUse() {
        boolean canUse = false;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }
        if (mCamera != null) {
            mCamera.release();
            canUse = true;
        }
        return canUse;
    }
}
