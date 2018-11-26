package com.hopen.darts.networks.request;

import com.hopen.darts.networks.commons.netty.BaseNettyRequest;
import com.hopen.darts.networks.commons.netty.N;

public class LoginRequest extends BaseNettyRequest {

    public LoginRequest(String equno){
        setType(N.Login);
        setDataValue("equno",equno);
    }

}

