package com.example.urionbean;

/**
 * Created by xiaochi on 2017/9/15.
 */

public class DevicesData {
    String deviceName;
    String userName;
    String sys_mmHg;
    String dia_mmHg;
    String pul_min;
    String mmol_L;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSys_mmHg() {
        return sys_mmHg;
    }

    public void setSys_mmHg(String sys_mmHg) {
        this.sys_mmHg = sys_mmHg;
    }

    public String getDia_mmHg() {
        return dia_mmHg;
    }

    public void setDia_mmHg(String dia_mmHg) {
        this.dia_mmHg = dia_mmHg;
    }

    public String getPul_min() {
        return pul_min;
    }

    public void setPul_min(String pul_min) {
        this.pul_min = pul_min;
    }

    public String getMmol_L() {
        return mmol_L;
    }

    public void setMmol_L(String mmol_L) {
        this.mmol_L = mmol_L;
    }

    public DevicesData(String deviceName, String userName, String sys_mmHg, String dia_mmHg, String pul_min, String mmol_L) {
        this.deviceName = deviceName;
        this.userName = userName;
        this.sys_mmHg = sys_mmHg;
        this.dia_mmHg = dia_mmHg;
        this.pul_min = pul_min;
        this.mmol_L = mmol_L;
    }
    public DevicesData() {
    }
}
