package com.hopen.darts.networks.request;

import android.support.annotation.IntRange;

import com.hopen.darts.networks.commons.netty.BaseNettyRequest;
import com.hopen.darts.networks.commons.netty.N;
import com.hopen.darts.networks.mapper.Step;
import com.hopen.darts.networks.netty.NettyUtil;

public class HitRequest extends BaseNettyRequest {
    private boolean sending;

    public HitRequest(Step step) {
        init(step);
    }

    public HitRequest(@IntRange(from = 0) int position, int i, int j) {
        Step step = new Step(position, i, j);
        init(step);
    }

    public HitRequest(@IntRange(from = 0) int position, Step.Decision decision) {
        Step step = new Step(position, decision);
        init(step);
    }

    private void init(Step step) {
        setType(N.hit);
        setDataValue("step", step);
    }

    public boolean isSending() {
        return sending;
    }

    public void send() {
        if (!sending) {
            sending = true;
            NettyUtil.sendRequest(this);
        }
    }

    public void sendAble() {
        sending = false;
    }
}
