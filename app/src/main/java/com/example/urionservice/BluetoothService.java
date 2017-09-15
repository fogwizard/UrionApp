package com.example.urionservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.example.urionbean.BaseMessageBean;
import com.example.urionbean.Data;
import com.example.urionbean.DevicesData;
import com.example.urionbean.Error;
import com.example.urionbean.Head;
import com.example.urionbean.IBean;
import com.example.urionbean.Msg;
import com.example.urionbean.Pressure;
import com.example.urionbean.contentBean;
import com.example.urionbean.dataBean;
import com.example.urionuntil.CodeFormat;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import okhttp3.OkHttpClient;
import java.util.Date;
import java.text.SimpleDateFormat;

public class BluetoothService {

	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothSocket socket;
	private BluetoothDevice device;
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	public static boolean liu = false;
	
	private int aa = 0;
	private Handler mHandler;
	private ConnectedThread mConnectedThread;
	private int mState;
    public static boolean al = false;
	public BluetoothService() {
		mState = Msg.MESSAGE_STATE_NONE;
	}

	public int getmState() {
		return mState;
	}

	public void setmState(int mState) {
		this.mState = mState;
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

	public void run() {
		connected();
	}

	/**
	 * 发送消息到前台
	 * 
	 * @param code
	 * @param bean
	 * 
	 */
	private void send(int code, IBean bean) {
		if (mHandler != null) {
			System.out.println("发送消息到前台");
			Message msg = mHandler.obtainMessage(code);
			System.out.println(mHandler.obtainMessage(code));
			Bundle bundle = new Bundle();
			bundle.putParcelable("bean", bean);
			msg.setData(bundle);
			msg.sendToTarget();
		}
	}

	/**
	 * 连接方法
	 */
	public void connect() {
		if (device != null) {
			try {
				socket = device.createRfcommSocketToServiceRecord(MY_UUID);
				socket.connect();
				mState = Msg.MESSAGE_STATE_CONNECTING;
				System.out.println("aaaaaaaa");
				send(IBean.MESSAGE, new Msg(mState, device.getName()));
				System.out.println("aa1111aaaaaa");
				connected();
				System.out.println("aa1111aaaauuuuuuaa");
			} catch (IOException e) {
				e.printStackTrace();
				connectionFailed();
			}
		}
	}

	/**
	 * 发送数据
	 * 
	 * @param f
	 */
	public void write(byte[] f) {
		if (mmOutStream != null)
			try {
				
				mmOutStream.write(f);
			} catch (IOException e) {
				e.printStackTrace();
		
			}
	}

	/**
	 * 连接成功后启动接收数据的线程
	 */
	public void connected() {
		if (socket != null) {
			mConnectedThread = new ConnectedThread();
			mConnectedThread.start();
		    mState = Msg.MESSAGE_STATE_CONNECTED;
			
			send(IBean.MESSAGE, new Msg(mState, device.getName()));
		}

	}

	/**
	 * 停止
	 */
	public void stop() {
		try {
			if (socket != null) {
				socket.close();
				mState = Msg.MESSAGE_STATE_NONE;
				send(IBean.MESSAGE, new Msg(mState, device.getName()));
			}

			if (mConnectedThread != null) {
				mConnectedThread.interrupt();
				// mConnectedThread.destroy();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void connectionFailed() {
		send(IBean.ERROR, new Error(Error.ERROR_CONNECTION_FAILED));
		
	}

	private void connectionLost() {
   	  send(IBean.ERROR, new Error(Error.ERROR_CONNECTION_LOST));

   	}

	String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str;) {
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
		File file = new File("/proc/user_label");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			if ((tempString = reader.readLine()) != null){
				user =  Integer.parseInt(tempString);
			} else {
				user = 1;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(1 == (user %100)){
			return "UserA";
		}else if (2 == (user%100)) {
			return "UserB";
		} else {
			return "UserC";
		}
    }

	private class ConnectedThread extends Thread {
		public ConnectedThread() {
			try {
				mmInStream = socket.getInputStream();
				mmOutStream = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			byte[] buffer = new byte[16];
			String device_id = getMac();
			while (true) {
				try {
					if (mmInStream.available() > 0) {// 如果流中有数据就进行解析
						Head head = new Head();
						mmInStream.read(buffer);
						int[] f = CodeFormat.bytesToHexStringTwo(buffer, 6);
						head.analysis(f);
						if (head.getType() == Head.TYPE_ERROR) {
							// APP接收到血压仪的错误信息

							Error error = new Error();
							error.analysis(f);
							error.setHead(head);
							// 前台根据错误编码显示相应的提示
							send(IBean.ERROR, error);
	
						}
						if (head.getType() == Head.TYPE_RESULT) {
							// APP接收到血压仪的测量结果
							Data data = new Data();
							data.analysis(f);
							data.setHead(head);
							// 前台根据测试结果来画线性图
							send(IBean.DATA, data);
							OkHttpClient okHttpClient = new OkHttpClient();
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String now = df.format(new Date());
							DevicesData[] devicesDatas = new DevicesData[2];

							devicesDatas[0].setUserName(getCustomUser());
							devicesDatas[0].setDeviceName("血压计");
							devicesDatas[0].setSys_mmHg("120");
							devicesDatas[0].setDia_mmHg("60");
							devicesDatas[0].setPul_min("70");
							devicesDatas[0].setMmol_L("5.2");
							//devdata[0].setDeviceName("血糖计");
							contentBean content = new contentBean(devicesDatas);
							dataBean dBean = new dataBean(1,1001,device_id,content);
							BaseMessageBean baseBean = new BaseMessageBean("Come in later",now,0,dBean);

							String MsgStr = JSON.toJSONString(baseBean);
						}

						if (head.getType() == Head.TYPE_MESSAGE) {
							// APP接收到血压仪开始测量的通知
							Msg msg = new Msg();
							msg.analysis(f);

							msg.setHead(head);
							send(IBean.MESSAGE, msg);
						}
						if (head.getType() == Head.TYPE_PRESSURE) {
							// APP接受到血压仪测量的压力数据
							Pressure pressure = new Pressure();
							pressure.analysis(f);
							pressure.setHead(head);
							// 每接收到一条数据就发送到前台，以改变进度条的显示
							send(IBean.DATA, pressure);
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
					connectionLost();
					interrupt();
					break;
				}
			}

		}
	}
}
