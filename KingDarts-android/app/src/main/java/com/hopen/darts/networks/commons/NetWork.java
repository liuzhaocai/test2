package com.hopen.darts.networks.commons;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hopen.darts.base.C;
import com.hopen.darts.networks.dialog.NetDialogUtil;
import com.hopen.darts.networks.interfaces.NetDialog;
import com.hopen.darts.networks.interfaces.OnErrorCallback;
import com.hopen.darts.networks.interfaces.OnSuccessCallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by weitong on 16/7/4.
 */
public class NetWork {
    public static final String TAG = "network";
    private static NetDialog netDialog = NetDialogUtil.getInstance();
    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    public static void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();
    }

    /**
     * 解析返回的数据
     *
     * @param baseRequest 请求对象
     * @param response    返回数据
     * @return 解析后的数据对象
     */
    private static BaseResponse analysis(BaseRequest baseRequest, final Response response) {
        try {
            final String result = response.body().string();
            Log.i(TAG, "请求成功 success -> 原始数据 : " + result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");
            String data = jsonObject.getString("data");
            if ((TextUtils.isEmpty(data) || TextUtils.isEmpty(result)) && !C.network.code_success.equals(code)) {
                if (C.network.code_invalid.equals(code)) {
                    return new BaseResponse("invalid", C.network.code_invalid);
                }
                Log.i(TAG, "请求回数据解析错误 - > 原始数据 : " + result);
                String err_msg = TextUtils.isEmpty(msg) ? "网络服务错误" : msg;
                return new BaseResponse(err_msg, C.network.code_fail);
            }
            Log.i(TAG, "请求成功 success - > 数据体 : " + data);
            return createResponse(baseRequest, jsonObject.toJSONString());
        } catch (Exception e) {
            Log.e(TAG, "请求过程中发生错误。错误信息如下 : " + e.toString());
            if (e instanceof ClassNotFoundException)
                return new BaseResponse("请求错误", C.network.code_fail);
            else
                return new BaseResponse("网络服务错误", C.network.code_fail);
        }
    }

    private static BaseResponse createResponse(BaseRequest base_request, String result) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        //得到数据接收类
        String requestClassName = base_request.getClass().getName();
        String responseClassName = requestClassName.replaceFirst("request", "response").replaceFirst("Request", "Response");
        Log.i(TAG, "请求完成，生成接收类 -> \n requestClassName = " + requestClassName + "\n  responseClassName = " + responseClassName);
        Class responseClass = Class.forName(responseClassName);
        BaseResponse baseResponse = (BaseResponse) JSON.parseObject(result, responseClass);
        return baseResponse;
    }

    private static void success(final Activity activity, final OnSuccessCallback successCallback, final OnErrorCallback errorCallback, final BaseResponse response) {
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                try {
                    if (netDialog != null)
                        netDialog.hide();
                    if (successCallback != null)
                        successCallback.onSuccess(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorCallback.onError(new BaseResponse("处理错误", C.network.code_handle_error));
                }
            }
        };
        if (activity == null)
            new Handler(Looper.getMainLooper()).post(callback);
        else
            activity.runOnUiThread(callback);
    }

    private static void error(Activity activity, final OnErrorCallback errorCallback, final BaseResponse error) {
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                try {
                    if (netDialog != null)
                        netDialog.hide();
                    if (errorCallback != null)
                        errorCallback.onError(error);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        if (activity == null)
            new Handler(Looper.getMainLooper()).post(callback);
        else
            activity.runOnUiThread(callback);
    }

    /**
     * 网络请求
     *
     * @param activity        所在activity
     * @param baseRequest     请求信息封装对象
     * @param successCallback 请求成功监听
     */
    public static void request(final Activity activity, final BaseRequest baseRequest, final OnSuccessCallback successCallback) {
        request(activity, baseRequest, successCallback, new OnErrorCallback() {
            @Override
            public void onError(BaseResponse baseResponse) {
                Toast.makeText(activity, baseResponse.getMsg(), Toast.LENGTH_SHORT).show();
            }
        }, true);
    }

    /**
     * 网络请求
     *
     * @param activity        所在activity
     * @param baseRequest     请求信息封装对象
     * @param successCallback 请求成功监听
     */
    public static void request(final Activity activity, final BaseRequest baseRequest, final OnSuccessCallback successCallback, boolean isShowDialog) {
        request(activity, baseRequest, successCallback, new OnErrorCallback() {
            @Override
            public void onError(BaseResponse baseResponse) {
                Toast.makeText(activity, baseResponse.getMsg(), Toast.LENGTH_SHORT).show();
            }
        }, isShowDialog);
    }

    /**
     * 网络请求
     *
     * @param activity        所在activity
     * @param baseRequest     请求信息封装对象
     * @param successCallback 请求成功监听
     */
    public static void request(final Activity activity, final BaseRequest baseRequest, final OnSuccessCallback successCallback, final OnErrorCallback errorCallback) {
        request(activity, baseRequest, successCallback, errorCallback, true);
    }

    /**
     * 网络请求
     *
     * @param activity        所在activity
     * @param baseRequest     请求信息封装对象
     * @param successCallback 请求成功监听
     * @param errorCallback   请求失败监听
     */
    public static void request(final Activity activity, final BaseRequest baseRequest, final OnSuccessCallback successCallback,
                               final OnErrorCallback errorCallback, boolean isShowDialog) {
        if (netDialog != null && isShowDialog)
            netDialog.show(activity);
        Log.i(TAG, "开始请求 接口名 -> " + baseRequest.getMethodName());
        final Request request = baseRequest.buildRequest();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException error) {
                Log.e(TAG, "网络请求失败，请检查网络质量 : error = " + error + "msg = " + error.getMessage());
                error(activity, errorCallback, new BaseResponse("请求超时", C.network.code_fail));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                BaseResponse base_response = analysis(baseRequest, response);
                switch (base_response.getCode()) {
                    case C.network.code_success:
                        success(activity, successCallback, errorCallback, base_response);
                        break;
                    case C.network.code_fail:
                        error(activity, errorCallback, base_response);
                        break;
                    case C.network.code_invalid:
                        break;
                }
            }
        });
    }

//    public static void upload() {
//        try {
//            String ext = "";
//            if (!TextUtils.isEmpty(filePath)) {
//                int location = filePath.lastIndexOf(".");
//                if (location != -1 && location < filePath.length() - 1) {
//                    ext = filePath.substring(location + 1);
//                }
//            }
//            MediaType mediaType = MediaType.parse("application/octet-stream");
//            if (!TextUtils.isEmpty(ext)) {
//                mediaType = MediaType.parse(MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext));
//            }
//            MultipartBody.Builder body_builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//            File old_file = new File(filePath);
//            if (is_compressor) {
//                File compressor_file = new Compressor(activity)
//                        .setMaxWidth(200)
//                        .setMaxHeight(200)
//                        .setQuality(75)
//                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
//                        .setDestinationDirectoryPath(Environment.getExternalStorageDirectory()
//                                .getAbsolutePath() + C.app.file_path_img_compress)
//                        .compressToFile(old_file);
//                body_builder.addFormDataPart("file", compressor_file.getName(), RequestBody.create(mediaType, compressor_file));
//            } else {
//                body_builder.addFormDataPart("file", old_file.getName(), RequestBody.create(mediaType, old_file));
//            }
//            Request request = new Request.Builder().url(C.network.file_url).post(body_builder.build()).build();
//            Call call = mOkHttpClient.newCall(request);
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException error) {
//                    Log.e(TAG, "图片上传失败，请检查网络质量 : error = " + error + "msg = " + error.getMessage());
//                    error(activity, errorResponse, new BaseResponse("图片上传失败", C.network.code_fail));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    final String result = response.body().string();
//                    try {
//                        Log.i(TAG, "请求成功 success -> 原始数据 : " + result);
//                        JSONObject jsonObject = JSONObject.parseObject(result);
//                        String code = jsonObject.getString("code");
//                        String msg = jsonObject.getString("msg");
//                        String data = jsonObject.getString("data");
//                        if ((TextUtils.isEmpty(data) || TextUtils.isEmpty(result)) && !C.network.code_success.equals(code)) {
//                            if (C.network.code_invalid.equals(code)) {
//                                return;
//                            }
//                            Log.i(TAG, "请求回数据解析错误 - > 原始数据 : " + result);
//                            String err_msg = TextUtils.isEmpty(msg) ? "网络服务错误" : msg;
//                            error(activity, errorResponse, new BaseResponse(err_msg, C.network.code_fail));
//                            return;
//                        }
//                        Log.i(TAG, "请求成功 success - > 数据体 : " + data);
//                        success(activity, successResponse, new BaseResponse("", C.network.code_success));
//                    } catch (Exception e) {
//                        Log.e(TAG, "请求过程中发生错误。错误信息如下 : " + e.toString());
//                        error(activity, errorResponse, new BaseResponse("网络服务错误", C.network.code_fail));
//                    }
//                }
//            });
//        } catch (Throwable e) {
//            e.printStackTrace();
//            error(activity, errorResponse, new BaseResponse("请求错误", C.network.code_fail));
//        }
//    }
}
