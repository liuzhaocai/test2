package com.hopen.darts.utils.SerialPort.ports;

import com.hopen.darts.utils.SerialPort.InstructionUtil;

public class SerialPortS2 extends BaseSerialPort {
    public SerialPortS2() {
        super("/dev/ttyS2", 9600);
    }

    /**
     * 发送指令
     *
     * @param instruction 指令的值
     */
    public void write(long instruction) {
        write(InstructionUtil.long2Bytes(instruction, 6));
    }
}
