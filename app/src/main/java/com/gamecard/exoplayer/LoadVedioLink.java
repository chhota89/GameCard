package com.gamecard.exoplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by bridgeit on 8/9/16.
 */

public class LoadVedioLink extends AsyncTask<String,Void,String> {

    private static final String TAG=LoadVedioLink.class.getSimpleName();
    Context context;

    public LoadVedioLink(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {

        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL("http://www.youtube.com/get_video_info?&video_id="+strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDefaultUseCaches(true);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return getPhisicalUrl(readStream(in));
        }
        catch (Exception excption){
            Log.e(TAG, "doInBackground: ", excption);
        }
        finally {
            if(urlConnection!=null)
                urlConnection.disconnect();
        }
        return null;
    }

    private String readStream(InputStream in) {
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private String getPhisicalUrl(String file){
        if(file!=null && file.contains("dashmpd")) {
            String dashUrl1 = file.substring(file.lastIndexOf("dashmpd"));
            //System.out.println("1 "+dashUrl1);
            String dashUrl2 = dashUrl1.substring(dashUrl1.lastIndexOf("dashmpd"), dashUrl1.indexOf("&"));
            //System.out.println("2 "+dashUrl2);
            String dashUrl = null;
            try {
                dashUrl = URLDecoder.decode(dashUrl2.substring(dashUrl2.indexOf("http")), "UTF-8");
                Log.i(TAG, "getPhisicalUrl: .... " + dashUrl);
                return dashUrl;
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "getPhisicalUrl: ", e);
                return null;
            }
        }
        return null;
    }
}