/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.hopen.darts.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.Bitmap.CompressFormat.JPEG;

/**
 * @author jarlen
 */
public class FileUtils {

    public static String SDCARD_PAHT = Environment
            .getExternalStorageDirectory().getPath();

    public static String DCIMCamera_PATH = Environment
            .getExternalStorageDirectory() + "/DCIM/Camera/";

    /**
     * 检测sdcard是否可用
     *
     * @return true为可用; false为不可用
     */
    public static boolean isSDAvailable() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED))
            return false;
        return true;
    }

    public static Bitmap ResizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

    public static Bitmap ResizeBitmap(Bitmap bitmap, int scale) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(1 / scale, 1 / scale);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

    public static String getNewFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());

        return formatter.format(curDate);
    }

    /**
     * @param context 上下文
     * @param bm      要保存的bitmap
     * @param name    保存的名字 可为null,就根据时间自定义一个文件名
     * @return 以“.jpg”格式保存至相册
     */
    public static Boolean saveBitmapToCamera(Context context, Bitmap bm,
                                             String name) {

        File file = null;

        if (name == null || name.equals("")) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date curDate = new Date(System.currentTimeMillis());
            name = formatter.format(curDate) + ".jpg";
        }

        file = new File(DCIMCamera_PATH, name);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;

        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);

        return true;
    }

    /**
     *
     * @param context
     * @param bmp
     * @param picName
     * @param exifInterface
     */
    public static String saveBmp2Gallery(Context context, Bitmap bmp, String picName, ExifInterface exifInterface) {
        String fileName = null;
        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;


        // 声明文件对象
        File file = null;
        // 声明输出流
        FileOutputStream outStream = null;

        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(galleryPath, picName + ".jpg");
            if (file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            // 获得文件相对路径
            fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
            if (null != outStream) {
                bmp.compress(JPEG, 90, outStream);
            }

        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (exifInterface!=null){
            try {
                ExifInterface exifInterface1 = new ExifInterface(file.getAbsolutePath());
                final String exifLatitude = exifInterface1.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                if (TextUtils.isEmpty(exifLatitude)){
                    copyExc(exifInterface1,exifInterface, ExifInterface.TAG_GPS_LATITUDE);
                    copyExc(exifInterface1,exifInterface, ExifInterface.TAG_GPS_LONGITUDE);
                    copyExc(exifInterface1,exifInterface, ExifInterface.TAG_GPS_LONGITUDE_REF);
                    copyExc(exifInterface1,exifInterface, ExifInterface.TAG_GPS_LATITUDE_REF);
                    copyExc(exifInterface1,exifInterface, ExifInterface.TAG_GPS_ALTITUDE_REF);
                    exifInterface1.saveAttributes();
                }
                exifInterface1.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(),
                bmp, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        return file.getAbsolutePath();
    }

    public static void copyExc(ExifInterface ex1, ExifInterface ex2, String tag){
        ex1.setAttribute(tag, ex2.getAttribute(tag));
    }

    /**
     * @param bmp     获取的bitmap数据
     * @param picName 自定义的图片名
     */
    public static String saveBmp2Gallery(Context context, Bitmap bmp, String picName) {
        return saveBmp2Gallery(context,bmp,picName,null);
    }

    /**
     * 更新媒体库
     * @param context
     * @param bmp
     * @param filePath
     */
    public static void updataMeida(Context context, Bitmap bmp, String filePath){
        File file = new File(filePath);
        MediaStore.Images.Media.insertImage(context.getContentResolver(),
                bmp, file.getName(), null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * @param bitmap
     * @param destPath
     * @param quality
     */
    public static void writeImage(Bitmap bitmap, String destPath, int quality) {
        try {
            deleteFile(destPath);
            if (createFile(destPath)) {
                FileOutputStream out = new FileOutputStream(destPath);
                if (bitmap.compress(JPEG, quality, out)) {
                    out.flush();
                    out.close();
                    out = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeData(byte[] data, String destPath) {
        if (new File(destPath).getParentFile().exists() == false){
            new File(destPath).getParentFile().mkdirs();
        }
        try {
            deleteFile(destPath);
            if (createFile(destPath)) {
                FileOutputStream out = new FileOutputStream(destPath);
                out.write(data);
                out.flush();
                out.close();
                out = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                return file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 删除一个文件
     *
     * @param filePath 要删除的文件路径名
     * @return true if this file was deleted, false otherwise
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除 dir目录下的所有文件，包括删除删除文件夹
     *
     * @param dir
     */
    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                deleteDirectory(listFiles[i]);
            }
        }
        dir.delete();
    }

}
