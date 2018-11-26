package com.hopen.darts.bean;

import com.hopen.darts.networks.request.GetResourceRequest;
import com.hopen.darts.networks.response.GetResourceResponse;

import java.io.File;

public class FileVersion {

    /**
     * resourceUrl : app_resource/video_animation.zip
     * resourceName : video_animation
     * resourceVersion : 1
     * isZip : 1
     */

    private String resourceUrl;
    private String resourceName;
    private int resourceVersion;
    private int type;

    public FileVersion(GetResourceResponse.DataBean dataBean){
        resourceUrl = dataBean.getResourceUrl();
        resourceName = dataBean.getResourceName();
        resourceVersion = dataBean.getResourceVersion();
        type = dataBean.getType();
    }

    public FileVersion(){}

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public int getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(int resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
