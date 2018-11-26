package com.hopen.darts.networks.request;

import com.hopen.darts.networks.commons.netty.BaseNettyRequest;
import com.hopen.darts.networks.commons.netty.N;
import com.hopen.darts.utils.PhoneUtil;

public class OrderRequest extends BaseNettyRequest {

    public OrderRequest(String game_code,String game_mode){
        setType(N.Order);
        setDataValue("game_code", game_code);
        setDataValue("game_mode",game_mode);
    }

}
