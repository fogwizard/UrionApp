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
    int mmol;
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

     String getCustomUser() {
        long user = 1;
        File file = new File("/proc/uptime");
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

     boolean Report2Server(int type,String sys, String dia, String pul, String mmol) {
        String device_id = getMac();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = df.format(new Date());
        DevicesData devicesDatas = new DevicesData();

        //devicesDatas.setUserName(getCustomUser());
        devicesDatas.setUserName("UserA");

         /* 0: 血压计  1：血糖计*/
         switch (type){
             case 0:
                 devicesDatas.setDeviceName("血压计");
                 break;
             case 1:
                 devicesDatas.setDeviceName("血糖仪");
                 break;
         }
        devicesDatas.setSys_mmHg(sys);
        devicesDatas.setDia_mmHg(dia);
        devicesDatas.setPul_min(pul);
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
                .url("http://doc.newmicrotech.cn:8080/app_web/physical/doc")
                .post(requestBody)
                .build();
        try {
            Response response=okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                Log.i(TAG,response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void run() {
        // TODO
        long  now = new Date().getTime();
        if(now - last_now <2000){
            return;
        }
        last_now = now;
        Report2Server(type,
                Integer.toString(sys),Integer.toString(dia),Integer.toString(pul),
                Integer.toString(mmol)
        );
    }

    public BluetoothReportor(int type,int sys,int dia,int pul) {
        /* 0: 血压计  1：血糖计*/
        this.type = type;
        this.sys  = sys;
        this.dia  = dia;
        this.pul  = pul;
    }
}
