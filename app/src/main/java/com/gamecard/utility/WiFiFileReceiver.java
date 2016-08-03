package com.gamecard.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Purpose:
 * 1 Open soket that is wait unit connection established.
 * 2 receive the file from connection
 * 3 Save the file in particular directory.
 * Created by bridgeit on 28/6/16.
 */

public class WiFiFileReceiver extends AsyncTask<String, Void, String> {

    private static final String TAG = "WiFiFileReceiver";
    String FILE_NAME = "";
    ProgressDialog progressDialog;
    private Context context;

    public WiFiFileReceiver(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Downloading the file");
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();

            //start showing progress dialog
            publishProgress();

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a .apk file
             */
            FILE_NAME = "" + System.currentTimeMillis() + ".apk";
            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/" + FILE_NAME);

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            InputStream inputstream = client.getInputStream();
            copyFile(inputstream, new FileOutputStream(f));
            serverSocket.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private void copyFile(InputStream inputstream, FileOutputStream fileOutputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;
        InputStreamReader isr = new InputStreamReader(inputstream);
        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
        while (true) {
            bytesRead = inputstream.read(buffer, 0, buffer.length);
            if (bytesRead == -1) {
                break;
            }
            bos.write(buffer, 0, bytesRead);
            bos.flush();

        }
        bos.close();
    }

    @Override
    protected void onProgressUpdate(Void... progress) {
        progressDialog.show();
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
    }
}