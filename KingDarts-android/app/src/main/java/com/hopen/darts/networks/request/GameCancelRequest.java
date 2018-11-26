package com.hopen.darts.networks.request;

import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.BaseRequest;
import com.hopen.darts.networks.commons.MethodType;
import com.hopen.darts.utils.PhoneUtil;

/**
 * Created by zhangyanxue on 2018/7/20.
 */

public class GameCancelRequest extends BaseRequest {

    public GameCancelRequest(String order_no){
        super(MethodType.POST);
        setMethodName("equ/game/cancel");
        addHeader("x-access-token", KDManager.getInstance().getToken());
        addHeader("equno", PhoneUtil.readSNCode());
        addQuery("orderNo", order_no);
    }

}
