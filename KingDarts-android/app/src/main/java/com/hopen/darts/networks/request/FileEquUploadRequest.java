package com.hopen.darts.networks.request;

import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.BaseRequest;
import com.hopen.darts.networks.commons.MethodType;
import com.hopen.darts.utils.PhoneUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by zhangyanxue on 2018/7/30.
 */

public class FileEquUploadRequest extends BaseRequest {

    public FileEquUploadRequest(String filePath){
        super(MethodType.POST);
        setMethodName("/file/equ/upload");
        addHeader("equno", PhoneUtil.readSNCode());
        addHeader("x-access-token", KDManager.getInstance().getToken());
        addQuery("files",filePath);
    }

    @Override
    public MultipartBody buildFormBody() {
        MediaType mediaType = MediaType.parse("application/octet-stream");
        MultipartBody.Builder body_builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        String path = (String) this.queryMap.get("files");
        File file = new File(path);
        body_builder.addFormDataPart("files", file.getName(), RequestBody.create(mediaType, file));
        return body_builder.build();
    }

}
