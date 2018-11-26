package com.hopen.darts.networks.request;

import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.BaseRequest;
import com.hopen.darts.utils.PhoneUtil;

public class ContractCancelRequest extends BaseRequest {

    public ContractCancelRequest() {
        setMethodName("/equ/unbooked");
        addHeader("x-access-token", KDManager.getInstance().getToken());
        addHeader("equno", PhoneUtil.readSNCode());
    }
}
