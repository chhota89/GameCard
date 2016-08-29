package com.gamecard.utility;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.os.ResultReceiver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.net.URL;

/**
 * Created by bridgeit on 26/8/16.
 */

public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private static final String TAG = "DownloadService";

    int lengthOfFile;
    String path;
    String extension;
    String title;
    int count = 0;
    long total;
    public static final int UPDATE_PROGRESS = 8344;
    ResultReceiver receiver;
    Bundle resultData;
    HttpURLConnection connection;
    BufferedOutputStream output;
    FileOutputStream fos;
    InputStream input;
    URL url;


    public DownloadService() {
        super(DownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
     //   extension=intent.getStringExtra("extension");
        resultData = new Bundle();
        if (!TextUtils.isEmpty(url)) {
            /* Update UI: Download Service is Running */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {
                String results = downloadData(url);

                /* Sending result back to activity */
                if (null != results && results.length() > 0) {
                    resultData.putString("result", results);
                    receiver.send(STATUS_FINISHED, resultData);
                }
            } catch (Exception e) {

                /* Sending error message back to activity */
                resultData.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, resultData);
            }
        }
        Log.d(TAG, "Service Stopped!");
    }

    private String downloadData(String... aurl) throws IOException, DownloadException {

        path = Environment.getExternalStorageDirectory()+"/GameCenter/"+title+".apk";
        File file = new File(path);
        try {
            // Make sure the directory exists.
            if(file!=null)
                file.getParentFile().mkdirs();

            // file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
      /*  path = Environment.getExternalStorageDirectory()+"/DownloadManager"+extension;
        File file = new File(path);
        try {
            // Make sure the directory exists.
            if(file!=null)
                file.getParentFile().mkdirs();

            // file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       /* FILE_NAME = "" + title + ".apk";
        final File f = new File(Environment.getExternalStorageDirectory() + "/"
                + "GameCard" + "/" + FILE_NAME);

        try {
            // Make sure the directory exists.
            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            return f.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }*/

        try {
          /*  url = new URL(aurl[0]);
            connection = (HttpURLConnection) url.openConnection();
            fos = (lengthOfFile == 0) ? new FileOutputStream(path) : new FileOutputStream(path,true);
            output = new BufferedOutputStream(fos, 1024);
            connection.setRequestProperty("Range", "bytes=" + file.length() + "-");
            connection.connect();
            lengthOfFile = connection.getContentLength();
            input = new BufferedInputStream(url.openStream());*/

            url = new URL(aurl[0]);
            connection = (HttpURLConnection) url.openConnection();
            fos = (lengthOfFile == 0) ? new FileOutputStream(path) : new FileOutputStream(path,true);
            output = new BufferedOutputStream(fos, 1024);
            lengthOfFile = connection.getContentLength();
            input = new BufferedInputStream(url.openStream());
            total=0;
            byte data[] = new byte[1024];
            while ((count = input.read(data)) != -1) {
                total += count;

                // publishing the progress....
                resultData.putInt("progress", (int) (total * 100 / lengthOfFile));
                receiver.send(UPDATE_PROGRESS, resultData);
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        resultData.putInt("progress", 100);
        receiver.send(STATUS_FINISHED, resultData);

        return null;
    }

    public class DownloadException extends Exception {

        public DownloadException(String message) {
            super(message);
        }

        public DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
