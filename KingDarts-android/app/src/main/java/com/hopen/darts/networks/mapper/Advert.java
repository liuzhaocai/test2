package com.hopen.darts.networks.mapper;

public class Advert {
    private float qr_left;
    private float qr_top;
    private String file_url;
    private String qrcode_url;

    public float getQr_left() {
        return qr_left;
    }

    public void setQr_left(float qr_left) {
        this.qr_left = qr_left;
    }

    public float getQr_top() {
        return qr_top;
    }

    public void setQr_top(float qr_top) {
        this.qr_top = qr_top;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getQrcode_url() {
        return qrcode_url;
    }

    public void setQrcode_url(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }
}
