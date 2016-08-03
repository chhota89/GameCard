package com.gamecard.utility;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by bridgeit on 8/7/16.
 */

public class FIleSendBluetooth extends IntentService {

    private  BluetoothSocket mmSocket;
    private  BluetoothDevice mmDevice;
    int len;
    byte buf[] = new byte[1024];
    private ResultReceiver resultReceiver;
    public  static  final int PORT=452;

    public FIleSendBluetooth() {
        super("FIleSendBluetooth");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        BluetoothDevice device = (BluetoothDevice) intent.getExtras().get("Device");
        String uuid = (String)intent.getExtras().get("UUID");
        File fileToSend = (File) intent.getExtras().get("fileToSend");
        resultReceiver = (ResultReceiver) intent.getExtras().get("resultReciver");

        //Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
        } catch (IOException e) { }
        mmSocket = tmp;

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            try {
                long total = 0;
                OutputStream outputStream = mmSocket.getOutputStream();
                Context context = getApplicationContext();
                ContentResolver cr = context.getContentResolver();
                InputStream inputStream = null;
                inputStream = cr.openInputStream(Uri.fromFile(fileToSend));
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                    total += len;
                    if (len > 0) {
                        long fileLength = fileToSend.length();
                        sendPercentage((int) ((total * 100) / fileLength));
                    }
                }
                outputStream.close();
                inputStream.close();
            }catch (FileNotFoundException exception){

            }catch (IOException ioException){

            }

        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
    }

    public void onDestroy() {
        //Signal that the service was stopped
        //serverResult.send(port, new Bundle());
        stopSelf();
    }

    public void sendPercentage(int percentage) {
        Bundle bundle = new Bundle();
        bundle.putInt("Progress", percentage);
        resultReceiver.send(PORT, bundle);
    }
}
