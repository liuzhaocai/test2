package com.hopen.darts.networks.request;

import com.hopen.darts.networks.commons.netty.BaseNettyRequest;
import com.hopen.darts.networks.commons.netty.N;

import java.util.Map;

/**
 * Created by zhangyanxue on 2018/7/30.
 */

public class CustomHitRequest extends BaseNettyRequest {

    public CustomHitRequest(Map<String, Object> playerInfo) {
        setType(N.hit);
        setDataValue("playerInfo", playerInfo);
    }

}
