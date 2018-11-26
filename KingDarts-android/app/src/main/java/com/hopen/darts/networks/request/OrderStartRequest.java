package com.hopen.darts.networks.request;

import com.hopen.darts.networks.commons.netty.BaseNettyRequest;
import com.hopen.darts.networks.commons.netty.N;

public class OrderStartRequest extends BaseNettyRequest {

    public OrderStartRequest(String order_no){
        setDataValue("order_no",order_no);
        setType(N.OrderStart);
    }

}
