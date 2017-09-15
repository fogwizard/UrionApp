package com.example.urionbean;

/**
 * Created by xiaochi on 2017/9/15.
 */

public class dataBean {
    private long clientType;
    private long msgId;
    private String deviceId;
    private contentBean content;

    public long getClientType() {
        return clientType;
    }

    public void setClientType(long clientType) {
        this.clientType = clientType;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public contentBean getContent() {
        return content;
    }

    public void setContent(contentBean content) {
        this.content = content;
    }

    public dataBean(long clientType, long msgId, String deviceId, contentBean content) {
        this.clientType = clientType;
        this.msgId = msgId;
        this.deviceId = deviceId;
        this.content = content;
    }
}
