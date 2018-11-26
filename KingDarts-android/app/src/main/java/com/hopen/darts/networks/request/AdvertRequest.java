package com.hopen.darts.networks.request;

import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.BaseRequest;
import com.hopen.darts.utils.PhoneUtil;

public class AdvertRequest extends BaseRequest {

    public AdvertRequest() {
        get();
        setMethodName("/medal/advert");
        addHeader("x-access-token", KDManager.getInstance().getToken());
        addHeader("equno", PhoneUtil.readSNCode());
    }
}
