package com.example.urionbean;

/**
 * Created by xiaochi on 2017/9/15.
 */

public class BaseMessageBean  {
    /**
     * 设备类型 0 设备 1 移动终端 3 pc 端 4 服务端
     */
    private String  sign;

    private String  timestamp;

    private int   encrypt;

    private dataBean data;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    public dataBean getData() {
        return data;
    }

    public void setData(dataBean data) {
        this.data = data;
    }

    public BaseMessageBean(String sign, String timestamp, int encrypt, dataBean data) {
        this.sign = sign;
        this.timestamp = timestamp;
        this.encrypt = encrypt;
        this.data = data;
    }
}
