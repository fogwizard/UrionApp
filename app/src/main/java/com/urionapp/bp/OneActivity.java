package com.urionapp.bp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.urionbean.Data;
import com.example.uriondb.DBOpenHelper;
import com.example.urionservice.BluetoothLeService;
import com.urionapp.bp.R;

public class OneActivity extends Activity implements OnClickListener {
	private ImageButton thread, history, save, ignore;
	private Button user;
	private Data data;
	private TextView syse, diae, pule;
	private LinearLayout ll;
	String name, time;
	int sys, dia, pul;
	private DBOpenHelper dbOpenHelper;
	private ImageView blue;
	
	private boolean isBleseviceRegiste;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout11);
		dbOpenHelper = new DBOpenHelper(OneActivity.this);
		
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		
		registerReceiver(mGattUpdateReceiver , intentFilter);
		isBleseviceRegiste = true;
		
		blue = (ImageView) this.findViewById(R.id.blue);
		thread = (ImageButton) this.findViewById(R.id.treads);
		history = (ImageButton) this.findViewById(R.id.history1);
		save = (ImageButton) this.findViewById(R.id.save);
		ignore = (ImageButton) this.findViewById(R.id.ignore);
		syse = (TextView) this.findViewById(R.id.sys);
		diae = (TextView) this.findViewById(R.id.dia);
		pule = (TextView) this.findViewById(R.id.pul);
		user = (Button) this.findViewById(R.id.user);
		ll = (LinearLayout) this.findViewById(R.id.bag);

		thread.setOnClickListener(this);
		history.setOnClickListener(this);
		save.setOnClickListener(this);
		ignore.setOnClickListener(this);

		Bundle bundle = this.getIntent().getExtras();
		name = bundle.getString("name");
		time = bundle.getString("time");
		sys = bundle.getInt("sys");
		dia = bundle.getInt("dia");
		pul = bundle.getInt("pul");
		if (sys >= 180 | dia >= 110) {
			ll.setBackgroundResource(R.drawable.ui022);
		} else if (sys <= 120 & dia <= 80) {
			ll.setBackgroundResource(R.drawable.ui021);
		} else if ((sys <= 130 & dia > 80 & dia <= 85)
				| (sys > 120 & sys <= 130 & dia <= 85)) {
			ll.setBackgroundResource(R.drawable.ui023);
		} else if ((sys <= 140 & dia > 85 & dia <= 90)
				| (sys > 130 & sys <= 140 & dia <= 90)) {
			ll.setBackgroundResource(R.drawable.ui024);
		} else if ((sys <= 160 & dia > 90 & dia <= 100)
				| (sys > 140 & sys <= 160 & dia <= 100)) {
			ll.setBackgroundResource(R.drawable.ui025);
		} else {
			ll.setBackgroundResource(R.drawable.ui026);
		}
		user.setText(name);
		syse.setText(sys + "");
		diae.setText(dia + "");
		pule.setText(pul + "");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.treads:
			Intent serverIntent = new Intent(OneActivity.this,
					TwoActivity.class);
			serverIntent.putExtra("gname", name);
			startActivity(serverIntent);
			finish();
			break;
		case R.id.history1:
			Intent two = new Intent(OneActivity.this, ThreadActivity.class);
			two.putExtra("gname", name);
			startActivity(two);
			finish();
			break;
		case R.id.save:
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			db.execSQL(
					"insert into sdp(name,time,sys,dia,pul) values(?,?,?,?,?)",
					new Object[] { name, time, sys, dia, pul });
			db.close();
			Intent ttt = new Intent(OneActivity.this, ThreadActivity.class);
			ttt.putExtra("gname", name);
			startActivity(ttt);
			finish();
			break;
		case R.id.ignore:
			Intent one = new Intent(OneActivity.this, MainActivity.class);
			one.putExtra("gname", name);
			startActivity(one);
			finish();
			break;
		}
	}
	
	
	
	/*
	 * 监听广播类，用来实施的接受数据
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			final String action = intent.getAction();
			if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){
				blue.setImageResource(R.drawable.bluetooth);
			}else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){
				blue.setImageResource(R.drawable.bluetoothno);
			}
		}
	};

	
	protected void onDestroy() {
		super.onDestroy();
		if(isBleseviceRegiste){
			unregisterReceiver(mGattUpdateReceiver);
		}
	};
	

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;

		}

		return super.dispatchKeyEvent(event);
	}

}
