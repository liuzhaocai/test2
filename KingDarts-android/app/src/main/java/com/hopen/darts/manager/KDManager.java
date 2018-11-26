package com.hopen.darts.manager;

import android.text.TextUtils;

import com.hopen.darts.networks.response.CustomHitResponse;
import com.hopen.darts.networks.response.OrderResponse;
import com.hopen.darts.networks.response.PaySuccessResponse;

public class KDManager {

    private static KDManager instance = null;

    public static KDManager getInstance() {
        if (instance == null) {
            instance = new KDManager();
        }
        return instance;
    }

    private boolean NetFlag;
    private CustomHitResponse.DataBean.PlayerInfoBean otherPlayer;
    private OrderResponse.DataBean orderDetail;
    private PaySuccessResponse.DataBean payerDetail;
    private String contractFlag;
    private String token;

    public boolean isNetFlag() {
        return NetFlag;
    }

    public void setNetFlag(boolean netFlag) {
        NetFlag = netFlag;
    }

    public CustomHitResponse.DataBean.PlayerInfoBean getOtherPlayer() {
        return otherPlayer;
    }

    public void setOtherPlayer(CustomHitResponse.DataBean.PlayerInfoBean otherPlayer) {
        this.otherPlayer = otherPlayer;
    }

    public OrderResponse.DataBean getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(OrderResponse.DataBean orderDetail) {
        this.orderDetail = orderDetail;
    }

    public PaySuccessResponse.DataBean getPayerDetail() {
        return payerDetail;
    }

    public void setPayerDetail(PaySuccessResponse.DataBean payerDetail) {
        this.payerDetail = payerDetail;
        if (this.payerDetail != null) {
            contractFlag = payerDetail.getOrder_type();
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    /**
     * @return 返回true代表处在或者可能处在包机状态，返回false代表一定不处在包机状态
     */
    public boolean isContractOrMaybeContract() {
        return TextUtils.isEmpty(contractFlag) || TextUtils.equals(contractFlag, "2");
    }

    /**
     * 设置包机状态
     *
     * @param flag 包机状态标识，同接口返回规则一致
     */
    public void setContractFlag(String flag) {
        this.contractFlag = flag;
        if (this.payerDetail != null) {
            payerDetail.setOrder_type(flag);
        }
    }
}
