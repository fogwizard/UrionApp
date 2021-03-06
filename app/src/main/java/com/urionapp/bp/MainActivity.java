package com.urionapp.bp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urionbean.Msg;
import com.example.urionclass.L;
import com.example.urionclass.MySpinnerButton;
import com.example.urionclass.SampleGattAttributes;

//UrionApp_2015_6_7_1.apk
public class MainActivity extends BleFragmentActivity implements
    OnClickListener, PackageParser.OnDataChangeListener {

    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    /* 0: 血压计  1：血糖计 2:心率计 3:血氧计*/
    public static final int TYPE_BP   = 0;
    public static final int TYPE_GLU  = 1;
    public static final int TYPE_P    = 2;
    public static final int TYPE_SO2  = 3;

    private BluetoothGattCharacteristic chReceive;
    private BluetoothGattCharacteristic chChangeBtName;

    private DataParser mDataParser;
    private PackageParser mPackageParser;
    private String strTargetBluetoothName = "BerryMed";

    private ImageButton start, user, thread, history, edit;
    private ImageView bluetooth;
    private TextView state,mDisplay;
    private MySpinnerButton mSpinnerBtn;
    private static long last_report;
    private static long last_start_spo2;
    private List<String> list = new ArrayList<String>();
    int i = 0, lu = 0;
    String str;
    private static final String TAG = "MainActivity";
    String nametwo;
    public static int fan = 0, ji = 0;
    // private String bleAddress;

    protected boolean isClickOn;
    private boolean backStop;

    private boolean RecievedDataFix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//		if (!mBluetoothAdapter.isEnabled()) {
//			// Intent enableIntent = new Intent(
//			// BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			// startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//			bleState = ble_off;
//			showBleDialog();
//		} else {
//			bleState = ble_on;
//		}
        Log.e("this", "开始了");
        initViews();
    }

    private void initViews() {
        start = (ImageButton) this.findViewById(R.id.start);
        thread = (ImageButton) this.findViewById(R.id.treads);
        history = (ImageButton) this.findViewById(R.id.history1);
        edit = (ImageButton) this.findViewById(R.id.bianji);
        bluetooth = (ImageView) this.findViewById(R.id.blue);
        bluetooth.setImageResource(R.drawable.bluetoothno);
        this.mSpinnerBtn = (MySpinnerButton) this.findViewById(R.id.spinner_btn);
        mDisplay = (TextView) this.findViewById(R.id.warning);
        state = (TextView) this.findViewById(R.id.war);
        state.setTextColor(Color.TRANSPARENT);
        // adapter = new ArrayAdapter<String>(this,
        // android.R.layout.simple_spinner_item, list);
        start.setOnClickListener(this);
        thread.setOnClickListener(this);
        edit.setOnClickListener(this);
        history.setOnClickListener(this);
        last_report = new Date().getTime();
        new UpdateCustomUser().start();
        mDataParser = new DataParser(DataParser.Protocol.BCI, new DataParser.onPackageReceivedListener() {
            @Override
            public void onPackageReceived(int[] dat) {
                if(mPackageParser == null) {
                    mPackageParser = new PackageParser(MainActivity.this);
                }

                mPackageParser.parse(dat);
            }
        });
        mDataParser.start();

        list.add("User");
        if (fan == 0) {
            mSpinnerBtn.setText("UserA");
        } else {
            mSpinnerBtn.setText("UserA");
            state.setText("aaaa");
        }
        i++;
        fan++;
    };

    class UpdateCustomUser  extends  Thread {
        public void run() {
            String LastUser = BluetoothReportor.getCustomUser();
            while(true) {
                String NewUser = BluetoothReportor.getCustomUser();
                if(!NewUser.equals(LastUser)){
                    L.d("new="+NewUser+",last="+LastUser);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSpinnerBtn.setText(BluetoothReportor.getCustomUser());
                        }
                    }, 50);
                    LastUser = NewUser;
                }

                try {
                    Thread.sleep(380);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.start:
            if (isFastDoubleClick()) {
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                showBleDialog();
            } else {
                L.d("bleSTATE_--->" + bleState);
                if (state.getText().toString().equals("The connected")) {
                    bluetooth.setImageResource(R.drawable.bluetooth);
                    if (isRecivced) {
                        toOneoneActivity();
                        isRecivced = false;
                    } else {
                        isClickOn = true;
                        toShowDataPage();
                    }
                } else {
                    if (bleState == ble_scaning) {
                        Toast.makeText(
                            this,
                            "bluetooth is scaning, please make sure your device is turning on......",
                            Toast.LENGTH_SHORT).show();
                    } else if (bleState == ble_connecting) {
                        Toast.makeText(
                            this,
                            "bluetooth is connecting, please wait......",
                            Toast.LENGTH_SHORT).show();
                    } else if (bleState == ble_disConnected) {
                        startScan();
                        Toast.makeText(this, "bluetooth is disconected, will be scaning again",
                                       Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }


    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    private void doShutdown() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                byte[] send = {(-3), (-3), -2, 6, 13, 10};
                gattCharacteristicWrite.setValue(send);
                mBluetoothLeService.getmBluetoothGatt().writeCharacteristic(
                    gattCharacteristicWrite);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        doBluetoothDisconnect(1000);
                    }
                }, 1000);
            }
        }, 2000);
    }

    private void toShowDataPage() {
        // [0xFD,0xFD,0xFA,0x05,0X0D, 0x0A]
        if (isClickOn) {
            isClickOn = false;
            byte[] send = {(-3), (-3), -6, 5, 13, 10};
            // rbxt.getService().write(send);
            gattCharacteristicWrite.setValue(send);
            mBluetoothLeService.getmBluetoothGatt().writeCharacteristic(
                gattCharacteristicWrite);
            RecievedDataFix = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    RecievedDataFix = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (KeyEvent.KEYCODE_HOME == keyCode)
            android.os.Process.killProcess(android.os.Process.myPid());
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
        // this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    public void exitProgrames() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onMessage(Msg message) {
        // super.onMessage(message);
        if (message.getHead() == null) {
            state.setText(getResources().getStringArray(R.array.connect_state)[message
                          .getMsg_code()]);
            // + message.getDevice_name());
            if (state.getText().toString().equals("connecting...")) {
                // bluetooth.setImageResource(R.drawable.bluetooth);
                Toast.makeText(this, "Pairing, please wait......",
                               Toast.LENGTH_SHORT).show();
                // rbxt.getService().connect();
            }
            if (state.getText().toString().equals("The connected")) {
                bluetooth.setImageResource(R.drawable.bluetooth);
                toShowDataPage();
                /*
                 * Toast .makeText( this,"Pairing, please wait......",
                 * Toast.LENGTH_SHORT).show();
                 */
                // rbxt.getService().connect();
            }
        } else {
            // Toast.makeText(this, "测量开始!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d("onActivityResult" + "requestCode---->" + requestCode);
        L.d("onActivityResult" + "resultCode---->" + resultCode);
        switch (requestCode) {
        case REQUEST_ENABLE_BT:
            if (resultCode == Activity.RESULT_OK) {
                // rbxt.setupChat();
            } else {
                Toast.makeText(this, "The bluetooth is not available.",
                               Toast.LENGTH_SHORT).show();
            }
            break;
        case 100:
            if (20 == resultCode) {
                String sp = mSpinnerBtn.getText().toString();
                if (sp.equals("User") && i != 0) {
                    String bname = data.getExtras().getString("bname");
                    mSpinnerBtn.setText(bname);
                }
            }
            break;
        case 30:
            isRecivced = false;
            // if (20 == resultCode) {
            // state.setText("aaaa");
            // bluetooth.setImageResource(R.drawable.bluetoothno);
            // }
            if (21 == resultCode) {
                backStop = true;
            }
            break;
        case REQUEST_CONNECT_DEVICE:
            if (resultCode == Activity.RESULT_OK) {
                String address = data.getExtras().getString(
                                     DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                BluetoothDevice device = mBluetoothAdapter
                                         .getRemoteDevice(address);
                System.out.println(state.getText() + "dfdfdfdfdfdfdfdfdfdfdf");
                if ((state.getText().toString().equals("aaaa"))) {// &rbxt.getService().getmState()
                    // ==
                    // Msg.MESSAGE_STATE_CONNECTED
                    state.setText("The connected");
                    bluetooth.setImageResource(R.drawable.bluetooth);
                }
                // rbxt.getService().setDevice(device);
                // rbxt.getService().connect();
            }
            // 选择设备。，，
            // if (resultCode == 300) {
            // bleAddress = data.getExtras().getString(
            // DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            //
            // startService();
            // mState = Msg.MESSAGE_STATE_CONNECTING;
            // onMessage(new Msg(mState, ""));
            // // rbxt.getService().setDevice(device);
            // // rbxt.getService().connect();
            //
            // }
            break;
        }
    }

    public  void doBluetoothDisconnect(int ms){
        if(ms >0){
            return;
        }
        if(null != mBluetoothLeService.getmBluetoothGatt()) {
            Log.d("====","doBluetoothDisconnect");
            mBluetoothLeService.getmBluetoothGatt().disconnect();
            mBluetoothLeService.getmBluetoothGatt().close();
            bleState = ble_disConnected;
        }
    }

    private float mMolValue;
    public void analysisData(String bData) { //解析数据
        /***/
        Log.i("console", "获得数据:"+bData);
        if(bData.substring(bData.length()-4,bData.length()-3).equals("F")) {
            float a =Integer.parseInt(bData.substring(bData.length()-6,bData.length()-4),16);
            mMolValue = a/10;

            xiaochiPostMessage(mMolValue+"nmol/L");
            new BluetoothReportor(TYPE_GLU,0,0,0,0,mMolValue).start();
            Log.e("console", "测量结果为："+mMolValue+"mmol/L");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    doBluetoothDisconnect(500);
                }
            }, 500);
        }
    };

    /*
     * 监听广播类，用来实施的接受数据
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if (null == mDevice) {
                    L.d("mDevice.getName() is NULL");
                }else if(-1 != mDevice.getName().indexOf("BJYC") ) {
                    analysisData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                }else if(-1 != mDevice.getName().indexOf("BerryMed") ) {
                    mDataParser.add(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
                } else if(-1 != mDevice.getName().indexOf("Bluetooth BP") ) {
                    byte[] data = intent.getExtras().getByteArray("data");
                    L.d("ble get data:" + Arrays.toString(data));
                    doWithData(data);
                }else {
                    L.d("[xiaochi]device is not support");
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                       .equals(action)) {
                state.setText("disConnected");
                bluetooth.setImageResource(R.drawable.bluetoothno);
                bleState = ble_disConnected;
                // isConnected = false;
                L.d("APP: Bluetooth disConnected");
            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                L.d("APP: Bluetooth Connected");
            } else if (BluetoothLeService.ACTION_GATT_WRITE_SUCCESS.equals(action)) {
                // isNotifyAble = true;
                L.d("APP Gatt Write Success");
                mState = Msg.MESSAGE_STATE_CONNECTED;
                onMessage(new Msg(mState, ""));
                backStop = false;
                isRecivced = false;
                bleState = ble_connected;
                // byte[] sends = { (-2), (-3), (-86), (-96), 13, 10 };
                // gattCharacteristicWrite.setValue(sends);
                // mBluetoothLeService.getmBluetoothGatt().writeCharacteristic(gattCharacteristicWrite);
            } else if (SampleGattAttributes.DISCONNECTEDBLE.equals(action)) {
                // if(intent.getBooleanExtra("stop", false)){
                // byte[] send = {(byte)0xFD,(byte)0xFD,(byte)0xFE, 0x06, 0X0D,
                // 0x0A};
                // gattCharacteristicWrite.setValue(send);
                // mBluetoothLeService.getmBluetoothGatt().writeCharacteristic(gattCharacteristicWrite);
                // }
                // else{
                // mBluetoothLeService.disconnect();
                // mBluetoothLeService.close();
                // state.setText("disconnected");
                // }
            }
        }
    };
    private BluetoothGattCharacteristic gattCharacteristicWrite;
    private int mState;
    private boolean isRecivced;

    // 设置setCharacteristicNotification 获取数据
    @SuppressLint("NewApi")
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        BluetoothGattService mInfoService = null;
        BluetoothGattService mDataService = null;

        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            if(gattService.getUuid().equals(Const.UUID_SERVICE_DATA))
            {
                mDataService = gattService;
            }
            //if (uuid.equalsIgnoreCase(SampleGattAttributes.SERVICE_UU))
            {
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuid1 = gattCharacteristic.getUuid().toString();
                    if (uuid1.equalsIgnoreCase(SampleGattAttributes.NOTIFY_UU)) {
                        mBluetoothLeService.setCharacteristicNotification(
                            gattCharacteristic, true);
                    }
                    if (uuid1.equalsIgnoreCase(SampleGattAttributes.WRITE_UU)) {
                        gattCharacteristicWrite = gattCharacteristic;
                    }
                    if (uuid1.contains("2a18")) {
                        mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                        mBluetoothLeService.readCharacteristic(gattCharacteristic);
                    }
                }
            }
            if(mDataService != null) {
                List<BluetoothGattCharacteristic> characteristics =
                        mDataService.getCharacteristics();
                for(BluetoothGattCharacteristic ch: characteristics) {
                    if(ch.getUuid().equals(Const.UUID_CHARACTER_RECEIVE)) {
                        long now = new Date().getTime();
                        if((now -last_start_spo2) >5000) {
                            last_start_spo2 = now;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBluetoothLeService.setCharacteristicNotification(chReceive,true);
                                    Log.e(TAG,">>>>>>>>>>>>>>>>>>>>START<<<<<<<<<<<<<<<<<<<");
                                }
                            },2000);
                        }
                        chReceive = ch;
                    } else if(ch.getUuid().equals(Const.UUID_MODIFY_BT_NAME)) {
                        chChangeBtName = ch;
                        Log.e(TAG,">>>>>>>>>>>>>>>>>>>>CHECK Bt Name<<<<<<<<<<<<<<<<<<<");
                    }
                }
            }
        }
    }


    static int request_count = 0;
    protected void doWithData(byte[] data) {
        if (data.length == 1 && (byte) data[0] == -91) {
            // mState = Msg.MESSAGE_STATE_CONNECTED;
            // onMessage(new Msg(mState, ""));
            if(3 <= (++request_count)) {
                request_count = 0;
                isClickOn = true;
                toShowDataPage();
            }
        } else if (data.length == 5 && data[0] == data[1] && data[1] == -3
                   && data[2] == 6) {
//			if (backStop) {
//				isRecivced = true;
//				backStop = false;
//			}
            toOneoneActivity();
        } else if (data.length == 6 && data[1] == data[2] && data[1] == -3
                   && data[3] == 6) {
//			if (backStop) {
//				isRecivced = true;
//				backStop = false;
//			}
            toOneoneActivity();
        } else if (data.length == 7 && data[0] == data[1] && data[1] == -3
                   && data[2] == -5) {
            if (backStop) {
                isRecivced = true;
                backStop = false;
            }
            if (RecievedDataFix) {
                toOneoneActivity();
            }
        } else if (data.length == 8 && data[0] == data[1] && data[1] == -3
                   && data[2] == -4) {
            new BluetoothReportor(TYPE_BP,data[3],data[4],data[5],0,0).start();
            doShutdown();
        } else if (RecievedDataFix && data.length > 0) {
            toOneoneActivity();
        }
    }


    private void toOneoneActivity() {
        Intent one = new Intent(MainActivity.this, OneoneActivity.class);
        String gname = mSpinnerBtn.getText().toString();
        one.putExtra("gname", gname);
        startActivityForResult(one, 30);
        RecievedDataFix = false;
    }

    @Override
    public BroadcastReceiver getBroadCastReceiver() {
        // TODO Auto-generated method stub
        return mGattUpdateReceiver;
    }

    @Override
    public TextView getTipText() {
        // TODO Auto-generated method stub
        return state;
    }

    @Override
    public String getDeviceName() {
        // TODO Auto-generated method stub
        return "Bluetooth BP";
    }

    @Override
    public String getUUID() {
        // TODO Auto-generated method stub
        return SampleGattAttributes.SERVICE_UU;
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    private  long msg_count = 0;
    private Handler xiaochiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String info = msg.getData().getString("val");
                    if(null != info) {
                        mDisplay.setText("val["+msg_count+"]="+info);
                        msg_count++;
                    }else {
                        mDisplay.setText("data error");
                    }
                    break;
            }
        }
    };

    void xiaochiPostMessage(String info){
        Message msg = new Message();
        msg.what = 1;
        Bundle b = new Bundle();
        b.putString("val",info);
        msg.setData(b);
        xiaochiHandler.sendMessage(msg);
    }

    private  long last_log_time = 0;
    @Override
    public void onSpO2ParamsChanged() {
        PackageParser.OxiParams params = mPackageParser.getOxiParams();
        int br = params.getPulseRate();
        int spo2 = params.getSpo2();
        long now = new Date().getTime();
        boolean should_log = (now - last_log_time) >1000? true:false;
        if(should_log){
            String br_spo2 = "(" + br + " " + spo2 + ")";
            Log.d(TAG, "DATA:"+br_spo2);
            xiaochiPostMessage(br_spo2);
            last_log_time = now;
        }

        if((spo2 >35) && (spo2 <= 100) ){
            if(now - last_report > 10000) {
                new BluetoothReportor(TYPE_SO2, 0, 0, br, spo2, 0).start();
                last_report = now;
            }
        }else {
            if(should_log) {
                Log.d(TAG, "Sensor disconnect");
            }
        }
        //mHandler.obtainMessage(Const.MESSAGE_OXIMETER_PARAMS,pc,params.getPulseRate()).sendToTarget();
    }

    @Override
    public void onSpO2WaveChanged(int wave) {
       // mHandler.obtainMessage(Const.MESSAGE_OXIMETER_WAVE,wave,0).sendToTarget();
    }

}
