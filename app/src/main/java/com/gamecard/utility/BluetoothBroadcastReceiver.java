package com.gamecard.utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gamecard.callback.CallBackBluetooth;

/**
 * Created by bridgeit on 2/7/16.
 */

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private CallBackBluetooth callBackBluetooth;

    public BluetoothBroadcastReceiver(CallBackBluetooth callBackBluetooth){
        super();
        this.callBackBluetooth=callBackBluetooth;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            //Listen to the bluetooth change action
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            boolean flag=false;
            switch(state) {
                case BluetoothAdapter.STATE_OFF:
                    flag=false;
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    flag=false;
                    break;
                case BluetoothAdapter.STATE_ON:
                    flag=true;
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    flag=true;
                    break;
            }
            callBackBluetooth.deviceState(flag);
        }else if(BluetoothDevice.ACTION_FOUND.equals(action)){
            // When discovery finds a device

            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            callBackBluetooth.addDevice(device);
        }
    }
}
