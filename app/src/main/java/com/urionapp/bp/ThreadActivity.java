package com.urionapp.bp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.uriondb.DBOpenHelper;
import com.example.urionservice.BluetoothLeService;
import com.urionapp.bp.R;

public class ThreadActivity extends Activity implements OnClickListener {
	private ImageButton email, home, thread;
	private Button user;
	String ggname;
	private ListView listview;
	private DBOpenHelper dbOpenHelper;
	private StringBuilder sbRecords;
	private SQLiteDatabase db;
	
	private ImageView blue;
	private boolean isBleseviceRegiste;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout3);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		
		registerReceiver(mGattUpdateReceiver , intentFilter);
		isBleseviceRegiste = true;
		
		blue = (ImageView) this.findViewById(R.id.blue);
		
		
		email = (ImageButton) this.findViewById(R.id.mail);
		home = (ImageButton) this.findViewById(R.id.home);
		thread = (ImageButton) this.findViewById(R.id.treads);
		user = (Button) this.findViewById(R.id.user);
		listview = (ListView) this.findViewById(R.id.list_show);
		listview.setDivider(null);
		listview.setFooterDividersEnabled(false);
		dbOpenHelper = new DBOpenHelper(ThreadActivity.this);
		email.setOnClickListener(this);
		thread.setOnClickListener(this);
		home.setOnClickListener(this);
		ggname = getIntent().getExtras().getString("gname");
		user.setText(ggname);
		show();

	}

	public void show() {
		db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("sdp", new String[] { "time,sys,dia,pul" },
				"name=?", new String[] { ggname }, null, null, null);
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

		while (cursor.moveToNext()) {
			String time = cursor.getString(cursor.getColumnIndex("time"));
			int sys = cursor.getInt(cursor.getColumnIndex("sys"));
			int dia = cursor.getInt(cursor.getColumnIndex("dia"));
			int pul = cursor.getInt(cursor.getColumnIndex("pul"));
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("time", time);
			item.put("sys", sys);
			item.put("dia", dia);
			item.put("pul", pul);
			data.add(item);
		}

		SimpleAdapter spa = new SimpleAdapter(this, data, R.layout.list_item,
				new String[] { "time", "sys", "dia", "pul" }, new int[] {
						R.id.time, R.id.sys, R.id.dia, R.id.pul });

		listview.setAdapter(spa);
		cursor.close();
	}

	public void emailto() {
		sbRecords = new StringBuilder("");
		// String result = null;
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("user",
				new String[] { "sex,age,height,weight" }, "name=?",
				new String[] { ggname }, null, null, null);
		while (cursor.moveToNext()) {
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			String age = cursor.getString(cursor.getColumnIndex("age"));
			String height = cursor.getString(cursor.getColumnIndex("height"));
			String weight = cursor.getString(cursor.getColumnIndex("weight"));

			sbRecords.append("Name:" + ggname);
			sbRecords.append(",");
			sbRecords.append("Gender:" + sex);
			sbRecords.append(",");
			sbRecords.append("Age:" + age);
			sbRecords.append(",");
			sbRecords.append("Height:" + height);
			sbRecords.append("cm,");
			sbRecords.append("Weight:" + weight + "kg");
			sbRecords.append("\n");
		}
		cursor.close();
		sbRecords.append("             time");
		sbRecords.append("                       ");
		sbRecords.append("sys");
		sbRecords.append(" ");
		sbRecords.append("dia");
		sbRecords.append(" ");
		sbRecords.append("pul");
		sbRecords.append("\n");
		Cursor c = db.query("sdp", new String[] { "time,sys,dia,pul" },
				"name=?", new String[] { ggname }, null, null, null);

		if (c.moveToFirst()) {

			int TIME = c.getColumnIndex("time");
			int SYS = c.getColumnIndex("sys");
			int DIA = c.getColumnIndex("dia");
			int PUL = c.getColumnIndex("pul");
			do {

				sbRecords.append(c.getString(TIME));
				sbRecords.append("");
				sbRecords.append(c.getString(SYS));
				sbRecords.append("  ");
				sbRecords.append(c.getString(DIA));
				sbRecords.append("  ");
				sbRecords.append(c.getString(PUL));
				sbRecords.append("\n");

			} while (c.moveToNext());
		}
		c.close();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;

		}

		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.treads:
			Intent serverIntent = new Intent(ThreadActivity.this,
					TwoActivity.class);
			serverIntent.putExtra("gname", ggname);
			startActivity(serverIntent);
			break;
		case R.id.home:
			Intent mainone = new Intent(ThreadActivity.this, MainActivity.class);
			mainone.putExtra("gname", ggname);
			startActivity(mainone);
			break;
		case R.id.mail:
			emailto();
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822"); // 真机上使用这行
			// i.setType("plain/text");
			i.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
			i.putExtra(Intent.EXTRA_SUBJECT, " Test History");
			i.putExtra(Intent.EXTRA_TEXT, sbRecords.toString());
			startActivity(Intent.createChooser(i, "Select email application."));
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

}
