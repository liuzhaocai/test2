package com.hopen.darts.networks.request;

import com.hopen.darts.networks.commons.BaseRequest;
import com.hopen.darts.networks.commons.MethodType;

/**
 * Created by zhangyanxue on 2018/7/20.
 */

public class GetAppVersionRequest extends BaseRequest {

    public GetAppVersionRequest(){
        super(MethodType.GET);
        setMethodName("resource/getAppVersion");
    }

}
