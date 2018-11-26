package com.hopen.darts.utils.SerialPort.ports;

import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class BaseSerialPort {
    protected static final String TAG = "SerialPortOperator";
    /**
     * 串口地址
     */
    private String path;
    /**
     * 波特率
     */
    private int baudRate;
    /**
     * 串口对象
     */
    private SerialPort serialPort;
    /**
     * 串口输入流
     */
    private InputStream inputStream;
    /**
     * 串口输出流
     */
    private OutputStream outputStream;
    /**
     * 是否已经打开
     */
    private boolean isOpen;

    public BaseSerialPort(String path, int baud_rate) {
        this.path = path;
        this.baudRate = baud_rate;
        this.isOpen = false;
    }

    /**
     * 打开串口
     *
     * @return 是否成功打开串口
     */
    public boolean open() {
        if (isOpen()) return true;
        try {
            //获取串口
            serialPort = new SerialPort(new File(path), baudRate, 0);
            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            isOpen = true;
            Log.e(TAG, "串口打开成功");
            return true;
        } catch (Throwable e) {
            Log.e(TAG, "串口打开失败");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭串口
     */
    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            serialPort.close();
            isOpen = false;
            Log.d(TAG, "串口关闭成功");
        } catch (Throwable e) {
            Log.e(TAG, "串口关闭异常");
            e.printStackTrace();
        }
    }

    /**
     * 从串口读取数据
     *
     * @param b   读取数据的缓冲区。
     * @param off 写入数据的数组在<code>b</code>的起始偏移量
     * @param len 要读取的最大字节数。
     * @return 取缓冲区的字节总数，没有数据返回-1
     * @throws Throwable 流读写异常，空指针，越界等异常
     */
    public int read(byte b[], int off, int len) throws Throwable {
        if (inputStream == null) return -1;
        else return inputStream.read(b, off, len);
    }

    /**
     * 向串口发送数据
     *
     * @param instruction 指令
     */
    public void write(byte[] instruction) {
        Log.d(TAG, "发送数据");
        try {
            if (outputStream != null) {
                outputStream.write(instruction);
                outputStream.flush();
                Log.d(TAG, "串口数据发送成功");
            } else Log.e(TAG, "串口数据发送失败：输出流不存在");
        } catch (Throwable e) {
            Log.e(TAG, "串口数据发送失败：" + e.toString());
        }
    }

    /**
     * @return 串口是否已经成功打开
     */
    public boolean isOpen() {
        return isOpen;
    }
}
