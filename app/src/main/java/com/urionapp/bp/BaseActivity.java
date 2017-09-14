package com.urionapp.bp;

import com.example.interfaces.ICallback;
import com.example.urionbean.IBean;
import com.example.urionbean.Msg;
import com.example.urionbean.Error;
import com.example.urionrxt.RbxtApp;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends Activity implements ICallback {

	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	public BluetoothAdapter mBluetoothAdapter;
	public RbxtApp rbxt;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Application app = getApplication();
		if (app instanceof RbxtApp)
			rbxt = (RbxtApp) app;
		if (mBluetoothAdapter == null)
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}

	}

	protected void onStart() {
		super.onStart();
		rbxt.setCall(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// rbxt.getService().stop();
	}

	public void onError(Error error) {

	}

	public void onReceive(IBean bean) {
		// TODO Auto-generated method stub

	}

	public void onMessage(Msg message) {
		// TODO Auto-generated method stub

	}

}
