package com.hopen.darts.networks.request;

import com.hopen.darts.networks.commons.BaseRequest;
import com.hopen.darts.networks.commons.MethodType;

public class GetResourceRequest extends BaseRequest {

    public GetResourceRequest(){
        super(MethodType.GET);
        setMethodName("resource/getResource");
    }

}
