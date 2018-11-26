package com.hopen.darts.networks.request;

import com.alibaba.fastjson.JSON;
import com.hopen.darts.game.GameUtil;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.BaseRequest;
import com.hopen.darts.utils.PhoneUtil;
import com.orhanobut.logger.Logger;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class OrderResultsRequest extends BaseRequest {

    public OrderResultsRequest(String order_no) {
        setMethodName("/equ/gameres");
        addHeader("x-access-token", KDManager.getInstance().getToken());
        addHeader("equno", PhoneUtil.readSNCode());
        addQuery("order_no", order_no);
        if (GameUtil.getPlayingGame() != null) {
            addQuery("results", GameUtil.getPlayingGame().getOrderGroup());
            if (GameUtil.getPlayingGame().isOnline()) {
                addQuery("is_win", GameUtil.getPlayingGame().isLocalWin() ? "Y" : "N");
            }
            if (GameUtil.getPlayingGame().getAttach() != null) {
                addQuery("attach", GameUtil.getPlayingGame().getAttach());
            }
        }
    }

    @Override
    public RequestBody buildFormBody() {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, JSON.toJSONString(this.queryMap));
        Logger.e(JSON.toJSONString(queryMap));
        return requestBody;
    }
}
