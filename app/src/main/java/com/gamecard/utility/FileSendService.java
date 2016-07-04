package com.gamecard.utility;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by bridgeit on 29/6/16.
 */

public class FileSendService extends IntentService {

    String host;
    int port;
    int len;
    Socket socket = new Socket();
    byte buf[] = new byte[1024];
    ResultReceiver resultReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p>
     * constructor is Used to name the worker thread, important only for debugging.
     */
    public FileSendService() {
        super("FileSendService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        port = ((Integer) intent.getExtras().get("port")).intValue();
        File fileToSend = (File) intent.getExtras().get("fileToSend");
        WifiP2pInfo wifiInfo = (WifiP2pInfo) intent.getExtras().get("wifiInfo");
        resultReceiver = (ResultReceiver) intent.getExtras().get("resultReciver");
        WifiP2pDevice p2pDevice = (WifiP2pDevice) intent.getExtras().get("wifip2pdevice");
        try {
            socket.bind(null);
            socket.connect((new InetSocketAddress(wifiInfo.groupOwnerAddress, port)), 500);

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            long total = 0;
            OutputStream outputStream = socket.getOutputStream();
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
        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
        }

        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */ finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
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
        resultReceiver.send(port, bundle);
    }

}
