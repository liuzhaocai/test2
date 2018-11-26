package com.hopen.darts.networks.interfaces;

import com.hopen.darts.networks.commons.BaseResponse;

/**
 * Created by SEELE on 2017/6/7.
 * 请求失败回调
 */

public interface OnErrorCallback {
    void onError(BaseResponse baseResponse);
}
