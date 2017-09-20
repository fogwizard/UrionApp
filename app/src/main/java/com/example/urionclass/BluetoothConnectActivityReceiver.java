package com.example.urionclass;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class BluetoothConnectActivityReceiver extends BroadcastReceiver {

    String strPsw = "1234";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(
                    "android.bluetooth.device.action.PAIRING_REQUEST")) {
            BluetoothDevice btDevice = intent
                                       .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            try {
                ClsUtils.setPin(btDevice.getClass(), btDevice, strPsw); // 手机和蓝牙采集器配对
                ClsUtils.createBond(btDevice.getClass(), btDevice);
                ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                // Thread thread=new Thread(strPsw);
                // thread.
            }
        }
    }
}
