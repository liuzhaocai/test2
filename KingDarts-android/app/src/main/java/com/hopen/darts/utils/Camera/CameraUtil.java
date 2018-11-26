package com.hopen.darts.utils.Camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.widget.FrameLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhangyanxue on 2018/5/28.
 */

public class CameraUtil {

    private static final String TAG = "CameraUtil";
    private Camera mCamera;
    private int mCameraId = 1;
    private int mOtherCameraId = 0;
    private Activity activity;
    private CameraPreview cameraPreview;
    //屏幕宽高
    private int screenWidth;
    private int screenHeight;
    private FrameLayout frameLayout;

    private static CameraUtil instance = null;
    private Camera.Parameters parameters;

    public static CameraUtil getInstance(Activity activity) {
        if (instance == null || instance.activity != activity) {
            if (instance != null) {
                instance.releaseCamera();
                instance.activity = null;
                instance.frameLayout = null;
                instance.cameraPreview = null;
                instance.parameters = null;
                instance = null;
            }
            instance = new CameraUtil(activity);
            return instance;
        } else {
            return instance;
        }
    }

    private CameraUtil(Activity activity) {
        this.activity = activity;
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }


    public void prepare() {
        mCamera = Camera.open(mCameraId);
    }


    /**
     * 获取Camera实例
     *
     * @return
     */
    private Camera getCamera(int id) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        try {
            mCamera = Camera.open(id);
        } catch (Exception e) {
            if (id == mCameraId && mCamera == null) {
                return getCamera(mOtherCameraId);
            }
        }
        if (id == mCameraId && mCamera == null) {
            return getCamera(mOtherCameraId);
        }
        return mCamera;
    }

    /**
     * 切换摄像头
     */
    public boolean switchCamera() {
        releaseCamera();
        mCameraId = (mCameraId + 1) % mCamera.getNumberOfCameras();
        mCamera = getCamera(mCameraId);
        if (cameraPreview != null) {
            return startPreview();
        }
        return false;
    }

    /**
     * 预览相机/打开相机
     */
    public boolean startPreview(FrameLayout frameLayout) {
        if (frameLayout == null) {
            Log.e(TAG, "请设置预览视图");
            return false;
        }
        mCamera = getCamera(mCameraId);
        this.frameLayout = frameLayout;
        cameraPreview = new CameraPreview(activity, mCamera);
        frameLayout.addView(cameraPreview);
        return startPreview();
    }


    /**
     * 刷新相机重新渲染
     */
    public boolean startPreview() {
        try {
            if (mCamera != null) {
                setupCamera(mCamera);
                mCamera.setPreviewDisplay(cameraPreview.getHolder());
                //亲测的一个方法 基本覆盖所有手机 将预览矫正
                setCameraDisplayOrientation(activity, mCameraId, mCamera);
//            camera.setDisplayOrientation(90);
                mCamera.startPreview();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 拍照
     *
     * @param call
     */
    public void captrue(final CameraCall call) {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //将data 转换为位图 或者你也可以直接保存为文件使用 FileOutputStream
                //这里我相信大部分都有其他用处把 比如加个水印 后续再讲解
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap saveBitmap = setTakePicktrueOrientation(mCameraId, bitmap);
                call.takePhoto(saveBitmap);
            }
        });
    }

    /**
     * 拍照得到路径
     *
     * @param call
     */
    public void captruePath(final CameraCallData call) {
        mCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                try {
                    Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
                    BitmapFactory.Options newOpts = new BitmapFactory.Options();
                    newOpts.inJustDecodeBounds = true;
                    YuvImage yuvimage = new YuvImage(
                            data,
                            ImageFormat.NV21,
                            previewSize.width,
                            previewSize.height,
                            null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
                    call.takePhoto(baos.toByteArray());
                }catch (Throwable e){
                    e.printStackTrace();
                    call.takePhoto(new byte[0]);
                }
            }
        });
//
//        mCamera.takePicture(null, null, new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//
//            }
//        });
    }

    /**
     * 设置
     */
    private void setupCamera(Camera camera) {
        parameters = camera.getParameters();

        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
        Camera.Size previewSize = getPropSizeForHeight(parameters.getSupportedPreviewSizes(), 800);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictrueSize = getPropSizeForHeight(parameters.getSupportedPictureSizes(), 800);
        parameters.setPictureSize(pictrueSize.width, pictrueSize.height);
        camera.setParameters(parameters);
        /**
         * 设置surfaceView的尺寸 因为camera默认是横屏，所以取得支持尺寸也都是横屏的尺寸
         * 我们在startPreview方法里面把它矫正了过来，但是这里我们设置设置surfaceView的尺寸的时候要注意 previewSize.height<previewSize.width
         * previewSize.width才是surfaceView的高度
         * 一般相机都是屏幕的宽度 这里设置为屏幕宽度 高度自适应 你也可以设置自己想要的大小
         *
         */

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((frameLayout.getLayoutParams().height * pictrueSize.width) / pictrueSize.height, frameLayout.getLayoutParams().height);
        //这里当然可以设置拍照位置 比如居中 我这里就置顶了
        //params.gravity = Gravity.CENTER;
        cameraPreview.setLayoutParams(params);
    }

    /**
     * 设置相机预览视图
     */
    private void setupCameraView() {

    }

    /**
     * 释放相机资源
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 获取所有支持的返回视频尺寸
     *
     * @param list
     * @param minHeight
     * @return
     */
    public Camera.Size getPropSizeForHeight(List<Camera.Size> list, int minHeight) {
        Collections.sort(list, new CameraAscendSizeComparatorForHeight());

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minHeight)) {
                Log.i(TAG, "s.height===" + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }

    //升序 按照高度
    public class CameraAscendSizeComparatorForHeight implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.height == rhs.height) {
                return 0;
            } else if (lhs.height > rhs.height) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * 保证预览方向正确
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    public void setCameraDisplayOrientation(Activity activity,
                                            int cameraId, Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public Bitmap setTakePicktrueOrientation(int id, Bitmap bitmap) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(id, info);
        bitmap = rotaingImageView(id, info.orientation, bitmap);
        return bitmap;
    }

    /**
     * 把相机拍照返回照片转正
     *
     * @param angle 旋转角度
     * @return bitmap 图片
     */
    public Bitmap rotaingImageView(int id, int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        //加入翻转 把相机拍照返回照片转正
        if (id == 1) {
            matrix.postScale(-1, 1);
        }
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 判断摄像头是否可用
     *
     * @return
     */
    public static boolean checkCameraHardware() {

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

    public interface CameraCall {
        void takePhoto(Bitmap bitmap);
    }

    public interface CameraCallData {
        void takePhoto(byte[] data);
    }

}
