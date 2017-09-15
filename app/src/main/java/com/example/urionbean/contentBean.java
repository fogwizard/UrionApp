package com.example.urionbean;

/**
 * Created by xiaochi on 2017/9/15.
 */

public class contentBean {
    private DevicesData[] content = new DevicesData[2];

    public DevicesData[] getContent() {
        return content;
    }

    public void setContent(DevicesData[] content) {
        this.content = content;
    }

    public contentBean(DevicesData[] content) {
        this.content = content;
    }
}
