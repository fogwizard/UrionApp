package com.urionapp.bp;

import java.util.ArrayList;

import com.example.urionclass.L;
import com.urionapp.bp.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DeviceListActivity extends Activity {

	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	private BluetoothAdapter mBtAdapter;
//	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	
	private ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);

		setResult(Activity.RESULT_CANCELED);

		Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doDiscovery();
				v.setVisibility(View.GONE);
			}
		});

//		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
//				R.layout.device_name);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);

//		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
//		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
//		pairedListView.setOnItemClickListener(mDeviceClickListener);

		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(nmDeviceClickListener);

//		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//		this.registerReceiver(mReceiver, filter);

//		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//		this.registerReceiver(mReceiver, filter);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

//		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
//
//		if (pairedDevices.size() > 0) {
//			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
//			for (BluetoothDevice device : pairedDevices) {
//				mPairedDevicesArrayAdapter.add(device.getName() + "\n"
//						+ device.getAddress());
//			}
//		} else {
//			String noDevices = getResources().getText(R.string.none_paired)
//					.toString();
//			mPairedDevicesArrayAdapter.add(noDevices);
//		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if(mBtAdapter != null){
			mBtAdapter.stopLeScan(mLeScanCallback);
		}
		

		//this.unregisterReceiver(mReceiver);
	}

	private void doDiscovery() {

		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);

		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

//		if (mBtAdapter.isDiscovering()) {
//			mBtAdapter.cancelDiscovery();
//		}

		mBtAdapter.startLeScan(mLeScanCallback);
	}

	
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!devices.contains(device)){
						devices.add(device);
						mNewDevicesArrayAdapter.add(device.getName() + "\n"
								+ device.getAddress());
					}
					
					
//					if (!mLeDevices.contains(device)) {
//						mLeDevices.add(device);
//						L.d("device-->" + device.getName());
//						if (getDeviceName().equals(device.getName())) {
//							mDevice = device;
//							startService();
//						}
//					}
				}
			});
		}
	};
	
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
//			mBtAdapter.cancelDiscovery();

			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17);

			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};
	private OnItemClickListener nmDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			//mBtAdapter.cancelDiscovery();

			
			if (mBtAdapter != null) {
				mBtAdapter.stopLeScan(mLeScanCallback);;
			}
			
			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17);

			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

			setResult(300, intent);
			finish();
		}
	};


	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (!devices.contains(device)){
					devices.add(device);
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
			} 
//			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
//					.equals(action)) {
//				setProgressBarIndeterminateVisibility(false);
//				setTitle(R.string.select_device);
//				if (mNewDevicesArrayAdapter.getCount() == 0) {
//					String noDevices = getResources().getText(
//							R.string.none_found).toString();
//					mNewDevicesArrayAdapter.add(noDevices);
//				}
//			}
		}
	};
}
