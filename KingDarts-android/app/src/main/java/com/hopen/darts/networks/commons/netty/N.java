package com.hopen.darts.networks.commons.netty;

public interface N {
    String active = "active";
    String Login = "login";
    String Ping = "ping";
    String Logout = "logout";
    String Order = "order";
    String OrderPay = "orderpay";
    String OrderStart = "orderstart";
    String OrderResults = "orderresults";
    String StartPush = "startpush";
    String hit = "hit";
    String hitpush = "hitpush";

    enum Result {
        success,
        fail,
        offline,
        ignore
    }

}
