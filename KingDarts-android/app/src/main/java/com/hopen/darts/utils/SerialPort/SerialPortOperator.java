package com.hopen.darts.utils.SerialPort;

import com.hopen.darts.utils.SerialPort.ports.SerialPortS2;
import com.hopen.darts.utils.SerialPort.ports.SerialPortS3;

/**
 * 串口数据交互工具类
 */

public class SerialPortOperator {
    /**
     * 单例
     */
    private static SerialPortOperator instance;
    /**
     * 灯板串口对象
     */
    private SerialPortS2 serialPortS2;
    /**
     * 按键靶盘串口对象
     */
    private SerialPortS3 serialPortS3;

    /**
     * 私有构造方法
     */
    private SerialPortOperator() {
        serialPortS2 = new SerialPortS2();
        serialPortS3 = new SerialPortS3();
    }

    /**
     * 打开本程序使用的所有串口
     *
     * @return 是否全部打开成功
     */
    private boolean openAll() {
        return serialPortS2.open() && serialPortS3.open();
    }

    /**
     * 关闭所有串口
     */
    private void closeAll() {
        serialPortS2.close();
        serialPortS3.close();
    }

    /**
     * 打开串口
     */
    public static void open() {
        if (instance == null) {
            instance = new SerialPortOperator();
            for (int i = 0; i < 3; i++) {
                if (instance.openAll()) break;
                if (i == 2) throw new RuntimeException("串口打开失败");
            }
        }
    }

    /**
     * 关闭串口
     */
    public static void close() {
        if (instance == null) return;
        instance.closeAll();
    }

    /**
     * 获取S2串口对象
     *
     * @return S2串口对象
     */
    public static SerialPortS2 getSerialPortS2() {
        return instance.serialPortS2;
    }
}
