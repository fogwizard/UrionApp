package com.example.urionrxt;

import com.example.interfaces.ICallback;
import com.example.urionbean.IBean;
import com.example.urionbean.Msg;
import com.example.urionbean.Error;
import com.example.urionservice.BluetoothService;
import android.app.Application;
import android.os.Handler;
import android.os.Message;

public class RbxtApp extends Application {

	public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;

	private BluetoothService service;

	private ICallback call;

	private Handler mHandler;

	public void onCreate() {
		super.onCreate();
		createHandler();
		setupChat();
		service.setHandler(mHandler);
	}

	public void setupChat() {
		if (service == null)
			service = new BluetoothService();
	}

	public ICallback getCall() {
		return call;
	}

	public void setCall(ICallback call) {
		this.call = call;
	}

	public BluetoothService getService() {
		return service;
	}

	public void setService(BluetoothService service) {
		this.service = service;
	}

	private void createHandler() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				IBean bean = msg.getData().getParcelable("bean");
				if (call != null)
					switch (msg.what) {
					case IBean.ERROR:
						getCall().onError((Error) bean);
						break;
					case IBean.MESSAGE:
						getCall().onMessage((Msg) bean);
						break;
					case IBean.DATA:
						getCall().onReceive(bean);
						break;
					}
			}
		};
	}
}
