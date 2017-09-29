package com.urionapp.bp;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.urionbean.BaseMessageBean;
import com.example.urionbean.DevicesData;
import com.example.urionbean.contentBean;
import com.example.urionbean.dataBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xiaochi on 2017/9/15.
 */

public class BluetoothReportor extends Thread {
    int sys;
    int dia;
    int pul;
    int SpO2;
    float mmol;
    int type;
    static long last_now;
    public static final String TAG = "BluetoothReportor";
    public static final MediaType REQ_JSON=MediaType.parse("application/json; charset=utf-8");
    static String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

    static String getCustomUser() {
        long user = 1;
        File file = new File("/proc/user_label");
        if(!file.exists()) {
            Log.d(TAG, "/proc/user_label doesn't exist,fall back to UserA");
            return "UserA";
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            if ((tempString = reader.readLine()) != null) {
                user = Integer.parseInt(tempString);
            } else {
                user = 1;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (1 == (user % 100)) {
            return "UserA";
        } else if (2 == (user % 100)) {
            return "UserB";
        } else {
            return "UserC";
        }
    }

    boolean Report2Server(int type,String sys, String dia, String pul, String SpO2,String mmol) {
        String device_id = getMac();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = df.format(new Date());
        DevicesData devicesDatas = new DevicesData();
        devicesDatas.setUserName(getCustomUser());
        /* 0: 血压计  1：血糖计*/
        switch (type) {
        case 0://("血压计");
            devicesDatas.setDeviceName("BP");
            break;
        case 1://("血糖计");
            devicesDatas.setDeviceName("GLU");
            break;
        case 2://P 心率计
            devicesDatas.setDeviceName("P");
            break;
        case 3://SO2 血氧计
            devicesDatas.setDeviceName("SO2");
            break;
        }
        devicesDatas.setSys_mmHg(sys);
        devicesDatas.setDia_mmHg(dia);
        devicesDatas.setPul_min(pul);
        devicesDatas.setSpo2_per(SpO2);
        devicesDatas.setMmol_L(mmol);
        //devdata[0].setDeviceName("血糖计");
        contentBean content = new contentBean();
        content.setContent1(devicesDatas);
        dataBean dBean = new dataBean(1, 1001, device_id, content);
        BaseMessageBean baseBean = new BaseMessageBean("Come in later", now, 0, dBean);
        String MsgStr = JSON.toJSONString(baseBean);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(REQ_JSON, MsgStr);
        //创建一个请求对象
        Request request = new Request.Builder()
        .url("http://doc.newmicrotech.cn:8080/physical/doc")
        .post(requestBody)
        .build();
        try {
            Response response=okHttpClient.newCall(request).execute();
            if(response.isSuccessful()) {
                String res = response.body().string();
                respondJsonBean resBean = JSON.parseObject(res,respondJsonBean.class);
                String msgLog = String.format("Do post success,req=%s\nres=%s\n", MsgStr,res);
                Log.i(TAG,msgLog);
            } else {
                Log.i(TAG,"Do post err, req="+ MsgStr);
            }
        } catch (IOException e) {
            Log.i(TAG,"Do Post Exception, req="+ MsgStr);
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void run() {
        // TODO
        long  now = new Date().getTime();
        if(now - last_now <2000) {
            last_now = now;
            return;
        }
        last_now = now;
        Report2Server(type,
                      Integer.toString(sys),Integer.toString(dia),Integer.toString(pul),
                      Integer.toString(SpO2),Float.toString(mmol)
                     );
    }

    public BluetoothReportor(int type,int sys,int dia,int pul,int SpO2,float mmol) {
        /* 0: 血压计  1：血糖计 2:心率计 3:血氧计*/
        this.type = type;
        this.sys  = sys;
        this.dia  = dia;
        this.pul  = pul;
        this.SpO2  = SpO2;
        this.mmol = mmol;
    }
    public static void main(String[] args) {
        System.out.print(getCustomUser());
    }

}
