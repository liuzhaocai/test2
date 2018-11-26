package com.hopen.darts.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.hopen.darts.base.BaseApplication;
import com.hopen.darts.base.C;
import com.hopen.darts.game.GameUtil;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.BaseResponse;
import com.hopen.darts.networks.commons.NetWork;
import com.hopen.darts.networks.interfaces.OnErrorCallback;
import com.hopen.darts.networks.interfaces.OnSuccessCallback;
import com.hopen.darts.networks.mapper.Advert;
import com.hopen.darts.networks.netty.NettyUtil;
import com.hopen.darts.networks.request.AdvertRequest;
import com.hopen.darts.networks.request.ContractCancelRequest;
import com.hopen.darts.networks.response.AdvertResponse;
import com.hopen.darts.ui.AdvertActivity;

public class DaemonUtil {
    private static DaemonUtil instance;//单例
    private long lastOperationTime;//最近一次操作的时间
    private Thread daemonThread;//轮循守护线程
    private Handler daemonHandler;//轮训守护线程消息接收Handler
    private final int networkInterval = 30 * 1000;//网络请求间隔
    private boolean isAdvertNetworking = false;//是否正在进行广告相关的网路请求
    private long lastAdvertNetworkTime;//最近一次请求广告接口的时间，避免没网或者接口返回无效数据时，终端一直在请求
    private final int noOperationToShowAdvertFixedTime = 60 * 1000;//没有操作而显示广告页面的时间
    private Advert advert;//广告数据
    private boolean isContractNetworking = false;//是否正在进行包机相关的网路请求
    private long lastContractNetworkTime;//最近一次请求取消包机接口的时间，避免没网或者接口返回无效数据时，终端一直在请求
    private final int noOperationToCancelContractFixedTime = 30 * 60 * 1000;//没有操作而取消包机状态的时间

    /**
     * 初始化
     */
    public synchronized static void init() {
        if (instance == null) {
            instance = new DaemonUtil();
            instance.startDaemonThread();
        }
    }

    /**
     * 重设最近一次的操作时间
     */
    public static void resetLastOperationTime() {
        if (instance == null) {
            init();
        } else if (instance.daemonThread == null || !instance.daemonThread.isAlive()) {
            instance.startDaemonThread();
        } else {
            instance.lastOperationTime = System.currentTimeMillis();
        }
    }

    /**
     * 获取广告数据
     *
     * @return 广告数据
     */
    public static Advert getAdvert() {
        if (instance == null) {
            init();
            return null;
        } else {
            return instance.advert;
        }
    }

    /**
     * 包机相关的处理方法
     *
     * @param msg 消息对象
     * @return True if no further handling is desired（官注）
     */
    private boolean handleContract(Message msg) {
        if (C.app.debug) return false;//debug模式不自动取消包机
        if (isContractNetworking) return false;
        long now = System.currentTimeMillis();
        if (now - lastOperationTime >= noOperationToCancelContractFixedTime && !GameUtil.isEnter()
                && KDManager.getInstance().isContractOrMaybeContract() && NettyUtil.isNettyLogin()
                && now - lastContractNetworkTime >= networkInterval) {
            //超过了固定的时间没有操作，并且不处在进入游戏状态，并且处在包机状态或者可能处在包机状态，并且处在已登录状态
            //并且超过固定请求间隔时间没有请求取消包机接口（避免网络出现问题的时候一直在请求接口）
            //先请求取消包机接口
            isContractNetworking = true;
            lastContractNetworkTime = now;
            NetWork.request(BaseApplication.getBaseActivity(), new ContractCancelRequest(), new OnSuccessCallback() {
                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    KDManager.getInstance().setContractFlag("1");
                    isContractNetworking = false;
                }
            }, new OnErrorCallback() {
                @Override
                public void onError(BaseResponse baseResponse) {
                    isContractNetworking = false;
                }
            }, false);
        }
        return false;
    }

    /**
     * 广告相关的处理方法
     *
     * @param msg 消息对象
     * @return True if no further handling is desired（官注）
     */
    private boolean handleAdvert(Message msg) {
        if (isAdvertNetworking) return false;
        long now = System.currentTimeMillis();
        if (now - lastOperationTime >= noOperationToShowAdvertFixedTime && BaseApplication.getBaseActivity() != null
                && BaseApplication.getBaseActivity().isFixedTimeNoOperationToShowAdvert
                && now - lastAdvertNetworkTime >= networkInterval) {
            //超过了固定的时间没有操作，并且存在当前页面，并且当前页面已设定需要在一定时间无操作后显示广告页面
            //并且超过固定请求间隔时间没有请求广告接口（避免网络出现问题的时候一直在请求接口）
            //先请求广告数据接口
            isAdvertNetworking = true;
            lastAdvertNetworkTime = now;
            NetWork.request(BaseApplication.getBaseActivity(), new AdvertRequest(), new OnSuccessCallback() {
                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    //网络请求是个耗时操作，请求成功后再进行一次上方的判断（除去请求间隔判断），是的话才显示广告页面
                    long now = System.currentTimeMillis();
                    if (now - lastOperationTime >= noOperationToShowAdvertFixedTime && BaseApplication.getBaseActivity() != null
                            && BaseApplication.getBaseActivity().isFixedTimeNoOperationToShowAdvert) {
                        advert = ((AdvertResponse) baseResponse).getData();
                        JumpIntent.jump(BaseApplication.getBaseActivity(), AdvertActivity.class);
                    }
                    isAdvertNetworking = false;
                }
            }, new OnErrorCallback() {
                @Override
                public void onError(BaseResponse baseResponse) {
                    isAdvertNetworking = false;
                }
            }, false);
        }
        return false;
    }

    /**
     * 获取广告显示控制线程消息接收Handler
     *
     * @return 广告显示控制线程消息接收Handler
     */
    private Handler getDaemonHandler() {
        if (daemonHandler == null) {
            daemonHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == C.handler.daemon) {
                        boolean handle_advert = handleAdvert(msg);
                        boolean handle_contract = handleContract(msg);
                        return handle_advert && handle_contract;
                    }
                    return false;
                }
            });
        }
        return daemonHandler;
    }

    /**
     * 发送消息到主线程
     */
    private void sendMessage() {
        boolean success = getDaemonHandler().sendEmptyMessage(C.handler.daemon);
        if (!success) {
            daemonHandler = null;
            sendMessage();
        }
    }

    /**
     * 开始运行广告显示控制线程
     */
    private void startDaemonThread() {
        lastOperationTime = System.currentTimeMillis();
        if (daemonThread == null || !daemonThread.isAlive()) {
            daemonThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            sendMessage();
                            Thread.sleep(2000);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
            daemonThread.setName("darts_daemon");
            daemonThread.setDaemon(true);//守护线程
            daemonThread.start();
        }
    }
}
