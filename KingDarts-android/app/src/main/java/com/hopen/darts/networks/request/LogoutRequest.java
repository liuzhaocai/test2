package com.hopen.darts.networks.request;

import com.hopen.darts.networks.commons.netty.BaseNettyRequest;
import com.hopen.darts.networks.commons.netty.N;

public class LogoutRequest extends BaseNettyRequest {

    public LogoutRequest(){
        setType(N.Logout);
    }

}
