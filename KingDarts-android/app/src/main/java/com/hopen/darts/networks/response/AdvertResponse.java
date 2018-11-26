package com.hopen.darts.networks.response;

import com.hopen.darts.networks.commons.BaseResponse;
import com.hopen.darts.networks.mapper.Advert;

public class AdvertResponse extends BaseResponse {
    private Advert data;

    @Override
    public Advert getData() {
        return data;
    }

    public void setData(Advert data) {
        this.data = data;
    }
}
