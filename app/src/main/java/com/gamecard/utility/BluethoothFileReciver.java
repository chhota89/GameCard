package com.gamecard.utility;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gamecard.R;
import com.gamecard.view.HomeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by bridgeit on 8/7/16.
 */

public class BluethoothFileReciver extends AsyncTask<String, Void, String> {

    private BluetoothServerSocket mmServerSocket;

    private static final String TAG = "WiFiFileReceiver";
    String FILE_NAME = "";
    ProgressDialog progressDialog;
    private Context context;
    File file;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;

    public BluethoothFileReciver(Context context, BluetoothAdapter adapter) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Downloading the file");

        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final

        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = adapter.listenUsingRfcommWithServiceRecord("NAME", UUID.fromString(context.getString(R.string.uuid)));
        } catch (IOException e) {
        }
        mmServerSocket = tmp;
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_game_center);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setLargeIcon(largeIcon);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_game_center);
        mBuilder.setContentTitle("Receiving game started ");
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */

            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
            }

            //start showing progress dialog
            publishProgress();

            //If this code is reached, a client has connected and transferred data
            //Save the input stream from the client as a .apk file
            FILE_NAME = "" + System.currentTimeMillis() + ".apk";
            file = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/" + FILE_NAME);

            File dirs = new File(file.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            file.createNewFile();
            InputStream inputstream = socket.getInputStream();

            FileUtility.copyFile(inputstream, new FileOutputStream(file));

            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return file.getAbsolutePath();
        }finally {
            if(mmServerSocket!=null)
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    @Override
    protected void onProgressUpdate(Void... progress) {
        progressDialog.show();
        // Sets an activity indicator for an operation of indeterminate length
        mBuilder.setProgress(0, 0, true);
        // Issues the notification
        mNotificationManager.notify(Constant.RECEIVE_BLUETOOTH_NOTIFICATION, mBuilder.build());
    }

    /**
     * Start activity that can handle the .apk file
     */
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/" + FILE_NAME)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            context.startActivity(intent);
        }
        if (progressDialog.isShowing())
            progressDialog.hide();

        mBuilder.setContentText("Receiving game Finish.").setProgress(100,100,false);
        // Issues the notification
        mNotificationManager.notify(Constant.RECEIVE_BLUETOOTH_NOTIFICATION, mBuilder.build());

        //Set bluetoothAsyncTaskStarted variable to false. i.e No Bluetooth async task is started
        HomeView.bluetoothAsyncTaskStarted=false;
    }
}