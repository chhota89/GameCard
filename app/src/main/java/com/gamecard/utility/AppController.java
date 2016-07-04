package com.gamecard.utility;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by bridgeit on 23/6/16.
 */

public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
