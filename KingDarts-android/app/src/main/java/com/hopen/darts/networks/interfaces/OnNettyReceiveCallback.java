package com.hopen.darts.networks.interfaces;

/**
 * 消息监听器
 * Created by zhangyanxue on 2018/5/29.
 */

public interface OnNettyReceiveCallback {

    void receiveMsg(String msg);

}
