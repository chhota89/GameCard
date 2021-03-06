package com.gamecard.utility;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.gamecard.dagger.component.CommonComponent;
import com.gamecard.dagger.component.DaggerCommonComponent;
import com.gamecard.dagger.component.DaggerMqttComponent;
import com.gamecard.dagger.component.DaggerNetComponent;
import com.gamecard.dagger.component.MqttComponent;
import com.gamecard.dagger.component.NetComponent;
import com.gamecard.dagger.module.AppModule;
import com.gamecard.dagger.module.CommonModule;
import com.gamecard.dagger.module.MqttModule;
import com.gamecard.dagger.module.NetModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by bridgeit on 23/6/16.
 */

public class AppController extends Application {

    private NetComponent mNetComponent;

    private MqttComponent mqttComponent;

    private CommonComponent commonComponent;

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

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                //.netModule(new NetModule("http://192.168.0.175:8080/GameCard/"))
                .netModule(new NetModule("https://bridgelabz-gemacenter.herokuapp.com"))

                .build();

        mqttComponent= DaggerMqttComponent.builder()
                .appModule(new AppModule(this))
                .mqttModule(new MqttModule())
                .build();

        commonComponent= DaggerCommonComponent.builder()
                .appModule(new AppModule(this))
                .commonModule(new CommonModule())
                .build();

    }

    public NetComponent getmNetComponent() {
        return mNetComponent;
    }

    public MqttComponent getMqttComponent() {
        return mqttComponent;
    }

    public CommonComponent getCommonComponent() {
        return commonComponent;
    }
}
