package com.gamecard.utility;

import android.app.Application;

import com.facebook.FacebookSdk;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by bridgeit on 23/6/16.
 */

public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
