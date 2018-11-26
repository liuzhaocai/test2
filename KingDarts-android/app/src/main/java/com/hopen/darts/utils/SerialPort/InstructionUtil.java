package com.hopen.darts.utils.SerialPort;

/**
 * 指令转换工具类
 */

public class InstructionUtil {
    public static void main(String[] args) {
        long instruction = 0x10ffffffffffffffL;
        System.out.println(Long.toHexString(instruction));
    }

    public static long bytes2Long(byte[] bytes) {
        if (bytes.length != 8) return 0L;
        long instruction = 0L;
        for (int i = 0; i < 8; i++) {
            instruction <<= 8;
            instruction |= (bytes[i] & 0xff);
        }
        return instruction;
    }

    public static byte[] long2Bytes(long instruction, int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; i++) {
            int off = (bytes.length - 1 - i) * 8;//偏移量
            bytes[i] = (byte) ((instruction >>> off) & 0xff);
        }
        return bytes;
    }
}
