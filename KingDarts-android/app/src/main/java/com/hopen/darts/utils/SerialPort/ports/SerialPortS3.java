package com.hopen.darts.utils.SerialPort.ports;

import android.util.Log;

import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.BaseApplication;
import com.hopen.darts.utils.SerialPort.InstructionUtil;

import java.util.LinkedList;

public class SerialPortS3 extends BaseSerialPort {
    /**
     * 串口指令接收监听回调
     */
    private LinkedList<OnReceiveInstructionListener> onReceiveInstructionListenerList;
    /**
     * 串口数据接收线程
     */
    private Thread readInstructionThread;

    public SerialPortS3() {
        super("/dev/ttyS3", 9600);
        this.onReceiveInstructionListenerList = new LinkedList<>();
        addOnInstructionReceiveListener(new OnReceiveInstructionListener() {
            @Override
            public void onReceive(final long instruction) {
                if (BaseApplication.getBaseActivity() != null) {
                    BaseApplication.getBaseActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BaseActivity activity = BaseApplication.getBaseActivity();
                            if (!activity.isVisible()) return;
                            activity.onKeyOperate(instruction);
                        }
                    });
                }
            }
        });
    }

    /**
     * 打开串口
     *
     * @return serialPort串口对象
     */
    @Override
    public boolean open() {
        if (isOpen()) return true;
        boolean success = super.open();
        if (success) startReadInstruction();
        return success;
    }

    /**
     * 单开一线程，来读数据
     */
    private void startReadInstruction() {
        if (readInstructionThread == null) {
            readInstructionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "进入串口指令读取线程");
                    long last_read_time = 0;
                    while (true) {
                        try {
                            //每8个字节为1个指令
                            byte[] buffer = new byte[8];
                            int size = 0; //读取数据的大小
                            while (size < 8) {//读满8字节，算一次指令
                                size += read(buffer, size, buffer.length - size);
                            }
                            long now_read_time = System.currentTimeMillis();
                            long instruction = InstructionUtil.bytes2Long(buffer);
                            if (now_read_time - last_read_time < 160) {
                                Log.d(TAG, "run: 忽略了指令(间隔过短)：0x" + Long.toHexString(instruction));
                            } else {
                                callListeners(instruction);
                                last_read_time = now_read_time;
                                Log.d(TAG, "run: 接收到了指令：0x" + Long.toHexString(instruction));
                            }
                        } catch (Throwable e) {
                            Log.e(TAG, "串口指令读取异常");
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            });
            readInstructionThread.setDaemon(true);//守护线程
            readInstructionThread.start();
        }
    }

    /**
     * 回调已注册的监听
     *
     * @param instruction 接到的指令
     */
    protected void callListeners(long instruction) {
        if (onReceiveInstructionListenerList != null) {
            for (OnReceiveInstructionListener item : onReceiveInstructionListenerList) {
                try {
                    item.onReceive(instruction);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加指令接收监听回调
     *
     * @param listener 指令接收监听回调
     */
    public void addOnInstructionReceiveListener(OnReceiveInstructionListener listener) {
        onReceiveInstructionListenerList.add(listener);
    }

    /**
     * 移除指令接收监听回调
     *
     * @param listener 指令接收监听回调
     */
    public void removeOnInstructionReceiveListener(OnReceiveInstructionListener listener) {
        onReceiveInstructionListenerList.remove(listener);
    }

    /**
     * 指令接收回调监听接口
     */
    public interface OnReceiveInstructionListener {
        /**
         * 当收到指令时回调
         *
         * @param instruction 收到的指令
         */
        public void onReceive(long instruction);
    }
}
